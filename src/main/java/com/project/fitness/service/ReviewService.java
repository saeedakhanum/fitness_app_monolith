package com.project.fitness.service;

import org.springframework.stereotype.Service;

import com.project.fitness.dto.ReviewRequest;
import com.project.fitness.dto.ReviewResponse;
import com.project.fitness.entity.Review;
import com.project.fitness.entity.User;
import com.project.fitness.exception.BadRequestException;
import com.project.fitness.exception.ResourceNotFoundException;
import com.project.fitness.repository.ReviewRepository;
import com.project.fitness.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;

	// Review on application
	public ReviewResponse createApplicationReview(Long userId, ReviewRequest request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		Review review = reviewRepository.getAppReviewByUser(userId);
		if (review == null) {
			review = new Review();
			review.setUser(user);
		}
		review.setRating(request.getRating());
		review.setComment(request.getComment());
		return mapToReviewResponse(reviewRepository.save(review));
	}
	
	
/*
 * i was thinking to give the review on product
	public ReviewResponse reviewProduct(Long userId, Long productId, ReviewRequest request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
		Review review = reviewRepository.getProductReviewByUser(userId, productId, ReviewTarget.PRODUCT)
				.orElse(Review.builder().user(user).product(product).reviewTarget(ReviewTarget.PRODUCT).build());
		review.setRating(request.getRating());
		review.setComment(request.getComment());
		return mapToReviewResponse(reviewRepository.save(review));
	}
*/
	// Delete review
	public void deleteReview(Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new ResourceNotFoundException("Review", "reviewId", reviewId));
		reviewRepository.delete(review);
	}

	//Update review
	public ReviewResponse updateReview(ReviewRequest request,Long userId) {
		Review review = reviewRepository.findByUserId(userId);
		if(review==null) {
			throw new ResourceNotFoundException("Review not found for the given userId");
		}
		review.setRating(request.getRating());
		review.setComment(request.getComment());
		Review updatedReview=reviewRepository.save(review);
		return mapToReviewResponse(updatedReview);
	}

	//Get review of the user
	public ReviewResponse getUserReview(Long userId) {
		Review review=reviewRepository.findByUserId(userId);
		if(review==null) {
			throw new ResourceNotFoundException("Review not found with the given userId");
		}
		return mapToReviewResponse(review);
	}
	
	ReviewResponse mapToReviewResponse(Review review) {
		ReviewResponse response = new ReviewResponse();
		response.setId(review.getId());
		response.setRating(review.getRating());
		response.setComment(review.getComment());
		response.setUserId(review.getUser().getId());
		response.setCreatedAt(review.getCreatedAt());
		response.setUpdatedAt(review.getUpdatedAt());
		return response;
	}
}
