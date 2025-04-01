package menu;

import auth.User;
import model.Project;
import model.UnitType;
import utils.FileUtils;
import manager.ProjectManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ManagerMenu {
    private Scanner scanner;
    private User user;
    private ProjectManager projectManager;
    private static final String OFFICER_REGISTRATION_FILE = "OfficerRegistrations.txt";
    private static final String WITHDRAWAL_REQUESTS_FILE = "WithdrawalRequests.txt";
    private static final String ENQUIRY_FILE = "EnquiryList.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

    public ManagerMenu(User user) {
        this.scanner = new Scanner(System.in);
        this.user = user;
        this.projectManager = new ProjectManager();
    }

    public void display() {
        while (true) {
            System.out.println("\n=== HDB Manager Menu ===");
            System.out.println("Welcome, " + user.getName());
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

            System.out.println("\n=== Reports & Enquiries ===");
            System.out.println("11. Generate Reports");
            System.out.println("12. View All Enquiries");
            System.out.println("13. Reply to Project Enquiries");

            System.out.println("\n=== System ===");
            System.out.println("14. Change Password");
            System.out.println("15. Logout");

            System.out.print("\nEnter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        createProject();
                        break;
                    case 2:
                        editProject();
                        break;
                    case 3:
                        deleteProject();
                        break;
                    case 4:
                        viewAllProjects();
                        break;
                    case 5:
                        viewMyProjects();
                        break;
                    case 6:
                        toggleProjectVisibility();
                        break;
                    case 7:
                        viewOfficerRegistrations();
                        break;
                    case 8:
                        processOfficerRegistration();
                        break;
                    case 9:
                        processBTOApplications();
                        break;
                    case 10:
                        processWithdrawalRequests();
                        break;
                    case 11:
                        generateReports();
                        break;
                    case 12:
                        viewAllEnquiries();
                        break;
                    case 13:
                        replyToEnquiries();
                        break;
                    case 14:
                        System.out.println("\nFeature to be implemented: Change Password");
                        break;
                    case 15:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void createProject() {
        System.out.println("\n=== Create New Project ===");
        
        // Check if already handling a project in application period
        if (isHandlingActiveProject()) {
            System.out.println("You are already handling a project within an application period.");
            return;
        }

        try {
            // Get project details
            System.out.print("Enter Project Name: ");
            String projectName = scanner.nextLine().trim();
            
            System.out.print("Enter Neighborhood: ");
            String neighborhood = scanner.nextLine().trim();
            
            // 2-Room details
            System.out.print("Enter number of 2-Room units: ");
            int twoRoomUnits = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter price for 2-Room units: ");
            double twoRoomPrice = Double.parseDouble(scanner.nextLine().trim());
            
            // 3-Room details
            System.out.print("Enter number of 3-Room units: ");
            int threeRoomUnits = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter price for 3-Room units: ");
            double threeRoomPrice = Double.parseDouble(scanner.nextLine().trim());
            
            // Application period
            System.out.print("Enter application opening date (MM/DD/YYYY): ");
            LocalDate openingDate = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);
            System.out.print("Enter application closing date (MM/DD/YYYY): ");
            LocalDate closingDate = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);
            
            System.out.print("Enter number of HDB Officer slots (max 10): ");
            int officerSlots = Math.min(10, Integer.parseInt(scanner.nextLine().trim()));

            // Create project
            Project project = new Project(projectName, neighborhood, openingDate, closingDate, 
                user.getNric(), officerSlots);
            
            // Add unit types
            project.addUnitType("2-Room", twoRoomUnits, twoRoomPrice);
            project.addUnitType("3-Room", threeRoomUnits, threeRoomPrice);

            if (projectManager.addProject(project)) {
                System.out.println("Project created successfully!");
            } else {
                System.out.println("Failed to create project. Project name may already exist.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please enter valid numbers.");
        } catch (Exception e) {
            System.out.println("Error creating project: " + e.getMessage());
        }
    }

    private boolean isHandlingActiveProject() {
        List<Project> projects = projectManager.getAllProjects();
        LocalDate now = LocalDate.now();
        
        for (Project project : projects) {
            if (project.getAssignedManager().equals(user.getNric()) &&
                !now.isAfter(project.getClosingDate()) &&
                !now.isBefore(project.getOpeningDate())) {
                return true;
            }
        }
        return false;
    }

    private void editProject() {
        System.out.println("\n=== Edit Project ===");
        List<Project> myProjects = getMyProjects();
        
        if (myProjects.isEmpty()) {
            System.out.println("You have no projects to edit.");
            return;
        }

        // Display projects
        for (int i = 0; i < myProjects.size(); i++) {
            Project project = myProjects.get(i);
            System.out.printf("%d. %s (%s)%n", i + 1, project.getProjectName(), project.getNeighborhood());
        }

        try {
            System.out.print("Select project to edit (0 to cancel): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 0) return;
            if (choice < 1 || choice > myProjects.size()) {
                System.out.println("Invalid project selection.");
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
                        System.out.print("Enter new opening date (MM/DD/YYYY): ");
                        LocalDate openingDate = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);
                        System.out.print("Enter new closing date (MM/DD/YYYY): ");
                        LocalDate closingDate = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);
                        
                        project.setOpeningDate(openingDate);
                        project.setClosingDate(closingDate);
                        break;

                    case 2:
                        System.out.print("Enter new number of officer slots (max 10): ");
                        int slots = Math.min(10, Integer.parseInt(scanner.nextLine().trim()));
                        project.setOfficerSlots(slots);
                        break;

                    case 3:
                        if (projectManager.updateProject(project)) {
                            System.out.println("Project updated successfully!");
                            return;
                        } else {
                            System.out.println("Failed to update project.");
                        }
                        break;

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
        List<Project> myProjects = getMyProjects();
        
        if (myProjects.isEmpty()) {
            System.out.println("You have no projects to delete.");
            return;
        }

        // Display projects
        for (int i = 0; i < myProjects.size(); i++) {
            Project project = myProjects.get(i);
            System.out.printf("%d. %s (%s)%n", i + 1, project.getProjectName(), project.getNeighborhood());
        }

        try {
            System.out.print("Select project to delete (0 to cancel): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 0) return;
            if (choice < 1 || choice > myProjects.size()) {
                System.out.println("Invalid project selection.");
                return;
            }

            Project project = myProjects.get(choice - 1);
            
            System.out.print("Are you sure you want to delete this project? (Y/N): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                if (projectManager.removeProject(project.getProjectName())) {
                    System.out.println("Project deleted successfully!");
                } else {
                    System.out.println("Failed to delete project.");
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void viewAllProjects() {
        System.out.println("\n=== All Projects ===");
        List<Project> allProjects = projectManager.getAllProjects();
        displayProjects(allProjects);
    }

    private void viewMyProjects() {
        System.out.println("\n=== My Projects ===");
        List<Project> myProjects = getMyProjects();
        displayProjects(myProjects);
    }

    private List<Project> getMyProjects() {
        List<Project> myProjects = new ArrayList<>();
        List<Project> allProjects = projectManager.getAllProjects();
        
        for (Project project : allProjects) {
            if (project.getAssignedManager().equals(user.getNric())) {
                myProjects.add(project);
            }
        }
        
        return myProjects;
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
        List<Project> myProjects = getMyProjects();
        
        if (myProjects.isEmpty()) {
            System.out.println("You have no projects to manage.");
            return;
        }

        // Display projects with their current visibility status
        for (int i = 0; i < myProjects.size(); i++) {
            Project project = myProjects.get(i);
            System.out.printf("%d. %s (Currently: %s)%n", 
                i + 1, project.getProjectName(), 
                project.isVisible() ? "Visible" : "Hidden");
        }

        try {
            System.out.print("Select project to toggle visibility (0 to cancel): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 0) return;
            if (choice < 1 || choice > myProjects.size()) {
                System.out.println("Invalid project selection.");
                return;
            }

            Project project = myProjects.get(choice - 1);
            project.setVisible(!project.isVisible());
            
            if (projectManager.updateProject(project)) {
                System.out.printf("Project visibility toggled to: %s%n", 
                    project.isVisible() ? "Visible" : "Hidden");
            } else {
                System.out.println("Failed to update project visibility.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void viewOfficerRegistrations() {
        System.out.println("\n=== Officer Registrations ===");
        List<String[]> registrations = FileUtils.readFile(OFFICER_REGISTRATION_FILE);
        
        if (registrations.isEmpty() || registrations.size() == 1) {
            System.out.println("No pending registrations.");
            return;
        }

        for (int i = 1; i < registrations.size(); i++) {
            String[] reg = registrations.get(i);
            System.out.printf("\nRegistration ID: %s%n", reg[0]);
            System.out.printf("Officer NRIC: %s%n", reg[1]);
            System.out.printf("Project: %s%n", reg[2]);
            System.out.printf("Status: %s%n", reg[3]);
            System.out.printf("Registration Date: %s%n", reg[4]);
        }
    }

    private void processOfficerRegistration() {
        System.out.println("\n=== Process Officer Registration ===");
        List<String[]> registrations = FileUtils.readFile(OFFICER_REGISTRATION_FILE);
        List<String[]> pendingRegistrations = new ArrayList<>();
        
        // Filter pending registrations for manager's projects
        for (int i = 1; i < registrations.size(); i++) {
            String[] reg = registrations.get(i);
            Project project = projectManager.getProject(reg[2]);
            if (project != null && project.getAssignedManager().equals(user.getNric()) 
                && reg[3].equals("PENDING")) {
                pendingRegistrations.add(reg);
            }
        }

        if (pendingRegistrations.isEmpty()) {
            System.out.println("No pending registrations for your projects.");
            return;
        }

        // Display pending registrations
        for (int i = 0; i < pendingRegistrations.size(); i++) {
            String[] reg = pendingRegistrations.get(i);
            System.out.printf("%d. Officer %s for project %s%n", 
                i + 1, reg[1], reg[2]);
        }

        try {
            System.out.print("Select registration to process (0 to cancel): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 0) return;
            if (choice < 1 || choice > pendingRegistrations.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            System.out.print("Approve registration? (Y/N): ");
            String approval = scanner.nextLine().trim().toUpperCase();
            
            String[] selectedReg = pendingRegistrations.get(choice - 1);
            String status = approval.equals("Y") ? "APPROVED" : "REJECTED";
            
            // Update registration status
            for (int i = 1; i < registrations.size(); i++) {
                if (registrations.get(i)[0].equals(selectedReg[0])) {
                    registrations.get(i)[3] = status;
                    break;
                }
            }

            // If approved, add officer to project
            if (status.equals("APPROVED")) {
                Project project = projectManager.getProject(selectedReg[2]);
                if (project != null) {
                    project.addOfficer(selectedReg[1]);
                    projectManager.updateProject(project);
                }
            }

            if (FileUtils.writeFile(OFFICER_REGISTRATION_FILE, registrations)) {
                System.out.println("Registration " + status.toLowerCase() + " successfully!");
            } else {
                System.out.println("Failed to update registration status.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void processBTOApplications() {
        System.out.println("\n=== Process BTO Applications ===");
        List<Project> myProjects = getMyProjects();
        
        if (myProjects.isEmpty()) {
            System.out.println("You have no projects to manage applications for.");
            return;
        }

        // Display projects
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

            Project project = myProjects.get(choice - 1);
            processApplicationsForProject(project);

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void processApplicationsForProject(Project project) {
        System.out.println("\nFeature to be implemented: Process applications for " + project.getProjectName());
        // Will implement:
        // 1. Load pending applications for the project
        // 2. Display application details including eligibility
        // 3. Allow approval/rejection based on flat availability
        // 4. Update application status and unit availability
    }

    private void processWithdrawalRequests() {
        System.out.println("\n=== Process Withdrawal Requests ===");
        List<Project> myProjects = getMyProjects();
        
        if (myProjects.isEmpty()) {
            System.out.println("You have no projects to manage withdrawals for.");
            return;
        }

        // Display projects
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

            Project project = myProjects.get(choice - 1);
            processWithdrawalsForProject(project);

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void processWithdrawalsForProject(Project project) {
        System.out.println("\nFeature to be implemented: Process withdrawals for " + project.getProjectName());
        // Will implement:
        // 1. Load pending withdrawal requests for the project
        // 2. Display application and withdrawal details
        // 3. Allow approval/rejection of withdrawal
        // 4. Update application status and unit availability if approved
    }

    private void generateReports() {
        while (true) {
            System.out.println("\n=== Generate Reports ===");
            System.out.println("1. All Applicants Report");
            System.out.println("2. Married Applicants Report");
            System.out.println("3. Single Applicants Report");
            System.out.println("4. Project-specific Report");
            System.out.println("5. Back to Main Menu");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        generateAllApplicantsReport();
                        break;
                    case 2:
                        generateMarriedApplicantsReport();
                        break;
                    case 3:
                        generateSingleApplicantsReport();
                        break;
                    case 4:
                        generateProjectReport();
                        break;
                    case 5:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void generateAllApplicantsReport() {
        System.out.println("\nFeature to be implemented: Generate all applicants report");
        // Will implement:
        // 1. Load all booked applications
        // 2. Format report with applicant details, flat type, and project
        // 3. Display or save report
    }

    private void generateMarriedApplicantsReport() {
        System.out.println("\nFeature to be implemented: Generate married applicants report");
        // Similar to all applicants but filtered for married status
    }

    private void generateSingleApplicantsReport() {
        System.out.println("\nFeature to be implemented: Generate single applicants report");
        // Similar to all applicants but filtered for single status
    }

    private void generateProjectReport() {
        System.out.println("\nFeature to be implemented: Generate project-specific report");
        // Will implement:
        // 1. Select project
        // 2. Show detailed statistics for the project
        // 3. Include unit allocation, application statistics, etc.
    }

    private void viewAllEnquiries() {
        System.out.println("\n=== All Project Enquiries ===");
        List<String[]> enquiries = FileUtils.readFile(ENQUIRY_FILE);
        
        if (enquiries.isEmpty() || enquiries.size() == 1) {
            System.out.println("No enquiries found.");
            return;
        }

        for (int i = 1; i < enquiries.size(); i++) {
            String[] enq = enquiries.get(i);
            System.out.printf("\nEnquiry ID: %s%n", enq[0]);
            System.out.printf("From: %s%n", enq[1]);
            System.out.printf("Enquiry: %s%n", enq[2]);
            System.out.printf("Response: %s%n", enq[3].isEmpty() ? "No response yet" : enq[3]);
            System.out.printf("Timestamp: %s%n", enq[4]);
        }
    }

    private void replyToEnquiries() {
        System.out.println("\n=== Reply to Project Enquiries ===");
        List<Project> myProjects = getMyProjects();
        
        if (myProjects.isEmpty()) {
            System.out.println("You have no projects to manage enquiries for.");
            return;
        }

        // Display projects
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

            Project project = myProjects.get(choice - 1);
            System.out.println("\nFeature to be implemented: Reply to enquiries for " + project.getProjectName());
            // Will implement:
            // 1. Load enquiries for selected project
            // 2. Display unanswered enquiries
            // 3. Allow manager to select and reply to enquiries

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
}
