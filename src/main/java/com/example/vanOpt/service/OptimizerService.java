package com.example.vanOpt.service;


import com.example.vanOpt.algorithm.KnapsackSolver;
import com.example.vanOpt.entity.*;
import com.example.vanOpt.entity.SelectedShipment;
import com.example.vanOpt.exception.RequestNotFoundException;
import com.example.vanOpt.model.OptimizationRequest;
import com.example.vanOpt.repo.OptimizationRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OptimizerService {

    private final KnapsackSolver solver;
    private final OptimizationRequestRepository repository;

    public OptimizerService(KnapsackSolver solver, OptimizationRequestRepository repository) {
        this.solver = solver;
        this.repository = repository;
    }

    @Transactional
    public OptimizeResponse optimize(OptimizeRequest request) {
        List<ShipmentRequest> selected = solver.solve(request.maxVolume(), request.availableShipments());

        int totalVolume = selected.stream().mapToInt(ShipmentRequest::volume).sum();
        BigDecimal totalRevenue = selected.stream()
                .map(ShipmentRequest::revenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OptimizationRequest entity = new OptimizationRequest();
        entity.setId(UUID.randomUUID().toString());
        entity.setMaxVolume(request.maxVolume());
        entity.setTotalVolume(totalVolume);
        entity.setTotalRevenue(totalRevenue);
        entity.setCreatedAt(Instant.now());

        for (ShipmentRequest s : selected) {
            entity.addShipment(new SelectedShipment(s.name(), s.volume(), s.revenue()));
        }

        repository.save(entity);
        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public OptimizeResponse getById(String requestId) {
        return repository.findByIdWithShipments(requestId)
                .map(this::toResponse)
                .orElseThrow(() -> new RequestNotFoundException(requestId));
    }

    @Transactional(readOnly = true)
    public List<OptimizeResponse> getAll() {
        return repository.findAllWithShipments().stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Mapping

    private OptimizeResponse toResponse(OptimizationRequest entity) {
        List<ShipmentResponse> shipments = entity.getSelectedShipments().stream()
                .map(s -> new ShipmentResponse(s.getName(), s.getVolume(), s.getRevenue()))
                .toList();

        return new OptimizeResponse(
                entity.getId(),
                shipments,
                entity.getTotalVolume(),
                entity.getTotalRevenue(),
                entity.getCreatedAt()
        );
    }
}