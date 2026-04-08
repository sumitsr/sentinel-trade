package com.bank.sentinel.adapter.out.persistence.mapper;

import com.bank.sentinel.adapter.out.persistence.entity.TradeEntity;
import com.bank.sentinel.domain.model.Trade;
import org.springframework.stereotype.Component;

@Component
public class TradeMapper {

    public Trade toModel(TradeEntity entity) {
        return new Trade(
                entity.getId(), entity.getAccountId(), entity.getInstrumentId(),
                entity.getQuantity(), entity.getPrice(),
                entity.getType(), entity.getStatus(), entity.getExecutedAt()
        );
    }

    public TradeEntity toEntity(Trade trade) {
        return new TradeEntity(
                trade.id(), trade.accountId(), trade.instrumentId(),
                trade.quantity(), trade.price(),
                trade.type(), trade.status(), trade.executedAt(), null
        );
    }
}
