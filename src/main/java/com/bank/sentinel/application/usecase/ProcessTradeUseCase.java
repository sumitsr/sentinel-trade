package com.bank.sentinel.application.usecase;

import com.bank.sentinel.domain.model.Trade;
import com.bank.sentinel.domain.model.TradeFailure;
import com.bank.sentinel.domain.port.in.TradeProcessingPort;
import com.bank.sentinel.domain.port.out.TradePersistencePort;
import com.leakyabstractions.result.Results;
import com.leakyabstractions.result.api.Result;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProcessTradeUseCase implements TradeProcessingPort {

    private final TradePersistencePort persistencePort;

    public ProcessTradeUseCase(TradePersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override
    public Result<Trade, TradeFailure> processTrade(Trade trade) {
        return validate(trade).flatMapSuccess(persistencePort::save);
    }

    @Override
    public Result<Trade, TradeFailure> findTradeById(UUID id) {
        return persistencePort.findById(id);
    }

    private Result<Trade, TradeFailure> validate(Trade trade) {
        if (trade == null) return Results.failure(new TradeFailure.ValidationFailure("trade must not be null"));
        if (trade.accountId() == null) return Results.failure(new TradeFailure.ValidationFailure("accountId must not be null"));
        if (trade.instrumentId() == null) return Results.failure(new TradeFailure.ValidationFailure("instrumentId must not be null"));
        if (trade.quantity() == null) return Results.failure(new TradeFailure.ValidationFailure("quantity must not be null"));
        if (trade.price() == null) return Results.failure(new TradeFailure.ValidationFailure("price must not be null"));
        if (trade.type() == null) return Results.failure(new TradeFailure.ValidationFailure("type must not be null"));
        return Results.success(trade);
    }
}
