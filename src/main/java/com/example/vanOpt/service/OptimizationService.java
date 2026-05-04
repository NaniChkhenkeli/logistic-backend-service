package com.example.vanOpt.service;

import com.example.vanOpt.api.OptimizationRequestDto;
import com.example.vanOpt.api.OptimizationResponseDto;
import com.example.vanOpt.api.SelectedShipmentDto;
import com.example.vanOpt.api.ShipmentInputDto;
import com.vanopt.logistics.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptimizationService {

    private final OptimizationRepository repository;
    private final KnapsackSolver solver;

    @Transactional
    public OptimizationResponseDto processOptimization(OptimizationRequestDto requestDto) {
        List<ShipmentInputDto> selectedShipments = solver.solve(
                requestDto.getMaxVolume(),
                requestDto.getAvailableShipments()
        );

        int totalVolume = selectedShipments.stream().mapToInt(ShipmentInputDto::getVolume).sum();
        int totalRevenue = selectedShipments.stream().mapToInt(ShipmentInputDto::getRevenue).sum();

        OptimizationRequest entity = new OptimizationRequest();
        entity.setMaxVolume(requestDto.getMaxVolume());
        entity.setTotalVolume(totalVolume);
        entity.setTotalRevenue(BigDecimal.valueOf(totalRevenue));

        List<SelectedShipment> shipmentEntities = selectedShipments.stream().map(s -> {
            SelectedShipment shipment = new SelectedShipment();
            shipment.setName(s.getName());
            shipment.setVolume(s.getVolume());
            shipment.setRevenue(BigDecimal.valueOf(s.getRevenue()));
            return shipment;
        }).collect(Collectors.toList());

        entity.setSelectedShipments(shipmentEntities);
        OptimizationRequest saved = repository.save(entity);

        return mapToResponse(saved);
    }

    private OptimizationResponseDto mapToResponse(OptimizationRequest entity) {
        return OptimizationResponseDto.builder()
                .requestId(entity.getId())
                .totalVolume(entity.getTotalVolume())
                .totalRevenue(entity.getTotalRevenue().intValue())
                .createdAt(entity.getCreatedAt())
                .selectedShipments(entity.getSelectedShipments().stream()
                        .map(s -> SelectedShipmentDto.builder()
                                .name(s.getName())
                                .volume(s.getVolume())
                                .revenue(s.getRevenue())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}