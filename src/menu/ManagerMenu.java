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
            
            System.out.println("\n=== Application Management ===");
            System.out.println("8. Process BTO Applications");
            System.out.println("9. Process Withdrawal Requests");
            
            System.out.println("\n=== Reports & Enquiries ===");
            System.out.println("10. Generate Reports");
            System.out.println("11. View All Project Enquiries");
            System.out.println("12. View and Reply My Project Enquiries");
            
            System.out.println("\n=== System ===");
            System.out.println("13. Change Password");
            System.out.println("14. Logout");
            printDivider();
            
            int choice = readChoice("Enter your choice: ", 1, 14);
            
            switch (choice) {
                case 1: createProject(); break;
                case 2: editProject(); break;
                case 3: deleteProject(); break;
                case 4: viewAllProjects(); break;
                case 5: viewMyProjects(); break;
                case 6: toggleProjectVisibility(); break;
                case 7: viewOfficerRegistrations(); break;
                case 8: processBTOApplications(); break;
                case 9: processWithdrawalRequests(); break;
                case 10: generateReports(); break;
                case 11: viewAllEnquiries(); break;
                case 12: replyToEnquiries(); break;
                case 13: changePassword(); break;
                case 14:
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
    
    /**
     * Reads an integer with validation and retry logic
     * @param prompt The prompt to display to the user
     * @param errorMessage The error message to display if input is invalid
     * @return The integer input or -1 if user wants to quit
     */
    private int readIntWithRetry(String prompt, String errorMessage) {
        while (true) {
            String input = readString(prompt);
            if (input.equalsIgnoreCase("quit")) {
                return -1;
            }
            
            try {
                int value = Integer.parseInt(input);
                if (value < 0) {
                    printError("Please enter a non-negative number.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                printError(errorMessage);
            }
        }
    }
    
    /**
     * Reads a double with validation and retry logic
     * @param prompt The prompt to display to the user
     * @param errorMessage The error message to display if input is invalid
     * @return The double input or -1 if user wants to quit
     */
    private double readDoubleWithRetry(String prompt, String errorMessage) {
        while (true) {
            String input = readString(prompt);
            if (input.equalsIgnoreCase("quit")) {
                return -1;
            }
            
            try {
                double value = Double.parseDouble(input);
                if (value < 0) {
                    printError("Please enter a non-negative number.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                printError(errorMessage);
            }
        }
    }
    
    /**
     * Reads an integer with validation for min and max values and retry logic
     * @param prompt The prompt to display to the user
     * @param errorMessage The error message to display if input is invalid format
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @param rangeErrorMessage The error message for out of range values
     * @return The integer input or -1 if user wants to quit
     */
    private int readIntWithValidationAndRetry(String prompt, String errorMessage, 
                                            int min, int max, String rangeErrorMessage) {
        while (true) {
            String input = readString(prompt);
            if (input.equalsIgnoreCase("quit")) {
                return -1;
            }
            
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    printError(rangeErrorMessage);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                printError(errorMessage);
            }
        }
    }
    
    /**
     * Helper method to convert a string to camel case
     * @param input The input string
     * @return The camel case version of the input
     */
    private String toCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        
        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    // --- Project Management Methods ---
    private void createProject() {
        printHeader("Create New Project");
        
        try {
            // Show quit instruction only once at the top
            System.out.println("You can type 'quit' at any prompt to cancel the project creation.\n");
            
            // Project Name - validate and re-prompt if empty
            String projectName = "";
            while (projectName.trim().isEmpty()) {
                projectName = readString("Enter Project Name: ");
                if (projectName.equalsIgnoreCase("quit")) {
                    printMessage("Project creation cancelled.");
                    return;
                }
                
                if (projectName.trim().isEmpty()) {
                    printError("Project Name cannot be empty.");
                    // Continue the loop to prompt again
                } else {
                    // Check if this project name already exists
                    List<Project> allProjects = projectFacade.getAllProjects();
                    boolean nameExists = false;
                    for (Project existingProject : allProjects) {
                        if (existingProject.getProjectName().equalsIgnoreCase(projectName)) {
                            printError("A project with this name already exists. Project names must be unique.");
                            nameExists = true;
                            projectName = ""; // Reset to continue the loop
                            break;
                        }
                    }
                    
                    if (!nameExists) {
                        // Valid project name, exit the loop
                        break;
                    }
                }
            }
            
            // Neighborhood - validate and re-prompt if empty
            String neighborhood = "";
            while (neighborhood.trim().isEmpty()) {
                neighborhood = readString("Enter Neighborhood: ");
                if (neighborhood.equalsIgnoreCase("quit")) {
                    printMessage("Project creation cancelled.");
                    return;
                }
                
                if (neighborhood.trim().isEmpty()) {
                    printError("Neighborhood cannot be empty.");
                    // Continue the loop to prompt again
                }
            }
            
            // Convert Project Name and Neighborhood to camel case
            projectName = toCamelCase(projectName);
            neighborhood = toCamelCase(neighborhood);
            
            // Number of 2-Room units - validate and re-prompt if invalid
            int twoRoomUnits = -1;
            while (twoRoomUnits < 0) {
                String input = readString("Enter number of 2-Room units: ");
                if (input.equalsIgnoreCase("quit")) {
                    printMessage("Project creation cancelled.");
                    return;
                }
                
                try {
                    twoRoomUnits = Integer.parseInt(input);
                    if (twoRoomUnits < 0) {
                        printError("Number of units cannot be negative.");
                        twoRoomUnits = -1; // Reset to continue the loop
                    }
                } catch (NumberFormatException e) {
                    printError("Please enter a valid number for 2-Room units.");
                }
            }
            
            // Price for 2-Room units - validate and re-prompt if invalid
            double twoRoomPrice = -1;
            while (twoRoomPrice < 0) {
                String input = readString("Enter price for 2-Room units: ");
                if (input.equalsIgnoreCase("quit")) {
                    printMessage("Project creation cancelled.");
                    return;
                }
                
                try {
                    twoRoomPrice = Double.parseDouble(input);
                    if (twoRoomPrice < 0) {
                        printError("Price cannot be negative.");
                        twoRoomPrice = -1; // Reset to continue the loop
                    }
                } catch (NumberFormatException e) {
                    printError("Please enter a valid price for 2-Room units.");
                }
            }
            
            // Number of 3-Room units - validate and re-prompt if invalid
            int threeRoomUnits = -1;
            while (threeRoomUnits < 0) {
                String input = readString("Enter number of 3-Room units: ");
                if (input.equalsIgnoreCase("quit")) {
                    printMessage("Project creation cancelled.");
                    return;
                }
                
                try {
                    threeRoomUnits = Integer.parseInt(input);
                    if (threeRoomUnits < 0) {
                        printError("Number of units cannot be negative.");
                        threeRoomUnits = -1; // Reset to continue the loop
                    }
                } catch (NumberFormatException e) {
                    printError("Please enter a valid number for 3-Room units.");
                }
            }
            
            // Price for 3-Room units - validate and re-prompt if invalid
            double threeRoomPrice = -1;
            while (threeRoomPrice < 0) {
                String input = readString("Enter price for 3-Room units: ");
                if (input.equalsIgnoreCase("quit")) {
                    printMessage("Project creation cancelled.");
                    return;
                }
                
                try {
                    threeRoomPrice = Double.parseDouble(input);
                    if (threeRoomPrice < 0) {
                        printError("Price cannot be negative.");
                        threeRoomPrice = -1; // Reset to continue the loop
                    }
                } catch (NumberFormatException e) {
                    printError("Please enter a valid price for 3-Room units.");
                }
            }
            
            // Parse and validate opening date
            LocalDate openingDate = null;
            while (openingDate == null) {
                String openingDateInput = readString("Enter application opening date (" + Constants.DATE_FORMAT + "): ");
                if (openingDateInput.equalsIgnoreCase("quit")) {
                    printMessage("Project creation cancelled.");
                    return;
                }
                
                try {
                    LocalDate currentDate = LocalDate.now();
                    openingDate = LocalDate.parse(openingDateInput, DATE_FORMATTER);
                    if (openingDate.isBefore(currentDate)) {
                        printError("Opening date cannot be in the past.");
                        openingDate = null; // Reset to continue the loop
                    }
                } catch (Exception e) {
                    printError("Invalid opening date format. Please use format: " + Constants.DATE_FORMAT);
                }
            }
            
            // Parse and validate closing date
            LocalDate closingDate = null;
            while (closingDate == null) {
                String closingDateInput = readString("Enter application closing date (" + Constants.DATE_FORMAT + "): ");
                if (closingDateInput.equalsIgnoreCase("quit")) {
                    printMessage("Project creation cancelled.");
                    return;
                }
                
                try {
                    closingDate = LocalDate.parse(closingDateInput, DATE_FORMATTER);
                    if (closingDate.isBefore(openingDate)) {
                        printError("Closing date cannot be before opening date.");
                        closingDate = null; // Reset to continue the loop
                    }
                } catch (Exception e) {
                    printError("Invalid closing date format. Please use format: " + Constants.DATE_FORMAT);
                }
            }
            
            // Check if the manager already has a project that overlaps with this application period
            boolean hasOverlap = false;
            List<Project> myProjects = projectFacade.getProjectsByManager(projectManager.getNric());
            for (Project existingProject : myProjects) {
                // Check if application periods overlap
                LocalDate existingOpeningDate = existingProject.getApplicationOpeningDate();
                LocalDate existingClosingDate = existingProject.getApplicationClosingDate();
                
                // If the new project's period overlaps with an existing project's period
                if (!(closingDate.isBefore(existingOpeningDate) || openingDate.isAfter(existingClosingDate))) {
                    printError("You cannot create a new project that overlaps with an existing project's application period.");
                    System.out.println("Project '" + existingProject.getProjectName() + "' has application period: " + 
                                      existingOpeningDate + " to " + existingClosingDate);
                    System.out.println("You can only have one project active within each application period.");
                    hasOverlap = true;
                    break;
                }
            }
            
            if (hasOverlap) {
                // Ask if the user wants to restart the creation process
                if (readYesNo("Do you want to restart the project creation process? (Y/N): ")) {
                    createProject(); // Restart project creation
                } else {
                    printMessage("Project creation cancelled.");
                }
                return;
            }
            
            // Officer slots - validate and re-prompt if invalid
            int officerSlots = -1;
            while (officerSlots < 1 || officerSlots > 10) {
                String input = readString("Enter number of HDB Officer slots (max 10): ");
                if (input.equalsIgnoreCase("quit")) {
                    printMessage("Project creation cancelled.");
                    return;
                }
                
                try {
                    officerSlots = Integer.parseInt(input);
                    if (officerSlots < 1 || officerSlots > 10) {
                        printError("Officer slots must be between 1 and 10.");
                        // Continue the loop
                    }
                } catch (NumberFormatException e) {
                    printError("Please enter a valid number for officer slots.");
                }
            }
            
            // Create the project
            Project project = new Project(projectName, neighborhood, openingDate, closingDate, projectManager.getNric(), officerSlots);
            project.addUnitType("2-Room", twoRoomUnits, twoRoomPrice);
            project.addUnitType("3-Room", threeRoomUnits, threeRoomPrice);
            
            // Add to project list and save changes
            projectFacade.addProject(project);
            
            // Save changes to file using our helper method
            saveProjectChanges();
            
            printSuccess("Project created successfully!");
        } catch (Exception e) {
            printError("Error creating project: " + e.getMessage());
        }
    }
    
    private boolean isHandlingActiveProject() {
        List<Project> projects = projectFacade.getProjectsByManager(projectManager.getNric());
        LocalDate now = LocalDate.now();
        for (Project project : projects) {
            if (!now.isBefore(project.getApplicationOpeningDate()) &&
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
            System.out.println("3. Back");
            try {
                int choice = readChoice("Enter your choice: ", 1, 3);
                switch (choice) {
                    case 1:
                        LocalDate newOpening = LocalDate.parse(readString("Enter new opening date (" + Constants.DATE_FORMAT + "): "), DATE_FORMATTER);
                        LocalDate newClosing = LocalDate.parse(readString("Enter new closing date (" + Constants.DATE_FORMAT + "): "), DATE_FORMATTER);
                        
                        if (newClosing.isBefore(newOpening)) {
                            printError("Closing date cannot be before opening date.");
                            break;
                        }
                        
                        project.setOpeningDate(newOpening);
                        project.setClosingDate(newClosing);
                        
                        // Save changes immediately
                        projectFacade.updateProject(project);
                        saveProjectChanges();
                        printSuccess("Application period updated successfully!");
                        break;
                        
                    case 2:
                        int slots = Integer.parseInt(readString("Enter new number of officer slots (max 10): "));
                        
                        if (slots <= 0 || slots > 10) {
                            printError("Officer slots must be between 1 and 10.");
                            break;
                        }
                        
                        project.setOfficerSlots(slots);
                        
                        // Save changes immediately
                        projectFacade.updateProject(project);
                        saveProjectChanges();
                        printSuccess("Officer slots updated successfully!");
                        break;
                        
                    case 3:
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
                saveProjectChanges();
                printSuccess("Project deleted successfully!");
            }
        } catch (NumberFormatException e) {
            printError("Please enter a valid number.");
        }
    }
    
    private void viewAllProjects() {
        while (true) {
            printHeader("All Projects");
            List<Project> allProjects = projectFacade.getAllProjects();
            
            // Add filtering options
            System.out.println("Filter Options:");
            System.out.println("1. View all projects");
            System.out.println("2. Filter by neighborhood");
            System.out.println("3. Filter by application period");
            System.out.println("4. Filter by visibility");
            
            int filterChoice = readChoice("Select filter option: ", 1, 4);
            List<Project> filteredProjects = new ArrayList<>(allProjects);
            
            switch (filterChoice) {
                case 1:
                    // No filtering needed
                    break;
                case 2:
                    String neighborhood = readString("Enter neighborhood to filter by: ");
                    filteredProjects = allProjects.stream()
                        .filter(p -> p.getNeighborhood().toLowerCase().contains(neighborhood.toLowerCase()))
                        .collect(java.util.stream.Collectors.toList());
                    break;
                case 3:
                    try {
                        LocalDate startDate = LocalDate.parse(readString("Enter start date (" + Constants.DATE_FORMAT + "): "), DATE_FORMATTER);
                        LocalDate endDate = LocalDate.parse(readString("Enter end date (" + Constants.DATE_FORMAT + "): "), DATE_FORMATTER);
                        
                        filteredProjects = allProjects.stream()
                            .filter(p -> {
                                // Check if project's application period overlaps with specified date range
                                return !(p.getApplicationClosingDate().isBefore(startDate) || 
                                        p.getApplicationOpeningDate().isAfter(endDate));
                            })
                            .collect(java.util.stream.Collectors.toList());
                    } catch (Exception e) {
                        printError("Invalid date format. Using all projects instead.");
                    }
                    break;
                case 4:
                    boolean visibilityChoice = readYesNo("Filter for visible projects? (Y/N): ");
                    filteredProjects = allProjects.stream()
                        .filter(p -> p.isVisible() == visibilityChoice)
                        .collect(java.util.stream.Collectors.toList());
                    break;
            }
            
            if (filteredProjects.isEmpty()) {
                printMessage("No projects found with the specified filter criteria.");
            } else {
                // Display projects in a tabular format without allowing selection
                System.out.printf("%-4s %-25s %-15s %-15s %-15s %-10s %-10s%n", 
                                 "No.", "Project Name", "Neighborhood", "Opening Date", "Closing Date", "Manager", "Visibility");
                printDivider();
                
                int i = 1;
                for (Project project : filteredProjects) {
                    System.out.printf("%-4d %-25s %-15s %-15s %-15s %-10s %-10s%n", 
                                    i++, 
                                    truncate(project.getProjectName(), 25),
                                    truncate(project.getNeighborhood(), 15),
                                    project.getApplicationOpeningDate(),
                                    project.getApplicationClosingDate(),
                                    truncate(project.getManager(), 10),
                                    project.isVisible() ? "Visible" : "Hidden");
                }
            }
            
            // Always show the options to continue regardless of whether projects were found
            System.out.println("\n1. Apply different filter");
            System.out.println("2. Return to main menu");
            
            int navChoice = readChoice("Select option: ", 1, 2);
            if (navChoice == 2) {
                // User wants to return to main menu
                return;
            }
            // Otherwise continue the loop to apply a different filter
        }
    }
    
    private void viewMyProjects() {
        printHeader("MY PROJECTS");
        List<Project> myProjects = getMyProjects();
        
        if (myProjects.isEmpty()) {
            printMessage("You are not currently managing any projects.");
            return;
        }
        
        // Add filtering options
        System.out.println("Filter Options:");
        System.out.println("1. View all my projects");
        System.out.println("2. Filter by neighborhood");
        System.out.println("3. Filter by application period");
        System.out.println("4. Filter by visibility");
        
        int filterChoice = readChoice("Select filter option: ", 1, 4);
        List<Project> filteredProjects = new ArrayList<>(myProjects);
        
        switch (filterChoice) {
            case 1:
                // No filtering needed
                break;
            case 2:
                String neighborhood = readString("Enter neighborhood to filter by: ");
                filteredProjects = myProjects.stream()
                    .filter(p -> p.getNeighborhood().toLowerCase().contains(neighborhood.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
                break;
            case 3:
                try {
                    LocalDate startDate = LocalDate.parse(readString("Enter start date (" + Constants.DATE_FORMAT + "): "), DATE_FORMATTER);
                    LocalDate endDate = LocalDate.parse(readString("Enter end date (" + Constants.DATE_FORMAT + "): "), DATE_FORMATTER);
                    
                    filteredProjects = myProjects.stream()
                        .filter(p -> {
                            // Check if project's application period overlaps with specified date range
                            return !(p.getApplicationClosingDate().isBefore(startDate) || 
                                    p.getApplicationOpeningDate().isAfter(endDate));
                        })
                        .collect(java.util.stream.Collectors.toList());
                } catch (Exception e) {
                    printError("Invalid date format. Using all projects instead.");
                }
                break;
            case 4:
                boolean visibilityChoice = readYesNo("Filter for visible projects? (Y/N): ");
                filteredProjects = myProjects.stream()
                    .filter(p -> p.isVisible() == visibilityChoice)
                    .collect(java.util.stream.Collectors.toList());
                break;
        }
        
        if (filteredProjects.isEmpty()) {
            printMessage("No projects found with the specified filter criteria.");
            return;
        }
        
        // Display projects in a tabular format
        System.out.printf("%-4s %-25s %-15s %-15s %-10s %-15s\n", 
                          "No.", "Project Name", "Neighborhood", "Application Period", "Officers", "Visibility");
        printDivider();
        
        int i = 1;
        for (Project project : filteredProjects) {
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
        int choice = readChoice("", 0, filteredProjects.size());
        if (choice == 0) return;
        
        Project selectedProject = filteredProjects.get(choice - 1);
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
            else if (status.equalsIgnoreCase("SUCCESSFUL")) approvedApps++;
            else if (status.equalsIgnoreCase("UNSUCCESSFUL")) rejectedApps++;
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
            saveProjectChanges();
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
        
        System.out.printf("%-4s %-25s %-15s %-15s %-15s %-10s %-10s%n", 
                         "No.", "Project Name", "Neighborhood", "Opening Date", "Closing Date", "Manager", "Visibility");
        printDivider();
        
        int i = 1;
        for (Project project : projects) {
            System.out.printf("%-4d %-25s %-15s %-15s %-15s %-10s %-10s%n", 
                            i++, 
                            truncate(project.getProjectName(), 25),
                            truncate(project.getNeighborhood(), 15),
                            project.getApplicationOpeningDate(),
                            project.getApplicationClosingDate(),
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
        while (true) {
            printHeader("View Officer Registrations");
            List<OfficerRegistration> regs = officerRegFacade.getAllOfficerRegistrations();
            
            // Filter for registrations for this manager's projects
            List<Project> myProjects = getMyProjects();
            List<String> myProjectNames = new ArrayList<>();
            for (Project project : myProjects) {
                myProjectNames.add(project.getProjectName());
            }
            
            List<OfficerRegistration> myRegs = new ArrayList<>();
            for (OfficerRegistration reg : regs) {
                if (myProjectNames.contains(reg.getProjectName())) {
                    myRegs.add(reg);
                }
            }
            
            if (myRegs.isEmpty()) {
                printError("No officer registrations found for your projects.");
                return;
            }
            
            // Display registrations in a table format
            System.out.printf("%-4s %-20s %-15s %-25s %-15s %-15s%n", 
                             "No.", "Registration ID", "Officer NRIC", "Project", "Status", "Date");
            printDivider();
            
            int i = 1;
            for (OfficerRegistration reg : myRegs) {
                System.out.printf("%-4d %-20s %-15s %-25s %-15s %-15s%n", 
                                i++, 
                                truncate(reg.getRegistrationId(), 20),
                                reg.getOfficerNric(),
                                truncate(reg.getProjectName(), 25),
                                reg.getStatus().toString(),
                                reg.getRegistrationDate().toLocalDate());
            }
            
            // User selection options
            System.out.println("\nSelect a registration to view details (0 to return): ");
            int choice = readChoice("", 0, myRegs.size());
            
            if (choice == 0) {
                return;
            }
            
            // View and potentially process the selected registration
            OfficerRegistration selectedReg = myRegs.get(choice - 1);
            processRegistrationDetails(selectedReg);
        }
    }
    
    private void processRegistrationDetails(OfficerRegistration registration) {
        while (true) {
            printHeader("Officer Registration Details");
            
            // Display detailed information
            System.out.println("Registration ID: " + registration.getRegistrationId());
            System.out.println("Officer NRIC: " + registration.getOfficerNric());
            System.out.println("Project: " + registration.getProjectName());
            System.out.println("Status: " + registration.getStatus());
            System.out.println("Registration Date: " + registration.getRegistrationDate().toLocalDate());
            
            // Get project details to show available slots
            Project project = projectFacade.getProject(registration.getProjectName());
            if (project == null) {
                printError("Error: Project not found.");
                return;
            }
            
            System.out.println("\nProject Officer Slots: " + project.getOfficers().size() + "/" + project.getOfficerSlot());
            
            // Only show process options if status is PENDING
            if (registration.getStatus().toString().equalsIgnoreCase("PENDING")) {
                printDivider();
                System.out.println("1. Approve Registration");
                System.out.println("2. Reject Registration");
                System.out.println("Enter 0 to Go Back");
                
                int choice = readChoice("Enter your choice: ", 0, 2);
                
                if (choice == 0) {
                    return;
                } else if (choice == 1) {
                    // Check if the project has available slots
                    if (project.getOfficers().size() >= project.getOfficerSlot()) {
                        printError("WARNING: This project has no available officer slots.");
                        if (!readYesNo("Do you still want to approve this registration? (Y/N): ")) {
                            continue;
                        }
                    }
                    
                    // Approve registration
                    officerRegFacade.approveRegistration(registration.getRegistrationId());
                    
                    // Save the registration changes to CSV
                    if (officerRegFacade instanceof access.officerregistration.OfficerRegistrationHandler) {
                        ((access.officerregistration.OfficerRegistrationHandler) officerRegFacade).saveChanges();
                    }
                    
                    // Update project with new officer
                    project.addOfficer(registration.getOfficerNric());
                    projectFacade.updateProject(project);
                    saveProjectChanges();
                    
                    printSuccess("Registration approved successfully.");
                    System.out.println("Enter 0 to Go Back");
                    readChoice("", 0, 0);
                    return;
                } else if (choice == 2) {
                    // Get reason for rejection (optional)
                    String reason = readString("Enter reason for rejection (optional): ");
                    
                    // Reject registration
                    officerRegFacade.rejectRegistration(registration.getRegistrationId());
                    
                    // Save the registration changes to CSV
                    if (officerRegFacade instanceof access.officerregistration.OfficerRegistrationHandler) {
                        ((access.officerregistration.OfficerRegistrationHandler) officerRegFacade).saveChanges();
                    }
                    
                    printSuccess("Registration rejected successfully.");
                    System.out.println("Enter 0 to Go Back");
                    readChoice("", 0, 0);
                    return;
                }
            } else {
                // If already processed, just show return option
                printDivider();
                System.out.println("Enter 0 to Go Back");
                readChoice("", 0, 0);
                return;
            }
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
                System.out.println("\nEnter 0 to go back: ");
                readChoice("", 0, 0);
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
            
            // Directly ask for applicant selection without showing menu options
            printDivider();
            int appChoice = readChoice("Select a applicant number to process or press 0 to quit: ", 0, pendingApplications.size());
            if (appChoice == 0) return;
            
            Application selectedApp = pendingApplications.get(appChoice - 1);
            processIndividualApplication(selectedApp, project);
        }
    }

    private void processIndividualApplication(Application application, Project project) {
        printHeader("APPLICATION DETAILS");
        
        // Find the applicant to show more details
        User applicant = findUserByNric(application.getApplicantNric());
        String unitType = application.getUnitType();
        int availableUnits = project.getAvailableUnits(unitType);
        
        System.out.println("Application ID: " + application.getApplicationId());
        System.out.println("Project: " + application.getProjectName());
        System.out.println("Applicant NRIC: " + application.getApplicantNric());
        
        if (applicant != null) {
            System.out.println("Applicant Name: " + applicant.getName());
            System.out.println("Applicant Age: " + applicant.getAge());
            System.out.println("Marital Status: " + applicant.getMaritalStatus());
        }
        
        System.out.println("Unit Type: " + unitType);
        System.out.println("Available Units: " + availableUnits);
        System.out.println("Application Date: " + application.getApplicationDate().toLocalDate());
        System.out.println("Current Status: " + application.getStatus());
        printDivider();
        
        // Check if application already has a final status
        String remarks = application.getRemarks();
        if (remarks != null && (remarks.startsWith("SUCCESSFUL") || remarks.startsWith("UNSUCCESSFUL"))) {
            System.out.println("This application has already been " + 
                              (remarks.startsWith("SUCCESSFUL") ? "successful" : "unsuccessful") + ".");
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        if (availableUnits <= 0) {
            System.out.println("WARNING: No available units of type " + unitType);
        }
        
        System.out.println("1. Mark application as successful");
        System.out.println("2. Mark application as unsuccessful");
        System.out.println("3. Back to application list");
        
        int choice = readChoice("Enter choice: ", 1, 3);
        if (choice == 3) return;
        
        if (choice == 1) {
            // Mark application as successful
            if (availableUnits <= 0) {
                if (!readYesNo("No available units. Are you sure you want to mark as successful? (Y/N): ")) {
                    return;
                }
            }
            
            // Create a special ApplicationStatus for SUCCESSFUL
            application.setStatus(ApplicationStatus.SUCCESSFUL);
            application.setRemarks("SUCCESSFUL: Pending unit assignment by officer");
            application.setApprovalDate(LocalDate.now());
            
            // Note: Unit assignment will now be done by the officer, not by the manager
            
            // Save the application changes
            if (appFacade instanceof access.application.ApplicationHandler) {
                ((access.application.ApplicationHandler) appFacade).updateApplication(application);
                ((access.application.ApplicationHandler) appFacade).saveChanges();
            }
            
            printSuccess("Application marked as successful. Unit will be assigned by an officer.");
        } else if (choice == 2) {
            // Mark application as unsuccessful
            String reason = readString("Enter reason for marking as unsuccessful: ");
            
            // Set to UNSUCCESSFUL internally but display as UNSUCCESSFUL
            application.setStatus(ApplicationStatus.UNSUCCESSFUL);
            application.setRemarks("UNSUCCESSFUL: " + reason);
            
            // Save the application changes
            if (appFacade instanceof access.application.ApplicationHandler) {
                ((access.application.ApplicationHandler) appFacade).updateApplication(application);
                ((access.application.ApplicationHandler) appFacade).saveChanges();
            }
            
            printSuccess("Application marked as unsuccessful.");
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
        
        // Display all pending withdrawal requests in a tabular format
        System.out.printf("%-4s %-15s %-15s %-15s %-20s%n", 
                         "No.", "Request ID", "Applicant", "Application ID", "Request Date");
        printDivider();
        
        int i = 1;
        for (WithdrawalRequest withdrawal : pendingWithdrawals) {
            System.out.printf("%-4d %-15s %-15s %-15s %-20s%n", 
                             i++, 
                             truncate(withdrawal.getRequestId(), 15),
                             withdrawal.getApplicantNric(),
                             truncate(withdrawal.getApplicationId(), 15),
                             withdrawal.getRequestDate().toLocalDate());
        }
        
        // Process a selected withdrawal request
        try {
            int choice = readChoice("Select a number to process or press 0 to quit: ", 0, pendingWithdrawals.size());
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
                
                // Update the application status based on its current state
                boolean wasSuccessful = application.getStatus() == ApplicationStatus.SUCCESSFUL || 
                                        application.getStatus() == ApplicationStatus.BOOKED;
                
                // Mark as WITHDRAWN in system and "Unsuccessful" in the remarks
                application.setStatus(ApplicationStatus.WITHDRAWN);
                application.setRemarks("Application unsuccessful - Withdrawal approved by manager: " + projectManager.getName());
                
                // If the application was previously successful/booked, return the unit to available pool
                if (wasSuccessful) {
                    String unitType = application.getUnitType();
                    project.incrementAvailableUnits(unitType);
                    projectFacade.updateProject(project);
                    saveProjectChanges(); // Save project changes
                }
                
                // Save withdrawal changes
                withdrawalFacade.approveWithdrawal(selectedWithdrawal.getRequestId());
                
                // Save the application status change properly
                if (appFacade instanceof access.application.ApplicationHandler) {
                    ((access.application.ApplicationHandler) appFacade).updateApplication(application);
                    ((access.application.ApplicationHandler) appFacade).saveChanges();
                }
                
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
                
                if (app.getStatus() == ApplicationStatus.SUCCESSFUL || app.getStatus() == ApplicationStatus.BOOKED) {
                    approvedApplications++;
                } else if (app.getStatus() == ApplicationStatus.PENDING) {
                    pendingApplications++;
                } else if (app.getStatus() == ApplicationStatus.UNSUCCESSFUL) {
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
            System.out.printf("%-15s %-15s %-15s %-10s %-15s %-10s %-15s%n",
                    "Applicant", "NRIC", "Project", "Flat Type", "Status", "Age", "Marital Status");
            System.out.println(FileUtils.repeatChar('=', 100));
            
            for (Application app : projectApplications) {
                User applicant = findUserByNric(app.getApplicantNric());
                if (applicant != null) {
                    System.out.printf("%-15s %-15s %-15s %-10s %-15s %-10d %-15s%n",
                            applicant.getName(),
                            app.getApplicantNric(),
                            truncate(selectedProject.getProjectName(), 15),
                            app.getUnitType(),
                            app.getStatus(),
                            applicant.getAge(),
                            applicant.getMaritalStatus()
                    );
                } else {
                    System.out.printf("%-15s %-15s %-15s %-10s %-15s %-10s %-15s%n",
                            "Unknown",
                            app.getApplicantNric(),
                            truncate(selectedProject.getProjectName(), 15),
                            app.getUnitType(),
                            app.getStatus(),
                            "N/A",
                            "N/A"
                    );
                }
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
            
            if (app.getStatus() == ApplicationStatus.SUCCESSFUL || app.getStatus() == ApplicationStatus.BOOKED) {
                approvedApplications++;
            } else if (app.getStatus() == ApplicationStatus.PENDING) {
                pendingApplications++;
            } else if (app.getStatus() == ApplicationStatus.UNSUCCESSFUL) {
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
        
        // Detailed list with all required information
        content.append("Detailed Application List:\n");
        content.append(String.format("%-15s %-15s %-15s %-10s %-15s %-10s %-15s%n",
                "Applicant", "NRIC", "Project", "Flat Type", "Status", "Age", "Marital Status"));
        content.append(FileUtils.repeatChar('=', 100)).append("\n");
        
        for (Application app : applications) {
            User applicant = findUserByNric(app.getApplicantNric());
            if (applicant != null) {
                content.append(String.format("%-15s %-15s %-15s %-10s %-15s %-10d %-15s%n",
                        applicant.getName(),
                        app.getApplicantNric(),
                        truncate(project.getProjectName(), 15),
                        app.getUnitType(),
                        app.getStatus(),
                        applicant.getAge(),
                        applicant.getMaritalStatus()
                ));
            } else {
                content.append(String.format("%-15s %-15s %-15s %-10s %-15s %-10s %-15s%n",
                        "Unknown",
                        app.getApplicantNric(),
                        truncate(project.getProjectName(), 15),
                        app.getUnitType(),
                        app.getStatus(),
                        "N/A",
                        "N/A"
                ));
            }
        }
        
        return content.toString();
    }
    
    // Helper method to save a report to a file
    private void saveReportToFile(String filename, String content) {
        try {
            // Create Reports directory if it doesn't exist
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("Reports"));
            
            String filePath = "Reports/" + filename;
            
            // Java 8 compatible way to write string to file
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            java.nio.file.Files.write(path, content.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            
            printSuccess("Report saved to " + filePath);
        } catch (Exception e) {
            printError("Error saving report: " + e.getMessage());
        }
    }
    
    private void viewAllEnquiries() {
        while (true) {
            printHeader("VIEW ALL PROJECT ENQUIRIES");
            List<Enquiry> enquiries = enquiryFacade.getAllEnquiries();
            
            if (enquiries.isEmpty()) {
                printMessage("No enquiries found in the system.");
                System.out.println("\nEnter 0 to return to main menu: ");
                readChoice("", 0, 0);
                return;
            }
            
            // Display enquiries in a tabular format with adjusted column order
            System.out.printf("%-4s %-20s %-15s %-20s %-15s %-20s %-15s%n", 
                            "No.", "Enquiry ID", "Applicant", "Project", "Status", "Message", "Date");
            printDivider();
            
            int i = 1;
            for (Enquiry enquiry : enquiries) {
                // Determine status based on whether there's a reply
                String status = (enquiry.getReply() == null || enquiry.getReply().isEmpty() || 
                                 enquiry.getReply().equals("No reply yet")) ? "Pending Reply" : "Replied";
                
                System.out.printf("%-4d %-20s %-15s %-20s %-15s %-20s %-15s%n", 
                                i++, 
                                enquiry.getEnquiryId(),
                                truncate(enquiry.getApplicantNric(), 15),
                                truncate(enquiry.getProjectName(), 20),
                                status,
                                truncate(enquiry.getMessage(), 20),
                                enquiry.getSubmittedAt().toLocalDate());
            }
            
            System.out.println("\nEnter enquiry number to view details (0 to return to main menu): ");
            int choice = readChoice("", 0, enquiries.size());
            
            if (choice == 0) {
                return;
            }
            
            // View the selected enquiry - only viewing, not editing
            Enquiry selectedEnquiry = enquiries.get(choice - 1);
            viewEnquiryDetailsReadOnly(selectedEnquiry);
        }
    }

    private void viewEnquiryDetailsReadOnly(Enquiry enquiry) {
        printHeader("ENQUIRY DETAILS");
        
        System.out.println("Enquiry ID: " + enquiry.getEnquiryId());
        System.out.println("From: " + enquiry.getApplicantNric());
        System.out.println("Project: " + enquiry.getProjectName());
        System.out.println("Date: " + enquiry.getSubmittedAt().toLocalDate());
        
        System.out.println("\nMessage:");
        System.out.println(enquiry.getMessage());
        
        // Display all replies if there are any
        List<Enquiry.Reply> replies = enquiry.getReplies();
        
        if (replies.isEmpty()) {
            System.out.println("\nNo replies yet.");
        } else {
            System.out.println("\nReplies:");
            printDivider();
            
            int i = 1;
            for (Enquiry.Reply reply : replies) {
                System.out.println(i + ". " + reply.getText());
                System.out.println("   Replied by: " + reply.getRespondentNric() + " at " + reply.getTimestamp().toLocalDate());
                i++;
            }
        }
        
        printDivider();
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void replyToEnquiries() {
        while (true) {
            printHeader("VIEW MY PROJECT ENQUIRIES");
            
            // Get projects managed by this manager
            List<Project> myProjects = getMyProjects();
            if (myProjects.isEmpty()) {
                printMessage("You are not managing any projects.");
                System.out.println("\nEnter 0 to return to main menu: ");
                readChoice("", 0, 0);
                return;
            }
            
            // Get all enquiries for projects managed by this manager
            List<Enquiry> allEnquiries = enquiryFacade.getAllEnquiries();
            List<Enquiry> myProjectEnquiries = new ArrayList<>();
            
            // Filter to only include enquiries for this manager's projects
            for (Enquiry enquiry : allEnquiries) {
                for (Project project : myProjects) {
                    if (enquiry.getProjectName().equals(project.getProjectName())) {
                        myProjectEnquiries.add(enquiry);
                        break;
                    }
                }
            }
            
            if (myProjectEnquiries.isEmpty()) {
                printMessage("No enquiries found for your projects.");
                System.out.println("\nEnter 0 to return to main menu: ");
                readChoice("", 0, 0);
                return;
            }
            
            // Display enquiries in a tabular format with adjusted column order
            System.out.printf("%-4s %-20s %-15s %-20s %-15s %-20s %-15s%n", 
                            "No.", "Enquiry ID", "Applicant", "Project", "Status", "Message", "Date");
            printDivider();
            
            int i = 1;
            for (Enquiry enquiry : myProjectEnquiries) {
                // Determine status based on whether there's a reply
                String status = (enquiry.getReply() == null || enquiry.getReply().isEmpty() || 
                                 enquiry.getReply().equals("No reply yet")) ? "Pending Reply" : "Replied";
                
                System.out.printf("%-4d %-20s %-15s %-20s %-15s %-20s %-15s%n", 
                                i++, 
                                enquiry.getEnquiryId(),
                                truncate(enquiry.getApplicantNric(), 15),
                                truncate(enquiry.getProjectName(), 20),
                                status,
                                truncate(enquiry.getMessage(), 20),
                                enquiry.getSubmittedAt().toLocalDate());
            }
            
            System.out.println("\nEnter enquiry number to view details (0 to return to main menu): ");
            int choice = readChoice("", 0, myProjectEnquiries.size());
            
            if (choice == 0) {
                return;
            }
            
            // View and potentially reply to the selected enquiry
            Enquiry selectedEnquiry = myProjectEnquiries.get(choice - 1);
            viewEnquiryDetails(selectedEnquiry);
        }
    }
    
    private void viewEnquiryDetails(Enquiry enquiry) {
        printHeader("ENQUIRY DETAILS");
        
        System.out.println("Enquiry ID: " + enquiry.getEnquiryId());
        System.out.println("From: " + enquiry.getApplicantNric());
        System.out.println("Project: " + enquiry.getProjectName());
        System.out.println("Date: " + enquiry.getSubmittedAt().toLocalDate());
        
        System.out.println("\nMessage:");
        System.out.println(enquiry.getMessage());
        
        // Display current reply if there is one
        String currentReply = enquiry.getReply();
        if (currentReply != null && !currentReply.isEmpty() && !currentReply.equals("No reply yet")) {
            System.out.println("\nCurrent Reply:");
            System.out.println(currentReply);
            if (enquiry.getRepliedAt() != null) {
                System.out.println("Replied at: " + enquiry.getRepliedAt().toLocalDate());
            }
        } else {
            System.out.println("\nNo reply yet.");
        }
        
        printDivider();
        
        // Check if the manager manages this project
        boolean isManagerOfProject = false;
        List<Project> myProjects = getMyProjects();
        for (Project project : myProjects) {
            if (project.getProjectName().equals(enquiry.getProjectName())) {
                isManagerOfProject = true;
                break;
            }
        }
        
        if (isManagerOfProject) {
            System.out.println("Enter your reply (or type /cancel to return to enquiry list):");
            String reply = scanner.nextLine().trim();
            
            if (reply.equals("/cancel")) {
                return;
            }
            
            if (!reply.isEmpty()) {
                try {
                    // Use the legacy replyEnquiry method
                    enquiryFacade.replyEnquiry(enquiry.getEnquiryId(), reply);
                    printSuccess("Response submitted successfully.");
                    
                    // Refresh the enquiry to show the updated reply
                    refreshEnquiry(enquiry);
                } catch (Exception e) {
                    printError("Error replying to enquiry: " + e.getMessage());
                }
            } else {
                printError("Reply cannot be empty.");
            }
        } else {
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
        }
    }
    
    private void refreshEnquiry(Enquiry enquiry) {
        // Get a fresh list of enquiries to find the updated enquiry
        List<Enquiry> allEnquiries = enquiryFacade.getAllEnquiries();
        for (Enquiry updatedEnquiry : allEnquiries) {
            if (updatedEnquiry.getEnquiryId().equals(enquiry.getEnquiryId())) {
                // Copy all the replies from the updated enquiry to the current one
                if (updatedEnquiry.getReplies() != null && !updatedEnquiry.getReplies().isEmpty()) {
                    enquiry.setReplies(updatedEnquiry.getReplies());
                }
                
                // For legacy support also copy the single reply field
                enquiry.setReply(updatedEnquiry.getReply());
                break;
            }
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
            
            if (!projectManager.getPassword().equals(current)) {
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
        projectManager.setPassword(newPass);
        
        // Update password in file system
        try {
            // Load all managers from file
            List<ProjectManager> managers = io.FileIO.loadManagers();
            
            // Find and update the current manager's password
            boolean managerFound = false;
            for (int i = 0; i < managers.size(); i++) {
                if (managers.get(i).getNric().equals(projectManager.getNric())) {
                    managers.set(i, projectManager);
                    managerFound = true;
                    break;
                }
            }
            
            if (!managerFound) {
                throw new IllegalStateException("Manager not found in the database");
            }
            
            // Save the updated managers list back to the file
            io.FileIO.saveManagers(managers);
            
            printSuccess("Password changed successfully.");
        } catch (Exception e) {
            printError("Error updating password: " + e.getMessage());
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

    /**
     * Helper method to ensure changes to projects are saved to the CSV file.
     * This method is called after each operation that modifies project data.
     */
    private void saveProjectChanges() {
        try {
            if (projectFacade instanceof access.project.ProjectHandler) {
                ((access.project.ProjectHandler) projectFacade).saveChanges();
            } else {
                // Fallback method to save changes through FileIO
                FileIO.saveProjects(projectFacade.getAllProjects());
            }
        } catch (Exception e) {
            printError("Error saving project changes: " + e.getMessage());
        }
    }
}
