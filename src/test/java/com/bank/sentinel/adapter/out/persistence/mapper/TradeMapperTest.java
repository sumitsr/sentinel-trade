package com.bank.sentinel.adapter.out.persistence.mapper;

import com.bank.sentinel.adapter.out.persistence.entity.TradeEntity;
import com.bank.sentinel.domain.model.Trade;
import com.bank.sentinel.domain.model.TradeStatus;
import com.bank.sentinel.domain.model.TradeType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TradeMapperTest {

    private final TradeMapper mapper = new TradeMapper();

    private static final UUID ID = UUID.randomUUID();
    private static final Instant NOW = Instant.now();

    private TradeEntity sampleEntity() {
        return new TradeEntity(ID, "ACC-001", "AAPL",
                new BigDecimal("100.00"), new BigDecimal("150.00"),
                TradeType.BUY, TradeStatus.PENDING, NOW, NOW);
    }

    private Trade sampleTrade() {
        return new Trade(ID, "ACC-001", "AAPL",
                new BigDecimal("100.00"), new BigDecimal("150.00"),
                TradeType.BUY, TradeStatus.PENDING, NOW);
    }

    @Test
    void toModel_mapsAllFieldsFromEntity() {
        Trade trade = mapper.toModel(sampleEntity());

        assertThat(trade.id()).isEqualTo(ID);
        assertThat(trade.accountId()).isEqualTo("ACC-001");
        assertThat(trade.instrumentId()).isEqualTo("AAPL");
        assertThat(trade.quantity()).isEqualByComparingTo("100.00");
        assertThat(trade.price()).isEqualByComparingTo("150.00");
        assertThat(trade.type()).isEqualTo(TradeType.BUY);
        assertThat(trade.status()).isEqualTo(TradeStatus.PENDING);
        assertThat(trade.executedAt()).isEqualTo(NOW);
    }

    @Test
    void toEntity_mapsAllFieldsFromTrade() {
        TradeEntity entity = mapper.toEntity(sampleTrade());

        assertThat(entity.getId()).isEqualTo(ID);
        assertThat(entity.getAccountId()).isEqualTo("ACC-001");
        assertThat(entity.getInstrumentId()).isEqualTo("AAPL");
        assertThat(entity.getQuantity()).isEqualByComparingTo("100.00");
        assertThat(entity.getPrice()).isEqualByComparingTo("150.00");
        assertThat(entity.getType()).isEqualTo(TradeType.BUY);
        assertThat(entity.getStatus()).isEqualTo(TradeStatus.PENDING);
        assertThat(entity.getExecutedAt()).isEqualTo(NOW);
    }

    @Test
    void toModel_afterToEntity_roundTripPreservesAllFields() {
        Trade original = sampleTrade();
        Trade roundTripped = mapper.toModel(mapper.toEntity(original));

        assertThat(roundTripped.id()).isEqualTo(original.id());
        assertThat(roundTripped.accountId()).isEqualTo(original.accountId());
        assertThat(roundTripped.instrumentId()).isEqualTo(original.instrumentId());
        assertThat(roundTripped.quantity()).isEqualByComparingTo(original.quantity());
        assertThat(roundTripped.price()).isEqualByComparingTo(original.price());
        assertThat(roundTripped.type()).isEqualTo(original.type());
        assertThat(roundTripped.status()).isEqualTo(original.status());
        assertThat(roundTripped.executedAt()).isEqualTo(original.executedAt());
    }
}
