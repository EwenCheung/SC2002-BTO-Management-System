package models.enums;

/**
 * Enumeration of possible statuses for withdrawal requests in the BTO Management System.
 * These statuses represent the different stages of a withdrawal request's lifecycle.
 */
public enum WithdrawalStatus {
    /** Withdrawal request has been submitted but not yet processed */
    PENDING("Pending"),
    
    /** Withdrawal request has been reviewed and approved */
    APPROVED("Approved"),
    
    /** Withdrawal request has been reviewed and rejected */
    REJECTED("Rejected");

    private final String displayName;

    /**
     * Constructor for WithdrawalStatus enum.
     * 
     * @param displayName The human-readable display name for this status
     */
    WithdrawalStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the display name of this withdrawal status.
     * 
     * @return The human-readable display name
     */
    @Override
    public String toString() {
        return displayName;
    }
}
