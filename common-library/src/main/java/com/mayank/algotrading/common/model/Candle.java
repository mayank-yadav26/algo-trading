package com.mayank.algotrading.common.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "candles", indexes = {
    @Index(name = "idx_symbol_timestamp", columnList = "symbol,timestamp"),
    @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Candle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String symbol;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal open;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal high;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal low;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal close;
    
    @Column(nullable = false)
    private Long volume;
    
    @Column(name = "timeframe")
    private String timeframe; // 1m, 5m, 15m, 1h, 1d
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
