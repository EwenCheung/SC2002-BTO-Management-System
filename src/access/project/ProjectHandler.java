package access.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.FileIO;
import models.Project;
import models.UnitInfo;

public class ProjectHandler implements ManagerProjectFeatures, OfficerProjectFeatures, ApplicantProjectFeatures {
    private List<Project> projects;
    
    // Constructor loads projects from file.
    public ProjectHandler(List<Project> projects) {
        this.projects = projects;
    }
    
    // ---- ManagerProjectFeatures methods ----
    @Override
    public void addProject(Project project) {
        projects.add(project);
    }

    @Override
    public Project getProject(String projectName) {
    return getProjectByName(projectName);
    }
    
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
    
    @Override
    public void deleteProject(String projectName) {
        projects.removeIf(project -> project.getProjectName().equalsIgnoreCase(projectName));
    }
    
    @Override
    public void toggleVisibility(String projectName, boolean visible) {
        Project p = getProjectByName(projectName);
        if (p != null) {
            p.setVisible(visible);
        } else {
            throw new IllegalArgumentException("Project not found: " + projectName);
        }
    }
    
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
    
    @Override
    public List<Project> getAllProjects() {
        return projects;
    }
    
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
    
    // ---- ApplicantProjectFeatures method ----
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
    
    // --- Internal helper ---
    private Project getProjectByName(String projectName) {
        for (Project p : projects) {
            if (p.getProjectName().equalsIgnoreCase(projectName)) {
                return p;
            }
        }
        return null;
    }
    
    // Optionally, a method to save changes.
    public void saveChanges() {
        FileIO.saveProjects(projects);
    }
}
