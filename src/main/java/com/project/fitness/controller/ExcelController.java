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
import com.project.fitness.service.ExcelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

	@Autowired
	private final ExcelService excelService;

	@GetMapping("/user/type/{type}")
	public ResponseEntity<byte[]> generateExcel(@RequestHeader("X-USER-ID") Long userId,
			@PathVariable ExportType type) {
		byte[] excel = excelService.generateExcel(userId, type);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=report.xlsx")
				.contentType(
						MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(excel);
	}
}
