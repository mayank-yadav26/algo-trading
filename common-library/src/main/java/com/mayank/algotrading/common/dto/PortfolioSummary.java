package com.mayank.algotrading.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSummary {
    private Long strategyId;
    private String strategyName;
    private BigDecimal initialCapital;
    private BigDecimal currentCapital;
    private BigDecimal totalPnl;
    private BigDecimal totalPnlPercentage;
    private BigDecimal realizedPnl;
    private BigDecimal unrealizedPnl;
    private Integer totalTrades;
    private Integer winningTrades;
    private Integer losingTrades;
    private BigDecimal winRate;
    private BigDecimal totalCommission;
    private BigDecimal maxDrawdown;
    private BigDecimal sharpeRatio;
}
