package com.project.fitness.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.project.fitness.entity.ActivityType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityRequest {

	private Long userId;
	private ActivityType type;
	private Map<String, Object> additionalMetrics;
	private Integer duration;
	private Integer caloriesBurned;
	private LocalDateTime startTime;

}
