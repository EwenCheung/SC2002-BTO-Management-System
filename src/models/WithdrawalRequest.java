package models;

import java.time.LocalDateTime;
import models.enums.WithdrawalStatus;

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

    public String getRequestId() {
        return requestId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getApplicantNric() {
        return applicantNric;
    }

    public String getProjectName() {
        return projectName;
    }

    public WithdrawalStatus getStatus() {
        return status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public LocalDateTime getProcessDate() {
        return processDate;
    }

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
