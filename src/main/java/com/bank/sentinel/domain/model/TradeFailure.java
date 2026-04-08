package com.bank.sentinel.domain.model;

import java.util.UUID;

public sealed interface TradeFailure
        permits TradeFailure.ValidationFailure,
                TradeFailure.PersistenceFailure,
                TradeFailure.DuplicateTradeFailure {

    record ValidationFailure(String message) implements TradeFailure {}
    record PersistenceFailure(String message, Throwable cause) implements TradeFailure {}
    record DuplicateTradeFailure(UUID tradeId) implements TradeFailure {}
}
