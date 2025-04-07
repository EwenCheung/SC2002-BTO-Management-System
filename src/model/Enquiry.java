package model;

import java.time.LocalDateTime;

public class Enquiry {
    private String message;
    private String reply;
    private LocalDateTime submittedAt;
    private LocalDateTime repliedAt;

    public Enquiry(String message){ // create enquiry
        this.message=message;
        this.submittedAt = LocalDateTime.now();
        this.reply = null;
        this.repliedAt = null;
    }
    
    public String getMessage(){
        return message;
    }

    public String getReply(){
        return reply;
    }

    public LocalDateTime getSubmittedAt(){
        return submittedAt;
    }

    public LocalDateTime getRepliedAt(){
        return repliedAt;
    }

    //setters
    public void setMessage(String message){ //updating existing message
        this.message = message;
        this.submittedAt = LocalDateTime.now();
    }

    public void setReply(String reply){ //for administrator reply
        this.reply = reply;
        this.repliedAt = LocalDateTime.now();
    }

    @Override
    public String toString(){
        return "Enquiry{"+
                "message ='" + message + '\''+
                ", reply ='" + (reply != null? reply: "No reply yet") + '\''+
                ", submittedAt = " + submittedAt +
                ", repliedAt = " + (repliedAt !=null? repliedAt:"N/A")+
                '}';
    }

}
