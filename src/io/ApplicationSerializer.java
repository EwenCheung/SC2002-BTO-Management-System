package io;

import java.time.format.DateTimeFormatter;
import models.Application;
import utils.FileUtils;
import static utils.Constants.DATE_TIME_FORMAT;
import static utils.Constants.DELIMITER;

/**
 * Provides serialization functionality for Application objects.
 * This class is responsible for converting Application objects to string format
 * for persistent storage in CSV files. It handles date formatting, null handling,
 * and proper escaping of text fields.
 */
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
          .append(FileUtils.escapeCsvField(app.getProjectName())).append(DELIMITER)
          .append(app.getUnitType()).append(DELIMITER)
          .append(app.getStatus().toString()).append(DELIMITER)
          // Check for nulls to avoid "null" string output.
          .append(app.getAssignedUnit() != null ? app.getAssignedUnit() : "").append(DELIMITER)
          .append(app.getAssignedOfficer() != null ? app.getAssignedOfficer() : "").append(DELIMITER)
          .append(DATE_FORMATTER.format(app.getApplicationDate())).append(DELIMITER)
          .append(DATE_FORMATTER.format(app.getLastUpdated())).append(DELIMITER)
          // Always append the remarks field, even if it's empty - this ensures consistent CSV columns
          // Escape the remarks field to handle commas
          .append(app.getRemarks() != null ? FileUtils.escapeCsvField(app.getRemarks()) : "");
          
        return sb.toString();
    }
}
