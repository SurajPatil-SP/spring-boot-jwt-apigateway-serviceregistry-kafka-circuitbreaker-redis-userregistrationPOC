package com.coforge.main.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

	@NotNull
	@Email
	private String email;
	
	@NotNull
	@Size(min = 8, message = "Password should have at least 8 characters")
	private String password;
}
