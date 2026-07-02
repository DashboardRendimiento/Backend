package com.rrhh.dashboard.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utilidades para manejo de fechas
 * 
 * @author Backend Dev 1
 */
public class DateUtils {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Convierte un String a LocalDate
     * 
     * @param dateStr String con formato "yyyy-MM-dd"
     * @return LocalDate o null si el formato es invalido
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Convierte un LocalDate a String
     * 
     * @param date LocalDate
     * @return String con formato "yyyy-MM-dd"
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * Convierte un LocalDateTime a String
     * 
     * @param dateTime LocalDateTime
     * @return String con formato "yyyy-MM-dd HH:mm:ss"
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    /**
     * Verifica si una fecha esta entre dos fechas
     * 
     * @param date Fecha a verificar
     * @param from Fecha inicio (inclusive)
     * @param to Fecha fin (inclusive)
     * @return true si esta en el rango
     */
    public static boolean isDateBetween(LocalDate date, LocalDate from, LocalDate to) {
        if (date == null) {
            return false;
        }
        if (from != null && date.isBefore(from)) {
            return false;
        }
        if (to != null && date.isAfter(to)) {
            return false;
        }
        return true;
    }
}
