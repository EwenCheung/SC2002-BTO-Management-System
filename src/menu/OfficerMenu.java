package menu;

import access.application.OfficerApplicationFeatures;
import access.enquiry.OfficerEnquiryFeatures;
import access.officerregistration.OfficerRegistrationApplicantFeatures;
import access.project.OfficerProjectFeatures;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import models.Application;
import models.Enquiry;
import models.Project;
import models.OfficerRegistration;
import models.WithdrawalRequest;
import users.HDBOfficer;
import users.User;
import utils.Constants;

public class OfficerMenu {
    private Scanner scanner;
    private HDBOfficer officer;
    
    // Interfaces for officer-specific features.
    private OfficerProjectFeatures projectFacade;
    private OfficerApplicationFeatures appFacade;
    private OfficerEnquiryFeatures enquiryFacade;
    private OfficerRegistrationApplicantFeatures regFacade;
    
    // Use the date format constant from Constants.
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
    
    public OfficerMenu(HDBOfficer officer,
                       OfficerProjectFeatures projectFacade,
                       OfficerApplicationFeatures appFacade,
                       OfficerEnquiryFeatures enquiryFacade,
                       OfficerRegistrationApplicantFeatures regFacade) {
        this.scanner = new Scanner(System.in);
        this.officer = officer;
        this.projectFacade = projectFacade;
        this.appFacade = appFacade;
        this.enquiryFacade = enquiryFacade;
        this.regFacade = regFacade;
    }
    
    /**
     * Displays the Officer Menu.
     * 
     * Returns true if the user chooses to switch to the Applicant Menu,
     * or false if the user chooses to logout.
     */
    public boolean display() {
        while (true) {
            System.out.println("\n=== HDB Officer Menu ===");
            System.out.println("Welcome, " + officer.getName());
            System.out.println("\n=== Project Management ===");
            System.out.println("1. Register for Project");
            System.out.println("2. View Registration Status");
            System.out.println("3. View Project Details");
            System.out.println("\n=== Application Processing ===");
            System.out.println("4. Process Application");
            System.out.println("5. Generate Booking Receipt");
            System.out.println("\n=== Enquiries Management ===");
            System.out.println("6. View Project Enquiries");
            System.out.println("7. Reply to Enquiries");
            System.out.println("\n=== System ===");
            System.out.println("8. Change Password");
            System.out.println("9. Switch to Applicant Menu");
            System.out.println("10. Logout");
            System.out.print("Enter your choice: ");
            
            int choice = 0;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }
            
            switch(choice) {
                case 1:
                    registerForProject();
                    break;
                case 2:
                    viewRegistrationStatus();
                    break;
                case 3:
                    viewProjectDetails();
                    break;
                case 4:
                    processApplication();
                    break;
                case 5:
                    generateBookingReceipt();
                    break;
                case 6:
                    viewProjectEnquiries();
                    break;
                case 7:
                    replyToEnquiries();
                    break;
                case 8:
                    changePassword();
                    break;
                case 9:
                    // Return true to indicate switching to Applicant Menu.
                    return true;
                case 10:
                    System.out.println("Logging out...");
                    return false;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    // ----- Officer-Specific Methods -----
    
    private void registerForProject() {
        System.out.println("\n=== Register for Project ===");
        List<Project> availableProjects = projectFacade.getProjectsForOfficer(officer.getNric());
        if (availableProjects.isEmpty()) {
            System.out.println("No projects available for registration.");
            return;
        }
        System.out.println("Available Projects for Registration:");
        for (int i = 0; i < availableProjects.size(); i++) {
            Project proj = availableProjects.get(i);
            System.out.printf("%d. %s (%s)%n", i + 1, proj.getProjectName(), proj.getNeighborhood());
        }
        System.out.print("Enter project number to register (0 to cancel): ");
        int selection;
        try {
            selection = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        if (selection == 0) return;
        if (selection < 1 || selection > availableProjects.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        Project selectedProject = availableProjects.get(selection - 1);
        
        // Check if already registered for this project.
        List<OfficerRegistration> myRegs = regFacade.getRegistrationsForOfficer(officer.getNric());
        for (OfficerRegistration reg : myRegs) {
            if (reg.getProjectName().equalsIgnoreCase(selectedProject.getProjectName())) {
                System.out.println("You have already registered for this project.");
                return;
            }
        }
        // Create new registration with an empty ID; the handler will generate a unique ID.
        OfficerRegistration newReg = new OfficerRegistration(officer.getNric(), selectedProject.getProjectName());
        regFacade.applyForOfficerRegistration(newReg);
        System.out.println("Registration submitted successfully. Pending manager approval.");
    }
    
    private void viewRegistrationStatus() {
        System.out.println("\n=== View Registration Status ===");
        List<OfficerRegistration> regs = regFacade.getRegistrationsForOfficer(officer.getNric());
        if (regs.isEmpty()) {
            System.out.println("No registration requests found.");
        } else {
            for (OfficerRegistration reg : regs) {
                System.out.println(reg);
            }
        }
    }
    
    private void viewProjectDetails() {
        System.out.println("\n=== View Project Details ===");
        List<Project> myProjects = projectFacade.getProjectsForOfficer(officer.getNric());
        if (myProjects.isEmpty()) {
            System.out.println("You are not assigned to any projects.");
        } else {
            for (Project proj : myProjects) {
                System.out.println(proj);
            }
        }
    }
    
    private void processApplication() {
        System.out.println("\n=== Process Application ===");
        System.out.print("Enter Application ID to process: ");
        String appId = scanner.nextLine().trim();
        try {
            appFacade.processApplication(appId);
            System.out.println("Application processed successfully. Status set to BOOKED.");
        } catch (Exception e) {
            System.out.println("Error processing application: " + e.getMessage());
        }
    }
    
    private void generateBookingReceipt() {
        System.out.println("\n=== Generate Booking Receipt ===");
        System.out.print("Enter Application ID: ");
        String appId = scanner.nextLine().trim();
        try {
            String receipt = appFacade.generateReceipt(appId);
            System.out.println("\nBooking Receipt:");
            System.out.println(receipt);
        } catch (Exception e) {
            System.out.println("Error generating receipt: " + e.getMessage());
        }
    }
    
    private void viewProjectEnquiries() {
        System.out.println("\n=== View Project Enquiries ===");
        List<Project> myProjects = projectFacade.getProjectsForOfficer(officer.getNric());
        if (myProjects.isEmpty()) {
            System.out.println("You are not assigned to any projects.");
            return;
        }
        System.out.println("Select a project to view enquiries:");
        for (int i = 0; i < myProjects.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, myProjects.get(i).getProjectName());
        }
        int selection;
        try {
            selection = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        if (selection < 1 || selection > myProjects.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        Project selectedProject = myProjects.get(selection - 1);
        List<models.Enquiry> enqs = enquiryFacade.getEnquiriesForProject(selectedProject.getProjectName());
        if (enqs.isEmpty()) {
            System.out.println("No enquiries for this project.");
        } else {
            for (models.Enquiry enq : enqs) {
                System.out.println(enq);
            }
        }
    }
    
    private void replyToEnquiries() {
        System.out.println("\n=== Reply to Enquiries ===");
        System.out.print("Enter Enquiry ID: ");
        String enqId = scanner.nextLine().trim();
        System.out.print("Enter your response: ");
        String response = scanner.nextLine().trim();
        try {
            enquiryFacade.replyEnquiry(enqId, response);
            System.out.println("Response submitted successfully.");
        } catch (Exception e) {
            System.out.println("Error replying to enquiry: " + e.getMessage());
        }
    }
    
    private void changePassword() {
        System.out.println("\n=== Change Password ===");
        System.out.print("Enter your current password: ");
        String current = scanner.nextLine().trim();
        if (!officer.getPassword().equals(current)) {
            System.out.println("Incorrect current password.");
            return;
        }
        System.out.print("Enter your new password: ");
        String newPass = scanner.nextLine().trim();
        officer.setPassword(newPass);
        System.out.println("Password changed successfully.");
    }
}