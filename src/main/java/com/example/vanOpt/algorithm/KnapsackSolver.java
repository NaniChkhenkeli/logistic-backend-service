package com.example.vanOpt.algorithm;



import com.example.vanOpt.entity.ShipmentRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 0/1 Knapsack solver using dynamic programming.
 *
 * <p>Works with integer volumes and scales decimal revenues to long integers
 * (2 decimal places) to avoid floating-point errors during DP.
 *
 * Time  complexity: O(n * maxVolume)
 * Space complexity: O(n * maxVolume)
 */
@Component
public class KnapsackSolver {

    private static final int REVENUE_SCALE = 100; // cents precision

    /**
     * Returns the subset of {@code shipments} that maximises total revenue
     * without exceeding {@code maxVolume}.
     */
    public List<ShipmentRequest> solve(int maxVolume, List<ShipmentRequest> shipments) {
        int n = shipments.size();

        // Scale revenues to longs to avoid BigDecimal comparisons inside the DP loop
        long[] rev = new long[n];
        for (int i = 0; i < n; i++) {
            rev[i] = shipments.get(i).revenue()
                    .multiply(BigDecimal.valueOf(REVENUE_SCALE))
                    .longValue();
        }

        // dp[i][v] = maximum scaled revenue using first i items with capacity v
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

        // Backtrack to find which items were selected
        List<ShipmentRequest> selected = new ArrayList<>();
        int remainingVolume = maxVolume;
        for (int i = n; i > 0; i--) {
            if (dp[i][remainingVolume] != dp[i - 1][remainingVolume]) {
                ShipmentRequest s = shipments.get(i - 1);
                selected.add(s);
                remainingVolume -= s.volume();
            }
        }

        return selected;
    }
}