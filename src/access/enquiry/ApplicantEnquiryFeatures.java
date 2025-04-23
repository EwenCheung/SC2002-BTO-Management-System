package access.enquiry;

import java.util.List;
import models.Enquiry;

/**
 * Interface that defines the enquiry operations available to Applicants.
 * Provides functionality for applicants to submit, edit, view, and delete
 * their enquiries related to BTO projects.
 */
public interface ApplicantEnquiryFeatures {
    /**
     * Submits a new enquiry.
     * @param enquiry the Enquiry to submit.
     */
    void submitEnquiry(Enquiry enquiry);

    /**
     * Edits an existing enquiry's message.
     * @param enquiryId the ID of the enquiry.
     * @param newMessage the new enquiry message.
     */
    void editEnquiry(String enquiryId, String newMessage);

    /**
     * Retrieves enquiries submitted by a specific applicant.
     * @param applicantNric the applicant's NRIC.
     * @return List of Enquiry objects.
     */
    List<Enquiry> getEnquiriesForApplicant(String applicantNric);

    /**
     * Deletes an enquiry identified by its unique ID.
     * @param enquiryId the ID of the enquiry.
     */
    void deleteEnquiry(String enquiryId);
}
