package com.coforge.main.exceptions.handler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.coforge.main.controller.UserRegistrationController;
import com.coforge.main.exceptions.InvalidPasswordException;
import com.coforge.main.exceptions.InvalidResetTokenException;
import com.coforge.main.exceptions.ResourceNotFoundException;
import com.coforge.main.exceptions.ServiceUnavailableException;
import com.coforge.main.exceptions.UserNotFoundException;

@RestControllerAdvice
public class CustomExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(CustomExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	protected ExceptionResponse handleInvalidMethodArgument(MethodArgumentNotValidException exception,
			WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), new Date(),
				exception.getBindingResult().getFieldError().getDefaultMessage(), request.getDescription(false));
		log.warn("Exception: {}", exceptionResponse);
		return exceptionResponse;
	}

	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ExceptionResponse handleUserNotFound(UserNotFoundException exception, WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.NOT_FOUND.value(), new Date(),
				exception.getMessage(), request.getDescription(false));
		log.warn("Exception: {}", exceptionResponse);
		return exceptionResponse;
	}

	@ExceptionHandler(InvalidPasswordException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleInvalidPassword(InvalidPasswordException exception, WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), new Date(),
				exception.getMessage(), request.getDescription(false));
		log.warn("Exception: {}", exceptionResponse);
		return exceptionResponse;
	}

	@ExceptionHandler(InvalidResetTokenException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleInvalidResetToken(InvalidResetTokenException exception, WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), new Date(),
				exception.getMessage(), request.getDescription(false));
		log.warn("Exception: {}", exceptionResponse);
		return exceptionResponse;
	}
	
	@ExceptionHandler(ServiceUnavailableException.class)
	@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
	public ExceptionResponse handleServiceUnavailable(ServiceUnavailableException exception, WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), new Date(),
				exception.getMessage(), request.getDescription(false));
		log.warn("Exception: {}", exceptionResponse);
		return exceptionResponse;
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ExceptionResponse handleResourceNotFound(ResourceNotFoundException exception, WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.NOT_FOUND.value(), new Date(),
				exception.getMessage(), request.getDescription(false));
		log.warn("Exception: {}", exceptionResponse);
		return exceptionResponse;
	}

}
