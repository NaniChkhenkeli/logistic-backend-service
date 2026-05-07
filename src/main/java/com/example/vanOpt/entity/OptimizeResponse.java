package com.example.vanOpt.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OptimizeResponse(
        String requestId,
        List<ShipmentResponse> selectedShipments,
        int totalVolume,
        BigDecimal totalRevenue,
        Instant createdAt
) {}