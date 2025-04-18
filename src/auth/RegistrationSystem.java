package auth;

import users.User;
import users.Applicant;
import users.HDBOfficer;
import users.ProjectManager;
import users.enums.MaritalStatus;
import users.enums.UserType;
import io.FileIO;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class RegistrationSystem {
    /**
     * Handles the entire user registration process by scanning inputs from the provided Scanner.
     * Prompts for all necessary registration details and then calls registerUser to process the registration.
     *
     * @param scanner  the Scanner instance for reading user input.
     * @param userList the global in-memory list of users.
     * @return the new User object if registration is successful; null otherwise.
     */
    public User registerUserFromInput(Scanner scanner, List<User> userList) {
        System.out.println("\n=== User Registration ===");

        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter NRIC: ");
        String nric = scanner.nextLine().trim();

        System.out.print("Enter Age: ");
        int age;
        try {
            age = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid age format. Registration failed.");
            return null;
        }

        System.out.print("Enter Marital Status (Single/Married): ");
        String maritalStatus = scanner.nextLine().trim();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Enter User Type (APPLICANT, OFFICER, MANAGER): ");
        String userType = scanner.nextLine().trim();

        // Delegate to the existing registration logic.
        User newUser = registerUser(name, nric, age, maritalStatus, password, userType, userList);
        return newUser;
    }

    // Helper validation methods
    public boolean isValidNRIC(String nric) {
        if (nric == null || nric.length() != 9) {
            return false;
        }
        char firstChar = nric.charAt(0);
        if (firstChar != 'S' && firstChar != 'T') {
            return false;
        }
        String digits = nric.substring(1, 8);
        try {
            Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return false;
        }
        return Character.isLetter(nric.charAt(8));
    }

    public boolean isValidAge(int age) {
        return age >= 18 && age <= 130;
    }

    public boolean isValidMaritalStatus(String status) {
        if (status == null) {
            return false;
        }
        String lowerStatus = status.toLowerCase();
        return lowerStatus.equals("single") || lowerStatus.equals("married");
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Attempts to register a new user (applicant, officer, or manager) based on the input parameters.
     * Validates input, checks if the NRIC already exists, and creates the correct user object.
     * Saves the new user to the appropriate type-specific file.
     *
     * @param name             the user's name.
     * @param nric             the user's NRIC.
     * @param age              the user's age.
     * @param maritalStatusStr the user's marital status as a string ("Single" or "Married").
     * @param password         the user's password.
     * @param userTypeStr      the type of user as a string ("APPLICANT", "OFFICER", or "MANAGER").
     * @param userList         the global in-memory list of users.
     * @return the new User object if registration is successful; null otherwise.
     */
    public User registerUser(String name, String nric, int age, String maritalStatusStr,
                            String password, String userTypeStr, List<User> userList) {
        // Validate input fields.
        if (name == null || name.trim().isEmpty() ||
            nric == null || nric.trim().isEmpty() ||
            age < 18 ||
            maritalStatusStr == null || maritalStatusStr.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            userTypeStr == null || userTypeStr.trim().isEmpty()) {
            System.out.println("Registration failed. Please provide all required fields.");
            return null;
        }

        // Check for existing NRIC in the appropriate user list
        boolean nricExists = false;
        UserType userTypeEnum;
        try {
            userTypeEnum = UserType.valueOf(userTypeStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid user type. Must be APPLICANT, OFFICER, or MANAGER.");
            return null;
        }
        
        // Check for duplicate NRIC in the appropriate file
        switch (userTypeEnum) {
            case APPLICANT:
                List<Applicant> applicants = FileIO.loadApplicants();
                for (Applicant applicant : applicants) {
                    if (applicant.getNric().equalsIgnoreCase(nric.trim())) {
                        nricExists = true;
                        break;
                    }
                }
                break;
            case OFFICER:
                List<HDBOfficer> officers = FileIO.loadOfficers();
                for (HDBOfficer officer : officers) {
                    if (officer.getNric().equalsIgnoreCase(nric.trim())) {
                        nricExists = true;
                        break;
                    }
                }
                break;
            case MANAGER:
                List<ProjectManager> managers = FileIO.loadManagers();
                for (ProjectManager manager : managers) {
                    if (manager.getNric().equalsIgnoreCase(nric.trim())) {
                        nricExists = true;
                        break;
                    }
                }
                break;
        }
        
        if (nricExists) {
            System.out.println("NRIC already exists. Please login instead.");
            return null;
        }

        // Convert marital status string to enum.
        MaritalStatus maritalStatus;
        try {
            maritalStatus = MaritalStatus.valueOf(maritalStatusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid marital status. Must be 'Single' or 'Married'.");
            return null;
        }

        // Create the correct user object based on the user type.
        User newUser = null;
        switch (userTypeEnum) {
            case APPLICANT:
                newUser = new Applicant(name.trim(), nric.trim(), age, maritalStatus, password.trim());
                List<Applicant> applicants = FileIO.loadApplicants();
                applicants.add((Applicant) newUser);
                FileIO.saveApplicants(applicants);
                break;
            case OFFICER:
                newUser = new HDBOfficer(name.trim(), nric.trim(), age, maritalStatus, password.trim());
                List<HDBOfficer> officers = FileIO.loadOfficers();
                officers.add((HDBOfficer) newUser);
                FileIO.saveOfficers(officers);
                break;
            case MANAGER:
                newUser = new ProjectManager(name.trim(), nric.trim(), age, maritalStatus, password.trim());
                List<ProjectManager> managers = FileIO.loadManagers();
                managers.add((ProjectManager) newUser);
                FileIO.saveManagers(managers);
                break;
            default:
                System.out.println("Unknown user type encountered.");
                return null;
        }

        System.out.println("Registration successful! Please proceed to login in the main menu.");
        return newUser;
    }
}