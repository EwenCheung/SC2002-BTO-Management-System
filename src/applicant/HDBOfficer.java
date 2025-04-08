package applicant;

import model.Project;
import model.UserType;
import utils.FileUtils;
import model.Application;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class HDBOfficer extends Applicant{
    private final List<Project> handledProjects;
    private Scanner scanner;

    public HDBOfficer(String name, String nric, int age, String maritalStatus){
        super(nric, name, age, maritalStatus);
        this.setUserType(UserType.OFFICER);
        this.handledProjects = new ArrayList<>();
    }

    public void registerProject(Project project){
        handledProjects.add(project);
    }

    public List<Project> getHandledProjects(){
        return handledProjects;
    }

    public String generateReceipt(Application app){
        return app.generateReceipt();
    }

    public void flatselection(String applicantNric, String type){
        System.out.println("\n=== Flat Selection ===");

        if (handledProjects.isEmpty()) {
            System.out.println("You are not assigned to any projects.");
            return;
        }
    
        for (int i = 0; i < handledProjects.size(); i++) {
            System.out.printf("%d. %s (%s)%n", i + 1,
                    handledProjects.get(i).getProjectName(),
                    handledProjects.get(i).getNeighborhood());
        }
    
        System.out.print("Select a project: ");
        int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
    
        if (index < 0 || index >= handledProjects.size()) {
            System.out.println("Invalid selection.");
            return;
        }
    
        Project selected = handledProjects.get(index);
        if(selected.assignUnit(type, applicantNric)){
            updateApplicationStatus(type,applicantNric);
            }
        return;
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
