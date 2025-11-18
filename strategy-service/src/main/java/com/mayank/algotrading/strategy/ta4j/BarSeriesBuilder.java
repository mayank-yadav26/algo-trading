package com.mayank.algotrading.strategy.ta4j;

import com.mayank.algotrading.common.model.Candle;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class BarSeriesBuilder {
    
    public static BarSeries buildSeries(List<Candle> candles, String name) {
        BarSeries series = new BaseBarSeries(name);
        
        for (Candle candle : candles) {
            ZonedDateTime endTime = candle.getTimestamp().atZone(ZoneId.systemDefault());
            
            // TA4J 0.15 addBar method
            series.addBar(
                    endTime,
                    candle.getOpen(),
                    candle.getHigh(),
                    candle.getLow(),
                    candle.getClose(),
                    candle.getVolume()
            );
        }
        
        return series;
    }
}
