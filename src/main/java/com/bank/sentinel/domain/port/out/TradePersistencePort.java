package com.bank.sentinel.domain.port.out;

import com.bank.sentinel.domain.model.Trade;
import com.bank.sentinel.domain.model.TradeFailure;
import com.leakyabstractions.result.api.Result;

import java.util.List;
import java.util.UUID;

public interface TradePersistencePort {

    Result<Trade, TradeFailure> save(Trade trade);

    Result<Trade, TradeFailure> findById(UUID id);

    Result<List<Trade>, TradeFailure> findByAccountId(String accountId);
}
