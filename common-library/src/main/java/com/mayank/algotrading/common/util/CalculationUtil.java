package com.mayank.algotrading.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculationUtil {
    
    private static final int PRICE_SCALE = 4;
    private static final int PERCENTAGE_SCALE = 2;
    
    public static BigDecimal calculatePercentageChange(BigDecimal initial, BigDecimal current) {
        if (initial == null || current == null || initial.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return current.subtract(initial)
                .divide(initial, PERCENTAGE_SCALE + 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(PERCENTAGE_SCALE, RoundingMode.HALF_UP);
    }
    
    public static BigDecimal calculatePnL(BigDecimal entryPrice, BigDecimal exitPrice, BigDecimal quantity, boolean isLong) {
        if (entryPrice == null || exitPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal priceDiff = isLong ? exitPrice.subtract(entryPrice) : entryPrice.subtract(exitPrice);
        return priceDiff.multiply(quantity).setScale(PRICE_SCALE, RoundingMode.HALF_UP);
    }
    
    public static BigDecimal roundPrice(BigDecimal price) {
        return price != null ? price.setScale(PRICE_SCALE, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }
    
    public static BigDecimal calculateWinRate(int winningTrades, int totalTrades) {
        if (totalTrades == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(winningTrades)
                .divide(BigDecimal.valueOf(totalTrades), PERCENTAGE_SCALE + 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(PERCENTAGE_SCALE, RoundingMode.HALF_UP);
    }
}
