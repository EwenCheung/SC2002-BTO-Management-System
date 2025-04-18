package access.project;

import java.util.List;
import models.Project;

/**
 * Interface that exposes project operations available for Applicants.
 */
public interface ApplicantProjectFeatures {
    
    /**
     * Retrieves only the projects that are visible.
     * @return a list of projects with visibility set to true.
     */
    List<Project> getVisibleProjects();
    
    /**
     * Retrieves projects that should be visible to a specific applicant.
     * Projects are visible if:
     * 1. They are marked as visible AND the current date is within the application period, OR
     * 2. The applicant has already applied to this project (regardless of visibility)
     * 
     * @param applicantNric The NRIC of the applicant
     * @param appliedProjectNames List of project names the applicant has already applied to
     * @return A list of projects that should be visible to this applicant
     */
    List<Project> getVisibleProjects(String applicantNric, List<String> appliedProjectNames);
}
