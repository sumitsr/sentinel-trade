# SentinelTrade Architecture

## System Overview

SentinelTrade is a high-throughput trade surveillance engine designed to detect anomalous trading patterns in real time. It is built to sustain **5,000 trades per second** with end-to-end latency under **50ms**, leveraging Java 25 virtual threads, PostgreSQL 18.3 with io_uring async I/O, and a strict hexagonal architecture that isolates domain logic from infrastructure concerns.

---

## Hexagonal Architecture Diagram

```mermaid
flowchart LR
    subgraph Adapters_In ["Adapters Layer (Driving)"]
        RC[REST Controller\nTradeController]
        JWT[JWT Filter\nJwtAuthFilter]
    end

    subgraph App ["Application Layer"]
        PORT_IN[TradeProcessingPort\n«interface»]
        UC[ProcessTradeUseCase]
        PORT_OUT[TradePersistencePort\n«interface»]
    end

    subgraph Domain ["Domain Core"]
        TRADE[Trade\n«aggregate»]
        RESULT[Result4J\nSuccess / Failure]
    end

    subgraph Adapters_Out ["Adapters Layer (Driven)"]
        AOP["@DatabaseResult\nAOP Aspect"]
        ADAPTER[TradePersistenceAdapter]
    end

    subgraph Infra ["Infrastructure"]
        PG[(PostgreSQL 18.3)]
        SC[Security Context\nSecurityContextHolder]
    end

    RC -->|TradeRequest DTO| PORT_IN
    PORT_IN --> UC
    UC --> TRADE
    UC --> PORT_OUT
    PORT_OUT --> ADAPTER
    JWT -->|JWT token| SC
    SC -.->|principal| UC
    AOP -.->|wraps| ADAPTER
    ADAPTER --> PG
    ADAPTER --> RESULT
```

---

## Request Flow

```mermaid
sequenceDiagram
    autonumber
    participant C  as Client
    participant JF as JwtAuthFilter
    participant TC as TradeController
    participant UC as ProcessTradeUseCase
    participant PP as TradePersistencePort
    participant AOP as @DatabaseResult Aspect
    participant TA as TradePersistenceAdapter
    participant PG as PostgreSQL

    C->>JF: POST /api/v1/trades (Bearer token)
    JF->>JF: validate JWT, populate SecurityContext
    JF->>TC: forward authenticated request
    TC->>UC: processTrade(TradeRequest, principal)
    UC->>UC: validate domain rules, build Trade aggregate
    UC->>PP: persist(trade)
    PP->>AOP: intercept call to TradePersistenceAdapter
    AOP->>TA: proceed with adapter method
    TA->>PG: INSERT INTO trades ...
    PG-->>TA: row saved
    TA-->>AOP: return raw result
    AOP-->>PP: wrap in Results.success(trade)
    PP-->>UC: Result<Trade, PersistenceFailure>
    UC-->>TC: Result<TradeResponse, DomainFailure>
    TC-->>C: 201 Created (TradeResponse JSON)
```

---

## AOP + Result4J Flow

```mermaid
flowchart TD
    A["Adapter method annotated\nwith @DatabaseResult"] --> B[Spring AOP proxy intercepts call]
    B --> C{Execute DB operation}
    C -->|success| D["Return Results.success(value)"]
    C -->|DataAccessException thrown| E["Aspect catches exception"]
    E --> F["Return Results.failure(PersistenceFailure)"]
    D --> G[Result propagates up call chain]
    F --> G
    G --> H{UseCase checks Result}
    H -->|isSuccess| I[Continue business logic]
    H -->|isFailure| J[Map to HTTP 422 / 500]
    style A fill:#1e40af,color:#fff
    style D fill:#166534,color:#fff
    style F fill:#991b1b,color:#fff
    style H fill:#78350f,color:#fff
```

**Key guarantee:** zero `try-catch` blocks appear in service or use-case layers. All exception handling is centralised in the `@DatabaseResult` aspect.

---

## Tech Stack

| Component        | Technology                        | Version  |
|------------------|-----------------------------------|----------|
| Language         | Java (Virtual Threads, Records)   | 25       |
| Framework        | Spring Boot                       | 4.0.5    |
| Database         | PostgreSQL (io_uring async I/O)   | 18.3     |
| Error Handling   | Result4J                          | latest   |
| Resilience       | Resilience4j (circuit breaker)    | 2.x      |
| Authentication   | JJWT                              | 0.12.x   |
| Build Tool       | Maven                             | 3.9+     |
| Observability    | Micrometer + Prometheus           | —        |
