package com.mayank.algotrading.broker.controller;

import com.mayank.algotrading.common.dto.PortfolioSummary;
import com.mayank.algotrading.common.model.Portfolio;
import com.mayank.algotrading.broker.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
@Tag(name = "Portfolio", description = "Portfolio management and reporting APIs")
public class PortfolioController {
    
    private final PortfolioService portfolioService;
    
    @PostMapping("/create")
    @Operation(summary = "Create portfolio", description = "Creates a new portfolio for a strategy")
    public ResponseEntity<Portfolio> createPortfolio(@RequestParam Long strategyId,
                                                     @RequestParam BigDecimal initialCapital) {
        return ResponseEntity.ok(portfolioService.createPortfolio(strategyId, initialCapital));
    }
    
    @GetMapping("/strategy/{strategyId}")
    @Operation(summary = "Get portfolio", description = "Retrieves portfolio for a strategy")
    public ResponseEntity<Portfolio> getPortfolio(@PathVariable Long strategyId) {
        return ResponseEntity.ok(portfolioService.getPortfolio(strategyId));
    }
    
    @GetMapping("/strategy/{strategyId}/summary")
    @Operation(summary = "Get portfolio summary", description = "Retrieves portfolio summary with calculated metrics")
    public ResponseEntity<PortfolioSummary> getPortfolioSummary(@PathVariable Long strategyId) {
        return ResponseEntity.ok(portfolioService.getPortfolioSummary(strategyId));
    }
    
    @GetMapping("/all")
    @Operation(summary = "Get all portfolios", description = "Retrieves all portfolios")
    public ResponseEntity<List<Portfolio>> getAllPortfolios() {
        return ResponseEntity.ok(portfolioService.getAllPortfolios());
    }
}
