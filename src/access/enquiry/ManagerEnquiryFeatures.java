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
    
    /**
     * Adds a new reply to an existing enquiry.
     * @param enquiryId the ID of the enquiry.
     * @param reply the reply message.
     * @param responderNric the NRIC of the person adding the reply.
     */
    void addReply(String enquiryId, String reply, String responderNric);
    
    /**
     * Edits a specific reply in an enquiry.
     * @param enquiryId the ID of the enquiry.
     * @param replyIndex the index of the reply to edit.
     * @param newReplyText the new text for the reply.
     */
    void editReply(String enquiryId, int replyIndex, String newReplyText);

    /**
     * Gets all enquiries for a specific project.
     * @param projectName the name of the project.
     * @return List of enquiries for that project.
     */
    List<Enquiry> getEnquiriesByProject(String projectName);
}
