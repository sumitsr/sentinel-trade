package com.bank.sentinel.adapter.out.persistence;

import com.bank.sentinel.adapter.out.persistence.mapper.TradeMapper;
import com.bank.sentinel.adapter.out.persistence.repository.TradeJpaRepository;
import com.bank.sentinel.domain.model.Trade;
import com.bank.sentinel.domain.model.TradeFailure;
import com.bank.sentinel.domain.port.out.TradePersistencePort;
import com.bank.sentinel.infrastructure.aop.DatabaseResult;
import com.leakyabstractions.result.core.Results;
import com.leakyabstractions.result.api.Result;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class TradePersistenceAdapter implements TradePersistencePort {

    private final TradeJpaRepository repository;
    private final TradeMapper mapper;

    public TradePersistenceAdapter(TradeJpaRepository repository, TradeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @DatabaseResult(operation = "save")
    public Result<Trade, TradeFailure> save(Trade trade) {
        return Results.success(mapper.toModel(repository.save(mapper.toEntity(trade))));
    }

    @Override
    @DatabaseResult(operation = "findById")
    public Result<Trade, TradeFailure> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toModel)
                .map(t -> Results.<Trade, TradeFailure>success(t))
                .orElse(Results.<Trade, TradeFailure>failure(new TradeFailure.PersistenceFailure("Not found: " + id, null)));
    }

    @Override
    @DatabaseResult(operation = "findByAccountId")
    public Result<List<Trade>, TradeFailure> findByAccountId(String accountId) {
        return Results.success(repository.findByAccountId(accountId).stream()
                .map(mapper::toModel)
                .toList());
    }
}
