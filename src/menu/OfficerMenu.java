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
import users.Applicant;
import utils.FileUtils;
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
    
    private boolean hasAppliedForProjectAsApplicant(String projectName) {
        try {
            // Get all applications
            List<Application> allApplications = new ArrayList<>();
            if (appFacade instanceof access.application.ApplicationHandler) {
                allApplications = ((access.application.ApplicationHandler) appFacade).getAllApplications();
            }
            
            // Check if the officer has applied for this project as an applicant
            for (Application app : allApplications) {
                if (app.getProjectName().equals(projectName) && 
                    app.getApplicantNric().equals(officer.getNric())) {
                    return true;
                }
            }
        } catch (Exception e) {
            // If there's an error, return false to be safe
            return false;
        }
        
        return false;
    }

    private void registerForProject() {
        printHeader("REGISTER FOR PROJECT");
        
        // Get officer's current registrations
        List<OfficerRegistration> myRegistrations = regFacade.getRegistrationsForOfficer(officer.getNric());
        
        // Check for active registrations (approved or pending)
        boolean hasApprovedRegistration = false;
        boolean hasPendingRegistration = false;
        
        for (OfficerRegistration reg : myRegistrations) {
            if (reg.getStatus() == OfficerRegistrationStatus.APPROVED) {
                hasApprovedRegistration = true;
                printError("You are already registered to handle a project.");
                System.out.println("You can view available projects but cannot register for them.");
                break;
            } else if (reg.getStatus() == OfficerRegistrationStatus.PENDING) {
                hasPendingRegistration = true;
                printError("You have a pending registration request.");
                System.out.println("You can view available projects but cannot submit a new registration.");
                break;
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
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Display available projects with remaining slots
        printHeader("AVAILABLE PROJECTS");
        System.out.printf("%-3s %-25s %-15s %-25s %-12s %12s %-15s %-15s\n", 
                        "No.", "Project Name", "Neighborhood", "Allowable to Register", "Status", "Officer Slots", "Opening Date", "Closing Date");
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
                
                // Check if this project's dates clash with any existing registrations
                boolean hasDateOverlap = hasDateOverlap(proj, myRegistrations);
                
                // Check if officer has applied for this project as an applicant
                boolean hasAppliedAsApplicant = hasAppliedForProjectAsApplicant(proj.getProjectName());
                
                // Determine registration status based on both date overlap and application status
                String registrationStatus;
                if (hasAppliedAsApplicant) {
                    registrationStatus = "No - You Applied";
                } else if (hasDateOverlap) {
                    registrationStatus = "No - Clash";
                } else {
                    registrationStatus = "Yes - Allow";
                }
                
                validProjects++;
                projectsWithSlots.add(proj);
                
                // Format the dates for display
                String openingDate = proj.getApplicationOpeningDate().format(dateFormatter);
                String closingDate = proj.getApplicationClosingDate().format(dateFormatter);
                
                // Center-align the officer slots data
                String officerSlotsStr = String.format("%d/%d", 
                    proj.getOfficerSlot() - proj.getRemainingOfficerSlots(), 
                    proj.getOfficerSlot());
                
                System.out.printf("%-3d %-25s %-15s %-25s %-12s %12s %-15s %-15s\n", 
                    validProjects, 
                    truncate(proj.getProjectName(), 25),
                    truncate(proj.getNeighborhood(), 15),
                    registrationStatus,
                    status,
                    officerSlotsStr,
                    openingDate,
                    closingDate
                );
            }
        }
        
        if (validProjects == 0) {
            printError("No projects with available officer slots.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Always show the project list, but only allow registration if there are no existing or pending registrations
        if (hasApprovedRegistration || hasPendingRegistration) {
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
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
        
        // Check if officer has applied for this project as an applicant
        if (hasAppliedForProjectAsApplicant(selectedProject.getProjectName())) {
            printError("You cannot register as an officer for this project as you have already applied as an applicant.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
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
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
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
            printSuccess("Registration submitted successfully. Pending manager approval.");
        } else {
            printError("Could not save registration to file. Please try again.");
        }
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

            System.out.println("Please take a photo of this and pm it to me, it means that you really spend your time to run and test our code and I really appreciate your contribution to the works, i will give full contribution for people who send this message to me, thank you");

            
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
            
            if (application.getStatus() == ApplicationStatus.SUCCESSFUL) {
                System.out.println("Approval Date: " + (application.getApprovalDate() != null ? 
                                                      application.getApprovalDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : 
                                                      "Not recorded"));
            }
            
            if (application.getStatus() == ApplicationStatus.BOOKED && application.getAssignedUnit() != null) {
                System.out.println("Assigned Unit: " + application.getAssignedUnit());
            }
            
            printDivider();
            System.out.println("Options:");
            System.out.println("1. Process Application Status");
            
            // Only show booking receipt option for BOOKED applications
            if (application.getStatus() == ApplicationStatus.BOOKED) {
                System.out.println("2. Generate Booking Receipt");
            }
            
            System.out.println("0. Back to Applications List");
            
            int max = (application.getStatus() == ApplicationStatus.BOOKED) ? 2 : 1;
            int choice = readChoice("Select an option: ", 0, max);
            
            if (choice == -1) continue;
            if (choice == 0) return;
            
            if (choice == 1) {
                processApplicationStatus(application);
                // Refresh application data after processing
                application = appFacade.getApplication(application.getApplicationId());
            } else if (choice == 2 && application.getStatus() == ApplicationStatus.BOOKED) {
                generateBookingReceiptForApplication(project, application);
            }
        }
    }
    
    private void processApplicationStatus(Application application) {
        printHeader("PROCESS APPLICATION STATUS");
        System.out.println("Current Status: " + application.getStatus());
        
        if (application.getStatus() == ApplicationStatus.SUCCESSFUL) {
            // If application is successful but doesn't have a unit assigned, offer to assign a unit
            if (application.getAssignedUnit() == null || application.getAssignedUnit().isEmpty()) {
                printMessage("This application has been marked as successful by a manager and is ready for unit assignment.");
                
                if (!readYesNo("Would you like to assign a unit and mark as BOOKED? (Y/N): ")) {
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    return;
                }
                
                // Get project to check for available units
                Project project = getProjectByName(application.getProjectName());
                if (project == null) {
                    printError("Could not find project information.");
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    return;
                }
                
                String unitType = application.getUnitType();
                int availableUnits = project.getAvailableUnits(unitType);
                
                if (availableUnits <= 0) {
                    printError("There are no available units of type " + unitType + " for this project.");
                    if (!readYesNo("Continue with assignment anyway? (Y/N): ")) {
                        return;
                    }
                }
                
                // Generate a unit number
                String unitNumber = generateUnitNumber(unitType, project.getProjectName());
                
                // Confirm the assignment
                System.out.println("\nAssigning unit: " + unitNumber);
                System.out.println("Unit Type: " + unitType);
                
                if (!readYesNo("Confirm unit assignment? (Y/N): ")) {
                    printMessage("Unit assignment cancelled.");
                    return;
                }
                
                try {
                    // Preserve the approval date or set it to today if it's null
                    LocalDate approvalDate = application.getApprovalDate();
                    if (approvalDate == null) {
                        approvalDate = LocalDate.now();
                    }
                    
                    // Update application status
                    application.setStatus(ApplicationStatus.BOOKED);
                    application.setAssignedUnit(unitNumber);
                    application.setAssignedOfficer(officer.getNric());
                    application.setRemarks("BOOKED: Unit assigned by " + officer.getName());
                    
                    // Ensure approval date is set
                    application.setApprovalDate(approvalDate);
                    
                    // Decrease available units count in project
                    project.decrementAvailableUnits(unitType);
                    
                    // Save project changes
                    if (projectFacade instanceof ProjectHandler) {
                        ((ProjectHandler) projectFacade).updateProject(project);
                        ((ProjectHandler) projectFacade).saveChanges();
                    }
                    
                    // Update application
                    appFacade.updateApplication(application);
                    
                    // Save changes to CSV file
                    if (appFacade instanceof access.application.ApplicationHandler) {
                        ((access.application.ApplicationHandler) appFacade).saveChanges();
                    }
                    
                    printSuccess("Unit successfully assigned! Application status updated to BOOKED.");
                    printMessage("You can now generate a booking receipt for this application.");
                    
                } catch (Exception e) {
                    printError("Error assigning unit: " + e.getMessage());
                }
                
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                return;
            } else {
                // Application already has a unit assigned
                printMessage("This application already has unit " + application.getAssignedUnit() + " assigned.");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                return;
            }
        } else if (application.getStatus() == ApplicationStatus.UNSUCCESSFUL) {
            // Show message based on the current status
            printMessage("This application is unsuccessful and cannot be processed further.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        } else if (application.getStatus() == ApplicationStatus.BOOKED) {
            printMessage("This application has already been processed and a unit has been assigned.");
            System.out.println("Assigned Unit: " + application.getAssignedUnit());
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        } else if (application.getStatus() == ApplicationStatus.PENDING) {
            // For pending applications, show that officers cannot process them
            printMessage("Officers cannot process pending applications. Only managers can mark applications as successful or unsuccessful.");
            printMessage("Please refer this application to a project manager for initial processing.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Get a project by its name
     * @param projectName The name of the project to find
     * @return The Project object if found, null otherwise
     */
    private Project getProjectByName(String projectName) {
        List<Project> projects = projectFacade.getProjectsForOfficer(officer.getNric());
        for (Project project : projects) {
            if (project.getProjectName().equals(projectName)) {
                return project;
            }
        }
        return null;
    }
    
    /**
     * Generates a unique unit number for a new assignment
     * @param unitType The type of unit (2-Room or 3-Room)
     * @param projectName The project name
     * @return A generated unit number
     */
    private String generateUnitNumber(String unitType, String projectName) {
        // Generate a unique identifier based on project name
        String projectCode = projectName.substring(0, Math.min(3, projectName.length())).toUpperCase();
        
        // Get the unit type prefix
        String typePrefix = unitType.startsWith("2") ? "2R" : "3R";
        
        // Load all applications for this project to check for existing unit numbers
        List<Application> projectApplications = new ArrayList<>();
        if (appFacade instanceof access.application.ApplicationHandler) {
            projectApplications = appFacade.getApplicationsForProject(projectName);
        }
        
        // Keep generating until we find an unused unit number
        String unitNumber;
        boolean isUnique;
        do {
            // Generate a random number between 100 and 999
            int random = 100 + (int)(Math.random() * 900);
            
            // Combine to form a unit number
            unitNumber = projectCode + "-" + typePrefix + "-" + random;
            
            // Check if this unit number already exists in any application
            isUnique = true;
            for (Application app : projectApplications) {
                if (unitNumber.equals(app.getAssignedUnit())) {
                    isUnique = false;
                    break;
                }
            }
        } while (!isUnique);
        
        return unitNumber;
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
        
        // Filter for BOOKED applications only
        List<Application> bookedApplications = new ArrayList<>();
        for (Application app : applications) {
            if (app.getStatus() == ApplicationStatus.BOOKED) {
                bookedApplications.add(app);
            }
        }
        
        if (bookedApplications.isEmpty()) {
            printError("No booked applications found for this project. Only applications with BOOKED status can have receipts generated.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Display booked applications in a table format
        printHeader("BOOKED APPLICATIONS FOR " + selectedProject.getProjectName());
        System.out.printf("%-5s %-15s %-15s %-15s %-20s\n", 
                        "No.", "Application ID", "Applicant", "Unit Type", "Unit Number");
        printDivider();
                
        for (int i = 0; i < bookedApplications.size(); i++) {
            Application app = bookedApplications.get(i);
            
            System.out.printf("%-5d %-15s %-15s %-15s %-20s\n", 
                            i + 1, 
                            app.getApplicationId(), 
                            truncate(app.getApplicantNric(), 15),
                            truncate(app.getUnitType(), 15),
                            app.getAssignedUnit() != null ? app.getAssignedUnit() : "Not assigned"
            );
        }
        
        int appChoice = readChoice("\nSelect application to generate booking receipt (0 to cancel): ", 0, bookedApplications.size());
        if (appChoice == 0) return;
        
        Application selectedApp = bookedApplications.get(appChoice - 1);
        
        // Generate booking receipt for selected application
        generateBookingReceiptForApplication(selectedProject, selectedApp);
    }
    
    private void generateBookingReceiptForApplication(Project project, Application application) {
        printHeader("GENERATE BOOKING RECEIPT");
        
        if (application.getStatus() != ApplicationStatus.BOOKED) {
            printError("Cannot generate a booking receipt for an application that is not booked. Only BOOKED applications can have receipts generated.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        try {
            // Get applicant information
            Applicant applicant = null;
            try {
                // Get the applicant from the FileIO system instead of AuthenticationSystem
                List<Applicant> applicants = io.FileIO.loadApplicants();
                for (Applicant app : applicants) {
                    if (app.getNric().equals(application.getApplicantNric())) {
                        applicant = app;
                        break;
                    }
                }
            } catch (Exception e) {
                printError("Error loading applicant data: " + e.getMessage());
            }
            
            if (applicant == null) {
                printError("Could not find applicant information.");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                return;
            }
            
            // Find the unit info for this application using the units map
            UnitInfo selectedUnit = null;
            String unitType = application.getUnitType();
            if (project.getUnits().containsKey(unitType)) {
                selectedUnit = project.getUnits().get(unitType);
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
            
            // Handle null approval date by using the current date
            LocalDate approvalDate = application.getApprovalDate();
            if (approvalDate == null) {
                approvalDate = LocalDate.now();
            }
            receipt.append("- Approval Date: ").append(approvalDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))).append("\n\n");
            
            receipt.append("Applicant Information:\n");
            receipt.append("- Name: ").append(applicant.getName()).append("\n");
            receipt.append("- NRIC: ").append(applicant.getNric()).append("\n");
            
            receipt.append("\nUnit Information:\n");
            receipt.append("- Unit Number: ").append(application.getAssignedUnit()).append("\n");
            receipt.append("- Unit Type: ").append(unitType).append("\n");
            receipt.append("- Unit Size: ").append("Standard").append(" sqm\n"); // UnitInfo doesn't store size, using placeholder
            receipt.append("- Unit Price: $").append(String.format("%.2f", selectedUnit.getSellingPrice())).append("\n\n");
            
            // Calculate booking fee as 5% of the selling price
            double bookingFee = selectedUnit.getSellingPrice() * 0.05;
            double remainingAmount = selectedUnit.getSellingPrice() - bookingFee;
            
            receipt.append("Payment Details:\n");
            receipt.append("- Booking Fee (5% of unit price): $").append(String.format("%.2f", bookingFee)).append("\n");
            receipt.append("- Remaining Amount: $").append(String.format("%.2f", remainingAmount)).append("\n\n");
            
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
                
                // Replace Files.writeString (JDK 11+) with Files.write (JDK 8 compatible)
                Files.write(Paths.get("Receipts", receiptFileName), 
                           receipt.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                
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
        System.out.println("Date: " + enquiry.getSubmittedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        
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
        
        System.out.println("Select a project to view enquiries:");
        for (int i = 0; i < myProjects.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, myProjects.get(i).getProjectName());
        }
        
        int selection = readChoice("Select project (0 to cancel): ", 0, myProjects.size());
        if (selection == 0 || selection == -1) return;
        
        Project selectedProject = myProjects.get(selection - 1);
        List<Enquiry> allEnquiries = enquiryFacade.getEnquiriesForProject(selectedProject.getProjectName());
        
        // Filter for open or pending enquiries
        List<Enquiry> openEnquiries = new ArrayList<>();
        for (Enquiry enquiry : allEnquiries) {
            if (enquiry.getStatus() == EnquiryStatus.OPEN) {
                openEnquiries.add(enquiry);
            }
        }
        
        if (openEnquiries.isEmpty()) {
            printMessage("No open enquiries for " + selectedProject.getProjectName());
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Display open enquiries
        printHeader("OPEN ENQUIRIES FOR " + selectedProject.getProjectName());
        System.out.printf("%-5s %-15s %-15s %-40s %-15s\n", 
                        "No.", "Enquiry ID", "Applicant", "Message", "Submitted Date");
        printDivider();
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        
        for (int i = 0; i < openEnquiries.size(); i++) {
            Enquiry enq = openEnquiries.get(i);
            System.out.printf("%-5d %-15s %-15s %-40s %-15s\n", 
                            i + 1, 
                            enq.getEnquiryId(), 
                            truncate(enq.getApplicantNric(), 15),
                            truncate(enq.getMessage(), 40),
                            enq.getSubmittedAt().toLocalDate().format(dateFormatter)
            );
        }
        
        int enquiryChoice = readChoice("\nSelect enquiry to reply to (0 to cancel): ", 0, openEnquiries.size());
        if (enquiryChoice == 0) return;
        
        Enquiry selectedEnquiry = openEnquiries.get(enquiryChoice - 1);
        
        printHeader("REPLY TO ENQUIRY");
        System.out.println("Enquiry ID: " + selectedEnquiry.getEnquiryId());
        System.out.println("From: " + selectedEnquiry.getApplicantNric());
        System.out.println("Project: " + selectedEnquiry.getProjectName());
        System.out.println("Date: " + selectedEnquiry.getSubmittedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));
        System.out.println("\nEnquiry Message:");
        System.out.println(selectedEnquiry.getMessage());
        
        printDivider();
        System.out.println("Enter your reply (leave empty to cancel):");
        String reply = scanner.nextLine().trim();
        
        if (reply.isEmpty()) {
            printMessage("Reply cancelled.");
            return;
        }
        
        try {
            // Use the replyEnquiry method instead of updateEnquiry
            enquiryFacade.replyEnquiry(selectedEnquiry.getEnquiryId(), reply);
            
            // Save changes to CSV file
            if (enquiryFacade instanceof access.enquiry.EnquiryHandler) {
                ((access.enquiry.EnquiryHandler) enquiryFacade).saveChanges();
                printSuccess("Reply sent successfully and saved to database!");
            } else {
                printSuccess("Reply sent successfully!");
            }
        } catch (Exception e) {
            printError("Error replying to enquiry: " + e.getMessage());
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
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
        System.out.println("Date: " + selectedEnquiry.getSubmittedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
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
        
        // Use the replyEnquiry method from the interface instead of updateEnquiry
        enquiryFacade.replyEnquiry(selectedEnquiry.getEnquiryId(), reply);
        
        // Save changes to CSV file
        if (enquiryFacade instanceof access.enquiry.EnquiryHandler) {
            ((access.enquiry.EnquiryHandler) enquiryFacade).saveChanges();
            printSuccess("Reply sent successfully and saved to database!");
        } else {
            printSuccess("Reply sent successfully!");
        }
    }

    private void respondToEnquiry(Enquiry enquiry) {
        printHeader("REPLY TO ENQUIRY");
        System.out.println("Enquiry ID: " + enquiry.getEnquiryId());
        System.out.println("From: " + enquiry.getApplicantNric());
        System.out.println("Date: " + enquiry.getSubmittedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
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
            
            // Save changes to CSV file
            if (enquiryFacade instanceof access.enquiry.EnquiryHandler) {
                ((access.enquiry.EnquiryHandler) enquiryFacade).saveChanges();
                printSuccess("Response submitted successfully and saved to database!");
            } else {
                printSuccess("Response submitted successfully.");
            }
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
            boolean officerFound = false;
            for (int i = 0; i < officers.size(); i++) {
                if (officers.get(i).getNric().equals(officer.getNric())) {
                    officers.set(i, officer);
                    officerFound = true;
                    break;
                }
            }
            
            if (!officerFound) {
                throw new IllegalStateException("Officer not found in the database");
            }
            
            // Save the updated officers list back to the file
            io.FileIO.saveOfficers(officers);
            
            printSuccess("Password changed successfully.");
        } catch (Exception e) {
            printError("Error updating password: " + e.getMessage());
            // Revert the password change in memory if file update failed
            // Note: we don't have the old password here, so we can't revert it
        }
    }
    
    // ----- Helper Methods -----
    
    private List<Project> getAssignedProjects() {
        // Need to use the method provided by the interface, which is getProjectsForOfficer
        return projectFacade.getProjectsForOfficer(officer.getNric());
    }
    
    // Helper method to get the response date (replied date) from an Enquiry
    private LocalDateTime getResponseDate(Enquiry enquiry) {
        return enquiry.getRepliedAt();
    }
    
    /**
     * Checks if a project's application period overlaps with any of the officer's existing registrations
     * @param project The project to check for overlap
     * @param registrations The officer's existing registrations
     * @return true if there is a date overlap, false otherwise
     */
    private boolean hasDateOverlap(Project project, List<OfficerRegistration> registrations) {
        // If the officer has no registrations, there can't be any overlap
        if (registrations == null || registrations.isEmpty()) {
            return false;
        }
        
        // For each registration, check if the project periods overlap
        for (OfficerRegistration reg : registrations) {
            // Only consider approved or pending registrations
            if (reg.getStatus() == OfficerRegistrationStatus.APPROVED || 
                reg.getStatus() == OfficerRegistrationStatus.PENDING) {
                
                // Get the project for this registration
                Project existingProject = null;
                if (projectFacade instanceof ProjectHandler) {
                    existingProject = ((ProjectHandler) projectFacade).getProject(reg.getProjectName());
                }
                
                if (existingProject != null) {
                    // Check for date overlap
                    // Two date ranges overlap if:
                    // 1. start1 <= end2 AND start2 <= end1
                    boolean overlap = 
                        !project.getApplicationOpeningDate().isAfter(existingProject.getApplicationClosingDate()) &&
                        !existingProject.getApplicationOpeningDate().isAfter(project.getApplicationClosingDate());
                    
                    if (overlap) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Helper method to truncate strings for display purposes
     * @param str The string to truncate
     * @param maxLength The maximum length before truncation
     * @return The truncated string
     */
    private String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Get the manager's name from their NRIC
     * @param managerNric The NRIC of the manager
     * @return The manager's name, or the NRIC if the manager is not found
     */
    private String getManagerName(String managerNric) {
        // This would ideally use a service to look up the manager's name
        // For now, just return the NRIC as a fallback
        return managerNric;
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
}