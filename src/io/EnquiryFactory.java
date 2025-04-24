package io;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import models.Enquiry;
import utils.FileUtils;
import static utils.Constants.DATE_TIME_FORMAT;

/**
 * Factory class responsible for creating Enquiry objects from CSV data.
 * Parses string arrays from CSV files into properly typed Enquiry objects,
 * handling both required and optional fields safely.
 */
public class EnquiryFactory {
    /** Date formatter used for parsing timestamp and response date fields in the standard system format */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    /**
     * Creates an Enquiry object from a string array of CSV data.
     * Handles required fields and optional fields like response information.
     * 
     * @param tokens The array of strings containing the enquiry data
     * @return A new Enquiry object with data from the input strings
     * @throws IllegalArgumentException if any required field is missing or empty
     */
    public static Enquiry createEnquiry(String[] tokens) {
        // Format: Enquiry ID,NRIC,Project Name,Enquiry,Response,Timestamp,Respondent NRIC,Response Date
        
        // Ensure we have the minimum required tokens
        if (tokens.length < 4) {
            throw new IllegalArgumentException("Missing required fields for Enquiry");
        }
        
        // Ensure the first 4 required fields are not empty
        if (tokens[0].isEmpty() || tokens[1].isEmpty() || tokens[2].isEmpty() || tokens[3].isEmpty()) {
            throw new IllegalArgumentException("Missing required fields for Enquiry");
        }

        String enquiryId = tokens[0];
        String nric = tokens[1];
        String projectName = tokens[2];
        String enquiryText = FileUtils.unescapeCsvField(tokens[3]);
        
        // Handle optional fields safely
        String responseText = (tokens.length > 4 && !tokens[4].isEmpty()) ? FileUtils.unescapeCsvField(tokens[4]) : null;
        LocalDateTime timestamp = (tokens.length > 5 && !tokens[5].isEmpty()) ? LocalDateTime.parse(tokens[5], DATE_FORMATTER) : LocalDateTime.now();
        String respondentNric = (tokens.length > 6 && !tokens[6].isEmpty()) ? tokens[6] : null;
        LocalDateTime responseDate = (tokens.length > 7 && !tokens[7].isEmpty()) ? LocalDateTime.parse(tokens[7], DATE_FORMATTER) : null;

        return new Enquiry(enquiryId, nric, projectName, enquiryText, responseText, timestamp, respondentNric, responseDate);
    }
}
