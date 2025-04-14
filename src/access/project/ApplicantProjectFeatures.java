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
}
