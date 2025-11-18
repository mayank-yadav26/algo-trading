package com.mayank.algotrading.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BacktestResult {
    private Long strategyId;
    private String strategyName;
    private String symbol;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal initialCapital;
    private BigDecimal finalCapital;
    private BigDecimal totalReturn;
    private BigDecimal totalReturnPercentage;
    private Integer totalTrades;
    private Integer winningTrades;
    private Integer losingTrades;
    private BigDecimal winRate;
    private BigDecimal maxDrawdown;
    private BigDecimal sharpeRatio;
    private BigDecimal maxConsecutiveWins;
    private BigDecimal maxConsecutiveLosses;
    private BigDecimal averageWin;
    private BigDecimal averageLoss;
    private BigDecimal profitFactor;
}
