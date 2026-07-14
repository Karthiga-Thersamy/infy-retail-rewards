package com.retail.rewards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static class ErrorResponse {
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    // Getters are required for Jackson to serialize them to JSON
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
}


    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFound(CustomerNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            ConstraintViolationException.class,
            DateTimeException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        String message = ex.getMessage();

        // 1. Handle Constraint Violations (e.g., on @RequestParam/@PathVariable)
        if (ex instanceof ConstraintViolationException) {
            message = ((ConstraintViolationException) ex).getConstraintViolations().stream()
                    .map(this::formatConstraintViolation)
                    .collect(Collectors.joining(", "));
        } 
        // 2. Handle Spring Validation Bind Errors (e.g., @Valid on @RequestBody)
        else if (ex instanceof MethodArgumentNotValidException) {
            message = ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        } 
        // 3. Handle Parameter Type Mismatches
        else if (ex instanceof MethodArgumentTypeMismatchException) {
            MethodArgumentTypeMismatchException mismatch = (MethodArgumentTypeMismatchException) ex;
            String fieldName = mismatch.getName() != null ? mismatch.getName() : "parameter";
            message = fieldName + " must be a valid value";
        }

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private String formatConstraintViolation(ConstraintViolation<?> violation) {
        String property = violation.getPropertyPath().toString();
        int lastDot = property.lastIndexOf('.');
        String fieldName = lastDot >= 0 ? property.substring(lastDot + 1) : property;
        return fieldName + " " + violation.getMessage();
    }
}