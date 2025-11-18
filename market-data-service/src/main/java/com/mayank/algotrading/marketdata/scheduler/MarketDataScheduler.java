package com.mayank.algotrading.marketdata.scheduler;

import com.mayank.algotrading.marketdata.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketDataScheduler {
    
    private final MarketDataService marketDataService;
    
    @Value("${market-data.schedule.enabled:false}")
    private boolean schedulingEnabled;
    
    @Value("${market-data.schedule.symbols}")
    private String symbolsConfig;
    
    @Value("${market-data.schedule.timeframe:5}")
    private String timeframe;
    
    @Scheduled(cron = "${market-data.schedule.cron:0 */5 * * * *}")
    public void fetchMarketData() {
        if (!schedulingEnabled) {
            return;
        }
        
        log.info("Starting scheduled market data fetch...");
        List<String> symbols = Arrays.asList(symbolsConfig.split(","));
        
        for (String symbol : symbols) {
            try {
                marketDataService.fetchAndSaveRealTimeQuote(symbol.trim());
            } catch (Exception e) {
                log.error("Error fetching data for symbol {}: {}", symbol, e.getMessage());
            }
        }
        
        log.info("Completed scheduled market data fetch for {} symbols", symbols.size());
    }
}
