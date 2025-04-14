package io;
import java.time.LocalDate;
import java.time.LocalDateTime;

import models.Application;
import models.enums.ApplicationStatus;

public class ApplicationFactory {
    public static Application createApplication(String[] tokens) {
        // Format: Application ID,Applicant NRIC,Project Name,Unit Type,Status,Assigned Unit,Assigned Officer,Application Date,Last Updated,Remarks
        if (tokens.length < 10 || tokens[0].isEmpty() || tokens[1].isEmpty() || tokens[2].isEmpty() || tokens[3].isEmpty() || tokens[4].isEmpty()) {
            throw new IllegalArgumentException("Missing required fields for Application");
        }

        String id = tokens[0];
        String applicantNric = tokens[1];
        String projectName = tokens[2];
        String unitType = tokens[3];
        ApplicationStatus status = ApplicationStatus.valueOf(tokens[4].toUpperCase());
        String assignedUnit = tokens[5].isEmpty() ? null : tokens[5];
        String assignedOfficer = tokens[6].isEmpty() ? null : tokens[6];
        LocalDateTime applicationDate = tokens[7].isEmpty() ? null : LocalDateTime.parse(tokens[7]);
        LocalDateTime lastUpdated = tokens[8].isEmpty() ? null : LocalDateTime.parse(tokens[8]);
        String remarks = tokens[9].isEmpty() ? null : tokens[9];

        return new Application(id, applicantNric, projectName, unitType, status,
                assignedUnit, assignedOfficer, applicationDate, lastUpdated, remarks);
    }
}
