package com.example.vanOpt.service;

import com.example.vanOpt.model.OptimizationRequest;
import com.example.vanOpt.model.Shipment;
import com.example.vanOpt.repo.RequestRepo;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OptimizerService {
    private final RequestRepo repo;

    public OptimizerService(RequestRepo repo) {
        this.repo = repo;
    }

    public Map<String, Object> optimize(int maxVol, List<Shipment> items) {
        int n = items.size();
        int[][] dp = new int[n + 1][maxVol + 1];

        for (int i = 1; i <= n; i++) {
            for (int v = 0; v <= maxVol; v++) {
                if (items.get(i - 1).volume() <= v) {
                    dp[i][v] = Math.max(dp[i - 1][v], dp[i - 1][v - items.get(i - 1).volume()] + items.get(i - 1).revenue());
                } else {
                    dp[i][v] = dp[i - 1][v];
                }
            }
        }

        List<Shipment> selected = new ArrayList<>();
        int res = dp[n][maxVol];
        int v = maxVol;
        for (int i = n; i > 0 && res > 0; i--) {
            if (res != dp[i - 1][v]) {
                Shipment s = items.get(i - 1);
                selected.add(s);
                res -= s.revenue();
                v -= s.volume();
            }
        }

        OptimizationRequest entity = new OptimizationRequest();
        entity.setRequestId(UUID.randomUUID().toString());
        entity.setTotalVolume(maxVol - v);
        entity.setTotalRevenue(dp[n][maxVol]);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setSelectedItems(selected.stream().map(Shipment::name).toList());
        repo.save(entity);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("requestId", entity.getRequestId());
        response.put("selectedShipments", selected);
        response.put("totalVolume", entity.getTotalVolume());
        response.put("totalRevenue", entity.getTotalRevenue());
        response.put("createdAt", entity.getCreatedAt());
        return response;
    }
}