package access.application;

import java.util.List;
import models.Application;

/**
 * Interface defining application functionality available to HDB Officers.
 */
public interface OfficerApplicationFeatures {
    
    /**
     * Retrieves all applications for a specific project.
     * 
     * @param projectName The name of the project to get applications for
     * @return A list of applications for the given project
     */
    List<Application> getApplicationsForProject(String projectName);
    
    /**
     * Processes an application, setting its status.
     * 
     * @param applicationId The ID of the application to process
     */
    void processApplication(String applicationId);
    
    /**
     * Generates a booking receipt for an approved application.
     * 
     * @param applicationId The ID of the application to generate a receipt for
     * @return A string representation of the receipt
     */
    String generateReceipt(String applicationId);
    
    /**
     * Retrieves a specific application by its ID.
     * 
     * @param applicationId The ID of the application to retrieve
     * @return The Application object, or null if not found
     */
    Application getApplication(String applicationId);
    
    /**
     * Updates an existing application with new data.
     * 
     * @param application The application with updated information
     */
    void updateApplication(Application application);
}
