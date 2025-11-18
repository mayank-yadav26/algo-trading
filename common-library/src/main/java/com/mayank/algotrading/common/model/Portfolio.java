package com.mayank.algotrading.common.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolios", indexes = {
    @Index(name = "idx_strategy_id", columnList = "strategy_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Portfolio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "strategy_id", nullable = false, unique = true)
    private Long strategyId;
    
    @Column(name = "initial_capital", nullable = false, precision = 19, scale = 4)
    private BigDecimal initialCapital;
    
    @Column(name = "current_capital", nullable = false, precision = 19, scale = 4)
    private BigDecimal currentCapital;
    
    @Column(name = "total_pnl", precision = 19, scale = 4)
    private BigDecimal totalPnl;
    
    @Column(name = "realized_pnl", precision = 19, scale = 4)
    private BigDecimal realizedPnl;
    
    @Column(name = "unrealized_pnl", precision = 19, scale = 4)
    private BigDecimal unrealizedPnl;
    
    @Column(name = "total_trades")
    private Integer totalTrades;
    
    @Column(name = "winning_trades")
    private Integer winningTrades;
    
    @Column(name = "losing_trades")
    private Integer losingTrades;
    
    @Column(name = "win_rate", precision = 5, scale = 2)
    private BigDecimal winRate;
    
    @Column(name = "total_commission", precision = 19, scale = 4)
    private BigDecimal totalCommission;
    
    @Column(name = "max_drawdown", precision = 19, scale = 4)
    private BigDecimal maxDrawdown;
    
    @Column(name = "sharpe_ratio", precision = 10, scale = 4)
    private BigDecimal sharpeRatio;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (totalPnl == null) totalPnl = BigDecimal.ZERO;
        if (realizedPnl == null) realizedPnl = BigDecimal.ZERO;
        if (unrealizedPnl == null) unrealizedPnl = BigDecimal.ZERO;
        if (totalTrades == null) totalTrades = 0;
        if (winningTrades == null) winningTrades = 0;
        if (losingTrades == null) losingTrades = 0;
        if (totalCommission == null) totalCommission = BigDecimal.ZERO;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
