package com.vanopt.logistics.api;

import com.example.vanOpt.api.OptimizationRequestDto;
import com.example.vanOpt.api.OptimizationResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/optimize")
@RequiredArgsConstructor
public class LogisticsController {

    private final OptimizationService optimizationService;

    @PostMapping
    public ResponseEntity<OptimizationResponseDto> calculateOptimization(
            @Valid @RequestBody OptimizationRequestDto requestDto) {

        OptimizationResponseDto response = optimizationService.processOptimization(requestDto);

        return ResponseEntity.ok(response);
    }
}