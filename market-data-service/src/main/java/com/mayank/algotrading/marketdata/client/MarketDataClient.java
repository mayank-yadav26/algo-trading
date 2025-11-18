package com.mayank.algotrading.marketdata.client;

import com.mayank.algotrading.common.dto.CandleDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface MarketDataClient {
    
    /**
     * Fetch real-time quote for a symbol
     */
    CandleDTO fetchRealTimeQuote(String symbol);
    
    /**
     * Fetch historical candles for a symbol
     */
    List<CandleDTO> fetchHistoricalCandles(String symbol, String timeframe, 
                                           LocalDateTime from, LocalDateTime to);
    
    /**
     * Check if the API is available and configured
     */
    boolean isAvailable();
}
