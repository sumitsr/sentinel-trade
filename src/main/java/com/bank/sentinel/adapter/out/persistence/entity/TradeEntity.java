package com.bank.sentinel.adapter.out.persistence.entity;

import com.bank.sentinel.domain.model.TradeStatus;
import com.bank.sentinel.domain.model.TradeType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "trades")
public class TradeEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "account_id", nullable = false, length = 50)
    private String accountId;

    @Column(name = "instrument_id", nullable = false, length = 50)
    private String instrumentId;

    @Column(name = "quantity", nullable = false, precision = 20, scale = 8)
    private BigDecimal quantity;

    @Column(name = "price", nullable = false, precision = 20, scale = 8)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private TradeType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TradeStatus status;

    @Column(name = "executed_at", nullable = false)
    private Instant executedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public TradeEntity() {}

    public TradeEntity(UUID id, String accountId, String instrumentId,
                       BigDecimal quantity, BigDecimal price,
                       TradeType type, TradeStatus status,
                       Instant executedAt, Instant createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.instrumentId = instrumentId;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.status = status;
        this.executedAt = executedAt;
        this.createdAt = createdAt;
    }

    @PrePersist
    void prePersist() {
        if (id == null) id = generateUUIDv7();
        if (createdAt == null) createdAt = Instant.now();
    }

    private static UUID generateUUIDv7() {
        long tsMs = System.currentTimeMillis();
        long rand = ThreadLocalRandom.current().nextLong();
        long msb = (tsMs << 16) | (7L << 12) | ((rand >>> 52) & 0x0FFFL);
        long lsb = 0x8000000000000000L | (rand & 0x3FFFFFFFFFFFFFFFL);
        return new UUID(msb, lsb);
    }

    public UUID getId() { return id; }
    public String getAccountId() { return accountId; }
    public String getInstrumentId() { return instrumentId; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public TradeType getType() { return type; }
    public TradeStatus getStatus() { return status; }
    public Instant getExecutedAt() { return executedAt; }
    public Instant getCreatedAt() { return createdAt; }
}
