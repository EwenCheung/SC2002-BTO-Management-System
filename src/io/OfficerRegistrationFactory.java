package io;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import models.OfficerRegistration;
import models.enums.OfficerRegistrationStatus;
import static utils.Constants.DATE_TIME_FORMAT;

/**
 * Factory class responsible for creating OfficerRegistration objects from CSV data.
 * Converts string arrays from CSV files into properly typed OfficerRegistration objects.
 */
public class OfficerRegistrationFactory {
    /** Date formatter used for parsing registration dates in the standard system format */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    /**
     * Creates an OfficerRegistration object from a string array of CSV data.
     * 
     * @param tokens The array of strings containing the officer registration data
     * @return A new OfficerRegistration object with data from the input strings
     * @throws IllegalArgumentException if any required field is missing or empty
     */
    public static OfficerRegistration createRegistration(String[] tokens) {
        // Format: Registration ID,Officer NRIC,Project Name,Status,Registration Date
        if (tokens.length < 5 || tokens[0].isEmpty() || tokens[1].isEmpty() || tokens[2].isEmpty() || tokens[3].isEmpty()) {
            throw new IllegalArgumentException("Missing required fields for Officer Registration");
        }

        String registrationId = tokens[0];
        String officerNric = tokens[1];
        String projectName = tokens[2];
        OfficerRegistrationStatus status = OfficerRegistrationStatus.valueOf(tokens[3].toUpperCase());
        LocalDateTime registrationDate = tokens[4].isEmpty() ? null : LocalDateTime.parse(tokens[4], DATE_FORMATTER);

        return new OfficerRegistration(registrationId, officerNric, projectName, status, registrationDate);
    }
}