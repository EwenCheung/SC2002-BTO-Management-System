package io;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import models.Project;
import utils.Constants;

public class ProjectFactory {
    public static Project createProject(String[] tokens) {
        // Format: Project Name, Neighborhood, Type 1, Number of Units for Type 1, Available Units for Type 1, Selling price for Type 1, 
        //         Type 2, Number of Units for Type 2, Available Units for Type 2, Selling price for Type 2,
        //         Application opening date, Application closing date, Manager, Officer Slot, Officer(s), Visibility
        if (tokens.length != 16) {
            throw new IllegalArgumentException("Project data must have 16 fields but has " + tokens.length);
        }

        String name = tokens[0];
        String neighborhood = tokens[1];
        
        // Create the project first
        LocalDate openDate = parseDate(tokens[10]);
        LocalDate closeDate = parseDate(tokens[11]);
        String managerNric = tokens[12]; // Manager
        int officerSlot = parseIntSafely(tokens[13], 0); // Officer Slot (default to 0 if empty)
        List<String> officers = tokens[14].isEmpty() ? new ArrayList<>() : Arrays.asList(tokens[14].split(";"));
        boolean visible = Boolean.parseBoolean(tokens[15]); // Visibility

        Project project = new Project(name, neighborhood, openDate, closeDate, managerNric, officerSlot);
        
        // Type 1 info - handle empty values safely
        String type1 = tokens[2];
        if (!type1.isEmpty()) {
            int num1 = parseIntSafely(tokens[3], 0);
            int avail1 = parseIntSafely(tokens[4], 0);
            double price1 = parseDoubleSafely(tokens[5], 0.0);
            project.addUnitType(type1, num1, price1);
            project.setAvailableUnits(type1, avail1);
        }
        
        // Type 2 info - handle empty values safely
        String type2 = tokens[6];
        if (!type2.isEmpty()) {
            int num2 = parseIntSafely(tokens[7], 0);
            int avail2 = parseIntSafely(tokens[8], 0);
            double price2 = parseDoubleSafely(tokens[9], 0.0);
            project.addUnitType(type2, num2, price2);
            project.setAvailableUnits(type2, avail2);
        }

        for (String officer : officers) {
            if (!officer.isEmpty()) {
                project.addOfficer(officer);
            }
        }

        project.setVisible(visible);
        return project;
    }
    
    /**
     * Safely parse an integer from a string, returning a default value if parsing fails
     * @param value The string to parse
     * @param defaultValue The default value to return if parsing fails
     * @return The parsed integer or the default value
     */
    private static int parseIntSafely(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            System.err.println("Warning: Could not parse integer from '" + value + "', using default: " + defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * Safely parse a double from a string, returning a default value if parsing fails
     * @param value The string to parse
     * @param defaultValue The default value to return if parsing fails
     * @return The parsed double or the default value
     */
    private static double parseDoubleSafely(String value, double defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            System.err.println("Warning: Could not parse double from '" + value + "', using default: " + defaultValue);
            return defaultValue;
        }
    }
    
    // Helper method to parse dates with various formats
    private static LocalDate parseDate(String dateStr) {
        // Try first with the full date-time format
        try {
            DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);
            return LocalDate.parse(dateStr, fullFormatter);
        } catch (DateTimeParseException e) {
            // If that fails, try with just the date format (no time)
            try {
                DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
                return LocalDate.parse(dateStr, dateOnlyFormatter);
            } catch (DateTimeParseException e2) {
                // As a last resort, try standard ISO format
                try {
                    return LocalDate.parse(dateStr);
                } catch (DateTimeParseException e3) {
                    throw new IllegalArgumentException("Cannot parse date: " + dateStr, e3);
                }
            }
        }
    }
}
