package models.enums;

public enum ApplicationStatus {
    PENDING("Pending"),
    UNDER_REVIEW("Under Review"),
    PENDING_DOCUMENTS("Pending Documents"),
    BACKGROUND_CHECK("Background Check"),
    WITHDRAW_REQUESTED("Withdraw Requested"),
    SUCCESSFUL("Successful"),  // Changed from APPROVED to SUCCESSFUL
    UNSUCCESSFUL("Unsuccessful"),  // REJECTED is now removed, using UNSUCCESSFUL only
    BOOKED("Booked"),
    WITHDRAWN("Unsuccessful");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}