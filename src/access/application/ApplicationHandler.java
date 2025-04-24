package access.application;

import java.util.List;
import java.util.ArrayList;
import models.Application;
import models.enums.ApplicationStatus;
import io.FileIO;

/**
 * Handles all application-related operations in the BTO Management System.
 * Implements interfaces for managers, officers, and applicants to provide
 * appropriate access control to application data and operations based on user roles.
 */
public class ApplicationHandler implements ManagerApplicationFeatures, OfficerApplicationFeatures, ApplicantApplicationFeatures {
    
    /** The in-memory list of all applications in the system */
    private List<Application> applications;
    
    /**
     * Constructs an ApplicationHandler with the given list of applications.
     *
     * @param applications The list of applications to manage
     */
    public ApplicationHandler(List<Application> applications) {
        this.applications = applications;
    }
    
    // Manager methods...
    /**
     * Returns all applications in the system.
     * Available to managers for oversight and reporting purposes.
     *
     * @return A list of all Application objects
     */
    @Override
    public List<Application> getAllApplications() {
        return applications;
    }
    
    /**
     * Approves an application by changing its status to SUCCESSFUL.
     * Available to managers as part of the application approval process.
     *
     * @param applicationId The ID of the application to approve
     * @throws IllegalArgumentException if application is not found
     */
    @Override
    public void approveApplication(String applicationId) {
        Application app = findApplicationById(applicationId);
        if (app == null) {
            throw new IllegalArgumentException("Application not found: " + applicationId);
        }
        app.setStatus(ApplicationStatus.SUCCESSFUL);
        saveChanges(); // Add this line to save changes to CSV
    }
    
    /**
     * Rejects an application by changing its status to UNSUCCESSFUL.
     * Available to managers as part of the application approval process.
     *
     * @param applicationId The ID of the application to reject
     * @throws IllegalArgumentException if application is not found
     */
    @Override
    public void rejectApplication(String applicationId) {
        Application app = findApplicationById(applicationId);
        if (app == null) {
            throw new IllegalArgumentException("Application not found: " + applicationId);
        }
        app.setStatus(ApplicationStatus.UNSUCCESSFUL);
        saveChanges(); // Add this line to save changes to CSV
    }

    /**
     * Returns all applications for a specific project.
     * Available to managers for project-specific oversight.
     *
     * @param projectName The name of the project
     * @return A list of Application objects for the specified project
     */
    @Override
    public List<Application> getApplicationsByProject(String projectName) {
        List<Application> result = new ArrayList<>();
        for (Application app : applications) {
            if (app.getProjectName().equalsIgnoreCase(projectName)) {
                result.add(app);
            }
        }
        return result;
    }

    /**
     * Returns pending applications for a specific project.
     * Available to managers to identify applications that require approval.
     *
     * @param projectName The name of the project
     * @return A list of pending Application objects for the specified project
     */
    @Override
    public List<Application> getPendingApplicationsByProject(String projectName) {
        List<Application> result = new ArrayList<>();
        for (Application app : applications) {
            // Compare project names (ignoring case) and check if status is pending.
            if (app.getProjectName().equalsIgnoreCase(projectName) &&
                app.getStatus() == ApplicationStatus.PENDING) {
                result.add(app);
            }
        }
        return result;
    }
    
    // Officer methods...
    /**
     * Returns all applications for a specific project.
     * Available to officers to view applications for their assigned projects.
     *
     * @param projectName The name of the project
     * @return A list of Application objects for the specified project
     */
    @Override
    public List<Application> getApplicationsForProject(String projectName) {
        List<Application> result = new ArrayList<>();
        for (Application app : applications) {
            if (app.getProjectName().equalsIgnoreCase(projectName)) {
                result.add(app);
            }
        }
        return result;
    }
    
    /**
     * Returns a specific application by its ID.
     * Available to officers to view detailed application information.
     *
     * @param applicationId The ID of the application
     * @return The Application object with the specified ID, or null if not found
     */
    @Override
    public Application getApplication(String applicationId) {
        return findApplicationById(applicationId);
    }
    
    /**
     * Processes an approved application by changing its status to BOOKED.
     * Available to officers for completing the post-approval booking process.
     *
     * @param applicationId The ID of the application to process
     * @throws IllegalArgumentException if application is not found or not in SUCCESSFUL status
     */
    @Override
    public void processApplication(String applicationId) {
        Application app = findApplicationById(applicationId);
        if (app == null) {
            throw new IllegalArgumentException("Application not found: " + applicationId);
        }
        if (app.getStatus() != ApplicationStatus.SUCCESSFUL) {
            throw new IllegalArgumentException("Only applications with 'Successful' status can be processed to 'Booked'.");
        }
        app.setStatus(ApplicationStatus.BOOKED);
        saveChanges(); // Ensure changes are saved to CSV
    }
    
    /**
     * Generates a receipt for a booked application.
     * Available to officers to provide booking confirmation to applicants.
     *
     * @param applicationId The ID of the application
     * @return A string containing the receipt information
     * @throws IllegalArgumentException if application is not found or not in BOOKED status
     */
    @Override
    public String generateReceipt(String applicationId) {
        Application app = findApplicationById(applicationId);
        if (app == null) {
            throw new IllegalArgumentException("Application not found: " + applicationId);
        }
        if (app.getStatus() != ApplicationStatus.BOOKED) {
            throw new IllegalArgumentException("Receipt can only be generated for applications with 'Booked' status.");
        }
        return app.generateReceipt();
    }
    
    // Applicant methods...
    /**
     * Submits a new application to the system.
     * Available to applicants to apply for BTO projects.
     *
     * @param application The application to submit
     */
    @Override
    public void submitApplication(Application application) {
        // Generate a unique ID if the application does not yet have one.
        if (application.getApplicationId() == null || application.getApplicationId().isEmpty()) {
            application.setApplicationId(generateUniqueId("APP"));
        }
        applications.add(application);
        saveChanges(); // Save to CSV after adding
    }
    
    /**
     * Returns all applications submitted by a specific applicant.
     * Available to applicants to view their own application history.
     *
     * @param applicantNric The NRIC of the applicant
     * @return A list of Application objects submitted by the specified applicant
     */
    @Override
    public List<Application> getApplicationsForApplicant(String applicantNric) {
        List<Application> result = new ArrayList<>();
        for (Application app : applications) {
            if (app.getApplicantNric().equalsIgnoreCase(applicantNric)) {
                result.add(app);
            }
        }
        return result;
    }
    
    /**
     * Saves current application data to persistent storage.
     * Called after operations that modify application data.
     */
    public void saveChanges() {
        FileIO.saveApplications(applications);
    }
    
    /**
     * Updates an existing application with new data.
     * Available to officers and managers to update application details.
     *
     * @param application The application with updated information
     * @throws IllegalArgumentException if application is not found
     */
    @Override
    public void updateApplication(Application application) {
        Application existingApp = findApplicationById(application.getApplicationId());
        if (existingApp == null) {
            throw new IllegalArgumentException("Application not found: " + application.getApplicationId());
        }
        
        // Update the application in the list
        int index = applications.indexOf(existingApp);
        applications.set(index, application);
        
        // Save changes to CSV
        saveChanges();
    }
    
    /**
     * Finds an application by its ID.
     * Helper method used by various public methods that require finding specific applications.
     *
     * @param applicationId The ID of the application to find
     * @return The Application object with the specified ID, or null if not found
     */
    private Application findApplicationById(String applicationId) {
        for (Application app : applications) {
            if (app.getApplicationId().equalsIgnoreCase(applicationId)) {
                return app;
            }
        }
        return null;
    }
    
    /**
     * Generates a unique ID for a new application.
     * Creates IDs in the format: [prefix]-[timestamp] to ensure uniqueness.
     *
     * @param prefix The prefix to use for the ID (e.g., "APP")
     * @return A unique ID string
     */
    private String generateUniqueId(String prefix) {
        // Format: 3-letter prefix, a hyphen, followed by a unique numeric value.
        return prefix + "-" + System.currentTimeMillis();
    }
}