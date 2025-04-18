package access.enquiry;

import java.util.ArrayList;
import java.util.List;
import models.Enquiry;
import io.FileIO;

public class EnquiryHandler implements ManagerEnquiryFeatures, OfficerEnquiryFeatures, ApplicantEnquiryFeatures {

    private List<Enquiry> enquiries;

    public EnquiryHandler(List<Enquiry> enquiries) {
        this.enquiries = enquiries;
    }

    // Manager methods...
    @Override
    public List<Enquiry> getAllEnquiries() {
        return enquiries;
    }

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
    
    // New method to add a reply with the responder's NRIC
    public void addReply(String enquiryId, String reply, String responderNric) {
        Enquiry enquiry = findEnquiryById(enquiryId);
        if (enquiry == null) {
            throw new IllegalArgumentException("Enquiry not found: " + enquiryId);
        }
        enquiry.addReply(reply, responderNric);
        saveChanges(); // Save changes to CSV
    }
    
    // New method to edit a specific reply
    public void editReply(String enquiryId, int replyIndex, String newReplyText) {
        Enquiry enquiry = findEnquiryById(enquiryId);
        if (enquiry == null) {
            throw new IllegalArgumentException("Enquiry not found: " + enquiryId);
        }
        enquiry.editReply(replyIndex, newReplyText);
        saveChanges(); // Save changes to CSV
    }

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
    @Override
    public void submitEnquiry(Enquiry enquiry) {
        // If the enquiry does not have an ID, generate one.
        if (enquiry.getEnquiryId() == null || enquiry.getEnquiryId().isEmpty()) {
            enquiry.setEnquiryId(generateUniqueId("ENQ"));
        }
        enquiries.add(enquiry);
        saveChanges(); // Save changes to CSV
    }

    @Override
    public void editEnquiry(String enquiryId, String newMessage) {
        Enquiry enquiry = findEnquiryById(enquiryId);
        if (enquiry == null) {
            throw new IllegalArgumentException("Enquiry not found: " + enquiryId);
        }
        enquiry.setMessage(newMessage);
        saveChanges(); // Save changes to CSV
    }

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

    @Override
    public void deleteEnquiry(String enquiryId) {
        Enquiry enquiry = findEnquiryById(enquiryId);
        if (enquiry == null) {
            throw new IllegalArgumentException("Enquiry not found: " + enquiryId);
        }
        enquiries.remove(enquiry);
        saveChanges(); // Save changes to CSV
    }

    public void saveChanges() {
        FileIO.saveEnquiries(enquiries);
    }

    private Enquiry findEnquiryById(String enquiryId) {
        for (Enquiry enquiry : enquiries) {
            if (enquiry.getEnquiryId().equalsIgnoreCase(enquiryId)) {
                return enquiry;
            }
        }
        return null;
    }
    
    private String generateUniqueId(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }
}
