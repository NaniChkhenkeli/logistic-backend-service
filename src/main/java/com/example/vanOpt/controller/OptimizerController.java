package com.example.vanOpt.controller;

import com.example.vanOpt.entity.OptimizeRequest;
import com.example.vanOpt.entity.OptimizeResponse;
import com.example.vanOpt.service.OptimizerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/optimize")
public class OptimizerController {

    private final OptimizerService service;

    public OptimizerController(OptimizerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OptimizeResponse> optimize(@Valid @RequestBody OptimizeRequest request) {
        return ResponseEntity.ok(service.optimize(request));
    }

    @GetMapping
    public ResponseEntity<List<OptimizeResponse>> listAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptimizeResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }
}