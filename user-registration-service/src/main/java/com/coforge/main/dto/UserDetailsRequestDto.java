package com.coforge.main.dto;

import java.time.LocalDateTime;
import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserDetailsRequestDto {

	@NotNull
	@Size(min = 2, message = "First name should have at least 2 characters")
	private String firstName;

	@NotNull
	@Size(min = 2, message = "Last name should have at least 2 characters")
	private String lastName;

	@NotNull(message = "The date of birth is required.")
	@Past(message = "The date of birth must be in the past.")
//	@DateTimeFormat(pattern = "dd-mm-yyyy")
//	@JsonFormat(shape = Shape.STRING, pattern = "dd-mm-yyyy")
	private Date dateOfBirth;

	@NotNull
	@Size(min = 10, max = 12, message = "Mobile Number Should be 10 Digit")
	@Pattern(regexp = "(0|91)?[6-9][0-9]{9}")
	private String mobileNumber;

	@NotNull
	@Email
//	@Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
	private String email;
	
	@NotNull
	private String departmentCode;

	@NotNull
	@Size(min = 8, message = "Password should have at least 8 characters")
	private String password;
	
	private String token;

	private LocalDateTime tokenCreationDate;
}
