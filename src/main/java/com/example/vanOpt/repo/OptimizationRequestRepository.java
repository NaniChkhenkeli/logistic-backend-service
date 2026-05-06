package com.example.vanOpt.repo;

import com.example.vanOpt.model.OptimizationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OptimizationRequestRepository extends JpaRepository<OptimizationRequest, UUID> {

    @Query("SELECT r FROM OptimizationRequest r LEFT JOIN FETCH r.selectedShipments")
    List<OptimizationRequest> findAllWithShipments();

    @Query("SELECT r FROM OptimizationRequest r LEFT JOIN FETCH r.selectedShipments WHERE r.id = :id")
    Optional<OptimizationRequest> findByIdWithShipments(@Param("id") UUID id);
}