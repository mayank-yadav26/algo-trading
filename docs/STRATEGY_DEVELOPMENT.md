# Strategy Development Guide

This guide explains how to develop and implement custom trading strategies in the Algo Trading System.

## 📚 Understanding TA4J

The system uses [TA4J (Technical Analysis for Java)](https://github.com/ta4j/ta4j) for implementing trading strategies and indicators.

### Key Concepts

#### BarSeries
A collection of bars (candles) representing price data over time.

#### Indicators
Technical indicators calculate values based on price data:
- SMA (Simple Moving Average)
- EMA (Exponential Moving Average)
- RSI (Relative Strength Index)
- MACD (Moving Average Convergence Divergence)
- Bollinger Bands

#### Rules
Entry and exit conditions for trades:
- `CrossedUpIndicatorRule` - When one indicator crosses above another
- `CrossedDownIndicatorRule` - When one indicator crosses below another
- `OverIndicatorRule` - When indicator is above a threshold
- `UnderIndicatorRule` - When indicator is below a threshold

#### Strategy
Combines entry and exit rules to generate trading signals.

## 🎯 Implementing a Custom Strategy

### Step 1: Add Strategy Type

Add your strategy type to `StrategyType` enum:

```java
// common-library/src/main/java/com/mayank/algotrading/common/enums/StrategyType.java
public enum StrategyType {
    SMA,
    EMA,
    RSI,
    MACD,
    BOLLINGER_BANDS,
    COMBINED,
    CUSTOM_MOMENTUM  // Your new strategy
}
```

### Step 2: Create Strategy Factory Method

Add a factory method in `StrategyFactory`:

```java
// strategy-service/src/main/java/com/mayank/algotrading/strategy/ta4j/StrategyFactory.java

/**
 * Custom Momentum Strategy
 * Combines RSI and volume for momentum detection
 */
public static Strategy createCustomMomentumStrategy(BarSeries series, int rsiPeriod, int volumeMultiplier) {
    ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
    VolumeIndicator volume = new VolumeIndicator(series);
    
    // RSI component
    RSIIndicator rsi = new RSIIndicator(closePrice, rsiPeriod);
    
    // Volume component - compare to average
    SMAIndicator avgVolume = new SMAIndicator(volume, 20);
    MultiplierIndicator volumeThreshold = new MultiplierIndicator(avgVolume, volumeMultiplier);
    
    // Entry: RSI crosses above 50 AND volume is high
    Rule entryRule = new CrossedUpIndicatorRule(rsi, 50)
            .and(new OverIndicatorRule(volume, volumeThreshold));
    
    // Exit: RSI crosses below 50 OR volume drops
    Rule exitRule = new CrossedDownIndicatorRule(rsi, 50)
            .or(new UnderIndicatorRule(volume, volumeThreshold));
    
    return new BaseStrategy(entryRule, exitRule);
}
```

### Step 3: Integrate in StrategyService

Update `createTA4JStrategy` method in `StrategyService`:

```java
private Strategy createTA4JStrategy(BarSeries series, StrategyConfig config) {
    try {
        Map<String, Object> params = objectMapper.readValue(
                config.getParameters(), new TypeReference<>() {});
        
        return switch (config.getType()) {
            case SMA -> StrategyFactory.createSMAStrategy(series,
                    (Integer) params.getOrDefault("shortPeriod", 10),
                    (Integer) params.getOrDefault("longPeriod", 30));
            // ... other cases ...
            case CUSTOM_MOMENTUM -> StrategyFactory.createCustomMomentumStrategy(series,
                    (Integer) params.getOrDefault("rsiPeriod", 14),
                    (Integer) params.getOrDefault("volumeMultiplier", 2));
        };
    } catch (Exception e) {
        log.error("Error creating strategy: {}", e.getMessage());
        return StrategyFactory.createSMAStrategy(series, 10, 30);
    }
}
```

### Step 4: Create Strategy via API

```bash
curl -X POST "http://localhost:8082/api/strategies" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Custom Momentum AAPL",
    "type": "CUSTOM_MOMENTUM",
    "symbol": "AAPL",
    "timeframe": "5m",
    "isActive": true,
    "initialCapital": 10000,
    "riskPerTrade": 2.0,
    "parameters": "{\"rsiPeriod\":14,\"volumeMultiplier\":2}",
    "description": "RSI + Volume momentum strategy"
  }'
```

## 💡 Strategy Examples

### Example 1: Triple Moving Average Crossover

```java
public static Strategy createTripleMAStrategy(BarSeries series) {
    ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
    
    SMAIndicator shortSma = new SMAIndicator(closePrice, 5);
    SMAIndicator mediumSma = new SMAIndicator(closePrice, 10);
    SMAIndicator longSma = new SMAIndicator(closePrice, 20);
    
    // Strong uptrend: short > medium > long
    Rule entryRule = new OverIndicatorRule(shortSma, mediumSma)
            .and(new OverIndicatorRule(mediumSma, longSma));
    
    // Trend reversal: short crosses below medium
    Rule exitRule = new CrossedDownIndicatorRule(shortSma, mediumSma);
    
    return new BaseStrategy(entryRule, exitRule);
}
```

### Example 2: Mean Reversion with Bollinger Bands

```java
public static Strategy createBBMeanReversionStrategy(BarSeries series, int period, double stdDev) {
    ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
    
    SMAIndicator middle = new SMAIndicator(closePrice, period);
    StandardDeviationIndicator std = new StandardDeviationIndicator(closePrice, period);
    
    BollingerBandsUpperIndicator upperBand = new BollingerBandsUpperIndicator(middle, std, stdDev);
    BollingerBandsLowerIndicator lowerBand = new BollingerBandsLowerIndicator(middle, std, stdDev);
    
    // Buy when price touches lower band (oversold)
    Rule entryRule = new UnderIndicatorRule(closePrice, lowerBand);
    
    // Sell when price returns to middle band or touches upper band
    Rule exitRule = new OverIndicatorRule(closePrice, middle)
            .or(new OverIndicatorRule(closePrice, upperBand));
    
    return new BaseStrategy(entryRule, exitRule);
}
```

### Example 3: Trend Following with ADX

```java
public static Strategy createADXTrendStrategy(BarSeries series, int adxPeriod, int adxThreshold) {
    ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
    
    // ADX measures trend strength
    ADXIndicator adx = new ADXIndicator(series, adxPeriod);
    
    // Use MACD for direction
    MACDIndicator macd = new MACDIndicator(closePrice, 12, 26);
    EMAIndicator signal = new EMAIndicator(macd, 9);
    
    // Entry: Strong trend (ADX > threshold) AND MACD bullish
    Rule entryRule = new OverIndicatorRule(adx, adxThreshold)
            .and(new CrossedUpIndicatorRule(macd, signal));
    
    // Exit: Weak trend OR MACD bearish
    Rule exitRule = new UnderIndicatorRule(adx, adxThreshold)
            .or(new CrossedDownIndicatorRule(macd, signal));
    
    return new BaseStrategy(entryRule, exitRule);
}
```

## 🧪 Testing Your Strategy

### 1. Unit Testing

Create unit tests in `strategy-service/src/test/java`:

```java
@SpringBootTest
class CustomMomentumStrategyTest {
    
    @Test
    void testStrategyCreation() {
        // Create sample data
        List<Candle> candles = createSampleCandles();
        BarSeries series = BarSeriesBuilder.buildSeries(candles, "TEST");
        
        // Create strategy
        Strategy strategy = StrategyFactory.createCustomMomentumStrategy(series, 14, 2);
        
        assertNotNull(strategy);
        assertTrue(strategy.getEntryRule() != null);
        assertTrue(strategy.getExitRule() != null);
    }
    
    @Test
    void testSignalGeneration() {
        // Test with specific market conditions
        List<Candle> candles = createBullishCandles();
        BarSeries series = BarSeriesBuilder.buildSeries(candles, "TEST");
        Strategy strategy = StrategyFactory.createCustomMomentumStrategy(series, 14, 2);
        
        int endIndex = series.getEndIndex();
        boolean shouldEnter = strategy.shouldEnter(endIndex);
        
        assertTrue(shouldEnter, "Should generate buy signal in bullish conditions");
    }
}
```

### 2. Backtesting

Test with historical data:

```bash
# First, ensure you have historical data
curl -X GET "http://localhost:8081/api/market-data/historical/AAPL?timeframe=5m&from=2024-01-01T00:00:00&to=2024-11-16T23:59:59"

# Run backtest
curl -X POST "http://localhost:8082/api/strategies/1/backtest?startDate=2024-01-01T00:00:00&endDate=2024-11-16T23:59:59"
```

### 3. Paper Trading

Test in real-time with virtual capital:

```bash
# Activate strategy
curl -X PUT "http://localhost:8082/api/strategies/1" \
  -H "Content-Type: application/json" \
  -d '{ ..., "isActive": true }'

# Monitor signals and performance
curl -X GET "http://localhost:8082/api/strategies/1/signal"
curl -X GET "http://localhost:8080/api/dashboard/strategy/1/performance"
```

## 📊 Strategy Parameters

### Recommended Parameters by Strategy Type

#### SMA Strategy
```json
{
  "shortPeriod": 10,    // Fast-moving average
  "longPeriod": 30      // Slow-moving average
}
```
- Shorter periods (5/15): More signals, more noise
- Longer periods (50/200): Fewer signals, stronger trends

#### RSI Strategy
```json
{
  "period": 14,         // Calculation period
  "oversold": 30,       // Buy threshold
  "overbought": 70      // Sell threshold
}
```
- Lower oversold/higher overbought: Fewer but stronger signals
- Standard values: 30/70 or 20/80

#### MACD Strategy
```json
{
  "shortPeriod": 12,    // Fast EMA
  "longPeriod": 26,     // Slow EMA
  "signalPeriod": 9     // Signal line
}
```
- Standard values work well for most timeframes
- Adjust based on asset volatility

#### Bollinger Bands
```json
{
  "period": 20,         // Middle band (SMA)
  "multiplier": 2.0     // Standard deviation multiplier
}
```
- Higher multiplier: Wider bands, fewer signals
- Lower multiplier: Tighter bands, more signals

## 🎨 Advanced Techniques

### Combining Multiple Indicators

```java
public static Strategy createAdvancedComboStrategy(BarSeries series) {
    ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
    
    // Trend indicators
    SMAIndicator sma50 = new SMAIndicator(closePrice, 50);
    SMAIndicator sma200 = new SMAIndicator(closePrice, 200);
    
    // Momentum indicators
    RSIIndicator rsi = new RSIIndicator(closePrice, 14);
    MACDIndicator macd = new MACDIndicator(closePrice, 12, 26);
    EMAIndicator macdSignal = new EMAIndicator(macd, 9);
    
    // Volume confirmation
    VolumeIndicator volume = new VolumeIndicator(series);
    SMAIndicator avgVolume = new SMAIndicator(volume, 20);
    
    // Complex entry: Uptrend + RSI not overbought + MACD bullish + volume confirmation
    Rule entryRule = new OverIndicatorRule(sma50, sma200)
            .and(new UnderIndicatorRule(rsi, 70))
            .and(new CrossedUpIndicatorRule(macd, macdSignal))
            .and(new OverIndicatorRule(volume, avgVolume));
    
    // Exit: Downtrend OR RSI overbought OR MACD bearish
    Rule exitRule = new UnderIndicatorRule(sma50, sma200)
            .or(new OverIndicatorRule(rsi, 70))
            .or(new CrossedDownIndicatorRule(macd, macdSignal));
    
    return new BaseStrategy(entryRule, exitRule);
}
```

### Stop Loss and Take Profit

When generating signals, add SL/TP levels:

```java
public TradingSignal generateSignalWithRiskManagement(Long strategyId) {
    // ... generate signal ...
    
    if (signal == SignalType.BUY) {
        BigDecimal currentPrice = latestCandle.getClose();
        
        // 2% stop loss
        BigDecimal stopLoss = currentPrice.multiply(BigDecimal.valueOf(0.98));
        
        // 4% take profit (2:1 reward/risk ratio)
        BigDecimal takeProfit = currentPrice.multiply(BigDecimal.valueOf(1.04));
        
        return TradingSignal.builder()
                .strategyId(strategyId)
                .symbol(config.getSymbol())
                .signal(signal)
                .price(currentPrice)
                .stopLoss(stopLoss)
                .takeProfit(takeProfit)
                .build();
    }
}
```

## 📈 Performance Optimization

### 1. Series Management
```java
// Limit series size to improve performance
series.setMaximumBarCount(500);  // Keep only last 500 bars
```

### 2. Caching Indicators
```java
// Calculate indicators once, reuse across multiple checks
private final Map<String, Indicator<?>> indicatorCache = new HashMap<>();

public Indicator<?> getOrCreateIndicator(BarSeries series, String key) {
    return indicatorCache.computeIfAbsent(key, k -> 
        new RSIIndicator(new ClosePriceIndicator(series), 14)
    );
}
```

## 🔍 Debugging Strategies

### Log Signal Details
```java
log.info("Signal: {} | Price: {} | RSI: {} | SMA20: {} | Volume: {}", 
    signal, price, rsi.getValue(endIndex), sma.getValue(endIndex), volume);
```

### Export Strategy Decisions
```java
// Add reasoning to trading signals
return TradingSignal.builder()
        .signal(signal)
        .reason(String.format("RSI=%.2f crossed above 30, Volume=%.0f above average", 
                rsiValue, volumeValue))
        .build();
```

## 📚 Resources

- [TA4J Documentation](https://ta4j.github.io/ta4j-wiki/)
- [TA4J Examples](https://github.com/ta4j/ta4j/tree/master/ta4j-examples)
- [Technical Analysis Explained](https://www.investopedia.com/technical-analysis-4689657)

## 🤝 Contributing Your Strategy

If you develop an interesting strategy, consider contributing it:

1. Fork the repository
2. Add your strategy to `StrategyFactory`
3. Write tests
4. Document parameters and use cases
5. Submit a pull request

See [CONTRIBUTING.md](../CONTRIBUTING.md) for details.
