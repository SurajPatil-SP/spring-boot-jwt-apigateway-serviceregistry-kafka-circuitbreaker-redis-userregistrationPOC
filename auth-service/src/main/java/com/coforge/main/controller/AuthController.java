package com.coforge.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coforge.main.dto.AuthRequest;
import com.coforge.main.entity.UserCredential;
import com.coforge.main.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthService authService;
	
	@Autowired
    private AuthenticationManager authenticationManager;

	@PostMapping("/register")
	public ResponseEntity<?> addNewUser(@RequestBody UserCredential user) {
		String response = authService.saveUser(user);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/token")
	public ResponseEntity<?> getToken(@RequestBody AuthRequest request) {
		Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		if(authenticate.isAuthenticated()) {
			String token = authService.generateToken(request.getUsername());
			return new ResponseEntity<>(token, HttpStatus.CREATED);
		}
		return new ResponseEntity<>("Invalid Access", HttpStatus.UNAUTHORIZED);

	}

	@GetMapping("/validate")
	public ResponseEntity<?> validateToken(@RequestParam("token") String token) {
		authService.validateToken(token);
		return new ResponseEntity<>("Token is Valid", HttpStatus.OK);
	}
}
