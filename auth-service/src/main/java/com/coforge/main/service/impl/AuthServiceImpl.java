package com.coforge.main.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.coforge.main.entity.UserCredential;
import com.coforge.main.repository.UserCredentialsRepository;
import com.coforge.main.service.AuthService;
import com.coforge.main.util.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private UserCredentialsRepository userCredentialsRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Override
	public String saveUser(UserCredential userCredentials) {
		userCredentials.setPassword(passwordEncoder.encode(userCredentials.getPassword()));
		userCredentialsRepository.save(userCredentials);
		return "User Added to the System";
	}

	@Override
	public String generateToken(String username) {
		String token = jwtUtil.generateToken(username);
		return token;
	}

	@Override
	public void validateToken(String token) {
		jwtUtil.validateToken(token);		
	}

}
