package com.project.fitness.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.fitness.dto.ReviewRequest;
import com.project.fitness.dto.ReviewResponse;
import com.project.fitness.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	// Review on application
	@PostMapping("/user/app/review")
	public ResponseEntity<ReviewResponse> addAppReview(@RequestHeader("X-USER-ID") Long userId,
			@Valid @RequestBody ReviewRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createApplicationReview(userId, request));
	}

	/*
	  //Review on product
	  
	  @PostMapping("/product/review") public ResponseEntity<ReviewResponse>
	  reviewProduct(@RequestHeader("X_USER_ID") Long userId,@PathVariable Long productId,
	  @RequestBody ReviewRequest request ){ 
	  return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.reviewProduct(userId,productId,request)); }
	 
	*/

	// get review of the user
	@GetMapping("/user/{userId}/review")
	public ResponseEntity<ReviewResponse> getUserReview(@PathVariable Long userId) {
		return new ResponseEntity<ReviewResponse>(reviewService.getUserReview(userId), HttpStatus.OK);
	}

	// update review of the user
	@PutMapping("/user/{userId}/review")
	public ResponseEntity<ReviewResponse> updateReview(@RequestBody ReviewRequest request, @PathVariable Long userId) {
		return new ResponseEntity<>(reviewService.updateReview(request, userId), HttpStatus.OK);
	}

	// Delete review
	@DeleteMapping("/user/delete/{reviewId}")
	public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
		reviewService.deleteReview(reviewId);
		return ResponseEntity.noContent().build();
	}

}
