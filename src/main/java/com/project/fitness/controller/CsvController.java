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
import com.project.fitness.service.CsvService;

import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/api/csv")
@RequiredArgsConstructor
public class CsvController {

	@Autowired
	private final CsvService csvService;

	@GetMapping("/user/type/{type}")
	public ResponseEntity<byte[]> generateCsv(@RequestHeader("X-USER-ID") Long userId, @PathVariable ExportType type) {
		byte[] csv = csvService.generateCsv(userId, type);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.csv")
				.contentType(MediaType.TEXT_PLAIN).body(csv);
	}
}
