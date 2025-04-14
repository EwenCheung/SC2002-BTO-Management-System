package menu;

import access.application.ApplicantApplicationFeatures;
import access.enquiry.ApplicantEnquiryFeatures;
import access.project.ApplicantProjectFeatures;
import access.withdrawal.ApplicantWithdrawalFeatures;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import models.Application;
import models.Enquiry;
import models.Project;
import models.WithdrawalRequest;
import models.enums.WithdrawalStatus;
import users.Applicant;
import users.User;
import utils.Constants;

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
            System.out.println("5. Request Withdrawal");
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
                case 5: requestWithdrawal(); break;
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
            switch (choice) {
                case 1:
                    displayProjects(projectFacade.getVisibleProjects());
                    break;
                case 2:
                    System.out.print("Enter neighborhood name: ");
                    String neighborhood = scanner.nextLine().trim();
                    projects = filterProjectsByNeighborhood(projectFacade.getVisibleProjects(), neighborhood);
                    displayProjects(projects);
                    break;
                case 3:
                    System.out.println("Select flat type:");
                    System.out.println("1. 2-Room");
                    System.out.println("2. 3-Room");
                    int flatChoice = readChoice(1, 2);
                    if (flatChoice == -1) continue;
                    
                    String flatType = (flatChoice == 1) ? "2-Room" : "3-Room";
                    projects = filterProjectsByFlatType(projectFacade.getVisibleProjects(), flatType);
                    displayProjects(projects);
                    break;
                case 4:
                    System.out.print("Enter minimum price: ");
                    double minPrice = readDouble();
                    if (minPrice < 0) continue;
                    
                    System.out.print("Enter maximum price: ");
                    double maxPrice = readDouble();
                    if (maxPrice < 0) continue;
                    
                    projects = filterProjectsByPriceRange(projectFacade.getVisibleProjects(), minPrice, maxPrice);
                    displayProjects(projects);
                    break;
            }
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
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
    
    private void displayProjects(List<Project> projects) {
        if (projects.isEmpty()) {
            printMessage("No projects found matching your criteria.");
            return;
        }
        
        printHeader("PROJECT LIST");
        System.out.printf("%-4s %-20s %-15s %-25s %-10s\n", "No.", "Project Name", "Neighborhood", "Application Period", "Visibility");
        printDivider();
        
        int i = 1;
        for (Project project : projects) {
            System.out.printf("%-4d %-20s %-15s %-12s to %-10s %-10s\n", 
                i++, 
                truncate(project.getProjectName(), 20),
                truncate(project.getNeighborhood(), 15),
                project.getApplicationOpeningDate(),
                project.getApplicationClosingDate(),
                project.isVisible() ? "Visible" : "Hidden"
            );
        }
        
        System.out.println("\nEnter project number for details (0 to go back): ");
        int choice = readChoice(0, projects.size());
        if (choice == 0 || choice == -1) return;
        
        Project selected = projects.get(choice - 1);
        printHeader("PROJECT DETAILS: " + selected.getProjectName());
        System.out.println(selected.toString());
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
        printSuccess("Application submitted successfully!");
    }

    private void viewApplicationStatus() {
        printHeader("APPLICATION STATUS");
        List<Application> myApps = appFacade.getApplicationsForApplicant(applicant.getNric());
        
        if (myApps.isEmpty()) {
            printMessage("You have not submitted any applications yet.");
            return;
        }
        
        System.out.printf("%-15s %-25s %-10s %-15s %-15s\n", 
                          "Application ID", "Project", "Unit Type", "Status", "Last Updated");
        printDivider();
        
        for (Application app : myApps) {
            System.out.printf("%-15s %-25s %-10s %-15s %-15s\n",
                    truncate(app.getApplicationId(), 15),
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
        System.out.print("Enter the project name for your enquiry: ");
        String projectName = scanner.nextLine().trim();
        System.out.print("Enter your enquiry message: ");
        String message = scanner.nextLine().trim();
        // Create a new Enquiry with the three-parameter constructor
        Enquiry enquiry = new Enquiry(applicant.getNric(), projectName, message);
        enquiryFacade.submitEnquiry(enquiry);
        printSuccess("Enquiry submitted successfully.");
    }

    private void viewEnquiries() {
        printHeader("MY ENQUIRIES");
        List<Enquiry> enquiries = enquiryFacade.getEnquiriesForApplicant(applicant.getNric());
        
        if (enquiries.isEmpty()) {
            printMessage("You haven't submitted any enquiries yet.");
            return;
        }
        
        System.out.printf("%-5s %-15s %-25s %-30s %-15s\n", 
            "No.", "Enquiry ID", "Project", "Message", "Status");
        printDivider();
        
        int i = 1;
        for (Enquiry enq : enquiries) {
            String status = (enq.getReply() == null || enq.getReply().isEmpty()) ? 
                            "Pending" : "Responded";
            
            System.out.printf("%-5d %-15s %-25s %-30s %-15s\n", 
                i++,
                truncate(enq.getEnquiryId(), 15),
                truncate(enq.getProjectName(), 25),
                truncate(enq.getMessage(), 30),
                status
            );
        }
        
        System.out.print("\nEnter enquiry number to view details (0 to go back): ");
        int choice = readChoice(0, enquiries.size());
        if (choice == 0 || choice == -1) return;
        
        Enquiry selectedEnq = enquiries.get(choice - 1);
        
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
        
        // Display the list of enquiries
        System.out.printf("%-5s %-15s %-25s %-30s %-15s\n", 
            "No.", "Enquiry ID", "Project", "Message", "Status");
        printDivider();
        
        int i = 1;
        for (Enquiry enq : enquiries) {
            String status = (enq.getReply() == null || enq.getReply().isEmpty()) ? 
                            "Pending" : "Responded";
            
            System.out.printf("%-5d %-15s %-25s %-30s %-15s\n", 
                i++,
                truncate(enq.getEnquiryId(), 15),
                truncate(enq.getProjectName(), 25),
                truncate(enq.getMessage(), 30),
                status
            );
        }
        
        // Let user select which enquiry to edit
        System.out.print("\nEnter enquiry number to edit (0 to cancel): ");
        int choice = readChoice(0, enquiries.size());
        if (choice == 0 || choice == -1) return;
        
        Enquiry selectedEnq = enquiries.get(choice - 1);
        
        // Check if the enquiry already has a response
        if (selectedEnq.getReply() != null && !selectedEnq.getReply().isEmpty()) {
            printError("Cannot edit an enquiry that has already been responded to.");
            return;
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
        
        // Display the list of enquiries
        System.out.printf("%-5s %-15s %-25s %-30s %-15s\n", 
            "No.", "Enquiry ID", "Project", "Message", "Status");
        printDivider();
        
        int i = 1;
        for (Enquiry enq : enquiries) {
            String status = (enq.getReply() == null || enq.getReply().isEmpty()) ? 
                            "Pending" : "Responded";
            
            System.out.printf("%-5d %-15s %-25s %-30s %-15s\n", 
                i++,
                truncate(enq.getEnquiryId(), 15),
                truncate(enq.getProjectName(), 25),
                truncate(enq.getMessage(), 30),
                status
            );
        }
        
        // Let user select which enquiry to delete
        System.out.print("\nEnter enquiry number to delete (0 to cancel): ");
        int choice = readChoice(0, enquiries.size());
        if (choice == 0 || choice == -1) return;
        
        Enquiry selectedEnq = enquiries.get(choice - 1);
        
        // Check if the enquiry already has a response
        if (selectedEnq.getReply() != null && !selectedEnq.getReply().isEmpty()) {
            printWarning("You are about to delete an enquiry that has been responded to.");
            if (!readYesNo("Are you sure you want to continue? (Y/N): ")) {
                printMessage("Delete cancelled.");
                return;
            }
        } else {
            // Confirm deletion
            if (!readYesNo("Are you sure you want to delete this enquiry? (Y/N): ")) {
                printMessage("Delete cancelled.");
                return;
            }
        }
        
        // Submit the delete request
        try {
            enquiryFacade.deleteEnquiry(selectedEnq.getEnquiryId());
            printSuccess("Enquiry deleted successfully.");
        } catch (Exception e) {
            printError("Error deleting enquiry: " + e.getMessage());
        }
    }
    
    private void requestWithdrawal() {
        printHeader("REQUEST WITHDRAWAL");
        
        // First, check if the applicant has any applications
        List<Application> myApps = appFacade.getApplicationsForApplicant(applicant.getNric());
        if (myApps.isEmpty()) {
            printError("You don't have any applications to withdraw.");
            return;
        }
        
        // Display the applications for the user to select from
        System.out.printf("%-5s %-15s %-25s %-10s %-15s\n", 
                          "No.", "Application ID", "Project", "Unit Type", "Status");
        printDivider();
        
        int i = 1;
        for (Application app : myApps) {
            System.out.printf("%-5d %-15s %-25s %-10s %-15s\n", 
                i++, 
                truncate(app.getApplicationId(), 15),
                truncate(app.getProjectName(), 25),
                app.getUnitType(),
                app.getStatus()
            );
        }
        
        // Let the user select which application to withdraw
        System.out.print("\nSelect application number to withdraw (0 to cancel): ");
        int choice = readChoice(0, myApps.size());
        if (choice == 0 || choice == -1) {
            printMessage("Withdrawal request cancelled.");
            return;
        }
        
        Application selectedApp = myApps.get(choice - 1);
        
        // Check if this application can be withdrawn (not already withdrawn or rejected)
        if (selectedApp.getStatus().toString().equalsIgnoreCase("WITHDRAWN")) {
            printError("This application has already been withdrawn.");
            return;
        }
        
        if (selectedApp.getStatus().toString().equalsIgnoreCase("REJECTED")) {
            printError("You cannot withdraw a rejected application.");
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
        printSuccess("Withdrawal request submitted successfully. Your request will be processed by a manager.");
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
        applicant.setPassword(newPass);
        printSuccess("Password changed successfully.");
    }

    private void printHeader(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(" ".repeat((60 - title.length()) / 2) + title);
        System.out.println("=".repeat(60));
    }
    
    private void printDivider() {
        System.out.println("-".repeat(60));
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

