package menu;

import access.application.OfficerApplicationFeatures;
import access.enquiry.OfficerEnquiryFeatures;
import access.officerregistration.OfficerRegistrationApplicantFeatures;
import access.project.OfficerProjectFeatures;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import models.Application;
import models.Enquiry;
import models.Project;
import models.UnitInfo;
import models.OfficerRegistration;
import models.enums.ApplicationStatus;
import models.enums.EnquiryStatus;
import models.enums.OfficerRegistrationStatus;
import users.HDBOfficer;
import utils.Constants;

public class OfficerMenu {
    private Scanner scanner;
    private HDBOfficer officer;
    
    // Interfaces for officer-specific features
    private OfficerProjectFeatures projectFacade;
    private OfficerApplicationFeatures appFacade;
    private OfficerEnquiryFeatures enquiryFacade;
    private OfficerRegistrationApplicantFeatures regFacade;
    
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
     * @return true if the user chooses to switch to the Applicant Menu,
     *         false if the user chooses to logout.
     */
    public boolean display() {
        while (true) {
            printHeader("HDB OFFICER PORTAL");
            System.out.println("Welcome, " + officer.getName() + " (NRIC: " + officer.getNric() + ")");
            printDivider();
            
            System.out.println("=== Project Management ===");
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
            printDivider();
            
            int choice = readChoice(1, 10);
            if (choice == -1) continue;
            
            switch(choice) {
                case 1: registerForProject(); break;
                case 2: viewRegistrationStatus(); break;
                case 3: viewProjectDetails(); break;
                case 4: processApplication(); break;
                case 5: generateBookingReceipt(); break;
                case 6: viewProjectEnquiries(); break;
                case 7: replyToEnquiries(); break;
                case 8: changePassword(); break;
                case 9: 
                    printMessage("Switching to Applicant Menu...");
                    return true;
                case 10:
                    printMessage("Logging out...");
                    return false;
                default:
                    printError("Invalid choice. Please try again.");
            }
        }
    }
    
    // ----- Project Management Methods -----
    
    private void registerForProject() {
        printHeader("REGISTER FOR PROJECT");
        
        // Check if officer is already registered for a project
        List<OfficerRegistration> myRegistrations = regFacade.getRegistrationsForOfficer(officer.getNric());
        for (OfficerRegistration reg : myRegistrations) {
            if (reg.getStatus() == OfficerRegistrationStatus.APPROVED) {
                printError("You are already registered to handle a project.");
                return;
            } else if (reg.getStatus() == OfficerRegistrationStatus.PENDING) {
                printError("You have a pending registration request.");
                return;
            }
        }
        
        // Get available projects
        List<Project> availableProjects = projectFacade.getProjectsForOfficer(officer.getNric());
        
        if (availableProjects.isEmpty()) {
            printError("No projects available for registration.");
            return;
        }
        
        // Display available projects with remaining slots
        printHeader("AVAILABLE PROJECTS");
        System.out.printf("%-3s %-25s %-15s %-10s %-15s %-15s\n", 
                        "No.", "Project Name", "Neighborhood", "Officer Slots", "Application Period", "Status");
        printDivider();
        
        int validProjects = 0;
        List<Project> projectsWithSlots = new ArrayList<>();
        
        // Get current date to check if projects are active
        java.time.LocalDate today = java.time.LocalDate.now();
        
        for (Project proj : availableProjects) {
            if (proj.getRemainingOfficerSlots() > 0) {
                // Check if project is within application period
                boolean isActive = !today.isBefore(proj.getApplicationOpeningDate()) && 
                                 !today.isAfter(proj.getApplicationClosingDate());
                String status = isActive ? "Active" : "Inactive";
                
                validProjects++;
                projectsWithSlots.add(proj);
                System.out.printf("%-3d %-25s %-15s %-10d %-15s %-15s\n", 
                    validProjects, 
                    truncate(proj.getProjectName(), 25),
                    truncate(proj.getNeighborhood(), 15),
                    proj.getRemainingOfficerSlots(),
                    proj.getApplicationOpeningDate() + " to " + proj.getApplicationClosingDate(),
                    status
                );
            }
        }
        
        if (validProjects == 0) {
            printError("No projects with available officer slots.");
            return;
        }
        
        // Select project
        System.out.print("\nSelect project number to register (0 to cancel): ");
        int selection = readChoice(0, validProjects);
        if (selection == 0 || selection == -1) {
            printMessage("Registration cancelled.");
            return;
        }
        
        Project selectedProject = projectsWithSlots.get(selection - 1);
        
        // Check if project is active before allowing registration
        boolean isActive = !today.isBefore(selectedProject.getApplicationOpeningDate()) && 
                         !today.isAfter(selectedProject.getApplicationClosingDate());
        
        if (!isActive) {
            if (!readYesNo("This project is not currently in its active application period. Do you still want to register? (Y/N): ")) {
                printMessage("Registration cancelled.");
                return;
            }
        }
        
        // Confirm registration
        printHeader("CONFIRM REGISTRATION");
        System.out.println("Project: " + selectedProject.getProjectName());
        System.out.println("Neighborhood: " + selectedProject.getNeighborhood());
        System.out.println("Manager: " + selectedProject.getManager());
        System.out.println("Application Period: " + selectedProject.getApplicationOpeningDate() + 
                         " to " + selectedProject.getApplicationClosingDate());
        printDivider();
        
        System.out.print("Confirm registration for this project? (Y/N): ");
        if (!readYesNo()) {
            printMessage("Registration cancelled.");
            return;
        }
        
        // Create and submit registration
        OfficerRegistration newReg = new OfficerRegistration(officer.getNric(), selectedProject.getProjectName());
        regFacade.applyForOfficerRegistration(newReg);
        printSuccess("Registration submitted successfully. Pending manager approval.");
    }
    
    private void viewRegistrationStatus() {
        printHeader("REGISTRATION STATUS");
        
        List<OfficerRegistration> regs = regFacade.getRegistrationsForOfficer(officer.getNric());
        if (regs.isEmpty()) {
            printMessage("No registration requests found.");
            return;
        }
        
        System.out.printf("%-20s %-25s %-15s %-20s\n", "Registration ID", "Project", "Status", "Registration Date");
        printDivider();
        
        for (OfficerRegistration reg : regs) {
            System.out.printf("%-20s %-25s %-15s %-20s\n", 
                reg.getRegistrationId(),
                truncate(reg.getProjectName(), 25),
                reg.getStatus(),
                reg.getRegistrationDate()
            );
        }
    }
    
    private void viewProjectDetails() {
        printHeader("MY ASSIGNED PROJECTS");
        
        List<Project> myProjects = getAssignedProjects();
        if (myProjects.isEmpty()) {
            printMessage("You are not assigned to any projects yet.");
            return;
        }
        
        for (int i = 0; i < myProjects.size(); i++) {
            Project proj = myProjects.get(i);
            System.out.println((i + 1) + ". " + proj.getProjectName() + " (" + proj.getNeighborhood() + ")");
        }
        
        System.out.print("\nSelect project number for details (0 to go back): ");
        int choice = readChoice(0, myProjects.size());
        if (choice == 0 || choice == -1) return;
        
        Project selected = myProjects.get(choice - 1);
        printHeader("PROJECT DETAILS: " + selected.getProjectName());
        System.out.println(selected.toString());
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    // ----- Application Processing Methods -----
    
    private void processApplication() {
        printHeader("PROCESS APPLICATION");
        
        List<Project> myProjects = getAssignedProjects();
        if (myProjects.isEmpty()) {
            printError("You are not assigned to any projects. Cannot process applications.");
            return;
        }
        
        printHeader("SELECT PROJECT");
        for (int i = 0; i < myProjects.size(); i++) {
            System.out.printf("%d. %s (%s)%n", i + 1, myProjects.get(i).getProjectName(), myProjects.get(i).getNeighborhood());
        }
        
        int projectChoice = readChoice("Select project (0 to cancel): ", 0, myProjects.size());
        if (projectChoice == 0) return;
        
        Project selectedProject = myProjects.get(projectChoice - 1);
        
        // Display unit availability before showing applications
        printHeader("UNIT AVAILABILITY FOR " + selectedProject.getProjectName());
        for (String unitType : selectedProject.getUnits().keySet()) {
            UnitInfo unitInfo = selectedProject.getUnits().get(unitType);
            System.out.printf("%s: %d/%d units available (Price: $%.2f)%n", 
                             unitType, 
                             unitInfo.getAvailableUnits(), 
                             unitInfo.getTotalUnits(),
                             unitInfo.getSellingPrice());
        }
        printDivider();
        
        List<Application> applications = appFacade.getApplicationsForProject(selectedProject.getProjectName());
        
        // Filter for applications that are approved but not yet booked
        List<Application> pendingBookingApps = new ArrayList<>();
        for (Application app : applications) {
            if (app.getStatus().toString().equalsIgnoreCase("APPROVED") && 
                (app.getAssignedOfficer() == null || app.getAssignedOfficer().equals(officer.getNric()))) {
                pendingBookingApps.add(app);
            }
        }
        
        if (pendingBookingApps.isEmpty()) {
            printError("No approved applications pending booking for this project.");
            return;
        }
        
        printHeader("APPROVED APPLICATIONS PENDING BOOKING");
        System.out.printf("%-5s %-15s %-15s %-10s %-10s %-20s%n", 
                          "No.", "Application ID", "Applicant NRIC", "Unit Type", "Unit", "Approval Date");
        printDivider();
        
        for (int i = 0; i < pendingBookingApps.size(); i++) {
            Application app = pendingBookingApps.get(i);
            System.out.printf("%-5d %-15s %-15s %-10s %-10s %-20s%n", 
                            i + 1, 
                            truncate(app.getApplicationId(), 15),
                            app.getApplicantNric(),
                            app.getUnitType(),
                            (app.getAssignedUnit() != null) ? app.getAssignedUnit() : "None",
                            app.getLastUpdated().toLocalDate());
        }
        
        int appChoice = readChoice("Select application to process (0 to cancel): ", 0, pendingBookingApps.size());
        if (appChoice == 0) return;
        
        Application selectedApp = pendingBookingApps.get(appChoice - 1);
        String unitType = selectedApp.getUnitType();
        
        // Check unit availability for the selected application
        int availableUnits = selectedProject.getUnits().get(unitType).getAvailableUnits();
        
        printHeader("PROCESS APPLICATION: " + selectedApp.getApplicationId());
        System.out.println("Applicant NRIC: " + selectedApp.getApplicantNric());
        System.out.println("Project: " + selectedApp.getProjectName());
        System.out.println("Unit Type: " + unitType);
        System.out.println("Available Units: " + availableUnits);
        
        if (selectedApp.getAssignedUnit() != null) {
            System.out.println("Assigned Unit: " + selectedApp.getAssignedUnit());
        }
        
        // Warn if no units are available
        if (availableUnits <= 0 && selectedApp.getAssignedUnit() == null) {
            printError("WARNING: No " + unitType + " units are currently available!");
            if (!readYesNo("Do you still want to continue with processing? (Y/N): ")) {
                printMessage("Processing cancelled.");
                return;
            }
        }
        
        if (!readYesNo("Confirm booking for this application? (Y/N): ")) {
            printMessage("Processing cancelled.");
            return;
        }
        
        try {
            // Assign the officer to the application if not already assigned
            if (selectedApp.getAssignedOfficer() == null || selectedApp.getAssignedOfficer().isEmpty()) {
                selectedApp.setAssignedOfficer(officer.getNric());
                // Use processApplication method instead of updateApplication
                // Just store the updated unit and officer assignment
                String unitNumber = null;
                if (selectedApp.getAssignedUnit() == null && availableUnits > 0) {
                    // Generate a unit number
                    unitNumber = generateUnitNumber(unitType, selectedProject.getProjectName());
                    selectedApp.setAssignedUnit(unitNumber);
                    
                    // Decrease the available units in the project
                    projectFacade.decreaseAvailableUnits(selectedProject.getProjectName(), unitType, 1);
                } else if (availableUnits <= 0 && selectedApp.getAssignedUnit() == null) {
                    printWarning("No units available. Booking will be processed without assigning a unit number.");
                }
            }
            
            // Process the booking
            appFacade.processApplication(selectedApp.getApplicationId());
            printSuccess("Application processed successfully. Status set to BOOKED.");
            
            if (readYesNo("Generate booking receipt? (Y/N): ")) {
                String receipt = appFacade.generateReceipt(selectedApp.getApplicationId());
                printHeader("BOOKING RECEIPT");
                System.out.println(receipt);
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        } catch (Exception e) {
            printError("Error processing application: " + e.getMessage());
        }
    }
    
    /**
     * Generates a unique unit number for a new assignment
     *
     * @param unitType The type of unit (2-Room or 3-Room)
     * @param projectName The project name
     * @return A generated unit number
     */
    private String generateUnitNumber(String unitType, String projectName) {
        // Generate a unique identifier based on project name
        String projectCode = projectName.substring(0, Math.min(3, projectName.length())).toUpperCase();
        
        // Get the unit type prefix
        String typePrefix = unitType.startsWith("2") ? "2R" : "3R";
        
        // Generate a random number between 100 and 999
        int random = 100 + (int)(Math.random() * 900);
        
        // Combine to form a unit number
        return projectCode + "-" + typePrefix + "-" + random;
    }
    
    private void printWarning(String message) {
        System.out.println("\n⚠ " + message);
    }
    
    private void generateBookingReceipt() {
        printHeader("GENERATE BOOKING RECEIPT");
        
        System.out.print("Enter Application ID: ");
        String appId = scanner.nextLine().trim();
        if (appId.isEmpty()) {
            printError("Application ID cannot be empty.");
            return;
        }
        
        try {
            String receipt = appFacade.generateReceipt(appId);
            printHeader("BOOKING RECEIPT");
            System.out.println(receipt);
        } catch (Exception e) {
            printError("Error generating receipt: " + e.getMessage());
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    // ----- Enquiries Management Methods -----
    
    private void viewProjectEnquiries() {
        printHeader("VIEW PROJECT ENQUIRIES");
        
        List<Project> myProjects = getAssignedProjects();
        if (myProjects.isEmpty()) {
            printError("You are not assigned to any projects.");
            return;
        }
        
        System.out.println("Select a project to view enquiries:");
        for (int i = 0; i < myProjects.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, myProjects.get(i).getProjectName());
        }
        
        int selection = readChoice(1, myProjects.size());
        if (selection == -1) return;
        
        Project selectedProject = myProjects.get(selection - 1);
        List<Enquiry> enqs = enquiryFacade.getEnquiriesForProject(selectedProject.getProjectName());
        
        if (enqs.isEmpty()) {
            printMessage("No enquiries for " + selectedProject.getProjectName());
            return;
        }
        
        viewEnquiriesForProject(selectedProject, enqs);
    }

    private void viewEnquiriesForProject(Project project, List<Enquiry> enquiries) {
        while (true) {
            printHeader("ENQUIRIES FOR " + project.getProjectName());
            System.out.printf("%-5s %-15s %-15s %-40s %-15s\n", 
                            "No.", "Enquiry ID", "Applicant", "Message", "Status");
            printDivider();
            
            for (int i = 0; i < enquiries.size(); i++) {
                Enquiry enq = enquiries.get(i);
                System.out.printf("%-5d %-15s %-15s %-40s %-15s\n", 
                                i + 1, 
                                enq.getEnquiryId(), 
                                truncate(enq.getApplicantNric(), 15),
                                truncate(enq.getMessage(), 40),
                                (enq.getReply() == null || enq.getReply().isEmpty()) ? "Pending" : "Responded"
                );
            }
            
            printDivider();
            System.out.println("1. View enquiry details");
            System.out.println("2. Reply to an enquiry");
            System.out.println("3. Filter by status (pending/responded)");
            System.out.println("4. Back to main menu");
            
            int menuChoice = readChoice("Select an option: ", 1, 4);
            if (menuChoice == -1) continue;
            
            if (menuChoice == 4) return;
            
            if (menuChoice == 1) {
                // View enquiry details
                int enqChoice = readChoice("Select enquiry number to view (0 to cancel): ", 0, enquiries.size());
                if (enqChoice == 0 || enqChoice == -1) continue;
                
                viewEnquiryDetails(enquiries.get(enqChoice - 1));
            } else if (menuChoice == 2) {
                // Redirect to reply function
                replyToEnquiryFromList(project, enquiries);
                return; // Return to main menu after replying
            } else if (menuChoice == 3) {
                // Filter by status
                System.out.println("Filter by:");
                System.out.println("1. Pending enquiries");
                System.out.println("2. Responded enquiries");
                System.out.println("3. All enquiries");
                
                int filterChoice = readChoice("Select filter: ", 1, 3);
                if (filterChoice == -1) continue;
                
                List<Enquiry> filteredEnquiries = new ArrayList<>();
                
                if (filterChoice == 1) {
                    // Pending enquiries
                    for (Enquiry enq : enquiries) {
                        if (enq.getReply() == null || enq.getReply().isEmpty()) {
                            filteredEnquiries.add(enq);
                        }
                    }
                    if (filteredEnquiries.isEmpty()) {
                        printMessage("No pending enquiries found.");
                        continue;
                    }
                    viewEnquiriesForProject(project, filteredEnquiries);
                    return;
                } else if (filterChoice == 2) {
                    // Responded enquiries
                    for (Enquiry enq : enquiries) {
                        if (enq.getReply() != null && !enq.getReply().isEmpty()) {
                            filteredEnquiries.add(enq);
                        }
                    }
                    if (filteredEnquiries.isEmpty()) {
                        printMessage("No responded enquiries found.");
                        continue;
                    }
                    viewEnquiriesForProject(project, filteredEnquiries);
                    return;
                } else {
                    // All enquiries - just continue the loop
                    continue;
                }
            }
        }
    }

    private void viewEnquiryDetails(Enquiry enquiry) {
        printHeader("ENQUIRY DETAILS");
        System.out.println("Enquiry ID: " + enquiry.getEnquiryId());
        System.out.println("Project: " + enquiry.getProjectName());
        System.out.println("From: " + enquiry.getApplicantNric());
        System.out.println("Date: " + ((getEnquiryDate(enquiry) != null) ? 
                                      getEnquiryDate(enquiry).toLocalDate() : "Unknown"));
        
        System.out.println("\nMessage:");
        System.out.println(enquiry.getMessage());
        
        if (enquiry.getReply() != null && !enquiry.getReply().isEmpty()) {
            System.out.println("\nResponse:");
            System.out.println(enquiry.getReply());
            System.out.println("\nResponded by: " + enquiry.getRespondentNric());
            System.out.println("Response date: " + ((getResponseDate(enquiry) != null) ? 
                                                   getResponseDate(enquiry).toLocalDate() : "Unknown"));
        } else {
            System.out.println("\nStatus: Pending response");
            if (readYesNo("\nWould you like to respond to this enquiry now? (Y/N): ")) {
                respondToEnquiry(enquiry);
            }
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void replyToEnquiries() {
        printHeader("REPLY TO ENQUIRIES");
        
        List<Project> myProjects = getAssignedProjects();
        if (myProjects.isEmpty()) {
            printError("You are not assigned to any projects.");
            return;
        }
        
        System.out.println("Select a project to reply to enquiries:");
        for (int i = 0; i < myProjects.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, myProjects.get(i).getProjectName());
        }
        
        int projectChoice = readChoice("Select project (0 to cancel): ", 0, myProjects.size());
        if (projectChoice == 0) return;
        
        Project selectedProject = myProjects.get(projectChoice - 1);
        List<Enquiry> allEnquiries = enquiryFacade.getEnquiriesForProject(selectedProject.getProjectName());
        
        // Filter for unanswered enquiries
        List<Enquiry> pendingEnquiries = new ArrayList<>();
        for (Enquiry enq : allEnquiries) {
            if (enq.getReply() == null || enq.getReply().isEmpty()) {
                pendingEnquiries.add(enq);
            }
        }
        
        if (pendingEnquiries.isEmpty()) {
            printMessage("No pending enquiries for " + selectedProject.getProjectName());
            return;
        }
        
        replyToEnquiryFromList(selectedProject, pendingEnquiries);
    }

    private void replyToEnquiryFromList(Project project, List<Enquiry> enquiries) {
        // Filter for unanswered enquiries
        List<Enquiry> pendingEnquiries = new ArrayList<>();
        for (Enquiry enq : enquiries) {
            if (enq.getReply() == null || enq.getReply().isEmpty()) {
                pendingEnquiries.add(enq);
            }
        }
        
        if (pendingEnquiries.isEmpty()) {
            printMessage("No pending enquiries to reply to.");
            return;
        }
        
        printHeader("PENDING ENQUIRIES FOR " + project.getProjectName());
        System.out.printf("%-5s %-15s %-15s %-40s %-15s\n", 
                        "No.", "Enquiry ID", "Applicant", "Message", "Timestamp");
        printDivider();
        
        for (int i = 0; i < pendingEnquiries.size(); i++) {
            Enquiry enq = pendingEnquiries.get(i);
            System.out.printf("%-5d %-15s %-15s %-40s %-15s\n", 
                            i + 1, 
                            enq.getEnquiryId(), 
                            truncate(enq.getApplicantNric(), 15),
                            truncate(enq.getMessage(), 40),
                            (getEnquiryDate(enq) != null) ? getEnquiryDate(enq).toLocalDate() : "Unknown");
        }
        
        int enqChoice = readChoice("Select enquiry to reply (0 to cancel): ", 0, pendingEnquiries.size());
        if (enqChoice == 0) return;
        
        respondToEnquiry(pendingEnquiries.get(enqChoice - 1));
    }

    private void respondToEnquiry(Enquiry enquiry) {
        printHeader("REPLY TO ENQUIRY");
        System.out.println("Enquiry ID: " + enquiry.getEnquiryId());
        System.out.println("From: " + enquiry.getApplicantNric());
        System.out.println("Date: " + ((getEnquiryDate(enquiry) != null) ? 
                                      getEnquiryDate(enquiry).toLocalDate() : "Unknown"));
        System.out.println("\nEnquiry:");
        System.out.println(enquiry.getMessage());
        printDivider();
        
        System.out.println("Enter your response (Type '/cancel' to cancel):");
        String response = scanner.nextLine().trim();
        
        if (response.equals("/cancel")) {
            printMessage("Reply cancelled.");
            return;
        }
        
        if (response.isEmpty()) {
            printError("Response cannot be empty.");
            return;
        }
        
        try {
            // Use the officer's NRIC for the response
            enquiryFacade.replyEnquiry(enquiry.getEnquiryId(), response);
            printSuccess("Response submitted successfully.");
        } catch (Exception e) {
            printError("Error replying to enquiry: " + e.getMessage());
        }
    }
    
    // ----- System Methods -----
    
    private void changePassword() {
        printHeader("CHANGE PASSWORD");
        
        System.out.print("Enter your current password: ");
        String current = scanner.nextLine().trim();
        if (!officer.getPassword().equals(current)) {
            printError("Incorrect current password.");
            return;
        }
        
        System.out.print("Enter your new password: ");
        String newPass = scanner.nextLine().trim();
        if (newPass.isEmpty()) {
            printError("New password cannot be empty.");
            return;
        }
        
        officer.setPassword(newPass);
        printSuccess("Password changed successfully.");
    }
    
    // ----- Helper Methods -----
    
    private List<Project> getAssignedProjects() {
        // Need to use the method provided by the interface, which is getProjectsForOfficer
        return projectFacade.getProjectsForOfficer(officer.getNric());
    }
    
    // Helper method to get the enquiry date (submitted date) from an Enquiry
    private LocalDateTime getEnquiryDate(Enquiry enquiry) {
        return enquiry.getSubmittedAt();
    }
    
    // Helper method to get the response date (replied date) from an Enquiry
    private LocalDateTime getResponseDate(Enquiry enquiry) {
        return enquiry.getRepliedAt();
    }
    
    // ----- UI Helper Methods -----
    
    private void printHeader(String title) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println(" ".repeat((70 - title.length()) / 2) + title);
        System.out.println("=".repeat(70));
    }
    
    private void printDivider() {
        System.out.println("-".repeat(70));
    }
    
    private void printMessage(String message) {
        System.out.println("\n" + message);
    }
    
    private void printSuccess(String message) {
        System.out.println("\n✓ " + message);
    }
    
    private void printError(String message) {
        System.out.println("\n✗ " + message);
    }
    
    private int readChoice(int min, int max) {
        System.out.print("Enter your choice: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < min || choice > max) {
                printError("Invalid choice. Please enter a number between " + min + " and " + max + ".");
                return -1;
            }
            return choice;
        } catch (NumberFormatException e) {
            printError("Please enter a valid number.");
            return -1;
        }
    }
    
    private int readChoice(String prompt, int min, int max) {
        System.out.print(prompt);
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < min || choice > max) {
                printError("Invalid choice. Please enter a number between " + min + " and " + max + ".");
                return -1;
            }
            return choice;
        } catch (NumberFormatException e) {
            printError("Please enter a valid number.");
            return -1;
        }
    }
    
    private boolean readYesNo(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim().toUpperCase();
        return input.equals("Y") || input.equals("YES");
    }
    
    private boolean readYesNo() {
        String input = scanner.nextLine().trim().toUpperCase();
        return input.equals("Y") || input.equals("YES");
    }
    
    private String truncate(String str, int length) {
        if (str == null) return "";
        if (str.length() <= length) return str;
        return str.substring(0, length - 3) + "...";
    }
}