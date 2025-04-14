package io;

import models.WithdrawalRequest;
import models.enums.ApplicationStatus;
import models.enums.WithdrawalStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class WithdrawalFactory {
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
        LocalDateTime requestDate = tokens[5].isEmpty() ? null : LocalDateTime.parse(tokens[5]);
        LocalDateTime processDate = tokens[6].isEmpty() ? null : LocalDateTime.parse(tokens[6]);
        String remarks = tokens[7].isEmpty() ? null : tokens[7];

        return new WithdrawalRequest(requestId, applicationId, applicantNric, projectName, status, requestDate, processDate, remarks);
    }
}
