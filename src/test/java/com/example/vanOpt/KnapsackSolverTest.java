package com.example.vanOpt;


import com.example.vanOpt.algorithm.KnapsackSolver;
import com.example.vanOpt.entity.ShipmentRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KnapsackSolverTest {

    private final KnapsackSolver solver = new KnapsackSolver();

    private ShipmentRequest shipment(String name, int vol, double rev) {
        return new ShipmentRequest(name, vol, BigDecimal.valueOf(rev));
    }



    @Test
    void shouldSelectOptimalShipmentsFromExample() {
        List<ShipmentRequest> shipments = List.of(
                shipment("Parcel A", 5, 120),
                shipment("Parcel B", 10, 200),
                shipment("Parcel C", 3, 80),
                shipment("Parcel D", 8, 160)
        );

        List<ShipmentRequest> result = solver.solve(15, shipments);

        // Optimal: A (5, 120) + B (10, 200) = volume 15, revenue 320
        List<String> names = result.stream().map(ShipmentRequest::name).toList();
        assertThat(names).containsExactlyInAnyOrder("Parcel A", "Parcel B");

        int totalVolume = result.stream().mapToInt(ShipmentRequest::volume).sum();
        double totalRevenue = result.stream()
                .mapToDouble(s -> s.revenue().doubleValue()).sum();

        assertThat(totalVolume).isEqualTo(15);
        assertThat(totalRevenue).isEqualTo(320.0);
    }

    @Test
    void shouldReturnEmptyWhenNoShipmentFits() {
        List<ShipmentRequest> shipments = List.of(
                shipment("Giant Parcel", 100, 999)
        );

        List<ShipmentRequest> result = solver.solve(10, shipments);

        assertThat(result).isEmpty();
    }


    @Test
    void shouldHandleVerySmallRevenueDifferences() {
        List<ShipmentRequest> shipments = List.of(
                shipment("A", 5, 100.001),
                shipment("B", 5, 100.009)
        );

        List<ShipmentRequest> result = solver.solve(5, shipments);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("B");
    }

    @Test
    void shouldReturnEmptyWhenMaxVolumeIsZero() {
        List<ShipmentRequest> shipments = List.of(shipment("A", 1, 100));
        List<ShipmentRequest> result = solver.solve(0, shipments);
        assertThat(result).isEmpty();
    }


    @Test
    void shouldHandleSingleShipmentThatFitsExactly() {
        List<ShipmentRequest> shipments = List.of(
                shipment("Exact Fit", 10, 500)
        );

        List<ShipmentRequest> result = solver.solve(10, shipments);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Exact Fit");
    }

    @Test
    void shouldNotExceedMaxVolume() {
        List<ShipmentRequest> shipments = List.of(
                shipment("A", 6, 300),
                shipment("B", 6, 300),
                shipment("C", 5, 250)
        );

        List<ShipmentRequest> result = solver.solve(10, shipments);

        int totalVolume = result.stream().mapToInt(ShipmentRequest::volume).sum();
        assertThat(totalVolume).isLessThanOrEqualTo(10);
    }

    @Test
    void shouldSelectHigherRevenueNotHigherVolume() {
        List<ShipmentRequest> shipments = List.of(
                shipment("Cheap Large", 10, 100),
                shipment("Expensive Small", 5, 300)
        );

        List<ShipmentRequest> result = solver.solve(10, shipments);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Expensive Small");
    }

    @Test
    void shouldHandleEmptyShipmentList() {
        List<ShipmentRequest> result = solver.solve(100, List.of());
        assertThat(result).isEmpty();
    }

    @Test
    void shouldWorkWithDecimalRevenues() {
        List<ShipmentRequest> shipments = List.of(
                shipment("A", 5, 99.99),
                shipment("B", 5, 100.01)
        );

        List<ShipmentRequest> result = solver.solve(5, shipments);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("B");
    }
}