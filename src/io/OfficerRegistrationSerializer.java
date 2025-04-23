package io;

import java.time.format.DateTimeFormatter;

import models.OfficerRegistration;
import utils.FileUtils;
import static utils.Constants.DELIMITER;
import static utils.Constants.DATE_TIME_FORMAT;

/**
 * Provides serialization functionality for OfficerRegistration objects.
 * This class converts OfficerRegistration objects to string format for storage
 * in CSV files, handling date formatting and proper field escaping.
 */
public class OfficerRegistrationSerializer {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    /**
     * Serializes an OfficerRegistration object into a delimited string.
     * Format: Registration ID||Officer NRIC||Project Name||Status||Registration Date
     *
     * @param registration The OfficerRegistration to serialize.
     * @return A string representation of the OfficerRegistration.
     */
    public static String serialize(OfficerRegistration registration) {
        StringBuilder sb = new StringBuilder();
        sb.append(registration.getRegistrationId()).append(DELIMITER)
          .append(registration.getOfficerNric()).append(DELIMITER)
          .append(FileUtils.escapeCsvField(registration.getProjectName())).append(DELIMITER)
          .append(registration.getStatus()).append(DELIMITER)
          .append(registration.getRegistrationDate() != null ? DATE_FORMATTER.format(registration.getRegistrationDate()) : "");
        return sb.toString();
    }
}
