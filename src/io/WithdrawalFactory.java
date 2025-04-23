package io;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import models.WithdrawalRequest;
import models.enums.WithdrawalStatus;
import static utils.Constants.DATE_TIME_FORMAT;

/**
 * Factory class responsible for creating WithdrawalRequest objects from CSV data.
 * Parses string arrays into properly typed withdrawal request objects for the system.
 */
public class WithdrawalFactory {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    
    /**
     * Creates a WithdrawalRequest object from a string array of CSV data.
     * 
     * @param tokens The array of strings containing the withdrawal request data
     * @return A new WithdrawalRequest object with data from the input strings
     * @throws IllegalArgumentException if any required field is missing or empty
     */
    public static WithdrawalRequest createRequest(String[] tokens) {
        // Format: Request ID,Application ID,Applicant NRIC,Project Name,Status,Request Date,Process Date,Remarks
        if (tokens.length < 8 || tokens[0].isEmpty() || tokens[1].isEmpty() || tokens[2].isEmpty() || tokens[3].isEmpty() || tokens[4].isEmpty()) {
            throw new IllegalArgumentException("Missing required fields for Withdrawal Request");
        }

        String requestId = tokens[0];
        String applicationId = tokens[1];
        String applicantNric = tokens[2];
        String projectName = tokens[3];
        WithdrawalStatus status = WithdrawalStatus.valueOf(tokens[4].toUpperCase());
        LocalDateTime requestDate = tokens[5].isEmpty() ? null : LocalDateTime.parse(tokens[5], DATE_FORMATTER);
        LocalDateTime processDate = tokens[6].isEmpty() ? null : LocalDateTime.parse(tokens[6], DATE_FORMATTER);
        String remarks = tokens[7].isEmpty() ? null : tokens[7];

        return new WithdrawalRequest(requestId, applicationId, applicantNric, projectName, status, requestDate, processDate, remarks);
    }
}
