package com.example.vanOpt.controller;


import com.example.vanOpt.model.Shipment;
import com.example.vanOpt.service.OptimizerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final OptimizerService service;

    public ApiController(OptimizerService service) {
        this.service = service;
    }

    @PostMapping("/optimize")
    public ResponseEntity<?> solve(@RequestBody Map<String, Object> body) {
        try {
            int maxVol = (int) body.get("maxVolume");
            List<Map<String, Object>> itemsRaw = (List<Map<String, Object>>) body.get("availableShipments");
            List<Shipment> items = itemsRaw.stream()
                    .map(m -> new Shipment((String) m.get("name"), (int) m.get("volume"), (int) m.get("revenue")))
                    .toList();

            return ResponseEntity.ok(service.optimize(maxVol, items));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid input format"));
        }
    }
}