package com.coforge.main.service;

import com.coforge.main.entity.UserCredential;

public interface AuthService {

	public String saveUser (UserCredential userCredentials);
	
    public String generateToken(String username);

    public void validateToken(String token);
}
