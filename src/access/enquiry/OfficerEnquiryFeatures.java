package access.enquiry;

import java.util.List;
import models.Enquiry;

/**
 * Interface that defines the enquiry operations available to Officers.
 * Provides functionality for officers to view and respond to enquiries
 * related to projects they are assigned to.
 */
public interface OfficerEnquiryFeatures {
    /**
     * Retrieves all enquiries for a specified project.
     * @param projectName the project name.
     * @return List of Enquiry objects for that project.
     */
    List<Enquiry> getEnquiriesForProject(String projectName);

    /**
     * Replies to an enquiry (for their assigned project) identified by its unique ID.
     * @param enquiryId the ID of the enquiry.
     * @param reply the reply message.
     */
    void replyEnquiry(String enquiryId, String reply);
}
