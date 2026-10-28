package com.affiliateLink.common.exception;

import org.hibernate.exception.ConstraintViolationException;
import tools.jackson.databind.exc.InvalidFormatException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InputValidationException.class)
    public ResponseEntity<ApiError> input(InputValidationException exception) {
        return ResponseEntity.badRequest()
                .body(
                        new ApiError(
                                400,
                                "Données invalides",
                                List.of(
                                        new ApiError.FieldError(
                                                exception.getField(), exception.getMessage()))));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> notFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(404)
                .body(new ApiError(404, exception.getMessage(), List.of()));
    }

    @ExceptionHandler(BusinessConflictException.class)
    public ResponseEntity<ApiError> conflict(BusinessConflictException exception) {
        log.warn("Business request refused: {}", exception.getMessage());
        return ResponseEntity.status(409)
                .body(new ApiError(409, exception.getMessage(), List.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException exception) {
        List<ApiError.FieldError> errors = new ArrayList<>();
        for (var error : exception.getBindingResult().getFieldErrors()) {
            errors.add(new ApiError.FieldError(error.getField(), error.getDefaultMessage()));
        }
        return ResponseEntity.badRequest().body(new ApiError(400, "Données invalides", errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> invalidJson(HttpMessageNotReadableException exception) {
        List<ApiError.FieldError> errors = new ArrayList<>();
        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof InvalidFormatException invalid) {
                String field = "body";
                if (!invalid.getPath().isEmpty()) {
                    field = invalid.getPath().get(invalid.getPath().size() - 1).getPropertyName();
                }
                errors.add(new ApiError.FieldError(field, "Valeur non reconnue"));
                break;
            }
            cause = cause.getCause();
        }
        return ResponseEntity.badRequest()
                .body(new ApiError(400, "JSON invalide ou valeur non reconnue", errors));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> invalidParameter(
            MethodArgumentTypeMismatchException exception) {
        return ResponseEntity.badRequest()
                .body(
                        new ApiError(
                                400,
                                "Paramètre invalide",
                                List.of(
                                        new ApiError.FieldError(
                                                exception.getName(), "Valeur non valide"))));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> databaseConflict(DataIntegrityViolationException exception) {
        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof ConstraintViolationException violation
                    && "uk_profile_email".equalsIgnoreCase(violation.getConstraintName())) {
                return ResponseEntity.status(409)
                        .body(new ApiError(409, "Cet email est déjà utilisé", List.of()));
            }
            if (cause instanceof ConstraintViolationException violation
                    && "uk_one_active_campaign_per_offre"
                    .equalsIgnoreCase(violation.getConstraintName())) {
                return ResponseEntity.status(409)
                        .body(
                                new ApiError(
                                        409,
                                        "Une campagne est déjà active pour cette offre",
                                        List.of()));
            }
            cause = cause.getCause();
        }
        log.error("Persistence failure type={}", exception.getClass().getSimpleName());
        return ResponseEntity.status(500)
                .body(new ApiError(500, "Erreur interne de persistance", List.of()));
    }
}
