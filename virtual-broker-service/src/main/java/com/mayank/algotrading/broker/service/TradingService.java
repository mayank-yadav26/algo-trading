package com.mayank.algotrading.broker.service;

import com.mayank.algotrading.common.dto.TradingSignal;
import com.mayank.algotrading.common.enums.OrderSide;
import com.mayank.algotrading.common.enums.OrderStatus;
import com.mayank.algotrading.common.enums.OrderType;
import com.mayank.algotrading.common.enums.PositionSide;
import com.mayank.algotrading.common.enums.SignalType;
import com.mayank.algotrading.common.model.Position;
import com.mayank.algotrading.common.model.Trade;
import com.mayank.algotrading.broker.repository.PositionRepository;
import com.mayank.algotrading.broker.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("null")
@Slf4j
@Service
@RequiredArgsConstructor
public class TradingService {
    
    private final TradeRepository tradeRepository;
    private final PositionRepository positionRepository;
    private final PortfolioService portfolioService;
    
    @Value("${broker.commission.rate:0.001}")
    private BigDecimal commissionRate;
    
    @Value("${broker.slippage.percentage:0.0005}")
    private BigDecimal slippagePercentage;
    
    @Transactional
    public Trade executeTrade(TradingSignal signal) {
        log.info("Executing trade for signal: {}", signal);
        
        if (signal.getSignal() == SignalType.BUY) {
            return executeBuyOrder(signal);
        } else if (signal.getSignal() == SignalType.SELL) {
            return executeSellOrder(signal);
        }
        
        return null;
    }
    
    private Trade executeBuyOrder(TradingSignal signal) {
        // Check if there's an open position
        Position existingPosition = positionRepository
                .findByStrategyIdAndSymbolAndIsOpenTrue(signal.getStrategyId(), signal.getSymbol())
                .orElse(null);
        
        if (existingPosition != null) {
            log.warn("Position already open for {} in strategy {}", signal.getSymbol(), signal.getStrategyId());
            return null;
        }
        
        // Calculate execution price with slippage
        BigDecimal executionPrice = signal.getPrice()
                .multiply(BigDecimal.ONE.add(slippagePercentage))
                .setScale(4, RoundingMode.HALF_UP);
        
        // Calculate quantity
        BigDecimal quantity = signal.getSuggestedQuantity() != null ? 
                signal.getSuggestedQuantity() : BigDecimal.ONE;
        
        // Calculate commission
        BigDecimal tradeValue = executionPrice.multiply(quantity);
        BigDecimal commission = tradeValue.multiply(commissionRate).setScale(4, RoundingMode.HALF_UP);
        
        // Create trade
        Trade trade = Trade.builder()
                .strategyId(signal.getStrategyId())
                .symbol(signal.getSymbol())
                .side(OrderSide.BUY)
                .type(OrderType.MARKET)
                .quantity(quantity)
                .price(signal.getPrice())
                .executedPrice(executionPrice)
                .executedQuantity(quantity)
                .status(OrderStatus.EXECUTED)
                .stopLoss(signal.getStopLoss())
                .takeProfit(signal.getTakeProfit())
                .commission(commission)
                .entryTime(LocalDateTime.now())
                .notes("Executed from trading signal")
                .build();
        
        trade = tradeRepository.save(trade);
        
        // Create position
        Position position = Position.builder()
                .strategyId(signal.getStrategyId())
                .symbol(signal.getSymbol())
                .side(PositionSide.LONG)
                .quantity(quantity)
                .entryPrice(executionPrice)
                .currentPrice(executionPrice)
                .stopLoss(signal.getStopLoss())
                .takeProfit(signal.getTakeProfit())
                .isOpen(true)
                .entryTime(LocalDateTime.now())
                .build();
        
        positionRepository.save(position);
        
        // Update portfolio
        portfolioService.updatePortfolioAfterTrade(signal.getStrategyId(), trade);
        
        log.info("Buy order executed: {} @ {} for strategy {}", 
                signal.getSymbol(), executionPrice, signal.getStrategyId());
        
        return trade;
    }
    
    private Trade executeSellOrder(TradingSignal signal) {
        // Find open position
        Position position = positionRepository
                .findByStrategyIdAndSymbolAndIsOpenTrue(signal.getStrategyId(), signal.getSymbol())
                .orElse(null);
        
        if (position == null) {
            log.warn("No open position to close for {} in strategy {}", 
                    signal.getSymbol(), signal.getStrategyId());
            return null;
        }
        
        // Calculate execution price with slippage (negative for sells)
        BigDecimal executionPrice = signal.getPrice()
                .multiply(BigDecimal.ONE.subtract(slippagePercentage))
                .setScale(4, RoundingMode.HALF_UP);
        
        // Calculate commission
        BigDecimal tradeValue = executionPrice.multiply(position.getQuantity());
        BigDecimal commission = tradeValue.multiply(commissionRate).setScale(4, RoundingMode.HALF_UP);
        
        // Calculate PnL
        BigDecimal pnl = executionPrice.subtract(position.getEntryPrice())
                .multiply(position.getQuantity())
                .subtract(commission)
                .setScale(4, RoundingMode.HALF_UP);
        
        // Create trade
        Trade trade = Trade.builder()
                .strategyId(signal.getStrategyId())
                .symbol(signal.getSymbol())
                .side(OrderSide.SELL)
                .type(OrderType.MARKET)
                .quantity(position.getQuantity())
                .price(signal.getPrice())
                .executedPrice(executionPrice)
                .executedQuantity(position.getQuantity())
                .status(OrderStatus.EXECUTED)
                .commission(commission)
                .pnl(pnl)
                .entryTime(position.getEntryTime())
                .exitTime(LocalDateTime.now())
                .notes("Position closed from trading signal")
                .build();
        
        trade = tradeRepository.save(trade);
        
        // Close position
        position.setIsOpen(false);
        position.setExitTime(LocalDateTime.now());
        position.setCurrentPrice(executionPrice);
        positionRepository.save(position);
        
        // Update portfolio
        portfolioService.updatePortfolioAfterTrade(signal.getStrategyId(), trade);
        
        log.info("Sell order executed: {} @ {} for strategy {}. PnL: {}", 
                signal.getSymbol(), executionPrice, signal.getStrategyId(), pnl);
        
        return trade;
    }
    
    public List<Trade> getTradesByStrategy(Long strategyId) {
        return tradeRepository.findByStrategyIdOrderByCreatedAtDesc(strategyId);
    }
    
    public List<Position> getOpenPositions(Long strategyId) {
        return positionRepository.findByStrategyIdAndIsOpenTrue(strategyId);
    }
    
    public Trade getTradeById(Long tradeId) {
        return tradeRepository.findById(tradeId)
                .orElseThrow(() -> new RuntimeException("Trade not found"));
    }
}
