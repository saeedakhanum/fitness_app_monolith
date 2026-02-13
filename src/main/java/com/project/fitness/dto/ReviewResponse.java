package com.project.fitness.dto;

import java.time.LocalDateTime;

import com.project.fitness.entity.ReviewTarget;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {

	private Long id;
	private Integer rating;
	private String comment;
	private Long userId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
