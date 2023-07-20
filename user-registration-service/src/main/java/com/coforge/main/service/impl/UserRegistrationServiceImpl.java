package com.coforge.main.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.coforge.main.dto.ApiResponseDto;
import com.coforge.main.dto.DepartmentDto;
import com.coforge.main.dto.ForgotPasswordRequestDto;
import com.coforge.main.dto.ForgotPasswordResponseDto;
import com.coforge.main.dto.LoginRequestDto;
import com.coforge.main.dto.LoginResponseDto;
import com.coforge.main.dto.ResetPasswordRequestDto;
import com.coforge.main.dto.ResetPasswordResponseDto;
import com.coforge.main.dto.UserDetailsEvent;
import com.coforge.main.dto.UserDetailsRequestDto;
import com.coforge.main.dto.UserDetailsResponseDto;
import com.coforge.main.exceptions.InvalidPasswordException;
import com.coforge.main.exceptions.InvalidResetTokenException;
import com.coforge.main.exceptions.UserNotFoundException;
import com.coforge.main.exceptions.handler.RestTemplateResponseErrorHandler;
import com.coforge.main.kafka.UserDetailsProducer;
import com.coforge.main.model.UserDetails;
import com.coforge.main.repository.UserDetailsRepository;
import com.coforge.main.service.UserRegistrationService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

	private static final Logger log = LoggerFactory.getLogger(UserRegistrationServiceImpl.class);

	private static final long EXPIRE_TOKEN_AFTER_MINUTES = 1;

	@Autowired
	private UserDetailsProducer userDetailsProducer;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	public UserRegistrationServiceImpl(RestTemplateBuilder restTemplateBuilder) {
		restTemplate = restTemplateBuilder.errorHandler(new RestTemplateResponseErrorHandler()).build();
	}

//	@Autowired
//	private WebClient webClient;

	@Autowired
	private UserDetailsRepository userDetailsRepository;

	private static final String BASE_URL = "http://localhost:8082/api/v1/departments/";

	@Override
	public UserDetailsResponseDto registerUser(UserDetailsRequestDto userDetailsRequestDto) {

		log.info("Executing registerUser...");

		UserDetails userDetails = new UserDetails();
		userDetails.setFirstName(userDetailsRequestDto.getFirstName());
		userDetails.setLastName(userDetailsRequestDto.getLastName());
		userDetails.setDateOfBirth(userDetailsRequestDto.getDateOfBirth());
		userDetails.setMobileNumber(userDetailsRequestDto.getMobileNumber());
		userDetails.setEmail(userDetailsRequestDto.getEmail());
		userDetails.setDepartmentCode(userDetailsRequestDto.getDepartmentCode());
		userDetails.setPassword(userDetailsRequestDto.getPassword());
		userDetails.setToken(null);
		userDetails.setTokenCreationDate(null);

		userDetails = userDetailsRepository.save(userDetails);

		log.info("User registered: {}", userDetails);

		UserDetailsResponseDto userDetailsResponseDto = new UserDetailsResponseDto(userDetails.getUserId(),
				userDetails.getEmail(), "User Registration Successful");
		return userDetailsResponseDto;

	}

	@Override
	public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {
		log.info("Executing loginUser...");
		LoginResponseDto loginResponse = new LoginResponseDto();
		UserDetails userDetails = null;

		Optional<UserDetails> user = userDetailsRepository.findByEmail(loginRequestDto.getEmail());
		if (user.isPresent()) {
			userDetails = user.get();

			if (userDetails.getEmail().equals(loginRequestDto.getEmail())
					&& !userDetails.getPassword().equals(loginRequestDto.getPassword())) {
				throw new InvalidPasswordException("Password is incorrect");
			}

			if (userDetails.getEmail().equals(loginRequestDto.getEmail())
					&& userDetails.getPassword().equals(loginRequestDto.getPassword())) {
				log.info("User login successful");
				loginResponse.setMessage("User login successful");
			}

		} else {
			throw new UserNotFoundException("User not found with email: " + loginRequestDto.getEmail());
		}
		return loginResponse;
	}

	@Override
	public ResetPasswordResponseDto resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
		log.info("Executing resetPassword...");
		ResetPasswordResponseDto resetPasswordResponse = new ResetPasswordResponseDto();
		UserDetails userDetails = null;

		Optional<UserDetails> user = userDetailsRepository.findByToken(resetPasswordRequestDto.getToken());
		if (user.isPresent()) {
			userDetails = user.get();

			LocalDateTime tokenCreationDate = userDetails.getTokenCreationDate();

			if (isTokenExpired(tokenCreationDate)) {
				throw new InvalidResetTokenException("Token is expired");
			}

			userDetails.setPassword(resetPasswordRequestDto.getPassword());
			userDetails.setToken(null);
			userDetails.setTokenCreationDate(null);
			userDetailsRepository.save(userDetails);
			log.info("Password reset successful...");
			resetPasswordResponse.setMessage("Password reset successful");

//			if (userDetails.getEmail().equals(resetPasswordRequestDto.getEmail())
//					&& !userDetails.getPassword().equals(resetPasswordRequestDto.getOldPassword())) {
//				throw new InvalidPasswordException("Old password is incorrect");
//			}
//
//			if (userDetails.getEmail().equals(resetPasswordRequestDto.getEmail())
//					&& userDetails.getPassword().equals(resetPasswordRequestDto.getOldPassword())) {
//				userDetails.setPassword(resetPasswordRequestDto.getNewPassword());
//				userDetailsRepository.save(userDetails);
//				resetPasswordResponse.setMessage("Password reset successful");
//			}

		} else {
			throw new InvalidResetTokenException("Token is invalid");
		}
		return resetPasswordResponse;
	}

	@Override
//	@CachePut(key = "#email", value = "Users")
	public ForgotPasswordResponseDto forgotPassword(String email, UriComponentsBuilder uriBuilder) {
		log.info("Executing forgotPassword...");
		ForgotPasswordResponseDto forgotPasswordResponse = new ForgotPasswordResponseDto();
		UserDetails userDetails = null;

		Optional<UserDetails> user = userDetailsRepository.findByEmail(email);
		if (user.isPresent()) {
			userDetails = user.get();

			String resetToken = generatePasswordResetToken();
			String resetLink = uriBuilder.path("/api/v1/users/reset-password")
//					.queryParam("email", userDetails.getEmail())
					.build().toUriString();

			userDetails.setToken(resetToken);
			userDetails.setTokenCreationDate(LocalDateTime.now());
			userDetailsRepository.save(userDetails);

			forgotPasswordResponse.setEmail(userDetails.getEmail());
			forgotPasswordResponse.setToken(userDetails.getToken());
			forgotPasswordResponse.setResetLink(resetLink);
			log.info("reset token: {} and reset link: {} shared on email: {}", userDetails.getToken(), resetLink,
					userDetails.getEmail());

		} else {
			throw new UserNotFoundException("User not found with email: " + email);
		}
		return forgotPasswordResponse;
	}

	private String generatePasswordResetToken() {
		StringBuilder token = new StringBuilder();
		return token.append(UUID.randomUUID().toString()).toString();
	}

	private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {

		LocalDateTime now = LocalDateTime.now();
		Duration diff = Duration.between(tokenCreationDate, now);

		return diff.toMinutes() >= EXPIRE_TOKEN_AFTER_MINUTES;
	}

	@Override
	@Cacheable(value = "UserDetails")
	public List<UserDetails> retrieveAllUsers() {
		log.info("Executing retrieveAllUsers...");
		List<UserDetails> allUsers = userDetailsRepository.findAll();
		return allUsers;
	}

	@Override
	@Cacheable(key = "#email", value = "UserDetails")
	public UserDetails retrieveUserByEmail(String email) {
		log.info("Executing retrieveUserByEmail...");
		UserDetails user = userDetailsRepository.findByEmail(email).get();
		return user;
	}

	@Override
	public String userWithDepartmentDetails(Long userId) {
		log.info("Executing userWithDepartmentDetails...");
		UserDetails user = userDetailsRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with userId: " + userId));

		UserDetailsEvent userDetailsEvent = new UserDetailsEvent();
		userDetailsEvent.setUserId(user.getUserId());
		userDetailsEvent.setFirstName(user.getFirstName());
		userDetailsEvent.setLastName(user.getLastName());
		userDetailsEvent.setDateOfBirth(user.getDateOfBirth());
		userDetailsEvent.setMobileNumber(user.getMobileNumber());
		userDetailsEvent.setEmail(user.getEmail());
		userDetailsEvent.setDepartmentCode(user.getDepartmentCode());
		userDetailsProducer.sendMessage(userDetailsEvent);
		return "User Details Published...";
	}

	@KafkaListener(topics = "${spring.kafka.topic1.name}", groupId = "${spring.kafka.consumer.group-id}")
	private ApiResponseDto userDetailsFromKafka(ApiResponseDto responseDto) {
		log.info("ApiResponse event received in user-service -> {}", responseDto);
		return responseDto;
	}

	@Override
//	@CircuitBreaker(name = "${spring.application.name}", fallbackMethod = "retrieveUserDetailsByIdFallback")
	@Retry(name = "${spring.application.name}", fallbackMethod = "retrieveUserDetailsByIdFallback")
	public ApiResponseDto retrieveUserDetailsById(Long userId) {
		log.info("Executing retrieveUserDetailsById...");
		DepartmentDto departmentDto = null;
		UserDetails user = userDetailsRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with userId: " + userId));

		UserDetailsEvent userDeatils = new UserDetailsEvent();
		userDeatils.setUserId(user.getUserId());
		userDeatils.setFirstName(user.getFirstName());
		userDeatils.setLastName(user.getLastName());
		userDeatils.setDateOfBirth(user.getDateOfBirth());
		userDeatils.setMobileNumber(user.getMobileNumber());
		userDeatils.setEmail(user.getEmail());
		userDeatils.setDepartmentCode(user.getDepartmentCode());

		// RestTemplate Implementation
//		restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
		
		try {
			ResponseEntity<DepartmentDto> responseEntity = restTemplate.getForEntity(
					"http://DEPARTMENT-SERVICE/api/v1/departments/" + user.getDepartmentCode(), DepartmentDto.class);
			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				departmentDto = responseEntity.getBody();
			}

		} catch (HttpClientErrorException exception) {
			log.error("Client error: {}, Response: {}", exception.getStatusCode(), exception.getResponseBodyAsString());
		} catch (HttpServerErrorException exception) {
			log.error("Server error: {}, Response: {}", exception.getStatusCode(), exception.getResponseBodyAsString());
		} catch (Exception exception) {
			log.error("Exception Occurred while calling Department Service");
		}

		// WebClient Implementation
//		DepartmentDto departmentDto = webClient.get()
//		.uri(BASE_URL + user.getDepartmentCode())
//		.retrieve()
//		.bodyToMono(DepartmentDto.class)
//		.block();

		ApiResponseDto response = new ApiResponseDto();
		response.setUser(userDeatils);
		response.setDepartment(departmentDto);
		return response;
	}

	public ApiResponseDto retrieveUserDetailsByIdFallback(Long userId, Exception exception) throws ParseException {
		log.info("Executing retrieveUserDetailsByIdFallback...");

		UserDetailsEvent userDeatils = new UserDetailsEvent();
		userDeatils.setUserId(101L);
		userDeatils.setFirstName("John");
		userDeatils.setLastName("Doe");
		userDeatils.setDateOfBirth(new SimpleDateFormat("dd/mm/yyyy").parse("10/10/1990"));
		userDeatils.setMobileNumber("9876543210");
		userDeatils.setEmail("john@mail.com");
		userDeatils.setDepartmentCode("CoforgeD-1");

		DepartmentDto departmentDto = new DepartmentDto();
		departmentDto.setDepartmentName("Java");
		departmentDto.setDepartmentDescription("Provides Java Services");
		departmentDto.setDepartmentCode("CoforgeD-1");

		ApiResponseDto response = new ApiResponseDto();
		response.setUser(userDeatils);
		response.setDepartment(departmentDto);
		return response;
	}

}
