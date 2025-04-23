package access.project;

import java.util.List;
import models.Project;

/**
 * Interface that defines the project-related operations available to HDB Officers.
 * Provides functionality for officers to retrieve assigned projects and manage 
 * unit availability for projects they are responsible for.
 */
public interface OfficerProjectFeatures {
    
    /**
     * Retrieves the projects to which the officer is assigned.
     * @param officerNric the officer's NRIC.
     * @return a list of assigned projects.
     */
    List<Project> getProjectsForOfficer(String officerNric);
    
    /**
     * Decreases the number of available units for a given unit type.
     * For example, after processing an approved application to booking.
     * @param projectName the name of the project.
     * @param unitType the type of unit (e.g., "2-Room", "3-Room").
     * @param count the number of units to decrease.
     */
    void decreaseAvailableUnits(String projectName, String unitType, int count);
}