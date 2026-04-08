package com.bank.sentinel.adapter.out.persistence;

import com.bank.sentinel.adapter.out.persistence.entity.TradeEntity;
import com.bank.sentinel.adapter.out.persistence.mapper.TradeMapper;
import com.bank.sentinel.adapter.out.persistence.repository.TradeJpaRepository;
import com.bank.sentinel.domain.model.Trade;
import com.bank.sentinel.domain.model.TradeFailure;
import com.bank.sentinel.domain.model.TradeStatus;
import com.bank.sentinel.domain.model.TradeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradePersistenceAdapterTest {

    @Mock TradeJpaRepository repository;
    @Mock TradeMapper mapper;
    @InjectMocks TradePersistenceAdapter adapter;

    private static final UUID ID = UUID.randomUUID();
    private static final Instant NOW = Instant.now();

    private Trade sampleTrade() {
        return new Trade(ID, "ACC-001", "AAPL",
                new BigDecimal("100.00"), new BigDecimal("150.00"),
                TradeType.BUY, TradeStatus.PENDING, NOW);
    }

    private TradeEntity sampleEntity() {
        return new TradeEntity(ID, "ACC-001", "AAPL",
                new BigDecimal("100.00"), new BigDecimal("150.00"),
                TradeType.BUY, TradeStatus.PENDING, NOW, NOW);
    }

    @Test
    void save_callsRepositorySaveAndReturnsMappedSuccessResult() {
        Trade trade = sampleTrade();
        TradeEntity entity = sampleEntity();
        when(mapper.toEntity(trade)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toModel(entity)).thenReturn(trade);

        var result = adapter.save(trade);

        assertThat(result.getSuccess()).contains(trade);
    }

    @Test
    void findById_entityFound_returnsMappedSuccessResult() {
        TradeEntity entity = sampleEntity();
        Trade trade = sampleTrade();
        when(repository.findById(ID)).thenReturn(Optional.of(entity));
        when(mapper.toModel(entity)).thenReturn(trade);

        var result = adapter.findById(ID);

        assertThat(result.getSuccess()).contains(trade);
    }

    @Test
    void findById_entityNotFound_returnsPersistenceFailureWithNotFoundMessage() {
        when(repository.findById(ID)).thenReturn(Optional.empty());

        var result = adapter.findById(ID);

        assertThat(result.getFailure())
                .get()
                .isInstanceOfSatisfying(TradeFailure.PersistenceFailure.class,
                        f -> assertThat(f.message()).startsWith("Not found:"));
    }

    @Test
    void findByAccountId_returnsMappedSuccessResultWithList() {
        TradeEntity entity = sampleEntity();
        Trade trade = sampleTrade();
        when(repository.findByAccountId("ACC-001")).thenReturn(List.of(entity));
        when(mapper.toModel(entity)).thenReturn(trade);

        var result = adapter.findByAccountId("ACC-001");

        assertThat(result.getSuccess()).hasValueSatisfying(list ->
                assertThat(list).containsExactly(trade));
    }
}
