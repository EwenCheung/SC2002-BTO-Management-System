package access.withdrawal;

import java.util.ArrayList;
import java.util.List;
import models.WithdrawalRequest;
import models.enums.WithdrawalStatus;
import io.FileIO;

public class WithdrawalHandler implements ManagerWithdrawalFeatures, ApplicantWithdrawalFeatures {
    
    private List<WithdrawalRequest> withdrawals;
    
    public WithdrawalHandler(List<WithdrawalRequest> withdrawals) {
        this.withdrawals = withdrawals;
    }
    
    // Manager methods...
    @Override
    public List<WithdrawalRequest> getAllWithdrawalRequests() {
        return withdrawals;
    }
    
    @Override
    public void approveWithdrawal(String requestId) {
        WithdrawalRequest req = findWithdrawalById(requestId);
        if (req == null) {
            throw new IllegalArgumentException("Withdrawal request not found: " + requestId);
        }
        req.setStatus(WithdrawalStatus.APPROVED);
    }
    
    @Override
    public void rejectWithdrawal(String requestId) {
        WithdrawalRequest req = findWithdrawalById(requestId);
        if (req == null) {
            throw new IllegalArgumentException("Withdrawal request not found: " + requestId);
        }
        req.setStatus(WithdrawalStatus.REJECTED);
    }

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
    
    public void saveChanges() {
        FileIO.saveWithdrawals(withdrawals);
    }
    
    // Applicant methods...
    @Override
    public void requestWithdrawal(WithdrawalRequest withdrawalRequest) {
        if (withdrawalRequest.getRequestId() == null || withdrawalRequest.getRequestId().isEmpty()) {
            withdrawalRequest.setRequestId(generateUniqueId("WDR"));
        }
        withdrawals.add(withdrawalRequest);
    }
    
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


    
    private WithdrawalRequest findWithdrawalById(String requestId) {
        for (WithdrawalRequest wr : withdrawals) {
            if (wr.getRequestId().equalsIgnoreCase(requestId)) {
                return wr;
            }
        }
        return null;
    }
    
    private String generateUniqueId(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }
}