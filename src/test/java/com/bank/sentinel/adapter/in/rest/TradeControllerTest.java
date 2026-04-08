package com.bank.sentinel.adapter.in.rest;

import com.bank.sentinel.adapter.in.rest.dto.TradeRequest;
import com.bank.sentinel.domain.model.Trade;
import com.bank.sentinel.domain.model.TradeFailure;
import com.bank.sentinel.domain.port.in.TradeProcessingPort;
import com.leakyabstractions.result.core.Results;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static com.bank.sentinel.domain.model.TradeStatus.PENDING;
import static com.bank.sentinel.domain.model.TradeType.BUY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeControllerTest {

    @Mock
    TradeProcessingPort processingPort;

    @InjectMocks
    TradeController controller;

    private static final UUID TRADE_ID = UUID.randomUUID();

    private Trade validTrade() {
        return new Trade(TRADE_ID, "ACC001", "AAPL",
                new BigDecimal("100.00"), new BigDecimal("150.00"),
                BUY, PENDING, Instant.now());
    }

    private TradeRequest validRequest() {
        return new TradeRequest("ACC001", "AAPL",
                new BigDecimal("100.00"), new BigDecimal("150.00"), BUY);
    }

    @Test
    void createTrade_validRequest_returns201() {
        when(processingPort.processTrade(any())).thenReturn(Results.success(validTrade()));

        ResponseEntity<?> response = controller.create(validRequest());

        assertThat(response.getStatusCode().value()).isEqualTo(201);
    }

    @Test
    void createTrade_useCaseReturnsFailure_returns422() {
        when(processingPort.processTrade(any()))
                .thenReturn(Results.failure(new TradeFailure.ValidationFailure("invalid")));

        ResponseEntity<?> response = controller.create(validRequest());

        assertThat(response.getStatusCode().value()).isEqualTo(422);
    }

    @Test
    void findTradeById_existingId_returns200() {
        when(processingPort.findTradeById(TRADE_ID)).thenReturn(Results.success(validTrade()));

        ResponseEntity<?> response = controller.findById(TRADE_ID);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void findTradeById_nonExistentId_returns404() {
        UUID unknown = UUID.randomUUID();
        when(processingPort.findTradeById(unknown))
                .thenReturn(Results.failure(new TradeFailure.PersistenceFailure("Not found", null)));

        ResponseEntity<?> response = controller.findById(unknown);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }
}
