package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class providing date formatting and validation operations.
 * Centralizes date-related operations to maintain consistent date handling across the application.
 */
public class DateUtils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);

    /**
     * Parses a date string into a LocalDate object using the system's standard format.
     * 
     * @param dateStr The date string to parse
     * @return A LocalDate object representing the parsed date
     * @throws java.time.format.DateTimeParseException if the date string cannot be parsed
     */
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, formatter);
    }

    /**
     * Formats a LocalDate object into a string using the system's standard format.
     * 
     * @param date The LocalDate object to format
     * @return A formatted date string
     */
    public static String formatDate(LocalDate date) {
        return date.format(formatter);
    }

    /**
     * Checks if a date falls within a specified period (inclusive of start and end dates).
     * 
     * @param start The start date of the period
     * @param end The end date of the period
     * @param checkDate The date to check
     * @return true if the date is within the period; false otherwise
     */
    public static boolean isWithinPeriod(LocalDate start, LocalDate end, LocalDate checkDate) {
        return (checkDate.isEqual(start) || checkDate.isAfter(start)) &&
               (checkDate.isEqual(end) || checkDate.isBefore(end));
    }
}
