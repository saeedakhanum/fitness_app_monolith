package com.project.fitness.service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
public class ExcelService {

	private final ActivityRepository activityRepository;
	private final RecommendationRepository recommendationRepository;
	private final ReviewRepository reviewRepository;

	public byte[] generateExcel(Long userId, ExportType type) {
		//Workbook ka object to create excel. XSSF means .xls format
		try (Workbook workbook = new XSSFWorkbook()) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			switch (type) {
			case ACTIVITY -> createActivitySheet(workbook, userId);
			case RECOMMENDATION -> createRecommendationSheet(workbook, userId);
			case REVIEW -> createReviewSheet(workbook, userId);
			default -> throw new IllegalArgumentException("Invalid excel type");
			}
			//workbook ka data out me insert karna
			workbook.write(out);
			return out.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("Excel generation failed", e);
		}
	}

	// =====================================================
	// ACTIVITY SHEET
	// =====================================================
	public void createActivitySheet(Workbook workbook, Long userId) {
		Sheet sheet = workbook.createSheet("Actvities");
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("User");
		header.createCell(1).setCellValue("Email");
		header.createCell(2).setCellValue("Activity type");
		header.createCell(3).setCellValue("Duration (min)");
		header.createCell(4).setCellValue("Calories Burnt");

		List<Activity> activities = activityRepository.findByUserId(userId);
		int rowNum = 1;
		for (Activity a : activities) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(a.getUser().getFirstName());
			row.createCell(1).setCellValue(a.getUser().getEmail());
			row.createCell(2).setCellValue(a.getType().name());
			row.createCell(3).setCellValue(a.getDuration());
			row.createCell(4).setCellValue(a.getCaloriesBurnt());
		}
		autoSize(sheet, 5);
	}

	// =====================================================
	// RECOMMENDATION SHEET
	// =====================================================
	public void createRecommendationSheet(Workbook workbook, Long userId) {
		Sheet sheet = workbook.createSheet("Recommendations");
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Activity");
		header.createCell(1).setCellValue("Type");
		header.createCell(2).setCellValue("Recommendations");
		header.createCell(3).setCellValue("Created At");
		header.createCell(4).setCellValue("User");
		header.createCell(5).setCellValue("Email");

		List<Recommendation> recommendations = recommendationRepository.findByUserId(userId);

		int rowNum = 1;
		for (Recommendation r : recommendations) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(r.getActivity().getType().name());
			row.createCell(1).setCellValue(r.getType());
			row.createCell(2).setCellValue(r.getRecommendation());
			row.createCell(3).setCellValue(r.getCreatedAt());
			row.createCell(4).setCellValue(r.getUser().getFirstName());
			row.createCell(5).setCellValue(r.getUser().getEmail());;
		}
		autoSize(sheet, 6);
	}

	// =====================================================
	// REVIEW SHEET (single review)
	// =====================================================
	public void createReviewSheet(Workbook workbook, Long userId) {
		Sheet sheet = workbook.createSheet("Reviews");

		Review review = reviewRepository.getAppReviewByUser(userId);
		Row r1 = sheet.createRow(0);
		r1.createCell(0).setCellValue("Rating");
		r1.createCell(1).setCellValue(review.getRating());

		Row r2 = sheet.createRow(1);
		r2.createCell(0).setCellValue("Comment");
		r2.createCell(1).setCellValue(review.getComment());

		Row r3 = sheet.createRow(2);
		r3.createCell(0).setCellValue("Reviewed on");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		r3.createCell(1).setCellValue(review.getCreatedAt().format(formatter));

		
		Row r4=sheet.createRow(3);
		r4.createCell(0).setCellValue("User");
		r4.createCell(1).setCellValue(review.getUser().getFirstName());
		
		Row r5=sheet.createRow(4);
		r5.createCell(0).setCellValue("Email");
		r5.createCell(1).setCellValue(review.getUser().getEmail());

		autoSize(sheet, 5);
	}

	// =====================================================
	// COMMON METHOD
	// =====================================================
	private void autoSize(Sheet sheet, int cols) {
		for (int i = 0; i < cols; i++) {
			sheet.autoSizeColumn(i);
		}
	}
}

/*
Workbook (RAM)
↓
Sheets / Rows / Cells
↓
workbook.write(out)
↓
ByteArrayOutputStream
↓
byte[]
*/