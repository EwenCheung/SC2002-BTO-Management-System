package users.enums;

/**
 * Enumeration of user types in the BTO Management System.
 * Defines the three distinct user roles with different permissions and access levels.
 */
public enum UserType {
    /** Represents a housing applicant who can browse projects and submit applications. */
    APPLICANT,
    /** Represents an HDB officer who can process applications and manage projects. */
    OFFICER,
    /** Represents a project manager who oversees all aspects of projects and has administrative privileges. */
    MANAGER
}
