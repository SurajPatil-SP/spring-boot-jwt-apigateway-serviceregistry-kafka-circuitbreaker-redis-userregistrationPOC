package com.coforge.main.exceptions.handler;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {

	private Integer statusCode;
	private Date timestamp;
	private String message;
	private String description;

}
