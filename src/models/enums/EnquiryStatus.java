package models.enums;

/**
 * Enumeration of possible statuses for enquiries in the BTO Management System.
 * An enquiry can either be open (awaiting response) or closed (responded to).
 */
public enum EnquiryStatus {
    /** Enquiry that has not yet been responded to */
    OPEN("Open"),
    
    /** Enquiry that has been responded to and resolved */
    CLOSE("Close");

    private final String displayName;

    /**
     * Constructor for EnquiryStatus enum.
     * 
     * @param displayName The human-readable display name for this status
     */
    EnquiryStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the display name of this enquiry status.
     * 
     * @return The human-readable display name
     */
    @Override
    public String toString() {
        return displayName;
    }
}
