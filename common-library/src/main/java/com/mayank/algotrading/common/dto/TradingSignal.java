package com.mayank.algotrading.common.dto;

import com.mayank.algotrading.common.enums.SignalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradingSignal {
    private Long strategyId;
    private String symbol;
    private SignalType signal;
    private BigDecimal price;
    private BigDecimal suggestedQuantity;
    private BigDecimal stopLoss;
    private BigDecimal takeProfit;
    private LocalDateTime timestamp;
    private String reason;
    private Map<String, BigDecimal> indicators;
}
