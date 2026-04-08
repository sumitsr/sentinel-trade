package com.bank.sentinel.domain.port.in;

import com.bank.sentinel.domain.model.Trade;
import com.bank.sentinel.domain.model.TradeFailure;
import com.leakyabstractions.result.api.Result;

import java.util.UUID;

public interface TradeProcessingPort {

    Result<Trade, TradeFailure> processTrade(Trade trade);

    Result<Trade, TradeFailure> findTradeById(UUID id);
}
