package com.project.fitness.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.fitness.dto.ActivityRequest;
import com.project.fitness.dto.ActivityResponse;
import com.project.fitness.entity.ActivityType;
import com.project.fitness.service.ActivityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

//This is used to group the related operation of an API like post, get, put etc
@Tag(name = "activity", description = "Operations on Activity")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/activities")
public class ActivityController {

	private final ActivityService activityService;
	private Logger logger = LoggerFactory.getLogger(ActivityController.class);

	// Add user activity
	// create
	@PostMapping(value="/user/activity",produces = "application/json", consumes = "application/json")
	public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request) {
		return new ResponseEntity<ActivityResponse>(activityService.createActivity(request), HttpStatus.CREATED);
	}

	// get all the activities
	@GetMapping("/admin/users")
	public ResponseEntity<List<ActivityResponse>> getUsersActivities() {
		logger.info("Recieved the request to fetch the activities");
		return ResponseEntity.ok(activityService.getAllActivities());
	}

	// get a single user activities based on the user id

	/**
	 * @Operation annotation is used to customise the individual endpoints by
	 *            providing the short message
	 */
	@Operation(summary = "get user activity by user id", description = " Fetch the user activities details based on the user id")

	/**
	 * @ApiResponse is use to customize the API response status code.
	 * @ApiResponse to group the ApiResponse
	 */
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User activities retrieved successfully"),
			@ApiResponse(responseCode = "404", description = "Ativities are not found") })
	/**
	 * We can give the information about the parameter by using @Parameter
	 * annotation
	 */
	// get activities by the userId
	@GetMapping("/admin/user")
	public ResponseEntity<List<ActivityResponse>> getUserActivities(
			@Parameter(description = "id of the user") @RequestHeader(value = "X-USER-ID") Long userId) {
		logger.info("Recieved request to fetch the request by the user id : {}", userId);
		return ResponseEntity.ok(activityService.getUserActivities(userId));
	}

	// get list of activities of the user by activity type
	@GetMapping("/user/{userId}/type/{type}")
	public ResponseEntity<List<ActivityResponse>> getActivitiesOfUserByType(@PathVariable("userId") Long userId,
			@PathVariable("type") ActivityType type) {
		return new ResponseEntity<>(activityService.fetchActivitiesOfUserByType(userId, type), HttpStatus.OK);
	}

	// Get activity by activity id
	@GetMapping("/admin/{activityId}")
	public ResponseEntity<ActivityResponse> getActvityById(@PathVariable("activityId") Long activityId) {
		logger.info("Recieved the request to fetch the activity : {}", activityId);
		return new ResponseEntity(activityService.getActivityById(activityId), HttpStatus.OK);
	}

	// update the activity
	@PutMapping("/admin/{activityId}")
	public ResponseEntity<ActivityResponse> updateActivity(@PathVariable Long activityId,
			@RequestBody ActivityRequest request) {
		logger.info("Recieved the request to update the activity");
		return new ResponseEntity<ActivityResponse>(activityService.updateActivity(activityId, request), HttpStatus.OK);
	}

	// delete activity
	@DeleteMapping("/admin/{activityId}")
	public ResponseEntity<Void> deleteActivity(@PathVariable Long activityId) {
		logger.info("Recieved the request to delete the activity by activity id : {}" + activityId);
		activityService.deleteActivityById(activityId);
		return ResponseEntity.noContent().build();
	}

	// get the latest activity of the user
	@GetMapping("/admin/{userId}/latest")
	public ResponseEntity<ActivityResponse> getLatestActivityOfUser(@PathVariable Long userId) {
		logger.info("Recieved the request to get the latest activity of the user : {}", userId);
		return new ResponseEntity<>(activityService.getLatestActivity(userId), HttpStatus.OK);
	}

	// Get user activity count
	@GetMapping("/admin/user/{userId}/count")
	public ResponseEntity<Long> getActivityCount(@PathVariable Long userId) {
		logger.info("Recieved request to get the count of the user's activities");
		return new ResponseEntity<>(activityService.getActivityCount(userId), HttpStatus.OK);
	}

	// get user activity based on the type
	@GetMapping("/admin/user/{userId}/count/type/{type}")
	public ResponseEntity<Long> GetUserActivitiesCountByType(@PathVariable Long userId,
			@PathVariable ActivityType type) {
		return new ResponseEntity<>(activityService.getUserActivitiesCountByType(userId, type), HttpStatus.OK);
	}

	// Get activities between dates
	@GetMapping("/user/{userId}/range")
	public ResponseEntity<List<ActivityResponse>> getActivitiesBetweenDates(@PathVariable Long userId,
			@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
		logger.info("fetching the activities betweeen {} to {} for the user : {}", start, end, userId);
		return new ResponseEntity<>(activityService.getActivitiesBetweenDates(start, end, userId), HttpStatus.OK);
	}

	// get total calories of the user
	@GetMapping("/user/{userId}/totalcalories")
	public ResponseEntity<Integer> getTotalCalories(@PathVariable Long userId) {
		logger.info("Recieved the request to get the total calories of the user : {}", userId);
		return new ResponseEntity<>(activityService.getTotalCalories(userId), HttpStatus.OK);
	}

	// Get maximum calories burnt by the user
	@GetMapping("/user/{userId}/maxcalories")
	public ResponseEntity<Integer> getMaximumCaloriesOfTheUser(@PathVariable Long userId) {
		return new ResponseEntity<Integer>(activityService.getMaxCaloriesBurntByTheUser(userId), HttpStatus.OK);
	}

	//Get minimum calories burnt by the user
	@GetMapping("/user/{userId}/mincalories")
	public ResponseEntity<Integer> getMinimumClories(@PathVariable Long userId) {
		return new ResponseEntity<Integer>(activityService.getMinCaloriesOfTheUser(userId), HttpStatus.OK);
	}
}