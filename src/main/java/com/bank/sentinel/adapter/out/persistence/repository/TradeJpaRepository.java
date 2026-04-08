package com.bank.sentinel.adapter.out.persistence.repository;

import com.bank.sentinel.adapter.out.persistence.entity.TradeEntity;
import com.bank.sentinel.domain.model.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TradeJpaRepository extends JpaRepository<TradeEntity, UUID> {

    List<TradeEntity> findByAccountId(String accountId);

    @Query("SELECT t FROM TradeEntity t WHERE t.status = :status")
    List<TradeEntity> findByStatus(TradeStatus status);
}
