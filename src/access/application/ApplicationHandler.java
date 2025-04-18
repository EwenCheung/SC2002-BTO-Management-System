package access.application;

import java.util.List;
import java.util.ArrayList;
import models.Application;
import models.enums.ApplicationStatus;
import io.FileIO;

public class ApplicationHandler implements ManagerApplicationFeatures, OfficerApplicationFeatures, ApplicantApplicationFeatures {
    
    private List<Application> applications;
    
    public ApplicationHandler(List<Application> applications) {
        this.applications = applications;
    }
    
    // Manager methods...
    @Override
    public List<Application> getAllApplications() {
        return applications;
    }
    
    @Override
    public void approveApplication(String applicationId) {
        Application app = findApplicationById(applicationId);
        if (app == null) {
            throw new IllegalArgumentException("Application not found: " + applicationId);
        }
        app.setStatus(ApplicationStatus.APPROVED);
        saveChanges(); // Add this line to save changes to CSV
    }
    
    @Override
    public void rejectApplication(String applicationId) {
        Application app = findApplicationById(applicationId);
        if (app == null) {
            throw new IllegalArgumentException("Application not found: " + applicationId);
        }
        app.setStatus(ApplicationStatus.REJECTED);
        saveChanges(); // Add this line to save changes to CSV
    }

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
    
    @Override
    public Application getApplication(String applicationId) {
        return findApplicationById(applicationId);
    }
    
    @Override
    public void processApplication(String applicationId) {
        Application app = findApplicationById(applicationId);
        if (app == null) {
            throw new IllegalArgumentException("Application not found: " + applicationId);
        }
        if (app.getStatus() != ApplicationStatus.APPROVED) {
            throw new IllegalArgumentException("Only applications with 'Successful' status can be processed to 'Booked'.");
        }
        app.setStatus(ApplicationStatus.BOOKED);
        saveChanges(); // Ensure changes are saved to CSV
    }
    
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
    @Override
    public void submitApplication(Application application) {
        // Generate a unique ID if the application does not yet have one.
        if (application.getApplicationId() == null || application.getApplicationId().isEmpty()) {
            application.setApplicationId(generateUniqueId("APP"));
        }
        applications.add(application);
        saveChanges(); // Save to CSV after adding
    }
    
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
    
    public void saveChanges() {
        FileIO.saveApplications(applications);
    }
    
    /**
     * Updates an existing application with new data.
     * @param application The application with updated information.
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
    
    private Application findApplicationById(String applicationId) {
        for (Application app : applications) {
            if (app.getApplicationId().equalsIgnoreCase(applicationId)) {
                return app;
            }
        }
        return null;
    }
    
    // --- Unique ID Generator ---
    private String generateUniqueId(String prefix) {
        // Format: 3-letter prefix, a hyphen, followed by a unique numeric value.
        return prefix + "-" + System.currentTimeMillis();
    }
}