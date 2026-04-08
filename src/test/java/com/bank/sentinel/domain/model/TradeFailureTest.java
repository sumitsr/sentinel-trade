package com.bank.sentinel.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TradeFailureTest {

    @Test
    void should_carry_message_in_validation_failure() {
        var failure = new TradeFailure.ValidationFailure("trade must not be null");

        assertThat(failure.message()).isEqualTo("trade must not be null");
    }

    @Test
    void should_carry_message_and_cause_in_persistence_failure() {
        Throwable cause = new RuntimeException("DB error");
        var failure = new TradeFailure.PersistenceFailure("save failed", cause);

        assertThat(failure.message()).isEqualTo("save failed");
        assertThat(failure.cause()).isSameAs(cause);
    }

    @Test
    void should_carry_trade_id_in_duplicate_trade_failure() {
        UUID tradeId = UUID.randomUUID();
        var failure = new TradeFailure.DuplicateTradeFailure(tradeId);

        assertThat(failure.tradeId()).isEqualTo(tradeId);
    }

    @Test
    void should_match_all_sealed_subtypes_exhaustively() {
        TradeFailure[] failures = {
                new TradeFailure.ValidationFailure("v"),
                new TradeFailure.PersistenceFailure("p", new RuntimeException()),
                new TradeFailure.DuplicateTradeFailure(UUID.randomUUID())
        };

        for (TradeFailure failure : failures) {
            String result = switch (failure) {
                case TradeFailure.ValidationFailure f -> "validation";
                case TradeFailure.PersistenceFailure f -> "persistence";
                case TradeFailure.DuplicateTradeFailure f -> "duplicate";
            };
            assertThat(result).isNotNull();
        }
    }

    @Test
    void should_be_instance_of_trade_failure_sealed_interface() {
        TradeFailure failure = new TradeFailure.ValidationFailure("error");

        assertThat(failure).isInstanceOf(TradeFailure.class);
        assertThat(failure).isInstanceOf(TradeFailure.ValidationFailure.class);
    }
}
