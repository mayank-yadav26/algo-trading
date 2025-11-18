package com.mayank.algotrading.dashboard.controller;

import com.mayank.algotrading.common.dto.PortfolioSummary;
import com.mayank.algotrading.common.model.Portfolio;
import com.mayank.algotrading.common.model.Position;
import com.mayank.algotrading.common.model.StrategyConfig;
import com.mayank.algotrading.common.model.Trade;
import com.mayank.algotrading.dashboard.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("null")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard aggregation and reporting APIs")
public class DashboardController {
    
    private final StrategyConfigRepository strategyConfigRepository;
    private final PortfolioRepository portfolioRepository;
    private final TradeRepository tradeRepository;
    private final PositionRepository positionRepository;
    
    @GetMapping("/overview")
    @Operation(summary = "Get system overview", description = "Returns overall system statistics")
    public ResponseEntity<Map<String, Object>> getSystemOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        List<StrategyConfig> allStrategies = strategyConfigRepository.findAll();
        List<StrategyConfig> activeStrategies = strategyConfigRepository.findByIsActiveTrue();
        List<Portfolio> portfolios = portfolioRepository.findAll();
        List<Position> openPositions = positionRepository.findByIsOpenTrue();
        
        overview.put("totalStrategies", allStrategies.size());
        overview.put("activeStrategies", activeStrategies.size());
        overview.put("totalPortfolios", portfolios.size());
        overview.put("openPositions", openPositions.size());
        
        return ResponseEntity.ok(overview);
    }
    
    @GetMapping("/portfolios")
    @Operation(summary = "Get all portfolio summaries", description = "Returns summary for all portfolios")
    public ResponseEntity<List<PortfolioSummary>> getAllPortfolioSummaries() {
        List<Portfolio> portfolios = portfolioRepository.findAll();
        
        List<PortfolioSummary> summaries = portfolios.stream()
                .map(this::buildPortfolioSummary)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(summaries);
    }
    
    @GetMapping("/strategy/{strategyId}/performance")
    @Operation(summary = "Get strategy performance", description = "Returns detailed performance metrics for a strategy")
    public ResponseEntity<Map<String, Object>> getStrategyPerformance(@PathVariable Long strategyId) {
        Map<String, Object> performance = new HashMap<>();
        
        StrategyConfig strategy = strategyConfigRepository.findById(strategyId)
                .orElseThrow(() -> new RuntimeException("Strategy not found"));
        
        Portfolio portfolio = portfolioRepository.findByStrategyId(strategyId)
                .orElse(null);
        
        List<Trade> trades = tradeRepository.findByStrategyIdOrderByCreatedAtDesc(strategyId);
        List<Position> openPositions = positionRepository.findByStrategyIdAndIsOpenTrue(strategyId);
        
        performance.put("strategy", strategy);
        performance.put("portfolio", portfolio);
        performance.put("recentTrades", trades.stream().limit(10).collect(Collectors.toList()));
        performance.put("openPositions", openPositions);
        
        return ResponseEntity.ok(performance);
    }
    
    @GetMapping("/trades/recent")
    @Operation(summary = "Get recent trades", description = "Returns most recent trades across all strategies")
    public ResponseEntity<List<Trade>> getRecentTrades(@RequestParam(defaultValue = "20") int limit) {
        List<Trade> trades = tradeRepository.findAll().stream()
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .limit(limit)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(trades);
    }
    
    @GetMapping("/positions/open")
    @Operation(summary = "Get all open positions", description = "Returns all open positions across all strategies")
    public ResponseEntity<List<Position>> getAllOpenPositions() {
        return ResponseEntity.ok(positionRepository.findByIsOpenTrue());
    }
    
    private PortfolioSummary buildPortfolioSummary(Portfolio portfolio) {
        StrategyConfig strategy = strategyConfigRepository.findById(portfolio.getStrategyId())
                .orElse(null);
        
        return PortfolioSummary.builder()
                .strategyId(portfolio.getStrategyId())
                .strategyName(strategy != null ? strategy.getName() : "Unknown")
                .initialCapital(portfolio.getInitialCapital())
                .currentCapital(portfolio.getCurrentCapital())
                .totalPnl(portfolio.getTotalPnl())
                .realizedPnl(portfolio.getRealizedPnl())
                .unrealizedPnl(portfolio.getUnrealizedPnl())
                .totalTrades(portfolio.getTotalTrades())
                .winningTrades(portfolio.getWinningTrades())
                .losingTrades(portfolio.getLosingTrades())
                .winRate(portfolio.getWinRate())
                .totalCommission(portfolio.getTotalCommission())
                .maxDrawdown(portfolio.getMaxDrawdown())
                .sharpeRatio(portfolio.getSharpeRatio())
                .build();
    }
}
