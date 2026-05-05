package com.example.vanOpt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class OptimizationRequest {
    @Id
    private String requestId;
    private int totalVolume;
    private int totalRevenue;
    private LocalDateTime createdAt;

    @ElementCollection
    private List<String> selectedItems;

    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public int getTotalVolume() { return totalVolume; }
    public void setTotalVolume(int totalVolume) { this.totalVolume = totalVolume; }
    public int getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(int totalRevenue) { this.totalRevenue = totalRevenue; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<String> getSelectedItems() { return selectedItems; }
    public void setSelectedItems(List<String> selectedItems) { this.selectedItems = selectedItems; }
}