package menu;

import access.application.ManagerApplicationFeatures;
import access.enquiry.ManagerEnquiryFeatures;
import access.officerregistration.ManagerOfficerRegistrationFeatures;
import access.project.ManagerProjectFeatures;
import access.withdrawal.ManagerWithdrawalFeatures;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import models.Application;
import models.Enquiry;
import models.Project;
import models.OfficerRegistration;
import models.WithdrawalRequest;
import users.ProjectManager;
import users.User;
import utils.Constants;

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
            System.out.println("\n=== HDB Manager Menu ===");
            System.out.println("Welcome, " + projectManager.getName());
            System.out.println("\n=== Project Management ===");
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
            System.out.println("\n=== Reports ===");
            System.out.println("11. Generate Reports");
            System.out.println("\n=== Enquiries ===");
            System.out.println("12. View All Enquiries");
            System.out.println("13. Reply to Project Enquiries");
            System.out.println("\n=== System ===");
            System.out.println("14. Change Password");
            System.out.println("15. Logout");
            System.out.print("Enter your choice: ");
            
            int choice = 0;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }
            
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
                case 15: System.out.println("Logging out..."); return;
                default: System.out.println("Invalid choice. Please try again."); break;
            }
        }
    }
    
    // --- Project Management Methods ---
    private void createProject() {
        System.out.println("\n=== Create New Project ===");
        if (isHandlingActiveProject()) {
            System.out.println("You are already handling a project within an active application period.");
            return;
        }
        try {
            System.out.print("Enter Project Name: ");
            String projectName = scanner.nextLine().trim();
            System.out.print("Enter Neighborhood: ");
            String neighborhood = scanner.nextLine().trim();
            System.out.print("Enter number of 2-Room units: ");
            int twoRoomUnits = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter price for 2-Room units: ");
            double twoRoomPrice = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Enter number of 3-Room units: ");
            int threeRoomUnits = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter price for 3-Room units: ");
            double threeRoomPrice = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Enter application opening date (" + Constants.DATE_FORMAT + "): ");
            LocalDate openingDate = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);
            System.out.print("Enter application closing date (" + Constants.DATE_FORMAT + "): ");
            LocalDate closingDate = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);
            System.out.print("Enter number of HDB Officer slots (max 10): ");
            int officerSlots = Math.min(10, Integer.parseInt(scanner.nextLine().trim()));
            
            Project project = new Project(projectName, neighborhood, openingDate, closingDate, projectManager.getNric(), officerSlots);
            project.addUnitType("2-Room", twoRoomUnits, twoRoomPrice);
            project.addUnitType("3-Room", threeRoomUnits, threeRoomPrice);
            
            projectFacade.addProject(project);
            System.out.println("Project created successfully!");
        } catch (Exception e) {
            System.out.println("Error creating project: " + e.getMessage());
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
        System.out.println("\n=== Edit Project ===");
        List<Project> myProjects = projectFacade.getProjectsByManager(projectManager.getNric());
        if (myProjects.isEmpty()) {
            System.out.println("You have no projects to edit.");
            return;
        }
        for (int i = 0; i < myProjects.size(); i++) {
            Project project = myProjects.get(i);
            System.out.printf("%d. %s (%s)%n", i + 1, project.getProjectName(), project.getNeighborhood());
        }
        try {
            System.out.print("Select project to edit (0 to cancel): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return;
            if (choice < 1 || choice > myProjects.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            Project project = myProjects.get(choice - 1);
            editProjectDetails(project);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    private void editProjectDetails(Project project) {
        while (true) {
            System.out.println("\n=== Editing " + project.getProjectName() + " ===");
            System.out.println("1. Edit Application Period");
            System.out.println("2. Edit Officer Slots");
            System.out.println("3. Save Changes");
            System.out.println("4. Cancel");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        System.out.print("Enter new opening date (" + Constants.DATE_FORMAT + "): ");
                        LocalDate newOpening = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);
                        System.out.print("Enter new closing date (" + Constants.DATE_FORMAT + "): ");
                        LocalDate newClosing = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);
                        project.setOpeningDate(newOpening);
                        project.setClosingDate(newClosing);
                        break;
                    case 2:
                        System.out.print("Enter new number of officer slots (max 10): ");
                        int slots = Math.min(10, Integer.parseInt(scanner.nextLine().trim()));
                        project.setOfficerSlots(slots);
                        break;
                    case 3:
                        projectFacade.updateProject(project);
                        System.out.println("Project updated successfully!");
                        return;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void deleteProject() {
        System.out.println("\n=== Delete Project ===");
        List<Project> myProjects = projectFacade.getProjectsByManager(projectManager.getNric());
        if (myProjects.isEmpty()) {
            System.out.println("You have no projects to delete.");
            return;
        }
        for (int i = 0; i < myProjects.size(); i++) {
            Project p = myProjects.get(i);
            System.out.printf("%d. %s (%s)%n", i + 1, p.getProjectName(), p.getNeighborhood());
        }
        try {
            System.out.print("Select project to delete (0 to cancel): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return;
            if (choice < 1 || choice > myProjects.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            Project project = myProjects.get(choice - 1);
            System.out.print("Are you sure you want to delete this project? (Y/N): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                projectFacade.deleteProject(project.getProjectName());
                System.out.println("Project deleted successfully!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    private void viewAllProjects() {
        System.out.println("\n=== All Projects ===");
        List<Project> allProjects = projectFacade.getAllProjects();
        displayProjects(allProjects);
    }
    
    private void viewMyProjects() {
        System.out.println("\n=== My Projects ===");
        List<Project> myProjects = projectFacade.getProjectsByManager(projectManager.getNric());
        displayProjects(myProjects);
    }
    
    private void displayProjects(List<Project> projects) {
        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }
        for (Project project : projects) {
            System.out.println("\n" + project.toString());
        }
    }
    
    private void toggleProjectVisibility() {
        System.out.println("\n=== Toggle Project Visibility ===");
        List<Project> myProjects = projectFacade.getProjectsByManager(projectManager.getNric());
        if (myProjects.isEmpty()) {
            System.out.println("You have no projects to manage.");
            return;
        }
        for (int i = 0; i < myProjects.size(); i++) {
            Project project = myProjects.get(i);
            System.out.printf("%d. %s (Currently: %s)%n", i + 1, project.getProjectName(), project.isVisible() ? "Visible" : "Hidden");
        }
        try {
            System.out.print("Select project to toggle visibility (0 to cancel): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return;
            if (choice < 1 || choice > myProjects.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            Project project = myProjects.get(choice - 1);
            boolean newVisibility = !project.isVisible();
            projectFacade.toggleVisibility(project.getProjectName(), newVisibility);
            System.out.printf("Project visibility toggled to: %s%n", newVisibility ? "Visible" : "Hidden");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    // --- Officer Management ---
    private void viewOfficerRegistrations() {
        System.out.println("\n=== View Officer Registrations ===");
        List<OfficerRegistration> regs = officerRegFacade.getAllOfficerRegistrations();
        if (regs.isEmpty()) {
            System.out.println("No officer registrations found.");
        } else {
            for (OfficerRegistration reg : regs) {
                System.out.println("\n" + reg.toString());
            }
        }
    }
    
    private void processOfficerRegistration() {
        System.out.println("\n=== Process Officer Registration ===");
        List<OfficerRegistration> regs = officerRegFacade.getAllOfficerRegistrations();
        List<OfficerRegistration> pendingRegs = new ArrayList<>();
        for (OfficerRegistration reg : regs) {
            if (reg.getStatus().toString().equalsIgnoreCase("Pending")) {
                pendingRegs.add(reg);
            }
        }
        if (pendingRegs.isEmpty()) {
            System.out.println("No pending officer registrations to process.");
            return;
        }
        for (int i = 0; i < pendingRegs.size(); i++) {
            OfficerRegistration reg = pendingRegs.get(i);
            System.out.printf("%d. Registration ID: %s, Officer NRIC: %s, Project: %s%n", 
                              i + 1, reg.getRegistrationId(), reg.getOfficerNric(), reg.getProjectName());
        }
        try {
            System.out.print("Select registration to process (0 to cancel): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return;
            if (choice < 1 || choice > pendingRegs.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            OfficerRegistration selectedReg = pendingRegs.get(choice - 1);
            System.out.print("Approve registration? (Y/N): ");
            String decision = scanner.nextLine().trim().toUpperCase();
            if (decision.equals("Y")) {
                officerRegFacade.approveRegistration(selectedReg.getRegistrationId());
                // Retrieve the project and update its officer list.
                Project project = projectFacade.getProject(selectedReg.getProjectName());
                if (project != null) {
                    project.addOfficer(selectedReg.getOfficerNric());
                    projectFacade.updateProject(project);
                }
                System.out.println("Registration approved and officer assigned.");
            } else {
                officerRegFacade.rejectRegistration(selectedReg.getRegistrationId());
                System.out.println("Registration rejected.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    // --- Application Management ---
    private void processBTOApplications() {
        System.out.println("\n=== Process BTO Applications ===");
        List<Project> myProjects = getMyProjects();
        if (myProjects.isEmpty()) {
            System.out.println("You have no projects to manage applications for.");
            return;
        }
        System.out.println("Select a project:");
        for (int i = 0; i < myProjects.size(); i++) {
            Project project = myProjects.get(i);
            System.out.printf("%d. %s%n", i + 1, project.getProjectName());
        }
        try {
            System.out.print("Select project (0 to cancel): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return;
            if (choice < 1 || choice > myProjects.size()) {
                System.out.println("Invalid project selection.");
                return;
            }
            Project selectedProject = myProjects.get(choice - 1);
            processApplicationsForProject(selectedProject);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    private void processApplicationsForProject(Project project) {
        System.out.println("\nFeature to be implemented: Process BTO applications for " + project.getProjectName());
        // This method should display pending applications for the project,
        // allow the manager to approve or reject, and update the available unit counts accordingly.
    }
    
    private void processWithdrawalRequests() {
        System.out.println("\n=== Process Withdrawal Requests ===");
        List<Project> myProjects = getMyProjects();
        if (myProjects.isEmpty()) {
            System.out.println("You have no projects to manage withdrawals for.");
            return;
        }
        System.out.println("Select a project:");
        for (int i = 0; i < myProjects.size(); i++) {
            Project project = myProjects.get(i);
            System.out.printf("%d. %s%n", i + 1, project.getProjectName());
        }
        try {
            System.out.print("Select project (0 to cancel): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return;
            if (choice < 1 || choice > myProjects.size()) {
                System.out.println("Invalid project selection.");
                return;
            }
            Project selectedProject = myProjects.get(choice - 1);
            processWithdrawalsForProject(selectedProject);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    private void processWithdrawalsForProject(Project project) {
        System.out.println("\nFeature to be implemented: Process withdrawal requests for " + project.getProjectName());
        // This method should display withdrawal requests for the project,
        // allow approval or rejection, and update the application status and unit availability.
    }
    
    // --- Reports & Enquiries ---
    private void generateReports() {
        while (true) {
            System.out.println("\n=== Generate Reports ===");
            System.out.println("1. Generate All Applicants Report");
            System.out.println("2. Generate Married Applicants Report");
            System.out.println("3. Generate Single Applicants Report");
            System.out.println("4. Generate Project-specific Report");
            System.out.println("5. Back to Manager Menu");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1: generateAllApplicantsReport(); break;
                    case 2: generateMarriedApplicantsReport(); break;
                    case 3: generateSingleApplicantsReport(); break;
                    case 4: generateProjectReport(); break;
                    case 5: return;
                    default: System.out.println("Invalid selection."); break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    

    private void generateAllApplicantsReport() {
        System.out.println("\nFeature to be implemented: Generate all applicants report");
        // Use reportFacade.generateFlatBookingReport(...) method if available.
    }
    
    private void generateMarriedApplicantsReport() {
        System.out.println("\nFeature to be implemented: Generate married applicants report");
    }
    
    private void generateSingleApplicantsReport() {
        System.out.println("\nFeature to be implemented: Generate single applicants report");
    }
    
    private void generateProjectReport() {
        System.out.println("\nFeature to be implemented: Generate project-specific report");
    }
    
    private void viewAllEnquiries() {
        System.out.println("\n=== View All Enquiries ===");
        List<Enquiry> enquiries = enquiryFacade.getAllEnquiries();
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
        } else {
            for (Enquiry enq : enquiries) {
                System.out.println(enq);
            }
        }
    }
    
    private void replyToEnquiries() {
        System.out.println("\n=== Reply to Project Enquiries ===");
        System.out.print("Enter Enquiry ID to reply: ");
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
        System.out.println("\n=== Change Password ===");
        System.out.print("Enter your current password: ");
        String current = scanner.nextLine().trim();
        if (!projectManager.getPassword().equals(current)) {
            System.out.println("Incorrect current password.");
            return;
        }
        System.out.print("Enter your new password: ");
        String newPass = scanner.nextLine().trim();
        projectManager.setPassword(newPass);
        System.out.println("Password changed successfully.");
    }
}
