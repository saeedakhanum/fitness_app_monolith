package com.project.fitness.service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.fitness.dto.ForgotPasswordRequest;
import com.project.fitness.dto.LoginRequest;
import com.project.fitness.dto.ResetPasswordRequest;
import com.project.fitness.dto.UserRequest;
import com.project.fitness.dto.UserResponse;
import com.project.fitness.entity.BlackListedToken;
import com.project.fitness.entity.PasswordResetToken;
import com.project.fitness.entity.User;
import com.project.fitness.entity.UserRole;
import com.project.fitness.exception.BadRequestException;
import com.project.fitness.exception.ResourceNotFoundException;
import com.project.fitness.exception.UnauthorizedAccessException;
import com.project.fitness.repository.BlackListedTokenRepository;
import com.project.fitness.repository.PasswordResetTokenRepository;
import com.project.fitness.repository.UserRepository;
import com.project.fitness.security.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final PasswordEncoder passwordEncoder;
	private final Logger logger = LoggerFactory.getLogger(UserService.class);
	private final JwtUtils jwtUtils;
	private final BlackListedTokenRepository blackListedTokenRepository;
	private final PasswordResetTokenRepository tokenRepository;
	private final EmailService emailService;

	// Register
	public UserResponse register(UserRequest request) {
//		User user=modelMapper.map(registerRequest,User.class);
//		User savedUser=userRepository.save(user);
//		return modelMapper.map(savedUser,UserResponse.class);
		if (userRepository.existsByEmail(request.getEmail())) {
			logger.warn("Email already exists : {}", request.getEmail());
			throw new BadRequestException("Email already exists");
		}
		UserRole role = request.getRole() != null ? request.getRole() : UserRole.USER;
		User user = User.builder().email(request.getEmail()).firstName(request.getFirstName())
				.lastName(request.getLastName()).password(passwordEncoder.encode(request.getPassword()))
				.roles(Set.of(role)).build();
		User savedUser = userRepository.save(user);
		return mapToUserResponse(savedUser);

	}

	// Login
	public User authenticate(LoginRequest loginRequest) {
		User user = userRepository.findByEmail(loginRequest.getEmail())
				.orElseThrow(() -> new BadRequestException("Invalid credentials"));
		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new BadRequestException("Invalid credentials");
		}
		return user;
	}

	// Logout
	public void logout(HttpServletRequest request) {
		String jwt = jwtUtils.getJwtTokenFromHeader(request);
		if (jwt == null || jwt.isBlank()) {
			throw new RuntimeException("JWT token is missing");
		}
		if (!jwtUtils.validateJwtToken(jwt)) {
			throw new RuntimeException("Invalid JWT token");
		}

		if (blackListedTokenRepository.existsByJwtToken(jwt)) {
			throw new RuntimeException("Token already logged out");
		}
		BlackListedToken token = new BlackListedToken();
		token.setJwtToken(jwt);
		token.setExpireAt(LocalDateTime.now());
		blackListedTokenRepository.save(token);
	}

	// get user by id
	public UserResponse getUserById(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		return mapToUserResponse(user);
	}

	// Get all the users
	public List<UserResponse> getAllUsers() {
		List<User> users = userRepository.findAll();
		return users.stream().map(user -> mapToUserResponse(user)).toList();
	}

	// update the user
	public UserResponse updateUser(Long userId, UserRequest request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		User updatedUser = userRepository.save(user);
		return mapToUserResponse(updatedUser);
	}

	// Delete user
	public void deleteUser(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		userRepository.delete(user);
	}

	// update the user role
	public UserResponse updateUserRole(Long userId, Set<UserRole> role) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		if (role.isEmpty() || role == null) {
			throw new BadRequestException("At least one role is required");
		}

		if (!role.contains(UserRole.USER)) {
			throw new BadRequestException("User role is mandatory");
		}
		user.getRoles().clear();
		user.getRoles().addAll(role);
		User roleUpdatedUser = userRepository.save(user);
		return mapToUserResponse(roleUpdatedUser);
	}

	// remove the role
	public UserResponse deleteRole(Long userId, Set<UserRole> rolesToRemove) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		Set<UserRole> existingRoles = user.getRoles();
		if (rolesToRemove.contains(UserRole.USER)) {
			throw new BadRequestException("User role is mandatory");
		}
		existingRoles.removeAll(rolesToRemove);
		User deletedRoleUser = userRepository.save(user);
		return mapToUserResponse(deletedRoleUser);
	}

	// forgot password
	public void forgotPassword(ForgotPasswordRequest request) {
		String jwtUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User loggedInUser = userRepository.findById(Long.valueOf(jwtUserId))
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
				() -> new ResourceNotFoundException("User does not exists with the given email " + request.getEmail()));

		if (!loggedInUser.getEmail().equals(user.getEmail())) {
			throw new UnauthorizedAccessException("You can only request the otp for your own email");
		}
		// check for the existing active otp
		tokenRepository.getLatestOtpOfTheUser(user.getId()).ifPresent(token -> {
			if (token.getOtpExpiryTime().isAfter(LocalDateTime.now())) {
				throw new BadRequestException("Otp is already sent. Please wait before requesting again");
			}
		});

		// generate the new otp
		// next int return int so converting to string
		String otp = String.valueOf(new Random().nextInt(999999));

		PasswordResetToken token = new PasswordResetToken();
		token.setUser(user);
		token.setOtp(otp);
		token.setOtpUsed(false);
		token.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
		PasswordResetToken savedToken = tokenRepository.save(token);
		emailService.sendOtpByEmail(request.getEmail(), otp);
	}

	// validate Otp
	public PasswordResetToken validateOtp(String otp) {
		PasswordResetToken token = tokenRepository.findByOtp(otp)
				.orElseThrow(() -> new ResourceNotFoundException(" Invalid Otp"));
		if (token.isOtpUsed()) {
			throw new BadRequestException("Otp is already used");
		}
		if (token.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
			throw new BadRequestException("Otp is expired");
		}
		return token;

	}

	// reset the password
	public void resetPassword(ResetPasswordRequest request) {
		PasswordResetToken token = validateOtp(request.getOtp());
		String jwtUserId = String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		User loggedInUser = userRepository.findById(Long.valueOf(jwtUserId))
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		User user = token.getUser();
		if (!loggedInUser.getId().equals(user.getId())) {
			throw new UnauthorizedAccessException("You can only reset your own password using your OTP");
		}
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userRepository.save(user);
		token.setOtpUsed(true);
		tokenRepository.save(token);
	}

	// Count users by role
	public Long countUserByRole(UserRole role) {
		return userRepository.countUsersByRole(role);
	}

	// count admins
	public Long countAdmins() {
		Long count = userRepository.countUsersByRole(UserRole.ADMIN);
		return count;
	}

	// get all the users by role
	public List<UserResponse> getAllUsersByRole(UserRole role) {
		List<User> userList = userRepository.findAllUsersByRole(role);
		if (userList.isEmpty()) {
			throw new BadRequestException("Users not found with the role :" + role);
		}
		return userList.stream().map(user -> mapToUserResponse(user)).toList();
	}

	public UserResponse mapToUserResponse(User savedUser) {
		UserResponse userResponse = new UserResponse();
		userResponse.setId(savedUser.getId());
		userResponse.setEmail(savedUser.getEmail());
		userResponse.setPassword(savedUser.getPassword());
		userResponse.setFirstName(savedUser.getFirstName());
		userResponse.setLastName(savedUser.getLastName());
		userResponse.setRoles(savedUser.getRoles());
		userResponse.setCreatedAt(savedUser.getCreatedAt());
		userResponse.setUpdatedAt(savedUser.getUpdatedAt());
		return userResponse;
	}
}
