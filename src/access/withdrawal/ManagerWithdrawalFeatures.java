package access.withdrawal;

import java.util.List;
import models.WithdrawalRequest;

public interface ManagerWithdrawalFeatures {
    /**
     * Returns all withdrawal requests.
     * @return a list of WithdrawalRequest objects.
     */
    List<WithdrawalRequest> getAllWithdrawalRequests();
    
    /**
     * Approves a withdrawal request identified by its request ID.
     * @param requestId the unique ID of the withdrawal request.
     */
    void approveWithdrawal(String requestId);
    
    /**
     * Rejects a withdrawal request identified by its request ID.
     * @param requestId the unique ID of the withdrawal request.
     */
    void rejectWithdrawal(String requestId);

    /**
     * Retrieves withdrawal requests for a specific project.
     * @param projectName the name of the project to filter withdrawal requests by.
     * @return a list of WithdrawalRequest objects associated with the specified project.
     */
    List<WithdrawalRequest> getWithdrawalRequestsByProject(String projectName);
}
