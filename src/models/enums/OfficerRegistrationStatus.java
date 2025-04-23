package models.enums;

/**
 * Enumeration of status options for officer registrations to BTO projects.
 * Used to track the approval workflow of officer assignment requests.
 */
public enum OfficerRegistrationStatus {
    /**
     * Registration has been submitted but not yet reviewed by a manager.
     */
    PENDING("Pending"),
    
    /**
     * Registration has been approved by a manager and officer is assigned to the project.
     */
    APPROVED("Approved"),
    
    /**
     * Registration has been rejected by a manager.
     */
    REJECTED("Rejected");

    private final String displayName;

    /**
     * Constructs an OfficerRegistrationStatus with a specified display name.
     * 
     * @param displayName The user-friendly name for display in the UI
     */
    OfficerRegistrationStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the display name of the status for UI presentation.
     * 
     * @return The user-friendly display name
     */
    @Override
    public String toString() {
        return displayName;
    }
}
