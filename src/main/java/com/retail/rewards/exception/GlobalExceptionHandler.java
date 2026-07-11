package com.retail.rewards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.DateTimeException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<String> handleCustomerNotFound(CustomerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            ConstraintViolationException.class,
            DateTimeException.class,
            MethodArgumentNotValidException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleBadRequest(Exception ex) {
        if (ex instanceof ConstraintViolationException) {
            String message = ((ConstraintViolationException) ex).getConstraintViolations().stream()
                    .map(this::formatConstraintViolation)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(message);
        }

        if (ex instanceof MethodArgumentTypeMismatchException) {
            MethodArgumentTypeMismatchException mismatch = (MethodArgumentTypeMismatchException) ex;
            String fieldName = mismatch.getName() != null ? mismatch.getName() : "parameter";
            return ResponseEntity.badRequest().body(fieldName + " must be a valid value");
        }

        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    private String formatConstraintViolation(ConstraintViolation<?> violation) {
        String property = violation.getPropertyPath().toString();
        int lastDot = property.lastIndexOf('.');
        String fieldName = lastDot >= 0 ? property.substring(lastDot + 1) : property;
        return fieldName + " " + violation.getMessage();
    }
}
