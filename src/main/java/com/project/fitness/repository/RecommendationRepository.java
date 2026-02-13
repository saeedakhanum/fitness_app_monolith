package com.project.fitness.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.fitness.entity.Recommendation;

import jakarta.transaction.Transactional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

	@Query("select r from Recommendation r where r.user.id=:userId")
	List<Recommendation> findByUserId(@Param("userId") Long userId);

	@Query("select r from Recommendation r where r.activity.id=:activityId")
	List<Recommendation> findByActivityId(@Param("activityId") String activityId);

	@Query("select r from Recommendation r where r.type=:type")
	List<Recommendation> findByType(@Param("type") String type);

	@Query("select count(r) from Recommendation r where r.user.id=:userId")
	Long countRecommendationsForUser(@Param("userId") Long userId);

	@Query("select count(r) from Recommendation r where r.activity.id=:activityId")
	Long countRecommendationsForActivity(@Param("activityId") Long activityId);

	@Query(value="select * from recommendation where user_id=:userId order by created_at desc limit 1",nativeQuery=true)
	Optional<Recommendation> getLatestRecommendationForUser(@Param("userId") Long userId);

	@Modifying
	@Transactional
	@Query("delete from Recommendation r where r.user.id=:userId")
	void deleteByUserId(@Param("userId") Long userId);
}
