package com.project.fitness.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.fitness.entity.ExportType;
import com.project.fitness.service.PdfService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
public class PdfController {

	@Autowired
	private final PdfService pdfService;


	@GetMapping("/admin/{type}")
	public ResponseEntity<byte[]> downloadPdf(@RequestHeader("X-USER-ID") Long userId,
			@PathVariable ExportType type) {
		byte[] pdf = pdfService.generatePdf(userId, type);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=" + type.name().toLowerCase() + "-report.pdf")
				.contentType(MediaType.APPLICATION_PDF).body(pdf);
	}

}
