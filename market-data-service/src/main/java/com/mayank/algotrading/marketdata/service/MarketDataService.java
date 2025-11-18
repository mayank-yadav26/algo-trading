package com.mayank.algotrading.marketdata.service;

import com.mayank.algotrading.common.dto.CandleDTO;
import com.mayank.algotrading.common.model.Candle;
import com.mayank.algotrading.marketdata.client.MarketDataClient;
import com.mayank.algotrading.marketdata.repository.CandleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("null")
@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataService {
    
    private final CandleRepository candleRepository;
    private final MarketDataClient marketDataClient;
    
    @Transactional
    public Candle saveCandle(CandleDTO candleDTO) {
        Candle candle = convertToEntity(candleDTO);
        return candleRepository.save(candle);
    }
    
    @Transactional
    public List<Candle> saveCandles(List<CandleDTO> candleDTOs) {
        List<Candle> candles = candleDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        return candleRepository.saveAll(candles);
    }
    
    public CandleDTO fetchAndSaveRealTimeQuote(String symbol) {
        log.info("Fetching real-time quote for symbol: {}", symbol);
        CandleDTO candleDTO = marketDataClient.fetchRealTimeQuote(symbol);
        
        if (candleDTO != null) {
            saveCandle(candleDTO);
            log.info("Saved real-time quote for {}", symbol);
        }
        return candleDTO;
    }
    
    public List<CandleDTO> fetchAndSaveHistoricalData(String symbol, String timeframe, 
                                                      LocalDateTime from, LocalDateTime to) {
        log.info("Fetching historical data for {} from {} to {}", symbol, from, to);
        List<CandleDTO> candles = marketDataClient.fetchHistoricalCandles(symbol, timeframe, from, to);
        
        if (!candles.isEmpty()) {
            saveCandles(candles);
            log.info("Saved {} historical candles for {}", candles.size(), symbol);
        }
        return candles;
    }
    
    public List<Candle> getHistoricalCandles(String symbol, String timeframe, 
                                             LocalDateTime from, LocalDateTime to) {
        return candleRepository.findBySymbolAndTimeframeAndTimestampBetweenOrderByTimestamp(
                symbol, timeframe, from, to);
    }
    
    public List<Candle> getRecentCandles(String symbol, String timeframe, int count) {
        List<Candle> allCandles = candleRepository
                .findBySymbolAndTimeframeOrderByTimestampDesc(symbol, timeframe);
        return allCandles.stream().limit(count).collect(Collectors.toList());
    }
    
    public Candle getLatestCandle(String symbol, String timeframe) {
        return candleRepository.findFirstBySymbolAndTimeframeOrderByTimestampDesc(symbol, timeframe)
                .orElse(null);
    }
    
    private Candle convertToEntity(CandleDTO dto) {
        return Candle.builder()
                .symbol(dto.getSymbol())
                .timestamp(dto.getTimestamp())
                .open(dto.getOpen())
                .high(dto.getHigh())
                .low(dto.getLow())
                .close(dto.getClose())
                .volume(dto.getVolume())
                .timeframe(dto.getTimeframe())
                .build();
    }
}
