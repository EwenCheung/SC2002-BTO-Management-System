package manager;

import model.Project;
import model.UnitType;
import utils.FileUtils;
import auth.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectManager {
    private static final String PROJECT_FILE = "ProjectList.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
    private Map<String, Project> projects;

    public ProjectManager() {
        this.projects = new HashMap<>();
        loadProjects();
    }

    private void loadProjects() {
        List<String[]> projectData = FileUtils.readFile(PROJECT_FILE);
        if (projectData.isEmpty()) {
            return;
        }

        // Skip header row
        for (int i = 1; i < projectData.size(); i++) {
            String[] data = projectData.get(i);
            if (data.length >= 13) { // Verify we have minimum required columns
                Project project = new Project(
                    data[0].trim(), // Project Name
                    data[1].trim(), // Neighborhood
                    LocalDate.parse(data[8].trim(), DATE_FORMATTER), // Opening Date
                    LocalDate.parse(data[9].trim(), DATE_FORMATTER), // Closing Date
                    data[10].trim(), // Manager
                    Integer.parseInt(data[11].trim()) // Officer Slots
                );

                // Add Type 1 units
                project.addUnitType(
                    data[2].trim(), // Type 1
                    Integer.parseInt(data[3].trim()), // Number of units
                    Double.parseDouble(data[4].trim()) // Price
                );

                // Add Type 2 units
                project.addUnitType(
                    data[5].trim(), // Type 2
                    Integer.parseInt(data[6].trim()), // Number of units
                    Double.parseDouble(data[7].trim()) // Price
                );

                // Set visibility
                if (data.length > 13) {
                    project.setVisible(Boolean.parseBoolean(data[13].trim()));
                }

                // Add assigned officers if any
                if (data[12] != null && !data[12].trim().isEmpty()) {
                    String[] officers = data[12].trim().split(";");
                    for (String officer : officers) {
                        project.addOfficer(officer.trim());
                    }
                }

                projects.put(project.getProjectName(), project);
            }
        }
    }

    private boolean saveProjects() {
        List<String[]> projectData = new ArrayList<>();
        
        // Add header
        projectData.add(new String[]{
            "Project Name", "Neighborhood", 
            "Type 1", "Number of units for Type 1", "Selling price for Type 1",
            "Type 2", "Number of units for Type 2", "Selling price for Type 2",
            "Application opening date", "Application closing date",
            "Manager", "Officer Slot", "Officer", "Visibility"
        });

        // Add project data
        for (Project project : projects.values()) {
            Map<String, UnitType> unitTypes = project.getUnitTypes();
            List<String> rowData = new ArrayList<>();
            
            // Basic project info
            rowData.add(project.getProjectName());
            rowData.add(project.getNeighborhood());

            // Unit type information
            List<UnitType> types = new ArrayList<>(unitTypes.values());
            if (types.size() >= 2) {
                UnitType type1 = types.get(0);
                UnitType type2 = types.get(1);

                rowData.add(type1.getType());
                rowData.add(String.valueOf(type1.getTotalUnits()));
                rowData.add(String.valueOf(type1.getPrice()));
                rowData.add(type2.getType());
                rowData.add(String.valueOf(type2.getTotalUnits()));
                rowData.add(String.valueOf(type2.getPrice()));
            }

            // Dates and management
            rowData.add(project.getOpeningDate().format(DATE_FORMATTER));
            rowData.add(project.getClosingDate().format(DATE_FORMATTER));
            rowData.add(project.getAssignedManager());
            rowData.add(String.valueOf(project.getOfficerSlots()));

            // Officers (joined with semicolons)
            rowData.add(String.join(";", project.getAssignedOfficers()));

            // Visibility
            rowData.add(String.valueOf(project.isVisible()));

            projectData.add(rowData.toArray(new String[0]));
        }

        return FileUtils.writeFile(PROJECT_FILE, projectData);
    }

    public List<Project> getAllProjects() {
        return new ArrayList<>(projects.values());
    }

    public List<Project> getVisibleProjects(User user) {
        List<Project> visibleProjects = new ArrayList<>();
        for (Project project : projects.values()) {
            // HDB Officers can see their assigned projects regardless of visibility
            boolean isAssignedOfficer = project.getAssignedOfficers().contains(user.getNric());
            // HDB Managers can see all projects
            boolean isManager = project.getAssignedManager().equals(user.getNric());
            
            if (isManager || isAssignedOfficer || project.isVisible()) {
                visibleProjects.add(project);
            }
        }
        return visibleProjects;
    }

    public List<Project> getEligibleProjects(User user) {
        List<Project> eligibleProjects = new ArrayList<>();
        boolean isSingle = user.getMaritalStatus().equalsIgnoreCase("Single");
        int age = user.getAge();
        
        for (Project project : projects.values()) {
            if (!project.isVisible()) continue;
            
            // Check age eligibility
            if ((isSingle && age < 35) || (!isSingle && age < 21)) {
                continue;
            }

            // Singles can only apply for 2-Room
            if (isSingle) {
                Map<String, UnitType> unitTypes = project.getUnitTypes();
                if (!unitTypes.containsKey("2-Room")) {
                    continue;
                }
            }
            
            eligibleProjects.add(project);
        }
        return eligibleProjects;
    }

    public Project getProject(String projectName) {
        return projects.get(projectName);
    }

    public boolean addProject(Project project) {
        if (projects.containsKey(project.getProjectName())) {
            return false;
        }
        projects.put(project.getProjectName(), project);
        return saveProjects();
    }

    public boolean updateProject(Project project) {
        if (!projects.containsKey(project.getProjectName())) {
            return false;
        }
        projects.put(project.getProjectName(), project);
        return saveProjects();
    }

    public boolean removeProject(String projectName) {
        if (projects.remove(projectName) != null) {
            return saveProjects();
        }
        return false;
    }

    public List<Project> filterProjects(String neighborhood, String flatType, boolean sortByName) {
        List<Project> filteredProjects = new ArrayList<>();
        
        for (Project project : projects.values()) {
            // Filter by neighborhood if specified
            if (neighborhood != null && !neighborhood.isEmpty() && 
                !project.getNeighborhood().equalsIgnoreCase(neighborhood)) {
                continue;
            }
            
            // Filter by flat type if specified
            if (flatType != null && !flatType.isEmpty()) {
                Map<String, UnitType> unitTypes = project.getUnitTypes();
                if (!unitTypes.containsKey(flatType)) {
                    continue;
                }
            }
            
            filteredProjects.add(project);
        }
        
        // Sort by name if requested
        if (sortByName) {
            filteredProjects.sort((p1, p2) -> 
                p1.getProjectName().compareToIgnoreCase(p2.getProjectName()));
        }
        
        return filteredProjects;
    }
}
