package com.mayank.algotrading.common.model;

import com.mayank.algotrading.common.enums.PositionSide;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "positions", indexes = {
    @Index(name = "idx_strategy_symbol", columnList = "strategy_id,symbol"),
    @Index(name = "idx_is_open", columnList = "is_open")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "strategy_id", nullable = false)
    private Long strategyId;
    
    @Column(nullable = false)
    private String symbol;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PositionSide side; // LONG, SHORT
    
    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;
    
    @Column(name = "entry_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal entryPrice;
    
    @Column(name = "current_price", precision = 19, scale = 4)
    private BigDecimal currentPrice;
    
    @Column(name = "unrealized_pnl", precision = 19, scale = 4)
    private BigDecimal unrealizedPnl;
    
    @Column(name = "stop_loss", precision = 19, scale = 4)
    private BigDecimal stopLoss;
    
    @Column(name = "take_profit", precision = 19, scale = 4)
    private BigDecimal takeProfit;
    
    @Builder.Default
    @Column(name = "is_open", nullable = false)
    private Boolean isOpen = true;
    
    @Column(name = "entry_time")
    private LocalDateTime entryTime;
    
    @Column(name = "exit_time")
    private LocalDateTime exitTime;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (entryTime == null) entryTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
