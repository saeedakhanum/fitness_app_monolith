package com.project.fitness.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException ex) {
		logger.info("invalid data : {} ", ex.getMessage());
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> {
			String fieldName = error.getField();
			String message = error.getDefaultMessage();
			errors.put(fieldName, message);
		});
		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex,
			HttpServletRequest request) {
		logger.info("Resource not found : {}" + ex.getMessage());
		ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now(),
				request.getRequestURI());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(UnauthorizedAccessException.class)
	public ResponseEntity<Map<String, Object>> handleUnauthorizedAccessException(UnauthorizedAccessException ex,
			HttpServletRequest request) {
		logger.info("Unauthorize access :{}" + ex.getMessage());
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("status", HttpStatus.UNAUTHORIZED.value());
		response.put("message", ex.getMessage());
		response.put("timeStamp", LocalDateTime.now());
		response.put("requestUri", request.getRequestURI());
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException ex,
			HttpServletRequest request) {
		logger.info("bad request : {}", ex.getMessage());
		return new ResponseEntity<>(responseBuilder(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, HttpServletRequest request) {
		logger.info("unexpected error occured : {}", ex.getMessage());
		return new ResponseEntity<>(responseBuilder(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), request),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public Map<String, Object> responseBuilder(int status, String message, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		response.put("status", status);
		response.put("message", message);
		response.put("timeStamp", LocalDateTime.now());
		response.put("requestUri", request.getRequestURI());
		return response;
	}
}
