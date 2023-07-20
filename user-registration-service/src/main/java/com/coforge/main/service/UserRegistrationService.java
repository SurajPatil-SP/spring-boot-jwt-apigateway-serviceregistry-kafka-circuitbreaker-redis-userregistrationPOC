package com.coforge.main.service;

import java.util.List;

import org.springframework.web.util.UriComponentsBuilder;

import com.coforge.main.dto.ApiResponseDto;
import com.coforge.main.dto.ForgotPasswordRequestDto;
import com.coforge.main.dto.ForgotPasswordResponseDto;
import com.coforge.main.dto.LoginRequestDto;
import com.coforge.main.dto.LoginResponseDto;
import com.coforge.main.dto.ResetPasswordRequestDto;
import com.coforge.main.dto.ResetPasswordResponseDto;
import com.coforge.main.dto.UserDetailsRequestDto;
import com.coforge.main.dto.UserDetailsResponseDto;
import com.coforge.main.model.UserDetails;

public interface UserRegistrationService {

	public UserDetailsResponseDto registerUser(UserDetailsRequestDto userDetailsRequestDto);

	public LoginResponseDto loginUser(LoginRequestDto loginRequestDto);

	public ResetPasswordResponseDto resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);

	public ForgotPasswordResponseDto forgotPassword(String email, UriComponentsBuilder uriBuilder);

	public List<UserDetails> retrieveAllUsers();
	
	public UserDetails retrieveUserByEmail (String email);
	
	public String userWithDepartmentDetails (Long userId);
	
	public ApiResponseDto retrieveUserDetailsById(Long userId);
}
