package com.example.vanOpt.model;

import com.example.vanOpt.entity.SelectedShipment;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import java.math.BigDecimal;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "optimization_requests")
public class OptimizationRequest {

    @Id
    @JdbcTypeCode(Types.VARCHAR) // აკავშირებს String-ს ბაზის UUID-თან
    @Column(columnDefinition = "uuid")
    private String id;

    @Column(name = "max_volume", nullable = false)
    private int maxVolume;

    @Column(name = "total_volume", nullable = false)
    private int totalVolume;

    @Column(name = "total_revenue", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalRevenue;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SelectedShipment> selectedShipments = new ArrayList<>();

    public OptimizationRequest() {}

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getMaxVolume() { return maxVolume; }
    public void setMaxVolume(int maxVolume) { this.maxVolume = maxVolume; }

    public int getTotalVolume() { return totalVolume; }
    public void setTotalVolume(int totalVolume) { this.totalVolume = totalVolume; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public List<SelectedShipment> getSelectedShipments() { return selectedShipments; }
    public void setSelectedShipments(List<SelectedShipment> selectedShipments) { this.selectedShipments = selectedShipments; }

    public void addShipment(SelectedShipment shipment) {
        shipment.setRequest(this);
        this.selectedShipments.add(shipment);
    }
}