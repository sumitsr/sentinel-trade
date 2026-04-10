# SentinelTrade

![Java 25](https://img.shields.io/badge/Java-25-blue) ![Spring Boot 4.0.5](https://img.shields.io/badge/Spring_Boot-4.0.5-brightgreen) ![PostgreSQL 18.3](https://img.shields.io/badge/PostgreSQL-18.3-336791)

**SentinelTrade** is a high-throughput trade surveillance engine capable of processing **5,000 trades per second** with end-to-end latency under **50ms**. It detects anomalous trading patterns in real time and enforces per-account authorization through layered BOLA protection.

---

## Architecture

SentinelTrade is built on **Hexagonal Architecture** (Ports & Adapters) with domain logic fully isolated from infrastructure. Detailed documentation lives in `docs/`:

| Document | Description |
|---|---|
| [docs/architecture.md](docs/architecture.md) | System overview, hexagonal diagram, request flow, AOP flow, tech stack |
| [docs/erd.md](docs/erd.md) | Entity relationship diagram, UUIDv7 strategy, indexing, BOLA protection |
| [docs/sequence.md](docs/sequence.md) | Trade ingestion happy path, DB failure path, JWT auth flow |
| [docs/aop-strategy.md](docs/aop-strategy.md) | `@DatabaseResult` AOP strategy, no-try-catch policy, code examples |

---

## Running Locally

The system has two services: a **Spring Boot backend** (port `8080`) and a **Vite + React frontend** (port `5173`). Both must be running — the frontend proxies all `/api` calls to the backend.

### Prerequisites

| Requirement | Version | Notes |
|---|---|---|
| Java (JDK) | 25+ | Required to build and run the backend |
| Maven | 3.9+ | Or use the included `./mvnw` wrapper |
| Docker | any | Easiest way to run PostgreSQL |
| Node.js | 18+ | Required for the frontend |

---

### Step 1 — Start PostgreSQL

The quickest path is Docker. This creates a `sentineldb` database with the credentials the app expects by default:

```bash
docker run -d \
  --name sentinel-pg \
  -e POSTGRES_DB=sentineldb \
  -e POSTGRES_USER=sentinel \
  -e POSTGRES_PASSWORD=sentinel \
  -p 5432:5432 \
  postgres:16
```

Verify it is up:

```bash
docker ps | grep sentinel-pg
```

> **Without Docker:** create the database manually and export `DB_USERNAME`, `DB_PASSWORD` to match your setup.

---

### Step 2 — Start the Backend

```bash
cd sentinel-trade

# Generate Maven wrapper if not present
mvn wrapper:wrapper   # one-time, skip if ./mvnw already exists

# Run
JWT_SECRET=sentinel-dev-secret-min-32-characters-xx ./mvnw spring-boot:run
```

Verify:

```bash
curl http://localhost:8080/actuator/health
# {"status":"UP"}
```

Flyway runs automatically on startup and applies `V1__create_trades_schema.sql`.

---

### Step 3 — Start the Frontend

Open a **new terminal tab**:

```bash
cd sentinel-trade/frontend
npm install        # first time only
npm run dev
```

Open **http://localhost:5173** in your browser.

---

### Step 4 — Log In

The auth endpoint issues a JWT for **any non-blank accountId and password** — no user database is required for local development:

| Field | Value |
|---|---|
| Account ID | Any string, e.g. `ACC001` or `trader1` |
| Password | Any non-blank string, e.g. `password` |

After logging in you will land on the Dashboard. Use **Submit Trade** to create trades and **Trade Lookup** to find them by UUID.

---

### Environment Variables

| Variable | Default | Description |
|---|---|---|
| `JWT_SECRET` | *(required)* | HMAC-SHA256 signing key — minimum 32 characters |
| `DB_USERNAME` | `sentinel` | PostgreSQL username |
| `DB_PASSWORD` | `sentinel` | PostgreSQL password |
| `SERVER_PORT` | `8080` | Backend HTTP port |

---

## API Reference

### Auth

```
POST /api/v1/auth/token
Content-Type: application/json

{ "accountId": "ACC001", "password": "password" }
```

Response:

```json
{ "token": "<jwt>", "expiresIn": 3600000 }
```

### Trades

All trade endpoints require `Authorization: Bearer <token>`.

```
POST /api/v1/trades
GET  /api/v1/trades/{id}
```

---

## Design Principles

### Hexagonal Architecture

Domain logic (`ProcessTradeUseCase`, `Trade` aggregate) knows nothing about Spring, JDBC, or HTTP. It communicates exclusively through port interfaces (`TradeProcessingPort`, `TradePersistencePort`). Adapters (REST controllers, JPA repositories) implement those ports and are swappable without touching the domain.

### AOP-First Error Handling

The `@DatabaseResult` annotation marks adapter methods that interact with the database. A single Spring AOP aspect intercepts every annotated call, catches `DataAccessException`, and returns a `Result.failure(...)` — meaning **no try-catch blocks exist anywhere in use-case or service layers**. See [docs/aop-strategy.md](docs/aop-strategy.md) for the full rationale and examples.

### Result4J Everywhere

Every operation that can fail returns `Result<SuccessType, FailureType>` rather than throwing exceptions or returning nulls. This makes failure paths explicit in the type signature and forces callers to handle both outcomes, eliminating silent error swallowing.

### Virtual Threads

SentinelTrade runs on Java 25 virtual threads. The Spring Boot 4 embedded server is configured to use a virtual-thread executor, allowing the application to handle tens of thousands of concurrent in-flight requests without the memory overhead of a native thread pool.
