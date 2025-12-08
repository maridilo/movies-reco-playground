package com.acme.reco.api.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> base(HttpServletRequest req, HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", req.getRequestURI());
        return body;
    }

    // 400 - Validación @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
                                                                HttpServletRequest req) {
        var status = HttpStatus.BAD_REQUEST;
        var body = base(req, status, "validation_failed");
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        body.put("errors", errors);
        return ResponseEntity.status(status).body(body);
    }

    // 409 - Duplicados / restricciones BD
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraint(DataIntegrityViolationException ex,
                                                                HttpServletRequest req) {
        var status = HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(base(req, status, "duplicate"));
    }

    // Propaga el status de ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleRSE(ResponseStatusException ex,
                                                         HttpServletRequest req) {
        var http = HttpStatus.valueOf(ex.getStatusCode().value());
        var msg = Optional.ofNullable(ex.getReason()).orElse("error");
        return ResponseEntity.status(http).body(base(req, http, msg));
    }

    // Fallback 500 (último recurso)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, HttpServletRequest req) {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(base(req, status, "internal_error"));
    }
}
