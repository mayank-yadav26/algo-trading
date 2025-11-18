package com.mayank.algotrading.broker.controller;

import com.mayank.algotrading.common.dto.TradingSignal;
import com.mayank.algotrading.common.model.Position;
import com.mayank.algotrading.common.model.Trade;
import com.mayank.algotrading.broker.service.TradingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trading")
@RequiredArgsConstructor
@Tag(name = "Trading", description = "Virtual trading execution APIs")
public class TradingController {
    
    private final TradingService tradingService;
    
    @PostMapping("/execute")
    @Operation(summary = "Execute trade", description = "Executes a trade based on trading signal")
    public ResponseEntity<Trade> executeTrade(@RequestBody TradingSignal signal) {
        Trade trade = tradingService.executeTrade(signal);
        return ResponseEntity.ok(trade);
    }
    
    @GetMapping("/trades/strategy/{strategyId}")
    @Operation(summary = "Get trades by strategy", description = "Retrieves all trades for a strategy")
    public ResponseEntity<List<Trade>> getTradesByStrategy(@PathVariable Long strategyId) {
        return ResponseEntity.ok(tradingService.getTradesByStrategy(strategyId));
    }
    
    @GetMapping("/positions/strategy/{strategyId}")
    @Operation(summary = "Get open positions", description = "Retrieves all open positions for a strategy")
    public ResponseEntity<List<Position>> getOpenPositions(@PathVariable Long strategyId) {
        return ResponseEntity.ok(tradingService.getOpenPositions(strategyId));
    }
    
    @GetMapping("/trades/{tradeId}")
    @Operation(summary = "Get trade by ID", description = "Retrieves a specific trade")
    public ResponseEntity<Trade> getTradeById(@PathVariable Long tradeId) {
        return ResponseEntity.ok(tradingService.getTradeById(tradeId));
    }
}
