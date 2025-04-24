package menu;

import auth.AuthenticationSystem;
import auth.RegistrationSystem;
import io.FileIO;
import utils.FileUtils;
import utils.UIFormatter;
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

/**
 * Main menu class that serves as the entry point for the BTO Management System.
 * Handles login flow, registration, and navigates to appropriate user menus.
 */
public class MainMenu {
    /** Scanner for reading user input from console */
    private Scanner scanner;
    
    /** Authentication system for handling user login */
    private AuthenticationSystem authSystem;
    
    /** Registration system for handling new user registrations */
    private RegistrationSystem regSystem;
    
    /** In-memory list of all BTO applications in the system */
    private List<Application> applicationList;
    
    /** In-memory list of all enquiries in the system */
    private List<Enquiry> enquiryList;
    
    /** In-memory list of all users registered in the system */
    private List<User> userList;
    
    /** In-memory list of all BTO projects in the system */
    private List<Project> projectList;
    
    /** In-memory list of all officer registrations for projects */
    private List<OfficerRegistration> officerRegistrationList;
    
    /** In-memory list of all withdrawal requests in the system */
    private List<WithdrawalRequest> withdrawalRequestsList;
    
    /** Handler for project-related operations */
    private ProjectHandler projectHandler;
    
    /** Handler for application-related operations */
    private ApplicationHandler applicationHandler;
    
    /** Handler for enquiry-related operations */
    private EnquiryHandler enquiryHandler;
    
    /** Handler for officer registration operations */
    private OfficerRegistrationHandler registrationHandler;
    
    /** Handler for withdrawal request operations */
    private WithdrawalHandler withdrawalHandler;

    /**
     * Constructor for MainMenu.
     * Initializes all required systems, handlers, and loads data from storage.
     */
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

    /**
     * Displays the main menu and handles user interactions.
     * This is the primary entry point for the application.
     */
    public void displayMainMenu() {
        // Initialize color support based on terminal capabilities
        UIFormatter.setColorEnabled(UIFormatter.supportsColors());
        
        while (true) {
            System.out.println(UIFormatter.formatHeader("BTO MANAGEMENT SYSTEM"));
            
            System.out.println(UIFormatter.formatSectionHeader("Main Menu"));
            System.out.println("1. Login");
            System.out.println("2. Register as Applicant");
            System.out.println("3. Exit");
            System.out.println(UIFormatter.formatDivider());
            
            int choice = readChoice("Enter your choice: ", 1, 3);
            
            switch (choice) {
                case 1:
                    loginFlow();
                    break;
                case 2:
                    registerFlow();
                    break;
                case 3:
                    System.out.println(UIFormatter.formatSuccess("Thank you for using BTO Management System. Exiting..."));
                    System.exit(0);
                    break;
                default:
                    System.out.println(UIFormatter.formatError("Invalid choice. Please try again."));
            }
        }
    }

    /**
     * Handles the login process for different user types.
     * Directs users to appropriate menus based on their role.
     */
    private void loginFlow() {
        System.out.println(UIFormatter.formatHeader("LOGIN MENU"));
        System.out.println("1. Applicant Login");
        System.out.println("2. Officer Login");
        System.out.println("3. Manager Login");
        System.out.println("4. Back to Main Menu");
        System.out.println(UIFormatter.formatDivider());
        
        int choice = readChoice("Enter your choice: ", 1, 4);
        if (choice == 4) return;
        
        User user = authSystem.login(userList, scanner, choice);
        if (user == null) {
            System.out.println(UIFormatter.formatError("Login failed. Returning to main menu."));
            return;
        }
        
        // Handle user session with potential mode switching
        handleUserSession(user);
        
    }
    
    /**
     * Handles a user session with support for mode switching between Officer and Applicant modes.
     * Officers can switch between officer functions and applicant functions.
     * 
     * @param user The logged-in user
     */
    private void handleUserSession(User user) {
        boolean keepSessionActive = true;
        boolean officerMode = false; // Track if we're currently in officer mode
        boolean skipModeSelection = false; // Flag to skip mode selection when switching
        
        while (keepSessionActive) {
            if (user.getUserType() == UserType.APPLICANT) {
                // Regular applicant - just display the menu and then logout
                new ApplicantMenu((Applicant) user, projectHandler, applicationHandler, enquiryHandler, withdrawalHandler).display();
                keepSessionActive = false; // Regular applicants don't have mode switching
            } else if (user.getUserType() == UserType.MANAGER) {
                // Manager - just display the menu and then logout
                new ManagerMenu((ProjectManager) user, projectHandler, applicationHandler, enquiryHandler, registrationHandler, withdrawalHandler).display();
                keepSessionActive = false; // Managers don't have mode switching
            } else if (user.getUserType() == UserType.OFFICER) {
                HDBOfficer officer = (HDBOfficer) user;
                
                // Only prompt for mode selection at initial login, not when switching modes
                if (!officerMode && !skipModeSelection) {
                    int mode = promptForMode(officer.getName());
                    
                    if (mode == 1) {
                        // Officer chose Officer mode
                        officerMode = true;
                    } else if (mode == 2) {
                        // Officer chose Applicant mode
                        skipModeSelection = true; // Skip mode selection next time in the loop
                    }
                }
                
                if (officerMode) {
                    // We're in Officer mode
                    boolean switchToApplicant = new OfficerMenu(officer, projectHandler, applicationHandler, 
                                            enquiryHandler, registrationHandler).display();
                    
                    if (switchToApplicant) {
                        // User wants to switch to Applicant mode
                        officerMode = false;
                        skipModeSelection = true; // Skip the mode selection prompt
                        continue;
                    } else {
                        // Officer chose to logout
                        keepSessionActive = false;
                    }
                } else {
                    // We're in Applicant mode
                    // Create a new Applicant with the officer's information but maintain OFFICER user type
                    Applicant officerAsApplicant = new Applicant(
                        officer.getName(),
                        officer.getNric(),
                        officer.getAge(),
                        officer.getMaritalStatus(),
                        officer.getPassword()
                    );
                    // Set the UserType to OFFICER so the ApplicantMenu knows to show the switch option
                    officerAsApplicant.setUserType(UserType.OFFICER);
                    
                    boolean switchToOfficer = new ApplicantMenu(officerAsApplicant, projectHandler, applicationHandler, 
                                   enquiryHandler, withdrawalHandler).display();
                    
                    if (switchToOfficer) {
                        // User wants to switch to Officer mode
                        officerMode = true;
                        continue;
                    } else {
                        // User chose to logout
                        keepSessionActive = false;
                    }
                }
            }
        }
    }
    
    /**
     * Prompts the officer to select a mode (Officer or Applicant).
     * 
     * @param name The name of the officer
     * @return The selected mode (1 for Officer, 2 for Applicant)
     */
    private int promptForMode(String name) {
        System.out.println(UIFormatter.formatHeader("OFFICER LOGIN SUCCESSFUL"));
        System.out.println("Welcome, " + name);
        System.out.println(UIFormatter.formatDivider());
        System.out.println("Select your mode: ");
        System.out.println("1. Officer Functions");
        System.out.println("2. Applicant Functions");
        System.out.println(UIFormatter.formatDivider());
        
        return readChoice("Enter your choice: ", 1, 2);
    }

    /**
     * Handles the applicant registration process.
     * Adds newly registered users to the userList if registration is successful.
     */
    private void registerFlow() {
        System.out.println(UIFormatter.formatHeader("REGISTER AS APPLICANT"));
        
        User newUser = regSystem.registerUserFromInput(scanner, userList);
        if (newUser != null) {
            userList.add(newUser);
            System.out.println(UIFormatter.formatSuccess("Registration successful! You can now login."));
        } else {
            System.out.println(UIFormatter.formatError("Registration failed. Please try again."));
        }
    }

    /**
     * Helper method to read a numeric choice from the user with validation.
     * 
     * @param prompt The prompt to display to the user
     * @param min The minimum acceptable value
     * @param max The maximum acceptable value
     * @return The validated user choice
     */
    private int readChoice(String prompt, int min, int max) {
        while (true) {
            System.out.print(UIFormatter.formatPrompt(prompt));
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.println(UIFormatter.formatError("Please enter a number between " + min + " and " + max));
                }
            } catch (NumberFormatException e) {
                System.out.println(UIFormatter.formatError("Please enter a valid number"));
            }
        }
    }
}