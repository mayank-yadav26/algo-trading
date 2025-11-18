package com.mayank.algotrading.broker.repository;

import com.mayank.algotrading.common.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    
    List<Trade> findByStrategyIdOrderByCreatedAtDesc(Long strategyId);
    
    List<Trade> findBySymbolOrderByCreatedAtDesc(String symbol);
    
    List<Trade> findByStrategyIdAndStatus(Long strategyId, com.mayank.algotrading.common.enums.OrderStatus status);
    
    @Query("SELECT t FROM Trade t WHERE t.strategyId = :strategyId AND t.entryTime BETWEEN :startDate AND :endDate")
    List<Trade> findTradesByDateRange(@Param("strategyId") Long strategyId, 
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(t) FROM Trade t WHERE t.strategyId = :strategyId AND t.pnl > 0")
    Long countWinningTrades(@Param("strategyId") Long strategyId);
    
    @Query("SELECT COUNT(t) FROM Trade t WHERE t.strategyId = :strategyId AND t.pnl < 0")
    Long countLosingTrades(@Param("strategyId") Long strategyId);
}
