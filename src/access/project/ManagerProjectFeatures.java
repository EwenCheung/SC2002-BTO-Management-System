package access.project;

import java.util.List;

import models.Project;

public interface ManagerProjectFeatures {    
    /**
     * Creates a new project.
     * @param project the project to create.
     */
    void addProject(Project project);
    
    /**
     * Updates an existing project.
     * @param project the project with updated information.
     */
    void updateProject(Project project);
    
    /**
     * Deletes a project by name.
     * @param projectName the name of the project to delete.
     */
    void deleteProject(String projectName);
    
    /**
     * Toggles the visibility of a project.
     * @param projectName the name of the project.
     * @param visible true for visible, false for hidden.
     */
    void toggleVisibility(String projectName, boolean visible);
    
    /**
     * Retrieves projects created by a specified manager.
     * @param manager the manager identifier.
     * @return a list of projects created by that manager.
     */
    List<Project> getProjectsByManager(String manager);
    
    /**
     * Retrieves all projects (regardless of visibility) for review.
     * @return a list of all projects.
     */
    List<Project> getAllProjects();
    
    /**
     * Assigns an officer to a project.
     * @param projectName the name of the project.
     * @param officerNric the NRIC of the officer.
     */
    void assignOfficer(String projectName, String officerNric);

    /**
     * Retrieves a project by its unique project name.
     * @param projectName the name of the project.
     * @return the Project object, or null if not found.
     */
    Project getProject(String projectName);
}
