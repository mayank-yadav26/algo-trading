package com.mayank.algotrading.broker.repository;

import com.mayank.algotrading.common.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    
    List<Position> findByStrategyIdAndIsOpenTrue(Long strategyId);
    
    List<Position> findByStrategyId(Long strategyId);
    
    Optional<Position> findByStrategyIdAndSymbolAndIsOpenTrue(Long strategyId, String symbol);
    
    List<Position> findByIsOpenTrue();
}
