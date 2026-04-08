package com.bank.sentinel.domain.model;

public sealed interface TradeFailure {
    record ValidationFailure(String message) implements TradeFailure {}
    record SystemFailure(String message) implements TradeFailure {}
}
