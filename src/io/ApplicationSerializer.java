package io;

import java.time.format.DateTimeFormatter;
import models.Application;
import static utils.Constants.DATE_TIME_FORMAT;
import static utils.Constants.DELIMITER;

public class ApplicationSerializer {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    /**
     * Serializes an Application object into a string for file storage.
     * Format:
     * Application ID||Applicant NRIC||Project Name||Unit Type||Status||
     * Assigned Unit||Assigned Officer||Application Date||Last Updated||Remarks
     * 
     * @param app The Application object to serialize.
     * @return A string representation of the Application.
     */
    public static String serialize(Application app) {
        StringBuilder sb = new StringBuilder();
        sb.append(app.getApplicationId()).append(DELIMITER)
          .append(app.getApplicantNric()).append(DELIMITER)
          .append(app.getProjectName()).append(DELIMITER)
          .append(app.getUnitType()).append(DELIMITER)
          .append(app.getStatus().toString()).append(DELIMITER)
          // Check for nulls to avoid "null" string output.
          .append(app.getAssignedUnit() != null ? app.getAssignedUnit() : "").append(DELIMITER)
          .append(app.getAssignedOfficer() != null ? app.getAssignedOfficer() : "").append(DELIMITER)
          .append(DATE_FORMATTER.format(app.getApplicationDate())).append(DELIMITER)
          .append(DATE_FORMATTER.format(app.getLastUpdated())).append(DELIMITER)
          .append(app.getRemarks() != null ? app.getRemarks() : "");
        return sb.toString();
    }
}
