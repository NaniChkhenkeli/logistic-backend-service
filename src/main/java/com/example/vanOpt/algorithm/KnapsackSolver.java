package com.example.vanOpt.algorithm;

import com.example.vanOpt.entity.ShipmentRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class KnapsackSolver {

    private static final int REVENUE_SCALE = 1000;

    public List<ShipmentRequest> solve(int maxVolume, List<ShipmentRequest> shipments) {
        int n = shipments.size();
        if (n == 0 || maxVolume <= 0) {
            return List.of();
        }

        long[] rev = new long[n];
        for (int i = 0; i < n; i++) {
            rev[i] = shipments.get(i).revenue()
                    .multiply(BigDecimal.valueOf(REVENUE_SCALE))
                    .longValue();
        }

        long[][] dp = new long[n + 1][maxVolume + 1];

        for (int i = 1; i <= n; i++) {
            int vol = shipments.get(i - 1).volume();
            for (int v = 0; v <= maxVolume; v++) {
                dp[i][v] = dp[i - 1][v];
                if (vol <= v) {
                    long withItem = dp[i - 1][v - vol] + rev[i - 1];
                    if (withItem > dp[i][v]) {
                        dp[i][v] = withItem;
                    }
                }
            }
        }

        // traceback to find which items were selected
        List<ShipmentRequest> selected = new ArrayList<>();
        int remaining = maxVolume;
        for (int i = n; i > 0; i--) {
            if (dp[i][remaining] != dp[i - 1][remaining]) {
                ShipmentRequest s = shipments.get(i - 1);
                selected.add(s);
                remaining -= s.volume();
            }
        }

        return selected;
    }
}