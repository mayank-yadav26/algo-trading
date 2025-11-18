package com.mayank.algotrading.marketdata.controller;

import com.mayank.algotrading.common.dto.CandleDTO;
import com.mayank.algotrading.common.model.Candle;
import com.mayank.algotrading.marketdata.service.MarketDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/market-data")
@RequiredArgsConstructor
@Tag(name = "Market Data", description = "Market data fetching and management APIs")
public class MarketDataController {
    
    private final MarketDataService marketDataService;
    
    @GetMapping("/quote/{symbol}")
    @Operation(summary = "Fetch real-time quote", description = "Fetches and saves real-time quote for a symbol")
    public ResponseEntity<CandleDTO> getRealTimeQuote(@PathVariable String symbol) {
        CandleDTO candle = marketDataService.fetchAndSaveRealTimeQuote(symbol);
        return ResponseEntity.ok(candle);
    }
    
    @GetMapping("/historical/{symbol}")
    @Operation(summary = "Fetch historical data", description = "Fetches and saves historical candles")
    public ResponseEntity<List<CandleDTO>> getHistoricalData(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "5m") String timeframe,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        
        List<CandleDTO> candles = marketDataService.fetchAndSaveHistoricalData(symbol, timeframe, from, to);
        return ResponseEntity.ok(candles);
    }
    
    @GetMapping("/candles/{symbol}")
    @Operation(summary = "Get stored candles", description = "Retrieves stored candles from database")
    public ResponseEntity<List<Candle>> getStoredCandles(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "5m") String timeframe,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        
        List<Candle> candles = marketDataService.getHistoricalCandles(symbol, timeframe, from, to);
        return ResponseEntity.ok(candles);
    }
    
    @GetMapping("/candles/{symbol}/recent")
    @Operation(summary = "Get recent candles", description = "Retrieves recent N candles")
    public ResponseEntity<List<Candle>> getRecentCandles(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "5m") String timeframe,
            @RequestParam(defaultValue = "100") int count) {
        
        List<Candle> candles = marketDataService.getRecentCandles(symbol, timeframe, count);
        return ResponseEntity.ok(candles);
    }
    
    @GetMapping("/candles/{symbol}/latest")
    @Operation(summary = "Get latest candle", description = "Retrieves the latest candle")
    public ResponseEntity<Candle> getLatestCandle(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "5m") String timeframe) {
        
        Candle candle = marketDataService.getLatestCandle(symbol, timeframe);
        return ResponseEntity.ok(candle);
    }
}
