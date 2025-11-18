package com.mayank.algotrading.strategy.service;

import com.mayank.algotrading.common.dto.BacktestResult;
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
import org.ta4j.core.criteria.*;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BacktestService {
    
    private final CandleRepository candleRepository;
    private final StrategyConfigRepository strategyConfigRepository;
    
    public BacktestResult runBacktest(Long strategyId, LocalDateTime startDate, LocalDateTime endDate) {
        @SuppressWarnings("null")
        StrategyConfig config = strategyConfigRepository.findById(strategyId)
                .orElseThrow(() -> new RuntimeException("Strategy not found"));
        
        log.info("Running backtest for strategy: {} from {} to {}", config.getName(), startDate, endDate);
        
        // Fetch historical data
        List<Candle> candles = candleRepository.findBySymbolAndTimeframeAndTimestampBetweenOrderByTimestamp(
                config.getSymbol(), config.getTimeframe(), startDate, endDate);
        
        if (candles.isEmpty()) {
            throw new RuntimeException("No historical data available for backtesting");
        }
        
        // Build TA4J series
        BarSeries series = BarSeriesBuilder.buildSeries(candles, config.getSymbol());
        
        // Create strategy
        Strategy strategy = createStrategyFromConfig(series, config);
        
        // Run backtest manually
        TradingRecord tradingRecord = new BaseTradingRecord();
        Num one = series.numOf(1);
        for (int i = 0; i < series.getBarCount(); i++) {
            if (strategy.shouldEnter(i, tradingRecord)) {
                tradingRecord.enter(i, series.getBar(i).getClosePrice(), one);
            } else if (strategy.shouldExit(i, tradingRecord)) {
                tradingRecord.exit(i, series.getBar(i).getClosePrice(), one);
            }
        }
        
        // Calculate metrics
        return calculateBacktestMetrics(series, tradingRecord, config, startDate, endDate);
    }
    
    private Strategy createStrategyFromConfig(BarSeries series, StrategyConfig config) {
        // Reuse logic from StrategyService or use default parameters
        return switch (config.getType()) {
            case SMA -> StrategyFactory.createSMAStrategy(series, 10, 30);
            case EMA -> StrategyFactory.createEMAStrategy(series, 12, 26);
            case RSI -> StrategyFactory.createRSIStrategy(series, 14, 30, 70);
            case MACD -> StrategyFactory.createMACDStrategy(series, 12, 26, 9);
            case BOLLINGER_BANDS -> StrategyFactory.createBollingerBandsStrategy(series, 20, 2.0);
            case COMBINED -> StrategyFactory.createCombinedStrategy(series);
        };
    }
    
    private BacktestResult calculateBacktestMetrics(BarSeries series, TradingRecord tradingRecord,
                                                    StrategyConfig config, LocalDateTime startDate,
                                                    LocalDateTime endDate) {
        
        // Calculate metrics manually since TA4J 0.15 has limited criterion support
        int totalTrades = tradingRecord.getPositions().size();
        long winningTrades = 0;
        double totalProfit = 1.0;
        double maxDrawdownValue = 0.0;
        Num one = series.numOf(1);
        
        // Calculate profit and winning trades
        for (org.ta4j.core.Position position : tradingRecord.getPositions()) {
            if (position.getExit() != null) {
                Num profit = position.getProfit();
                totalProfit *= profit.doubleValue();
                if (profit.isGreaterThan(one)) {
                    winningTrades++;
                }
            }
        }
        
        // Simple max drawdown calculation
        MaximumDrawdownCriterion maxDD = new MaximumDrawdownCriterion();
        maxDrawdownValue = maxDD.calculate(series, tradingRecord).doubleValue();
        
        long losingTrades = totalTrades - winningTrades;
        
        BigDecimal initialCapital = config.getInitialCapital() != null ? 
                config.getInitialCapital() : BigDecimal.valueOf(10000);
        
        BigDecimal totalReturn = BigDecimal.valueOf(totalProfit - 1.0)
                .multiply(initialCapital)
                .setScale(4, RoundingMode.HALF_UP);
        
        BigDecimal finalCapital = initialCapital.add(totalReturn);
        
        BigDecimal totalReturnPercentage = totalReturn
                .divide(initialCapital, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        
        BigDecimal winRate = totalTrades > 0 ? 
                BigDecimal.valueOf(winningTrades)
                        .divide(BigDecimal.valueOf(totalTrades), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
        
        // Calculate average win/loss
        BigDecimal avgWin = winningTrades > 0 ? 
                totalReturn.divide(BigDecimal.valueOf(winningTrades), 4, RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;
        BigDecimal avgLoss = losingTrades > 0 ?
                BigDecimal.ZERO : BigDecimal.ZERO; // Simplified for now
        
        return BacktestResult.builder()
                .strategyId(config.getId())
                .strategyName(config.getName())
                .symbol(config.getSymbol())
                .startDate(startDate)
                .endDate(endDate)
                .initialCapital(initialCapital)
                .finalCapital(finalCapital)
                .totalReturn(totalReturn)
                .totalReturnPercentage(totalReturnPercentage)
                .totalTrades(totalTrades)
                .winningTrades((int) winningTrades)
                .losingTrades((int) losingTrades)
                .winRate(winRate)
                .maxDrawdown(BigDecimal.valueOf(maxDrawdownValue))
                .averageWin(avgWin)
                .averageLoss(avgLoss)
                .build();
    }
}
