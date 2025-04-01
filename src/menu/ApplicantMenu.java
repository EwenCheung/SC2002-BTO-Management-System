package menu;

import auth.User;
import auth.AuthenticationSystem;
import java.util.Scanner;

public class ApplicantMenu {
    private Scanner scanner;
    private User user;
    private AuthenticationSystem authSystem;

    public ApplicantMenu(User user) {
        this.scanner = new Scanner(System.in);
        this.user = user;
        this.authSystem = new AuthenticationSystem();
    }

    public void display() {
        // Managers cannot access BTO application features
        if (authSystem.isManager(user.getNric())) {
            System.out.println("HDB Managers are not allowed to apply for BTO projects.");
            return;
        }

        while (true) {
            System.out.println("\n=== Applicant Menu ===");
            System.out.println("Welcome, " + user.getName());
            System.out.println("1. Browse Projects");
            System.out.println("2. Submit Application");
            System.out.println("3. View Application Status");
            System.out.println("4. Submit/View Enquiries");
            System.out.println("5. Request Withdrawal");
            System.out.println("6. Change Password");
            System.out.println("7. Logout");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        System.out.println("\nFeature to be implemented: Browse Projects");
                        // Will show projects based on marital status and visibility
                        break;
                    case 2:
                        System.out.println("\nFeature to be implemented: Submit Application");
                        // Will handle BTO application submission
                        break;
                    case 3:
                        System.out.println("\nFeature to be implemented: View Application Status");
                        // Will show application status (Pending/Successful/Unsuccessful/Booked)
                        break;
                    case 4:
                        manageEnquiries();
                        break;
                    case 5:
                        System.out.println("\nFeature to be implemented: Request Withdrawal");
                        // Will handle application withdrawal requests
                        break;
                    case 6:
                        System.out.println("\nFeature to be implemented: Change Password");
                        // Will handle password changes
                        break;
                    case 7:
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

    private void manageEnquiries() {
        while (true) {
            System.out.println("\n=== Enquiries Management ===");
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
