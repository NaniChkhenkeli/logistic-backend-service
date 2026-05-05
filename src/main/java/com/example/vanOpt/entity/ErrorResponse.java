package com.example.vanOpt.entity;


import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        int status,
        String message,
        List<String> details,
        Instant timestamp
) {
    public ErrorResponse(int status, String message) {
        this(status, message, List.of(), Instant.now());
    }

    public ErrorResponse(int status, String message, List<String> details) {
        this(status, message, details, Instant.now());
    }
}