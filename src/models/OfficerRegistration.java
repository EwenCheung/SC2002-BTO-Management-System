package models;

import java.time.LocalDateTime;
import models.enums.OfficerRegistrationStatus;

public class OfficerRegistration {
    private String registrationId;
    private String officerNric;
    private String projectName;
    private OfficerRegistrationStatus status;
    private LocalDateTime registrationDate;
    
    /**
     * Constructs an OfficerRegistration using the given parameters.
     * 
     * @param registrationId the unique registration ID
     * @param officerNric the officer's NRIC
     * @param projectName the name of the project
     * @param status the registration status
     * @param registrationDate the date of registration
     */
    public OfficerRegistration(String registrationId, String officerNric, String projectName, OfficerRegistrationStatus status, LocalDateTime registrationDate) {
        this.registrationId = registrationId;
        this.officerNric = officerNric;
        this.projectName = projectName;
        this.status = status;
        this.registrationDate = registrationDate;
    }

    public OfficerRegistration(String officerNric, String projectName){
        this.officerNric = officerNric;
        this.projectName = projectName;
        this.status = OfficerRegistrationStatus.PENDING;
        this.registrationDate = LocalDateTime.now(); 
    }
    
    // Getters
    public String getRegistrationId() {
        return registrationId;
    }
    
    public String getOfficerNric() {
        return officerNric;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public OfficerRegistrationStatus getStatus() {
        return status;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    // setters
    public void setStatus(OfficerRegistrationStatus status) {
        this.status = status;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setRegistrationId(String registrationId){
        this.registrationId = registrationId;
    }
    
    @Override
    public String toString(){
        return "OfficerRegistration{" + 
                "registrationId='" + registrationId + '\'' + 
                ", officerNric='" + officerNric + '\'' +
                ", projectName='" + projectName + '\'' +
                ", status='" + status + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }
}
