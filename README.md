# VanOpt — Van Loading Optimiser

A Spring Boot REST API that selects the revenue-maximising combination of shipments for a delivery van using a 0/1 Knapsack dynamic-programming algorithm. Every optimisation run is persisted to PostgreSQL so the operations team can audit past decisions.

---

## Table of Contents

1. Prerequisites
2. Build & Run
3. Database Setup
4. API Endpoints & cURL Examples
5. Running Tests
6. Database Schema & Index Design

---

## Prerequisites

| Tool                    | Version                                    |
| ----------------------- | ------------------------------------------ |
| Java                    | 21+                                        |
| Gradle                  | 8+ (or use the included ./gradlew wrapper) |
| Docker & Docker Compose | any recent version                         |

---

## Build & Run

### 1. Clone the repository

```bash
git clone https://github.com/your-username/vanOpt.git
cd vanOpt
```

### 2. Start the database

```bash
docker compose up -d
```

This starts a PostgreSQL 16 container on localhost:5432 with database vanopt, user vanopt, password vanopt.

### 3. Build the JAR

```bash
./gradlew clean bootJar
```

The JAR is produced at:

```text
build/libs/vanOpt-0.0.1-SNAPSHOT.jar
```

### 4. Run the application

```bash
java -jar build/libs/vanOpt-0.0.1-SNAPSHOT.jar
```

The API is now available at:

```text
http://localhost:8080
```

Flyway runs automatically on startup and applies all schema migrations under:

```text
src/main/resources/db/migration/
```

### Overriding database connection at runtime

```bash
java -jar build/libs/vanOpt-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:postgresql://myhost:5432/vanopt \
  --spring.datasource.username=myuser \
  --spring.datasource.password=mypassword
```

---

## Database Setup

Docker Compose handles everything. The full configuration is in docker-compose.yml:

```yaml
services:
  db:
    image: postgres:16
    environment:
      POSTGRES_DB: vanopt
      POSTGRES_USER: vanopt
      POSTGRES_PASSWORD: vanopt
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

volumes:
  pgdata:
```

### Start

```bash
docker compose up -d
```

### Stop (preserves data)

```bash
docker compose stop
```

### Destroy (removes all data)

```bash
docker compose down -v
```

Schema migrations are managed by Flyway and run automatically when the application starts.

Migration scripts live in:

```text
src/main/resources/db/migration/
```

---

## API Endpoints & cURL Examples

Base URL:

```text
http://localhost:8080
```

---

### POST /api/optimize — Run optimisation

Selects the revenue-maximising shipment combination that fits within maxVolume.

### Request body

| Field                        | Type    | Constraints         |
| ---------------------------- | ------- | ------------------- |
| maxVolume                    | integer | required, >= 1      |
| availableShipments           | array   | required, non-empty |
| availableShipments[].name    | string  | required, non-blank |
| availableShipments[].volume  | integer | required, >= 1      |
| availableShipments[].revenue | decimal | required, > 0       |

### cURL

```bash
curl -s -X POST http://localhost:8080/api/optimize \
  -H "Content-Type: application/json" \
  -d '{
    "maxVolume": 15,
    "availableShipments": [
      { "name": "Parcel A", "volume": 5,  "revenue": 120 },
      { "name": "Parcel B", "volume": 10, "revenue": 200 },
      { "name": "Parcel C", "volume": 3,  "revenue": 80  },
      { "name": "Parcel D", "volume": 8,  "revenue": 160 }
    ]
  }' | jq
```

### Response 200 OK

```json
{
  "requestId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "selectedShipments": [
    { "name": "Parcel A", "volume": 5,  "revenue": 120.00 },
    { "name": "Parcel B", "volume": 10, "revenue": 200.00 }
  ],
  "totalVolume": 15,
  "totalRevenue": 320.00,
  "createdAt": "2025-06-01T10:00:00Z"
}
```

### Nothing fits — Response 200 OK

```bash
curl -s -X POST http://localhost:8080/api/optimize \
  -H "Content-Type: application/json" \
  -d '{
    "maxVolume": 1,
    "availableShipments": [
      { "name": "Too Large", "volume": 100, "revenue": 9999 }
    ]
  }' | jq
```

```json
{
  "requestId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "selectedShipments": [],
  "totalVolume": 0,
  "totalRevenue": 0.00,
  "createdAt": "2025-06-01T10:01:00Z"
}
```

### Validation error — Response 400 Bad Request

```bash
curl -s -X POST http://localhost:8080/api/optimize \
  -H "Content-Type: application/json" \
  -d '{
    "availableShipments": [
      { "name": "A", "volume": 5, "revenue": 100 }
    ]
  }' | jq
```

```json
{
  "status": 400,
  "message": "Validation failed",
  "details": [
    "maxVolume: maxVolume is required"
  ],
  "timestamp": "2025-06-01T10:02:00Z"
}
```

---

### GET /api/optimize — List all past results

Returns all persisted optimisation results ordered by creation time (newest first).

### cURL

```bash
curl -s http://localhost:8080/api/optimize | jq
```

### Response 200 OK

```json
[
  {
    "requestId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "selectedShipments": [
      { "name": "Parcel A", "volume": 5,  "revenue": 120.00 },
      { "name": "Parcel B", "volume": 10, "revenue": 200.00 }
    ],
    "totalVolume": 15,
    "totalRevenue": 320.00,
    "createdAt": "2025-06-01T10:00:00Z"
  }
]
```

---

### GET /api/optimize/{id} — Get a single result by ID

### cURL

```bash
curl -s http://localhost:8080/api/optimize/a1b2c3d4-e5f6-7890-abcd-ef1234567890 | jq
```

### Response 200 OK

```json
{
  "requestId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "selectedShipments": [
    { "name": "Parcel A", "volume": 5,  "revenue": 120.00 },
    { "name": "Parcel B", "volume": 10, "revenue": 200.00 }
  ],
  "totalVolume": 15,
  "totalRevenue": 320.00,
  "createdAt": "2025-06-01T10:00:00Z"
}
```

### Not found — Response 404 Not Found

```bash
curl -s http://localhost:8080/api/optimize/00000000-0000-0000-0000-000000000000 | jq
```

```json
{
  "status": 404,
  "message": "Optimization request not found: 00000000-0000-0000-0000-000000000000",
  "details": [],
  "timestamp": "2025-06-01T10:03:00Z"
}
```

### Malformed UUID — Response 400 Bad Request

```bash
curl -s http://localhost:8080/api/optimize/not-a-uuid | jq
```

```json
{
  "status": 400,
  "message": "Invalid value for parameter 'id'. Expected type: UUID",
  "details": [],
  "timestamp": "2025-06-01T10:04:00Z"
}
```

---

## Running Tests

### Unit + integration tests (uses in-memory H2 — no Docker required)

```bash
./gradlew test
```

Test reports are written to:

```text
build/reports/tests/test/index.html
```

The integration tests run against an H2 database in PostgreSQL-compatibility mode, so no running Docker container is needed for ./gradlew test.

---

## Database Schema & Index Design

```text
optimization_requests
─────────────────────────────────────────────────────────────
id             UUID          PRIMARY KEY
max_volume     INT           NOT NULL
total_volume   INT           NOT NULL
total_revenue  DECIMAL(10,2) NOT NULL
created_at     TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP

selected_shipments
─────────────────────────────────────────────────────────────
id             BIGSERIAL     PRIMARY KEY
request_id     UUID          NOT NULL → optimization_requests(id)
name           VARCHAR(255)  NOT NULL
volume         INT           NOT NULL
revenue        DECIMAL(10,2) NOT NULL
```

### Design decisions

#### UUID primary key on optimization_requests

UUIDs are generated in the application layer before the row is inserted. This means the ID is known immediately (useful for returning it in the same response) and avoids exposing sequential integer IDs that would allow clients to enumerate all requests.

#### BIGSERIAL primary key on selected_shipments

Shipments are always accessed through their parent request, never by their own ID from the outside. A cheap auto-increment integer keeps insertion fast and the FK join efficient.

#### DECIMAL(10, 2) for revenue

Monetary values must not be stored as floating-point. DECIMAL(10,2) gives exact arithmetic up to 99 999 999.99 in any currency — sufficient for delivery fees.

#### TIMESTAMPTZ for created_at

Stores the timestamp with time-zone offset so it round-trips correctly regardless of the server's local timezone. Defaults to CURRENT_TIMESTAMP at the database level as a safety net, though the application always sets it explicitly.

#### ON DELETE CASCADE on the FK

If an optimization_requests row is deleted, its child selected_shipments rows are removed automatically — no orphaned rows.

### Index choices

| Index                                | Table                 | Column          | Reason                                                                                                                                                                         |
| ------------------------------------ | --------------------- | --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| idx_optimization_requests_created_at | optimization_requests | created_at DESC | Supports the GET /api/optimize audit list sorted newest-first without a sequential scan.                                                                                       |
| idx_selected_shipments_request_id    | selected_shipments    | request_id      | Speeds up the LEFT JOIN FETCH used by both findAllWithShipments and findByIdWithShipments. Without this index each join would require a full table scan of selected_shipments. |

The PK indexes on both tables are created automatically by PostgreSQL and cover point-lookup by ID.
