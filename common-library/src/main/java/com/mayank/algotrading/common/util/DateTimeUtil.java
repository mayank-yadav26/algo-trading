package com.mayank.algotrading.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static String formatToISO(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(ISO_FORMATTER) : null;
    }
    
    public static String formatForDisplay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DISPLAY_FORMATTER) : null;
    }
    
    public static LocalDateTime parseISO(String dateTimeString) {
        return dateTimeString != null ? LocalDateTime.parse(dateTimeString, ISO_FORMATTER) : null;
    }
}
