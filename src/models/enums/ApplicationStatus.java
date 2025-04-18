package models.enums;

public enum ApplicationStatus {
    PENDING("Pending"),
    UNDER_REVIEW("Under Review"),
    PENDING_DOCUMENTS("Pending Documents"),
    BACKGROUND_CHECK("Background Check"),
    WITHDRAW_REQUESTED("Withdraw Requested"),
    APPROVED("Successful"),
    REJECTED("Unsuccessful"),
    BOOKED("Booked"),
    WITHDRAWN("Unsuccessful"),
    UNSUCCESSFUL("Unsuccessful");  // Added for compatibility with existing data

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}