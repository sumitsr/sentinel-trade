package com.bank.sentinel;

import com.bank.sentinel.domain.model.Trade;
import com.bank.sentinel.domain.model.TradeStatus;
import com.bank.sentinel.domain.model.TradeType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TestFixtures {

    public static Trade validTrade() {
        return new Trade(
                UUID.randomUUID(),
                "ACC-001",
                "AAPL",
                new BigDecimal("100"),
                new BigDecimal("150.00"),
                TradeType.BUY,
                TradeStatus.PENDING,
                Instant.now()
        );
    }

    public static Trade tradeWithNullAccountId() {
        return new Trade(UUID.randomUUID(), null, "AAPL",
                new BigDecimal("100"), new BigDecimal("150.00"),
                TradeType.BUY, TradeStatus.PENDING, Instant.now());
    }

    public static Trade tradeWithNullInstrumentId() {
        return new Trade(UUID.randomUUID(), "ACC-001", null,
                new BigDecimal("100"), new BigDecimal("150.00"),
                TradeType.BUY, TradeStatus.PENDING, Instant.now());
    }

    public static Trade tradeWithNullQuantity() {
        return new Trade(UUID.randomUUID(), "ACC-001", "AAPL",
                null, new BigDecimal("150.00"),
                TradeType.BUY, TradeStatus.PENDING, Instant.now());
    }

    public static Trade tradeWithNullPrice() {
        return new Trade(UUID.randomUUID(), "ACC-001", "AAPL",
                new BigDecimal("100"), null,
                TradeType.BUY, TradeStatus.PENDING, Instant.now());
    }

    public static Trade tradeWithNullType() {
        return new Trade(UUID.randomUUID(), "ACC-001", "AAPL",
                new BigDecimal("100"), new BigDecimal("150.00"),
                null, TradeStatus.PENDING, Instant.now());
    }
}
