package access.enquiry;

import java.util.List;
import models.Enquiry;

public interface ManagerEnquiryFeatures {
    /**
     * Retrieves all enquiries for every project.
     * @return List of all Enquiry objects.
     */
    List<Enquiry> getAllEnquiries();

    /**
     * Replies to an enquiry identified by its unique ID.
     * @param enquiryId the ID of the enquiry.
     * @param reply the reply message.
     */
    void replyEnquiry(String enquiryId, String reply);

    List<Enquiry> getEnquiriesByProject(String projectName);
}
