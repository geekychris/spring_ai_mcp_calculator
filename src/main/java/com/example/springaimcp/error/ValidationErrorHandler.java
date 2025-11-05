package com.example.springaimcp.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller advice to handle validation errors.
 * Provides detailed error responses for validation failures.
 */
@RestControllerAdvice
public class ValidationErrorHandler {

    /**
     * Handle validation errors on request body objects annotated with @Valid
     * 
     * @param ex the validation exception
     * @return a map of field names to error messages
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        response.put("error", "Validation Failed");
        response.put("details", errors);
        
        return response;
    }
    
    /**
     * Handle constraint violation exceptions (e.g., from @Validated controller methods)
     * 
     * @param ex the constraint violation exception
     * @return a map of constraint violation details
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, Object> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String path = violation.getPropertyPath().toString();
            // Remove method name from the property path if present
            String fieldName = path.contains(".") ? path.substring(path.lastIndexOf(".") + 1) : path;
            errors.put(fieldName, violation.getMessage());
        }
        
        response.put("error", "Validation Failed");
        response.put("details", errors);
        
        return response;
    }
}

