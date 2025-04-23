package access.enquiry;

import java.util.ArrayList;
import java.util.List;
import models.Enquiry;
import io.FileIO;

/**
 * Handles all enquiry-related operations in the BTO Management System.
 * Implements interfaces for managers, officers, and applicants to provide
 * role-appropriate access to enquiry functionality and data.
 */
public class EnquiryHandler implements ManagerEnquiryFeatures, OfficerEnquiryFeatures, ApplicantEnquiryFeatures {

    private List<Enquiry> enquiries;

    /**
     * Constructs an EnquiryHandler with the given list of enquiries.
     * 
     * @param enquiries The list of enquiries to manage
     */
    public EnquiryHandler(List<Enquiry> enquiries) {
        this.enquiries = enquiries;
    }

    // Manager methods...
    /**
     * Returns all enquiries in the system.
     * Available to managers for oversight and reporting.
     * 
     * @return A list of all Enquiry objects
     */
    @Override
    public List<Enquiry> getAllEnquiries() {
        return enquiries;
    }

    /**
     * Adds a reply to an enquiry.
     * Available to managers to respond to applicant enquiries.
     * 
     * @param enquiryId The ID of the enquiry to reply to
     * @param reply The reply message text
     * @throws IllegalArgumentException if the enquiry is not found
     */
    @Override
    public void replyEnquiry(String enquiryId, String reply) {
        Enquiry enquiry = findEnquiryById(enquiryId);
        if (enquiry == null) {
            throw new IllegalArgumentException("Enquiry not found: " + enquiryId);
        }
        enquiry.setReply(reply);
        
        // Save the changes to file
        saveChanges();
    }
    
    /**
     * Adds a reply to an enquiry with the responder's NRIC.
     * Supports accountability by tracking who responded.
     * 
     * @param enquiryId The ID of the enquiry to reply to
     * @param reply The reply message text
     * @param responderNric The NRIC of the user adding the reply
     * @throws IllegalArgumentException if the enquiry is not found
     */
    public void addReply(String enquiryId, String reply, String responderNric) {
        Enquiry enquiry = findEnquiryById(enquiryId);
        if (enquiry == null) {
            throw new IllegalArgumentException("Enquiry not found: " + enquiryId);
        }
        enquiry.addReply(reply, responderNric);
        saveChanges(); // Save changes to CSV
    }
    
    /**
     * Edits a specific reply in an enquiry conversation.
     * 
     * @param enquiryId The ID of the enquiry containing the reply
     * @param replyIndex The index of the reply to edit
     * @param newReplyText The new text for the reply
     * @throws IllegalArgumentException if the enquiry is not found
     */
    public void editReply(String enquiryId, int replyIndex, String newReplyText) {
        Enquiry enquiry = findEnquiryById(enquiryId);
        if (enquiry == null) {
            throw new IllegalArgumentException("Enquiry not found: " + enquiryId);
        }
        enquiry.editReply(replyIndex, newReplyText);
        saveChanges(); // Save changes to CSV
    }

    /**
     * Returns all enquiries for a specific project.
     * Available to managers for project-specific monitoring.
     * 
     * @param projectName The name of the project
     * @return A list of Enquiry objects for the specified project
     */
    @Override
    public List<Enquiry> getEnquiriesByProject(String projectName) {
        List<Enquiry> result = new ArrayList<>();
        for (Enquiry enquiry : enquiries) {
            if (enquiry.getProjectName().equalsIgnoreCase(projectName)) {
                result.add(enquiry);
            }
        }
        return result;
    }

    // Officer methods...
    /**
     * Returns all enquiries for a specific project.
     * Available to officers to view enquiries for their assigned projects.
     * 
     * @param projectName The name of the project
     * @return A list of Enquiry objects for the specified project
     */
    @Override
    public List<Enquiry> getEnquiriesForProject(String projectName) {
        List<Enquiry> result = new ArrayList<>();
        for (Enquiry enquiry : enquiries) {
            if (enquiry.getProjectName().equalsIgnoreCase(projectName)) {
                result.add(enquiry);
            }
        }
        return result;
    }

    // Applicant methods...
    /**
     * Submits a new enquiry to the system.
     * Available to applicants to ask questions about projects.
     * 
     * @param enquiry The enquiry to submit
     */
    @Override
    public void submitEnquiry(Enquiry enquiry) {
        // If the enquiry does not have an ID, generate one.
        if (enquiry.getEnquiryId() == null || enquiry.getEnquiryId().isEmpty()) {
            enquiry.setEnquiryId(generateUniqueId("ENQ"));
        }
        enquiries.add(enquiry);
        saveChanges(); // Save changes to CSV
    }

    /**
     * Edits an existing enquiry message.
     * Available to applicants to update their enquiry before receiving a response.
     * 
     * @param enquiryId The ID of the enquiry to edit
     * @param newMessage The new enquiry message text
     * @throws IllegalArgumentException if the enquiry is not found
     */
    @Override
    public void editEnquiry(String enquiryId, String newMessage) {
        Enquiry enquiry = findEnquiryById(enquiryId);
        if (enquiry == null) {
            throw new IllegalArgumentException("Enquiry not found: " + enquiryId);
        }
        enquiry.setMessage(newMessage);
        saveChanges(); // Save changes to CSV
    }

    /**
     * Returns all enquiries submitted by a specific applicant.
     * Available to applicants to view their enquiry history.
     * 
     * @param applicantNric The NRIC of the applicant
     * @return A list of Enquiry objects submitted by the specified applicant
     */
    @Override
    public List<Enquiry> getEnquiriesForApplicant(String applicantNric) {
        List<Enquiry> result = new ArrayList<>();
        for (Enquiry enquiry : enquiries) {
            if (enquiry.getApplicantNric().equalsIgnoreCase(applicantNric)) {
                result.add(enquiry);
            }
        }
        return result;
    }

    /**
     * Deletes an enquiry from the system.
     * Available to applicants to remove their own enquiries.
     * 
     * @param enquiryId The ID of the enquiry to delete
     * @throws IllegalArgumentException if the enquiry is not found
     */
    @Override
    public void deleteEnquiry(String enquiryId) {
        Enquiry enquiry = findEnquiryById(enquiryId);
        if (enquiry == null) {
            throw new IllegalArgumentException("Enquiry not found: " + enquiryId);
        }
        enquiries.remove(enquiry);
        saveChanges(); // Save changes to CSV
    }

    /**
     * Saves current enquiry data to persistent storage.
     * Called after operations that modify enquiry data.
     */
    public void saveChanges() {
        FileIO.saveEnquiries(enquiries);
    }

    /**
     * Finds an enquiry by its ID.
     * Helper method used by various public methods that require finding specific enquiries.
     * 
     * @param enquiryId The ID of the enquiry to find
     * @return The Enquiry object with the specified ID, or null if not found
     */
    private Enquiry findEnquiryById(String enquiryId) {
        for (Enquiry enquiry : enquiries) {
            if (enquiry.getEnquiryId().equalsIgnoreCase(enquiryId)) {
                return enquiry;
            }
        }
        return null;
    }
    
    /**
     * Generates a unique ID for a new enquiry.
     * Creates IDs in the format: [prefix]-[timestamp] to ensure uniqueness.
     * 
     * @param prefix The prefix to use for the ID (e.g., "ENQ")
     * @return A unique ID string
     */
    private String generateUniqueId(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }
}
