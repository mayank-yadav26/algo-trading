package com.mayank.algotrading.marketdata.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayank.algotrading.common.dto.CandleDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FinnhubClient implements MarketDataClient {
    
    @Value("${market-data.api.finnhub.api-key}")
    private String apiKey;
    
    @Value("${market-data.api.finnhub.base-url}")
    private String baseUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public FinnhubClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public CandleDTO fetchRealTimeQuote(String symbol) {
        try {
            String url = String.format("%s/quote?symbol=%s&token=%s", baseUrl, symbol, apiKey);
            @SuppressWarnings("null")
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            
            BigDecimal currentPrice = BigDecimal.valueOf(jsonNode.get("c").asDouble());
            BigDecimal open = BigDecimal.valueOf(jsonNode.get("o").asDouble());
            BigDecimal high = BigDecimal.valueOf(jsonNode.get("h").asDouble());
            BigDecimal low = BigDecimal.valueOf(jsonNode.get("l").asDouble());
            // previousClose not used currently
            // BigDecimal previousClose = BigDecimal.valueOf(jsonNode.get("pc").asDouble());
            
            return CandleDTO.builder()
                    .symbol(symbol)
                    .timestamp(LocalDateTime.now())
                    .open(open)
                    .high(high)
                    .low(low)
                    .close(currentPrice)
                    .volume(0L) // Finnhub quote doesn't include volume
                    .timeframe("1m")
                    .build();
                    
        } catch (Exception e) {
            log.error("Error fetching real-time quote for {}: {}", symbol, e.getMessage());
            return null;
        }
    }
    
    @Override
    public List<CandleDTO> fetchHistoricalCandles(String symbol, String timeframe, 
                                                   LocalDateTime from, LocalDateTime to) {
        List<CandleDTO> candles = new ArrayList<>();
        try {
            long fromTimestamp = from.atZone(ZoneId.systemDefault()).toEpochSecond();
            long toTimestamp = to.atZone(ZoneId.systemDefault()).toEpochSecond();
            String resolution = mapTimeframeToResolution(timeframe);
            
            String url = String.format("%s/stock/candle?symbol=%s&resolution=%s&from=%d&to=%d&token=%s",
                    baseUrl, symbol, resolution, fromTimestamp, toTimestamp, apiKey);
            
            @SuppressWarnings("null")
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            
            if (jsonNode.has("s") && "ok".equals(jsonNode.get("s").asText())) {
                JsonNode timestamps = jsonNode.get("t");
                JsonNode opens = jsonNode.get("o");
                JsonNode highs = jsonNode.get("h");
                JsonNode lows = jsonNode.get("l");
                JsonNode closes = jsonNode.get("c");
                JsonNode volumes = jsonNode.get("v");
                
                for (int i = 0; i < timestamps.size(); i++) {
                    LocalDateTime timestamp = LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(timestamps.get(i).asLong()), ZoneId.systemDefault());
                    
                    candles.add(CandleDTO.builder()
                            .symbol(symbol)
                            .timestamp(timestamp)
                            .open(BigDecimal.valueOf(opens.get(i).asDouble()))
                            .high(BigDecimal.valueOf(highs.get(i).asDouble()))
                            .low(BigDecimal.valueOf(lows.get(i).asDouble()))
                            .close(BigDecimal.valueOf(closes.get(i).asDouble()))
                            .volume(volumes.get(i).asLong())
                            .timeframe(timeframe)
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("Error fetching historical candles for {}: {}", symbol, e.getMessage());
        }
        return candles;
    }
    
    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your_finnhub_api_key");
    }
    
    private String mapTimeframeToResolution(String timeframe) {
        // Finnhub resolutions: 1, 5, 15, 30, 60, D, W, M
        return switch (timeframe) {
            case "1m" -> "1";
            case "5m" -> "5";
            case "15m" -> "15";
            case "30m" -> "30";
            case "1h" -> "60";
            case "1d" -> "D";
            default -> "5";
        };
    }
}
