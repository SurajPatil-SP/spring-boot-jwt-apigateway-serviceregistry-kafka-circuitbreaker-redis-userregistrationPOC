package com.coforge.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coforge.main.model.UserDetails;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {

	Optional<UserDetails> findByEmail (String email);
	
	Optional<UserDetails> findByToken (String token);
}
