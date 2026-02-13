package com.project.fitness.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationResponse {

	private Long id;
	private String type;
	private String recommendation;
	private List<String> improvements;
	private List<String> suggestions;
	private List<String> safety;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Long userId;
	private Long activityId;
}
