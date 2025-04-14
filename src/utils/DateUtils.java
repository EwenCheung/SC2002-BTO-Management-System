package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);

    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, formatter);
    }

    public static String formatDate(LocalDate date) {
        return date.format(formatter);
    }

    public static boolean isWithinPeriod(LocalDate start, LocalDate end, LocalDate checkDate) {
        return (checkDate.isEqual(start) || checkDate.isAfter(start)) &&
               (checkDate.isEqual(end) || checkDate.isBefore(end));
    }
}
