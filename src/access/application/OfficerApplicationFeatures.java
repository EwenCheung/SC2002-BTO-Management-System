package access.application;

import java.util.List;
import models.Application;

/**
 * Interface to define the operations on applications that an HDB Officer can perform.
 */
public interface OfficerApplicationFeatures {
    /**
     * Retrieves all applications for a given project.
     * @param projectName the name of the project.
     * @return a List of Application objects for that project.
     */
    List<Application> getApplicationsForProject(String projectName);
    
    /**
     * Processes an approved application and changes its status to booked.
     * @param applicationId the ID of the application.
     */
    void processApplication(String applicationId);
    
    /**
     * Generates a flat selection receipt for a booked application.
     * @param applicationId the ID of the application.
     * @return a receipt in String format.
     */
    String generateReceipt(String applicationId);
}
