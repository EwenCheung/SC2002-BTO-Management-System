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

    List<WithdrawalRequest> getWithdrawalRequestsByProject(String projectName);
}
