package auth;

import java.util.List;
import java.util.Scanner;
import users.User;
import users.Applicant;
import users.HDBOfficer;
import users.ProjectManager;
import io.FileIO;

public class AuthenticationSystem {
    /**
     * Logs in a user with NRIC and password
     * 
     * @param users List of all users in the system
     * @param scanner Scanner for input
     * @param userTypeChoice Optional parameter: 1 for Applicant, 2 for Officer, 3 for Manager
     * @return The logged in User, or null if login failed
     */
    public User login(List<User> users, Scanner scanner, int userTypeChoice) {
        System.out.print("Enter NRIC: ");
        String nric = scanner.nextLine().trim();

        // Instead of checking the combined user list, load and check the appropriate type-specific list
        User foundUser = null;
        
        // Based on the user type choice, load the appropriate user list
        if (userTypeChoice == 1) {
            // Applicant login
            List<Applicant> applicants = FileIO.loadApplicants();
            for (User user : applicants) {
                if (user.getNric().equalsIgnoreCase(nric)) {
                    foundUser = user;
                    break;
                }
            }
            if (foundUser == null) {
                System.out.println("Applicant not found. Please register if you're a first-time user.");
                return null;
            }
        } else if (userTypeChoice == 2) {
            // Officer login
            List<HDBOfficer> officers = FileIO.loadOfficers();
            for (User user : officers) {
                if (user.getNric().equalsIgnoreCase(nric)) {
                    foundUser = user;
                    break;
                }
            }
            if (foundUser == null) {
                System.out.println("Officer not found. Please contact administrator if this is an error.");
                return null;
            }
        } else if (userTypeChoice == 3) {
            // Manager login
            List<ProjectManager> managers = FileIO.loadManagers();
            for (User user : managers) {
                if (user.getNric().equalsIgnoreCase(nric)) {
                    foundUser = user;
                    break;
                }
            }
            if (foundUser == null) {
                System.out.println("Manager not found. Please contact administrator if this is an error.");
                return null;
            }
        }

        // Allow up to 3 attempts for correct password.
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;
        
        while (attempts < MAX_ATTEMPTS) {
            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();
            
            if (foundUser.getPassword().equals(password)) {
                System.out.println("Login successful!");
                return foundUser;
            } else {
                attempts++;
                if (attempts < MAX_ATTEMPTS) {
                    System.out.println("Incorrect password. You have " + (MAX_ATTEMPTS - attempts) + " attempts remaining.");
                } else {
                    System.out.println("Too many failed attempts. Please try again later.");
                    return null;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Legacy method for backward compatibility
     */
    public User login(List<User> users, Scanner scanner) {
        return login(users, scanner, 0); // 0 means no user type filter
    }
}
