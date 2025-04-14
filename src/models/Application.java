package models;

import java.time.LocalDateTime;
import models.enums.ApplicationStatus;

public class Application {

    private String applicationId;
    private String applicantNric;
    private String projectName;
    private String unitType;
    private ApplicationStatus status;
    private String assignedUnit;
    private String assignedOfficer;
    private LocalDateTime applicationDate;
    private LocalDateTime lastUpdated;
    private String remarks;

    public Application(String applicationId, String applicantNric, String projectName, String unitType, ApplicationStatus status, String assignedUnit, String assignedOfficer, LocalDateTime applicationDate, LocalDateTime lastUpdated, String remarks){
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

    public Application(String applicantNric, String projectName, String unitType) {
        this.applicantNric = applicantNric;
        this.projectName = projectName;
        this.unitType = unitType;
        this.status = ApplicationStatus.PENDING;
        this.applicationDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters
    public String getApplicationId() {
        return applicationId;
    }

    public String getApplicantNric() {
        return applicantNric;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getUnitType() {
        return unitType;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public String getAssignedUnit() {
        return assignedUnit;
    }

    public String getAssignedOfficer() {
        return assignedOfficer;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public String getRemarks() {
        return remarks;
    }

    // Setters
    public void setStatus(ApplicationStatus status) {
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }

    public void setAssignedUnit(String assignedUnit) {
        this.assignedUnit = assignedUnit;
        this.lastUpdated = LocalDateTime.now();
    }

    public void setAssignedOfficer(String assignedOfficer) {
        this.assignedOfficer = assignedOfficer;
        this.lastUpdated = LocalDateTime.now();
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
        this.lastUpdated = LocalDateTime.now();
    }

    public void setApplicationId(String applicationId){
        this.applicationId = applicationId;
    }

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
        
        if (remarks != null && !remarks.trim().isEmpty()) {
            receipt.append("Remarks: ").append(remarks).append("\n");
        }
        
        receipt.append("Last Updated: ").append(lastUpdated);
        return receipt.toString();
    }

    @Override
    public String toString() {
        return String.format("Application %s: %s - %s (%s)",
                applicationId, projectName, unitType, status);
    }
}
