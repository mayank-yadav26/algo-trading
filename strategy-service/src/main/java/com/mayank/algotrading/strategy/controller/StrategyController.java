package com.mayank.algotrading.strategy.controller;

import com.mayank.algotrading.common.dto.BacktestResult;
import com.mayank.algotrading.common.dto.TradingSignal;
import com.mayank.algotrading.common.model.StrategyConfig;
import com.mayank.algotrading.strategy.service.BacktestService;
import com.mayank.algotrading.strategy.service.StrategyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/strategies")
@RequiredArgsConstructor
@Tag(name = "Strategy", description = "Trading strategy management and signal generation APIs")
public class StrategyController {
    
    private final StrategyService strategyService;
    private final BacktestService backtestService;
    
    @PostMapping
    @Operation(summary = "Create strategy", description = "Creates a new trading strategy configuration")
    public ResponseEntity<StrategyConfig> createStrategy(@RequestBody StrategyConfig config) {
        return ResponseEntity.ok(strategyService.createStrategy(config));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update strategy", description = "Updates an existing trading strategy")
    public ResponseEntity<StrategyConfig> updateStrategy(@PathVariable Long id, 
                                                         @RequestBody StrategyConfig config) {
        return ResponseEntity.ok(strategyService.updateStrategy(id, config));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete strategy", description = "Deletes a trading strategy")
    public ResponseEntity<Void> deleteStrategy(@PathVariable Long id) {
        strategyService.deleteStrategy(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    @Operation(summary = "Get all strategies", description = "Retrieves all trading strategies")
    public ResponseEntity<List<StrategyConfig>> getAllStrategies() {
        return ResponseEntity.ok(strategyService.getAllStrategies());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get strategy by ID", description = "Retrieves a specific trading strategy")
    public ResponseEntity<StrategyConfig> getStrategy(@PathVariable Long id) {
        return ResponseEntity.ok(strategyService.getStrategyById(id));
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active strategies", description = "Retrieves all active trading strategies")
    public ResponseEntity<List<StrategyConfig>> getActiveStrategies() {
        return ResponseEntity.ok(strategyService.getActiveStrategies());
    }
    
    @GetMapping("/{id}/signal")
    @Operation(summary = "Generate trading signal", description = "Generates a trading signal based on current market data")
    public ResponseEntity<TradingSignal> generateSignal(@PathVariable Long id) {
        return ResponseEntity.ok(strategyService.generateSignal(id));
    }
    
    @PostMapping("/{id}/backtest")
    @Operation(summary = "Run backtest", description = "Runs a backtest on historical data")
    public ResponseEntity<BacktestResult> runBacktest(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        return ResponseEntity.ok(backtestService.runBacktest(id, startDate, endDate));
    }
}
