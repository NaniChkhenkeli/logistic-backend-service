package com.example.vanOpt.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OptimizationRequestDto {
    @NotNull(message = "maxVolume is required")
    @Min(value = 1, message = "maxVolume must be greater than 0")
    private Integer maxVolume;

    @NotEmpty(message = "availableShipments list cannot be empty")
    private List<ShipmentInputDto> availableShipments;
}

@Data
class ShipmentInputDto {
    @NotBlank(message = "Shipment name is required")
    private String name;

    @NotNull
    @Min(value = 1, message = "Volume must be at least 1")
    private Integer volume;

    @NotNull
    @Min(value = 0, message = "Revenue cannot be negative")
    private Integer revenue;
}

@Data
@Builder
class OptimizationResponseDto {
    private UUID requestId;
    private List<SelectedShipmentDto> selectedShipments;
    private Integer totalVolume;
    private Integer totalRevenue;
    private LocalDateTime createdAt;
}

@Data
@Builder
class SelectedShipmentDto {
    private String name;
    private Integer volume;
    private BigDecimal revenue;
}