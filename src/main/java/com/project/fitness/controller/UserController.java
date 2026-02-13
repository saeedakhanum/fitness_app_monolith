package com.project.fitness.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.fitness.dto.ForgotPasswordRequest;
import com.project.fitness.dto.ResetPasswordRequest;
import com.project.fitness.dto.UserRequest;
import com.project.fitness.dto.UserResponse;
import com.project.fitness.entity.UserRole;
import com.project.fitness.repository.UserRepository;
import com.project.fitness.security.JwtUtils;
import com.project.fitness.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtils jwtUtils;

	// Get user by id
	@GetMapping("/user/{userId}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
		return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
	}

	// Get all users
	@GetMapping("/admin/users")
	public ResponseEntity<List<UserResponse>> getAllUsers() {
		return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
	}

	// update user
	@PutMapping("/admin/user/{userId}")
	public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId, @RequestBody UserRequest request) {
		return new ResponseEntity<>(userService.updateUser(userId, request), HttpStatus.OK);
	}

	// delete user
	@DeleteMapping("/admin/user/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
		userService.deleteUser(userId);
		return ResponseEntity.noContent().build();
	}

	// update the role
	@PutMapping("/admin/user/{userId}/role")
	public ResponseEntity<UserResponse> changeUserRole(@PathVariable Long userId, @RequestBody Set<UserRole> role) {
		return new ResponseEntity<UserResponse>(userService.updateUserRole(userId, role), HttpStatus.OK);
	}

	// delete the role
	@DeleteMapping("/admin/user/{userId}/role")
	public ResponseEntity<UserResponse> deleteRole(@PathVariable Long userId, @RequestBody Set<UserRole> roleToRemove) {
		return new ResponseEntity<>(userService.deleteRole(userId, roleToRemove), HttpStatus.OK);
	}

	// Send the otp
	@PostMapping("/user/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
		userService.forgotPassword(request);
		return new ResponseEntity<String>(("OTP sent to the email"), HttpStatus.OK);
	}

	// validate the otp and reset the password
	@PostMapping
	public ResponseEntity<String> validateOtpAndResetPassword(@RequestBody ResetPasswordRequest request) {
		userService.resetPassword(request);
		return new ResponseEntity<>("Otp is successfully verified and Password has been resetted successfully",
				HttpStatus.OK);
	}

	// count user with the specific role
	@GetMapping("/admin/users/count")
	public ResponseEntity<String> countUserByRole(@RequestParam UserRole role) {
		Long count = userService.countUserByRole(role);
		String roleName = switch (role) {
		case ADMIN -> "admin";
		case USER -> "user";
		case TRAINER -> "trainer";
		};
		String message = (count == 1) ? "There is 1 " + roleName : "There are " + count + " " + roleName;
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	// Count admin
	@GetMapping("/admin/count")
	public ResponseEntity<String> countAdmins() {
		Long count = userService.countAdmins();
		String message = (count == 1) ? "There is 1 admin" : "There are " + count + "admins";
		return new ResponseEntity<String>(message, HttpStatus.OK);
	}

	// Get all users by role
	@GetMapping("admin/users/role")
	public ResponseEntity<List<UserResponse>> getAllUsersByRole(@RequestParam UserRole role) {
		return new ResponseEntity<>(userService.getAllUsersByRole(role), HttpStatus.OK);
	}

}
