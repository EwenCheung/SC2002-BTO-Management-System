package users;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import models.Application;
import models.Project;
import users.enums.MaritalStatus;
import users.enums.UserType;
import utils.FileUtils;


public class HDBOfficer extends User{

    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus, String password){
        super(name, nric, age, maritalStatus, UserType.OFFICER, password);
    }



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
//update Application status to book in the txt file
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
