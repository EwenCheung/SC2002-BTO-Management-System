package access.application;

import java.util.List;
import models.Application;

/**
 * Interface to define the operations on applications that a Manager can perform.
 */
public interface ManagerApplicationFeatures {
    /**
     * Retrieves all applications.
     * @return a List of all Application objects.
     */
    List<Application> getAllApplications();
    
    /**
     * Approves an application by its unique ID.
     * @param applicationId the ID of the application.
     */
    void approveApplication(String applicationId);
    
    /**
     * Rejects an application by its unique ID.
     * @param applicationId the ID of the application.
     */
    void rejectApplication(String applicationId);

    /**
     * Retrieves all applications for a specific project.
     * @param projectName the name of the project.
     * @return a List of Application objects for the specified project.
     */
    List<Application> getApplicationsByProject(String projectName);
    
    /**
     * Retrieves all pending applications for a specific project.
     * @param projectName the name of the project.
     * @return a List of pending Application objects for the specified project.
     */
    List<Application> getPendingApplicationsByProject(String projectName);
}
