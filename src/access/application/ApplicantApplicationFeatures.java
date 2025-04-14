package access.application;

import java.util.List;
import models.Application;

/**
 * Interface to define the operations on applications that an Applicant can perform.
 */
public interface ApplicantApplicationFeatures {
    /**
     * Submits a new application.
     * @param application the Application to submit.
     */
    void submitApplication(Application application);
    
    /**
     * Retrieves applications submitted by the applicant.
     * @param applicantNric the applicant's NRIC.
     * @return a List of Application objects.
     */
    List<Application> getApplicationsForApplicant(String applicantNric);
}