package models;

import java.time.LocalDateTime;
import models.enums.OfficerRegistrationStatus;

/**
 * Represents an officer's registration to be assigned to a specific BTO project.
 * This class tracks the registration process from submission through approval/rejection.
 * Officer registrations are created when HDB Officers express interest in working on a
 * specific project and must be approved by Project Managers before the officer is assigned.
 */
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

    /**
     * Constructs a new officer registration with initial pending status.
     * The registration date is automatically set to the current date and time.
     * Registration ID will be generated and set later.
     * 
     * @param officerNric the NRIC of the officer requesting assignment
     * @param projectName the name of the project the officer wants to join
     */
    public OfficerRegistration(String officerNric, String projectName){
        this.officerNric = officerNric;
        this.projectName = projectName;
        this.status = OfficerRegistrationStatus.PENDING;
        this.registrationDate = LocalDateTime.now(); 
    }
    
    // Getters
    /**
     * Gets the unique identifier for this registration request.
     * 
     * @return the registration ID
     */
    public String getRegistrationId() {
        return registrationId;
    }
    
    /**
     * Gets the NRIC of the officer who submitted this registration.
     * 
     * @return the officer's NRIC
     */
    public String getOfficerNric() {
        return officerNric;
    }
    
    /**
     * Gets the name of the project this registration is for.
     * 
     * @return the project name
     */
    public String getProjectName() {
        return projectName;
    }
    
    /**
     * Gets the current status of this registration (pending, approved, or rejected).
     * 
     * @return the registration status
     */
    public OfficerRegistrationStatus getStatus() {
        return status;
    }
    
    /**
     * Gets the timestamp when this registration was submitted.
     * 
     * @return the registration date and time
     */
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    // setters
    /**
     * Updates the status of this registration.
     * Used when a manager approves or rejects the registration.
     * 
     * @param status the new registration status
     */
    public void setStatus(OfficerRegistrationStatus status) {
        this.status = status;
    }
    
    /**
     * Updates the registration date.
     * 
     * @param registrationDate the new registration date
     */
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    /**
     * Sets the unique identifier for this registration.
     * Typically called after registration is created but before saving to database.
     * 
     * @param registrationId the registration ID to set
     */
    public void setRegistrationId(String registrationId){
        this.registrationId = registrationId;
    }
    
    /**
     * Returns a string representation of this officer registration.
     * 
     * @return a string containing all registration details
     */
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
