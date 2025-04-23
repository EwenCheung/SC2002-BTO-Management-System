package auth;

import users.User;
import users.Applicant;
import users.enums.MaritalStatus;
import io.FileIO;
import java.util.List;
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
        // Header is handled by MainMenu, so we don't print it here
        System.out.println("Type 'quit' at any prompt to cancel registration and return to the main menu.");

        // Name validation
        String name = "";
        boolean validName = false;
        while (!validName) {
            System.out.print("Enter Name: ");
            name = scanner.nextLine().trim();
            if (name.equalsIgnoreCase("quit")) {
                System.out.println("Registration cancelled.");
                return null;
            }
            if (!isValidName(name)) {
                System.out.println("✗ Name must contain only letters and spaces. Please enter a valid name.");
            } else {
                validName = true;
            }
        }

        // NRIC validation
        String nric = "";
        boolean validNRIC = false;
        while (!validNRIC) {
            System.out.print("Enter NRIC (e.g., S1234567A): ");
            nric = scanner.nextLine().trim();
            if (nric.equalsIgnoreCase("quit")) {
                System.out.println("Registration cancelled.");
                return null;
            }
            if (!isValidNRIC(nric)) {
                System.out.println("✗ Invalid NRIC format. Must be S/T followed by 7 digits and ending with a letter.");
            } else {
                validNRIC = true;
            }
        }

        // Age validation
        int age = 0;
        boolean validAge = false;
        while (!validAge) {
            System.out.print("Enter Age: ");
            String ageInput = scanner.nextLine().trim();
            if (ageInput.equalsIgnoreCase("quit")) {
                System.out.println("Registration cancelled.");
                return null;
            }
            try {
                age = Integer.parseInt(ageInput);
                if (!isValidAge(age)) {
                    System.out.println("✗ Age must be between 18 and 130.");
                } else {
                    validAge = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("✗ You should only key in numbers for age.");
            }
        }

        // Marital Status validation
        String maritalStatusStr = "";
        boolean validMaritalStatus = false;
        while (!validMaritalStatus) {
            System.out.print("Enter Marital Status (Single/Married): ");
            maritalStatusStr = scanner.nextLine().trim();
            if (maritalStatusStr.equalsIgnoreCase("quit")) {
                System.out.println("Registration cancelled.");
                return null;
            }
            if (!isValidMaritalStatus(maritalStatusStr)) {
                System.out.println("✗ Please enter 'Single' or 'Married'.");
            } else {
                validMaritalStatus = true;
            }
        }

        // Password validation
        String password = "";
        boolean validPassword = false;
        while (!validPassword) {
            System.out.print("Enter Password (minimum 6 characters): ");
            password = scanner.nextLine().trim();
            if (password.equalsIgnoreCase("quit")) {
                System.out.println("Registration cancelled.");
                return null;
            }
            if (!isValidPassword(password)) {
                System.out.println("✗ Password must be at least 6 characters long.");
            } else {
                validPassword = true;
            }
        }

        // User type is fixed to APPLICANT
        String userType = "APPLICANT";

        // Delegate to the existing registration logic.
        User newUser = registerUser(name, nric, age, maritalStatusStr, password, userType, userList);
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
    
    public boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        // Check if the name contains only letters and spaces
        return name.chars().allMatch(c -> Character.isLetter(c) || Character.isSpaceChar(c));
    }

    /**
     * Attempts to register a new user as an applicant based on the input parameters.
     * Validates input, checks if the NRIC already exists, and creates the correct user object.
     * Saves the new user to the appropriate type-specific file.
     *
     * @param name             the user's name.
     * @param nric             the user's NRIC.
     * @param age              the user's age.
     * @param maritalStatusStr the user's marital status as a string ("Single" or "Married").
     * @param password         the user's password.
     * @param userTypeStr      the type of user as a string (always "APPLICANT").
     * @param userList         the global in-memory list of users.
     * @return the new User object if registration is successful; null otherwise.
     */
    public User registerUser(String name, String nric, int age, String maritalStatusStr,
                            String password, String userTypeStr, List<User> userList) {
        // Validate input fields - this is a final validation check
        if (name == null || name.trim().isEmpty() ||
            nric == null || nric.trim().isEmpty() ||
            age < 18 ||
            maritalStatusStr == null || maritalStatusStr.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            System.out.println("✗ Registration failed. Please provide all required fields.");
            return null;
        }

        // Check for existing NRIC in the applicant list
        List<Applicant> applicants = FileIO.loadApplicants();
        for (Applicant applicant : applicants) {
            if (applicant.getNric().equalsIgnoreCase(nric.trim())) {
                System.out.println("✗ NRIC already exists. Please login instead.");
                return null;
            }
        }

        // Convert marital status string to enum.
        MaritalStatus maritalStatus;
        try {
            maritalStatus = MaritalStatus.valueOf(maritalStatusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Invalid marital status. Must be 'Single' or 'Married'.");
            return null;
        }

        // Create the new applicant user
        User newUser = new Applicant(name.trim(), nric.trim(), age, maritalStatus, password.trim());
        applicants.add((Applicant) newUser);
        FileIO.saveApplicants(applicants);

        System.out.println("✓ Registration successful! Please proceed to login in the main menu.");
        return newUser;
    }
    
}