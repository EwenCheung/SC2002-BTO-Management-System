package menu;

import access.application.ApplicantApplicationFeatures;
import access.enquiry.ApplicantEnquiryFeatures;
import access.project.ApplicantProjectFeatures;
import access.withdrawal.ApplicantWithdrawalFeatures;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import models.Application;
import models.Enquiry;
import models.Project;
import models.WithdrawalRequest;
import users.Applicant;
import users.User;
import utils.Constants;

public class ApplicantMenu {
    // Use the DATE_FORMAT constant from Constants.java.
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
        // Construct an Applicant object using the details in the generic User.
        this.applicant = applicant;
        this.projectFacade = projectFacade;
        this.appFacade = appFacade;
        this.enquiryFacade = enquiryFacade;
        this.withdrawalFacade = withdrawalFacade;
    }

    public void display() {
        while (true) {
            System.out.println("\n=== Applicant Menu ===");
            System.out.println("Welcome, " + applicant.getName());
            System.out.println("1. Browse Projects");
            System.out.println("2. Submit Application");
            System.out.println("3. View Application Status");
            System.out.println("4. Manage Enquiries");
            System.out.println("5. Request Withdrawal");
            System.out.println("6. Change Password");
            System.out.println("7. Logout");
            System.out.print("Enter your choice: ");

            int choice = 0;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1:
                    browseProjects();
                    break;
                case 2:
                    submitApplication();
                    break;
                case 3:
                    viewApplicationStatus();
                    break;
                case 4:
                    manageEnquiries();
                    break;
                case 5:
                    requestWithdrawal();
                    break;
                case 6:
                    changePassword();
                    break;
                case 7:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Option 1: Browse Projects (using ApplicantProjectFeatures)
    private void browseProjects() {
        System.out.println("\n=== Browse Projects ===");
        List<Project> visibleProjects = projectFacade.getVisibleProjects();
        if (visibleProjects.isEmpty()) {
            System.out.println("No projects available at the moment.");
        } else {
            for (Project project : visibleProjects) {
                System.out.println(project);
            }
        }
    }

    // Option 2: Submit Application (using ApplicantApplicationFeatures)
    private void submitApplication() {
        System.out.println("\n=== Submit Application ===");
        // Check if the applicant already has an application.
        List<Application> myApps = appFacade.getApplicationsForApplicant(applicant.getNric());
        if (!myApps.isEmpty()) {
            System.out.println("You have already applied for a project. You cannot submit multiple applications.");
            return;
        }
        System.out.print("Enter the name of the project you wish to apply for: ");
        String projectName = scanner.nextLine().trim();
        
        // Determine eligibility and allowed flat type.
        String allowedUnitType = null;
        if (applicant.getMaritalStatus().toString().equalsIgnoreCase("SINGLE")) {
            if (applicant.getAge() >= 35) {
                allowedUnitType = "2-Room";
            } else {
                System.out.println("As a Single, you must be at least 35 years old to apply.");
                return;
            }
        } else { // Married.
            if (applicant.getAge() >= 21) {
                System.out.println("Choose flat type:\n1. 2-Room\n2. 3-Room");
                int flatChoice;
                try {
                    flatChoice = Integer.parseInt(scanner.nextLine().trim());
                    if (flatChoice == 1) {
                        allowedUnitType = "2-Room";
                    } else if (flatChoice == 2) {
                        allowedUnitType = "3-Room";
                    } else {
                        System.out.println("Invalid flat type choice.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Application cancelled.");
                    return;
                }
            } else {
                System.out.println("As a Married applicant, you must be at least 21 years old.");
                return;
            }
        }
        
        // Create new application with an empty ID; the ApplicationHandler will generate a unique ID.
        Application application = new Application(applicant.getNric(), projectName, allowedUnitType);
        appFacade.submitApplication(application);
        System.out.println("Application submitted successfully.");
    }

    // Option 3: View Application Status (using ApplicantApplicationFeatures)
    private void viewApplicationStatus() {
        System.out.println("\n=== Application Status ===");
        List<Application> myApps = appFacade.getApplicationsForApplicant(applicant.getNric());
        if (myApps.isEmpty()) {
            System.out.println("You have not submitted any applications yet.");
        } else {
            for (Application app : myApps) {
                System.out.println(app);
            }
        }
    }

    // Option 4: Manage Enquiries (using ApplicantEnquiryFeatures)
    private void manageEnquiries() {
        while (true) {
            System.out.println("\n=== Enquiries Management ===");
            System.out.println("1. Submit New Enquiry");
            System.out.println("2. View My Enquiries");
            System.out.println("3. Edit Enquiry");
            System.out.println("4. Delete Enquiry");
            System.out.println("5. Back to Applicant Menu");
            System.out.print("Enter your choice: ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }
            switch (choice) {
                case 1:
                    submitEnquiry();
                    break;
                case 2:
                    viewEnquiries();
                    break;
                case 3:
                    editEnquiry();
                    break;
                case 4:
                    deleteEnquiry();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void submitEnquiry() {
        System.out.println("\n=== Submit New Enquiry ===");
        System.out.print("Enter the project name for your enquiry: ");
        String projectName = scanner.nextLine().trim();
        System.out.print("Enter your enquiry message: ");
        String message = scanner.nextLine().trim();
        // Create new Enquiry with an empty ID; the EnquiryHandler will generate a unique ID.
        models.Enquiry enquiry = new models.Enquiry("", applicant.getNric(), projectName, message, null, null, null, null);
        enquiryFacade.submitEnquiry(enquiry);
        System.out.println("Enquiry submitted successfully.");
    }

    private void viewEnquiries() {
        System.out.println("\n=== My Enquiries ===");
        List<models.Enquiry> enquiries = enquiryFacade.getEnquiriesForApplicant(applicant.getNric());
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
        } else {
            for (models.Enquiry enq : enquiries) {
                System.out.println(enq);
            }
        }
    }

    private void editEnquiry() {
        System.out.println("\n=== Edit Enquiry ===");
        System.out.print("Enter the Enquiry ID to edit: ");
        String enquiryId = scanner.nextLine().trim();
        System.out.print("Enter the new enquiry message: ");
        String newMessage = scanner.nextLine().trim();
        enquiryFacade.editEnquiry(enquiryId, newMessage);
        System.out.println("Enquiry updated successfully.");
    }

    private void deleteEnquiry() {
        System.out.println("\n=== Delete Enquiry ===");
        System.out.print("Enter the Enquiry ID to delete: ");
        String enquiryId = scanner.nextLine().trim();
        enquiryFacade.deleteEnquiry(enquiryId);
        System.out.println("Enquiry deleted successfully.");
    }

    // Option 5: Request Withdrawal (using ApplicantWithdrawalFeatures)
    private void requestWithdrawal() {
        System.out.println("\n=== Request Withdrawal ===");
        System.out.print("Enter your Application ID for withdrawal: ");
        String applicationId = scanner.nextLine().trim();
        System.out.print("Enter any remarks (optional): ");
        String remarks = scanner.nextLine().trim();
        // Create a new WithdrawalRequest with an empty ID; the WithdrawalHandler will assign one.
        WithdrawalRequest wdRequest = new WithdrawalRequest("", applicationId, applicant.getNric(), "", null, java.time.LocalDateTime.now(), null, remarks);
        withdrawalFacade.requestWithdrawal(wdRequest);
        System.out.println("Withdrawal request submitted successfully.");
    }

    // Option 6: Change Password (directly on the applicant object for demonstration)
    private void changePassword() {
        System.out.println("\n=== Change Password ===");
        System.out.print("Enter your current password: ");
        String current = scanner.nextLine().trim();
        if (!applicant.getPassword().equals(current)) {
            System.out.println("Incorrect current password.");
            return;
        }
        System.out.print("Enter your new password: ");
        String newPass = scanner.nextLine().trim();
        applicant.setPassword(newPass);
        System.out.println("Password changed successfully.");
    }
}

