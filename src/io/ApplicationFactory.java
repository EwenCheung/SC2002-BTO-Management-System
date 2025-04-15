package io;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import models.Application;
import models.enums.ApplicationStatus;
import static utils.Constants.DATE_TIME_FORMAT;

public class ApplicationFactory {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
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
        LocalDateTime applicationDate = tokens[7].isEmpty() ? null : LocalDateTime.parse(tokens[7], DATE_FORMATTER);
        LocalDateTime lastUpdated = tokens[8].isEmpty() ? null : LocalDateTime.parse(tokens[8], DATE_FORMATTER);
        String remarks = tokens[9].isEmpty() ? null : tokens[9];

        return new Application(id, applicantNric, projectName, unitType, status,
                assignedUnit, assignedOfficer, applicationDate, lastUpdated, remarks);
    }
}
