package com.meterflow.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {

        e.printStackTrace();

        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());

        return ResponseEntity.badRequest().body(error);
    }
}