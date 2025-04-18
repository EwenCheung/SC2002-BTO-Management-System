package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import models.enums.*;

public class Enquiry {
    private String enquiryId;
    private String applicantNric;
    private String projectName;
    private String enquiryText;
    
    // New structure for multiple replies
    private List<Reply> replies;
    
    // Legacy fields for backward compatibility
    private String responseText;
    private String respondentNric;
    private LocalDateTime submittedAt;
    private LocalDateTime repliedAt;

    // Inner class to represent a single reply
    public static class Reply {
        private String text;
        private String respondentNric;
        private LocalDateTime timestamp;
        
        public Reply(String text, String respondentNric, LocalDateTime timestamp) {
            this.text = text;
            this.respondentNric = respondentNric;
            this.timestamp = timestamp;
        }
        
        public String getText() {
            return text;
        }
        
        public String getRespondentNric() {
            return respondentNric;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        @Override
        public String toString() {
            return text + " (Replied by " + respondentNric + " at " + timestamp + ")";
        }
    }

    //for enquiry factory
    public Enquiry(String enquiryId, String nric, String projectName, String enquiryText, String responseText, LocalDateTime submittedAt, String respondentNric, LocalDateTime repliedAt){ // create enquiry
        this.enquiryId = enquiryId;
        this.applicantNric = nric;
        this.projectName = projectName;
        this.enquiryText = enquiryText;
        this.responseText = responseText;
        this.submittedAt = submittedAt;
        this.respondentNric = respondentNric;
        this.repliedAt = repliedAt;
        
        // Initialize replies list
        this.replies = new ArrayList<>();
        
        // If there's an existing response, add it as the first reply
        if (responseText != null && !responseText.isEmpty() && !responseText.equals("No reply yet")) {
            replies.add(new Reply(responseText, respondentNric, repliedAt));
        }
    }

    public Enquiry(String nric, String projectName, String enquiryText){ //used for new enquiry
        this.applicantNric = nric;
        this.projectName = projectName;
        this.enquiryText = enquiryText;
        this.submittedAt = LocalDateTime.now();
        this.replies = new ArrayList<>();
    }
    
    public String getEnquiryId(){
        return enquiryId;
    }
    
    public String getApplicantNric(){
        return applicantNric;
    }

    public String getProjectName(){
        return projectName;
    }

    public String getMessage(){
        return enquiryText;
    }

    // Legacy method for backward compatibility
    public String getReply(){
        if (replies.isEmpty()) {
            return responseText;
        } else {
            return replies.get(replies.size() - 1).getText();
        }
    }
    
    // New method to get all replies
    public List<Reply> getReplies() {
        return replies;
    }
    
    // New method to set all replies
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
    
    // Method to check if the enquiry has any replies
    public boolean hasReplies() {
        return !replies.isEmpty();
    }

    public LocalDateTime getSubmittedAt(){
        return submittedAt;
    }

    // Legacy method for backward compatibility
    public String getRespondentNric(){
        if (replies.isEmpty()) {
            return respondentNric;
        } else {
            return replies.get(replies.size() - 1).getRespondentNric();
        }
    }

    // Legacy method for backward compatibility
    public LocalDateTime getRepliedAt(){
        if (replies.isEmpty()) {
            return repliedAt;
        } else {
            return replies.get(replies.size() - 1).getTimestamp();
        }
    }

    //setters
    public void setMessage(String message){ //updating existing message
        this.enquiryText = message;
        this.submittedAt = LocalDateTime.now();
    }

    // Legacy method for backward compatibility
    public void setReply(String reply){ //for administrator reply
        this.responseText = reply;
        this.repliedAt = LocalDateTime.now();
        
        // Also add to the replies list for the new implementation
        addReply(reply, respondentNric);
    }
    
    // New method to add a reply
    public void addReply(String replyText, String responderNric) {
        Reply newReply = new Reply(replyText, responderNric, LocalDateTime.now());
        replies.add(newReply);
        
        // Update legacy fields for backward compatibility
        this.responseText = replyText;
        this.respondentNric = responderNric;
        this.repliedAt = newReply.getTimestamp();
    }
    
    // New method to edit a specific reply
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

    public void setEnquiryId(String enquiryId){
        this.enquiryId = enquiryId;
    }

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
