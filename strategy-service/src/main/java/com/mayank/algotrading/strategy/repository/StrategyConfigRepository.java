package com.mayank.algotrading.strategy.repository;

import com.mayank.algotrading.common.model.StrategyConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StrategyConfigRepository extends JpaRepository<StrategyConfig, Long> {
    
    Optional<StrategyConfig> findByName(String name);
    
    List<StrategyConfig> findByIsActiveTrue();
    
    List<StrategyConfig> findBySymbol(String symbol);
}
