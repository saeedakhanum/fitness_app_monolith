package com.project.fitness.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.project.fitness.dto.ActivityRequest;
import com.project.fitness.dto.ActivityResponse;
import com.project.fitness.entity.Activity;
import com.project.fitness.entity.ActivityType;
import com.project.fitness.entity.User;
import com.project.fitness.exception.BadRequestException;
import com.project.fitness.exception.ResourceNotFoundException;
import com.project.fitness.repository.ActivityRepository;
import com.project.fitness.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityService {
	private final Logger logger = LoggerFactory.getLogger("ActivityService.class");
	private final ActivityRepository activityRepository;
	private final UserRepository userRepository;

	// track activity
	public ActivityResponse createActivity(ActivityRequest request) {
		logger.info("create activity request for user {} ", request.getUserId());
		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User","userId",request.getUserId()));
		Activity activity = Activity.builder().user(user).type(request.getType()).duration(request.getDuration())
				.caloriesBurnt(request.getCaloriesBurned()).startTime(request.getStartTime())
				.additionalMetrics(request.getAdditionalMetrics()).build();
		Activity savedActivity = activityRepository.save(activity);
		logger.info("activity is created successfully");
		return mapToActivityResponse(savedActivity);
	}

	// get all the activities
	public List<ActivityResponse> getAllActivities() {
		logger.info("Fetching all the activities");
		List<Activity> activities = activityRepository.findAll();
		if (activities.isEmpty()) {
			logger.info("no activities found");
			throw new ResourceNotFoundException("Activities not found");
		}
		logger.info("Actvities are fetched successfully");
		return activities.stream().map(activity -> mapToActivityResponse(activity)).toList();
	}

	// get activities by userId
	public List<ActivityResponse> getUserActivities(Long userId) {
		logger.info("fetching activities by the user id : {}", userId);
		List<Activity> activities = activityRepository.findByUserId(userId);
		if (activities.isEmpty()) {
			logger.info("No activies found for the user id : {}", userId);
			throw new ResourceNotFoundException("No activities found");
		}
		return activities.stream().map(this::mapToActivityResponse).collect(Collectors.toList());
	}

	// get the activity if the user by type
	public List<ActivityResponse> fetchActivitiesOfUserByType(Long userId, ActivityType type) {
		logger.info("fetching the user");
		userRepository.findById(userId).orElseThrow(() -> {
			logger.info("User not found");
			return new ResourceNotFoundException("User", "userId", userId);
		});
		List<Activity> activities = activityRepository.findByUserIdAndType(userId, type);
		if (activities.isEmpty()) {
			logger.info("No activities found for user : {} and type : {}", userId, type);
			throw new ResourceNotFoundException("No activities found");
		}
		return activities.stream().map(activity -> mapToActivityResponse(activity)).toList();
	}

	// Get activity by activity id
	public ActivityResponse getActivityById(Long activityId) {
		logger.info("get activity by activity id : {}", activityId);
		Activity activity = activityRepository.findById(activityId)
				.orElseThrow(() -> new ResourceNotFoundException("Activity", "activityId", activityId));
		return mapToActivityResponse(activity);
	}

	// update the activity
	public ActivityResponse updateActivity(Long activityId, ActivityRequest request) {
		logger.info("fetching the activity based on the activity id : {}", activityId);
		Activity activity = activityRepository.findById(activityId)
				.orElseThrow(() -> new ResourceNotFoundException("Activity", "activity", activityId));
		logger.info("fetching the user by the user id :{}", request.getUserId());
		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", request.getUserId()));
		activity.setType(request.getType());
		activity.setAdditionalMetrics(request.getAdditionalMetrics());
		activity.setDuration(request.getDuration());
		activity.setCaloriesBurnt(request.getCaloriesBurned());
		activity.setStartTime(request.getStartTime());
		activity.setUser(user);
		Activity updatedActivity = activityRepository.save(activity);
		logger.info("Activity is updated successfully");
		return mapToActivityResponse(updatedActivity);
	}

	// delete the activity
	public void deleteActivityById(Long activityId) {
		logger.info("delete activity by the activity id :{}", activityId);
		Activity activity = activityRepository.findById(activityId)
				.orElseThrow(() -> new ResourceNotFoundException("Activity", "activityId", activityId));
		activityRepository.delete(activity);
		logger.info("activity is deleted successfully");
	}

	// get the latest activity of the user
	public ActivityResponse getLatestActivity(Long userId) {
		logger.info("Fetching the latest activity of the user : {}", userId);
		User user = userRepository.findById(userId).orElseThrow(() -> {
			logger.warn("User not found");
			return new ResourceNotFoundException("User", "userId", userId);
		});
		Activity activity = activityRepository.findTopByUserIdOrderByStartTimeDesc(userId);
		if (activity == null) {
			logger.warn("No latest activity found for the user : {}", userId);
			throw new ResourceNotFoundException("No latest activity found");
		}
		return mapToActivityResponse(activity);
	}

	// get user activities count
	public Long getActivityCount(Long userId) {
		logger.info("get the activity count");
		User user = userRepository.findById(userId).orElseThrow(() -> {
			logger.info("User not found");
			return new ResourceNotFoundException("User", "userId", userId);
		});
		Long count = activityRepository.countByUserId(userId);
		return count;
	}

	// get the user activities count based on the type
	public Long getUserActivitiesCountByType(Long userId, ActivityType type) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		Long count = activityRepository.countByUserIdAndType(userId, type);
		return count;
	}

	// Get activities between the dates
	public List<ActivityResponse> getActivitiesBetweenDates(LocalDateTime start, LocalDateTime end, Long userId) {
		if (start.isAfter(end)) {
			throw new BadRequestException("Invalid dates range");
		}
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		List<Activity> activities = activityRepository.findByUserIdAndStartTimeBetween(userId, start, end);
		if (activities.isEmpty()) {
			throw new ResourceNotFoundException("Activities are not found");
		}
		return activities.stream().map(activity->mapToActivityResponse(activity)).toList();
	}

	//get the total calories of the user
	public Integer getTotalCalories(Long userId) {
		logger.info("Get the total calories of the user :{}",userId);
		User user=userRepository.findById(userId).orElseThrow(()-> {
			logger.info("User not found");
			return new ResourceNotFoundException("User","userId",userId);
		});
		Integer caloriesSum=activityRepository.totalCaloriesByUser(userId);
		if(caloriesSum==null) {
			throw new ResourceNotFoundException("Calories are not found");
		}
		return caloriesSum;
		}
	
	//Get Maximum calories burnt by the user
	public Integer getMaxCaloriesBurntByTheUser(Long userId) {
		User user=userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User","userId",userId));
		return activityRepository.getMaxCaloriesBurntByTheUser(userId);	
	}
	
	
	//Get minimum calories of the user
	public Integer getMinCaloriesOfTheUser(Long userId) {
		User user=userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User","userId",userId));
		return activityRepository.getMinimumCaloriesOfTheUser(userId);
	}

	public ActivityResponse mapToActivityResponse(Activity activity) {
		ActivityResponse response = new ActivityResponse();
		response.setId(activity.getId());
		response.setUserId(activity.getUser().getId());
		response.setType(activity.getType());
		response.setDuration(activity.getDuration());
		response.setCaloriesBurned(activity.getCaloriesBurnt());
		response.setStartTime(activity.getStartTime());
		response.setAdditionalMetrics(activity.getAdditionalMetrics());
		response.setCreatedAt(activity.getCreatedAt());
		response.setUpdatedAt(activity.getUpdatedAt());
		return response;
	}

}
