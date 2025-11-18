package com.mayank.algotrading.marketdata.repository;

import com.mayank.algotrading.common.model.Candle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CandleRepository extends JpaRepository<Candle, Long> {
    
    List<Candle> findBySymbolOrderByTimestampDesc(String symbol);
    
    List<Candle> findBySymbolAndTimeframeOrderByTimestampDesc(String symbol, String timeframe);
    
    List<Candle> findBySymbolAndTimestampBetweenOrderByTimestamp(
            String symbol, LocalDateTime startTime, LocalDateTime endTime);
    
    List<Candle> findBySymbolAndTimeframeAndTimestampBetweenOrderByTimestamp(
            String symbol, String timeframe, LocalDateTime startTime, LocalDateTime endTime);
    
    Optional<Candle> findFirstBySymbolAndTimeframeOrderByTimestampDesc(String symbol, String timeframe);
    
    @Query("SELECT c FROM Candle c WHERE c.symbol = :symbol AND c.timeframe = :timeframe " +
           "AND c.timestamp >= :startTime ORDER BY c.timestamp ASC")
    List<Candle> findHistoricalData(@Param("symbol") String symbol, 
                                     @Param("timeframe") String timeframe,
                                     @Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT COUNT(c) FROM Candle c WHERE c.symbol = :symbol AND c.timeframe = :timeframe")
    Long countBySymbolAndTimeframe(@Param("symbol") String symbol, @Param("timeframe") String timeframe);
}
