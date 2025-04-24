package access.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.FileIO;
import models.Project;
import models.UnitInfo;

/**
 * Handler class that implements all project-related features for different user roles.
 * This class serves as the central implementation for project management, providing
 * functionality for managers, officers, and applicants.
 */
public class ProjectHandler implements ManagerProjectFeatures, OfficerProjectFeatures, ApplicantProjectFeatures {
    private List<Project> projects;
    
    /**
     * Constructs a ProjectHandler with the provided list of projects.
     * 
     * @param projects the list of projects to be managed by this handler.
     */
    public ProjectHandler(List<Project> projects) {
        this.projects = projects;
    }
    
    // ---- ManagerProjectFeatures methods ----
    /**
     * {@inheritDoc}
     */
    @Override
    public void addProject(Project project) {
        projects.add(project);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Project getProject(String projectName) {
        return getProjectByName(projectName);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateProject(Project updatedProject) {
        boolean found = false;
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getProjectName().equalsIgnoreCase(updatedProject.getProjectName())) {
                projects.set(i, updatedProject);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Project not found: " + updatedProject.getProjectName());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProject(String projectName) {
        projects.removeIf(project -> project.getProjectName().equalsIgnoreCase(projectName));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void toggleVisibility(String projectName, boolean visible) {
        Project p = getProjectByName(projectName);
        if (p != null) {
            p.setVisible(visible);
        } else {
            throw new IllegalArgumentException("Project not found: " + projectName);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Project> getProjectsByManager(String manager) {
        List<Project> result = new ArrayList<>();
        for (Project p : projects) {
            if (p.getManager().equalsIgnoreCase(manager)) {
                result.add(p);
            }
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Project> getAllProjects() {
        return projects;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void assignOfficer(String projectName, String officerNric) {
        Project p = getProjectByName(projectName);
        if (p != null) {
            p.addOfficer(officerNric);
        } else {
            throw new IllegalArgumentException("Project not found: " + projectName);
        }
    }
    
    // ---- OfficerProjectFeatures methods ----
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Project> getProjectsForOfficer(String officerNric) {
        List<Project> result = new ArrayList<>();
        for (Project p : projects) {
            if (p.getOfficers().contains(officerNric)) {
                result.add(p);
            }
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void decreaseAvailableUnits(String projectName, String unitType, int count) {
        Project project = getProjectByName(projectName);
        if (project != null) {
            Map<String, UnitInfo> units = project.getUnits();
            if (units.containsKey(unitType)) {
                UnitInfo info = units.get(unitType);
                int available = info.getAvailableUnits();
                if (available >= count) {
                    info.setAvailableUnits(available - count);
                    saveChanges(); // Save changes to ProjectList.csv
                } else {
                    throw new IllegalArgumentException("Not enough available units for " + unitType);
                }
            } else {
                throw new IllegalArgumentException("Unit type not found in project: " + unitType);
            }
        } else {
            throw new IllegalArgumentException("Project not found: " + projectName);
        }
    }
    
    /**
     * Updates visibility of all projects based on application date range.
     * Projects are automatically set to invisible if the current date is outside their application period,
     * which means either:
     * 1. The current date is before the application opening date, or
     * 2. The current date is after the application closing date.
     * 
     * This method is designed to be called before displaying projects to users to ensure
     * that project visibility is always up-to-date with respect to the current date.
     * 
     * Only projects that are currently marked as visible will be checked and potentially updated.
     * If any project's visibility is changed, the changes will be saved to the persistent storage.
     * 
     * @see Project#isVisible()
     * @see Project#setVisible(boolean)
     * @see #saveChanges()
     */
    public void updateVisibilityBasedOnDate() {
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        boolean changesNeeded = false;
        
        for (Project p : projects) {
            // Only update projects that are visible
            if (p.isVisible()) {
                // Check if the application period has closed or not started yet
                boolean outsideDateRange = currentDate.isBefore(p.getApplicationOpeningDate()) || 
                                         currentDate.isAfter(p.getApplicationClosingDate());
                
                if (outsideDateRange) {
                    p.setVisible(false);
                    changesNeeded = true;
                }
            }
        }
        
        // Save changes to file if any project's visibility was updated
        if (changesNeeded) {
            saveChanges();
        }
    }
    
    /**
     * Gets all projects that have open officer slots and are available for registration
     * @return a list of projects with available officer slots
     */
    public List<Project> getProjectsWithOpenSlots() {
        List<Project> availableProjects = new ArrayList<>();
        for (Project p : projects) {
            if (p.getRemainingOfficerSlots() > 0) {
                availableProjects.add(p);
            }
        }
        return availableProjects;
    }
    
    // ---- ApplicantProjectFeatures methods ----
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Project> getVisibleProjects() {
        List<Project> visibleProjects = new ArrayList<>();
        for (Project p : projects) {
            if (p.isVisible()) {
                visibleProjects.add(p);
            }
        }
        return visibleProjects;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Project> getVisibleProjects(String applicantNric, List<String> appliedProjectNames) {
        List<Project> visibleProjects = new ArrayList<>();
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        
        for (Project p : projects) {
            // Project is visible if:
            // 1. It's marked as visible AND hasn't reached closing date yet (includes upcoming projects)
            // OR
            // 2. The applicant has already applied to this project (regardless of visibility)
            if ((p.isVisible() && !currentDate.isAfter(p.getApplicationClosingDate())) || 
                (appliedProjectNames != null && appliedProjectNames.contains(p.getProjectName()))) {
                visibleProjects.add(p);
            }
        }
        return visibleProjects;
    }
    
    /**
     * Helper method to find a project by its name (case insensitive).
     * 
     * @param projectName the name of the project to find.
     * @return the Project object if found, null otherwise.
     */
    private Project getProjectByName(String projectName) {
        for (Project p : projects) {
            if (p.getProjectName().equalsIgnoreCase(projectName)) {
                return p;
            }
        }
        return null;
    }
    
    /**
     * Saves all changes made to the projects list to the data file.
     */
    public void saveChanges() {
        FileIO.saveProjects(projects);
    }
}
