package com.coforge.main.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.coforge.main.model.UserDetails;

@DataJpaTest
public class UserDetailsRepositoryTest {

	@Autowired
	private UserDetailsRepository userDetailsRepository;

	@Test
	public void testFindByEmail() {

		Optional<UserDetails> user = userDetailsRepository.findByEmail("peter@gmail.com");
		assertTrue(user.isPresent());
	}

	@Test
	public void testFindByEmailNotFound() {

		Optional<UserDetails> user = userDetailsRepository.findByEmail("peterfinch@gmail.com");
		assertFalse(user.isPresent());
	}

	@Test
	public void testGetAllUser() {
		List<UserDetails> users = userDetailsRepository.findAll();
		assertThat(users.size()).isGreaterThan(0);
	}

	@Test
	public void saveUserDetails() throws ParseException {
		UserDetails user = new UserDetails(101L, "Peter", "Finch",
				new SimpleDateFormat("dd/mm/yyyy").parse("10/10/1990"), "9895969897", "peter@gmail.com", "CoforgeD-1",
				"peter@123", null, null);
		UserDetails savedUser = userDetailsRepository.save(user);

		assertEquals("Peter", savedUser.getFirstName());
		assertThat(savedUser.getLastName()).isEqualTo(user.getLastName());
	}
}
