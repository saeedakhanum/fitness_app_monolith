package com.project.fitness.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

	@Min(value=1,message="Rating should be between 1 to 5")
	@Max(value=5,message="Rating should be between 1 to 5")
	private Integer rating;
	private String comment;
}
