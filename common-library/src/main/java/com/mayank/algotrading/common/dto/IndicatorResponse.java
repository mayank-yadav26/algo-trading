package com.mayank.algotrading.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndicatorResponse {
    private String symbol;
    private String timeframe;
    private Map<String, BigDecimal> indicators;
    private String timestamp;
}
