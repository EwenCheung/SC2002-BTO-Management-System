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
    }

    public void display() {
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
            System.out.println("6. Change Password");
            System.out.println("7. Logout");
            printDivider();
            
            int choice = readChoice(1, 7);
            if (choice == -1) continue;

            switch (choice) {
                case 1: browseProjects(); break;
                case 2: submitApplication(); break;
                case 3: viewApplicationStatus(); break;
                case 4: manageEnquiries(); break;
                case 5: manageWithdrawal(); break;  // Changed method name
                case 6: changePassword(); break;
                case 7:
                    printMessage("Logging out...");
                    return;
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
            
            List<Project> projects;
            boolean keepShowingProjects = true;
            
            while (keepShowingProjects) {
                switch (choice) {
                    case 1:
                        keepShowingProjects = displayProjects(projectFacade.getVisibleProjects());
                        break;
                    case 2:
                        System.out.print("Enter neighborhood name: ");
                        String neighborhood = scanner.nextLine().trim();
                        projects = filterProjectsByNeighborhood(projectFacade.getVisibleProjects(), neighborhood);
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
                        projects = filterProjectsByFlatType(projectFacade.getVisibleProjects(), flatType);
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
                        
                        projects = filterProjectsByPriceRange(projectFacade.getVisibleProjects(), minPrice, maxPrice);
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
            printMessage("No projects found matching your criteria.");
            
            // Enhanced feedback based on applicant's profile
            System.out.println("\nEligibility information:");
            if (maritalStatus.equals("SINGLE")) {
                if (applicantAge < 35) {
                    System.out.println("As a single applicant under 35 years old, you are not eligible for BTO applications yet.");
                    System.out.println("Singles must be 35 years old and above to apply for 2-Room flats only.");
                } else {
                    System.out.println("As a single applicant 35 years old and above, you are eligible for 2-Room flats only.");
                    System.out.println("Currently there are no 2-Room flats available for application.");
                }
            } else if (maritalStatus.equals("MARRIED")) {
                if (applicantAge < 21) {
                    System.out.println("As a married applicant under 21 years old, you are not eligible for BTO applications yet.");
                    System.out.println("Married applicants must be 21 years old and above to apply for any flat type.");
                } else {
                    System.out.println("As a married applicant 21 years old and above, you are eligible for any flat type (2-Room or 3-Room).");
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
                
                System.out.println("\nSystem-wide availability:");
                System.out.println("Projects with 2-Room flats available: " + total2Room);
                System.out.println("Projects with 3-Room flats available: " + total3Room);
                System.out.println("Please check back later for updates or adjust your filter criteria.");
            }
            
            return false;
        }
        
        // Display eligibility information first
        System.out.println();
        if (maritalStatus.equals("SINGLE") && applicantAge >= 35) {
            System.out.println("As a single above 35, you are only allowed to apply flat type with 2 room.");
        } else if (maritalStatus.equals("MARRIED") && applicantAge >= 21) {
            System.out.println("As a married above 21, you are allowed to apply flat type with 2 room or 3 room.");
        }
        System.out.println("Projects that match with you: " + eligibleProjects.size());
        
        // Display total projects as well
        System.out.println("Total Projects: " + projects.size());
        
        printHeader("PROJECT LIST");
        // Improved header alignment
        System.out.printf("%-4s %-25s %-12s %-12s %-15s %-25s\n", 
            "No.", "Project Name", "Flat Type", "Price", "Units Left", "Application Period");
        printDivider();
        
        // New display format with grouped flat types and empty line between projects
        int projectCounter = 1;
        for (Project project : eligibleProjects) {
            Map<String, UnitInfo> units = project.getUnits();
            
            // For singles, only show 2-Room flats
            if (isSingleEligible) {
                if (units.containsKey("2-Room") && units.get("2-Room").getAvailableUnits() > 0) {
                    UnitInfo info = units.get("2-Room");
                    System.out.printf("%-4d %-25s a) %-8s $%-10.0f %-15d %-25s\n", 
                        projectCounter, 
                        project.getProjectName(),
                        "2-Room",
                        info.getSellingPrice(),
                        info.getAvailableUnits(),
                        project.getApplicationOpeningDate() + " to " + project.getApplicationClosingDate()
                    );
                }
            } 
            // For married, show both flat types if available
            else if (isMarriedEligible) {
                boolean has2Room = units.containsKey("2-Room") && units.get("2-Room").getAvailableUnits() > 0;
                boolean has3Room = units.containsKey("3-Room") && units.get("3-Room").getAvailableUnits() > 0;
                
                if (has2Room) {
                    UnitInfo info = units.get("2-Room");
                    System.out.printf("%-4d %-25s a) %-8s $%-10.0f %-15d %-25s\n", 
                        projectCounter, 
                        project.getProjectName(),
                        "2-Room",
                        info.getSellingPrice(),
                        info.getAvailableUnits(),
                        project.getApplicationOpeningDate() + " to " + project.getApplicationClosingDate()
                    );
                }
                
                if (has3Room) {
                    UnitInfo info = units.get("3-Room");
                    if (!has2Room) {
                        // If no 2-Room, this becomes the 'a)' option
                        System.out.printf("%-4d %-25s a) %-8s $%-10.0f %-15d %-25s\n", 
                            projectCounter, 
                            project.getProjectName(),
                            "3-Room",
                            info.getSellingPrice(),
                            info.getAvailableUnits(),
                            project.getApplicationOpeningDate() + " to " + project.getApplicationClosingDate()
                        );
                    } else {
                        // If has 2-Room, this becomes the 'b)' option
                        System.out.printf("%-4s %-25s b) %-8s $%-10.0f %-15d %-25s\n", 
                            "", 
                            "",
                            "3-Room",
                            info.getSellingPrice(),
                            info.getAvailableUnits(),
                            project.getApplicationOpeningDate() + " to " + project.getApplicationClosingDate()
                        );
                    }
                }
            }
            
            // Add an empty line between projects
            System.out.println();
            projectCounter++;
        }
        
        System.out.println("\nEnter project number for details (0 to go back): ");
        int choice = readChoice(0, eligibleProjects.size());
        if (choice == 0 || choice == -1) return false;
        
        // Get the selected project directly from the eligibleProjects list
        Project selected = eligibleProjects.get(choice - 1);
        
        if (selected != null) {
            printHeader("PROJECT DETAILS: " + selected.getProjectName());
            System.out.println("Project: " + selected.getProjectName());
            System.out.println("Neighborhood: " + selected.getNeighborhood());
            System.out.println("Application Period: " + selected.getApplicationOpeningDate() + " to " + selected.getApplicationClosingDate());
            System.out.println("\nUnit Types Available:");
            
            Map<String, UnitInfo> units = selected.getUnits();
            
            // Display only eligible unit types based on marital status and age
            if (isSingleEligible) {
                if (units.containsKey("2-Room")) {
                    UnitInfo info = units.get("2-Room");
                    System.out.println("  - 2-Room: " + info.getAvailableUnits() + "/" + info.getTotalUnits() + 
                                      " units available, Price: $" + String.format("%.2f", info.getSellingPrice()));
                }
            } else if (isMarriedEligible) {
                if (units.containsKey("2-Room")) {
                    UnitInfo info = units.get("2-Room");
                    System.out.println("  - 2-Room: " + info.getAvailableUnits() + "/" + info.getTotalUnits() + 
                                      " units available, Price: $" + String.format("%.2f", info.getSellingPrice()));
                }
                
                if (units.containsKey("3-Room")) {
                    UnitInfo info = units.get("3-Room");
                    System.out.println("  - 3-Room: " + info.getAvailableUnits() + "/" + info.getTotalUnits() + 
                                      " units available, Price: $" + String.format("%.2f", info.getSellingPrice()));
                }
            }
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
        
        // Return true to indicate that we should stay in the project list view
        return true;
    }

    private void submitApplication() {
        printHeader("SUBMIT APPLICATION");
        
        List<Application> myApps = appFacade.getApplicationsForApplicant(applicant.getNric());
        if (!myApps.isEmpty()) {
            printError("You have already applied for a project. You cannot submit multiple applications.");
            return;
        }
        
        List<Project> projects = projectFacade.getVisibleProjects();
        if (projects.isEmpty()) {
            printError("No projects available for application.");
            return;
        }
        
        System.out.println("Available Projects:");
        printDivider();
        
        int i = 1;
        for (Project project : projects) {
            System.out.printf("%-2d. %-25s (%s)\n", i++, project.getProjectName(), project.getNeighborhood());
        }
        
        System.out.print("\nSelect project number (0 to cancel): ");
        int projectChoice = readChoice(0, projects.size());
        if (projectChoice == 0 || projectChoice == -1) {
            printMessage("Application cancelled.");
            return;
        }
        
        Project selectedProject = projects.get(projectChoice - 1);
        
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
                
                int flatTypeCount = 0;
                if (selectedProject.getUnits().containsKey("2-Room")) {
                    flatTypeCount++;
                    System.out.println("1. 2-Room (Price: $" + selectedProject.getUnits().get("2-Room").getSellingPrice() + ")");
                }
                
                if (selectedProject.getUnits().containsKey("3-Room")) {
                    flatTypeCount++;
                    System.out.println((flatTypeCount == 1 ? 1 : 2) + ". 3-Room (Price: $" + selectedProject.getUnits().get("3-Room").getSellingPrice() + ")");
                }
                
                System.out.print("Choose flat type (0 to cancel): ");
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
        System.out.println("Project: " + selectedProject.getProjectName());
        System.out.println("Neighborhood: " + selectedProject.getNeighborhood());
        System.out.println("Flat Type: " + allowedUnitType);
        System.out.println("Price: $" + selectedProject.getUnits().get(allowedUnitType).getSellingPrice());
        printDivider();
        
        System.out.print("Confirm application? (Y/N): ");
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
        
        // Modified to ensure application ID is displayed in full without truncation
        System.out.printf("%-20s %-25s %-10s %-15s %-15s\n", 
                          "Application ID", "Project", "Unit Type", "Status", "Last Updated");
        printDivider();
        
        for (Application app : myApps) {
            System.out.printf("%-20s %-25s %-10s %-15s %-15s\n",
                    app.getApplicationId(), // No truncation for Application ID
                    truncate(app.getProjectName(), 25),
                    app.getUnitType(),
                    app.getStatus(),
                    app.getLastUpdated().toLocalDate()
            );
        }
        
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
                    System.out.println("Application ID: " + selectedApp.getApplicationId());
                    System.out.println("Project: " + selectedApp.getProjectName());
                    System.out.println("Unit Type: " + selectedApp.getUnitType());
                    System.out.println("Status: " + selectedApp.getStatus());
                    System.out.println("Application Date: " + selectedApp.getApplicationDate().toLocalDate());
                    System.out.println("Last Updated: " + selectedApp.getLastUpdated().toLocalDate());
                    
                    if (selectedApp.getAssignedUnit() != null && !selectedApp.getAssignedUnit().isEmpty()) {
                        System.out.println("Assigned Unit: " + selectedApp.getAssignedUnit());
                    }
                    
                    if (selectedApp.getAssignedOfficer() != null && !selectedApp.getAssignedOfficer().isEmpty()) {
                        System.out.println("Assigned Officer: " + selectedApp.getAssignedOfficer());
                    }
                    
                    if (selectedApp.getRemarks() != null && !selectedApp.getRemarks().isEmpty()) {
                        System.out.println("Remarks: " + selectedApp.getRemarks());
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
        
        // Display available projects
        System.out.println("Available projects for enquiry:");
        printDivider();
        for (int i = 0; i < visibleProjects.size(); i++) {
            System.out.printf("%d. %s (%s)\n", i + 1, visibleProjects.get(i).getProjectName(), 
                              visibleProjects.get(i).getNeighborhood());
        }
        printDivider();
        
        // Let user select project by number
        System.out.print("Choose project number (0 to cancel): ");
        int projectChoice = readChoice(0, visibleProjects.size());
        if (projectChoice == 0 || projectChoice == -1) {
            printMessage("Enquiry cancelled.");
            return;
        }
        
        String projectName = visibleProjects.get(projectChoice - 1).getProjectName();
        
        // Get enquiry message
        System.out.print("Enter your enquiry message: ");
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
        
        // Improved formatting to ensure proper alignment
        System.out.printf("%-5s %-25s %-25s %-30s %-15s\n", 
            "No.", "Enquiry ID", "Project", "Message", "Status");
        printDivider();
        
        int i = 1;
        for (Enquiry enq : enquiries) {
            String status = (enq.getReply() == null || enq.getReply().isEmpty()) ? 
                            "Pending" : "Responded";
            
            System.out.printf("%-5d %-25s %-25s %-30s %-15s\n", 
                i++,
                enq.getEnquiryId(),
                truncate(enq.getProjectName(), 25),
                truncate(enq.getMessage(), 15), // Reduced to 15 characters
                status
            );
        }
        
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
        System.out.println("Enquiry ID: " + selectedEnq.getEnquiryId());
        System.out.println("Project: " + selectedEnq.getProjectName());
        System.out.println("Submit Date: " + (selectedEnq.getSubmittedAt() != null ? 
                            selectedEnq.getSubmittedAt().toLocalDate() : "N/A"));
        System.out.println("\nYour Message:");
        System.out.println(selectedEnq.getMessage());
        
        if (selectedEnq.getReply() != null && !selectedEnq.getReply().isEmpty()) {
            System.out.println("\nResponse:");
            System.out.println(selectedEnq.getReply());
            System.out.println("\nResponded by: " + 
                              (selectedEnq.getRespondentNric() != null ? selectedEnq.getRespondentNric() : "N/A"));
            System.out.println("Response Date: " + 
                              (selectedEnq.getRepliedAt() != null ? selectedEnq.getRepliedAt().toLocalDate() : "N/A"));
        } else {
            System.out.println("\nStatus: Pending response");
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
        
        // Display only the list of editable enquiries
        System.out.printf("%-5s %-25s %-25s %-30s %-15s\n", 
            "No.", "Enquiry ID", "Project", "Message", "Status");
        printDivider();
        
        int i = 1;
        for (Enquiry enq : editableEnquiries) {
            System.out.printf("%-5d %-25s %-25s %-30s %-15s\n", 
                i++,
                enq.getEnquiryId(),
                truncate(enq.getProjectName(), 25),
                truncate(enq.getMessage(), 15),
                "Pending"
            );
        }
        
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
        
        System.out.println("\nCurrent message:");
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
        
        // Display only deletable enquiries
        System.out.printf("%-5s %-25s %-25s %-30s %-15s\n", 
            "No.", "Enquiry ID", "Project", "Message", "Status");
        printDivider();
        
        int i = 1;
        for (Enquiry enq : deletableEnquiries) {
            System.out.printf("%-5d %-25s %-25s %-30s %-15s\n", 
                i++,
                enq.getEnquiryId(),
                truncate(enq.getProjectName(), 25),
                truncate(enq.getMessage(), 15),  // Changed to 15 chars to match other methods
                "Pending"
            );
        }
        
        // Let user select which enquiry to delete
        System.out.print("\nEnter enquiry number to delete (or enquiry ID, 0 to cancel): ");
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
        
        // Display the applications for the user to select from - with application ID in full
        System.out.printf("%-5s %-25s %-25s %-10s %-15s\n", 
                          "No.", "Application ID", "Project", "Unit Type", "Status");
        printDivider();
        
        int i = 1;
        for (Application app : withdrawableApps) {
            System.out.printf("%-5d %-25s %-25s %-10s %-15s\n", 
                i++, 
                app.getApplicationId(),
                truncate(app.getProjectName(), 25),
                app.getUnitType(),
                app.getStatus()
            );
        }
        
        // Let the user select which application to withdraw
        System.out.print("\nSelect application number to withdraw (0 to cancel): ");
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
        System.out.println("Application ID: " + selectedApp.getApplicationId());
        System.out.println("Project: " + selectedApp.getProjectName());
        System.out.println("Unit Type: " + selectedApp.getUnitType());
        System.out.println("Current Status: " + selectedApp.getStatus());
        printDivider();
        System.out.println("WARNING: Withdrawal is irreversible once processed.");
        
        if (!readYesNo("Are you sure you want to withdraw this application? (Y/N): ")) {
            printMessage("Withdrawal request cancelled.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Get reason for withdrawal
        System.out.print("Please provide a reason for withdrawal (optional): ");
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
        
        // Display the withdrawal requests
        System.out.printf("%-5s %-25s %-25s %-15s %-15s\n", 
                        "No.", "Request ID", "Application ID", "Status", "Request Date");
        printDivider();
        
        int i = 1;
        for (WithdrawalRequest req : myRequests) {
            System.out.printf("%-5d %-25s %-25s %-15s %-15s\n", 
                i++, 
                req.getRequestId(),
                req.getApplicationId(),
                req.getStatus(),
                req.getRequestDate().toLocalDate()
            );
        }
        
        // Let the user select a request to view details
        System.out.print("\nSelect request number for details (or request ID, 0 to go back): ");
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
        System.out.println("Request ID: " + selectedReq.getRequestId());
        System.out.println("Application ID: " + selectedReq.getApplicationId());
        System.out.println("Project: " + selectedReq.getProjectName());
        System.out.println("Status: " + selectedReq.getStatus());
        System.out.println("Request Date: " + selectedReq.getRequestDate().toLocalDate());
        
        if (selectedReq.getRemarks() != null && !selectedReq.getRemarks().isEmpty()) {
            System.out.println("\nReason for withdrawal:");
            System.out.println(selectedReq.getRemarks());
        }
        
        if (selectedReq.getStatus().toString().equalsIgnoreCase("APPROVED")) {
            System.out.println("\nYour withdrawal request has been approved.");
            System.out.println("Processed Date: " + (selectedReq.getProcessDate() != null ? 
                              selectedReq.getProcessDate().toLocalDate() : "N/A"));
        } else if (selectedReq.getStatus().toString().equalsIgnoreCase("REJECTED")) {
            System.out.println("\nYour withdrawal request has been rejected.");
            System.out.println("Processed Date: " + (selectedReq.getProcessDate() != null ? 
                              selectedReq.getProcessDate().toLocalDate() : "N/A"));
        } else {
            System.out.println("\nYour withdrawal request is still pending.");
            System.out.println("Please check back later for updates.");
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void changePassword() {
        printHeader("CHANGE PASSWORD");
        
        System.out.print("Enter your current password: ");
        String current = scanner.nextLine().trim();
        
        if (!applicant.getPassword().equals(current)) {
            printError("Incorrect current password.");
            return;
        }
        
        System.out.print("Enter your new password: ");
        String newPass = scanner.nextLine().trim();
        
        System.out.print("Confirm your new password: ");
        String confirmPass = scanner.nextLine().trim();
        
        if (!newPass.equals(confirmPass)) {
            printError("Passwords do not match. Password change cancelled.");
            return;
        }
        
        if (newPass.isEmpty()) {
            printError("Password cannot be empty.");
            return;
        }
        
        applicant.setPassword(newPass);
        
        // Save the updated password to the ApplicantList.csv file
        if (updateUserPassword()) {
            printSuccess("Password changed successfully.");
        } else {
            printError("Failed to save the new password. Please try again.");
        }
    }

    /**
     * Updates the password in the ApplicantList.csv file
     * @return true if successful, false otherwise
     */
    private boolean updateUserPassword() {
        try {
            // Read all lines from the ApplicantList.csv file
            java.nio.file.Path path = java.nio.file.Paths.get("Datasets/ApplicantList.csv");
            List<String> lines = java.nio.file.Files.readAllLines(path);
            
            boolean updated = false;
            
            // Update the applicant's line with the new password
            for (int i = 1; i < lines.size(); i++) { // Start from 1 to skip header
                String[] fields = lines.get(i).split(",");
                if (fields.length >= 2 && fields[1].equals(applicant.getNric())) {
                    // Reconstruct the line with the new password
                    String updatedLine = fields[0] + "," + 
                                        fields[1] + "," + 
                                        fields[2] + "," + 
                                        fields[3] + "," + 
                                        applicant.getPassword();
                    lines.set(i, updatedLine);
                    updated = true;
                    break;
                }
            }
            
            if (updated) {
                // Write the updated lines back to the CSV file
                java.nio.file.Files.write(path, lines);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }

    private void printHeader(String title) {
        System.out.println("\n" + FileUtils.repeatChar('=', 60));
        System.out.println(FileUtils.repeatChar(' ', (60 - title.length()) / 2) + title);
        System.out.println(FileUtils.repeatChar('=', 60));
    }
    
    private void printDivider() {
        System.out.println(FileUtils.repeatChar('-', 60));
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
    
    private void printWarning(String message) {
        System.out.println("\n⚠ " + message);
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
    
    private double readDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            printError("Please enter a valid number.");
            return -1;
        }
    }
    
    private boolean readYesNo() {
        String input = scanner.nextLine().trim().toUpperCase();
        return input.equals("Y") || input.equals("YES");
    }
    
    private boolean readYesNo(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim().toUpperCase();
        return input.equals("Y") || input.equals("YES");
    }
    
    private String truncate(String str, int length) {
        if (str.length() <= length) return str;
        return str.substring(0, length - 3) + "...";
    }
}

