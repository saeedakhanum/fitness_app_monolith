package com.project.fitness.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.fitness.dto.RecommendationRequest;
import com.project.fitness.dto.RecommendationResponse;
import com.project.fitness.service.RecommendationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

	private final RecommendationService recommendationService;

	// generate recommendation
	@PostMapping("/admin/generate")
	public ResponseEntity<RecommendationResponse> generateRecommendation(@RequestBody RecommendationRequest request) {
		return new ResponseEntity<>(recommendationService.generateRecommendation(request), HttpStatus.CREATED);
	}

	// get user recommendation
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<RecommendationResponse>> getUserRecommendations(@PathVariable("userId") Long userId) {
		return ResponseEntity.ok(recommendationService.getUserRecommendations(userId));
	}

	// get recommendation by activity id
	// this specify that against an activity how many recommendations have been
	// provided
	@GetMapping("/user/activity/{activityId}")
	public ResponseEntity<List<RecommendationResponse>> getActivityRecommendation(
			@PathVariable("activityId") String activityId) {
		return ResponseEntity.ok(recommendationService.getActivityRecommendation(activityId));
	}

	// get recommendation by recommendation id
	@GetMapping("/user/recommendation/{recommendationId}")
	public ResponseEntity<RecommendationResponse> getRecommendationByRecommendationId(
			@PathVariable Long recommendationId) {
		return new ResponseEntity<>(recommendationService.getRecommendationById(recommendationId), HttpStatus.OK);
	}

	// Get All recommendations
	@GetMapping("/admin/recommendations")
	public ResponseEntity<List<RecommendationResponse>> getAllRecommendations() {
		return new ResponseEntity<>(recommendationService.getAllRecommendations(), HttpStatus.OK);
	}

	// Delete recommendation
	@DeleteMapping("/admin/recommendation/{recommendationId}")
	public ResponseEntity<Void> deleteRecommendation(@PathVariable Long recommendationId) {
		recommendationService.deleteRecommendation(recommendationId);
		return ResponseEntity.noContent().build();
	}

	// Update recommendation
	@PutMapping("/admin/recommendation/{recommendationId}")
	public ResponseEntity<RecommendationResponse> updateRecommendation(@PathVariable Long recommendationId,
			@RequestBody RecommendationRequest request) {
		return new ResponseEntity<RecommendationResponse>(
				recommendationService.updateRecommendation(recommendationId, request), HttpStatus.OK);
	}

	// Get recommendation by type
	@GetMapping("/user/recommendation/type/{type}")
	public ResponseEntity<List<RecommendationResponse>> getRecommendationsByType(@PathVariable String type) {
		return new ResponseEntity<List<RecommendationResponse>>(recommendationService.getRecommendationsByType(type),
				HttpStatus.OK);
	}

	// Count recommendation for a user
	@GetMapping("/user/{userId}/count")
	public ResponseEntity<String> countRecommendationsForUser(@PathVariable Long userId) {
		Long count = recommendationService.countRecommendationsForUser(userId);
		String message = (count == 1) ? "There is 1 recommendation available for the user"
				: "There are " + count + " recommendations available for the user";
		return new ResponseEntity<String>(message, HttpStatus.OK);
	}

	// Count recommendation for an activity
	@GetMapping("/user/activity/{activityId}/count")
	public ResponseEntity<String> countRecommendationForActivity(@PathVariable Long activityId) {
		Long count = recommendationService.countRecommendationsForActivity(activityId);
		String message = (count == 1) ? "There is 1 recommendation available for the activity"
				: "There are " + count + " recommendations available for the activity";
		return new ResponseEntity<String>(message, HttpStatus.OK);
	}

	// Get the latest recommendation for the user
	@GetMapping("/user/{userId}/recommendation")
	public ResponseEntity<RecommendationResponse> getLatestRecommendationForUser(@PathVariable Long userId) {
		return new ResponseEntity<>(recommendationService.getLatestRecommendationForUser(userId), HttpStatus.OK);
	}

	// Delete all recommendations for the user
	@DeleteMapping("/admin/user/{userId}/allrecommendations")
	public ResponseEntity<Void> deleteAllRecommendationsForUser(@PathVariable Long userId) {
		recommendationService.deleteAllRecommendationsForUser(userId);
		return ResponseEntity.noContent().build();
	}

}
