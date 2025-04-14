package menu;

import auth.AuthenticationSystem;
import auth.RegistrationSystem;
import io.FileIO;
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
import menu.ApplicantMenu;
import menu.OfficerMenu;
import menu.ManagerMenu;

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
        ProjectHandler projectHandler = new ProjectHandler(projectList);
        ApplicationHandler applicationHandler = new ApplicationHandler(applicationList);
        EnquiryHandler enquiryHandler = new EnquiryHandler(enquiryList);
        OfficerRegistrationHandler registrationHandler = new OfficerRegistrationHandler(officerRegistrationList);
        WithdrawalHandler withdrawalHandler = new WithdrawalHandler(withdrawalRequestsList);

    }

    public void displayMainMenu() {
        while (true) {
            System.out.println("\n=== BTO Management System ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = 0;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1:
                    loginFlow();
                    break;
                case 2:
                    registerFlow();
                    break;
                case 3:
                    saveAllData();
                    System.out.println("Thank you for using BTO Management System. Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void loginFlow() {
        User user = authSystem.login(userList, scanner);
        if (user == null) {
            System.out.println("Login failed. Returning to main menu.");
            return;
        }
        // Now call the relevant sub-menu based on user type.
        if (user.getUserType() == UserType.APPLICANT) {
            new ApplicantMenu((Applicant) user, projectHandler, applicationHandler, enquiryHandler, withdrawalHandler).display();
        } else if (user.getUserType() == UserType.MANAGER) {
            new ManagerMenu((ProjectManager) user, projectHandler, applicationHandler, enquiryHandler, registrationHandler, withdrawalHandler).display();
        } else if (user.getUserType() == UserType.OFFICER) {
            // If a HDBOfficer logs in, prompt to choose between officer and applicant functions.
            System.out.println("You are logged in as HDB Officer.");
            System.out.println("Select your mode: ");
            System.out.println("1. Officer Functions");
            System.out.println("2. Applicant Functions");
            System.out.print("Enter your choice: ");
            int mode = 0;
            try {
                mode = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Defaulting to Officer Functions.");
                mode = 1;
            }
            if (mode == 1) {
                new OfficerMenu((HDBOfficer) user, projectHandler, applicationHandler, enquiryHandler, registrationHandler).display();
            } else if (mode == 2) {
                new ApplicantMenu((Applicant) user, projectHandler, applicationHandler, enquiryHandler, withdrawalHandler).display();
            } else {
                System.out.println("Invalid selection. Defaulting to Officer Functions.");
                new OfficerMenu((HDBOfficer) user, projectHandler, applicationHandler, enquiryHandler, registrationHandler).display();
            }
        }
    }

    private void registerFlow() {
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
            return;
        }
        System.out.print("Enter Marital Status (Single/Married): ");
        String maritalStatus = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Enter User Type (APPLICANT, OFFICER, MANAGER): ");
        String userType = scanner.nextLine().trim();

        User newUser = regSystem.registerUser(name, nric, age, maritalStatus, password, userType, userList);
        if (newUser != null) {
            userList.add(newUser);
            System.out.println("Registration successful! Please login from the main menu.");
        } else {
            System.out.println("Registration failed. Please check your details and try again.");
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
}