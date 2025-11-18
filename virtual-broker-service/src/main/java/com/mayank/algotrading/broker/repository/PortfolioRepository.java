package com.mayank.algotrading.broker.repository;

import com.mayank.algotrading.common.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    
    Optional<Portfolio> findByStrategyId(Long strategyId);
}
