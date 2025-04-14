package models.enums;

public enum EnquiryStatus {
    OPEN("Open"),
    CLOSE("Close");

    private final String displayName;

    EnquiryStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
