# vanOpt - Shipment Optimizer

**vanOpt** is a high-performance RESTful API designed to solve the "Van Loading Problem." It uses a **0/1 Knapsack Algorithm** to determine the most profitable combination of shipments that can fit into a delivery vehicle with a limited volume capacity.

## Features

* **Optimal Loading Logic:** Implements dynamic programming to maximize revenue based on volume constraints.
* **Persistent Tracking:** Every optimization request and its results are stored in a relational database for future auditing.
* **High Precision:** Handles monetary values using `BigDecimal` with a scaled integer approach for internal calculations to prevent floating-point errors.
* **Robust Validation:** Strict input validation for shipment names, positive volumes, and non-zero revenues.
* **Comprehensive Error Handling:** Standardized JSON error responses for validation, malformed requests, and missing resources.

## Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3.x (Web, Data JPA, Validation)
* **Database:** H2 (In-memory for development/testing)
* **Testing:** JUnit 5, Mockito, AssertJ, MockMvc

## API Documentation

### 1. Create Optimization Request
Calculate the best shipment combination and persist it to the database.

**URL:** `POST /api/optimize`  
**Payload:**
```json
{
  "maxVolume": 15,
  "availableShipments": [
    { "name": "Parcel A", "volume": 5, "revenue": 120.00 },
    { "name": "Parcel B", "volume": 10, "revenue": 200.50 },
    { "name": "Parcel C", "volume": 3, "revenue": 80.00 }
  ]
}
