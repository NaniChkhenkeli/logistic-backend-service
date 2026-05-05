package com.example.vanOpt.repo;


import com.example.vanOpt.model.OptimizationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptimizationRequestRepository extends JpaRepository<OptimizationRequest, String> {}