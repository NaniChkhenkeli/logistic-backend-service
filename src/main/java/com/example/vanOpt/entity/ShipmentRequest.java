package com.example.vanOpt.entity;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ShipmentRequest(

        @NotBlank(message = "Shipment name must not be blank")
        String name,

        @NotNull(message = "volume is required")
        @Min(value = 1, message = "volume must be at least 1")
        Integer volume,

        @NotNull(message = "revenue is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "revenue must be positive")
        BigDecimal revenue
) {}