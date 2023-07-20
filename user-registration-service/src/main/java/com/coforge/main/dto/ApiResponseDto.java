package com.coforge.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto {

	private UserDetailsEvent user;
	
	private DepartmentDto department;
}
