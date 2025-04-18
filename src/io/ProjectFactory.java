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
        
        // Type 1 info
        String type1 = tokens[2];
        int num1 = Integer.parseInt(tokens[3]);
        int avail1 = Integer.parseInt(tokens[4]);
        double price1 = Double.parseDouble(tokens[5]);
        
        // Type 2 info
        String type2 = tokens[6];
        int num2 = Integer.parseInt(tokens[7]);
        int avail2 = Integer.parseInt(tokens[8]);
        double price2 = Double.parseDouble(tokens[9]);

        // Create a more flexible approach to date parsing
        LocalDate openDate = parseDate(tokens[10]);
        LocalDate closeDate = parseDate(tokens[11]);
        
        String managerNric = tokens[12]; // Manager
        int officerSlot = Integer.parseInt(tokens[13]); // Officer Slot
        List<String> officers = tokens[14].isEmpty() ? new ArrayList<>() : Arrays.asList(tokens[14].split(";"));
        boolean visible = Boolean.parseBoolean(tokens[15]); // Visibility

        Project project = new Project(name, neighborhood, openDate, closeDate, managerNric, officerSlot);
        
        // Add unit types
        project.addUnitType(type1, num1, price1);
        project.setAvailableUnits(type1, avail1);
        
        project.addUnitType(type2, num2, price2);
        project.setAvailableUnits(type2, avail2);

        for (String officer : officers) {
            if (!officer.isEmpty()) {
                project.addOfficer(officer);
            }
        }

        project.setVisible(visible);
        return project;
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
