package com.project.fitness.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.project.fitness.entity.User;
import com.project.fitness.exception.ResourceNotFoundException;
import com.project.fitness.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		if (user == null) {
			throw new RuntimeException("User not found with : " + email);
		}

		return org.springframework.security.core.userdetails.User.builder().username(user.getEmail())
				.password(user.getPassword())
				.roles(user.getRoles().stream().map(role -> role.name()).toArray(size -> new String[size])).build();
	}

}

