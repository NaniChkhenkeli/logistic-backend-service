package com.example.vanOpt.service;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class KnapsackSolver {

    public List<ShipmentInputDto> solve(int maxVolume, List<ShipmentInputDto> shipments) {
        int n = shipments.size();
        int[][] dp = new int[n + 1][maxVolume + 1];

        for (int i = 1; i <= n; i++) {
            ShipmentInputDto s = shipments.get(i - 1);
            for (int v = 0; v <= maxVolume; v++) {
                if (s.getVolume() <= v) {
                    dp[i][v] = Math.max(dp[i - 1][v], s.getRevenue() + dp[i - 1][v - s.getVolume()]);
                } else {
                    dp[i][v] = dp[i - 1][v];
                }
            }
        }

        List<ShipmentInputDto> selected = new ArrayList<>();
        int res = dp[n][maxVolume];
        int w = maxVolume;
        for (int i = n; i > 0 && res > 0; i--) {
            if (res != dp[i - 1][w]) {
                ShipmentInputDto s = shipments.get(i - 1);
                selected.add(s);
                res -= s.getRevenue();
                w -= s.getVolume();
            }
        }
        return selected;
    }
}