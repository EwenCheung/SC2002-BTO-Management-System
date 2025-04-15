package menu;

import auth.AuthenticationSystem;
import auth.RegistrationSystem;
import io.FileIO;
import utils.FileUtils;
import java.util.List;
import java.util.Scanner;

import access.application.ApplicationHandler;
import access.enquiry.EnquiryHandler;
import access.officerregistration.OfficerRegistrationHandler;
import access.project.ProjectHandler;
import access.withdrawal.WithdrawalHandler;
import users.Applicant;
import users.HDBOfficer;
import users.ProjectManager;
import users.User;
import users.enums.UserType;
import models.Application;
import models.Enquiry;
import models.Project;
import models.OfficerRegistration;
import models.WithdrawalRequest;

public class MainMenu {
    private Scanner scanner;
    private AuthenticationSystem authSystem;
    private RegistrationSystem regSystem;
    private List<Application> applicationList;
    private List<Enquiry> enquiryList;
    private List<User> userList;
    private List<Project> projectList;
    private List<OfficerRegistration> officerRegistrationList;
    private List<WithdrawalRequest> withdrawalRequestsList;
    private ProjectHandler projectHandler;
    private ApplicationHandler applicationHandler;
    private EnquiryHandler enquiryHandler;
    private OfficerRegistrationHandler registrationHandler;
    private WithdrawalHandler withdrawalHandler;

    public MainMenu() {
        this.scanner = new Scanner(System.in);
        this.authSystem = new AuthenticationSystem();
        this.regSystem = new RegistrationSystem();
        this.applicationList = FileIO.loadApplications();
        this.enquiryList = FileIO.loadEnquiries();
        this.userList = FileIO.loadUsers();
        this.projectList = FileIO.loadProjects();
        this.officerRegistrationList = FileIO.loadOfficerRegistrations();
        this.withdrawalRequestsList = FileIO.loadWithdrawals();
        this.projectHandler = new ProjectHandler(projectList);
        this.applicationHandler = new ApplicationHandler(applicationList);
        this.enquiryHandler = new EnquiryHandler(enquiryList);
        this.registrationHandler = new OfficerRegistrationHandler(officerRegistrationList);
        this.withdrawalHandler = new WithdrawalHandler(withdrawalRequestsList);
    }

    public void displayMainMenu() {
        while (true) {
            printHeader("BTO MANAGEMENT SYSTEM");
            System.out.println("1. Login");
            System.out.println("2. Register as Applicant");
            System.out.println("3. Exit");
            printDivider();
            
            int choice = readChoice("Enter your choice: ", 1, 3);
            
            switch (choice) {
                case 1:
                    loginFlow();
                    break;
                case 2:
                    registerFlow();
                    break;
                case 3:
                    saveAllData();
                    printSuccess("Thank you for using BTO Management System. Exiting...");
                    System.exit(0);
                    break;
                default:
                    printError("Invalid choice. Please try again.");
            }
        }
    }

    private void loginFlow() {
        printHeader("LOGIN MENU");
        System.out.println("1. Applicant Login");
        System.out.println("2. Officer Login");
        System.out.println("3. Manager Login");
        System.out.println("4. Back to Main Menu");
        printDivider();
        
        int choice = readChoice("Enter your choice: ", 1, 4);
        if (choice == 4) return;
        
        User user = authSystem.login(userList, scanner, choice);
        if (user == null) {
            printError("Login failed. Returning to main menu.");
            return;
        }
        
        // Now call the relevant sub-menu based on user type.
        if (user.getUserType() == UserType.APPLICANT) {
            new ApplicantMenu((Applicant) user, projectHandler, applicationHandler, enquiryHandler, withdrawalHandler).display();
        } else if (user.getUserType() == UserType.MANAGER) {
            new ManagerMenu((ProjectManager) user, projectHandler, applicationHandler, enquiryHandler, registrationHandler, withdrawalHandler).display();
        } else if (user.getUserType() == UserType.OFFICER) {
            // If a HDBOfficer logs in, prompt to choose between officer and applicant functions.
            printHeader("OFFICER LOGIN SUCCESSFUL");
            System.out.println("Welcome, " + user.getName());
            printDivider();
            System.out.println("Select your mode: ");
            System.out.println("1. Officer Functions");
            System.out.println("2. Applicant Functions");
            printDivider();
            
            int mode = readChoice("Enter your choice: ", 1, 2);
            
            if (mode == 1) {
                new OfficerMenu((HDBOfficer) user, projectHandler, applicationHandler, enquiryHandler, registrationHandler).display();
            } else if (mode == 2) {
                // Create a new Applicant with the same personal details as the Officer
                HDBOfficer officer = (HDBOfficer) user;
                Applicant applicantView = new Applicant(
                    officer.getName(), 
                    officer.getNric(), 
                    officer.getAge(), 
                    officer.getMaritalStatus(), 
                    officer.getPassword()
                );
                new ApplicantMenu(applicantView, projectHandler, applicationHandler, enquiryHandler, withdrawalHandler).display();
            }
        }
        
        saveAllData(); // Save data when returning from a sub-menu
    }

    private void registerFlow() {
        printHeader("REGISTER AS APPLICANT");
        
        User newUser = regSystem.registerUserFromInput(scanner, userList);
        if (newUser != null) {
            userList.add(newUser);
            printSuccess("Registration successful! You can now login.");
        } else {
            printError("Registration failed. Please try again.");
        }
    }

    private void saveAllData() {
        FileIO.saveApplications(applicationList);
        FileIO.saveEnquiries(enquiryList);
        FileIO.saveProjects(projectList);
        FileIO.saveOfficerRegistrations(officerRegistrationList);
        FileIO.saveUsers(userList);
        FileIO.saveWithdrawals(withdrawalRequestsList);
    }
    
    // UI Helper Methods for consistent look and feel
    private void printHeader(String title) {
        System.out.println("\n" + FileUtils.repeatChar('=', 60));
        System.out.println(FileUtils.repeatChar(' ', (60 - title.length()) / 2) + title);
        System.out.println(FileUtils.repeatChar('=', 60));
    }
    
    private void printDivider() {
        System.out.println(FileUtils.repeatChar('-', 60));
    }
    
    private void printSuccess(String message) {
        System.out.println("\n✓ " + message);
    }
    
    private void printError(String message) {
        System.out.println("\n✗ " + message);
    }
    
    private int readChoice(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    printError("Please enter a number between " + min + " and " + max);
                }
            } catch (NumberFormatException e) {
                printError("Please enter a valid number");
            }
        }
    }
}