package menu;

import auth.User;
import auth.AuthenticationSystem;
import manager.ProjectManager;
import model.Project;
import utils.FileUtils;
import java.util.*;
import java.time.LocalDateTime;

public class OfficerMenu {
    private Scanner scanner;
    private User user;

    private AuthenticationSystem authSystem;
    private ProjectManager projectManager;

    public OfficerMenu(User user) {
        this.scanner = new Scanner(System.in);
        this.user = user;
        this.authSystem = new AuthenticationSystem();
        this.projectManager = new ProjectManager();
    }

    public void display() {
        while (true) {
            System.out.println("\n=== HDB Officer Menu ===");
            System.out.println("Welcome, " + user.getName());
            System.out.println("=== Project Management ===");
            System.out.println("1. Register for Project");
            System.out.println("2. View Registration Status");
            System.out.println("3. View Project Details");
            System.out.println("4. Process Flat Selection");
            System.out.println("5. Generate Booking Receipt");
            
            System.out.println("\n=== Enquiries Management ===");
            System.out.println("6. View Project Enquiries");
            System.out.println("7. Reply to Enquiries");
            
            System.out.println("\n=== Applicant Features ===");
            System.out.println("8. Browse Projects");
            System.out.println("9. Submit Application");
            System.out.println("10. View Application Status");
            System.out.println("11. Submit/View Enquiries");
            System.out.println("12. Request Withdrawal");
            
            System.out.println("\n=== System ===");
            System.out.println("13. Change Password");
            System.out.println("14. Logout");
            
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        // Check if already registered for another active project
                        List<Project> assignedProjects = projectManager.getVisibleProjects(user);
                        boolean isAssigned = false;
                        for (Project project : assignedProjects) {
                            if (project.getAssignedOfficers().contains(user.getNric()) &&
                                project.isApplicationOpen()) {
                                isAssigned = true;
                                break;
                            }
                        }
                        if (isAssigned) {
                            System.out.println("You are already registered to handle another project within an application period.");
                            break;
                        }
                        registerForProject();
                        break;
                    case 2:
                        viewRegistrationStatus();
                        break;
                    case 3:
                        viewProjectDetails();
                        break;
                    case 4:
                        processFlatSelection();
                        break;
                    case 5:
                        System.out.println("\nFeature to be implemented: Generate Booking Receipt");
                        // Will generate receipt for successful bookings
                        break;
                    case 6:
                        viewProjectEnquiries();
                        break;
                    case 7:
                        replyToEnquiries();
                        break;
                    case 8:
                        System.out.println("\nFeature to be implemented: Browse Projects");
                        // Inherited from Applicant - Browse other projects
                        break;
                    case 9:
                        // Check if trying to apply for a project they're handling
                        List<Project> allProjects = projectManager.getAllProjects();
                        List<Project> availableProjects = new ArrayList<>();
                        
                        // Filter for visible and open projects
                        for (Project p : allProjects) {
                            if (p.isVisible() && p.isApplicationOpen()) {
                                availableProjects.add(p);
                            }
                        }
                        List<Project> applicableProjects = new ArrayList<>();
                        
                        for (Project project : availableProjects) {
                            if (!project.getAssignedOfficers().contains(user.getNric())) {
                                applicableProjects.add(project);
                            }
                        }

                        if (applicableProjects.isEmpty()) {
                            System.out.println("No projects available for application.");
                            break;
                        }

                        System.out.println("\n=== Available Projects for Application ===");
                        for (int i = 0; i < applicableProjects.size(); i++) {
                            Project project = applicableProjects.get(i);
                            System.out.printf("%d. %s (%s)%n", 
                                i + 1, project.getProjectName(), project.getNeighborhood());
                        }
                        System.out.println("\nNote: Projects you handle as an officer are excluded.");
                        System.out.println("Feature to be implemented: Submit Application");
                        break;
                    case 10:
                        System.out.println("\nFeature to be implemented: View Application Status");
                        // Inherited from Applicant - View own applications
                        break;
                    case 11:
                        manageEnquiries();
                        break;
                    case 12:
                        System.out.println("\nFeature to be implemented: Request Withdrawal");
                        // Inherited from Applicant - Request withdrawal for own applications
                        break;
                    case 13:
                        System.out.println("\nFeature to be implemented: Change Password");
                        // Will handle password changes
                        break;
                    case 14:
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

    private void processFlatSelection() {
        while (true) {
            System.out.println("\n=== Process Flat Selection ===");
            System.out.println("1. Update Available Units");
            System.out.println("2. Retrieve Applicant Details");
            System.out.println("3. Update Application Status");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        System.out.println("\nFeature to be implemented: Update Available Units");
                        // Will handle updating remaining units
                        break;
                    case 2:
                        System.out.println("\nFeature to be implemented: Retrieve Applicant Details");
                        // Will show applicant details by NRIC
                        break;
                    case 3:
                        System.out.println("\nFeature to be implemented: Update Application Status");
                        // Will update status from successful to booked
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void registerForProject() {
        System.out.println("\n=== Register for Project ===");
        List<Project> availableProjects = projectManager.getAllProjects();
        List<Project> eligibleProjects = new ArrayList<>();

        // Filter projects that are open and have available officer slots
        for (Project project : availableProjects) {
            if (project.isApplicationOpen() && 
                project.getAssignedOfficers().size() < project.getOfficerSlots()) {
                eligibleProjects.add(project);
            }
        }

        if (eligibleProjects.isEmpty()) {
            System.out.println("No projects available for registration.");
            return;
        }

        // Display eligible projects
        System.out.println("Available Projects:");
        for (int i = 0; i < eligibleProjects.size(); i++) {
            Project project = eligibleProjects.get(i);
            System.out.printf("%d. %s (%s)%n", 
                i + 1, project.getProjectName(), project.getNeighborhood());
            System.out.printf("   Officer slots: %d/%d%n", 
                project.getAssignedOfficers().size(), project.getOfficerSlots());
        }

        // Get project selection
        System.out.print("\nSelect project number (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return;
            if (choice < 1 || choice > eligibleProjects.size()) {
                System.out.println("Invalid project selection.");
                return;
            }

            Project selectedProject = eligibleProjects.get(choice - 1);

            // Check if officer has applied for this project as an applicant
            List<String[]> applications = FileUtils.readFile("ApplicationList.txt");
            for (int i = 1; i < applications.size(); i++) {
                String[] app = applications.get(i);
                if (app[1].equals(user.getNric()) && app[2].equals(selectedProject.getProjectName())) {
                    System.out.println("You cannot register as an officer for a project you have applied for.");
                    return;
                }
            }

            // Create registration record
            List<String[]> registrations = FileUtils.readFile("OfficerRegistrations.txt");
            String registrationId = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + generateRandomString(4);

            registrations.add(new String[]{
                registrationId,
                user.getNric(),
                selectedProject.getProjectName(),
                "PENDING",
                LocalDateTime.now().toString()
            });

            if (FileUtils.writeFile("OfficerRegistrations.txt", registrations)) {
                System.out.println("Registration submitted successfully. Pending manager approval.");
            } else {
                System.out.println("Failed to submit registration. Please try again.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void viewRegistrationStatus() {
        System.out.println("\n=== View Registration Status ===");
        List<String[]> registrations = FileUtils.readFile("OfficerRegistrations.txt");
        boolean found = false;

        if (registrations.size() > 1) {
            System.out.println("\nYour Registration Requests:");
            for (int i = 1; i < registrations.size(); i++) {
                String[] reg = registrations.get(i);
                if (reg[1].equals(user.getNric())) {
                    found = true;
                    System.out.println("\nRegistration ID: " + reg[0]);
                    System.out.println("Project: " + reg[2]);
                    System.out.println("Status: " + reg[3]);
                    System.out.println("Registration Date: " + reg[4]);
                }
            }
        }

        if (!found) {
            System.out.println("No registration requests found.");
        }
        
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewProjectDetails() {
        System.out.println("\n=== View Project Details ===");
        List<Project> assignedProjects = projectManager.getVisibleProjects(user);
        List<Project> activeProjects = new ArrayList<>();

        // Filter projects where officer is assigned
        for (Project project : assignedProjects) {
            if (project.getAssignedOfficers().contains(user.getNric())) {
                activeProjects.add(project);
            }
        }

        if (activeProjects.isEmpty()) {
            System.out.println("You are not assigned to any projects.");
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Display project list
        System.out.println("\nYour Assigned Projects:");
        for (int i = 0; i < activeProjects.size(); i++) {
            Project project = activeProjects.get(i);
            System.out.printf("%d. %s (%s)%n", 
                i + 1, project.getProjectName(), project.getNeighborhood());
        }

        // Get project selection
        System.out.print("\nSelect project number (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return;
            if (choice < 1 || choice > activeProjects.size()) {
                System.out.println("Invalid project selection.");
                return;
            }

            Project selectedProject = activeProjects.get(choice - 1);
            
            // Display detailed project information
            System.out.println("\nProject Details:");
            System.out.println(selectedProject.toString());
            
            // Display additional officer-specific information
            System.out.println("\nOfficer Information:");
            System.out.println("Total Officer Slots: " + selectedProject.getOfficerSlots());
            System.out.println("Current Officers: " + selectedProject.getAssignedOfficers().size());
            System.out.println("Officer List: " + String.join(", ", selectedProject.getAssignedOfficers()));
            
            // Display application period status
            boolean isOpen = selectedProject.isApplicationOpen();
            System.out.println("\nApplication Period Status: " + (isOpen ? "OPEN" : "CLOSED"));
            System.out.println("Opening Date: " + selectedProject.getOpeningDate());
            System.out.println("Closing Date: " + selectedProject.getClosingDate());

            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void viewProjectEnquiries() {
        System.out.println("\n=== View Project Enquiries ===");
        List<Project> assignedProjects = projectManager.getVisibleProjects(user);
        List<Project> activeProjects = new ArrayList<>();

        // Filter projects where officer is assigned
        for (Project project : assignedProjects) {
            if (project.getAssignedOfficers().contains(user.getNric())) {
                activeProjects.add(project);
            }
        }

        if (activeProjects.isEmpty()) {
            System.out.println("You are not assigned to any projects.");
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Display project list
        System.out.println("\nSelect Project to View Enquiries:");
        for (int i = 0; i < activeProjects.size(); i++) {
            Project project = activeProjects.get(i);
            System.out.printf("%d. %s%n", i + 1, project.getProjectName());
        }

        try {
            System.out.print("\nEnter project number (0 to cancel): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 0) return;
            if (choice < 1 || choice > activeProjects.size()) {
                System.out.println("Invalid project selection.");
                return;
            }

            Project selectedProject = activeProjects.get(choice - 1);
            List<String[]> enquiries = FileUtils.readFile("EnquiryList.txt");
            boolean found = false;

            System.out.println("\nEnquiries for " + selectedProject.getProjectName() + ":");
            for (int i = 1; i < enquiries.size(); i++) {
                String[] enq = enquiries.get(i);
                if (enq[2].equals(selectedProject.getProjectName())) {
                    found = true;
                    System.out.println("\nEnquiry ID: " + enq[0]);
                    System.out.println("From: " + enq[1]);
                    System.out.println("Question: " + enq[3]);
                    System.out.println("Response: " + (enq[4].isEmpty() ? "No response yet" : enq[4]));
                    System.out.println("Date: " + enq[5]);
                }
            }

            if (!found) {
                System.out.println("No enquiries found for this project.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void replyToEnquiries() {
        System.out.println("\n=== Reply to Project Enquiries ===");
        List<Project> assignedProjects = projectManager.getVisibleProjects(user);
        List<Project> activeProjects = new ArrayList<>();

        // Filter projects where officer is assigned
        for (Project project : assignedProjects) {
            if (project.getAssignedOfficers().contains(user.getNric())) {
                activeProjects.add(project);
            }
        }

        if (activeProjects.isEmpty()) {
            System.out.println("You are not assigned to any projects.");
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Display project list
        System.out.println("\nSelect Project:");
        for (int i = 0; i < activeProjects.size(); i++) {
            Project project = activeProjects.get(i);
            System.out.printf("%d. %s%n", i + 1, project.getProjectName());
        }

        try {
            System.out.print("\nEnter project number (0 to cancel): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 0) return;
            if (choice < 1 || choice > activeProjects.size()) {
                System.out.println("Invalid project selection.");
                return;
            }

            Project selectedProject = activeProjects.get(choice - 1);
            List<String[]> enquiries = FileUtils.readFile("EnquiryList.txt");
            List<Integer> pendingEnquiries = new ArrayList<>();

            // Display pending enquiries
            System.out.println("\nPending Enquiries for " + selectedProject.getProjectName() + ":");
            for (int i = 1; i < enquiries.size(); i++) {
                String[] enq = enquiries.get(i);
                if (enq[2].equals(selectedProject.getProjectName()) && enq[4].isEmpty()) {
                    pendingEnquiries.add(i);
                    System.out.printf("\n%d. From: %s%n", pendingEnquiries.size(), enq[1]);
                    System.out.println("Question: " + enq[3]);
                    System.out.println("Date: " + enq[5]);
                }
            }

            if (pendingEnquiries.isEmpty()) {
                System.out.println("No pending enquiries found for this project.");
                System.out.print("\nPress Enter to continue...");
                scanner.nextLine();
                return;
            }

            System.out.print("\nSelect enquiry to reply (0 to cancel): ");
            int enquiryChoice = Integer.parseInt(scanner.nextLine().trim());
            
            if (enquiryChoice == 0) return;
            if (enquiryChoice < 1 || enquiryChoice > pendingEnquiries.size()) {
                System.out.println("Invalid enquiry selection.");
                return;
            }

            System.out.println("\nEnter your response:");
            String response = scanner.nextLine().trim();
            
            if (response.isEmpty()) {
                System.out.println("Response cannot be empty.");
                return;
            }

            // Update the enquiry
            int selectedIndex = pendingEnquiries.get(enquiryChoice - 1);
            enquiries.get(selectedIndex)[4] = response;
            enquiries.get(selectedIndex)[6] = user.getNric();
            enquiries.get(selectedIndex)[7] = LocalDateTime.now().toString();

            if (FileUtils.writeFile("EnquiryList.txt", enquiries)) {
                System.out.println("Response submitted successfully.");
            } else {
                System.out.println("Failed to submit response. Please try again.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void manageEnquiries() {
        while (true) {
            System.out.println("\n=== Personal Enquiries Management ===");
            System.out.println("1. Submit New Enquiry");
            System.out.println("2. View My Enquiries");
            System.out.println("3. Edit Enquiry");
            System.out.println("4. Delete Enquiry");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        System.out.println("\nFeature to be implemented: Submit Enquiry");
                        break;
                    case 2:
                        System.out.println("\nFeature to be implemented: View Enquiries");
                        break;
                    case 3:
                        System.out.println("\nFeature to be implemented: Edit Enquiry");
                        break;
                    case 4:
                        System.out.println("\nFeature to be implemented: Delete Enquiry");
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
}
