package models.enums;

public enum ApplicationStatus {
    PENDING("Pending"),
    UNDER_REVIEW("Under Review"),
    PENDING_DOCUMENTS("Pending Documents"),
    BACKGROUND_CHECK("Background Check"),
    WITHDRAW_REQUESTED("Withdraw Requested"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    BOOKED("Booked"),
    WITHDRAWN("Withdrawn");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}