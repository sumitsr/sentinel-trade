package com.bank.sentinel.domain.model;

import com.bank.sentinel.TestFixtures;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TradeTest {

    @Test
    void should_construct_trade_with_all_valid_fields() {
        Trade trade = TestFixtures.validTrade();

        assertThat(trade.id()).isNotNull();
        assertThat(trade.accountId()).isEqualTo("ACC-001");
        assertThat(trade.instrumentId()).isEqualTo("AAPL");
        assertThat(trade.quantity()).isEqualByComparingTo("100");
        assertThat(trade.price()).isEqualByComparingTo("150.00");
        assertThat(trade.type()).isEqualTo(TradeType.BUY);
        assertThat(trade.status()).isEqualTo(TradeStatus.PENDING);
        assertThat(trade.executedAt()).isNotNull();
    }

    @Test
    void should_be_equal_when_all_fields_are_the_same() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        Trade t1 = new Trade(id, "ACC-001", "AAPL", BigDecimal.TEN, BigDecimal.ONE,
                TradeType.SELL, TradeStatus.PROCESSED, now);
        Trade t2 = new Trade(id, "ACC-001", "AAPL", BigDecimal.TEN, BigDecimal.ONE,
                TradeType.SELL, TradeStatus.PROCESSED, now);

        assertThat(t1).isEqualTo(t2);
    }

    @Test
    void should_not_be_equal_when_id_differs() {
        Instant now = Instant.now();
        Trade t1 = new Trade(UUID.randomUUID(), "ACC-001", "AAPL", BigDecimal.TEN,
                BigDecimal.ONE, TradeType.BUY, TradeStatus.PENDING, now);
        Trade t2 = new Trade(UUID.randomUUID(), "ACC-001", "AAPL", BigDecimal.TEN,
                BigDecimal.ONE, TradeType.BUY, TradeStatus.PENDING, now);

        assertThat(t1).isNotEqualTo(t2);
    }

    @Test
    void should_allow_null_id_for_unsaved_trade() {
        Trade trade = new Trade(null, "ACC-001", "AAPL", BigDecimal.TEN,
                BigDecimal.ONE, TradeType.BUY, TradeStatus.PENDING, Instant.now());

        assertThat(trade.id()).isNull();
    }

    @Test
    void should_have_consistent_hash_code_for_equal_trades() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        Trade t1 = new Trade(id, "ACC-001", "AAPL", BigDecimal.TEN, BigDecimal.ONE,
                TradeType.BUY, TradeStatus.PENDING, now);
        Trade t2 = new Trade(id, "ACC-001", "AAPL", BigDecimal.TEN, BigDecimal.ONE,
                TradeType.BUY, TradeStatus.PENDING, now);

        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
    }
}
