package auth;

import java.util.List;
import java.util.Scanner;
import users.User;
import users.enums.UserType;

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

        // Find the user by NRIC
        User foundUser = null;
        for (User user : users) {
            if (user.getNric().equalsIgnoreCase(nric)) {
                // Check if the user type matches the selected one
                if (userTypeChoice == 1 && user.getUserType() != UserType.APPLICANT) {
                    System.out.println("This NRIC is not registered as an Applicant.");
                    return null;
                } else if (userTypeChoice == 2 && user.getUserType() != UserType.OFFICER) {
                    System.out.println("This NRIC is not registered as an Officer.");
                    return null;
                } else if (userTypeChoice == 3 && user.getUserType() != UserType.MANAGER) {
                    System.out.println("This NRIC is not registered as a Manager.");
                    return null;
                }
                
                foundUser = user;
                break;
            }
        }

        if (foundUser == null) {
            System.out.println("User not found. Please register if you're a first-time user.");
            return null;
        }

        // Allow up to 3 attempts for correct password.
        int attempts = 0;
        while (attempts < 3) {
            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();
            if (foundUser.getPassword().equals(password)) {
                System.out.println("Login successful!");
                return foundUser;
            } else {
                attempts++;
                System.out.println("Wrong password. Please try again (" + (3 - attempts) + " attempts left).");
            }
        }

        System.out.println("Too many incorrect attempts. Returning to main menu.");
        return null;
    }
    
    /**
     * Legacy method for backward compatibility
     */
    public User login(List<User> users, Scanner scanner) {
        return login(users, scanner, 0); // 0 means no user type filter
    }
}
