package com.project.fitness.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.project.fitness.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
	private Long id;
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private Set<UserRole> roles;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
