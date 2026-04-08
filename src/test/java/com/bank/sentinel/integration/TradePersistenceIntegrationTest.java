package com.bank.sentinel.integration;

import com.bank.sentinel.domain.model.Trade;
import com.bank.sentinel.domain.model.TradeStatus;
import com.bank.sentinel.domain.model.TradeType;
import com.bank.sentinel.domain.port.out.TradePersistencePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class TradePersistenceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "false");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @Autowired
    TradePersistencePort persistencePort;

    private Trade newTrade(String accountId) {
        return new Trade(UUID.randomUUID(), accountId, "AAPL",
                new BigDecimal("100.00"), new BigDecimal("150.00"),
                TradeType.BUY, TradeStatus.PENDING, Instant.now());
    }

    @Test
    void saveAndFindById_returnsPersistedTrade() {
        Trade trade = newTrade("ACC-INTEG-1");
        persistencePort.save(trade);

        var found = persistencePort.findById(trade.id());

        assertThat(found.getSuccess()).hasValueSatisfying(t ->
                assertThat(t.accountId()).isEqualTo("ACC-INTEG-1"));
    }

    @Test
    void findByAccountId_returnsAllTradesForAccount() {
        persistencePort.save(newTrade("ACC-INTEG-2"));
        persistencePort.save(newTrade("ACC-INTEG-2"));

        var result = persistencePort.findByAccountId("ACC-INTEG-2");

        assertThat(result.getSuccess()).hasValueSatisfying(list ->
                assertThat(list).hasSize(2));
    }

    @Test
    void saveTwiceWithDifferentIds_doesNotThrow() {
        Trade first = newTrade("ACC-INTEG-3");
        Trade second = newTrade("ACC-INTEG-3");

        var r1 = persistencePort.save(first);
        var r2 = persistencePort.save(second);

        assertThat(r1.getSuccess()).isPresent();
        assertThat(r2.getSuccess()).isPresent();
    }
}
