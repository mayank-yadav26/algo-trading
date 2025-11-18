package com.mayank.algotrading.dashboard.repository;

import com.mayank.algotrading.common.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByStrategyIdOrderByCreatedAtDesc(Long strategyId);
}
