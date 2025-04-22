package io;

import models.Enquiry;
import java.time.format.DateTimeFormatter;
import utils.FileUtils;

import static utils.Constants.DELIMITER;
import static utils.Constants.DATE_TIME_FORMAT;

public class EnquirySerializer {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    
    /**
     * Serializes an Enquiry object into a string.
     * Format: Enquiry ID||NRIC||Project Name||Enquiry||Response||Timestamp||Respondent NRIC||Response Date
     *
     * @param enquiry The Enquiry object to serialize.
     * @return A string representation of the Enquiry.
     */
    public static String serialize(Enquiry enquiry) {
        StringBuilder sb = new StringBuilder();
        
        // Assuming getters exist in Enquiry for these properties:
        sb.append(enquiry.getEnquiryId()).append(DELIMITER) // Enquiry ID
          .append(enquiry.getApplicantNric()).append(DELIMITER) // Applicant NRIC
          .append(enquiry.getProjectName()).append(DELIMITER)   // Project Name
          .append(FileUtils.escapeCsvField(enquiry.getMessage())).append(DELIMITER)       // Enquiry text with escaping
          .append(FileUtils.escapeCsvField(enquiry.getReply() != null ? enquiry.getReply() : "")).append(DELIMITER) // Response text with escaping
          .append(enquiry.getSubmittedAt() != null ? DATE_FORMATTER.format(enquiry.getSubmittedAt()) : "").append(DELIMITER) // Timestamp
          .append(enquiry.getRespondentNric() != null ? enquiry.getRespondentNric() : "").append(DELIMITER) // Respondent NRIC
          .append(enquiry.getRepliedAt() != null ? DATE_FORMATTER.format(enquiry.getRepliedAt()) : ""); // Response Date

        return sb.toString();
    }
}
