package net.microfin.financeapp.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceAccessException(ResourceAccessException e) {
        log.error("Resource not found exception", e);
        return buildErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("ConstraintViolationException occured", ex);
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        return ResponseEntity.badRequest().body(
                new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation error",
                        errors,
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleResponseStatusException(ResponseStatusException ex) {
        log.error("Exception occured", ex);
        return buildErrorResponse(ex.getStatusCode().value(), ex.getReason());
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(int status, String message) {

        return ResponseEntity.status(status).body(
                new ErrorResponseDTO(
                        status,
                        message,
                        List.of(), LocalDateTime.now()));
    }
}
