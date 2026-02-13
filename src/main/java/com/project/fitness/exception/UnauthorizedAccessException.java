package com.project.fitness.exception;

public class UnauthorizedAccessException extends RuntimeException{

	public UnauthorizedAccessException(String message) {
		super(message);
	}
}
