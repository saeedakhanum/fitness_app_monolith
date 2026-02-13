package com.project.fitness.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.fitness.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	@Query("select r from Review r where r.user.id=:userId")
	Review getAppReviewByUser(@Param("userId") Long userId);

	@Query("select r from Review r where r.user.id=:userId")
	Review findByUserId(@Param("userId")Long userId);

	/*
	@Query("select r from Review r where r.user.id=:userId and r.product.id=:productId and r.target=:target")
	Optional<Review> getProductReviewByUser(@Param("userId") Long userId, @Param("productId") Long productId,
			ReviewTarget target);
	*/
}
