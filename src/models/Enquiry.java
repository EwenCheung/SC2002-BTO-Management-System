package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import models.enums.*;

/**
 * Represents an enquiry in the BTO Management System.
 * Enquiries are questions or concerns submitted by applicants regarding BTO projects.
 * This class supports a thread-like conversation model with multiple replies from officers
 * and managers, while maintaining backward compatibility with the original single-reply model.
 */
public class Enquiry {
    private String enquiryId;
    private String applicantNric;
    private String projectName;
    private String enquiryText;
    private EnquiryStatus status;
    
    // New structure for multiple replies
    private List<Reply> replies;
    
    // Legacy fields for backward compatibility
    private String responseText;
    private String respondentNric;
    private LocalDateTime submittedAt;
    private LocalDateTime repliedAt;

    /**
     * Inner class to represent a single reply to an enquiry.
     * This supports the conversation thread model where multiple officers
     * can provide input on a single enquiry.
     */
    public static class Reply {
        private String text;
        private String respondentNric;
        private LocalDateTime timestamp;
        
        /**
         * Constructs a new Reply with the specified details.
         * 
         * @param text The content of the reply
         * @param respondentNric The NRIC of the person who provided the reply
         * @param timestamp When the reply was submitted
         */
        public Reply(String text, String respondentNric, LocalDateTime timestamp) {
            this.text = text;
            this.respondentNric = respondentNric;
            this.timestamp = timestamp;
        }
        
        /**
         * Gets the text content of the reply.
         * 
         * @return The reply text
         */
        public String getText() {
            return text;
        }
        
        /**
         * Gets the NRIC of the person who provided the reply.
         * 
         * @return The respondent's NRIC
         */
        public String getRespondentNric() {
            return respondentNric;
        }
        
        /**
         * Gets the timestamp of when the reply was submitted.
         * 
         * @return The reply timestamp
         */
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        /**
         * Returns a string representation of the reply.
         * 
         * @return A formatted string containing the reply text, respondent, and timestamp
         */
        @Override
        public String toString() {
            return text + " (Replied by " + respondentNric + " at " + timestamp + ")";
        }
    }

    /**
     * Constructs an Enquiry with all fields specified.
     * Used primarily by the EnquiryFactory when loading enquiries from storage.
     * 
     * @param enquiryId The unique ID of the enquiry
     * @param nric The NRIC of the applicant who submitted the enquiry
     * @param projectName The name of the BTO project the enquiry is about
     * @param enquiryText The text of the enquiry
     * @param responseText The text of the response (if any)
     * @param submittedAt When the enquiry was submitted
     * @param respondentNric The NRIC of the respondent (if any)
     * @param repliedAt When the reply was submitted (if any)
     */
    public Enquiry(String enquiryId, String nric, String projectName, String enquiryText, String responseText, LocalDateTime submittedAt, String respondentNric, LocalDateTime repliedAt){ // create enquiry
        this.enquiryId = enquiryId;
        this.applicantNric = nric;
        this.projectName = projectName;
        this.enquiryText = enquiryText;
        this.responseText = responseText;
        this.submittedAt = submittedAt;
        this.respondentNric = respondentNric;
        this.repliedAt = repliedAt;
        this.status = (responseText != null && !responseText.isEmpty() && !responseText.equals("No reply yet")) ? EnquiryStatus.CLOSE : EnquiryStatus.OPEN;
        
        // Initialize replies list
        this.replies = new ArrayList<>();
        
        // If there's an existing response, add it as the first reply
        if (responseText != null && !responseText.isEmpty() && !responseText.equals("No reply yet")) {
            replies.add(new Reply(responseText, respondentNric, repliedAt));
        }
    }

    /**
     * Constructs a new Enquiry with essential details.
     * Used when an applicant is submitting a new enquiry.
     * 
     * @param nric The NRIC of the applicant submitting the enquiry
     * @param projectName The name of the BTO project the enquiry is about
     * @param enquiryText The text of the enquiry
     */
    public Enquiry(String nric, String projectName, String enquiryText){ //used for new enquiry
        this.applicantNric = nric;
        this.projectName = projectName;
        this.enquiryText = enquiryText;
        this.submittedAt = LocalDateTime.now();
        this.replies = new ArrayList<>();
        this.status = EnquiryStatus.OPEN;
    }
    
    /**
     * Gets the unique ID of this enquiry.
     * 
     * @return The enquiry ID
     */
    public String getEnquiryId(){
        return enquiryId;
    }
    
    /**
     * Gets the NRIC of the applicant who submitted this enquiry.
     * 
     * @return The applicant's NRIC
     */
    public String getApplicantNric(){
        return applicantNric;
    }

    /**
     * Gets the name of the BTO project this enquiry is about.
     * 
     * @return The project name
     */
    public String getProjectName(){
        return projectName;
    }

    /**
     * Gets the text content of the enquiry.
     * 
     * @return The enquiry message
     */
    public String getMessage(){
        return enquiryText;
    }

    /**
     * Gets the most recent reply text, or the legacy reply if no new replies exist.
     * Provided for backward compatibility with single-reply model.
     * 
     * @return The most recent reply text, or null if no replies
     */
    public String getReply(){
        if (replies.isEmpty()) {
            return responseText;
        } else {
            return replies.get(replies.size() - 1).getText();
        }
    }
    
    /**
     * Gets all replies to this enquiry.
     * 
     * @return List of Reply objects
     */
    public List<Reply> getReplies() {
        return replies;
    }
    
    /**
     * Sets the full list of replies for this enquiry.
     * Also updates legacy fields for backward compatibility.
     * 
     * @param replies The list of Reply objects to set
     */
    public void setReplies(List<Reply> replies) {
        this.replies = replies;
        
        // Update legacy fields for backward compatibility
        if (!replies.isEmpty()) {
            Reply latestReply = replies.get(replies.size() - 1);
            this.responseText = latestReply.getText();
            this.respondentNric = latestReply.getRespondentNric();
            this.repliedAt = latestReply.getTimestamp();
        }
    }
    
    /**
     * Checks if this enquiry has received any replies.
     * 
     * @return true if there are replies, false otherwise
     */
    public boolean hasReplies() {
        return !replies.isEmpty();
    }

    /**
     * Gets the timestamp when this enquiry was submitted.
     * 
     * @return The submission timestamp
     */
    public LocalDateTime getSubmittedAt(){
        return submittedAt;
    }

    /**
     * Gets the NRIC of the most recent respondent, or the legacy respondent if no new replies exist.
     * Provided for backward compatibility with single-reply model.
     * 
     * @return The NRIC of the respondent, or null if no replies
     */
    public String getRespondentNric(){
        if (replies.isEmpty()) {
            return respondentNric;
        } else {
            return replies.get(replies.size() - 1).getRespondentNric();
        }
    }

    /**
     * Gets the timestamp of the most recent reply, or the legacy reply timestamp if no new replies exist.
     * Provided for backward compatibility with single-reply model.
     * 
     * @return The timestamp of the most recent reply, or null if no replies
     */
    public LocalDateTime getRepliedAt(){
        if (replies.isEmpty()) {
            return repliedAt;
        } else {
            return replies.get(replies.size() - 1).getTimestamp();
        }
    }

    /**
     * Updates the text content of this enquiry.
     * Also updates the submission timestamp to reflect the edit time.
     * 
     * @param message The new enquiry text
     */
    public void setMessage(String message){ //updating existing message
        this.enquiryText = message;
        this.submittedAt = LocalDateTime.now();
    }

    /**
     * Sets a reply to this enquiry.
     * Provided for backward compatibility with single-reply model.
     * Also adds the reply to the new replies list structure.
     * 
     * @param reply The reply text
     */
    public void setReply(String reply){ //for administrator reply
        this.responseText = reply;
        this.repliedAt = LocalDateTime.now();
        this.status = EnquiryStatus.CLOSE;
        
        // Also add to the replies list for the new implementation
        addReply(reply, respondentNric);
    }
    
    /**
     * Adds a new reply to this enquiry.
     * Updates legacy fields for backward compatibility.
     * 
     * @param replyText The text of the reply
     * @param responderNric The NRIC of the respondent
     */
    public void addReply(String replyText, String responderNric) {
        Reply newReply = new Reply(replyText, responderNric, LocalDateTime.now());
        replies.add(newReply);
        
        // Update legacy fields for backward compatibility
        this.responseText = replyText;
        this.respondentNric = responderNric;
        this.repliedAt = newReply.getTimestamp();
    }
    
    /**
     * Edits an existing reply.
     * If the edited reply is the most recent one, also updates legacy fields.
     * 
     * @param index The index of the reply to edit
     * @param newReplyText The new text for the reply
     */
    public void editReply(int index, String newReplyText) {
        if (index >= 0 && index < replies.size()) {
            Reply oldReply = replies.get(index);
            Reply newReply = new Reply(newReplyText, oldReply.getRespondentNric(), LocalDateTime.now());
            replies.set(index, newReply);
            
            // Update legacy fields if this is the most recent reply
            if (index == replies.size() - 1) {
                this.responseText = newReplyText;
                this.repliedAt = newReply.getTimestamp();
            }
        }
    }

    /**
     * Sets the unique ID for this enquiry.
     * Typically used when a new enquiry is being persisted.
     * 
     * @param enquiryId The enquiry ID to set
     */
    public void setEnquiryId(String enquiryId){
        this.enquiryId = enquiryId;
    }

    /**
     * Gets the current status of this enquiry.
     * 
     * @return The enquiry status
     */
    public EnquiryStatus getStatus() {
        return status;
    }
    
    /**
     * Sets the status of this enquiry.
     * 
     * @param status The new status to set
     */
    public void setStatus(EnquiryStatus status) {
        this.status = status;
    }

    /**
     * Returns a string representation of this enquiry.
     * 
     * @return A formatted string containing the enquiry details
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("Enquiry{" +
                "Enquiry ID = " + enquiryId + 
                "For Project : " + projectName +
                "message ='" + enquiryText + '\'');
        
        if (replies.isEmpty()) {
            sb.append(", reply ='").append(responseText != null ? responseText : "No reply yet").append('\'');
            sb.append(", submittedAt = ").append(submittedAt);
            sb.append(", repliedAt = ").append(repliedAt != null ? repliedAt : "N/A");
        } else {
            sb.append(", replies = ").append(replies.size());
            sb.append(", submittedAt = ").append(submittedAt);
            sb.append(", lastRepliedAt = ").append(replies.get(replies.size() - 1).getTimestamp());
        }
        
        sb.append('}');
        return sb.toString();
    }
}
