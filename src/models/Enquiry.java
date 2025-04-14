package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import models.enums.*;

public class Enquiry {
    private String enquiryId;
    private String applicantNric;
    private String projectName;
    private String enquiryText;
    private String responseText;
    private String respondentNric;
    private LocalDateTime submittedAt;
    private LocalDateTime repliedAt;

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
    }

    public Enquiry(String nric, String projectName, String enquiryText){ //used for new enquiry
        this.applicantNric = nric;
        this.projectName = projectName;
        this.enquiryText = enquiryText;
        this.submittedAt = LocalDateTime.now();
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

    public String getReply(){
        return responseText;
    }

    public LocalDateTime getSubmittedAt(){
        return submittedAt;
    }

    public String getRespondentNric(){
        return respondentNric;
    }

    public LocalDateTime getRepliedAt(){
        return repliedAt;
    }

    //setters
    public void setMessage(String message){ //updating existing message
        this.enquiryText = message;
        this.submittedAt = LocalDateTime.now();
    }

    public void setReply(String reply){ //for administrator reply
        this.responseText = reply;
        this.repliedAt = LocalDateTime.now();       
    }

    public void setEnquiryId(String enquiryId){
        this.enquiryId = enquiryId;
    }

    @Override
    public String toString(){
        return "Enquiry{"+
                "Enquiry ID = " + enquiryId + 
                "For Project : " + projectName +
                "message ='" + enquiryText + '\''+
                ", reply ='" + (responseText != null? responseText: "No reply yet") + '\''+
                ", submittedAt = " + submittedAt +
                ", repliedAt = " + (repliedAt !=null? repliedAt:"N/A")+
                '}';
    }

}
