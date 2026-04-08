# SentinelTrade Sequence Diagrams

## Trade Ingestion — Happy Path

```mermaid
sequenceDiagram
    autonumber
    participant C   as Client
    participant GW  as API Gateway
    participant JF  as JwtAuthFilter
    participant TC  as TradeController
    participant UC  as ProcessTradeUseCase
    participant VA  as TradeValidator
    participant PP  as TradePersistencePort
    participant AOP as @DatabaseResult Aspect
    participant TA  as TradePersistenceAdapter
    participant PG  as PostgreSQL

    C->>GW: POST /api/v1/trades\n{instrument, quantity, price, type}
    GW->>JF: forward with Bearer token
    JF->>JF: parse + validate JWT signature
    JF->>JF: check expiry, extract accountId claim
    JF->>TC: authenticated request + SecurityContext populated
    TC->>TC: deserialise body → TradeRequest DTO
    TC->>UC: processTrade(TradeRequest, principal)
    UC->>VA: validate(trade)
    VA-->>UC: ValidationResult OK
    UC->>UC: build Trade aggregate (UUIDv7 id, PENDING status)
    UC->>PP: persist(trade)
    PP->>AOP: proxy intercepts TradePersistenceAdapter.save()
    AOP->>TA: proceed()
    TA->>PG: INSERT INTO trades VALUES (...)
    PG-->>TA: 1 row inserted
    TA-->>AOP: Trade entity
    AOP-->>PP: Results.success(trade)
    PP-->>UC: Result<Trade, PersistenceFailure> — success
    UC-->>TC: Result<TradeResponse, DomainFailure> — success
    TC-->>C: HTTP 201 Created\n{id, status: "PENDING", ...}
```

---

## Trade Ingestion — Database Failure Path

This diagram shows how a database failure is handled **without any try-catch in business code**. The `@DatabaseResult` AOP aspect intercepts the `DataAccessException`, wraps it in a `Result.failure`, and the failure propagates up the call chain functionally.

```mermaid
sequenceDiagram
    autonumber
    participant C   as Client
    participant TC  as TradeController
    participant UC  as ProcessTradeUseCase
    participant PP  as TradePersistencePort
    participant AOP as @DatabaseResult Aspect
    participant TA  as TradePersistenceAdapter
    participant PG  as PostgreSQL

    C->>TC: POST /api/v1/trades (authenticated)
    TC->>UC: processTrade(TradeRequest, principal)
    UC->>UC: build Trade aggregate
    UC->>PP: persist(trade)
    PP->>AOP: proxy intercepts TradePersistenceAdapter.save()
    AOP->>TA: proceed()
    TA->>PG: INSERT INTO trades VALUES (...)
    PG-->>TA: connection timeout / deadlock
    TA--xAOP: DataAccessException thrown
    Note over AOP: Aspect catches DataAccessException\nNo try-catch exists in UC or TC
    AOP-->>PP: Results.failure(PersistenceFailure("DB unavailable"))
    PP-->>UC: Result<Trade, PersistenceFailure> — failure
    Note over UC: UC checks result.isFailure()\nNo exception handling needed
    UC-->>TC: Result<TradeResponse, DomainFailure> — failure mapped
    TC-->>C: HTTP 422 Unprocessable Entity\n{error: "Trade could not be persisted"}
```

**Invariant:** Neither `ProcessTradeUseCase` nor `TradeController` contains a single `try`, `catch`, or `throws` clause. All exception handling lives exclusively in the `@DatabaseResult` aspect.

---

## JWT Authentication Flow

```mermaid
sequenceDiagram
    autonumber
    participant C   as Client
    participant AC  as AuthController
    participant AS  as AuthService
    participant UR  as UserRepository
    participant JU  as JwtUtil
    participant JF  as JwtAuthFilter
    participant TC  as TradeController

    Note over C,TC: Step 1 — Obtain token
    C->>AC: POST /api/v1/auth/login\n{username, password}
    AC->>AS: authenticate(username, password)
    AS->>UR: findByUsername(username)
    UR-->>AS: UserDetails
    AS->>AS: BCrypt.verify(password, hash)
    AS->>JU: generateToken(userDetails)
    JU->>JU: sign HS256 with JWT_SECRET\nembed accountId, roles, exp
    JU-->>AS: signed JWT string
    AS-->>AC: TokenResponse
    AC-->>C: HTTP 200 OK\n{accessToken, expiresIn}

    Note over C,TC: Step 2 — Access protected resource
    C->>JF: POST /api/v1/trades\nAuthorization: Bearer <token>
    JF->>JU: validateToken(token)
    JU->>JU: verify signature
    JU->>JU: check exp claim
    JU-->>JF: Claims (accountId, roles)
    JF->>JF: build UsernamePasswordAuthenticationToken
    JF->>JF: set in SecurityContextHolder
    JF->>TC: forward request
    TC-->>C: 201 Created
```
