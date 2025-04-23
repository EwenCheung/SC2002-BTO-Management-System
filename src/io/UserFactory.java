package io;

import users.*;
import users.enums.MaritalStatus;
import users.enums.UserType;

/**
 * Factory class responsible for creating User objects from CSV data.
 * Provides methods for creating different types of users (applicants, officers, and managers)
 * from string arrays containing user data.
 */
public class UserFactory {
    /**
     * Creates a User object of the appropriate type based on the provided role.
     * This method is maintained for compatibility with existing code.
     *
     * @param tokens An array of strings with user data: [Name, NRIC, Age, Marital Status, Password, UserType]
     * @return A User object of the appropriate subtype
     * @throws IllegalArgumentException if the user role is unknown
     */
    public static User createUser(String[] tokens) {
        // This method is maintained for compatibility with existing code
        // Format: Name,NRIC,Age,Marital Status,Password,UserType
        String name = tokens[0];
        String nric = tokens[1];
        int age = Integer.parseInt(tokens[2]);
        MaritalStatus maritalStatus = MaritalStatus.valueOf(tokens[3].toUpperCase());
        String password = tokens[4];
        UserType role = UserType.valueOf(tokens[5].toUpperCase());

        switch (role) {
            case APPLICANT:
                return new Applicant(name, nric, age, maritalStatus, password);
            case OFFICER:
                return new HDBOfficer(name, nric, age, maritalStatus, password);
            case MANAGER:
                return new ProjectManager(name, nric, age, maritalStatus, password);
            default:
                throw new IllegalArgumentException("Unknown user role: " + role);
        }
    }
    
    /**
     * Creates an Applicant user from CSV data.
     *
     * @param tokens An array of strings with user data: [Name, NRIC, Age, Marital Status, Password]
     * @return An Applicant object with the provided user data
     * @throws NumberFormatException if age cannot be parsed as integer
     * @throws IllegalArgumentException if marital status is invalid
     */
    public static User createApplicant(String[] tokens) {
        // Format: Name,NRIC,Age,Marital Status,Password
        String name = tokens[0];
        String nric = tokens[1];
        int age = Integer.parseInt(tokens[2]);
        MaritalStatus maritalStatus = MaritalStatus.valueOf(tokens[3].toUpperCase());
        String password = tokens[4];
        
        return new Applicant(name, nric, age, maritalStatus, password);
    }
    
    /**
     * Creates an HDBOfficer user from CSV data.
     *
     * @param tokens An array of strings with user data: [Name, NRIC, Age, Marital Status, Password]
     * @return An HDBOfficer object with the provided user data
     * @throws NumberFormatException if age cannot be parsed as integer
     * @throws IllegalArgumentException if marital status is invalid
     */
    public static User createOfficer(String[] tokens) {
        // Format: Name,NRIC,Age,Marital Status,Password
        String name = tokens[0];
        String nric = tokens[1];
        int age = Integer.parseInt(tokens[2]);
        MaritalStatus maritalStatus = MaritalStatus.valueOf(tokens[3].toUpperCase());
        String password = tokens[4];
        
        return new HDBOfficer(name, nric, age, maritalStatus, password);
    }
    
    /**
     * Creates a ProjectManager user from CSV data.
     *
     * @param tokens An array of strings with user data: [Name, NRIC, Age, Marital Status, Password]
     * @return A ProjectManager object with the provided user data
     * @throws NumberFormatException if age cannot be parsed as integer
     * @throws IllegalArgumentException if marital status is invalid
     */
    public static User createManager(String[] tokens) {
        // Format: Name,NRIC,Age,Marital Status,Password
        String name = tokens[0];
        String nric = tokens[1];
        int age = Integer.parseInt(tokens[2]);
        MaritalStatus maritalStatus = MaritalStatus.valueOf(tokens[3].toUpperCase());
        String password = tokens[4];
        
        return new ProjectManager(name, nric, age, maritalStatus, password);
    }
}
