package com.affiliateLink.common.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InputValidationException.class)
    public ResponseEntity<ProblemDetail> input(InputValidationException exception) {
        return problem(400, "Données invalides", exception.getMessage(),
                List.of(new ApiError.FieldError(exception.getField(), exception.getMessage())));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> notFound(ResourceNotFoundException exception) {
        return problem(404, "Ressource introuvable", exception.getMessage(), List.of());
    }

    @ExceptionHandler(BusinessConflictException.class)
    public ResponseEntity<ProblemDetail> conflict(BusinessConflictException exception) {
        log.warn("Business request refused: {}", exception.getMessage());
        return problem(409, "Conflit métier", exception.getMessage(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> validation(MethodArgumentNotValidException exception) {
        List<ApiError.FieldError> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiError.FieldError(error.getField(), error.getDefaultMessage()))
                .toList();
        return problem(400, "Données invalides", "Vérifiez les champs du formulaire", errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> invalidJson(HttpMessageNotReadableException exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof InvalidFormatException invalid) {
                String field = "body";
                if (!invalid.getPath().isEmpty()) {
                    String property = invalid.getPath().get(invalid.getPath().size() - 1).getPropertyName();
                    if (property != null) {
                        field = property;
                    }
                }
                return problem(400, "JSON invalide", "Valeur non reconnue",
                        List.of(new ApiError.FieldError(field, "Valeur non reconnue")));
            }
        }
        return problem(400, "JSON invalide", "Le corps de la requête ne peut pas être lu", List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> invalidParameter(MethodArgumentTypeMismatchException exception) {
        return problem(400, "Paramètre invalide", "Valeur non valide",
                List.of(new ApiError.FieldError(exception.getName(), "Valeur non valide")));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> databaseConflict(DataIntegrityViolationException exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException violation) {
                String name = violation.getConstraintName();
                if ("uk_profile_email".equalsIgnoreCase(name)) {
                    return problem(409, "Conflit métier", "Cet email est déjà utilisé", List.of());
                }
                if ("uk_one_active_campaign_per_offre".equalsIgnoreCase(name)) {
                    return problem(409, "Conflit métier",
                            "Une campagne est déjà active pour cette offre", List.of());
                }
            }
        }
        log.error("Persistence failure type={}", exception.getClass().getSimpleName());
        return problem(500, "Erreur interne", "Erreur interne de persistance", List.of());
    }

    private ResponseEntity<ProblemDetail> problem(
            int status, String title, String detail, List<ApiError.FieldError> errors) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(status), detail);
        body.setTitle(title);
        body.setProperty("errors", errors);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(body);
    }
}