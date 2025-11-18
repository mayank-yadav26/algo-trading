package com.mayank.algotrading.strategy.repository;

import com.mayank.algotrading.common.model.Candle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CandleRepository extends JpaRepository<Candle, Long> {
    
    List<Candle> findBySymbolAndTimeframeAndTimestampBetweenOrderByTimestamp(
            String symbol, String timeframe, LocalDateTime startTime, LocalDateTime endTime);
    
    List<Candle> findBySymbolAndTimeframeOrderByTimestampDesc(String symbol, String timeframe);
}
