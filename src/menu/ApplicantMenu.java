package menu;

import access.application.ApplicantApplicationFeatures;
import access.enquiry.ApplicantEnquiryFeatures;
import access.project.ApplicantProjectFeatures;
import access.withdrawal.ApplicantWithdrawalFeatures;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList; // Added import for ArrayList
import models.Application;
import models.Enquiry;
import models.Project;
import models.WithdrawalRequest;
import models.UnitInfo;
import users.Applicant;
import utils.FileUtils;
import utils.UIFormatter; // Added import for UIFormatter
import utils.TablePrinter; // Added import for TablePrinter

public class ApplicantMenu {
    private Scanner scanner;
    private Applicant applicant;
    private ApplicantProjectFeatures projectFacade;
    private ApplicantApplicationFeatures appFacade;
    private ApplicantEnquiryFeatures enquiryFacade;
    private ApplicantWithdrawalFeatures withdrawalFacade;

    public ApplicantMenu(Applicant applicant, 
                         ApplicantProjectFeatures projectFacade,
                         ApplicantApplicationFeatures appFacade,
                         ApplicantEnquiryFeatures enquiryFacade,
                         ApplicantWithdrawalFeatures withdrawalFacade) {
        this.scanner = new Scanner(System.in);
        this.applicant = applicant;
        this.projectFacade = projectFacade;
        this.appFacade = appFacade;
        this.enquiryFacade = enquiryFacade;
        this.withdrawalFacade = withdrawalFacade;
        
        // Initialize color support based on terminal capabilities
        UIFormatter.setColorEnabled(UIFormatter.supportsColors());
    }

    /**
     * Displays the Applicant Menu and handles user interactions.
     * 
     * @return true if the user chooses to switch to Officer Mode (for HDB Officers only),
     *         false if the user chooses to logout.
     */
    public boolean display() {
        while (true) {
            printHeader("APPLICANT MENU");
            System.out.println("Welcome, " + applicant.getName());
            System.out.println("Age: " + applicant.getAge() + " | Marital Status: " + applicant.getMaritalStatus());
            printDivider();
            
            System.out.println("1. Browse Projects");
            System.out.println("2. Submit Application");
            System.out.println("3. View Application Status");
            System.out.println("4. Manage Enquiries");
            System.out.println("5. Manage Withdrawal");  // Changed from "Request Withdrawal" to "Manage Withdrawal"
            
            // Check if user is an HDB Officer (to show Change Mode option)
            boolean isOfficer = applicant.getUserType() == users.enums.UserType.OFFICER;
            if (isOfficer) {
                System.out.println("6. Switch to Officer Mode");
                System.out.println("7. Logout");
            } else {
                System.out.println("6. Change Password");
                System.out.println("7. Logout");
            }
            
            printDivider();
            
            int maxChoice = 7;
            int choice = readChoice(1, maxChoice);
            if (choice == -1) continue;

            switch (choice) {
                case 1: browseProjects(); break;
                case 2: submitApplication(); break;
                case 3: viewApplicationStatus(); break;
                case 4: manageEnquiries(); break;
                case 5: manageWithdrawal(); break;  // Changed method name
                case 6:
                    if (isOfficer) {
                        printMessage("Switching to Officer Mode...");
                        return true; // Return true to switch to Officer mode
                    } else {
                        changePassword();
                    }
                    break;
                case 7:
                    printMessage("Logging out...");
                    return false; // Return false to logout
            }
        }
    }

    private void browseProjects() {
        while (true) {
            printHeader("BROWSE PROJECTS");
            System.out.println("1. View All Available Projects");
            System.out.println("2. Filter by Neighborhood");
            System.out.println("3. Filter by Flat Type");
            System.out.println("4. Filter by Price Range");
            System.out.println("5. Back to Main Menu");
            printDivider();
            
            int choice = readChoice(1, 5);
            if (choice == -1) continue;
            
            if (choice == 5) return;
            
            // Get the list of projects the applicant has already applied to
            List<Application> myApps = appFacade.getApplicationsForApplicant(applicant.getNric());
            List<String> appliedProjectNames = new ArrayList<>();
            for (Application app : myApps) {
                appliedProjectNames.add(app.getProjectName());
            }
            
            // Get visible projects based on application period and applied projects
            List<Project> visibleProjects = projectFacade.getVisibleProjects(applicant.getNric(), appliedProjectNames);
            
            List<Project> projects;
            boolean keepShowingProjects = true;
            
            while (keepShowingProjects) {
                switch (choice) {
                    case 1:
                        keepShowingProjects = displayProjects(visibleProjects);
                        break;
                    case 2:
                        System.out.print("Enter neighborhood name: ");
                        String neighborhood = scanner.nextLine().trim();
                        projects = filterProjectsByNeighborhood(visibleProjects, neighborhood);
                        keepShowingProjects = displayProjects(projects);
                        break;
                    case 3:
                        System.out.println("Select flat type:");
                        System.out.println("1. 2-Room");
                        System.out.println("2. 3-Room");
                        int flatChoice = readChoice(1, 2);
                        if (flatChoice == -1) {
                            keepShowingProjects = false;
                            break;
                        }
                        
                        String flatType = (flatChoice == 1) ? "2-Room" : "3-Room";
                        projects = filterProjectsByFlatType(visibleProjects, flatType);
                        keepShowingProjects = displayProjects(projects);
                        break;
                    case 4:
                        System.out.print("Enter minimum price: ");
                        double minPrice = readDouble();
                        if (minPrice < 0) {
                            keepShowingProjects = false;
                            break;
                        }
                        
                        System.out.print("Enter maximum price: ");
                        double maxPrice = readDouble();
                        if (maxPrice < 0) {
                            keepShowingProjects = false;
                            break;
                        }
                        
                        projects = filterProjectsByPriceRange(visibleProjects, minPrice, maxPrice);
                        keepShowingProjects = displayProjects(projects);
                        break;
                }
                
                if (!keepShowingProjects) {
                    break; // Break out of the inner while loop
                }
            }
        }
    }
    
    private List<Project> filterProjectsByNeighborhood(List<Project> projects, String neighborhood) {
        List<Project> filtered = new java.util.ArrayList<>();
        for (Project project : projects) {
            if (project.getNeighborhood().toLowerCase().contains(neighborhood.toLowerCase())) {
                filtered.add(project);
            }
        }
        return filtered;
    }
    
    private List<Project> filterProjectsByFlatType(List<Project> projects, String flatType) {
        List<Project> filtered = new java.util.ArrayList<>();
        for (Project project : projects) {
            if (project.getUnits().containsKey(flatType)) {
                filtered.add(project);
            }
        }
        return filtered;
    }
    
    private List<Project> filterProjectsByPriceRange(List<Project> projects, double minPrice, double maxPrice) {
        List<Project> filtered = new java.util.ArrayList<>();
        for (Project project : projects) {
            for (java.util.Map.Entry<String, models.UnitInfo> entry : project.getUnits().entrySet()) {
                if (entry.getValue().getSellingPrice() >= minPrice && entry.getValue().getSellingPrice() <= maxPrice) {
                    filtered.add(project);
                    break;
                }
            }
        }
        return filtered;
    }
    
    private boolean displayProjects(List<Project> projects) {
        // First, filter projects based on applicant's eligibility
        List<Project> eligibleProjects = new ArrayList<>();
        
        String maritalStatus = applicant.getMaritalStatus().toString();
        int applicantAge = applicant.getAge();
        
        // Determine eligibility based on marital status and age
        boolean isSingleEligible = maritalStatus.equals("SINGLE") && applicantAge >= 35;
        boolean isMarriedEligible = maritalStatus.equals("MARRIED") && applicantAge >= 21;
        
        for (Project project : projects) {
            // For singles, 35 years old and above, can ONLY apply for 2-Room
            if (isSingleEligible && project.getUnits().containsKey("2-Room") && project.getAvailableUnits("2-Room") > 0) {
                eligibleProjects.add(project);
            }
            // For married, 21 years old and above, can apply for any flat types (2-Room or 3-Room)
            else if (isMarriedEligible) {
                if ((project.getUnits().containsKey("2-Room") && project.getAvailableUnits("2-Room") > 0) || 
                    (project.getUnits().containsKey("3-Room") && project.getAvailableUnits("3-Room") > 0)) {
                    eligibleProjects.add(project);
                }
            }
        }
        
        // If no eligible projects found, provide feedback based on eligibility
        if (eligibleProjects.isEmpty()) {
            // If we're showing all projects and none match, it's an eligibility issue
            if (projects.size() == projectFacade.getVisibleProjects().size()) {
                printMessage("No projects found matching your criteria.");
                
                // Enhanced feedback based on applicant's profile
                System.out.println("\nEligibility information:");
                if (maritalStatus.equals("SINGLE")) {
                    if (applicantAge < 35) {
                        System.out.println(UIFormatter.formatWarning("As a single applicant under 35 years old, you are not eligible for BTO applications yet."));
                        System.out.println("Singles must be 35 years old and above to apply for 2-Room flats only.");
                    } else {
                        System.out.println(UIFormatter.formatWarning("As a single applicant 35 years old and above, you are eligible for 2-Room flats only."));
                        System.out.println("Currently there are no 2-Room flats available for application.");
                    }
                } else if (maritalStatus.equals("MARRIED")) {
                    if (applicantAge < 21) {
                        System.out.println(UIFormatter.formatWarning("As a married applicant under 21 years old, you are not eligible for BTO applications yet."));
                        System.out.println("Married applicants must be 21 years old and above to apply for any flat type.");
                    } else {
                        System.out.println(UIFormatter.formatWarning("As a married applicant 21 years old and above, you are eligible for any flat type (2-Room or 3-Room)."));
                        System.out.println("Currently there are no flats available for application.");
                    }
                }
                
                // Only show system-wide availability if the user is eligible to apply
                if (isSingleEligible || isMarriedEligible) {
                    // Get ALL visible projects to show total availability
                    List<Project> allVisibleProjects = projectFacade.getVisibleProjects();
                    int total2Room = 0;
                    int total3Room = 0;
                    
                    for (Project p : allVisibleProjects) {
                        Map<String, UnitInfo> units = p.getUnits();
                        if (units.containsKey("2-Room") && units.get("2-Room").getAvailableUnits() > 0) {
                            total2Room++;
                        }
                        if (units.containsKey("3-Room") && units.get("3-Room").getAvailableUnits() > 0) {
                            total3Room++;
                        }
                    }
                    
                    System.out.println("\n" + UIFormatter.formatSectionHeader("System-wide Availability"));
                    
                    TablePrinter availabilityTable = new TablePrinter(new String[] {
                        "Flat Type", "Projects with Available Units"
                    });
                    
                    availabilityTable.addRow("2-Room", String.valueOf(total2Room));
                    availabilityTable.addRow("3-Room", String.valueOf(total3Room));
                    availabilityTable.print();
                    
                    System.out.println("\nPlease check back later for updates or adjust your filter criteria.");
                }
            } else {
                // This is a filter result with no matches
                printMessage("No projects match your filter criteria.");
            }
            
            return false;
        }
        
        // Display eligibility information first
        System.out.println();
        if (maritalStatus.equals("SINGLE") && applicantAge >= 35) {
            System.out.println(UIFormatter.formatInfo("As a single above 35, you are only allowed to apply flat type with 2 room."));
        } else if (maritalStatus.equals("MARRIED") && applicantAge >= 21) {
            System.out.println(UIFormatter.formatInfo("As a married above 21, you are allowed to apply flat type with 2 room or 3 room."));
        }
        System.out.println("Projects that match with you: " + UIFormatter.highlight(String.valueOf(eligibleProjects.size())));
        System.out.println("Total Projects: " + projects.size());
        
        printHeader("PROJECT LIST");
        
        // Create table for projects using TablePrinter
        TablePrinter projectTable = new TablePrinter(new String[] {
            "No.", "Project Name", "Flat Type", "Price", "Units Left", "Application Period"
        });
        
        // Add rows to the table based on eligibility and flat type availability
        int projectCounter = 1;
        for (Project project : eligibleProjects) {
            Map<String, UnitInfo> units = project.getUnits();
            String period = project.getApplicationOpeningDate() + " to " + project.getApplicationClosingDate();
            
            // For singles, only show 2-Room flats
            if (isSingleEligible) {
                if (units.containsKey("2-Room") && units.get("2-Room").getAvailableUnits() > 0) {
                    UnitInfo info = units.get("2-Room");
                    projectTable.addRow(
                        String.valueOf(projectCounter),
                        project.getProjectName(),
                        "2-Room",
                        "$" + String.format("%.0f", info.getSellingPrice()),
                        String.valueOf(info.getAvailableUnits()),
                        period
                    );
                }
            } 
            // For married, show both flat types if available
            else if (isMarriedEligible) {
                boolean has2Room = units.containsKey("2-Room") && units.get("2-Room").getAvailableUnits() > 0;
                boolean has3Room = units.containsKey("3-Room") && units.get("3-Room").getAvailableUnits() > 0;
                
                if (has2Room) {
                    UnitInfo info = units.get("2-Room");
                    projectTable.addRow(
                        String.valueOf(projectCounter),
                        project.getProjectName(),
                        "2-Room",
                        "$" + String.format("%.0f", info.getSellingPrice()),
                        String.valueOf(info.getAvailableUnits()),
                        period
                    );
                }
                
                if (has3Room) {
                    UnitInfo info = units.get("3-Room");
                    if (!has2Room) {
                        projectTable.addRow(
                            String.valueOf(projectCounter),
                            project.getProjectName(),
                            "3-Room",
                            "$" + String.format("%.0f", info.getSellingPrice()),
                            String.valueOf(info.getAvailableUnits()),
                            period
                        );
                    } else {
                        projectTable.addRow(
                            "",
                            "",
                            "3-Room",
                            "$" + String.format("%.0f", info.getSellingPrice()),
                            String.valueOf(info.getAvailableUnits()),
                            ""
                        );
                    }
                }
            }
            
            projectCounter++;
        }
        
        projectTable.print();
        
        System.out.print("\nEnter project number for details (0 to go back): ");
        int choice = readChoice(0, eligibleProjects.size());
        if (choice == 0 || choice == -1) return false;
        
        // Get the selected project directly from the eligibleProjects list
        Project selected = eligibleProjects.get(choice - 1);
        
        if (selected != null) {
            printHeader("PROJECT DETAILS: " + selected.getProjectName());
            System.out.println("Project: " + UIFormatter.highlight(selected.getProjectName()));
            System.out.println("Neighborhood: " + UIFormatter.highlight(selected.getNeighborhood()));
            System.out.println("Application Period: " + selected.getApplicationOpeningDate() + " to " + selected.getApplicationClosingDate());
            
            System.out.println("\n" + UIFormatter.formatSectionHeader("Unit Types Available"));
            
            // Create unit types table
            TablePrinter unitsTable = new TablePrinter(new String[] {
                "Unit Type", "Available Units", "Total Units", "Price ($)"
            });
            
            Map<String, UnitInfo> units = selected.getUnits();
            
            // Display only eligible unit types based on marital status and age
            if (isSingleEligible) {
                if (units.containsKey("2-Room")) {
                    UnitInfo info = units.get("2-Room");
                    unitsTable.addRow(
                        "2-Room",
                        String.valueOf(info.getAvailableUnits()),
                        String.valueOf(info.getTotalUnits()),
                        String.format("%.2f", info.getSellingPrice())
                    );
                }
            } else if (isMarriedEligible) {
                if (units.containsKey("2-Room")) {
                    UnitInfo info = units.get("2-Room");
                    unitsTable.addRow(
                        "2-Room",
                        String.valueOf(info.getAvailableUnits()),
                        String.valueOf(info.getTotalUnits()),
                        String.format("%.2f", info.getSellingPrice())
                    );
                }
                
                if (units.containsKey("3-Room")) {
                    UnitInfo info = units.get("3-Room");
                    unitsTable.addRow(
                        "3-Room",
                        String.valueOf(info.getAvailableUnits()),
                        String.valueOf(info.getTotalUnits()),
                        String.format("%.2f", info.getSellingPrice())
                    );
                }
            }
            
            unitsTable.print();
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
        
        // Return true to indicate that we should stay in the project list view
        return true;
    }

    /**
     * Check if the applicant (who is also an officer) has any pending or approved registration for a specific project
     * @param projectName The name of the project to check
     * @return true if the applicant has registered for the project as an officer, false otherwise
     */
    private boolean hasOfficerRegistrationForProject(String projectName) {
        // Only check if this applicant is also an officer
        if (applicant.getUserType() != users.enums.UserType.OFFICER) {
            return false;
        }
        
        try {
            // Try to load officer registrations from file
            java.nio.file.Path path = java.nio.file.Paths.get("Datasets/OfficerRegistrations.csv");
            
            if (java.nio.file.Files.exists(path)) {
                List<String> lines = java.nio.file.Files.readAllLines(path);
                
                // Skip header row
                for (int i = 1; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (line == null || line.trim().isEmpty()) {
                        continue;
                    }
                    
                    String[] data = line.split(",");
                    
                    // Registration ID (0), Officer NRIC (1), Project Name (2), Status (3), Date (4)
                    if (data.length >= 4) {
                        String officerNric = data[1].trim();
                        String regProjectName = data[2].trim();
                        String status = data[3].trim();
                        
                        // If this registration belongs to the current user and is for the specified project
                        if (officerNric.equals(applicant.getNric()) && regProjectName.equals(projectName)) {
                            // Check if status is PENDING or APPROVED - using case-insensitive comparison
                            if (status.equalsIgnoreCase("PENDING") || status.equalsIgnoreCase("APPROVED")) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking officer registrations: " + e.getMessage());
        }
        
        return false;
    }

    private void submitApplication() {
        printHeader("SUBMIT APPLICATION");
        
        List<Application> myApps = appFacade.getApplicationsForApplicant(applicant.getNric());
        if (!myApps.isEmpty()) {
            printError("You have already applied for a project. You cannot submit multiple applications.");
            return;
        }
        
        // Get all visible projects
        List<Project> allProjects = projectFacade.getVisibleProjects();
        if (allProjects.isEmpty()) {
            printError("No projects available for application.");
            return;
        }
        
        // Filter out projects that the applicant has registered for as an officer
        List<Project> availableProjects = new ArrayList<>();
        
        for (Project project : allProjects) {
            boolean hasOfficerRegistration = hasOfficerRegistrationForProject(project.getProjectName());
            if (!hasOfficerRegistration) {
                availableProjects.add(project);
            }
        }
        
        if (availableProjects.isEmpty()) {
            printError("You cannot apply for any projects as you have pending or approved officer registrations for all available projects.");
            return;
        }
        
        System.out.println(UIFormatter.formatSectionHeader("Available Projects"));
        
        // Use TablePrinter for available projects table
        TablePrinter projectsTable = new TablePrinter(new String[] {
            "No.", "Project Name", "Neighborhood", "Application Period"
        });
        
        int i = 1;
        for (Project project : availableProjects) {
            String period = project.getApplicationOpeningDate() + " to " + project.getApplicationClosingDate();
            projectsTable.addRow(
                String.valueOf(i++),
                project.getProjectName(),
                project.getNeighborhood(),
                period
            );
        }
        
        projectsTable.print();
        
        System.out.print("\n" + UIFormatter.formatPrompt("Select project number (0 to cancel): "));
        int projectChoice = readChoice(0, availableProjects.size());
        if (projectChoice == 0 || projectChoice == -1) {
            printMessage("Application cancelled.");
            return;
        }
        
        Project selectedProject = availableProjects.get(projectChoice - 1);
        
        String allowedUnitType = null;
        
        if (applicant.getMaritalStatus().toString().equalsIgnoreCase("SINGLE")) {
            if (applicant.getAge() >= 35) {
                allowedUnitType = "2-Room";
                printMessage("As a Single applicant over 35, you are eligible for 2-Room flats only.");
            } else {
                printError("As a Single applicant, you must be at least 35 years old to apply.");
                return;
            }
        } else {
            if (applicant.getAge() >= 21) {
                printHeader("FLAT SELECTION");
                System.out.println("As a Married applicant, you are eligible for:");
                
                // Use TablePrinter for flat type options
                TablePrinter flatsTable = new TablePrinter(new String[] {
                    "Option", "Flat Type", "Price"
                });
                
                int flatTypeCount = 0;
                if (selectedProject.getUnits().containsKey("2-Room")) {
                    flatTypeCount++;
                    flatsTable.addRow(
                        "1",
                        "2-Room",
                        "$" + String.format("%.2f", selectedProject.getUnits().get("2-Room").getSellingPrice())
                    );
                }
                
                if (selectedProject.getUnits().containsKey("3-Room")) {
                    flatTypeCount++;
                    flatsTable.addRow(
                        flatTypeCount == 1 ? "1" : "2",
                        "3-Room",
                        "$" + String.format("%.2f", selectedProject.getUnits().get("3-Room").getSellingPrice())
                    );
                }
                
                flatsTable.print();
                
                System.out.print(UIFormatter.formatPrompt("Choose flat type (0 to cancel): "));
                int flatChoice = readChoice(0, flatTypeCount);
                if (flatChoice == 0 || flatChoice == -1) {
                    printMessage("Application cancelled.");
                    return;
                }
                
                if (flatTypeCount == 1) {
                    if (selectedProject.getUnits().containsKey("2-Room")) {
                        allowedUnitType = "2-Room";
                    } else {
                        allowedUnitType = "3-Room";
                    }
                } else {
                    allowedUnitType = (flatChoice == 1) ? "2-Room" : "3-Room";
                }
            } else {
                printError("As a Married applicant, you must be at least 21 years old.");
                return;
            }
        }
        
        printHeader("CONFIRM APPLICATION");
        TablePrinter confirmTable = new TablePrinter(new String[] {
            "Detail", "Value"
        });
        
        confirmTable.addRow("Project", selectedProject.getProjectName());
        confirmTable.addRow("Neighborhood", selectedProject.getNeighborhood());
        confirmTable.addRow("Flat Type", allowedUnitType);
        confirmTable.addRow("Price", "$" + String.format("%.2f", selectedProject.getUnits().get(allowedUnitType).getSellingPrice()));
        
        confirmTable.print();
        
        printDivider();
        
        System.out.print(UIFormatter.formatPrompt("Confirm application? (Y/N): "));
        if (!readYesNo()) {
            printMessage("Application cancelled.");
            return;
        }
        
        Application application = new Application(applicant.getNric(), selectedProject.getProjectName(), allowedUnitType);
        appFacade.submitApplication(application);
        
        // Save changes immediately to ensure the application is stored in the CSV file
        if (appFacade instanceof access.application.ApplicationHandler) {
            ((access.application.ApplicationHandler) appFacade).saveChanges();
        }
        
        printSuccess("Application submitted successfully!");
    }

    private void viewApplicationStatus() {
        printHeader("APPLICATION STATUS");
        List<Application> myApps = appFacade.getApplicationsForApplicant(applicant.getNric());
        
        if (myApps.isEmpty()) {
            printMessage("You have not submitted any applications yet.");
            return;
        }
        
        // Use TablePrinter for application status table
        TablePrinter table = new TablePrinter(new String[] {
            "Application ID", "Project", "Unit Type", "Status", "Last Updated"
        });
        
        for (Application app : myApps) {
            table.addRow(
                app.getApplicationId(),
                TablePrinter.formatCell(app.getProjectName(), 25),
                app.getUnitType(),
                UIFormatter.formatStatus(app.getStatus().toString()),
                app.getLastUpdated().toLocalDate().toString()
            );
        }
        
        table.print();
        
        // If there are applications, allow viewing more details
        if (!myApps.isEmpty()) {
            System.out.print("\nEnter Application ID for more details (or press Enter to go back): ");
            String appId = scanner.nextLine().trim();
            
            if (!appId.isEmpty()) {
                Application selectedApp = null;
                for (Application app : myApps) {
                    if (app.getApplicationId().equals(appId)) {
                        selectedApp = app;
                        break;
                    }
                }
                
                if (selectedApp != null) {
                    printHeader("APPLICATION DETAILS");
                    System.out.println("Application ID: " + UIFormatter.highlight(selectedApp.getApplicationId()));
                    System.out.println("Project: " + UIFormatter.highlight(selectedApp.getProjectName()));
                    System.out.println("Unit Type: " + selectedApp.getUnitType());
                    System.out.println("Status: " + UIFormatter.formatStatus(selectedApp.getStatus().toString()));
                    System.out.println("Application Date: " + selectedApp.getApplicationDate().toLocalDate());
                    System.out.println("Last Updated: " + selectedApp.getLastUpdated().toLocalDate());
                    
                    if (selectedApp.getAssignedUnit() != null && !selectedApp.getAssignedUnit().isEmpty()) {
                        System.out.println("Assigned Unit: " + selectedApp.getAssignedUnit());
                    }
                    
                    if (selectedApp.getAssignedOfficer() != null && !selectedApp.getAssignedOfficer().isEmpty()) {
                        System.out.println("Assigned Officer: " + selectedApp.getAssignedOfficer());
                    }
                    
                    if (selectedApp.getRemarks() != null && !selectedApp.getRemarks().isEmpty()) {
                        System.out.println("\nRemarks:");
                        System.out.println(selectedApp.getRemarks());
                    }
                } else {
                    printError("Invalid Application ID.");
                }
                
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private void manageEnquiries() {
        while (true) {
            printHeader("ENQUIRIES MANAGEMENT");
            System.out.println("1. Submit New Enquiry");
            System.out.println("2. View My Enquiries");
            System.out.println("3. Edit Enquiry");
            System.out.println("4. Delete Enquiry");
            System.out.println("5. Back to Applicant Menu");
            printDivider();
            int choice = readChoice(1, 5);
            if (choice == -1) continue;
            switch (choice) {
                case 1: submitEnquiry(); break;
                case 2: viewEnquiries(); break;
                case 3: editEnquiry(); break;
                case 4: deleteEnquiry(); break;
                case 5: return;
            }
        }
    }

    private void submitEnquiry() {
        printHeader("SUBMIT NEW ENQUIRY");
        
        // Get all visible projects for this applicant
        List<Project> visibleProjects = projectFacade.getVisibleProjects();
        
        if (visibleProjects.isEmpty()) {
            printError("No projects are currently available for enquiry.");
            return;
        }
        
        // Display available projects using TablePrinter
        System.out.println(UIFormatter.formatSectionHeader("Available Projects for Enquiry"));
        
        TablePrinter projectsTable = new TablePrinter(new String[] {
            "No.", "Project Name", "Neighborhood"
        });
        
        int i = 1;
        for (Project project : visibleProjects) {
            projectsTable.addRow(
                String.valueOf(i++),
                project.getProjectName(),
                project.getNeighborhood()
            );
        }
        
        projectsTable.print();
        
        // Let user select project by number
        System.out.print("\n" + UIFormatter.formatPrompt("Choose project number (0 to cancel): "));
        int projectChoice = readChoice(0, visibleProjects.size());
        if (projectChoice == 0 || projectChoice == -1) {
            printMessage("Enquiry cancelled.");
            return;
        }
        
        String projectName = visibleProjects.get(projectChoice - 1).getProjectName();
        
        // Get enquiry message
        System.out.print(UIFormatter.formatPrompt("Enter your enquiry message: "));
        String message = scanner.nextLine().trim();
        
        if (message.isEmpty()) {
            printError("Enquiry message cannot be empty.");
            return;
        }
        
        // Create the Enquiry object with the correct constructor (3 parameters)
        Enquiry enquiry = new Enquiry(applicant.getNric(), projectName, message);
        
        // Submit the enquiry
        enquiryFacade.submitEnquiry(enquiry);
        
        // Save changes immediately to ensure the enquiry is stored in the CSV file
        if (enquiryFacade instanceof access.enquiry.EnquiryHandler) {
            ((access.enquiry.EnquiryHandler) enquiryFacade).saveChanges();
        }
        
        printSuccess("Enquiry submitted successfully.");
    }

    private void viewEnquiries() {
        printHeader("MY ENQUIRIES");
        List<Enquiry> enquiries = enquiryFacade.getEnquiriesForApplicant(applicant.getNric());
        
        if (enquiries.isEmpty()) {
            printMessage("You haven't submitted any enquiries yet.");
            return;
        }
        
        // Use TablePrinter for enquiries table
        TablePrinter table = new TablePrinter(new String[] {
            "No.", "Enquiry ID", "Project", "Message", "Status"
        });
        
        int i = 1;
        for (Enquiry enq : enquiries) {
            String status = (enq.getReply() == null || enq.getReply().isEmpty()) ? 
                            "Pending" : "Responded";
            
            table.addRow(
                String.valueOf(i++),
                enq.getEnquiryId(),
                TablePrinter.formatCell(enq.getProjectName(), 25),
                TablePrinter.formatCell(enq.getMessage(), 30),
                UIFormatter.formatStatus(status)
            );
        }
        
        table.print();
        
        System.out.print("\nEnter enquiry number to view details (or enquiry ID, 0 to go back): ");
        String input = scanner.nextLine().trim();
        
        // If the user enters 0 or nothing, go back
        if (input.equals("0") || input.isEmpty()) return;
        
        Enquiry selectedEnq = null;
        
        // Try to parse as a number (row number)
        try {
            int choice = Integer.parseInt(input);
            if (choice > 0 && choice <= enquiries.size()) {
                selectedEnq = enquiries.get(choice - 1);
            } else {
                printError("Invalid enquiry number. Please enter a number between 1 and " + enquiries.size() + ".");
                return;
            }
        } catch (NumberFormatException e) {
            // If not a number, try to match by Enquiry ID
            for (Enquiry enq : enquiries) {
                if (enq.getEnquiryId().equals(input)) {
                    selectedEnq = enq;
                    break;
                }
            }
            
            if (selectedEnq == null) {
                printError("Invalid enquiry ID. Please enter a valid enquiry ID or number.");
                return;
            }
        }
        
        // Display the selected enquiry details
        printHeader("ENQUIRY DETAILS");
        System.out.println("Enquiry ID: " + UIFormatter.highlight(selectedEnq.getEnquiryId()));
        System.out.println("Project: " + UIFormatter.highlight(selectedEnq.getProjectName()));
        System.out.println("Submit Date: " + (selectedEnq.getSubmittedAt() != null ? 
                            selectedEnq.getSubmittedAt().toLocalDate() : "N/A"));
        
        System.out.println("\n" + UIFormatter.formatSectionHeader("Your Message"));
        System.out.println(selectedEnq.getMessage());
        
        if (selectedEnq.getReply() != null && !selectedEnq.getReply().isEmpty()) {
            System.out.println("\n" + UIFormatter.formatSectionHeader("Response"));
            System.out.println(selectedEnq.getReply());
            System.out.println("\nResponded by: " + 
                              (selectedEnq.getRespondentNric() != null ? selectedEnq.getRespondentNric() : "N/A"));
            System.out.println("Response Date: " + 
                              (selectedEnq.getRepliedAt() != null ? selectedEnq.getRepliedAt().toLocalDate() : "N/A"));
        } else {
            System.out.println("\nStatus: " + UIFormatter.formatStatus("Pending"));
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void editEnquiry() {
        printHeader("EDIT ENQUIRY");
        
        // Get user's enquiries
        List<Enquiry> enquiries = enquiryFacade.getEnquiriesForApplicant(applicant.getNric());
        
        if (enquiries.isEmpty()) {
            printMessage("You haven't submitted any enquiries yet.");
            return;
        }
        
        // Filter out enquiries that have been responded to
        List<Enquiry> editableEnquiries = new ArrayList<>();
        for (Enquiry enq : enquiries) {
            if (enq.getReply() == null || enq.getReply().isEmpty()) {
                editableEnquiries.add(enq);
            }
        }
        
        if (editableEnquiries.isEmpty()) {
            printMessage("You don't have any enquiries that can be edited. Enquiries that have been responded to cannot be modified.");
            return;
        }
        
        // Use TablePrinter for editable enquiries table
        TablePrinter table = new TablePrinter(new String[] {
            "No.", "Enquiry ID", "Project", "Message", "Status"
        });
        
        int i = 1;
        for (Enquiry enq : editableEnquiries) {
            table.addRow(
                String.valueOf(i++),
                enq.getEnquiryId(),
                TablePrinter.formatCell(enq.getProjectName(), 25),
                TablePrinter.formatCell(enq.getMessage(), 30),
                UIFormatter.formatStatus("Pending")
            );
        }
        
        table.print();
        
        // Let user select which enquiry to edit
        System.out.print("\nEnter enquiry number to edit (or enquiry ID, 0 to cancel): ");
        String input = scanner.nextLine().trim();
        
        // If the user enters 0 or nothing, go back
        if (input.equals("0") || input.isEmpty()) return;
        
        Enquiry selectedEnq = null;
        
        // Try to parse as a number (row number)
        try {
            int choice = Integer.parseInt(input);
            if (choice > 0 && choice <= editableEnquiries.size()) {
                selectedEnq = editableEnquiries.get(choice - 1);
            } else {
                printError("Invalid enquiry number. Please enter a number between 1 and " + editableEnquiries.size() + ".");
                return;
            }
        } catch (NumberFormatException e) {
            // If not a number, try to match by Enquiry ID
            for (Enquiry enq : editableEnquiries) {
                if (enq.getEnquiryId().equals(input)) {
                    selectedEnq = enq;
                    break;
                }
            }
            
            if (selectedEnq == null) {
                printError("Invalid enquiry ID. Please enter a valid enquiry ID or number.");
                return;
            }
        }
        
        System.out.println("\n" + UIFormatter.formatSectionHeader("Current Message"));
        System.out.println(selectedEnq.getMessage());
        
        // Get the new message
        System.out.print("\nEnter the new enquiry message (or press Enter to cancel): ");
        String newMessage = scanner.nextLine().trim();
        
        if (newMessage.isEmpty()) {
            printMessage("Edit cancelled.");
            return;
        }
        
        // Submit the edit
        try {
            enquiryFacade.editEnquiry(selectedEnq.getEnquiryId(), newMessage);
            
            // Save changes immediately to ensure the edit is stored in the CSV file
            if (enquiryFacade instanceof access.enquiry.EnquiryHandler) {
                ((access.enquiry.EnquiryHandler) enquiryFacade).saveChanges();
            }
            
            printSuccess("Enquiry updated successfully.");
        } catch (Exception e) {
            printError("Error updating enquiry: " + e.getMessage());
        }
    }

    private void deleteEnquiry() {
        printHeader("DELETE ENQUIRY");
        
        // Get user's enquiries
        List<Enquiry> enquiries = enquiryFacade.getEnquiriesForApplicant(applicant.getNric());
        
        if (enquiries.isEmpty()) {
            printMessage("You haven't submitted any enquiries yet.");
            return;
        }
        
        // Filter out enquiries that have been responded to
        List<Enquiry> deletableEnquiries = new ArrayList<>();
        for (Enquiry enq : enquiries) {
            if (enq.getReply() == null || enq.getReply().isEmpty()) {
                deletableEnquiries.add(enq);
            }
        }
        
        if (deletableEnquiries.isEmpty()) {
            printMessage("You don't have any enquiries that can be deleted. Enquiries that have been responded to cannot be deleted.");
            return;
        }
        
        // Use TablePrinter for deletable enquiries table
        TablePrinter table = new TablePrinter(new String[] {
            "No.", "Enquiry ID", "Project", "Message", "Status"
        });
        
        int i = 1;
        for (Enquiry enq : deletableEnquiries) {
            table.addRow(
                String.valueOf(i++),
                enq.getEnquiryId(),
                TablePrinter.formatCell(enq.getProjectName(), 25),
                TablePrinter.formatCell(enq.getMessage(), 30),
                UIFormatter.formatStatus("Pending")
            );
        }
        
        table.print();
        
        // Let user select which enquiry to delete
        System.out.print("\n" + UIFormatter.formatPrompt("Enter enquiry number to delete (or enquiry ID, 0 to cancel): "));
        String input = scanner.nextLine().trim();
        
        // If the user enters 0 or nothing, go back
        if (input.equals("0") || input.isEmpty()) return;
        
        Enquiry selectedEnq = null;
        
        // Try to parse as a number (row number)
        try {
            int choice = Integer.parseInt(input);
            if (choice > 0 && choice <= deletableEnquiries.size()) {
                selectedEnq = deletableEnquiries.get(choice - 1);
            } else {
                printError("Invalid enquiry number. Please enter a number between 1 and " + deletableEnquiries.size() + ".");
                return;
            }
        } catch (NumberFormatException e) {
            // If not a number, try to match by Enquiry ID
            for (Enquiry enq : deletableEnquiries) {
                if (enq.getEnquiryId().equals(input)) {
                    selectedEnq = enq;
                    break;
                }
            }
            
            if (selectedEnq == null) {
                printError("Invalid enquiry ID. Please enter a valid enquiry ID or number.");
                return;
            }
        }
        
        // Confirm deletion
        if (!readYesNo("Are you sure you want to delete this enquiry? (Y/N): ")) {
            printMessage("Delete cancelled.");
            return;
        }
        
        // Submit the delete request
        try {
            enquiryFacade.deleteEnquiry(selectedEnq.getEnquiryId());
            
            // Save changes immediately to ensure the deletion is reflected in the CSV file
            if (enquiryFacade instanceof access.enquiry.EnquiryHandler) {
                ((access.enquiry.EnquiryHandler) enquiryFacade).saveChanges();
            }
            
            printSuccess("Enquiry deleted successfully.");
        } catch (Exception e) {
            printError("Error deleting enquiry: " + e.getMessage());
        }
    }
    
    private void manageWithdrawal() {
        while (true) {
            printHeader("MANAGE WITHDRAWAL");
            System.out.println("1. Submit Withdrawal Request");
            System.out.println("2. View My Withdrawal Requests");
            System.out.println("3. Back to Main Menu");
            printDivider();
            
            int choice = readChoice(1, 3);
            if (choice == -1) continue;
            
            switch (choice) {
                case 1:
                    submitWithdrawalRequest();
                    break;
                case 2:
                    viewWithdrawalRequests();
                    break;
                case 3:
                    return;
            }
        }
    }

    private void submitWithdrawalRequest() {
        printHeader("SUBMIT WITHDRAWAL REQUEST");
        
        // First, check if the applicant has any applications
        List<Application> myApps = appFacade.getApplicationsForApplicant(applicant.getNric());
        if (myApps.isEmpty()) {
            printError("You don't have any applications to withdraw.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Get existing withdrawal requests to check for duplicates
        List<WithdrawalRequest> existingRequests = withdrawalFacade.getWithdrawalRequestsForApplicant(applicant.getNric());
        
        // Filter out applications that already have withdrawal requests
        List<Application> withdrawableApps = new ArrayList<>();
        for (Application app : myApps) {
            boolean hasExistingRequest = false;
            for (WithdrawalRequest request : existingRequests) {
                if (request.getApplicationId().equals(app.getApplicationId())) {
                    hasExistingRequest = true;
                    break;
                }
            }
            
            if (!hasExistingRequest) {
                withdrawableApps.add(app);
            }
        }
        
        if (withdrawableApps.isEmpty()) {
            printError("You don't have any applications available for withdrawal. You may have already submitted withdrawal requests for all your applications.");
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Use TablePrinter for withdrawal applications table
        TablePrinter table = new TablePrinter(new String[] {
            "No.", "Application ID", "Project", "Unit Type", "Status"
        });
        
        int i = 1;
        for (Application app : withdrawableApps) {
            table.addRow(
                String.valueOf(i++),
                app.getApplicationId(),
                TablePrinter.formatCell(app.getProjectName(), 25),
                app.getUnitType(),
                UIFormatter.formatStatus(app.getStatus().toString())
            );
        }
        
        table.print();
        
        // Let the user select which application to withdraw
        System.out.print("\n" + UIFormatter.formatPrompt("Select application number to withdraw (0 to cancel): "));
        int choice = readChoice(0, withdrawableApps.size());
        if (choice == 0 || choice == -1) {
            printMessage("Withdrawal request cancelled.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        Application selectedApp = withdrawableApps.get(choice - 1);
        
        // Check if this application can be withdrawn (not already withdrawn or rejected)
        if (selectedApp.getStatus().toString().equalsIgnoreCase("WITHDRAWN")) {
            printError("This application has already been withdrawn.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        if (selectedApp.getStatus().toString().equalsIgnoreCase("REJECTED")) {
            printError("You cannot withdraw a rejected application.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Get confirmation from the user
        printHeader("CONFIRM WITHDRAWAL");
        System.out.println("Application ID: " + UIFormatter.highlight(selectedApp.getApplicationId()));
        System.out.println("Project: " + UIFormatter.highlight(selectedApp.getProjectName()));
        System.out.println("Unit Type: " + selectedApp.getUnitType());
        System.out.println("Current Status: " + UIFormatter.formatStatus(selectedApp.getStatus().toString()));
        printDivider();
        System.out.println(UIFormatter.formatWarning("WARNING: Withdrawal is irreversible once processed."));
        
        if (!readYesNo("Are you sure you want to withdraw this application? (Y/N): ")) {
            printMessage("Withdrawal request cancelled.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Get reason for withdrawal
        System.out.print(UIFormatter.formatPrompt("Please provide a reason for withdrawal (optional): "));
        String remarks = scanner.nextLine().trim();
        
        // Create and submit withdrawal request
        WithdrawalRequest request = new WithdrawalRequest(
            "", // ID will be generated by the handler
            selectedApp.getApplicationId(),
            applicant.getNric(),
            selectedApp.getProjectName(),
            models.enums.WithdrawalStatus.PENDING,
            java.time.LocalDateTime.now(),
            null,
            remarks
        );
        
        withdrawalFacade.requestWithdrawal(request);
        
        // Save changes immediately to ensure the withdrawal request is stored in the CSV file
        if (withdrawalFacade instanceof access.withdrawal.WithdrawalHandler) {
            ((access.withdrawal.WithdrawalHandler) withdrawalFacade).saveChanges();
        }
        
        printSuccess("Withdrawal request submitted successfully. Your request will be processed by a manager.");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewWithdrawalRequests() {
        printHeader("MY WITHDRAWAL REQUESTS");
        
        // Get existing withdrawal requests
        List<WithdrawalRequest> myRequests = withdrawalFacade.getWithdrawalRequestsForApplicant(applicant.getNric());
        
        if (myRequests.isEmpty()) {
            printMessage("You haven't submitted any withdrawal requests yet.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Use TablePrinter for withdrawal requests table
        TablePrinter table = new TablePrinter(new String[] {
            "No.", "Request ID", "Application ID", "Project", "Status", "Request Date"
        });
        
        int i = 1;
        for (WithdrawalRequest req : myRequests) {
            table.addRow(
                String.valueOf(i++),
                req.getRequestId(),
                req.getApplicationId(),
                TablePrinter.formatCell(req.getProjectName(), 25),
                UIFormatter.formatStatus(req.getStatus().toString()),
                req.getRequestDate().toLocalDate().toString()
            );
        }
        
        table.print();
        
        // Let the user select a request to view details
        System.out.print("\n" + UIFormatter.formatPrompt("Select request number for details (or request ID, 0 to go back): "));
        String input = scanner.nextLine().trim();
        
        // If the user enters 0 or nothing, go back
        if (input.equals("0") || input.isEmpty()) return;
        
        // Try to find the selected request
        WithdrawalRequest selectedReq = null;
        
        // Try to parse as a number (row number)
        try {
            int choice = Integer.parseInt(input);
            if (choice > 0 && choice <= myRequests.size()) {
                selectedReq = myRequests.get(choice - 1);
            } else {
                printError("Invalid request number. Please enter a number between 1 and " + myRequests.size() + ".");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                return;
            }
        } catch (NumberFormatException e) {
            // If not a number, try to match by Request ID
            for (WithdrawalRequest req : myRequests) {
                if (req.getRequestId().equals(input)) {
                    selectedReq = req;
                    break;
                }
            }
            
            if (selectedReq == null) {
                printError("Invalid request ID. Please enter a valid request ID or number.");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                return;
            }
        }
        
        // Display the selected request details
        printHeader("WITHDRAWAL REQUEST DETAILS");
        System.out.println("Request ID: " + UIFormatter.highlight(selectedReq.getRequestId()));
        System.out.println("Application ID: " + UIFormatter.highlight(selectedReq.getApplicationId()));
        System.out.println("Project: " + UIFormatter.highlight(selectedReq.getProjectName()));
        System.out.println("Status: " + UIFormatter.formatStatus(selectedReq.getStatus().toString()));
        System.out.println("Request Date: " + selectedReq.getRequestDate().toLocalDate());
        
        if (selectedReq.getRemarks() != null && !selectedReq.getRemarks().isEmpty()) {
            System.out.println("\n" + UIFormatter.formatSectionHeader("Reason for Withdrawal"));
            System.out.println(selectedReq.getRemarks());
        }
        
        if (selectedReq.getStatus().toString().equalsIgnoreCase("APPROVED")) {
            System.out.println("\nYour withdrawal request has been " + UIFormatter.formatStatus("APPROVED") + ".");
            System.out.println("Processed Date: " + (selectedReq.getProcessDate() != null ? 
                              selectedReq.getProcessDate().toLocalDate() : "N/A"));
        } else if (selectedReq.getStatus().toString().equalsIgnoreCase("REJECTED")) {
            System.out.println("\nYour withdrawal request has been " + UIFormatter.formatStatus("REJECTED") + ".");
            System.out.println("Processed Date: " + (selectedReq.getProcessDate() != null ? 
                              selectedReq.getProcessDate().toLocalDate() : "N/A"));
        } else {
            System.out.println("\nYour withdrawal request is " + UIFormatter.formatStatus("PENDING") + ".");
            System.out.println("Please check back later for updates.");
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void changePassword() {
        printHeader("CHANGE PASSWORD");
        
        System.out.println(UIFormatter.formatInfo("Enter 0 at any time to cancel the password change."));
        
        // Get current password
        while (true) {
            System.out.print(UIFormatter.formatPrompt("Enter your current password: "));
            String current = scanner.nextLine().trim();
            
            // Check if user wants to quit
            if (current.equals("0")) {
                printMessage("Password change cancelled.");
                return;
            }
            
            if (!applicant.getPassword().equals(current)) {
                printError("Incorrect current password. Try again or enter 0 to quit.");
                continue;
            }
            break;
        }
        
        // Get new password
        String newPass;
        while (true) {
            System.out.print(UIFormatter.formatPrompt("Enter your new password: "));
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
            System.out.print(UIFormatter.formatPrompt("Confirm your new password: "));
            String confirmPass = scanner.nextLine().trim();
            
            if (!newPass.equals(confirmPass)) {
                printError("Passwords do not match. Try again.");
                continue;
            }
            
            break;
        }
        
        // Update password in memory
        applicant.setPassword(newPass);
        
        // Update password in file system
        try {
            // Load all applicants from file
            List<Applicant> applicants = io.FileIO.loadApplicants();
            
            // Find and update the current applicant's password
            boolean applicantFound = false;
            for (int i = 0; i < applicants.size(); i++) {
                if (applicants.get(i).getNric().equals(applicant.getNric())) {
                    applicants.set(i, applicant);
                    applicantFound = true;
                    break;
                }
            }
            
            if (!applicantFound) {
                throw new IllegalStateException("Applicant not found in the database");
            }
            
            // Save the updated applicants list back to the file
            io.FileIO.saveApplicants(applicants);
            
            printSuccess("Password changed successfully.");
        } catch (Exception e) {
            printError("Error updating password: " + e.getMessage());
        }
    }

    private void printHeader(String title) {
        System.out.println(UIFormatter.formatHeader(title));
    }
    
    private void printDivider() {
        System.out.println(UIFormatter.formatDivider());
    }
    
    private void printMessage(String message) {
        System.out.println(UIFormatter.formatInfo(message));
    }
    
    private void printSuccess(String message) {
        System.out.println(UIFormatter.formatSuccess(message));
    }
    
    private void printError(String message) {
        System.out.println(UIFormatter.formatError(message));
    }
    
    private int readChoice(int min, int max) {
        System.out.print(UIFormatter.formatPrompt("Enter your choice: "));
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
    
    private double readDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            printError("Please enter a valid number.");
            return -1;
        }
    }
    
    private boolean readYesNo() {
        System.out.print(UIFormatter.formatPrompt("Confirm (Y/N): "));
        String input = scanner.nextLine().trim().toUpperCase();
        return input.equals("Y") || input.equals("YES");
    }
    
    private boolean readYesNo(String prompt) {
        System.out.print(UIFormatter.formatPrompt(prompt));
        String input = scanner.nextLine().trim().toUpperCase();
        return input.equals("Y") || input.equals("YES");
    }
    
    private String truncate(String str, int length) {
        return TablePrinter.formatCell(str, length);
    }
}

