package com.project.fitness.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.fitness.dto.LoginRequest;
import com.project.fitness.dto.LoginResponse;
import com.project.fitness.dto.UserRequest;
import com.project.fitness.dto.UserResponse;
import com.project.fitness.entity.User;
import com.project.fitness.security.JwtUtils;
import com.project.fitness.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtils jwtUtils;

	// Register the user
	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
		return new ResponseEntity<>(userService.register(request), HttpStatus.CREATED);
	}

	// Login
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
		
			User user = userService.authenticate(loginRequest);
			String jwtToken = jwtUtils.generateJwtToken(user.getId(), user.getRoles().stream().map(role->role.name()).toList());
			return ResponseEntity.ok(new LoginResponse(jwtToken, userService.mapToUserResponse(user)));
	}

	// Logout
	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request) {
		userService.logout(request);
		return new ResponseEntity<String>("Logout is successfull", HttpStatus.OK);
	}

	
}
