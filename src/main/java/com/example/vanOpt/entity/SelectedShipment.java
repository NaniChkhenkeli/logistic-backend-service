package com.example.vanOpt.entity;

import com.example.vanOpt.model.OptimizationRequest;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "selected_shipments")
public class SelectedShipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private OptimizationRequest request;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private int volume;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal revenue;

    public SelectedShipment() {}

    public SelectedShipment(String name, int volume, BigDecimal revenue) {
        this.name = name;
        this.volume = volume;
        this.revenue = revenue;
    }

    public Long getId() { return id; }
    public OptimizationRequest getRequest() { return request; }
    public void setRequest(OptimizationRequest request) { this.request = request; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getVolume() { return volume; }
    public void setVolume(int volume) { this.volume = volume; }
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}