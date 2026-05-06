package com.example.vanOpt.service;

import com.example.vanOpt.algorithm.KnapsackSolver;
import com.example.vanOpt.entity.*;
import com.example.vanOpt.exception.RequestNotFoundException;
import com.example.vanOpt.model.OptimizationRequest;
import com.example.vanOpt.entity.SelectedShipment;
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
        // ალგორითმის გამოძახება საუკეთესო კომბინაციის საპოვნელად
        List<ShipmentRequest> selected = solver.solve(request.maxVolume(), request.availableShipments());

        int totalVolume = selected.stream().mapToInt(ShipmentRequest::volume).sum();
        BigDecimal totalRevenue = selected.stream()
                .map(ShipmentRequest::revenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Entity-ს მომზადება შესანახად
        OptimizationRequest entity = new OptimizationRequest();
        entity.setId(UUID.randomUUID());
        entity.setMaxVolume(request.maxVolume());
        entity.setTotalVolume(totalVolume);
        entity.setTotalRevenue(totalRevenue);
        entity.setCreatedAt(Instant.now());

        // შერჩეული ტვირთების დაკავშირება ძირითად მოთხოვნასთან
        for (ShipmentRequest s : selected) {
            entity.addShipment(new SelectedShipment(s.name(), s.volume(), s.revenue()));
        }

        // მონაცემთა ბაზაში შენახვა
        OptimizationRequest savedEntity = repository.save(entity);
        return toResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public OptimizeResponse getById(UUID id) {
        // id უკვე არის UUID ტიპის, რაც გამორიცხავს SQL შეცდომას
        return repository.findByIdWithShipments(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RequestNotFoundException(id.toString()));
    }

    @Transactional(readOnly = true)
    public List<OptimizeResponse> getAll() {
        return repository.findAllWithShipments().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * გარდაქმნის მონაცემთა ბაზის ობიექტს (Entity) საპასუხო DTO-დ
     */
    private OptimizeResponse toResponse(OptimizationRequest entity) {
        List<ShipmentResponse> shipments = entity.getSelectedShipments().stream()
                .map(s -> new ShipmentResponse(s.getName(), s.getVolume(), s.getRevenue()))
                .toList();

        return new OptimizeResponse(
                entity.getId().toString(),
                shipments,
                entity.getTotalVolume(),
                entity.getTotalRevenue(),
                entity.getCreatedAt()
        );
    }
}