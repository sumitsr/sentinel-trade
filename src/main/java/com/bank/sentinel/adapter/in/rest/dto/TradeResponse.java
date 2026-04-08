package com.bank.sentinel.adapter.in.rest.dto;

import com.bank.sentinel.domain.model.TradeStatus;
import com.bank.sentinel.domain.model.TradeType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeResponse(
        UUID id,
        String accountId,
        String instrumentId,
        BigDecimal quantity,
        BigDecimal price,
        TradeType type,
        TradeStatus status,
        Instant executedAt
) {}
