package com.example.vanOpt.entity;

import java.math.BigDecimal;

public record ShipmentResponse(
        String name,
        int volume,
        BigDecimal revenue
) {}