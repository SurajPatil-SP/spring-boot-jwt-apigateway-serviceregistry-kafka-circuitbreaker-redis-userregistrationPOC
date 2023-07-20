package com.coforge.main.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserDetails implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long userId;
	
	private String firstName;
	
	private String lastName;
	
	private Date dateOfBirth;
	
	private String mobileNumber;
	
	private String email;
	
	private String departmentCode;
	
	private String password;
	
	private String token;

	private LocalDateTime tokenCreationDate;
	
}
