package io;

import java.time.format.DateTimeFormatter;

import models.WithdrawalRequest;
import utils.FileUtils;
import static utils.Constants.DELIMITER;
import static utils.Constants.DATE_TIME_FORMAT;

public class WithdrawalRequestSerializer {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    /**
     * Serializes a WithdrawalRequest object into a string.
     * Format: Request ID||Application ID||Applicant NRIC||Project Name||Status||Request Date||Process Date||Remarks
     *
     * @param request The WithdrawalRequest object to serialize.
     * @return A string representation of the WithdrawalRequest.
     */
    public static String serialize(WithdrawalRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getRequestId()).append(DELIMITER)
          .append(request.getApplicationId()).append(DELIMITER)
          .append(request.getApplicantNric()).append(DELIMITER)
          .append(request.getProjectName()).append(DELIMITER)
          .append(request.getStatus()).append(DELIMITER)
          .append(request.getRequestDate() != null ? DATE_TIME_FORMATTER.format(request.getRequestDate()) : "").append(DELIMITER)
          .append(request.getProcessDate() != null ? DATE_TIME_FORMATTER.format(request.getProcessDate()) : "").append(DELIMITER)
          .append(request.getRemarks() != null ? FileUtils.escapeCsvField(request.getRemarks()) : "");
        return sb.toString();
    }
}
