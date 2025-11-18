package com.mayank.algotrading.common.model;

import com.mayank.algotrading.common.enums.OrderSide;
import com.mayank.algotrading.common.enums.OrderStatus;
import com.mayank.algotrading.common.enums.OrderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trades", indexes = {
    @Index(name = "idx_symbol", columnList = "symbol"),
    @Index(name = "idx_strategy_id", columnList = "strategy_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "strategy_id", nullable = false)
    private Long strategyId;
    
    @Column(nullable = false)
    private String symbol;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderSide side; // BUY, SELL
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType type; // MARKET, LIMIT
    
    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal price;
    
    @Column(name = "executed_price", precision = 19, scale = 4)
    private BigDecimal executedPrice;
    
    @Column(name = "executed_quantity", precision = 19, scale = 8)
    private BigDecimal executedQuantity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status; // PENDING, EXECUTED, CANCELLED, REJECTED
    
    @Column(name = "stop_loss", precision = 19, scale = 4)
    private BigDecimal stopLoss;
    
    @Column(name = "take_profit", precision = 19, scale = 4)
    private BigDecimal takeProfit;
    
    @Column(name = "pnl", precision = 19, scale = 4)
    private BigDecimal pnl;
    
    @Column(name = "commission", precision = 19, scale = 4)
    private BigDecimal commission;
    
    @Column(name = "entry_time")
    private LocalDateTime entryTime;
    
    @Column(name = "exit_time")
    private LocalDateTime exitTime;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
