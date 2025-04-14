package access.withdrawal;

import java.util.List;
import models.WithdrawalRequest;

public interface ApplicantWithdrawalFeatures {
    /**
     * Submits a new withdrawal request.
     * @param withdrawalRequest the WithdrawalRequest object to be submitted.
     */
    void requestWithdrawal(WithdrawalRequest withdrawalRequest);
    
    /**
     * Retrieves all withdrawal requests submitted by the applicant.
     * @param applicantNric the applicant's NRIC.
     * @return a list of WithdrawalRequest objects.
     */
    List<WithdrawalRequest> getWithdrawalRequestsForApplicant(String applicantNric);
}