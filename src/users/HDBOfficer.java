package users;

import java.util.List;

import users.enums.MaritalStatus;
import users.enums.UserType;
import utils.FileUtils;

/**
 * Represents an HDB officer in the BTO Management System.
 * HDB officers have the ability to process applications, update application statuses,
 * and retrieve applicant information.
 */
public class HDBOfficer extends User{

    /**
     * Constructor for creating a new HDB Officer.
     * Sets the user type to OFFICER automatically.
     *
     * @param name           The officer's full name
     * @param nric           The officer's NRIC number
     * @param age            The officer's age in years
     * @param maritalStatus  The officer's marital status
     * @param password       The officer's login password
     */
    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus, String password){
        super(name, nric, age, maritalStatus, UserType.OFFICER, password);
    }

    /**
     * Retrieves and displays details of an applicant based on NRIC.
     * Searches through application records to find matching applicant information.
     *
     * @param nric The NRIC of the applicant to retrieve details for
     */
    public void retrieveApplicantDetails(String nric){
        System.out.println("\n=== Retrieve Applicant Details ===");
        List<String[]> applications = FileUtils.readFile("ApplicationList.txt");

        for (String[] row : applications) {
            if (row.length < 5) continue;
            if (row[1].equals(nric)) {
                System.out.println("Applicant: " + row[0]);
                System.out.println("Project: " + row[2]);
                System.out.println("Flat Type: " + row[3]);
                System.out.println("Status: " + row[4]);
                return;
            }
        }

        System.out.println("Applicant not found or has not applied.");
    }
    
    /**
     * Updates an application status to "Booked" for a specified applicant.
     * Only applications with "Successful" status can be updated to "Booked".
     *
     * @param type The unit type being booked
     * @param nric The NRIC of the applicant booking the unit
     */
    public void updateApplicationStatus(String type, String nric){
        System.out.println("\n=== Book Flat and Update Application ===");
    
        List<String[]> applications = FileUtils.readFile("ApplicationList.txt");
        boolean updated = false;
    
        for (String[] row : applications) {
            if (row.length < 5) continue;
            if (row[1].equals(nric) && row[4].equalsIgnoreCase("Successful")) {
                row[3] = type;
                row[4] = "Booked";
                updated = true;
                break;
            }
        }
    
        if (updated && FileUtils.writeFile("ApplicationList.txt", applications)) {
            System.out.println("Flat successfully booked.");
        } else {
            System.out.println("Booking failed. Applicant must have 'Successful' status.");
        }
    }
}
