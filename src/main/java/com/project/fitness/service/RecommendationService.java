package com.project.fitness.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.fitness.dto.RecommendationRequest;
import com.project.fitness.dto.RecommendationResponse;
import com.project.fitness.entity.Activity;
import com.project.fitness.entity.Recommendation;
import com.project.fitness.entity.User;
import com.project.fitness.exception.ResourceNotFoundException;
import com.project.fitness.repository.ActivityRepository;
import com.project.fitness.repository.RecommendationRepository;
import com.project.fitness.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationService {

	private final UserRepository userRepository;
	private final ActivityRepository activityRepository;
	private final RecommendationRepository recommendationRepository;

	// create recommendations
	public RecommendationResponse generateRecommendation(RecommendationRequest request) {
		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User","userId", request.getUserId()));
		Activity activity = activityRepository.findById(request.getActivityId())
				.orElseThrow(() -> new ResourceNotFoundException("Activity","activityId", request.getActivityId()));
		Recommendation recommendation = Recommendation.builder().type(request.getType())
				.recommendation(request.getRecommendation()).improvements(request.getImprovements())
				.suggestions(request.getSuggestions()).safety(request.getSafety()).user(user).activity(activity)
				.build();
		Recommendation savedRecommendation = recommendationRepository.save(recommendation);
		return mapToRecommendationResponse(savedRecommendation);
	}

	// get the user recommendations
	public List<RecommendationResponse> getUserRecommendations(Long userId) {
		List<Recommendation> recommendations = recommendationRepository.findByUserId(userId);
		return recommendations.stream().map(this::mapToRecommendationResponse).toList();
	}

	// get recommendation by the activityId
	public List<RecommendationResponse> getActivityRecommendation(String activityId) {
		List<Recommendation> recommendations = recommendationRepository.findByActivityId(activityId);
		if(recommendations.isEmpty()) {
			throw new ResourceNotFoundException("No recommendations found");
		}
		return recommendations.stream().map(this::mapToRecommendationResponse).toList();
	}

	// Get recommendation by recommendation id
	public RecommendationResponse getRecommendationById(Long recommendationId) {
		Recommendation recommendation = recommendationRepository.findById(recommendationId).orElseThrow(
				() -> new ResourceNotFoundException("Recommendation", "recommendationId", recommendationId));
		return mapToRecommendationResponse(recommendation);
	}

	// Get all recommendations
	public List<RecommendationResponse> getAllRecommendations() {
		List<Recommendation> recommendations = recommendationRepository.findAll();
		if (recommendations.isEmpty() || recommendations == null) {
			throw new ResourceNotFoundException("No recommendations found");
		}
		return recommendations.stream().map(recommendation -> mapToRecommendationResponse(recommendation)).toList();
	}

	// Delete recommendation
	public void deleteRecommendation(Long recommendationId) {
		Recommendation recommendation = recommendationRepository.findById(recommendationId).orElseThrow(
				() -> new ResourceNotFoundException("Recommendation", "recommendationId", recommendationId));
		recommendationRepository.delete(recommendation);
	}

	// Update recommendation
	public RecommendationResponse updateRecommendation(Long recommendationId, RecommendationRequest request) {
		Recommendation recommendation = recommendationRepository.findById(recommendationId).orElseThrow(
				() -> new ResourceNotFoundException("Recommendation", "recommendationid", recommendationId));
		recommendation.setType(request.getType());
		recommendation.setRecommendation(request.getRecommendation());
		recommendation.setImprovements(request.getImprovements());
		recommendation.setSuggestions(request.getSuggestions());
		recommendation.setSafety(request.getSafety());
		recommendation.setUser(userRepository.findById(request.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", request.getUserId())));
		recommendation.setActivity(activityRepository.findById(request.getActivityId())
				.orElseThrow(() -> new ResourceNotFoundException("Activity", "activityId", request.getActivityId())));
		Recommendation updatedRecommendation = recommendationRepository.save(recommendation);
		return mapToRecommendationResponse(updatedRecommendation);
	}

	// Get recommendations by type
	public List<RecommendationResponse> getRecommendationsByType(String type) {
		List<Recommendation> recommendations = recommendationRepository.findByType(type);
		if (recommendations.isEmpty()) {
			throw new ResourceNotFoundException("No recommendations found");
		}
		return recommendations.stream().map(recommendation -> mapToRecommendationResponse(recommendation)).toList();
	}

	// Count recommendation for a user
	public Long countRecommendationsForUser(Long userId) {
		return recommendationRepository.countRecommendationsForUser(userId);
	}

	// Count recommendation for the activity
	public Long countRecommendationsForActivity(Long activityId) {
		return recommendationRepository.countRecommendationsForActivity(activityId);
	}

	// Get latest recommendation for a user
	public RecommendationResponse getLatestRecommendationForUser(Long userId) {
		Recommendation recommendation = recommendationRepository.getLatestRecommendationForUser(userId)
				.orElseThrow(() -> new ResourceNotFoundException("No recommendation found for the user"));
		return mapToRecommendationResponse(recommendation);
	}
	
	//Delete all recommendations for a user
	public void deleteAllRecommendationsForUser(Long userId) {
		recommendationRepository.deleteByUserId(userId);
	}

	public RecommendationResponse mapToRecommendationResponse(Recommendation recommendation) {
		RecommendationResponse response = new RecommendationResponse();
		response.setId(recommendation.getId());
		response.setType(recommendation.getType());
		response.setRecommendation(recommendation.getRecommendation());
		response.setImprovements(recommendation.getImprovements());
		response.setSuggestions(recommendation.getSuggestions());
		response.setSafety(recommendation.getSafety());
		response.setActivityId(recommendation.getActivity().getId());
		response.setUserId(recommendation.getUser().getId());
		response.setCreatedAt(recommendation.getCreatedAt());
		response.setUpdatedAt(recommendation.getUpdatedAt());
		return response;
	}
}
