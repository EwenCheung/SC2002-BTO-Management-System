package auth;

import java.util.List;
import java.util.Scanner;


import users.User;


public class AuthenticationSystem {
public User login(List<User> users, Scanner scanner) {
    System.out.print("Enter NRIC: ");
    String nric = scanner.nextLine().trim();

    // Find the user by NRIC
    User foundUser = null;
    for (User user : users) {
        if (user.getNric().equalsIgnoreCase(nric)) {
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
            return foundUser;
        } else {
            attempts++;
            System.out.println("Wrong password. Please try again (" + (3 - attempts) + " attempts left).");
        }
    }

    System.out.println("Too many incorrect attempts. Returning to main menu.");
    return null;
    }
}
