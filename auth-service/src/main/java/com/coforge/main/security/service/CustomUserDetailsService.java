package com.coforge.main.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.coforge.main.entity.UserCredential;
import com.coforge.main.repository.UserCredentialsRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserCredentialsRepository userCredentialsRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserCredential userCredential = userCredentialsRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with Username: " + username));
		return new CustomUserDetails(userCredential);
	}
}
