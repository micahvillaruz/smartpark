# SmartPark

A REST API for managing parking lots and vehicles, built with Spring Boot. Supports JWT-based authentication, parking check-in/check-out with automatic cost calculation, and a scheduled job that auto-removes vehicles parked longer than 15 minutes.

## Tech Stack

- Java 26
- Spring Boot 4.0.6 (Web MVC, Data JPA, Security, Validation)
- H2 in-memory database
- JJWT 0.12.5 for token signing/verification
- JUnit 5 + Mockito for testing (via Spring Boot's modular test starters)
- Maven build

## Prerequisites

- JDK 17 or higher
- Maven 3.6+ (or use the included `./mvnw` wrapper — no install needed)

## Build & Run

Clone the repo, then from the project root:

```bash
# Build
./mvnw clean install

# Run
./mvnw spring-boot:run
```

The app starts on **http://localhost:8080**.

H2 console (for inspecting the in-memory DB) is available at **http://localhost:8080/h2-console**:
- JDBC URL: `jdbc:h2:mem:smartparkdb`
- Username: `sa`
- Password: *(empty)*

## Run Tests

```bash
./mvnw test
```

## Authentication

All endpoints except `/api/auth/login` require a JWT bearer token.

**Static credentials (configurable via env vars):**
- Username: `admin`
- Password: `admin123`

To override: set `APP_AUTH_USERNAME`, `APP_AUTH_PASSWORD`, and `JWT_SECRET` environment variables. Defaults are baked in for ease of evaluation; in production, secrets would be injected via environment variables or a secrets manager and never committed.

Get a token:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Then include it on subsequent requests:

```bash
curl http://localhost:8080/api/lots -H "Authorization: Bearer <token>"
```

Tokens expire after 1 hour by default.

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/login` | Login, returns JWT |
| POST | `/api/lots` | Register a parking lot |
| GET | `/api/lots` | List all parking lots |
| GET | `/api/lots/{lotId}` | Get a parking lot (includes occupancy/availability) |
| POST | `/api/vehicles` | Register a vehicle |
| GET | `/api/vehicles` | List all vehicles |
| GET | `/api/vehicles/{licensePlate}` | Get a vehicle |
| POST | `/api/parking/check-in` | Check a vehicle into a lot |
| POST | `/api/parking/check-out` | Check a vehicle out, returns cost |
| GET | `/api/parking/lots/{lotId}/vehicles` | List vehicles currently parked in a lot |

## Postman Collection

A Postman collection with all endpoints is included at `postman/SmartPark.postman_collection.json`.

To use it:
1. Import the file into Postman (`Import` → select the file)
2. Run the **Login** request first — it auto-populates the `token` collection variable
3. Run any other request; auth is inherited from the collection

## Preloaded Data

On startup, `data.sql` seeds:
- 4 parking lots (LOT-001 through LOT-004)
- 5 vehicles (ABC-1234, XYZ-9876, MOT-0001, TRK-5555, CAR-2222)

This lets you exercise the check-in / check-out flow immediately without registering anything new.

## Business Rules

- **Lot capacity is enforced.** Check-in fails with HTTP 409 if the lot is full.
- **A vehicle can only be parked in one lot at a time.** Check-in fails with HTTP 409 if the vehicle has an active session.
- **Occupancy updates automatically** on check-in (increment) and check-out (decrement).
- **Cost is calculated on checkout** based on `costPerMinute × minutes parked`. Partial minutes are rounded up — a 15-second stay still costs 1 minute. The final cost is rounded to 2 decimal places.
- **Auto-checkout for vehicles parked over 15 minutes.** A scheduled job runs every 30 seconds, finds active sessions older than the threshold, and force-checks them out — calculating cost, freeing the space, and marking the session with `autoCheckedOut: true`. Threshold is configurable via `app.parking.auto-checkout-minutes`.

## Design Notes & Assumptions

- **`ParkingSession` is a separate entity** tracking each check-in/check-out as a record. It's not in the spec explicitly, but it's needed to track timestamps, cost, and the active/inactive state cleanly.
- **`BigDecimal` is used for all monetary values** (`costPerMinute`, `cost`) to avoid floating-point precision issues. Rates use scale 4 (per-minute pricing may be sub-cent); final costs are rounded to scale 2.
- **The "view occupancy and availability" requirement** is satisfied by `GET /api/lots/{lotId}` — the response includes `capacity`, `occupiedSpaces`, and `availableSpaces`. A separate endpoint would just return a subset of the same data.
- **Auto-checkout interpreted literally:** at 15 minutes, the vehicle is force-removed and the customer is charged for the time used. If the spec intended a different behavior (e.g., a free grace period), this can be adjusted by changing `ParkingService.autoCheckOut()`.
- **HTTP 409 (Conflict) is used for state-conflict errors** (lot full, already parked) to distinguish them from input-validation errors (HTTP 400).
- **No controller tests, only service tests.** Controllers in this app are thin pass-throughs; the business rules under test live in the service layer. Service-level testing covers the spec's requirements without testing Spring's plumbing.

## Project Structure

```
src/main/java/com/smartpark/
├── SmartParkApplication.java       # Main entry point with @EnableScheduling
├── config/                          # (reserved for future use)
├── controller/                      # REST endpoints
├── dto/                             # Request and response objects
├── entity/                          # JPA entities
├── exception/                       # Global exception handler
├── repository/                      # Spring Data JPA repositories
├── scheduler/                       # Auto-checkout scheduled job
├── security/                        # JWT service, auth filter, security config
└── service/                         # Business logic

src/main/resources/
├── application.properties           # Config
└── data.sql                         # Preloaded reference data

src/test/java/com/smartpark/
└── service/                         # Service layer unit tests

postman/
└── SmartPark.postman_collection.json
```

## Configuration Reference

Defaults in `application.properties`. Override via environment variables.

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8080 | HTTP port |
| `app.jwt.secret` | (base64-encoded demo key) | HMAC signing key for JWTs |
| `app.jwt.expiration-ms` | 3600000 (1 hour) | Token lifetime |
| `app.auth.username` | admin | Login username |
| `app.auth.password` | admin123 | Login password |
| `app.parking.auto-checkout-minutes` | 15 | Threshold for auto-checkout |
| `app.parking.scheduler-rate-ms` | 30000 (30s) | Scheduler frequency |