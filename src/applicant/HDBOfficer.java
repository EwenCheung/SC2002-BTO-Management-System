package applicant;

import java.io.ObjectInputFilter;
import java.net.http.HttpResponse;
import model.Project;
import model.UserType;
import model.Application;
import java.util.ArrayList;
import java.util.List;

import model.Application.Status;

public class HDBOfficer extends Applicant{
    private final List<Project> handledProjects;

    public HDBOfficer(String nric, String name, int age, String maritalStatus){
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

    // Set Application status to booked and decrease flat count
    public boolean bookFlat(Application app, String unitType, Project project){
        if(Status.APPROVED.equals(app.getStatus())){
            System.out.println("Applicant must be approved before booking.");
            return false;
        }
        if(!project.hasAvailableUnits(unitType)){
            System.out.println("No units available for selected type.");
            return false;
        }

        app.setStatus(Status.BOOKED);
        project.decreaseFlatCount(unitType);
        return true;
    }

    public String generateReceipt(Application app){
        return app.generateReceipt();
    }


}
