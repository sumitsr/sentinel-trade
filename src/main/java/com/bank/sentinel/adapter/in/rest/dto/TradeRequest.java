package com.bank.sentinel.adapter.in.rest.dto;

import com.bank.sentinel.domain.model.TradeType;

import java.math.BigDecimal;

public record TradeRequest(
        String accountId,
        String instrumentId,
        BigDecimal quantity,
        BigDecimal price,
        TradeType type
) {}
