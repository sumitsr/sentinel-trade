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

## Getting Started

### Prerequisites

| Requirement | Version |
|---|---|
| Java (JDK) | 25+ |
| PostgreSQL | 18.3+ |
| Maven | 3.9+ |

### Steps

1. **Clone the repository**

   ```bash
   git clone https://github.com/your-org/sentinel-trade.git
   cd sentinel-trade
   ```

2. **Set environment variables**

   ```bash
   export JWT_SECRET="your-256-bit-secret"
   export DB_URL="jdbc:postgresql://localhost:5432/sentinel_trade"
   export DB_USER="sentinel"
   export DB_PASSWORD="your-db-password"
   ```

3. **Create the database**

   ```bash
   psql -U postgres -c "CREATE DATABASE sentinel_trade;"
   psql -U postgres -c "CREATE USER sentinel WITH PASSWORD 'your-db-password';"
   psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE sentinel_trade TO sentinel;"
   ```

4. **Build and run**

   ```bash
   mvn clean package -DskipTests
   java -jar target/sentinel-trade-*.jar
   ```

5. **Verify**

   ```bash
   curl http://localhost:8080/actuator/health
   # {"status":"UP"}
   ```

### Environment Variables

| Variable | Required | Description |
|---|---|---|
| `JWT_SECRET` | Yes | HMAC-SHA256 signing key — minimum 256 bits (32 bytes) |
| `DB_URL` | Yes | JDBC connection URL to PostgreSQL 18.3 instance |
| `DB_USER` | Yes | PostgreSQL username with read/write access to the schema |
| `DB_PASSWORD` | Yes | Password for the PostgreSQL user |
| `SERVER_PORT` | No | HTTP port (default: `8080`) |
| `LOG_LEVEL` | No | Root log level (default: `INFO`) |

---

## Design Principles

### Hexagonal Architecture

Domain logic (`ProcessTradeUseCase`, `Trade` aggregate) knows nothing about Spring, JDBC, or HTTP. It communicates exclusively through port interfaces (`TradeProcessingPort`, `TradePersistencePort`). Adapters (REST controllers, JPA repositories) implement those ports and are swappable without touching the domain.

### AOP-First Error Handling

The `@DatabaseResult` annotation marks adapter methods that interact with the database. A single Spring AOP aspect intercepts every annotated call, catches `DataAccessException`, and returns a `Result.failure(...)` — meaning **no try-catch blocks exist anywhere in use-case or service layers**. See [docs/aop-strategy.md](docs/aop-strategy.md) for the full rationale and examples.

### Result4J Everywhere

Every operation that can fail returns `Result<SuccessType, FailureType>` rather than throwing exceptions or returning nulls. This makes failure paths explicit in the type signature and forces callers to handle both outcomes, eliminating silent error swallowing.

### Virtual Threads

SentinelTrade runs on Java 25 virtual threads (`--enable-preview` not required in JDK 25). The Spring Boot 4 embedded server is configured to use a virtual-thread executor, allowing the application to handle tens of thousands of concurrent in-flight requests without the memory overhead of a native thread pool.
