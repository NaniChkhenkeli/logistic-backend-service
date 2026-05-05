package com.example.vanOpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OptimizerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();


    // ── POST /api/optimize

    @Test
    void postOptimize_shouldReturnOptimalSelection() throws Exception {
        String body = """
            {
              "maxVolume": 15,
              "availableShipments": [
                { "name": "Parcel A", "volume": 5,  "revenue": 120 },
                { "name": "Parcel B", "volume": 10, "revenue": 200 },
                { "name": "Parcel C", "volume": 3,  "revenue": 80  },
                { "name": "Parcel D", "volume": 8,  "revenue": 160 }
              ]
            }
            """;

        mockMvc.perform(post("/api/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").isNotEmpty())
                .andExpect(jsonPath("$.totalVolume").value(15))
                .andExpect(jsonPath("$.totalRevenue").value(320.0))
                .andExpect(jsonPath("$.selectedShipments", hasSize(2)))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void postOptimize_shouldReturn200WithEmptyListWhenNothingFits() throws Exception {
        String body = """
            {
              "maxVolume": 1,
              "availableShipments": [
                { "name": "Too Large", "volume": 100, "revenue": 9999 }
              ]
            }
            """;

        mockMvc.perform(post("/api/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selectedShipments").isEmpty())
                .andExpect(jsonPath("$.totalRevenue").value(0.0))
                .andExpect(jsonPath("$.totalVolume").value(0));
    }

    @Test
    void postOptimize_shouldReturn400WhenMaxVolumeIsMissing() throws Exception {
        String body = """
            {
              "availableShipments": [
                { "name": "A", "volume": 5, "revenue": 100 }
              ]
            }
            """;

        mockMvc.perform(post("/api/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void postOptimize_shouldReturn400WhenShipmentsEmpty() throws Exception {
        String body = """
            {
              "maxVolume": 10,
              "availableShipments": []
            }
            """;

        mockMvc.perform(post("/api/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postOptimize_shouldReturn400WhenShipmentVolumeIsZero() throws Exception {
        String body = """
            {
              "maxVolume": 10,
              "availableShipments": [
                { "name": "Zero Vol", "volume": 0, "revenue": 100 }
              ]
            }
            """;

        mockMvc.perform(post("/api/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postOptimize_shouldReturn400WhenRevenueIsNegative() throws Exception {
        String body = """
            {
              "maxVolume": 10,
              "availableShipments": [
                { "name": "Bad Revenue", "volume": 5, "revenue": -50 }
              ]
            }
            """;

        mockMvc.perform(post("/api/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postOptimize_shouldReturn400WhenShipmentNameIsBlank() throws Exception {
        String body = """
            {
              "maxVolume": 10,
              "availableShipments": [
                { "name": "  ", "volume": 5, "revenue": 100 }
              ]
            }
            """;

        mockMvc.perform(post("/api/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/optimize ──────────────────────────────────────────────────

    @Test
    void getAll_shouldReturnEmptyListInitially() throws Exception {
        mockMvc.perform(get("/api/optimize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAll_shouldReturnPersistedRequests() throws Exception {
        String body = """
            {
              "maxVolume": 10,
              "availableShipments": [
                { "name": "Box", "volume": 5, "revenue": 100 }
              ]
            }
            """;

        mockMvc.perform(post("/api/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/optimize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    // ── GET /api/optimize/{id}

    @Test
    void getById_shouldReturnPersistedRequest() throws Exception {
        String body = """
            {
              "maxVolume": 10,
              "availableShipments": [
                { "name": "Box", "volume": 5, "revenue": 100 }
              ]
            }
            """;

        MvcResult createResult = mockMvc.perform(post("/api/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        String requestId = objectMapper.readTree(responseBody).get("requestId").asText();

        mockMvc.perform(get("/api/optimize/{id}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(requestId));
    }

    @Test
    void getById_shouldReturn404ForUnknownId() throws Exception {
        mockMvc.perform(get("/api/optimize/non-existent-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}