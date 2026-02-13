package com.project.fitness.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.project.fitness.entity.Activity;
import com.project.fitness.entity.ExportType;
import com.project.fitness.entity.Recommendation;
import com.project.fitness.entity.Review;
import com.project.fitness.repository.ActivityRepository;
import com.project.fitness.repository.RecommendationRepository;
import com.project.fitness.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CsvService {

	private final ActivityRepository activityRepository;
	private final RecommendationRepository recommendationRepository;
	private final ReviewRepository reviewRepository;

	public byte[] generateCsv(Long userId, ExportType type) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); PrintWriter writer = new PrintWriter(out)) {
			switch (type) {
			case ACTIVITY -> writeActivityCsv(writer, userId);
			case RECOMMENDATION -> writeRecommendationCsv(writer, userId);
			case REVIEW -> writeReviewCsv(writer, userId);
			default -> throw new IllegalArgumentException("Invalid Csv type");
			}
			writer.flush();
			return out.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("CSV Generation Failed!", e);
		}
	}

	// =====================================================
	// Activity CSV
	// =====================================================
	public void writeActivityCsv(PrintWriter writer, Long userId) {
		writer.println("Date,User,Email,Activity type,Duration (min),Calories Burnt");
		List<Activity> activities = activityRepository.findByUserId(userId);
		if (activities == null || activities.isEmpty()) {
			writer.println("No activities found");
			return;
		}
		for (Activity a : activities) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
			String date = a.getStartTime().format(formatter);
			String name = a.getUser().getFirstName();
			String email = a.getUser().getEmail();
			String type = a.getType() != null ? a.getType().name() : "N/A";
			String duration = String.valueOf(a.getDuration());
			String caloriesBurnt = String.valueOf(a.getCaloriesBurnt());
			writer.println(date + "," + name + "," + email + "," + type + "," + duration + "," + caloriesBurnt);
		}

	}

	// =====================================================
	// Recommendation CSV
	// =====================================================

	public void writeRecommendationCsv(PrintWriter writer, Long userId) {
		writer.println("Date,User,Email,Activity, Type, Recommendations,Improvements,Suggestion,Safety");
		List<Recommendation> recommendations = recommendationRepository.findByUserId(userId);
		if (recommendations == null || recommendations.isEmpty()) {
			writer.println("No recommendations found");
			return;
		}
		for (Recommendation r : recommendations) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm");
			String date = r.getCreatedAt().format(formatter);
			String user = r.getUser().getFirstName();
			String email = r.getUser().getEmail();
			String activity = r.getActivity() != null ? r.getActivity().getType().name() : "N/A";
			String type = r.getType();
			String recommendation = r.getRecommendation();
			// Convert List<String> to single String
			String improvements = r.getImprovements() != null ? String.join(" | ", r.getImprovements()) : "";

			String suggestions = r.getSuggestions() != null ? String.join(" | ", r.getSuggestions()) : "";

			String safety = r.getSafety() != null ? String.join(" | ", r.getSafety()) : "";

			writer.println(date + "," + user + "," + email + "," + activity + "," + type + "," + recommendation + ","
					+ improvements + "," + suggestions + "," + safety);
		}

	}

	// =====================================================
	// Review CSV
	// =====================================================

	public void writeReviewCsv(PrintWriter writer, Long userId) {
		writer.println("User,Email,Rating,Comment,Reviewed on");
		Review review = reviewRepository.getAppReviewByUser(userId);
		if (review == null) {
			writer.println("No review found");
			return;
		}
		String user = review.getUser().getFirstName();
		String email = review.getUser().getEmail();
		String rating = String.valueOf(review.getRating());
		String comment = review.getComment();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		String reviewedOn = review.getCreatedAt().format(formatter);
		writer.println(user + "," + email + "," + rating + "," + comment + "," + reviewedOn);
	}
}
