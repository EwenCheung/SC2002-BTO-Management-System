package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import models.enums.ApplicationStatus;

/**
 * Represents a BTO housing application in the system.
 * Contains all relevant information about an application including applicant details,
 * project information, application status, and processing details.
 */
public class Application {

    /**
     * Unique identifier for the application.
     */
    private String applicationId;
    
    /**
     * NRIC of the applicant who submitted this application.
     */
    private String applicantNric;
    
    /**
     * Name of the BTO project the application is for.
     */
    private String projectName;
    
    /**
     * Type of housing unit applied for (e.g., "2-Room", "3-Room").
     */
    private String unitType;
    
    /**
     * Current status of the application.
     */
    private ApplicationStatus status;
    
    /**
     * Assigned unit number if application is approved.
     */
    private String assignedUnit;
    
    /**
     * NRIC of the HDB officer assigned to this application.
     */
    private String assignedOfficer;
    
    /**
     * Date and time when the application was submitted.
     */
    private LocalDateTime applicationDate;
    
    /**
     * Date and time when the application was last modified.
     */
    private LocalDateTime lastUpdated;
    
    /**
     * Additional comments or notes about the application.
     */
    private String remarks;
    
    /**
     * Date when the application was approved.
     */
    private LocalDate approvalDate; // Added approval date field

    /**
     * Full constructor for creating an Application with all attributes.
     *
     * @param applicationId    Unique identifier for the application
     * @param applicantNric    NRIC of the applicant
     * @param projectName      Name of the BTO project
     * @param unitType         Type of unit being applied for
     * @param status           Current status of the application
     * @param assignedUnit     Unit identifier assigned to the applicant (if any)
     * @param assignedOfficer  Officer assigned to process the application (if any)
     * @param applicationDate  Date and time when application was submitted
     * @param lastUpdated      Date and time when application was last updated
     * @param remarks          Additional notes or comments on the application
     */
    public Application(String applicationId, String applicantNric, String projectName, String unitType, ApplicationStatus status, String assignedUnit, String assignedOfficer, LocalDateTime applicationDate, LocalDateTime lastUpdated, String remarks){
        this.applicationId = applicationId;
        this.applicantNric = applicantNric;
        this.projectName = projectName;
        this.unitType = unitType;
        this.status = status;
        this.assignedUnit = assignedUnit;
        this.assignedOfficer = assignedOfficer;
        this.applicationDate = applicationDate;
        this.lastUpdated = lastUpdated;
        this.remarks = remarks;
    }

    /**
     * Simplified constructor for creating a new application with minimal information.
     * Sets status to PENDING and initializes timestamps to current date and time.
     *
     * @param applicantNric  NRIC of the applicant
     * @param projectName    Name of the BTO project
     * @param unitType       Type of unit being applied for
     */
    public Application(String applicantNric, String projectName, String unitType) {
        this.applicantNric = applicantNric;
        this.projectName = projectName;
        this.unitType = unitType;
        this.status = ApplicationStatus.PENDING;
        this.applicationDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters
    /**
     * Gets the unique identifier of this application.
     * 
     * @return The application ID
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Gets the NRIC of the applicant.
     * 
     * @return The applicant's NRIC
     */
    public String getApplicantNric() {
        return applicantNric;
    }

    /**
     * Gets the name of the BTO project for this application.
     * 
     * @return The project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Gets the unit type requested in this application.
     * 
     * @return The unit type
     */
    public String getUnitType() {
        return unitType;
    }

    /**
     * Gets the current status of this application.
     * 
     * @return The application status
     */
    public ApplicationStatus getStatus() {
        return status;
    }

    /**
     * Gets the unit identifier assigned to this application, if any.
     * 
     * @return The assigned unit identifier or null if none assigned
     */
    public String getAssignedUnit() {
        return assignedUnit;
    }

    /**
     * Gets the officer assigned to process this application, if any.
     * 
     * @return The assigned officer's identifier or null if none assigned
     */
    public String getAssignedOfficer() {
        return assignedOfficer;
    }

    /**
     * Gets the date and time when this application was submitted.
     * 
     * @return The application submission date and time
     */
    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    /**
     * Gets the date and time when this application was last updated.
     * 
     * @return The last updated date and time
     */
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Gets any remarks or additional notes on this application.
     * 
     * @return The remarks
     */
    public String getRemarks() {
        return remarks;
    }
    
    /**
     * Gets the approval date of this application, if approved.
     * 
     * @return The approval date or null if not approved
     */
    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    // Setters
    /**
     * Updates the status of this application and updates the last updated timestamp.
     * 
     * @param status The new status to set
     */
    public void setStatus(ApplicationStatus status) {
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Sets the assigned unit for this application and updates the last updated timestamp.
     * 
     * @param assignedUnit The unit identifier to assign
     */
    public void setAssignedUnit(String assignedUnit) {
        this.assignedUnit = assignedUnit;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Sets the officer assigned to process this application and updates the last updated timestamp.
     * 
     * @param assignedOfficer The officer identifier to assign
     */
    public void setAssignedOfficer(String assignedOfficer) {
        this.assignedOfficer = assignedOfficer;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Updates the remarks on this application and updates the last updated timestamp.
     * 
     * @param remarks The new remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Sets the application ID for this application.
     * 
     * @param applicationId The application ID to set
     */
    public void setApplicationId(String applicationId){
        this.applicationId = applicationId;
    }
    
    /**
     * Sets the approval date for this application and updates the last updated timestamp.
     * 
     * @param approvalDate The approval date to set
     */
    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Generates a formatted receipt for this application containing all relevant details.
     * 
     * @return A formatted string containing the application details as a receipt
     */
    public String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("=== BTO Application Receipt ===\n")
               .append("Application ID: ").append(applicationId).append("\n")
               .append("Applicant NRIC: ").append(applicantNric).append("\n")
               .append("Project: ").append(projectName).append("\n")
               .append("Unit Type: ").append(unitType).append("\n")
               .append("Status: ").append(status).append("\n")
               .append("Application Date: ").append(applicationDate).append("\n");
        
        if (assignedUnit != null) {
            receipt.append("Assigned Unit: ").append(assignedUnit).append("\n");
        }
        
        if (assignedOfficer != null) {
            receipt.append("Assigned Officer: ").append(assignedOfficer).append("\n");
        }
        
        if (approvalDate != null) {
            receipt.append("Approval Date: ").append(approvalDate).append("\n");
        }
        
        if (remarks != null && !remarks.trim().isEmpty()) {
            receipt.append("Remarks: ").append(remarks).append("\n");
        }
        
        receipt.append("Last Updated: ").append(lastUpdated);
        return receipt.toString();
    }

    /**
     * Returns a string representation of this application.
     * 
     * @return A string containing the application ID, project name, unit type, and status
     */
    @Override
    public String toString() {
        return String.format("Application %s: %s - %s (%s)",
                applicationId, projectName, unitType, status);
    }
}
