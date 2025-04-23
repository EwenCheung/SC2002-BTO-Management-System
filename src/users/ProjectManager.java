package users;

import users.enums.MaritalStatus;
import users.enums.UserType;

/**
 * Represents a project manager in the BTO Management System.
 * Project managers have administrative privileges to create and manage BTO projects,
 * approve officer registrations, and oversee the application process.
 * This class extends the User base class with project manager specific behavior.
 */
public class ProjectManager extends User{

    /**
     * Constructor for creating a new Project Manager.
     * Sets the user type to MANAGER automatically.
     *
     * @param name           The manager's full name
     * @param nric           The manager's NRIC (National Registration Identity Card) number
     * @param age            The manager's age in years
     * @param maritalStatus  The manager's marital status
     * @param password       The manager's login password
     */
    public ProjectManager(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, UserType.MANAGER, password);
    }

}
