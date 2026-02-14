package com.project.fitness.service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.project.fitness.entity.Activity;
import com.project.fitness.entity.ExportType;
import com.project.fitness.entity.Recommendation;
import com.project.fitness.entity.Review;
import com.project.fitness.entity.User;
import com.project.fitness.repository.ActivityRepository;
import com.project.fitness.repository.RecommendationRepository;
import com.project.fitness.repository.ReviewRepository;
import com.project.fitness.utility.HeaderFooterPageEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PdfService {

	private final ActivityRepository activityRepository;
	private final RecommendationRepository recommendationRepository;
	private final ReviewRepository reviewRepository;

	public byte[] generatePdf(Long userId, ExportType pdfType) {

		// It is the storage where data will be stored
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			// creating the empty page
			Document document = new Document();
			// Document → PdfWriter → ByteArrayOutputStream
			// Document me jo likhoge Writer usse PDF format karega aur Bytes out me daalega
			PdfWriter writer = PdfWriter.getInstance(document, out);

			// Adding the header and footer
			writer.setPageEvent(new HeaderFooterPageEvent());

			// Now document is in write mode ex jese notebook khol di
			document.open();

			switch (pdfType) {
			case ACTIVITY -> addActivitySection(document, userId);
			case RECOMMENDATION -> addRecommendationSection(document, userId);
			case REVIEW -> addReviewSection(document, userId);
			default -> throw new IllegalArgumentException("Invalid PDF Type");
			}
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Pure PDF ka binary data
		return out.toByteArray();
	}

	// add title
	public void addTitle(Document document, String title) {
		try {
			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
			Paragraph titlePara = new Paragraph(title, titleFont);
			titlePara.setAlignment(Element.ALIGN_CENTER);
			titlePara.setSpacingAfter(20);
			document.add(titlePara);
			document.add(Chunk.NEWLINE);
		} catch (DocumentException ex) {
			ex.printStackTrace();
		}
	}

	// add user info
	public void addUserInfo(Document document, User user) {
		try {
			document.add(new Paragraph("User: " + user.getFirstName() + " " + user.getLastName()));
			document.add(new Paragraph("Email: " + user.getEmail()));
			document.add(Chunk.NEWLINE);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// ======================================================
	// Activity pdf section
	// ======================================================
	public void addActivitySection(Document document, Long userId) {
		List<Activity> activities = activityRepository.findByUserId(userId);
		if (activities.isEmpty()) {
			document.add(new Paragraph("No activities found"));
			return;
		}

		PdfPTable table = new PdfPTable(5);
		table.addCell("Start Time");
		table.addCell("Username");
		table.addCell("Activity Type");
		table.addCell("Duration (min)");
		table.addCell("Calories Burnt");
		for (Activity a : activities) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
			table.addCell(String.valueOf(a.getStartTime().format(formatter)));
			table.addCell(a.getUser().getFirstName());
			table.addCell(a.getType().name());
			table.addCell(String.valueOf(a.getDuration()));
			table.addCell(String.valueOf(a.getCaloriesBurnt()));
		}
		document.add(table);

	}
	// ======================================
	// Recommendation pdf section
	// ======================================

	public void addRecommendationSection(Document document, Long userId) {
		List<Recommendation> recommendations = recommendationRepository.findByUserId(userId);
		if (recommendations.isEmpty()) {
			document.add(new Paragraph("No Recommendation Found"));
			return;
		}

		Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
		Font normalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

		for (Recommendation r : recommendations) {
			// Activity Name
			document.add(new Paragraph(r.getActivity().getType().name(), headingFont));
			// Recommendation Type
			document.add(new Paragraph(r.getType(), normalFont));
			document.add(Chunk.NEWLINE);
			// Recommendation
			document.add(new Paragraph("Recommendations: ", normalFont));
			document.add(new Paragraph(r.getRecommendation(), normalFont));
			document.add(Chunk.NEWLINE);
			// Improvements
			if (r.getImprovements() != null && !r.getImprovements().isEmpty()) {
				document.add(new Paragraph("Improvements: ", headingFont));
				for (String imp : r.getImprovements()) {
					document.add(new Paragraph("• " + imp, normalFont));
				}
				document.add(Chunk.NEWLINE);
			}

			// Suggestions
			if (r.getSuggestions() != null && r.getSuggestions().isEmpty()) {
				document.add(new Paragraph("Suggestion: ", headingFont));
				for (String sug : r.getSuggestions()) {
					document.add(new Paragraph(sug, normalFont));
				}
				document.add(Chunk.NEWLINE);
			}

			// Safety instruction
			if (r.getSafety() != null && !r.getSafety().isEmpty()) {
				document.add(new Paragraph("Safety: ", headingFont));
				for (String s : r.getSafety()) {
					document.add(new Paragraph(s, normalFont));
				}
				document.add(Chunk.NEWLINE);
			}

			// created date
			document.add(new Paragraph("Generated on: " + r.getCreatedAt(),
					FontFactory.getFont(FontFactory.HELVETICA_BOLD)));

			// divider line
			document.add(Chunk.NEWLINE);
			document.add(new LineSeparator());
			document.add(Chunk.NEWLINE);
		}
	}

	// ======================================================
	// Review pdf section
	// ======================================================

	public void addReviewSection(Document document, Long userId) {
		try {
			Review review = reviewRepository.getAppReviewByUser(userId);
			document.add(new Paragraph("User Review", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
			document.add(Chunk.NEWLINE);
			//User
			document.add(new Paragraph("User: "+review.getUser().getFirstName()));
			//Email
			document.add(new Paragraph("Email: "+review.getUser().getEmail()));
			// rating
			document.add(new Paragraph("Rating: " + review.getRating()));
			// comment
			document.add(new Paragraph("Comment: " + review.getComment()));
			// Date
			document.add(new Paragraph("Reviewed on: " + review.getCreatedAt()));
			int nextInt = ThreadLocalRandom.current().nextInt(0,10);
		} catch (DocumentException ex) {
			ex.printStackTrace();
		}
	}
}
