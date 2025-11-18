package com.mayank.algotrading.broker.service;

import com.mayank.algotrading.common.dto.PortfolioSummary;
import com.mayank.algotrading.common.model.Portfolio;
import com.mayank.algotrading.common.model.Trade;
import com.mayank.algotrading.common.util.CalculationUtil;
import com.mayank.algotrading.broker.repository.PortfolioRepository;
import com.mayank.algotrading.broker.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings({"null", "unused"})
@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {
    
    private final PortfolioRepository portfolioRepository;
    private final TradeRepository tradeRepository;
    
    @Transactional
    public Portfolio createPortfolio(Long strategyId, BigDecimal initialCapital) {
        Portfolio portfolio = Portfolio.builder()
                .strategyId(strategyId)
                .initialCapital(initialCapital)
                .currentCapital(initialCapital)
                .totalPnl(BigDecimal.ZERO)
                .realizedPnl(BigDecimal.ZERO)
                .unrealizedPnl(BigDecimal.ZERO)
                .totalTrades(0)
                .winningTrades(0)
                .losingTrades(0)
                .winRate(BigDecimal.ZERO)
                .totalCommission(BigDecimal.ZERO)
                .build();
        
        return portfolioRepository.save(portfolio);
    }
    
    @Transactional
    public void updatePortfolioAfterTrade(Long strategyId, Trade trade) {
        Portfolio portfolio = portfolioRepository.findByStrategyId(strategyId)
                .orElseGet(() -> createPortfolio(strategyId, BigDecimal.valueOf(10000)));
        
        // Update total trades
        portfolio.setTotalTrades(portfolio.getTotalTrades() + 1);
        
        // Update commission
        BigDecimal totalCommission = portfolio.getTotalCommission().add(trade.getCommission());
        portfolio.setTotalCommission(totalCommission);
        
        // If it's a closing trade (has PnL)
        if (trade.getPnl() != null) {
            BigDecimal pnl = trade.getPnl();
            
            // Update realized PnL
            portfolio.setRealizedPnl(portfolio.getRealizedPnl().add(pnl));
            
            // Update current capital
            portfolio.setCurrentCapital(portfolio.getCurrentCapital().add(pnl));
            
            // Update winning/losing trades
            if (pnl.compareTo(BigDecimal.ZERO) > 0) {
                portfolio.setWinningTrades(portfolio.getWinningTrades() + 1);
            } else if (pnl.compareTo(BigDecimal.ZERO) < 0) {
                portfolio.setLosingTrades(portfolio.getLosingTrades() + 1);
            }
            
            // Calculate win rate
            BigDecimal winRate = CalculationUtil.calculateWinRate(
                    portfolio.getWinningTrades(), 
                    portfolio.getTotalTrades());
            portfolio.setWinRate(winRate);
        }
        
        // Calculate total PnL
        portfolio.setTotalPnl(portfolio.getRealizedPnl().add(portfolio.getUnrealizedPnl()));
        
        portfolioRepository.save(portfolio);
        log.info("Portfolio updated for strategy {}: Total PnL = {}, Win Rate = {}%", 
                strategyId, portfolio.getTotalPnl(), portfolio.getWinRate());
    }
    
    public Portfolio getPortfolio(Long strategyId) {
        return portfolioRepository.findByStrategyId(strategyId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for strategy: " + strategyId));
    }
    
    public PortfolioSummary getPortfolioSummary(Long strategyId) {
        Portfolio portfolio = getPortfolio(strategyId);
        
        BigDecimal totalPnlPercentage = CalculationUtil.calculatePercentageChange(
                portfolio.getInitialCapital(), 
                portfolio.getCurrentCapital());
        
        return PortfolioSummary.builder()
                .strategyId(portfolio.getStrategyId())
                .initialCapital(portfolio.getInitialCapital())
                .currentCapital(portfolio.getCurrentCapital())
                .totalPnl(portfolio.getTotalPnl())
                .totalPnlPercentage(totalPnlPercentage)
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
    
    public List<Portfolio> getAllPortfolios() {
        return portfolioRepository.findAll();
    }
}
