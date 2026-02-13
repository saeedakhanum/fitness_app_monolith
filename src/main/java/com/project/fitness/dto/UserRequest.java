package com.project.fitness.dto;

import com.project.fitness.entity.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
	@NotEmpty(message="Email cannot be empty")
	@Email(message="Invalid email")
	private String email;
	
	@NotEmpty(message="Password cannot be empty")
	@Size(min=8, message="Password should contain at least 8 characters")
	private String password;
	private String firstName;
	private String lastName;
	private UserRole role;
}
