package com.project.fitness.exception;

public class ResourceNotFoundException extends RuntimeException {
    String resourceName;
    String fieldName;
    Long fieldId;
    
    public ResourceNotFoundException(String message) {
    	super(message);
    }
  
	public ResourceNotFoundException(String resourceName,String fieldName,Long fieldId) {
		super(String.format("%s not found with %s : %d", resourceName,fieldName,fieldId ));
	}
}
