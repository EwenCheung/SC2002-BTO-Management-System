package access.withdrawal;

import java.util.ArrayList;
import java.util.List;
import models.WithdrawalRequest;
import models.enums.WithdrawalStatus;
import io.FileIO;

/**
 * Handles all withdrawal request operations in the BTO Management System.
 * Implements interfaces for managers and applicants to provide
 * role-appropriate access to withdrawal functionality and data.
 */
public class WithdrawalHandler implements ManagerWithdrawalFeatures, ApplicantWithdrawalFeatures {
    
    private List<WithdrawalRequest> withdrawals;
    
    /**
     * Constructs a WithdrawalHandler with the given list of withdrawal requests.
     * 
     * @param withdrawals The list of withdrawal requests to manage
     */
    public WithdrawalHandler(List<WithdrawalRequest> withdrawals) {
        this.withdrawals = withdrawals;
    }
    
    // Manager methods...
    /**
     * Returns all withdrawal requests in the system.
     * Available to managers for oversight and reporting.
     * 
     * @return A list of all WithdrawalRequest objects
     */
    @Override
    public List<WithdrawalRequest> getAllWithdrawalRequests() {
        return withdrawals;
    }
    
    /**
     * Approves a withdrawal request.
     * Available to managers to process applicant withdrawal requests.
     * 
     * @param requestId The ID of the withdrawal request to approve
     * @throws IllegalArgumentException if the request is not found
     */
    @Override
    public void approveWithdrawal(String requestId) {
        WithdrawalRequest req = findWithdrawalById(requestId);
        if (req == null) {
            throw new IllegalArgumentException("Withdrawal request not found: " + requestId);
        }
        req.setStatus(WithdrawalStatus.APPROVED);
        saveChanges(); // Add this line to save changes to CSV
    }
    
    /**
     * Rejects a withdrawal request.
     * Available to managers to process applicant withdrawal requests.
     * 
     * @param requestId The ID of the withdrawal request to reject
     * @throws IllegalArgumentException if the request is not found
     */
    @Override
    public void rejectWithdrawal(String requestId) {
        WithdrawalRequest req = findWithdrawalById(requestId);
        if (req == null) {
            throw new IllegalArgumentException("Withdrawal request not found: " + requestId);
        }
        req.setStatus(WithdrawalStatus.REJECTED);
        saveChanges(); // Add this line to save changes to CSV
    }

    /**
     * Returns all withdrawal requests for a specific project.
     * Available to managers for project-specific monitoring.
     * 
     * @param projectName The name of the project
     * @return A list of WithdrawalRequest objects for the specified project
     */
    @Override
    public List<WithdrawalRequest> getWithdrawalRequestsByProject(String projectName) {
        List<WithdrawalRequest> result = new ArrayList<>();
        for (WithdrawalRequest request : withdrawals) {
            if (request.getProjectName().equalsIgnoreCase(projectName)) {
                result.add(request);
            }
        }
        return result;
    }
    
    /**
     * Saves current withdrawal request data to persistent storage.
     * Called after operations that modify withdrawal request data.
     */
    public void saveChanges() {
        FileIO.saveWithdrawals(withdrawals);
    }
    
    // Applicant methods...
    /**
     * Submits a new withdrawal request.
     * Available to applicants to request withdrawal from a BTO project.
     * 
     * @param withdrawalRequest The withdrawal request to submit
     */
    @Override
    public void requestWithdrawal(WithdrawalRequest withdrawalRequest) {
        if (withdrawalRequest.getRequestId() == null || withdrawalRequest.getRequestId().isEmpty()) {
            withdrawalRequest.setRequestId(generateUniqueId("WDR"));
        }
        withdrawals.add(withdrawalRequest);
    }
    
    /**
     * Returns all withdrawal requests submitted by a specific applicant.
     * Available to applicants to view their withdrawal request history.
     * 
     * @param applicantNric The NRIC of the applicant
     * @return A list of WithdrawalRequest objects submitted by the specified applicant
     */
    @Override
    public List<WithdrawalRequest> getWithdrawalRequestsForApplicant(String applicantNric) {
        List<WithdrawalRequest> result = new ArrayList<>();
        for (WithdrawalRequest wr : withdrawals) {
            if (wr.getApplicantNric().equalsIgnoreCase(applicantNric)) {
                result.add(wr);
            }
        }
        return result;
    }

    /**
     * Finds a withdrawal request by its ID.
     * Helper method used by various public methods that require finding specific requests.
     * 
     * @param requestId The ID of the withdrawal request to find
     * @return The WithdrawalRequest object with the specified ID, or null if not found
     */
    private WithdrawalRequest findWithdrawalById(String requestId) {
        for (WithdrawalRequest wr : withdrawals) {
            if (wr.getRequestId().equalsIgnoreCase(requestId)) {
                return wr;
            }
        }
        return null;
    }
    
    /**
     * Generates a unique ID for a new withdrawal request.
     * Creates IDs in the format: [prefix]-[timestamp] to ensure uniqueness.
     * 
     * @param prefix The prefix to use for the ID (e.g., "WDR")
     * @return A unique ID string
     */
    private String generateUniqueId(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }
}