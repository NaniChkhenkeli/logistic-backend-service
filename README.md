# vanOpt - Shipment Optimizer

vanOpt is a high-performance RESTful API designed to solve the Van Loading Problem.  
It uses a 0/1 Knapsack Algorithm to determine the most profitable combination of shipments that can fit into a delivery vehicle with limited volume capacity.


# Features

- **Optimal Loading Logic**  
  Implements dynamic programming to maximize revenue based on volume constraints.

- **Persistent Tracking**  
  Every optimization request and its results are stored in a relational database for future auditing.

- **High Precision**  
  Handles monetary values using `BigDecimal` with a scaled integer approach for internal calculations to prevent floating-point errors.

- **Robust Validation**  
  Strict input validation for shipment names, positive volumes, and non-zero revenues.

- **Comprehensive Error Handling**  
  Standardized JSON error responses for validation failures, malformed requests, and missing resources.



# Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot 3.x (Web, Data JPA, Validation)
- **Database:** H2 (In-memory for development/testing)
- **Testing:** JUnit 5, Mockito, AssertJ, MockMvc



# API Documentation

## 1. Create Optimization Request

Calculate the best shipment combination and persist it to the database.

### URL

```http
POST /api/optimize
```

### Request Payload

```json
{
  "maxVolume": 15,
  "availableShipments": [
    {
      "name": "Parcel A",
      "volume": 5,
      "revenue": 120.00
    },
    {
      "name": "Parcel B",
      "volume": 10,
      "revenue": 200.50
    },
    {
      "name": "Parcel C",
      "volume": 3,
      "revenue": 80.00
    }
  ]
}
```



## 2. List History

Retrieve all past optimization results, ordered by the most recent.

### URL

```http
GET /api/optimize
```



## 3. Get Specific Result

Retrieve a specific optimization result by its UUID.

### URL

```http
GET /api/optimize/{id}
```



# How It Works

The core optimization is handled by the `KnapsackSolver`.

To maintain both speed and precision, the solver:

- Converts `BigDecimal` revenues into scaled `long` values
- Uses a **Bottom-Up Dynamic Programming** approach
- Performs traceback analysis on the DP table to identify selected shipments

The algorithm follows the recurrence relation:

```math
dp[i][v] = \max(dp[i-1][v], revenue_i + dp[i-1][v - volume_i])
```

Where:

- `i` represents the current shipment
- `v` represents the remaining volume capacity
- `dp[i][v]` stores the maximum achievable revenue



# Getting Started

## Prerequisites

- JDK 21
- Gradle (or the provided Wrapper)



## Installation & Run

### Clone the Repository

```bash
git clone https://github.com/your-username/vanOpt.git
cd vanOpt
```

### Run the Application

```bash
./gradlew bootRun
```

The server will start at:

```text
http://localhost:8080
```



# Running Tests

Execute the full suite of unit and integration tests, including:

- Knapsack optimization logic
- Controller endpoint testing
- Validation and exception handling

```bash
./gradlew test
```



# Error Handling

The API utilizes a `GlobalExceptionHandler` to provide standardized and user-friendly error responses.

| Status Code | Description |
|---|---|
| `400 Bad Request` | Validation failure, malformed JSON, or invalid request data |
| `404 Not Found` | Requested optimization UUID does not exist |
| `500 Internal Server Error` | Unexpected server-side exception |


# Example Use Cases

- Delivery route optimization
- Cargo loading systems
- Logistics revenue maximization
- Warehouse shipment planning
- Fleet capacity management


# Future Improvements

- PostgreSQL/MySQL support
- Swagger/OpenAPI documentation
- Authentication & authorization
- Docker containerization
- Shipment prioritization rules
- Multi-vehicle optimization
