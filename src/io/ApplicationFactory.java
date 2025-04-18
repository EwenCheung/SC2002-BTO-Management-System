package io;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import models.Application;
import models.enums.ApplicationStatus;
import static utils.Constants.DATE_TIME_FORMAT;

public class ApplicationFactory {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    
    public static Application createApplication(String[] tokens) {
        // Format: Application ID,Applicant NRIC,Project Name,Unit Type,Status,Assigned Unit,Assigned Officer,Application Date,Last Updated,Remarks
        // First ensure we have the minimal required fields (first 5 fields)
        if (tokens.length < 5 || tokens[0].isEmpty() || tokens[1].isEmpty() || tokens[2].isEmpty() || tokens[3].isEmpty() || tokens[4].isEmpty()) {
            throw new IllegalArgumentException("Missing required fields for Application");
        }

        // Create a new tokens array with correct length if the original is too short
        String[] paddedTokens = new String[10];
        for (int i = 0; i < paddedTokens.length; i++) {
            if (i < tokens.length) {
                paddedTokens[i] = tokens[i];
            } else {
                paddedTokens[i] = ""; // Pad with empty strings for missing fields
            }
        }
        
        String id = paddedTokens[0];
        String applicantNric = paddedTokens[1];
        String projectName = paddedTokens[2];
        String unitType = paddedTokens[3];
        ApplicationStatus status = ApplicationStatus.valueOf(paddedTokens[4].toUpperCase());
        String assignedUnit = paddedTokens[5].isEmpty() ? null : paddedTokens[5];
        String assignedOfficer = paddedTokens[6].isEmpty() ? null : paddedTokens[6];
        
        // Use a more flexible date parsing approach
        LocalDateTime applicationDate = parseDateTime(paddedTokens[7]);
        LocalDateTime lastUpdated = parseDateTime(paddedTokens[8]);
        
        String remarks = paddedTokens[9].isEmpty() ? null : paddedTokens[9];

        return new Application(id, applicantNric, projectName, unitType, status,
                assignedUnit, assignedOfficer, applicationDate, lastUpdated, remarks);
    }
    
    // Helper method to parse dates with various formats
    private static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return LocalDateTime.now(); // Default to current time if no date is provided
        }
        
        // Try with the standard formatter first
        try {
            return LocalDateTime.parse(dateTimeStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            // Try with an alternative format that matches the CSV data
            try {
                DateTimeFormatter altFormatter = DateTimeFormatter.ofPattern("M/d/yyyy HH:mm:ss");
                return LocalDateTime.parse(dateTimeStr, altFormatter);
            } catch (DateTimeParseException e2) {
                System.err.println("Warning: Could not parse date '" + dateTimeStr + "', using current time instead.");
                return LocalDateTime.now(); // Fallback to current time if parsing fails
            }
        }
    }
}
