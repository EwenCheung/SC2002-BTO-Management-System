package models.enums;

/**
 * Enumeration of possible statuses for BTO applications.
 * This enum represents the different states an application can be in throughout its lifecycle.
 */
public enum ApplicationStatus {
    /** Application has been submitted but processing has not yet begun */
    PENDING("Pending"),
    
    /** Application is currently being reviewed by HDB officers */
    UNDER_REVIEW("Under Review"),
    
    /** Application requires additional documents from the applicant */
    PENDING_DOCUMENTS("Pending Documents"),
    
    /** Application is undergoing background verification */
    BACKGROUND_CHECK("Background Check"),
    
    /** Applicant has requested to withdraw their application */
    WITHDRAW_REQUESTED("Withdraw Requested"),
    
    /** Application has been approved */
    SUCCESSFUL("Successful"),  // Changed from APPROVED to SUCCESSFUL
    
    /** Application was not approved */
    UNSUCCESSFUL("Unsuccessful"),  // REJECTED is now removed, using UNSUCCESSFUL only
    
    /** Unit has been successfully booked by the applicant */
    BOOKED("Booked"),
    
    /** Application has been withdrawn by the applicant */
    WITHDRAWN("Unsuccessful");

    /**
     * The display name of the status, used for presenting the status to users in a more readable form.
     */
    private final String displayName;

    /**
     * Constructor for ApplicationStatus enum.
     * 
     * @param displayName The human-readable name to display for this status
     */
    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the display name of this application status.
     * 
     * @return The human-readable display name
     */
    @Override
    public String toString() {
        return displayName;
    }
}