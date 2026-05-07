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
import java.util.UUID;

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

        when(solver.solve(eq(15), any())).thenReturn(solverResult);
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
        when(solver.solve(anyInt(), any())).thenReturn(List.of());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.optimize(request);

        ArgumentCaptor<OptimizationRequest> captor = ArgumentCaptor.forClass(OptimizationRequest.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getMaxVolume()).isEqualTo(20);
    }

    @Test
    void optimize_shouldReturnEmptyListWhenNothingFits() {
        OptimizeRequest request = new OptimizeRequest(1, List.of(sr("Huge", 100, 999)));
        when(solver.solve(anyInt(), any())).thenReturn(List.of());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OptimizeResponse response = service.optimize(request);

        assertThat(response.selectedShipments()).isEmpty();
        assertThat(response.totalRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.totalVolume()).isZero();
    }

    @Test
    void getById_shouldReturnExistingRequest() {
        UUID randomId = UUID.randomUUID();
        OptimizationRequest entity = buildEntity(randomId, 10, 200.0);

        // ვიყენებთ findByIdWithShipments, რადგან სერვისი ამ მეთოდს იძახებს
        when(repository.findByIdWithShipments(randomId)).thenReturn(Optional.of(entity));

        OptimizeResponse response = service.getById(randomId);

        assertThat(response.requestId()).isEqualTo(randomId.toString());
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        UUID missingId = UUID.randomUUID();
        when(repository.findByIdWithShipments(missingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(missingId))
                .isInstanceOf(RequestNotFoundException.class)
                .hasMessageContaining(missingId.toString());
    }


    @Test
    void getAll_shouldReturnEmptyListWhenNoDataExists() {
        when(repository.findAllWithShipments()).thenReturn(List.of());

        List<OptimizeResponse> result = service.getAll();

        assertThat(result).isEmpty();
    }

    @Test
    void getById_shouldMapSelectedShipmentsCorrectly() {
        UUID id = UUID.randomUUID();
        OptimizationRequest entity = buildEntity(id, 10, 500.0);
        entity.addShipment(new SelectedShipment("Item 1", 10, BigDecimal.valueOf(500.0)));

        when(repository.findByIdWithShipments(id)).thenReturn(Optional.of(entity));

        OptimizeResponse response = service.getById(id);

        assertThat(response.selectedShipments()).hasSize(1);
        assertThat(response.selectedShipments().get(0).name()).isEqualTo("Item 1");
    }

    @Test
    void getAll_shouldReturnMappedList() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        when(repository.findAllWithShipments()).thenReturn(List.of(
                buildEntity(id1, 5, 100.0),
                buildEntity(id2, 10, 200.0)
        ));

        List<OptimizeResponse> all = service.getAll();

        assertThat(all).hasSize(2);
        assertThat(all).extracting(OptimizeResponse::requestId)
                .containsExactlyInAnyOrder(id1.toString(), id2.toString());
    }

    private OptimizationRequest buildEntity(UUID id, int volume, double revenue) {
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