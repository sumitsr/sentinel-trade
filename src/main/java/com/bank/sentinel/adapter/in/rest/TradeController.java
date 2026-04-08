package com.bank.sentinel.adapter.in.rest;

import com.bank.sentinel.adapter.in.rest.dto.TradeRequest;
import com.bank.sentinel.adapter.in.rest.dto.TradeResponse;
import com.bank.sentinel.domain.model.Trade;
import com.bank.sentinel.domain.model.TradeStatus;
import com.bank.sentinel.domain.port.in.TradeProcessingPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trades")
public class TradeController {

    private final TradeProcessingPort processingPort;

    public TradeController(TradeProcessingPort processingPort) {
        this.processingPort = processingPort;
    }

    @PostMapping
    public ResponseEntity<TradeResponse> create(@RequestBody TradeRequest request) {
        return processingPort.processTrade(toTrade(request))
                .getSuccess()
                .map(this::toResponse)
                .map(r -> ResponseEntity.status(201).<TradeResponse>body(r))
                .orElse(ResponseEntity.status(422).build());
    }

    private Trade toTrade(TradeRequest r) {
        return new Trade(null, r.accountId(), r.instrumentId(),
                r.quantity(), r.price(), r.type(), TradeStatus.PENDING, Instant.now());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TradeResponse> findById(@PathVariable UUID id) {
        return processingPort.findTradeById(id)
                .getSuccess()
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private TradeResponse toResponse(Trade trade) {
        return new TradeResponse(trade.id(), trade.accountId(), trade.instrumentId(),
                trade.quantity(), trade.price(), trade.type(), trade.status(), trade.executedAt());
    }
}
