package com.example.vanOpt;


import com.example.vanOpt.algorithm.KnapsackSolver;
import com.example.vanOpt.entity.*;
import com.example.vanOpt.model.OptimizationRequest;
import com.example.vanOpt.exception.RequestNotFoundException;
import com.example.vanOpt.repo.OptimizationRequestRepository;
import com.example.vanOpt.service.OptimizerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OptimizerServiceTest {

    @Mock
    private KnapsackSolver solver;

    @Mock
    private OptimizationRequestRepository repository;

    private OptimizerService service;

    @BeforeEach
    void setUp() {
        service = new OptimizerService(solver, repository);
    }

    private ShipmentRequest sr(String name, int vol, double rev) {
        return new ShipmentRequest(name, vol, BigDecimal.valueOf(rev));
    }

    @Test
    void optimize_shouldPersistAndReturnResult() {
        OptimizeRequest request = new OptimizeRequest(15, List.of(sr("A", 5, 120), sr("B", 10, 200)));
        List<ShipmentRequest> solverResult = List.of(sr("A", 5, 120), sr("B", 10, 200));

        when(solver.solve(15, request.availableShipments())).thenReturn(solverResult);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OptimizeResponse response = service.optimize(request);

        assertThat(response.requestId()).isNotBlank();
        assertThat(response.totalVolume()).isEqualTo(15);
        assertThat(response.totalRevenue()).isEqualByComparingTo("320.0");
        assertThat(response.selectedShipments()).hasSize(2);

        verify(repository).save(any(OptimizationRequest.class));
    }

    @Test
    void optimize_shouldPersistWithCorrectMaxVolume() {
        OptimizeRequest request = new OptimizeRequest(20, List.of(sr("X", 5, 50)));
        when(solver.solve(any(Integer.class), any())).thenReturn(List.of());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.optimize(request);

        ArgumentCaptor<OptimizationRequest> captor = ArgumentCaptor.forClass(OptimizationRequest.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getMaxVolume()).isEqualTo(20);
    }

    @Test
    void optimize_shouldReturnEmptyListWhenNothingFits() {
        OptimizeRequest request = new OptimizeRequest(1, List.of(sr("Huge", 100, 999)));
        when(solver.solve(any(Integer.class), any())).thenReturn(List.of());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OptimizeResponse response = service.optimize(request);

        assertThat(response.selectedShipments()).isEmpty();
        assertThat(response.totalRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.totalVolume()).isZero();
    }

    @Test
    void getById_shouldReturnExistingRequest() {
        OptimizationRequest entity = buildEntity("test-id", 10, 200.0);
        when(repository.findById("test-id")).thenReturn(Optional.of(entity));

        OptimizeResponse response = service.getById("test-id");

        assertThat(response.requestId()).isEqualTo("test-id");
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(repository.findById("missing")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById("missing"))
                .isInstanceOf(RequestNotFoundException.class)
                .hasMessageContaining("missing");
    }

    @Test
    void getAll_shouldReturnMappedList() {
        when(repository.findAll()).thenReturn(List.of(
                buildEntity("id1", 5, 100.0),
                buildEntity("id2", 10, 200.0)
        ));

        List<OptimizeResponse> all = service.getAll();

        assertThat(all).hasSize(2);
        assertThat(all).extracting(OptimizeResponse::requestId).containsExactly("id1", "id2");
    }

    private OptimizationRequest buildEntity(String id, int volume, double revenue) {
        OptimizationRequest e = new OptimizationRequest();
        e.setId(id);
        e.setMaxVolume(volume + 5);
        e.setTotalVolume(volume);
        e.setTotalRevenue(BigDecimal.valueOf(revenue));
        e.setCreatedAt(Instant.now());
        e.setSelectedShipments(new ArrayList<>());
        return e;
    }
}