package com.example.vanOpt.exception;


public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(String requestId) {
        super("Optimization request not found: " + requestId);
    }
}