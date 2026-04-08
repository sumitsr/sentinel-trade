package com.bank.sentinel.application.usecase;

import com.bank.sentinel.TestFixtures;
import com.bank.sentinel.domain.model.Trade;
import com.bank.sentinel.domain.model.TradeFailure;
import com.bank.sentinel.domain.port.out.TradePersistencePort;
import com.leakyabstractions.result.core.Results;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessTradeUseCaseTest {

    @Mock
    TradePersistencePort persistencePort;

    @InjectMocks
    ProcessTradeUseCase useCase;

    @Test
    void should_return_success_when_trade_is_valid_and_persistence_saves() {
        Trade trade = TestFixtures.validTrade();
        when(persistencePort.save(trade)).thenReturn(Results.success(trade));

        var result = useCase.processTrade(trade);

        assertThat(result.getSuccess()).isPresent().contains(trade);
    }

    @Test
    void should_return_validation_failure_when_trade_is_null() {
        var result = useCase.processTrade(null);

        assertThat(result.getFailure()).isPresent();
        assertThat(result.getFailure().get()).isInstanceOf(TradeFailure.ValidationFailure.class);
        verify(persistencePort, never()).save(any());
    }

    @Test
    void should_return_validation_failure_when_account_id_is_null() {
        var result = useCase.processTrade(TestFixtures.tradeWithNullAccountId());

        assertThat(result.getFailure()).isPresent();
        assertThat(result.getFailure().get()).isInstanceOf(TradeFailure.ValidationFailure.class);
        verify(persistencePort, never()).save(any());
    }

    @Test
    void should_return_validation_failure_when_instrument_id_is_null() {
        var result = useCase.processTrade(TestFixtures.tradeWithNullInstrumentId());

        assertThat(result.getFailure()).isPresent();
        assertThat(result.getFailure().get()).isInstanceOf(TradeFailure.ValidationFailure.class);
        verify(persistencePort, never()).save(any());
    }

    @Test
    void should_return_validation_failure_when_quantity_is_null() {
        var result = useCase.processTrade(TestFixtures.tradeWithNullQuantity());

        assertThat(result.getFailure()).isPresent();
        assertThat(result.getFailure().get()).isInstanceOf(TradeFailure.ValidationFailure.class);
        verify(persistencePort, never()).save(any());
    }

    @Test
    void should_return_validation_failure_when_price_is_null() {
        var result = useCase.processTrade(TestFixtures.tradeWithNullPrice());

        assertThat(result.getFailure()).isPresent();
        assertThat(result.getFailure().get()).isInstanceOf(TradeFailure.ValidationFailure.class);
        verify(persistencePort, never()).save(any());
    }

    @Test
    void should_return_validation_failure_when_type_is_null() {
        var result = useCase.processTrade(TestFixtures.tradeWithNullType());

        assertThat(result.getFailure()).isPresent();
        assertThat(result.getFailure().get()).isInstanceOf(TradeFailure.ValidationFailure.class);
        verify(persistencePort, never()).save(any());
    }

    @Test
    void should_propagate_persistence_failure_when_save_fails() {
        Trade trade = TestFixtures.validTrade();
        TradeFailure.PersistenceFailure persistenceFailure =
                new TradeFailure.PersistenceFailure("DB unavailable", new RuntimeException());
        when(persistencePort.save(trade)).thenReturn(Results.failure(persistenceFailure));

        var result = useCase.processTrade(trade);

        assertThat(result.getFailure()).isPresent().contains(persistenceFailure);
    }

    @Test
    void should_delegate_find_by_id_to_persistence_port() {
        UUID id = UUID.randomUUID();
        Trade trade = TestFixtures.validTrade();
        when(persistencePort.findById(id)).thenReturn(Results.success(trade));

        var result = useCase.findTradeById(id);

        assertThat(result.getSuccess()).isPresent().contains(trade);
        verify(persistencePort).findById(id);
    }

    @Test
    void should_propagate_failure_when_find_by_id_returns_failure() {
        UUID id = UUID.randomUUID();
        TradeFailure.PersistenceFailure persistenceFailure =
                new TradeFailure.PersistenceFailure("not found", null);
        when(persistencePort.findById(id)).thenReturn(Results.failure(persistenceFailure));

        var result = useCase.findTradeById(id);

        assertThat(result.getFailure()).isPresent().contains(persistenceFailure);
    }
}
