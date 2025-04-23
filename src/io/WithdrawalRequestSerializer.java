package io;

import java.time.format.DateTimeFormatter;
import models.WithdrawalRequest;
import utils.FileUtils;
import static utils.Constants.DATE_TIME_FORMAT;
import static utils.Constants.DELIMITER;

/**
 * Provides serialization functionality for WithdrawalRequest objects.
 * This class is responsible for converting WithdrawalRequest objects to string format
 * for persistent storage in CSV files. It handles proper formatting of dates and
 * ensures special characters in text fields are properly escaped.
 */
public class WithdrawalRequestSerializer {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    /**
     * Serializes a WithdrawalRequest object into a delimited string.
     * Format: Request ID||Application ID||Applicant NRIC||Project Name||Status||Request Date||Process Date||Remarks
     *
     * @param request The WithdrawalRequest to serialize.
     * @return A string representation of the WithdrawalRequest.
     */
    public static String serialize(WithdrawalRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getRequestId()).append(DELIMITER)
          .append(request.getApplicationId()).append(DELIMITER)
          .append(request.getApplicantNric()).append(DELIMITER)
          .append(FileUtils.escapeCsvField(request.getProjectName())).append(DELIMITER)
          .append(request.getStatus()).append(DELIMITER)
          .append(request.getRequestDate() != null ? DATE_FORMATTER.format(request.getRequestDate()) : "").append(DELIMITER)
          .append(request.getProcessDate() != null ? DATE_FORMATTER.format(request.getProcessDate()) : "").append(DELIMITER)
          .append(request.getRemarks() != null ? FileUtils.escapeCsvField(request.getRemarks()) : "");
        return sb.toString();
    }
}
