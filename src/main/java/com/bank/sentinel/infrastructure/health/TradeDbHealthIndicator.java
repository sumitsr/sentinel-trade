package com.bank.sentinel.infrastructure.health;

import com.bank.sentinel.domain.model.TradeFailure;
import com.bank.sentinel.domain.port.TradePersistencePort;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TradeDbHealthIndicator implements HealthIndicator {

    private static final UUID PROBE_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private final TradePersistencePort persistencePort;

    public TradeDbHealthIndicator(TradePersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override
    public Health health() {
        var result = persistencePort.findById(PROBE_ID);
        return result.getFailure().map(f -> f instanceof TradeFailure.PersistenceFailure)
                .orElse(false) ? Health.down().build() : Health.up().build();
    }
}
