package com.mayank.algotrading.strategy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayank.algotrading.common.dto.TradingSignal;
import com.mayank.algotrading.common.enums.SignalType;
import com.mayank.algotrading.common.enums.StrategyType;
import com.mayank.algotrading.common.model.Candle;
import com.mayank.algotrading.common.model.StrategyConfig;
import com.mayank.algotrading.strategy.repository.CandleRepository;
import com.mayank.algotrading.strategy.repository.StrategyConfigRepository;
import com.mayank.algotrading.strategy.ta4j.BarSeriesBuilder;
import com.mayank.algotrading.strategy.ta4j.StrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("null")
@Slf4j
@Service
@RequiredArgsConstructor
public class StrategyService {
    
    private final StrategyConfigRepository strategyConfigRepository;
    private final CandleRepository candleRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public StrategyConfig createStrategy(StrategyConfig config) {
        return strategyConfigRepository.save(config);
    }
    
    public StrategyConfig updateStrategy(Long id, StrategyConfig config) {
        StrategyConfig existing = strategyConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Strategy not found"));
        
        existing.setName(config.getName());
        existing.setType(config.getType());
        existing.setSymbol(config.getSymbol());
        existing.setTimeframe(config.getTimeframe());
        existing.setIsActive(config.getIsActive());
        existing.setInitialCapital(config.getInitialCapital());
        existing.setRiskPerTrade(config.getRiskPerTrade());
        existing.setParameters(config.getParameters());
        existing.setDescription(config.getDescription());
        
        return strategyConfigRepository.save(existing);
    }
    
    public void deleteStrategy(Long id) {
        strategyConfigRepository.deleteById(id);
    }
    
    public List<StrategyConfig> getAllStrategies() {
        return strategyConfigRepository.findAll();
    }
    
    public StrategyConfig getStrategyById(Long id) {
        return strategyConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Strategy not found"));
    }
    
    public List<StrategyConfig> getActiveStrategies() {
        return strategyConfigRepository.findByIsActiveTrue();
    }
    
    public TradingSignal generateSignal(Long strategyId) {
        StrategyConfig config = getStrategyById(strategyId);
        
        // Fetch recent candles
        List<Candle> candles = candleRepository
                .findBySymbolAndTimeframeOrderByTimestampDesc(config.getSymbol(), config.getTimeframe());
        
        if (candles.isEmpty()) {
            log.warn("No candles found for symbol: {}", config.getSymbol());
            return null;
        }
        
        // Build TA4J series
        BarSeries series = BarSeriesBuilder.buildSeries(candles, config.getSymbol());
        
        // Create strategy based on type
        Strategy strategy = createTA4JStrategy(series, config);
        
        // Get current bar index
        int endIndex = series.getEndIndex();
        
        // Determine signal
        boolean shouldEnter = strategy.shouldEnter(endIndex);
        boolean shouldExit = strategy.shouldExit(endIndex);
        
        SignalType signal = shouldEnter ? SignalType.BUY : 
                           shouldExit ? SignalType.SELL : SignalType.HOLD;
        
        // Calculate indicators
        Map<String, BigDecimal> indicators = calculateIndicators(series, config.getType());
        
        Candle latestCandle = candles.get(0);
        
        return TradingSignal.builder()
                .strategyId(strategyId)
                .symbol(config.getSymbol())
                .signal(signal)
                .price(latestCandle.getClose())
                .timestamp(LocalDateTime.now())
                .indicators(indicators)
                .build();
    }
    
    private Strategy createTA4JStrategy(BarSeries series, StrategyConfig config) {
        try {
            Map<String, Object> params = objectMapper.readValue(
                    config.getParameters(), new TypeReference<>() {});
            
            return switch (config.getType()) {
                case SMA -> StrategyFactory.createSMAStrategy(series,
                        (Integer) params.getOrDefault("shortPeriod", 10),
                        (Integer) params.getOrDefault("longPeriod", 30));
                case EMA -> StrategyFactory.createEMAStrategy(series,
                        (Integer) params.getOrDefault("shortPeriod", 12),
                        (Integer) params.getOrDefault("longPeriod", 26));
                case RSI -> StrategyFactory.createRSIStrategy(series,
                        (Integer) params.getOrDefault("period", 14),
                        (Integer) params.getOrDefault("oversold", 30),
                        (Integer) params.getOrDefault("overbought", 70));
                case MACD -> StrategyFactory.createMACDStrategy(series,
                        (Integer) params.getOrDefault("shortPeriod", 12),
                        (Integer) params.getOrDefault("longPeriod", 26),
                        (Integer) params.getOrDefault("signalPeriod", 9));
                case BOLLINGER_BANDS -> StrategyFactory.createBollingerBandsStrategy(series,
                        (Integer) params.getOrDefault("period", 20),
                        (Double) params.getOrDefault("multiplier", 2.0));
                case COMBINED -> StrategyFactory.createCombinedStrategy(series);
            };
        } catch (Exception e) {
            log.error("Error creating strategy: {}", e.getMessage());
            return StrategyFactory.createSMAStrategy(series, 10, 30);
        }
    }
    
    private Map<String, BigDecimal> calculateIndicators(BarSeries series, StrategyType type) {
        Map<String, BigDecimal> indicators = new HashMap<>();
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        int endIndex = series.getEndIndex();
        
        // Always include basic indicators
        SMAIndicator sma20 = new SMAIndicator(closePrice, 20);
        RSIIndicator rsi14 = new RSIIndicator(closePrice, 14);
        
        indicators.put("SMA20", BigDecimal.valueOf(sma20.getValue(endIndex).doubleValue()));
        indicators.put("RSI14", BigDecimal.valueOf(rsi14.getValue(endIndex).doubleValue()));
        indicators.put("CLOSE", BigDecimal.valueOf(series.getBar(endIndex).getClosePrice().doubleValue()));
        
        return indicators;
    }
}
