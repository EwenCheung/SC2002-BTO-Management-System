package models;

import java.time.LocalDateTime;
import models.enums.WithdrawalStatus;

/**
 * Represents a withdrawal request for a BTO application.
 * Contains information about the withdrawal request including its status,
 * the related application, and processing details.
 */
public class WithdrawalRequest {
    private String requestId;
    private String applicationId;
    private String applicantNric;
    private String projectName;
    private WithdrawalStatus status;
    private LocalDateTime requestDate;
    private LocalDateTime processDate;
    private String remarks;

    /**
     * Constructs a new WithdrawalRequest.
     *
     * @param requestId the unique ID of the withdrawal request
     * @param applicationId the associated application ID
     * @param applicantNric the NRIC of the applicant making the request
     * @param projectName the project name related to the withdrawal request
     * @param status the status of the request
     * @param requestDate the date and time when the request was made
     * @param processDate the date and time when the request was processed
     * @param remarks any additional remarks regarding the request
     */
    public WithdrawalRequest(String requestId, String applicationId, String applicantNric,
                               String projectName, WithdrawalStatus status, LocalDateTime requestDate,
                               LocalDateTime processDate, String remarks) {
        this.requestId = requestId;
        this.applicationId = applicationId;
        this.applicantNric = applicantNric;
        this.projectName = projectName;
        this.status = status;
        this.requestDate = requestDate;
        this.processDate = processDate;
        this.remarks = remarks;
    }

    // Getters
    /**
     * Gets the unique identifier for this withdrawal request.
     * 
     * @return The request ID
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Gets the ID of the application being withdrawn.
     * 
     * @return The application ID
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Gets the NRIC of the applicant who submitted the withdrawal request.
     * 
     * @return The applicant's NRIC
     */
    public String getApplicantNric() {
        return applicantNric;
    }

    /**
     * Gets the name of the project associated with the withdrawal request.
     * 
     * @return The project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Gets the current status of the withdrawal request.
     * 
     * @return The withdrawal status
     */
    public WithdrawalStatus getStatus() {
        return status;
    }

    /**
     * Gets the date and time when the withdrawal request was submitted.
     * 
     * @return The request date
     */
    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    /**
     * Gets the date and time when the withdrawal request was processed.
     * 
     * @return The process date, or null if not yet processed
     */
    public LocalDateTime getProcessDate() {
        return processDate;
    }

    /**
     * Gets any remarks or additional notes associated with this withdrawal request.
     * 
     * @return The remarks
     */
    public String getRemarks() {
        return remarks;
    }

    // Setters

    /**
     * Updates the status of the withdrawal request.
     * @param status the new WithdrawalStatus.
     */
    public void setStatus(WithdrawalStatus status) {
        this.status = status;
    }

    /**
     * Sets the process date of the withdrawal request.
     * @param processDate the LocalDateTime when the request is processed.
     */
    public void setProcessDate(LocalDateTime processDate) {
        this.processDate = processDate;
    }

    /**
     * Updates the remarks for the withdrawal request.
     * @param remarks the updated remarks.
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Updates the requestID with unique ID from the handler.
     * @param requestId the updated ID.
     */
    public void setRequestId(String requestId){
        this.requestId = requestId;
    }

    /**
     * Returns a string representation of this withdrawal request.
     * 
     * @return A formatted string containing all withdrawal request details
     */
    @Override
    public String toString() {
        return "WithdrawalRequest{" +
               "requestId='" + requestId + '\'' +
               ", applicationId='" + applicationId + '\'' +
               ", applicantNric='" + applicantNric + '\'' +
               ", projectName='" + projectName + '\'' +
               ", status='" + status + '\'' +
               ", requestDate=" + requestDate +
               ", processDate=" + processDate +
               ", remarks='" + remarks + '\'' +
               '}';
    }
}
