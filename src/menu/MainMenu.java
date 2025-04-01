package menu;

import java.util.Scanner;
import auth.AuthenticationSystem;
import auth.RegistrationSystem;
import auth.User;
import auth.UserType;

public class MainMenu {
    private Scanner scanner;
    private AuthenticationSystem authSystem;
    private RegistrationSystem regSystem;

    public MainMenu() {
        this.scanner = new Scanner(System.in);
        this.authSystem = new AuthenticationSystem();
        this.regSystem = new RegistrationSystem();
    }

    public void displayMainMenu() {
        while (true) {
            System.out.println("\n=== BTO Management System ===");
            System.out.println("1. Login");
            System.out.println("2. Register as Applicant");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        showLoginMenu();
                        break;
                    case 2:
                        registerApplicant();
                        break;
                    case 3:
                        System.out.println("Thank you for using BTO Management System");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void showLoginMenu() {
        while (true) {
            System.out.println("\n=== Login Menu ===");
            System.out.println("1. Applicant Login");
            System.out.println("2. Officer Login");
            System.out.println("3. Manager Login");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        handleApplicantLogin();
                        break;
                    case 2:
                        handleOfficerLogin();
                        break;
                    case 3:
                        handleManagerLogin();
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void handleApplicantLogin() {
        System.out.print("Enter NRIC: ");
        String nric = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        User user = authSystem.login(nric, password, UserType.APPLICANT);
        if (user != null) {
            new ApplicantMenu(user).display();
        } else {
            System.out.println("Login failed. Invalid credentials.");
        }
    }

    private void handleOfficerLogin() {
        System.out.print("Enter NRIC: ");
        String nric = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        User user = authSystem.login(nric, password, UserType.OFFICER);
        if (user != null) {
            new OfficerMenu(user).display();
        } else {
            System.out.println("Login failed. Invalid credentials.");
        }
    }

    private void handleManagerLogin() {
        System.out.print("Enter NRIC: ");
        String nric = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        User user = authSystem.login(nric, password, UserType.MANAGER);
        if (user != null) {
            new ManagerMenu(user).display();
        } else {
            System.out.println("Login failed. Invalid credentials.");
        }
    }

    private void registerApplicant() {
        System.out.println("\n=== Applicant Registration ===");
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
            return;
        }

        System.out.print("Enter Marital Status (Single/Married): ");
        String maritalStatus = scanner.nextLine().trim();
        if (!maritalStatus.equalsIgnoreCase("Single") && !maritalStatus.equalsIgnoreCase("Married")) {
            System.out.println("Invalid marital status. Must be 'Single' or 'Married'. Registration failed.");
            return;
        }

        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        if (regSystem.registerApplicant(name, nric, age, maritalStatus, password)) {
            System.out.println("Registration successful! Please login.");
        } else {
            System.out.println("Registration failed. NRIC might already exist.");
        }
    }
}
