package com.bank.sentinel.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Trade(
        UUID id,
        String accountId,
        String instrumentId,
        BigDecimal quantity,
        BigDecimal price,
        TradeType type,
        TradeStatus status,
        Instant executedAt
) {}
