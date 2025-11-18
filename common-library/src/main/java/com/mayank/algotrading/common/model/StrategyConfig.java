package com.mayank.algotrading.common.model;

import com.mayank.algotrading.common.enums.StrategyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "strategy_configs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategyConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StrategyType type; // SMA, EMA, RSI, MACD, COMBINED
    
    @Column(nullable = false)
    private String symbol;
    
    @Column(nullable = false)
    private String timeframe;
    
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;
    
    @Column(name = "initial_capital", precision = 19, scale = 4)
    private BigDecimal initialCapital;
    
    @Column(name = "risk_per_trade", precision = 5, scale = 2)
    private BigDecimal riskPerTrade; // Percentage
    
    // Strategy parameters stored as JSON
    @Column(columnDefinition = "TEXT")
    private String parameters;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
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
