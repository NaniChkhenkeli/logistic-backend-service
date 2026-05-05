package com.example.vanOpt.entity;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OptimizeRequest(

        @NotNull(message = "maxVolume is required")
        @Min(value = 1, message = "maxVolume must be at least 1")
        Integer maxVolume,

        @NotNull(message = "availableShipments is required")
        @NotEmpty(message = "availableShipments must not be empty")
        @Valid
        List<ShipmentRequest> availableShipments
) {}