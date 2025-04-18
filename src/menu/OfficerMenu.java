package menu;

import access.application.OfficerApplicationFeatures;
import access.enquiry.OfficerEnquiryFeatures;
import access.officerregistration.OfficerRegistrationApplicantFeatures;
import access.project.OfficerProjectFeatures;
import access.project.ProjectHandler;
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
import users.ProjectManager;
import users.Applicant;
import utils.Constants;
import utils.FileUtils;
import utils.AuthenticationSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

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
            System.out.println("9. Switch to Applicant Mode");
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
                    printMessage("Switching to Applicant Mode...");
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
        
        // Get officer's current registrations
        List<OfficerRegistration> myRegistrations = regFacade.getRegistrationsForOfficer(officer.getNric());
        
        // Check for active registrations (approved or pending)
        boolean hasActiveRegistration = false;
        for (OfficerRegistration reg : myRegistrations) {
            if (reg.getStatus() == OfficerRegistrationStatus.APPROVED) {
                printError("You are already registered to handle a project.");
                return;
            } else if (reg.getStatus() == OfficerRegistrationStatus.PENDING) {
                printError("You have a pending registration request.");
                return;
            }
        }
        
        // Get ALL available projects with open officer slots
        List<Project> availableProjects;
        if (projectFacade instanceof ProjectHandler) {
            availableProjects = ((ProjectHandler) projectFacade).getProjectsWithOpenSlots();
        } else {
            // Fallback to original method if projectFacade is not an instance of ProjectHandler
            availableProjects = projectFacade.getProjectsForOfficer(officer.getNric());
        }
        
        if (availableProjects.isEmpty()) {
            printError("No projects available for registration.");
            return;
        }
        
        // Display available projects with remaining slots
        printHeader("AVAILABLE PROJECTS");
        System.out.printf("%-3s %-25s %-15s %12s    %-15s %-15s %-12s\n", 
                        "No.", "Project Name", "Neighborhood", "Officer Slots", "Opening Date", "Closing Date", "Status");
        printDivider();
        
        int validProjects = 0;
        List<Project> projectsWithSlots = new ArrayList<>();
        
        // Get current date to check if projects are active
        java.time.LocalDate today = java.time.LocalDate.now();
        
        // DateTimeFormatter for displaying dates in a consistent format
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        
        for (Project proj : availableProjects) {
            // Show all projects with remaining slots, even if the officer is already part of the project
            if (proj.getRemainingOfficerSlots() > 0) {
                // Check if project is within application period or will be in the future
                boolean isActive = !today.isAfter(proj.getApplicationClosingDate());
                String status = today.isBefore(proj.getApplicationOpeningDate()) ? "Upcoming" : 
                               (today.isAfter(proj.getApplicationClosingDate()) ? "Closed" : "Active");
                
                validProjects++;
                projectsWithSlots.add(proj);
                
                // Format the dates for display
                String openingDate = proj.getApplicationOpeningDate().format(dateFormatter);
                String closingDate = proj.getApplicationClosingDate().format(dateFormatter);
                
                // Center-align the officer slots data
                String officerSlotsStr = String.format("%d/%d", 
                    proj.getOfficerSlot() - proj.getRemainingOfficerSlots(), 
                    proj.getOfficerSlot());
                
                System.out.printf("%-3d %-25s %-15s %12s    %-15s %-15s %-12s\n", 
                    validProjects, 
                    truncate(proj.getProjectName(), 25),
                    truncate(proj.getNeighborhood(), 15),
                    officerSlotsStr,
                    openingDate,
                    closingDate,
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
        
        // Check if this project's dates clash with any existing registrations
        if (hasDateOverlap(selectedProject, myRegistrations)) {
            printError("You cannot register for this project as its application period overlaps with another project you are already registered for.");
            System.out.println("Selected project period: " + selectedProject.getApplicationOpeningDate() + " to " + selectedProject.getApplicationClosingDate());
            
            // Show the overlapping project
            for (OfficerRegistration reg : myRegistrations) {
                if (reg.getStatus() == OfficerRegistrationStatus.APPROVED || 
                    reg.getStatus() == OfficerRegistrationStatus.PENDING) {
                    if (projectFacade instanceof ProjectHandler) {
                        Project existingProject = ((ProjectHandler) projectFacade).getProject(reg.getProjectName());
                        if (existingProject != null) {
                            System.out.println("Existing registration: " + existingProject.getProjectName() + 
                                            " (" + existingProject.getApplicationOpeningDate() + " to " + 
                                            existingProject.getApplicationClosingDate() + ")");
                        }
                    }
                }
            }
            return;
        }
        
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
        
        // Get the manager's name instead of just showing NRIC
        String managerNric = selectedProject.getManager();
        String managerName = getManagerName(managerNric);
        System.out.println("Manager: " + managerName + " (" + managerNric + ")");
        
        System.out.println("Opening Date: " + selectedProject.getApplicationOpeningDate().format(dateFormatter));
        System.out.println("Closing Date: " + selectedProject.getApplicationClosingDate().format(dateFormatter));
        printDivider();
        
        System.out.print("Confirm registration for this project? (Y/N): ");
        if (!readYesNo()) {
            printMessage("Registration cancelled.");
            return;
        }
        
        // Create and submit registration
        OfficerRegistration newReg = new OfficerRegistration(officer.getNric(), selectedProject.getProjectName());
        regFacade.applyForOfficerRegistration(newReg);
        
        // Save the registration to the CSV file
        if (regFacade instanceof access.officerregistration.OfficerRegistrationHandler) {
            ((access.officerregistration.OfficerRegistrationHandler) regFacade).saveChanges();
        }
        
        printSuccess("Registration submitted successfully. Pending manager approval.");
    }
    
    private void viewRegistrationStatus() {
        printHeader("REGISTRATION STATUS");
        
        List<OfficerRegistration> regs = regFacade.getRegistrationsForOfficer(officer.getNric());
        if (regs.isEmpty()) {
            printMessage("No registration requests found.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        System.out.printf("%-20s %-25s %-15s %-20s\n", "Registration ID", "Project", "Status", "Registration Date");
        printDivider();
        
        // DateTimeFormatter to format the registration date (show only date, not time)
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        
        for (OfficerRegistration reg : regs) {
            // Format registration date to show only the date portion
            String formattedDate = reg.getRegistrationDate() != null ? 
                                  reg.getRegistrationDate().toLocalDate().format(dateFormatter) : 
                                  "Unknown";
            
            System.out.printf("%-20s %-25s %-15s %-20s\n", 
                reg.getRegistrationId(),
                truncate(reg.getProjectName(), 25),
                reg.getStatus(),
                formattedDate
            );
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private void viewProjectDetails() {
        printHeader("MY ASSIGNED PROJECTS");
        
        List<Project> myProjects = getAssignedProjects();
        if (myProjects.isEmpty()) {
            printMessage("You are not assigned to any projects yet.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
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
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        printHeader("SELECT PROJECT");
        for (int i = 0; i < myProjects.size(); i++) {
            System.out.printf("%d. %s (%s)%n", i + 1, myProjects.get(i).getProjectName(), myProjects.get(i).getNeighborhood());
        }
        
        int projectChoice = readChoice("Select project (0 to cancel): ", 0, myProjects.size());
        if (projectChoice == 0) return;
        
        Project selectedProject = myProjects.get(projectChoice - 1);
        
        // Get all applications for the project
        List<Application> applications = appFacade.getApplicationsForProject(selectedProject.getProjectName());
        
        while (true) {
            // Display applications in a table format
            printHeader("APPLICATIONS FOR " + selectedProject.getProjectName());
            displayApplicationsTable(selectedProject, applications);
            
            System.out.println("\nEnter application number to view details (0 to go back): ");
            int appChoice = readChoice(0, applications.size());
            if (appChoice == 0 || appChoice == -1) return;
            
            Application selectedApp = applications.get(appChoice - 1);
            
            // View application details and allow for processing
            viewApplicationDetails(selectedProject, selectedApp);
        }
    }
    
    private void displayApplicationsTable(Project project, List<Application> applications) {
        if (applications.isEmpty()) {
            printMessage("No applications found for this project.");
            return;
        }
        
        System.out.printf("%-5s %-15s %-15s %-15s %-20s %-15s\n", 
                        "No.", "Application ID", "Applicant", "Unit Type", "Application Date", "Status");
        printDivider();
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        
        for (int i = 0; i < applications.size(); i++) {
            Application app = applications.get(i);
            
            System.out.printf("%-5d %-15s %-15s %-15s %-20s %-15s\n", 
                            i + 1, 
                            app.getApplicationId(), 
                            truncate(app.getApplicantNric(), 15),
                            truncate(app.getUnitType(), 15),
                            app.getApplicationDate().format(dateFormatter),
                            app.getStatus()
            );
        }
    }
    
    private void viewApplicationDetails(Project project, Application application) {
        while (true) {
            printHeader("APPLICATION DETAILS");
            System.out.println("Application ID: " + application.getApplicationId());
            System.out.println("Project: " + application.getProjectName());
            System.out.println("Applicant NRIC: " + application.getApplicantNric());
            System.out.println("Unit Type: " + application.getUnitType());
            System.out.println("Application Date: " + application.getApplicationDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
            System.out.println("Status: " + application.getStatus());
            
            if (application.getStatus() == ApplicationStatus.APPROVED) {
                System.out.println("Approval Date: " + (application.getApprovalDate() != null ? 
                                                      application.getApprovalDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : 
                                                      "Not recorded"));
            }
            
            printDivider();
            System.out.println("Options:");
            System.out.println("1. Process Application Status");
            
            // Only show booking receipt option for approved applications
            if (application.getStatus() == ApplicationStatus.APPROVED) {
                System.out.println("2. Generate Booking Receipt");
            }
            
            System.out.println("0. Back to Applications List");
            
            int max = (application.getStatus() == ApplicationStatus.APPROVED) ? 2 : 1;
            int choice = readChoice("Select an option: ", 0, max);
            
            if (choice == -1) continue;
            if (choice == 0) return;
            
            if (choice == 1) {
                processApplicationStatus(application);
                // Refresh application data after processing
                application = appFacade.getApplication(application.getApplicationId());
            } else if (choice == 2 && application.getStatus() == ApplicationStatus.APPROVED) {
                generateBookingReceiptForApplication(project, application);
            }
        }
    }
    
    private void processApplicationStatus(Application application) {
        printHeader("PROCESS APPLICATION STATUS");
        System.out.println("Current Status: " + application.getStatus());
        
        if (application.getStatus() == ApplicationStatus.APPROVED || 
            application.getStatus() == ApplicationStatus.REJECTED) {
            printMessage("This application has already been " + application.getStatus().toString().toLowerCase() + ".");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("\nSelect new status:");
        System.out.println("1. Approve Application");
        System.out.println("2. Reject Application");
        System.out.println("0. Cancel");
        
        int choice = readChoice(0, 2);
        if (choice == 0 || choice == -1) return;
        
        ApplicationStatus newStatus = (choice == 1) ? ApplicationStatus.APPROVED : ApplicationStatus.REJECTED;
        
        // Confirm the action
        System.out.print("\nConfirm " + (newStatus == ApplicationStatus.APPROVED ? "approval" : "rejection") + 
                       " of application " + application.getApplicationId() + "? (Y/N): ");
        if (!readYesNo()) {
            printMessage("Action cancelled.");
            return;
        }
        
        try {
            // Update application status
            application.setStatus(newStatus);
            if (newStatus == ApplicationStatus.APPROVED) {
                application.setApprovalDate(LocalDate.now());
            }
            
            appFacade.updateApplication(application);
            
            printSuccess("Application " + (newStatus == ApplicationStatus.APPROVED ? "approved" : "rejected") + " successfully.");
            
            if (newStatus == ApplicationStatus.APPROVED) {
                printMessage("You can now generate a booking receipt for this application.");
            }
        } catch (Exception e) {
            printError("Error updating application: " + e.getMessage());
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private void generateBookingReceipt() {
        printHeader("GENERATE BOOKING RECEIPT");
        
        List<Project> myProjects = getAssignedProjects();
        if (myProjects.isEmpty()) {
            printError("You are not assigned to any projects. Cannot generate booking receipts.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        printHeader("SELECT PROJECT");
        for (int i = 0; i < myProjects.size(); i++) {
            System.out.printf("%d. %s (%s)%n", i + 1, myProjects.get(i).getProjectName(), myProjects.get(i).getNeighborhood());
        }
        
        int projectChoice = readChoice("Select project (0 to cancel): ", 0, myProjects.size());
        if (projectChoice == 0) return;
        
        Project selectedProject = myProjects.get(projectChoice - 1);
        
        // Get all applications for the project
        List<Application> applications = appFacade.getApplicationsForProject(selectedProject.getProjectName());
        
        // Filter for approved applications only
        List<Application> approvedApplications = new ArrayList<>();
        for (Application app : applications) {
            if (app.getStatus() == ApplicationStatus.APPROVED) {
                approvedApplications.add(app);
            }
        }
        
        if (approvedApplications.isEmpty()) {
            printError("No approved applications found for this project.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Display approved applications in a table format
        printHeader("APPROVED APPLICATIONS FOR " + selectedProject.getProjectName());
        System.out.printf("%-5s %-15s %-15s %-15s %-20s\n", 
                        "No.", "Application ID", "Applicant", "Unit Type", "Approval Date");
        printDivider();
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        
        for (int i = 0; i < approvedApplications.size(); i++) {
            Application app = approvedApplications.get(i);
            
            System.out.printf("%-5d %-15s %-15s %-15s %-20s\n", 
                            i + 1, 
                            app.getApplicationId(), 
                            truncate(app.getApplicantNric(), 15),
                            truncate(app.getUnitType(), 15),
                            app.getApprovalDate().format(dateFormatter)
            );
        }
        
        int appChoice = readChoice("\nSelect application to generate booking receipt (0 to cancel): ", 0, approvedApplications.size());
        if (appChoice == 0) return;
        
        Application selectedApp = approvedApplications.get(appChoice - 1);
        
        // Generate booking receipt for selected application
        generateBookingReceiptForApplication(selectedProject, selectedApp);
    }
    
    private void generateBookingReceiptForApplication(Project project, Application application) {
        printHeader("GENERATE BOOKING RECEIPT");
        
        if (application.getStatus() != ApplicationStatus.APPROVED) {
            printError("Cannot generate a booking receipt for an application that is not approved.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        try {
            // Get applicant information
            Applicant applicant = (Applicant) AuthenticationSystem.getInstance().getUserByNric(application.getApplicantNric());
            
            if (applicant == null) {
                printError("Could not find applicant information.");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                return;
            }
            
            // Find the unit info for this application
            UnitInfo selectedUnit = null;
            for (UnitInfo unit : project.getUnitInfoList()) {
                if (unit.getUnitType().equals(application.getUnitType())) {
                    selectedUnit = unit;
                    break;
                }
            }
            
            if (selectedUnit == null) {
                printError("Could not find unit information for this application.");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                return;
            }
            
            // Generate a receipt file name
            String receiptFileName = "booking_receipt_" + application.getApplicationId() + ".txt";
            
            // Create the receipt content
            StringBuilder receipt = new StringBuilder();
            receipt.append("=====================================================\n");
            receipt.append("                BOOKING RECEIPT                     \n");
            receipt.append("=====================================================\n\n");
            
            receipt.append("Date: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))).append("\n\n");
            
            receipt.append("Application Details:\n");
            receipt.append("- Application ID: ").append(application.getApplicationId()).append("\n");
            receipt.append("- Project Name: ").append(project.getProjectName()).append("\n");
            receipt.append("- Project Location: ").append(project.getNeighborhood()).append("\n");
            receipt.append("- Approval Date: ").append(application.getApprovalDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))).append("\n\n");
            
            receipt.append("Applicant Information:\n");
            receipt.append("- Name: ").append(applicant.getName()).append("\n");
            receipt.append("- NRIC: ").append(applicant.getNric()).append("\n");
            receipt.append("- Address: ").append(applicant.getAddress()).append("\n");
            receipt.append("- Contact: ").append(applicant.getContact()).append("\n\n");
            
            receipt.append("Unit Information:\n");
            receipt.append("- Unit Type: ").append(selectedUnit.getUnitType()).append("\n");
            receipt.append("- Unit Size: ").append(selectedUnit.getUnitSize()).append(" sqm\n");
            receipt.append("- Unit Price: $").append(String.format("%.2f", selectedUnit.getUnitPrice())).append("\n\n");
            
            receipt.append("Payment Details:\n");
            receipt.append("- Booking Fee: $").append(String.format("%.2f", selectedUnit.getUnitPrice() * 0.05)).append("\n");
            receipt.append("- Remaining Amount: $").append(String.format("%.2f", selectedUnit.getUnitPrice() * 0.95)).append("\n\n");
            
            receipt.append("=====================================================\n");
            receipt.append("This is an official booking receipt for your BTO application.\n");
            receipt.append("Please keep this receipt for your records.\n");
            receipt.append("=====================================================\n");
            
            // Save the receipt to a file
            try {
                Path receiptPath = Paths.get("Receipts");
                if (!Files.exists(receiptPath)) {
                    Files.createDirectory(receiptPath);
                }
                
                Files.writeString(Paths.get("Receipts", receiptFileName), receipt.toString());
                
                printSuccess("Booking receipt generated successfully!");
                System.out.println("Receipt saved to: Receipts/" + receiptFileName);
            } catch (IOException e) {
                printError("Error saving receipt: " + e.getMessage());
            }
            
        } catch (Exception e) {
            printError("Error generating booking receipt: " + e.getMessage());
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
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("Select a project to view enquiries:");
        for (int i = 0; i < myProjects.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, myProjects.get(i).getProjectName());
        }
        
        int selection = readChoice("Select project (0 to cancel): ", 0, myProjects.size());
        if (selection == 0 || selection == -1) return;
        
        Project selectedProject = myProjects.get(selection - 1);
        List<Enquiry> allEnquiries = enquiryFacade.getEnquiriesForProject(selectedProject.getProjectName());
        
        if (allEnquiries.isEmpty()) {
            printMessage("No enquiries for " + selectedProject.getProjectName());
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        viewEnquiriesForProject(selectedProject, allEnquiries);
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
            System.out.println("0. Back to main menu");
            
            int menuChoice = readChoice("Select an option: ", 0, 3);
            if (menuChoice == -1) continue;
            
            if (menuChoice == 0) return;
            
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
        
        // Fixed the logic - check if reply is NOT empty (was incorrectly checking if reply is not null AND is empty)
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
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
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
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        replyToEnquiryFromList(selectedProject, pendingEnquiries);
    }

    private void replyToEnquiryFromList(Project project, List<Enquiry> enquiries) {
        printHeader("REPLY TO ENQUIRY");
        
        // Display all enquiries with numbers
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
        
        // Select an enquiry to reply to
        int choice = readChoice("Select enquiry number to reply to (0 to cancel): ", 0, enquiries.size());
        if (choice == 0 || choice == -1) return;
        
        Enquiry selectedEnquiry = enquiries.get(choice - 1);
        
        // Display full enquiry details
        printHeader("ENQUIRY DETAILS");
        System.out.println("Enquiry ID: " + selectedEnquiry.getEnquiryId());
        System.out.println("Project: " + project.getProjectName());
        System.out.println("Applicant: " + selectedEnquiry.getApplicantNric());
        System.out.println("Date: " + selectedEnquiry.getEnquiryDate());
        System.out.println("Message: " + selectedEnquiry.getMessage());
        System.out.println("Current Status: " + (selectedEnquiry.getReply() == null || selectedEnquiry.getReply().isEmpty() ? 
                                               "Pending" : "Responded"));
        
        if (selectedEnquiry.getReply() != null && !selectedEnquiry.getReply().isEmpty()) {
            System.out.println("Previous Reply: " + selectedEnquiry.getReply());
        }
        
        printDivider();
        System.out.println("Enter your reply message (type /cancel to cancel):");
        Scanner sc = new Scanner(System.in);
        String reply = sc.nextLine().trim();
        
        if (reply.equalsIgnoreCase("/cancel")) {
            printMessage("Reply cancelled.");
            return;
        }
        
        // Update the enquiry with the reply
        selectedEnquiry.setReply(reply);
        selectedEnquiry.setStatus(EnquiryStatus.RESPONDED);
        EnquiryHandler.updateEnquiry(selectedEnquiry);
        
        printMessage("Reply sent successfully!");
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
        
        System.out.println("Press 0 to quit");
        
        // Get current password
        while (true) {
            System.out.print("Enter your current password: ");
            String current = scanner.nextLine().trim();
            
            // Check if user wants to quit
            if (current.equals("0")) {
                printMessage("Password change cancelled.");
                return;
            }
            
            if (!officer.getPassword().equals(current)) {
                printError("Incorrect current password. Try again or enter 0 to quit.");
                continue;
            }
            break;
        }
        
        // Get new password
        String newPass;
        while (true) {
            System.out.print("Enter your new password: ");
            newPass = scanner.nextLine().trim();
            
            // Check if user wants to quit
            if (newPass.equals("0")) {
                printMessage("Password change cancelled.");
                return;
            }
            
            // Validate new password
            if (newPass.isEmpty()) {
                printError("New password cannot be empty. Try again or enter 0 to quit.");
                continue;
            }
            
            // Confirm new password
            System.out.print("Confirm your new password: ");
            String confirmPass = scanner.nextLine().trim();
            
            if (!newPass.equals(confirmPass)) {
                printError("Passwords do not match. Try again.");
                continue;
            }
            
            break;
        }
        
        // Update password in memory
        officer.setPassword(newPass);
        
        // Update password in file system
        try {
            // Load all officers from file
            List<HDBOfficer> officers = io.FileIO.loadOfficers();
            
            // Find and update the current officer's password
            for (int i = 0; i < officers.size(); i++) {
                if (officers.get(i).getNric().equals(officer.getNric())) {
                    officers.set(i, officer);
                    break;
                }
            }
            
            // Save the updated officers list back to the file
            io.FileIO.saveOfficers(officers);
            
            printSuccess("Password changed successfully.");
        } catch (Exception e) {
            printError("Error updating password: " + e.getMessage());
            // Revert the password change in memory if file update failed
            officer.setPassword(officer.getPassword());
        }
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
    
    /**
     * Checks if a project application period overlaps with any existing approved or pending registrations
     * @param project The project to check for overlaps
     * @param registrations List of officer registrations to check against
     * @return true if there's an overlap, false otherwise
     */
    private boolean hasDateOverlap(Project project, List<OfficerRegistration> registrations) {
        // Filter for only approved or pending registrations
        List<OfficerRegistration> activeRegistrations = new ArrayList<>();
        for (OfficerRegistration reg : registrations) {
            if (reg.getStatus() == OfficerRegistrationStatus.APPROVED || 
                reg.getStatus() == OfficerRegistrationStatus.PENDING) {
                activeRegistrations.add(reg);
            }
        }
        
        if (activeRegistrations.isEmpty()) {
            return false; // No active registrations, so no overlap
        }
        
        // For each active registration, get the project and check date overlap
        LocalDate newProjectStart = project.getApplicationOpeningDate();
        LocalDate newProjectEnd = project.getApplicationClosingDate();
        
        for (OfficerRegistration reg : activeRegistrations) {
            // Get the project details for this registration
            Project existingProject = null;
            
            // Use ProjectHandler to find the project by name
            if (projectFacade instanceof ProjectHandler) {
                existingProject = ((ProjectHandler) projectFacade).getProject(reg.getProjectName());
            }
            
            if (existingProject != null) {
                LocalDate existingStart = existingProject.getApplicationOpeningDate();
                LocalDate existingEnd = existingProject.getApplicationClosingDate();
                
                // Check for overlap: if one period starts before the other ends
                if ((newProjectStart.isBefore(existingEnd) || newProjectStart.isEqual(existingEnd)) && 
                    (existingStart.isBefore(newProjectEnd) || existingStart.isEqual(newProjectEnd))) {
                    return true; // Dates overlap
                }
            }
        }
        
        return false; // No overlap found
    }
    
    /**
     * Gets the manager's name from their NRIC
     * @param managerNric The NRIC of the manager
     * @return The manager's name, or the NRIC if the manager is not found
     */
    private String getManagerName(String managerNric) {
        List<ProjectManager> managers = io.FileIO.loadManagers();
        for (ProjectManager manager : managers) {
            if (manager.getNric().equals(managerNric)) {
                return manager.getName();
            }
        }
        return managerNric; // Return the NRIC if manager not found
    }
    
    // ----- UI Helper Methods -----
    
    private void printHeader(String title) {
        System.out.println("\n" + FileUtils.repeatChar('=', 70));
        System.out.println(FileUtils.repeatChar(' ', (70 - title.length()) / 2) + title);
        System.out.println(FileUtils.repeatChar('=', 70));
    }
    
    private void printDivider() {
        System.out.println(FileUtils.repeatChar('-', 70));
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