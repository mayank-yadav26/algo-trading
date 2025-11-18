package com.mayank.algotrading.strategy.ta4j;

import org.ta4j.core.*;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

public class StrategyFactory {
    
    /**
     * Simple Moving Average Crossover Strategy
     * Buy when short SMA crosses above long SMA
     * Sell when short SMA crosses below long SMA
     */
    public static Strategy createSMAStrategy(BarSeries series, int shortPeriod, int longPeriod) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        
        SMAIndicator shortSma = new SMAIndicator(closePrice, shortPeriod);
        SMAIndicator longSma = new SMAIndicator(closePrice, longPeriod);
        
        Rule entryRule = new CrossedUpIndicatorRule(shortSma, longSma);
        Rule exitRule = new CrossedDownIndicatorRule(shortSma, longSma);
        
        return new BaseStrategy(entryRule, exitRule);
    }
    
    /**
     * Exponential Moving Average Strategy
     */
    public static Strategy createEMAStrategy(BarSeries series, int shortPeriod, int longPeriod) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        
        EMAIndicator shortEma = new EMAIndicator(closePrice, shortPeriod);
        EMAIndicator longEma = new EMAIndicator(closePrice, longPeriod);
        
        Rule entryRule = new CrossedUpIndicatorRule(shortEma, longEma);
        Rule exitRule = new CrossedDownIndicatorRule(shortEma, longEma);
        
        return new BaseStrategy(entryRule, exitRule);
    }
    
    /**
     * RSI Strategy
     * Buy when RSI crosses above oversold threshold (30)
     * Sell when RSI crosses below overbought threshold (70)
     */
    public static Strategy createRSIStrategy(BarSeries series, int period, int oversold, int overbought) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        RSIIndicator rsi = new RSIIndicator(closePrice, period);
        
        Rule entryRule = new CrossedUpIndicatorRule(rsi, oversold);
        Rule exitRule = new CrossedDownIndicatorRule(rsi, overbought);
        
        return new BaseStrategy(entryRule, exitRule);
    }
    
    /**
     * MACD Strategy
     * Buy when MACD crosses above signal line
     * Sell when MACD crosses below signal line
     */
    public static Strategy createMACDStrategy(BarSeries series, int shortPeriod, int longPeriod, int signalPeriod) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        
        MACDIndicator macd = new MACDIndicator(closePrice, shortPeriod, longPeriod);
        EMAIndicator signal = new EMAIndicator(macd, signalPeriod);
        
        Rule entryRule = new CrossedUpIndicatorRule(macd, signal);
        Rule exitRule = new CrossedDownIndicatorRule(macd, signal);
        
        return new BaseStrategy(entryRule, exitRule);
    }
    
    /**
     * Bollinger Bands Strategy
     * Buy when price crosses below lower band
     * Sell when price crosses above upper band
     */
    public static Strategy createBollingerBandsStrategy(BarSeries series, int period, double multiplier) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        
        SMAIndicator sma = new SMAIndicator(closePrice, period);
        StandardDeviationIndicator stdDev = new StandardDeviationIndicator(closePrice, period);
        
        // TA4J 0.16: Use the BollingerBandsMiddleIndicator and calculate manually
        BollingerBandsMiddleIndicator middleBand = new BollingerBandsMiddleIndicator(sma);
        BollingerBandsUpperIndicator upperBand = new BollingerBandsUpperIndicator(middleBand, stdDev);
        BollingerBandsLowerIndicator lowerBand = new BollingerBandsLowerIndicator(middleBand, stdDev);
        
        Rule entryRule = new UnderIndicatorRule(closePrice, lowerBand);
        Rule exitRule = new OverIndicatorRule(closePrice, upperBand);
        
        return new BaseStrategy(entryRule, exitRule);
    }
    
    /**
     * Combined Strategy using multiple indicators
     */
    public static Strategy createCombinedStrategy(BarSeries series) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        
        // SMA indicators
        SMAIndicator shortSma = new SMAIndicator(closePrice, 10);
        SMAIndicator longSma = new SMAIndicator(closePrice, 30);
        
        // RSI indicator
        RSIIndicator rsi = new RSIIndicator(closePrice, 14);
        
        // Entry: Short SMA crosses above long SMA AND RSI is not overbought
        Rule entryRule = new CrossedUpIndicatorRule(shortSma, longSma)
                .and(new UnderIndicatorRule(rsi, 70));
        
        // Exit: Short SMA crosses below long SMA OR RSI is overbought
        Rule exitRule = new CrossedDownIndicatorRule(shortSma, longSma)
                .or(new OverIndicatorRule(rsi, 70));
        
        return new BaseStrategy(entryRule, exitRule);
    }
}
