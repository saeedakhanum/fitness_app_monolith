package com.project.fitness.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.fitness.entity.Activity;
import com.project.fitness.entity.ActivityType;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

	@Query("select a from Activity a where a.user.id=:userId")
	List<Activity> findByUserId(@Param("userId") Long userId);

	@Query("select a from Activity a where a.user.id=:userId order by a.startTime desc")
	Activity findTopByUserIdOrderByStartTimeDesc(@Param("userId") Long userId);

	@Query("select a from Activity a where a.user.id=:userId and a.type=:type")
	List<Activity> findByUserIdAndType(@Param("userId") Long userId, @Param("type") ActivityType type);

	@Query("select count(a) from Activity a where a.user.id=:userId")
	Long countByUserId(@Param("userId") Long userId);

	@Query("select count(a) from Activity a where a.user.id=:userId and a.type=:type")
	Long countByUserIdAndType(@Param("userId") Long userId, @Param("type") ActivityType type);

	@Query("select a from Activity a where a.user.id=:userId and a.startTime between :start and :end")
	List<Activity> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);

	@Query("select SUM(a.caloriesBurnt) from Activity a where a.user.id=:userId")
	Integer totalCaloriesByUser(@Param("userId") Long userId);

	@Query("select MAX(a.caloriesBurnt) from Activity a where a.user.id=:userId")
	Integer getMaxCaloriesBurntByTheUser(@Param("userId") Long userId);

	@Query("select MIN(a.caloriesBurnt) from Activity a where a.user.id=:userId")
	Integer getMinimumCaloriesOfTheUser(@Param("userId") Long userId);
}
