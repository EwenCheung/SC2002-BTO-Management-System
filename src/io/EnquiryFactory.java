package io;

import models.Enquiry;
import java.time.LocalDateTime;

public class EnquiryFactory {
    public static Enquiry createEnquiry(String[] tokens) {
        // Format: Enquiry ID,NRIC,Project Name,Enquiry,Response,Timestamp,Respondent NRIC,Response Date
        if (tokens.length < 8 || tokens[0].isEmpty() || tokens[1].isEmpty() || tokens[2].isEmpty() || tokens[3].isEmpty()) {
            throw new IllegalArgumentException("Missing required fields for Enquiry");
        }

        String enquiryId = tokens[0];
        String nric = tokens[1];
        String projectName = tokens[2];
        String enquiryText = tokens[3];
        String responseText = tokens[4].isEmpty() ? null : tokens[4];
        LocalDateTime timestamp = tokens[5].isEmpty() ? null : LocalDateTime.parse(tokens[5]);
        String respondentNric = tokens[6].isEmpty() ? null : tokens[6];
        LocalDateTime responseDate = tokens[7].isEmpty() ? null : LocalDateTime.parse(tokens[7]);

        return new Enquiry(enquiryId, nric, projectName, enquiryText, responseText, timestamp, respondentNric, responseDate);
    }
}
