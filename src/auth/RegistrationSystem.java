package auth;

import users.User;
import users.Applicant;
import users.HDBOfficer;
import users.ProjectManager;
import users.enums.MaritalStatus;
import users.enums.UserType;
import java.util.List;

public class RegistrationSystem {

    /**
     * Attempts to register a new user (applicant, officer, or manager) based on the input parameters.
     * Validates input, checks if the NRIC already exists in the provided user list, and creates the correct
     * user object if all validations pass.
     *
     * @param name             the user's name.
     * @param nric             the user's NRIC.
     * @param age              the user's age.
     * @param maritalStatusStr the user's marital status as a string ("Single" or "Married").
     * @param password         the user's password.
     * @param userTypeStr      the type of user as a string ("APPLICANT", "OFFICER", or "MANAGER").
     * @param userList         the global in-memory list of users (loaded via FileIO in MainMenu).
     * @return the new User object if registration is successful; null otherwise.
     */
    public User registerUser(String name, String nric, int age, String maritalStatusStr, String password, String userTypeStr, List<User> userList) {
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

        // Check if the NRIC already exists in the in-memory list.
        for (User user : userList) {
            if (user.getNric().equalsIgnoreCase(nric.trim())) {
                System.out.println("NRIC already exists. Please login instead.");
                return null;
            }
        }

        // Convert marital status and user type strings to enums.
        MaritalStatus maritalStatus;
        try {
            maritalStatus = MaritalStatus.valueOf(maritalStatusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid marital status. Must be 'Single' or 'Married'.");
            return null;
        }

        UserType userType;
        try {
            userType = UserType.valueOf(userTypeStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid user type. Must be APPLICANT, OFFICER, or MANAGER.");
            return null;
        }

        // Create the correct user object based on the user type.
        User newUser = null;
        switch (userType) {
            case APPLICANT:
                newUser = new Applicant(name.trim(), nric.trim(), age, maritalStatus, password.trim());
                break;
            case OFFICER:
                newUser = new HDBOfficer(name.trim(), nric.trim(), age, maritalStatus, password.trim());
                break;
            case MANAGER:
                newUser = new ProjectManager(name.trim(), nric.trim(), age, maritalStatus, password.trim());
                break;
            default:
                System.out.println("Unknown user type encountered.");
                return null;
        }

        System.out.println("Registration successful! Please proceed to login in the main menu.");
        return newUser;
    }

    // Helper validation methods can be retained if needed.
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
}
