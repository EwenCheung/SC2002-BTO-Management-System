package menu;

import access.application.ManagerApplicationFeatures;
import access.enquiry.ManagerEnquiryFeatures;
import access.officerregistration.ManagerOfficerRegistrationFeatures;
import access.project.ManagerProjectFeatures;
import access.withdrawal.ManagerWithdrawalFeatures;
import io.FileIO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import models.Application;
import models.Enquiry;
import models.Project;
import models.OfficerRegistration;
import models.WithdrawalRequest;
import models.enums.ApplicationStatus;
import models.enums.OfficerRegistrationStatus;
import models.enums.WithdrawalStatus;
import models.UnitInfo;
import users.ProjectManager;
import users.User;
import users.enums.MaritalStatus;
import utils.Constants;
import utils.FileUtils;

public class ManagerMenu {
    private Scanner scanner;
    private ProjectManager projectManager;
    
    // Interfaces for manager-specific features.
    private ManagerProjectFeatures projectFacade;
    private ManagerApplicationFeatures appFacade;
    private ManagerEnquiryFeatures enquiryFacade;
    private ManagerOfficerRegistrationFeatures officerRegFacade;
    private ManagerWithdrawalFeatures withdrawalFacade;
    
    // Date formatter using constant pattern.
    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
    
    public ManagerMenu(ProjectManager projectManager,
                       ManagerProjectFeatures projectFacade,
                       ManagerApplicationFeatures appFacade,
                       ManagerEnquiryFeatures enquiryFacade,
                       ManagerOfficerRegistrationFeatures officerRegFacade,
                       ManagerWithdrawalFeatures withdrawalFacade) {
        this.scanner = new Scanner(System.in);
        // Construct a ProjectManager based on the logged-in user's details.
        this.projectManager = projectManager;
        this.projectFacade = projectFacade;
        this.appFacade = appFacade;
        this.enquiryFacade = enquiryFacade;
        this.officerRegFacade = officerRegFacade;
        this.withdrawalFacade = withdrawalFacade;
    }
    
    public void display() {
        while (true) {
            printHeader("HDB MANAGER PORTAL");
            System.out.println("Welcome, " + projectManager.getName());
            printDivider();
            
            System.out.println("=== Project Management ===");
            System.out.println("1. Create New Project");
            System.out.println("2. Edit Project");
            System.out.println("3. Delete Project");
            System.out.println("4. View All Projects");
            System.out.println("5. View My Projects");
            System.out.println("6. Toggle Project Visibility");
            
            System.out.println("\n=== Officer Management ===");
            System.out.println("7. View Officer Registrations");
            System.out.println("8. Process Officer Registration");
            
            System.out.println("\n=== Application Management ===");
            System.out.println("9. Process BTO Applications");
            System.out.println("10. Process Withdrawal Requests");
            
            System.out.println("\n=== Reports & Enquiries ===");
            System.out.println("11. Generate Reports");
            System.out.println("12. View All Enquiries");
            System.out.println("13. Reply to Project Enquiries");
            
            System.out.println("\n=== System ===");
            System.out.println("14. Change Password");
            System.out.println("15. Logout");
            printDivider();
            
            int choice = readChoice("Enter your choice: ", 1, 15);
            
            switch (choice) {
                case 1: createProject(); break;
                case 2: editProject(); break;
                case 3: deleteProject(); break;
                case 4: viewAllProjects(); break;
                case 5: viewMyProjects(); break;
                case 6: toggleProjectVisibility(); break;
                case 7: viewOfficerRegistrations(); break;
                case 8: processOfficerRegistration(); break;
                case 9: processBTOApplications(); break;
                case 10: processWithdrawalRequests(); break;
                case 11: generateReports(); break;
                case 12: viewAllEnquiries(); break;
                case 13: replyToEnquiries(); break;
                case 14: changePassword(); break;
                case 15:
                    printMessage("Logging out...");
                    return;
                default:
                    printError("Invalid choice. Please try again.");
            }
        }
    }
    
    // --- UI Helper Methods ---
    
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
    
    private String readString(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input;
    }
    
    private boolean readYesNo(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim().toUpperCase();
        return input.equals("Y") || input.equals("YES");
    }
    
    private String truncate(String str, int length) {
        if (str == null) return "";
        if (str.length() <= length) return str;
        return str.substring(0, length - 3) + "...";
    }
    
    // --- Project Management Methods ---
    private void createProject() {
        printHeader("Create New Project");
        if (isHandlingActiveProject()) {
            printError("You are already handling a project within an active application period.");
            return;
        }
        try {
            String projectName = readString("Enter Project Name: ");
            String neighborhood = readString("Enter Neighborhood: ");
            int twoRoomUnits = Integer.parseInt(readString("Enter number of 2-Room units: "));
            double twoRoomPrice = Double.parseDouble(readString("Enter price for 2-Room units: "));
            int threeRoomUnits = Integer.parseInt(readString("Enter number of 3-Room units: "));
            double threeRoomPrice = Double.parseDouble(readString("Enter price for 3-Room units: "));
            LocalDate openingDate = LocalDate.parse(readString("Enter application opening date (" + Constants.DATE_FORMAT + "): "), DATE_FORMATTER);
            LocalDate closingDate = LocalDate.parse(readString("Enter application closing date (" + Constants.DATE_FORMAT + "): "), DATE_FORMATTER);
            int officerSlots = Math.min(10, Integer.parseInt(readString("Enter number of HDB Officer slots (max 10): ")));
            
            Project project = new Project(projectName, neighborhood, openingDate, closingDate, projectManager.getNric(), officerSlots);
            project.addUnitType("2-Room", twoRoomUnits, twoRoomPrice);
            project.addUnitType("3-Room", threeRoomUnits, threeRoomPrice);
            
            projectFacade.addProject(project);
            printSuccess("Project created successfully!");
        } catch (Exception e) {
            printError("Error creating project: " + e.getMessage());
        }
    }
    
    private boolean isHandlingActiveProject() {
        List<Project> projects = projectFacade.getAllProjects();
        LocalDate now = LocalDate.now();
        for (Project project : projects) {
            if (project.getManager().equalsIgnoreCase(projectManager.getNric()) &&
                !now.isBefore(project.getApplicationOpeningDate()) &&
                !now.isAfter(project.getApplicationClosingDate())) {
                return true;
            }
        }
        return false;
    }
    
    private void editProject() {
        printHeader("Edit Project");
        List<Project> myProjects = projectFacade.getProjectsByManager(projectManager.getNric());
        if (myProjects.isEmpty()) {
            printError("You have no projects to edit.");
            return;
        }
        for (int i = 0; i < myProjects.size(); i++) {
            Project project = myProjects.get(i);
            System.out.printf("%d. %s (%s)%n", i + 1, project.getProjectName(), project.getNeighborhood());
        }
        try {
            int choice = readChoice("Select project to edit (0 to cancel): ", 0, myProjects.size());
            if (choice == 0) return;
            Project project = myProjects.get(choice - 1);
            editProjectDetails(project);
        } catch (NumberFormatException e) {
            printError("Please enter a valid number.");
        }
    }
    
    private void editProjectDetails(Project project) {
        while (true) {
            printHeader("Editing " + project.getProjectName());
            System.out.println("1. Edit Application Period");
            System.out.println("2. Edit Officer Slots");
            System.out.println("3. Save Changes");
            System.out.println("4. Cancel");
            try {
                int choice = readChoice("Enter your choice: ", 1, 4);
                switch (choice) {
                    case 1:
                        LocalDate newOpening = LocalDate.parse(readString("Enter new opening date (" + Constants.DATE_FORMAT + "): "), DATE_FORMATTER);
                        LocalDate newClosing = LocalDate.parse(readString("Enter new closing date (" + Constants.DATE_FORMAT + "): "), DATE_FORMATTER);
                        project.setOpeningDate(newOpening);
                        project.setClosingDate(newClosing);
                        break;
                    case 2:
                        int slots = Math.min(10, Integer.parseInt(readString("Enter new number of officer slots (max 10): ")));
                        project.setOfficerSlots(slots);
                        break;
                    case 3:
                        projectFacade.updateProject(project);
                        printSuccess("Project updated successfully!");
                        return;
                    case 4:
                        return;
                    default:
                        printError("Invalid choice.");
                }
            } catch (Exception e) {
                printError("Error: " + e.getMessage());
            }
        }
    }
    
    private void deleteProject() {
        printHeader("Delete Project");
        List<Project> myProjects = projectFacade.getProjectsByManager(projectManager.getNric());
        if (myProjects.isEmpty()) {
            printError("You have no projects to delete.");
            return;
        }
        for (int i = 0; i < myProjects.size(); i++) {
            Project p = myProjects.get(i);
            System.out.printf("%d. %s (%s)%n", i + 1, p.getProjectName(), p.getNeighborhood());
        }
        try {
            int choice = readChoice("Select project to delete (0 to cancel): ", 0, myProjects.size());
            if (choice == 0) return;
            Project project = myProjects.get(choice - 1);
            if (readYesNo("Are you sure you want to delete this project? (Y/N): ")) {
                projectFacade.deleteProject(project.getProjectName());
                printSuccess("Project deleted successfully!");
            }
        } catch (NumberFormatException e) {
            printError("Please enter a valid number.");
        }
    }
    
    private void viewAllProjects() {
        printHeader("All Projects");
        List<Project> allProjects = projectFacade.getAllProjects();
        displayProjects(allProjects);
    }
    
    private void viewMyProjects() {
        printHeader("MY PROJECTS");
        List<Project> myProjects = getMyProjects();
        
        if (myProjects.isEmpty()) {
            printMessage("You are not currently managing any projects.");
            return;
        }
        
        // Display projects in a tabular format
        System.out.printf("%-4s %-25s %-15s %-15s %-10s %-15s\n", 
                          "No.", "Project Name", "Neighborhood", "Application Period", "Officers", "Visibility");
        printDivider();
        
        int i = 1;
        for (Project project : myProjects) {
            String period = project.getApplicationOpeningDate() + " to " + project.getApplicationClosingDate();
            String officers = project.getOfficers().size() + "/" + project.getOfficerSlot();
            
            System.out.printf("%-4d %-25s %-15s %-15s %-10s %-15s\n", 
                i++, 
                truncate(project.getProjectName(), 25),
                truncate(project.getNeighborhood(), 15),
                truncate(period, 15),
                officers,
                project.isVisible() ? "Visible" : "Hidden"
            );
        }
        
        // Allow selecting a project for more details
        System.out.print("\nSelect project number for details (0 to go back): ");
        int choice = readChoice("", 0, myProjects.size());
        if (choice == 0) return;
        
        Project selectedProject = myProjects.get(choice - 1);
        displayProjectDetails(selectedProject);
    }

    private void displayProjectDetails(Project project) {
        printHeader("PROJECT DETAILS: " + project.getProjectName());
        
        // Basic project information
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("Application Period: " + project.getApplicationOpeningDate() + " to " + project.getApplicationClosingDate());
        System.out.println("Visibility: " + (project.isVisible() ? "Visible" : "Hidden"));
        System.out.println("Officer Slots: " + project.getOfficers().size() + "/" + project.getOfficerSlot());
        
        // Unit information
        System.out.println("\nUnit Types:");
        printDivider();
        for (String unitType : project.getUnits().keySet()) {
            UnitInfo unitInfo = project.getUnits().get(unitType);
            System.out.printf("%-10s: %d/%d units available, Price: $%.2f\n", 
                unitType, 
                unitInfo.getAvailableUnits(), 
                unitInfo.getTotalUnits(),
                unitInfo.getSellingPrice()
            );
        }
        
        // Application statistics
        List<Application> applications = appFacade.getApplicationsByProject(project.getProjectName());
        int totalApps = applications.size();
        int pendingApps = 0;
        int approvedApps = 0;
        int rejectedApps = 0;
        int bookedApps = 0;
        int withdrawnApps = 0;
        
        for (Application app : applications) {
            String status = app.getStatus().toString();
            if (status.equalsIgnoreCase("PENDING")) pendingApps++;
            else if (status.equalsIgnoreCase("APPROVED")) approvedApps++;
            else if (status.equalsIgnoreCase("REJECTED")) rejectedApps++;
            else if (status.equalsIgnoreCase("BOOKED")) bookedApps++;
            else if (status.equalsIgnoreCase("WITHDRAWN")) withdrawnApps++;
        }
        
        System.out.println("\nApplication Statistics:");
        printDivider();
        System.out.println("Total Applications: " + totalApps);
        System.out.println("Pending: " + pendingApps);
        System.out.println("Approved: " + approvedApps);
        System.out.println("Booked: " + bookedApps);
        System.out.println("Rejected: " + rejectedApps);
        System.out.println("Withdrawn: " + withdrawnApps);
        
        // Assigned officers
        if (!project.getOfficers().isEmpty()) {
            System.out.println("\nAssigned Officers:");
            printDivider();
            for (String officerNric : project.getOfficers()) {
                System.out.println("- " + officerNric);
            }
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private void toggleProjectVisibility() {
        printHeader("Toggle Project Visibility");
        List<Project> myProjects = projectFacade.getProjectsByManager(projectManager.getNric());
        if (myProjects.isEmpty()) {
            printError("You have no projects to manage.");
            return;
        }
        for (int i = 0; i < myProjects.size(); i++) {
            Project project = myProjects.get(i);
            System.out.printf("%d. %s (Currently: %s)%n", i + 1, project.getProjectName(), project.isVisible() ? "Visible" : "Hidden");
        }
        try {
            int choice = readChoice("Select project to toggle visibility (0 to cancel): ", 0, myProjects.size());
            if (choice == 0) return;
            Project project = myProjects.get(choice - 1);
            boolean newVisibility = !project.isVisible();
            projectFacade.toggleVisibility(project.getProjectName(), newVisibility);
            printSuccess("Project visibility toggled to: " + (newVisibility ? "Visible" : "Hidden"));
        } catch (NumberFormatException e) {
            printError("Please enter a valid number.");
        }
    }
    
    private void displayProjects(List<Project> projects) {
        if (projects.isEmpty()) {
            printMessage("No projects found.");
            return;
        }
        
        System.out.printf("%-4s %-25s %-15s %-15s %-10s %-10s%n", 
                         "No.", "Project Name", "Neighborhood", "Application Period", "Manager", "Visibility");
        printDivider();
        
        int i = 1;
        for (Project project : projects) {
            String period = project.getApplicationOpeningDate() + " to " + project.getApplicationClosingDate();
            
            System.out.printf("%-4d %-25s %-15s %-15s %-10s %-10s%n", 
                            i++, 
                            truncate(project.getProjectName(), 25),
                            truncate(project.getNeighborhood(), 15),
                            truncate(period, 15),
                            truncate(project.getManager(), 10),
                            project.isVisible() ? "Visible" : "Hidden");
        }
        
        System.out.print("\nSelect project number for details (0 to go back): ");
        int choice = readChoice("", 0, projects.size());
        if (choice == 0) return;
        
        Project selectedProject = projects.get(choice - 1);
        displayProjectDetails(selectedProject);
    }
    
    // --- Officer Management ---
    private void viewOfficerRegistrations() {
        printHeader("View Officer Registrations");
        List<OfficerRegistration> regs = officerRegFacade.getAllOfficerRegistrations();
        if (regs.isEmpty()) {
            printError("No officer registrations found.");
        } else {
            for (OfficerRegistration reg : regs) {
                System.out.println("\n" + reg.toString());
            }
        }
    }
    
    private void processOfficerRegistration() {
        printHeader("PROCESS OFFICER REGISTRATION");
        
        // Get all pending registrations
        List<OfficerRegistration> regs = officerRegFacade.getAllOfficerRegistrations();
        List<OfficerRegistration> pendingRegs = new ArrayList<>();
        
        for (OfficerRegistration reg : regs) {
            if (reg.getStatus().toString().equalsIgnoreCase("PENDING")) {
                pendingRegs.add(reg);
            }
        }
        
        if (pendingRegs.isEmpty()) {
            printMessage("No pending officer registrations to process.");
            return;
        }
        
        // Display pending registrations in a tabular format
        System.out.printf("%-4s %-15s %-15s %-25s %-15s%n", 
                         "No.", "Registration ID", "Officer NRIC", "Project", "Date");
        printDivider();
        
        int i = 1;
        for (OfficerRegistration reg : pendingRegs) {
            System.out.printf("%-4d %-15s %-15s %-25s %-15s%n", 
                             i++, 
                             truncate(reg.getRegistrationId(), 15),
                             reg.getOfficerNric(),
                             truncate(reg.getProjectName(), 25),
                             reg.getRegistrationDate().toLocalDate());
        }
        
        // Let the manager select a registration to process
        int choice = readChoice("Select registration to process (0 to cancel): ", 0, pendingRegs.size());
        if (choice == 0) return;
        
        OfficerRegistration selectedReg = pendingRegs.get(choice - 1);
        
        // Get project details to show available slots
        Project project = projectFacade.getProject(selectedReg.getProjectName());
        if (project == null) {
            printError("Error: Project not found.");
            return;
        }
        
        // Display registration details with project info for better decision making
        printHeader("REGISTRATION DETAILS");
        System.out.println("Registration ID: " + selectedReg.getRegistrationId());
        System.out.println("Officer NRIC: " + selectedReg.getOfficerNric());
        System.out.println("Project: " + selectedReg.getProjectName());
        System.out.println("Registration Date: " + selectedReg.getRegistrationDate().toLocalDate());
        System.out.println("\nProject Officer Slots: " + project.getOfficers().size() + "/" + project.getOfficerSlot());
        
        // Check if the project has available slots
        if (project.getOfficers().size() >= project.getOfficerSlot()) {
            printError("WARNING: This project has no available officer slots.");
            if (!readYesNo("Do you still want to approve this registration? (Y/N): ")) {
                officerRegFacade.rejectRegistration(selectedReg.getRegistrationId());
                printSuccess("Registration rejected (no available slots).");
                return;
            }
        }
        
        // Ask manager for decision
        if (readYesNo("Approve this registration? (Y/N): ")) {
            // Approve registration
            officerRegFacade.approveRegistration(selectedReg.getRegistrationId());
            
            // Update project with new officer
            project.addOfficer(selectedReg.getOfficerNric());
            projectFacade.updateProject(project);
            
            printSuccess("Registration approved and officer assigned successfully.");
        } else {
            // Get reason for rejection
            String reason = readString("Enter reason for rejection (optional): ");
            
            // Reject registration
            officerRegFacade.rejectRegistration(selectedReg.getRegistrationId());
            
            printSuccess("Registration rejected successfully.");
        }
    }
    
    // --- Application Management ---
    private void processBTOApplications() {
        printHeader("PROCESS BTO APPLICATIONS");
        
        List<Project> myProjects = getMyProjects();
        if (myProjects.isEmpty()) {
            printError("You have no projects to manage applications for.");
            return;
        }
        
        // Display projects in a tabular format
        System.out.printf("%-4s %-25s %-15s %-15s %-15s%n", 
                         "No.", "Project Name", "Neighborhood", "Applications", "Pending");
        printDivider();
        
        int i = 1;
        for (Project project : myProjects) {
            List<Application> projectApps = appFacade.getApplicationsByProject(project.getProjectName());
            int pendingCount = 0;
            
            for (Application app : projectApps) {
                if (app.getStatus() == ApplicationStatus.PENDING) {
                    pendingCount++;
                }
            }
            
            System.out.printf("%-4d %-25s %-15s %-15d %-15d%n", 
                             i++, 
                             truncate(project.getProjectName(), 25),
                             truncate(project.getNeighborhood(), 15),
                             projectApps.size(),
                             pendingCount);
        }
        
        // Let the manager select a project
        int choice = readChoice("Select project to process applications (0 to cancel): ", 0, myProjects.size());
        if (choice == 0) return;
        
        Project selectedProject = myProjects.get(choice - 1);
        approveApplicationsForProject(selectedProject);
    }

    private void approveApplicationsForProject(Project project) {
        while (true) {
            printHeader("PROCESS APPLICATIONS: " + project.getProjectName());
            
            // Get all applications for this project that are pending
            List<Application> projectApplications = appFacade.getApplicationsByProject(project.getProjectName());
            List<Application> pendingApplications = new ArrayList<>();
            
            for (Application app : projectApplications) {
                if (app.getStatus() == ApplicationStatus.PENDING) {
                    pendingApplications.add(app);
                }
            }
            
            if (pendingApplications.isEmpty()) {
                printMessage("No pending applications for this project.");
                return;
            }
            
            // Display a summary of available units before processing
            System.out.println("\nAvailable Units:");
            printDivider();
            
            for (String unitType : project.getUnits().keySet()) {
                UnitInfo unitInfo = project.getUnits().get(unitType);
                System.out.printf("%-10s: %d/%d units available%n", 
                                 unitType,
                                 unitInfo.getAvailableUnits(),
                                 unitInfo.getTotalUnits());
            }
            
            // Display pending applications
            printHeader("PENDING APPLICATIONS");
            System.out.printf("%-4s %-15s %-15s %-10s %-20s%n", 
                             "No.", "Application ID", "Applicant", "Unit Type", "Application Date");
            printDivider();
            
            for (int i = 0; i < pendingApplications.size(); i++) {
                Application app = pendingApplications.get(i);
                System.out.printf("%-4d %-15s %-15s %-10s %-20s%n", 
                                 i + 1, 
                                 truncate(app.getApplicationId(), 15),
                                 app.getApplicantNric(),
                                 app.getUnitType(),
                                 app.getApplicationDate().toLocalDate());
            }
            
            // Options menu
            printDivider();
            System.out.println("1. Process individual application");
            System.out.println("2. Back to project selection");
            
            int choice = readChoice("Enter choice: ", 1, 2);
            if (choice == 2) return;
            
            if (choice == 1) {
                // Process individual application
                int appChoice = readChoice("Select application to process (0 to cancel): ", 0, pendingApplications.size());
                if (appChoice == 0) continue;
                
                Application selectedApp = pendingApplications.get(appChoice - 1);
                processIndividualApplication(selectedApp, project);
            }
        }
    }

    private void processIndividualApplication(Application application, Project project) {
        printHeader("APPLICATION DETAILS");
        
        // Find the applicant to show more details
        User applicant = findUserByNric(application.getApplicantNric());
        String unitType = application.getUnitType();
        int availableUnits = project.getAvailableUnits(unitType);
        
        System.out.println("Application ID: " + application.getApplicationId());
        System.out.println("Applicant NRIC: " + application.getApplicantNric());
        
        if (applicant != null) {
            System.out.println("Applicant Name: " + applicant.getName());
            System.out.println("Applicant Age: " + applicant.getAge());
            System.out.println("Marital Status: " + applicant.getMaritalStatus());
        }
        
        System.out.println("Unit Type: " + unitType);
        System.out.println("Available Units: " + availableUnits);
        System.out.println("Application Date: " + application.getApplicationDate().toLocalDate());
        printDivider();
        
        if (availableUnits <= 0) {
            System.out.println("WARNING: No available units of type " + unitType);
        }
        
        System.out.println("1. Approve application");
        System.out.println("2. Reject application");
        System.out.println("3. Back to application list");
        
        int choice = readChoice("Enter choice: ", 1, 3);
        if (choice == 3) return;
        
        if (choice == 1) {
            // Approve application
            if (availableUnits <= 0) {
                if (!readYesNo("No available units. Are you sure you want to approve? (Y/N): ")) {
                    return;
                }
            }
            
            application.setStatus(ApplicationStatus.APPROVED);
            
            // Generate a unit number
            String unitNumber = generateUnitNumber(unitType, project.getProjectName());
            application.setAssignedUnit(unitNumber);
            
            // Update available units
            project.decrementAvailableUnits(unitType);
            projectFacade.updateProject(project);
            
            // Save the application - use approveApplication instead of updateApplication
            appFacade.approveApplication(application.getApplicationId());
            printSuccess("Application approved and unit assigned: " + unitNumber);
        } else if (choice == 2) {
            // Reject application
            String reason = readString("Enter rejection reason: ");
            
            application.setStatus(ApplicationStatus.REJECTED);
            application.setRemarks(reason);
            appFacade.approveApplication(application.getApplicationId());
            printSuccess("Application rejected successfully.");
        }
    }

    private void processWithdrawalRequests() {
        printHeader("Process Withdrawal Requests");
        List<Project> myProjects = getMyProjects();
        if (myProjects.isEmpty()) {
            printError("You have no projects to manage withdrawals for.");
            return;
        }
        System.out.println("Select a project:");
        for (int i = 0; i < myProjects.size(); i++) {
            Project project = myProjects.get(i);
            System.out.printf("%d. %s%n", i + 1, project.getProjectName());
        }
        try {
            int choice = readChoice("Select project (0 to cancel): ", 0, myProjects.size());
            if (choice == 0) return;
            Project selectedProject = myProjects.get(choice - 1);
            processWithdrawalsForProject(selectedProject);
        } catch (NumberFormatException e) {
            printError("Please enter a valid number.");
        }
    }
    
    private void processWithdrawalsForProject(Project project) {
        printHeader("Process Withdrawal Requests for " + project.getProjectName());
        
        List<WithdrawalRequest> allWithdrawals = withdrawalFacade.getAllWithdrawalRequests();
        List<WithdrawalRequest> pendingWithdrawals = new ArrayList<>();
        
        // Filter withdrawals by project and pending status
        for (WithdrawalRequest withdrawal : allWithdrawals) {
            if (withdrawal.getProjectName().equals(project.getProjectName()) && 
                withdrawal.getStatus() == WithdrawalStatus.PENDING) {
                pendingWithdrawals.add(withdrawal);
            }
        }
        
        if (pendingWithdrawals.isEmpty()) {
            printError("No pending withdrawal requests for this project.");
            return;
        }
        
        // Display all pending withdrawal requests
        System.out.println("\nPending Withdrawal Requests:");
        for (int i = 0; i < pendingWithdrawals.size(); i++) {
            WithdrawalRequest withdrawal = pendingWithdrawals.get(i);
            System.out.printf("%d. Request ID: %s, Applicant: %s, Application ID: %s%n", 
                i + 1, withdrawal.getRequestId(), withdrawal.getApplicantNric(), withdrawal.getApplicationId());
        }
        
        // Process a selected withdrawal request
        try {
            int choice = readChoice("Select withdrawal request to process (0 to cancel): ", 0, pendingWithdrawals.size());
            if (choice == 0) return;
            
            WithdrawalRequest selectedWithdrawal = pendingWithdrawals.get(choice - 1);
            String applicationId = selectedWithdrawal.getApplicationId();
            
            // Get the related application - need to find it within all applications
            Application application = null;
            List<Application> allApplications = appFacade.getAllApplications();
            for (Application app : allApplications) {
                if (app.getApplicationId().equals(applicationId)) {
                    application = app;
                    break;
                }
            }
            
            if (application == null) {
                printError("Error: Related application not found.");
                return;
            }
            
            System.out.println("\nWithdrawal Request Details:");
            System.out.println("Applicant NRIC: " + selectedWithdrawal.getApplicantNric());
            System.out.println("Project: " + selectedWithdrawal.getProjectName());
            System.out.println("Unit Type: " + application.getUnitType());
            System.out.println("Current Application Status: " + application.getStatus());
            
            // Prompt for approval/rejection
            if (readYesNo("Approve this withdrawal request? (Y/N): ")) {
                // Approve withdrawal
                selectedWithdrawal.setStatus(WithdrawalStatus.APPROVED);
                selectedWithdrawal.setProcessDate(LocalDateTime.now());
                
                // Update the application status to WITHDRAWN
                application.setStatus(ApplicationStatus.WITHDRAWN);
                application.setRemarks("Withdrawal approved by manager: " + projectManager.getName());
                
                // If the application was previously approved/booked, return the unit to available pool
                if (application.getStatus() == ApplicationStatus.APPROVED || 
                    application.getStatus() == ApplicationStatus.BOOKED) {
                    String unitType = application.getUnitType();
                    project.incrementAvailableUnits(unitType);
                    projectFacade.updateProject(project);
                }
                
                // Save changes
                withdrawalFacade.approveWithdrawal(selectedWithdrawal.getRequestId());
                appFacade.approveApplication(application.getApplicationId());
                
                printSuccess("Withdrawal request approved successfully.");
            } else {
                // Reject withdrawal
                String reason = readString("Enter rejection reason: ");
                
                selectedWithdrawal.setStatus(WithdrawalStatus.REJECTED);
                selectedWithdrawal.setProcessDate(LocalDateTime.now());
                selectedWithdrawal.setRemarks(reason);
                
                // Save changes
                withdrawalFacade.rejectWithdrawal(selectedWithdrawal.getRequestId());
                
                printSuccess("Withdrawal request rejected successfully.");
            }
        } catch (NumberFormatException e) {
            printError("Please enter a valid number.");
        } catch (Exception e) {
            printError("Error processing withdrawal request: " + e.getMessage());
        }
    }
    
    // --- Reports & Enquiries ---
    private void generateReports() {
        while (true) {
            printHeader("Generate Reports");
            System.out.println("1. Generate All Applicants Report");
            System.out.println("2. Generate Married Applicants Report");
            System.out.println("3. Generate Single Applicants Report");
            System.out.println("4. Generate Project-specific Report");
            System.out.println("5. Back to Manager Menu");
            try {
                int choice = readChoice("Enter your choice: ", 1, 5);
                switch (choice) {
                    case 1: generateAllApplicantsReport(); break;
                    case 2: generateMarriedApplicantsReport(); break;
                    case 3: generateSingleApplicantsReport(); break;
                    case 4: generateProjectReport(); break;
                    case 5: return;
                    default: printError("Invalid selection.");
                }
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }
    }
    
    private void generateAllApplicantsReport() {
        printHeader("All Applicants Report");
        List<Application> applications = appFacade.getAllApplications();
        
        if (applications.isEmpty()) {
            printError("No applications found in the system.");
            return;
        }
        
        // Build a header row
        System.out.printf("%-15s %-15s %-25s %-10s %-15s %-10s %s%n",
                "Applicant", "NRIC", "Project", "Flat Type", "Status", "Age", "Marital Status");
        System.out.println(FileUtils.repeatChar('=', 100));
        
        // For each application, get the applicant details
        for (Application app : applications) {
            String applicantNric = app.getApplicantNric();
            User applicant = findUserByNric(applicantNric);
            
            if (applicant != null) {
                System.out.printf("%-15s %-15s %-25s %-10s %-15s %-10d %s%n",
                        applicant.getName(),
                        applicantNric,
                        app.getProjectName(),
                        app.getUnitType(),
                        app.getStatus(),
                        applicant.getAge(),
                        applicant.getMaritalStatus()
                );
            }
        }
        System.out.println(FileUtils.repeatChar('=', 100));
        System.out.println("Total Applications: " + applications.size());
        
        // Ask if user wants to save the report to a file
        if (readYesNo("\nSave this report to a file? (Y/N): ")) {
            saveReportToFile("all_applicants_report.txt", generateReportContent(applications, null));
        }
    }
    
    private void generateMarriedApplicantsReport() {
        printHeader("Married Applicants Report");
        List<Application> applications = appFacade.getAllApplications();
        List<Application> marriedApplicantsApps = new ArrayList<>();
        
        // Filter applications for married applicants
        for (Application app : applications) {
            User applicant = findUserByNric(app.getApplicantNric());
            if (applicant != null && applicant.getMaritalStatus().toString().equalsIgnoreCase("Married")) {
                marriedApplicantsApps.add(app);
            }
        }
        
        if (marriedApplicantsApps.isEmpty()) {
            printError("No applications from married applicants found.");
            return;
        }
        
        // Build the report
        System.out.printf("%-15s %-15s %-25s %-10s %-15s %-10s%n",
                "Applicant", "NRIC", "Project", "Flat Type", "Status", "Age");
        System.out.println(FileUtils.repeatChar('=', 90));
        
        for (Application app : marriedApplicantsApps) {
            User applicant = findUserByNric(app.getApplicantNric());
            if (applicant != null) {
                System.out.printf("%-15s %-15s %-25s %-10s %-15s %-10d%n",
                        applicant.getName(),
                        app.getApplicantNric(),
                        app.getProjectName(),
                        app.getUnitType(),
                        app.getStatus(),
                        applicant.getAge()
                );
            }
        }
        System.out.println(FileUtils.repeatChar('=', 90));
        System.out.println("Total Applications from Married Applicants: " + marriedApplicantsApps.size());
        
        // Ask if user wants to save the report to a file
        if (readYesNo("\nSave this report to a file? (Y/N): ")) {
            saveReportToFile("married_applicants_report.txt", generateReportContent(marriedApplicantsApps, "Married"));
        }
    }
    
    private void generateSingleApplicantsReport() {
        printHeader("Single Applicants Report");
        List<Application> applications = appFacade.getAllApplications();
        List<Application> singleApplicantsApps = new ArrayList<>();
        
        // Filter applications for single applicants
        for (Application app : applications) {
            User applicant = findUserByNric(app.getApplicantNric());
            if (applicant != null && applicant.getMaritalStatus().toString().equalsIgnoreCase("Single")) {
                singleApplicantsApps.add(app);
            }
        }
        
        if (singleApplicantsApps.isEmpty()) {
            printError("No applications from single applicants found.");
            return;
        }
        
        // Build the report
        System.out.printf("%-15s %-15s %-25s %-10s %-15s %-10s%n",
                "Applicant", "NRIC", "Project", "Flat Type", "Status", "Age");
        System.out.println(FileUtils.repeatChar('=', 90));
        
        for (Application app : singleApplicantsApps) {
            User applicant = findUserByNric(app.getApplicantNric());
            if (applicant != null) {
                System.out.printf("%-15s %-15s %-25s %-10s %-15s %-10d%n",
                        applicant.getName(),
                        app.getApplicantNric(),
                        app.getProjectName(),
                        app.getUnitType(),
                        app.getStatus(),
                        applicant.getAge()
                );
            }
        }
        System.out.println(FileUtils.repeatChar('=', 90));
        System.out.println("Total Applications from Single Applicants: " + singleApplicantsApps.size());
        
        // Ask if user wants to save the report to a file
        if (readYesNo("\nSave this report to a file? (Y/N): ")) {
            saveReportToFile("single_applicants_report.txt", generateReportContent(singleApplicantsApps, "Single"));
        }
    }
    
    private void generateProjectReport() {
        printHeader("Project-specific Report");
        List<Project> myProjects = getMyProjects();
        
        if (myProjects.isEmpty()) {
            printError("You have no projects to generate reports for.");
            return;
        }
        
        // List projects
        System.out.println("Select a project for the report:");
        for (int i = 0; i < myProjects.size(); i++) {
            Project project = myProjects.get(i);
            System.out.printf("%d. %s%n", i + 1, project.getProjectName());
        }
        
        try {
            int choice = readChoice("Select project (0 to cancel): ", 0, myProjects.size());
            if (choice == 0) return;
            
            Project selectedProject = myProjects.get(choice - 1);
            List<Application> projectApplications = appFacade.getApplicationsByProject(selectedProject.getProjectName());
            
            if (projectApplications.isEmpty()) {
                printError("No applications found for project: " + selectedProject.getProjectName());
                return;
            }
            
            printHeader("Report for " + selectedProject.getProjectName());
            
            // Summary statistics
            int totalApplications = projectApplications.size();
            int twoRoomApplications = 0;
            int threeRoomApplications = 0;
            int approvedApplications = 0;
            int pendingApplications = 0;
            int rejectedApplications = 0;
            
            for (Application app : projectApplications) {
                if (app.getUnitType().equals("2-Room")) {
                    twoRoomApplications++;
                } else if (app.getUnitType().equals("3-Room")) {
                    threeRoomApplications++;
                }
                
                if (app.getStatus() == ApplicationStatus.APPROVED || app.getStatus() == ApplicationStatus.BOOKED) {
                    approvedApplications++;
                } else if (app.getStatus() == ApplicationStatus.PENDING) {
                    pendingApplications++;
                } else if (app.getStatus() == ApplicationStatus.REJECTED) {
                    rejectedApplications++;
                }
            }
            
            // Print summary
            System.out.println("\nSummary:");
            System.out.println("Total Applications: " + totalApplications);
            System.out.println("2-Room Applications: " + twoRoomApplications);
            System.out.println("3-Room Applications: " + threeRoomApplications);
            System.out.println("Approved/Booked: " + approvedApplications);
            System.out.println("Pending: " + pendingApplications);
            System.out.println("Rejected: " + rejectedApplications);
            
            // Detailed list
            System.out.println("\nDetailed Application List:");
            System.out.printf("%-15s %-15s %-10s %-15s %-15s%n",
                    "Applicant", "NRIC", "Unit Type", "Status", "Assigned Unit");
            System.out.println(FileUtils.repeatChar('=', 80));
            
            for (Application app : projectApplications) {
                User applicant = findUserByNric(app.getApplicantNric());
                String applicantName = (applicant != null) ? applicant.getName() : "Unknown";
                
                System.out.printf("%-15s %-15s %-10s %-15s %-15s%n",
                        applicantName,
                        app.getApplicantNric(),
                        app.getUnitType(),
                        app.getStatus(),
                        (app.getAssignedUnit() != null) ? app.getAssignedUnit() : "Not Assigned"
                );
            }
            
            // Ask if user wants to save the report to a file
            if (readYesNo("\nSave this report to a file? (Y/N): ")) {
                saveReportToFile(selectedProject.getProjectName() + "_report.txt", 
                                generateProjectReportContent(selectedProject, projectApplications));
            }
            
        } catch (NumberFormatException e) {
            printError("Please enter a valid number.");
        }
    }
    
    // Helper method to find a user by NRIC
    private User findUserByNric(String nric) {
        List<User> users = FileIO.loadUsers();
        for (User user : users) {
            if (user.getNric().equals(nric)) {
                return user;
            }
        }
        return null;
    }
    
    // Helper method to generate report content for saving to a file
    private String generateReportContent(List<Application> applications, String maritalStatusFilter) {
        StringBuilder content = new StringBuilder();
        
        // Add header
        if (maritalStatusFilter == null) {
            content.append("All Applicants Report\n");
        } else {
            content.append(maritalStatusFilter).append(" Applicants Report\n");
        }
        content.append("Generated on: ").append(LocalDate.now()).append("\n\n");
        
        // Add column headers
        content.append(String.format("%-15s %-15s %-25s %-10s %-15s %-10s %s%n",
                "Applicant", "NRIC", "Project", "Flat Type", "Status", "Age", "Marital Status"));
        content.append(FileUtils.repeatChar('=', 100)).append("\n");
        
        // Add data rows
        for (Application app : applications) {
            User applicant = findUserByNric(app.getApplicantNric());
            if (applicant != null) {
                if (maritalStatusFilter == null || applicant.getMaritalStatus().toString().equalsIgnoreCase(maritalStatusFilter)) {
                    content.append(String.format("%-15s %-15s %-25s %-10s %-15s %-10d %s%n",
                            applicant.getName(),
                            app.getApplicantNric(),
                            app.getProjectName(),
                            app.getUnitType(),
                            app.getStatus(),
                            applicant.getAge(),
                            applicant.getMaritalStatus()
                    ));
                }
            }
        }
        
        content.append(FileUtils.repeatChar('=', 100)).append("\n");
        content.append("Total Applications: ").append(applications.size());
        
        return content.toString();
    }
    
    // Helper method to generate project-specific report content
    private String generateProjectReportContent(Project project, List<Application> applications) {
        StringBuilder content = new StringBuilder();
        
        // Project Info
        content.append("Project Report: ").append(project.getProjectName()).append("\n");
        content.append("Neighborhood: ").append(project.getNeighborhood()).append("\n");
        content.append("Application Period: ").append(project.getApplicationOpeningDate())
               .append(" to ").append(project.getApplicationClosingDate()).append("\n");
        content.append("Generated on: ").append(LocalDate.now()).append("\n\n");
        
        // Summary
        int totalApplications = applications.size();
        int twoRoomApplications = 0;
        int threeRoomApplications = 0;
        int approvedApplications = 0;
        int pendingApplications = 0;
        int rejectedApplications = 0;
        
        for (Application app : applications) {
            if (app.getUnitType().equals("2-Room")) {
                twoRoomApplications++;
            } else if (app.getUnitType().equals("3-Room")) {
                threeRoomApplications++;
            }
            
            if (app.getStatus() == ApplicationStatus.APPROVED || app.getStatus() == ApplicationStatus.BOOKED) {
                approvedApplications++;
            } else if (app.getStatus() == ApplicationStatus.PENDING) {
                pendingApplications++;
            } else if (app.getStatus() == ApplicationStatus.REJECTED) {
                rejectedApplications++;
            }
        }
        
        content.append("Summary:\n");
        content.append("Total Applications: ").append(totalApplications).append("\n");
        content.append("2-Room Applications: ").append(twoRoomApplications).append("\n");
        content.append("3-Room Applications: ").append(threeRoomApplications).append("\n");
        content.append("Approved/Booked: ").append(approvedApplications).append("\n");
        content.append("Pending: ").append(pendingApplications).append("\n");
        content.append("Rejected: ").append(rejectedApplications).append("\n\n");
        
        // Detailed list
        content.append("Detailed Application List:\n");
        content.append(String.format("%-15s %-15s %-10s %-15s %-15s%n",
                "Applicant", "NRIC", "Unit Type", "Status", "Assigned Unit"));
        content.append(FileUtils.repeatChar('=', 80)).append("\n");
        
        for (Application app : applications) {
            User applicant = findUserByNric(app.getApplicantNric());
            String applicantName = (applicant != null) ? applicant.getName() : "Unknown";
            
            content.append(String.format("%-15s %-15s %-10s %-15s %-15s%n",
                    applicantName,
                    app.getApplicantNric(),
                    app.getUnitType(),
                    app.getStatus(),
                    (app.getAssignedUnit() != null) ? app.getAssignedUnit() : "Not Assigned"
            ));
        }
        
        return content.toString();
    }
    
    // Helper method to save a report to a file
    private void saveReportToFile(String filename, String content) {
        try {
            // Create Reports directory if it doesn't exist
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("Reports"));
            
            String filePath = "Reports/" + filename;
            java.nio.file.Files.writeString(java.nio.file.Paths.get(filePath), content);
            printSuccess("Report saved to " + filePath);
        } catch (Exception e) {
            printError("Error saving report: " + e.getMessage());
        }
    }
    
    private void viewAllEnquiries() {
        printHeader("View All Enquiries");
        List<Enquiry> enquiries = enquiryFacade.getAllEnquiries();
        if (enquiries.isEmpty()) {
            printError("No enquiries found.");
        } else {
            for (Enquiry enq : enquiries) {
                System.out.println(enq);
            }
        }
    }
    
    private void replyToEnquiries() {
        printHeader("REPLY TO PROJECT ENQUIRIES");
        
        // Get projects managed by this manager
        List<Project> myProjects = getMyProjects();
        if (myProjects.isEmpty()) {
            printError("You are not managing any projects.");
            return;
        }
        
        // Let manager select which project's enquiries to view
        System.out.println("Select a project to view enquiries:");
        for (int i = 0; i < myProjects.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, myProjects.get(i).getProjectName());
        }
        
        int projectChoice = readChoice("Select project (0 to cancel): ", 0, myProjects.size());
        if (projectChoice == 0) return;
        
        Project selectedProject = myProjects.get(projectChoice - 1);
        List<Enquiry> projectEnquiries = enquiryFacade.getEnquiriesByProject(selectedProject.getProjectName());
        
        // Filter for unanswered enquiries
        List<Enquiry> pendingEnquiries = new ArrayList<>();
        for (Enquiry enq : projectEnquiries) {
            if (enq.getReply() == null || enq.getReply().isEmpty()) {
                pendingEnquiries.add(enq);
            }
        }
        
        if (pendingEnquiries.isEmpty()) {
            printMessage("No pending enquiries for " + selectedProject.getProjectName());
            return;
        }
        
        // Display pending enquiries
        printHeader("PENDING ENQUIRIES FOR " + selectedProject.getProjectName());
        System.out.printf("%-5s %-15s %-15s %-40s %-15s\n", 
                        "No.", "Enquiry ID", "Applicant", "Message", "Date");
        printDivider();
        
        for (int i = 0; i < pendingEnquiries.size(); i++) {
            Enquiry enq = pendingEnquiries.get(i);
            System.out.printf("%-5d %-15s %-15s %-40s %-15s\n", 
                            i + 1, 
                            truncate(enq.getEnquiryId(), 15),
                            truncate(enq.getApplicantNric(), 15),
                            truncate(enq.getMessage(), 40),
                            (enq.getSubmittedAt() != null) ? enq.getSubmittedAt().toLocalDate() : "Unknown");
        }
        
        // Let manager select which enquiry to reply to
        int enquiryChoice = readChoice("Select enquiry to reply to (0 to cancel): ", 0, pendingEnquiries.size());
        if (enquiryChoice == 0) return;
        
        Enquiry selectedEnquiry = pendingEnquiries.get(enquiryChoice - 1);
        
        // Display the full enquiry and get response
        printHeader("REPLY TO ENQUIRY");
        System.out.println("Enquiry ID: " + selectedEnquiry.getEnquiryId());
        System.out.println("From: " + selectedEnquiry.getApplicantNric());
        System.out.println("Project: " + selectedEnquiry.getProjectName());
        System.out.println("Date: " + (selectedEnquiry.getSubmittedAt() != null ? 
                                    selectedEnquiry.getSubmittedAt().toLocalDate() : "Unknown"));
        System.out.println("\nEnquiry:");
        System.out.println(selectedEnquiry.getMessage());
        printDivider();
        
        System.out.println("Enter your response (or type /cancel to cancel):");
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
            // Submit the response
            enquiryFacade.replyEnquiry(selectedEnquiry.getEnquiryId(), response);
            printSuccess("Response submitted successfully.");
        } catch (Exception e) {
            printError("Error replying to enquiry: " + e.getMessage());
        }
    }
    
    private List<Project> getMyProjects() {
        List<Project> myProjects = new ArrayList<>();
        List<Project> allProjects = projectFacade.getAllProjects();
        for (Project project : allProjects) {
            if (project.getManager().equalsIgnoreCase(projectManager.getNric())) {
                myProjects.add(project);
            }
        }
        return myProjects;
    }

    private void changePassword() {
        printHeader("Change Password");
        String current = readString("Enter your current password: ");
        if (!projectManager.getPassword().equals(current)) {
            printError("Incorrect current password.");
            return;
        }
        String newPass = readString("Enter your new password: ");
        projectManager.setPassword(newPass);
        printSuccess("Password changed successfully.");
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
}
