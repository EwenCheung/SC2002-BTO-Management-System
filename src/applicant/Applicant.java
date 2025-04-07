package applicant;
import java.util.ArrayList;
import java.util.List;
import model.Application;
import model.Application.Status;
import model.Enquiry;
import model.User;
import model.UserType;

public class Applicant extends User{
    private Application application;
    private final List<Enquiry> enquiries;

    public Applicant(String nric, String name, int age, String maritalStatus){
        super(nric, name, age, maritalStatus, UserType.APPLICANT);
        this.enquiries= new ArrayList<>();
    }

    public Application geApplication(){
        return application;
    }

    public List<Enquiry> getEnquiries(){
        return enquiries;
    }

    public void apply(Application application){
        this.application=application;
    }

    public void withdrawApplication(){
        if(application != null){
            application.setStatus(Status.WITHDRAW_REQUESTED);
        }
    }
    
    public void addEnquiry(Enquiry e){
        enquiries.add(e);
    }
}
