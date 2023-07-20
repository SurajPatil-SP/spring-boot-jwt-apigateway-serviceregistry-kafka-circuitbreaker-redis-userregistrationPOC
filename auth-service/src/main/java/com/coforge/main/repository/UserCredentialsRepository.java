package com.coforge.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coforge.main.entity.UserCredential;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredential, Long> {

	Optional<UserCredential> findByUsername(String username);
}
