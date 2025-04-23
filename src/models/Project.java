package models;

import java.time.LocalDate;
import java.util.*;

/**
 * Represents a BTO housing project in the system.
 * Contains all information about a project including its name, neighborhood,
 * application period, manager, officers, and available unit types.
 */
public class Project {
    private String projectName;
    private String neighborhood;
    private LocalDate applicationOpeningDate;
    private LocalDate applicationClosingDate;
    private String manager;
    private int officerSlot;
    private List<String> officers;
    private boolean visible;
    private Map<String, UnitInfo> units; // Mapping unit types to their unit info

    /**
     * Constructor for creating a new Project.
     * Initializes the project with basic details and empty lists for officers and units.
     *
     * @param projectName             The name of the BTO project
     * @param neighborhood            The neighborhood where the project is located
     * @param applicationOpeningDate  The date when applications open for this project
     * @param applicationClosingDate  The date when applications close for this project
     * @param manager                 The identifier of the project manager in charge
     * @param officerSlot             The number of HDB officers that can be assigned to this project
     */
    public Project(String projectName, String neighborhood, LocalDate applicationOpeningDate, 
                   LocalDate applicationClosingDate, String manager, int officerSlot) {
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.applicationOpeningDate = applicationOpeningDate;
        this.applicationClosingDate = applicationClosingDate;
        this.manager = manager;
        this.officerSlot = officerSlot;
        this.officers = new ArrayList<>();
        this.visible = true;
        this.units = new HashMap<>();
    }

    // Getter methods
    /**
     * Gets the name of the project.
     * 
     * @return The project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Gets the neighborhood where the project is located.
     * 
     * @return The neighborhood name
     */
    public String getNeighborhood() {
        return neighborhood;
    }

    /**
     * Gets the date when applications open for this project.
     * 
     * @return The application opening date
     */
    public LocalDate getApplicationOpeningDate() {
        return applicationOpeningDate;
    }

    /**
     * Gets the date when applications close for this project.
     * 
     * @return The application closing date
     */
    public LocalDate getApplicationClosingDate() {
        return applicationClosingDate;
    }

    /**
     * Gets the project manager's identifier.
     * 
     * @return The manager's identifier
     */
    public String getManager() {
        return manager;
    }

    /**
     * Gets the total number of officer slots for this project.
     * 
     * @return The total number of officer slots
     */
    public int getOfficerSlot() {
        return officerSlot;
    }

    /**
     * Gets the list of officers assigned to this project.
     * 
     * @return List of officer identifiers
     */
    public List<String> getOfficers() {
        return officers;
    }

    /**
     * Checks if this project is currently visible to applicants.
     * 
     * @return true if the project is visible, false otherwise
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Gets the map of unit types and their associated information.
     * 
     * @return Map of unit types to UnitInfo objects
     */
    public Map<String, UnitInfo> getUnits() {
        return units;
    }

    // Setter methods
    /**
     * Sets the visibility of this project.
     * 
     * @param visible true to make the project visible to applicants, false to hide it
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Adds a new unit type to the project. The initial available units are set equal to total units.
     * 
     * @param type The unit type (e.g. "2-Room" or "3-Room")
     * @param totalUnits Total number of units
     * @param sellingPrice Selling price for this type
     */
    public void addUnitType(String type, int totalUnits, double sellingPrice) {
        this.units.put(type, new UnitInfo(totalUnits, totalUnits, sellingPrice));
    }
    
    /**
     * Sets the number of available units for a given unit type.
     * 
     * @param type The unit type
     * @param availableUnits The available number of units
     */
    public void setAvailableUnits(String type, int availableUnits) {
        if (this.units.containsKey(type)) {
            UnitInfo info = this.units.get(type);
            info.setAvailableUnits(availableUnits);
        }
    }
    
    /**
     * Adds an officer to the project.
     * 
     * @param officer The officer's identifier
     */
    public void addOfficer(String officer) {
        this.officers.add(officer);
    }

    /**
     * Updates the application opening date.
     * 
     * @param openingDate The updated opening date
     */
    public void setOpeningDate(LocalDate openingDate){
        this.applicationOpeningDate = openingDate;
    }

    /**
     * Updates the application closing date.
     * 
     * @param closingDate The updated closing date
     */
    public void setClosingDate(LocalDate closingDate){
        this.applicationClosingDate = closingDate;
    }

    /**
     * Updates the number of officer slots available.
     * 
     * @param officerSlot The updated number of officer slots
     */
     public void setOfficerSlots(int officerSlot){
        this.officerSlot = officerSlot;
     }

    /**
     * Gets the number of available units for a specified unit type.
     * 
     * @param type The unit type (e.g., "2-Room", "3-Room")
     * @return The number of available units, or 0 if the unit type doesn't exist
     */
    public int getAvailableUnits(String type) {
        if (this.units.containsKey(type)) {
            return this.units.get(type).getAvailableUnits();
        }
        return 0;
    }
    
    /**
     * Decrements the available units count for a specified unit type.
     * Used when a unit is assigned to an application.
     * 
     * @param type The unit type
     * @return true if units were successfully decremented, false if no units are available
     */
    public boolean decrementAvailableUnits(String type) {
        if (this.units.containsKey(type)) {
            UnitInfo info = this.units.get(type);
            if (info.getAvailableUnits() > 0) {
                info.setAvailableUnits(info.getAvailableUnits() - 1);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Increments the available units count for a specified unit type.
     * Used when applications are withdrawn or cancelled.
     * 
     * @param type The unit type
     * @return true if units were successfully incremented, false if max capacity reached
     */
    public boolean incrementAvailableUnits(String type) {
        if (this.units.containsKey(type)) {
            UnitInfo info = this.units.get(type);
            if (info.getAvailableUnits() < info.getTotalUnits()) {
                info.setAvailableUnits(info.getAvailableUnits() + 1);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if there are available slots for new HDB Officers.
     * 
     * @return true if there are available slots, false otherwise
     */
    public boolean hasAvailableOfficerSlots() {
        return officers.size() < officerSlot;
    }
    
    /**
     * Gets the number of remaining officer slots.
     * 
     * @return The number of remaining slots
     */
    public int getRemainingOfficerSlots() {
        return officerSlot - officers.size();
    }
    
    /**
     * Returns a string representation of this project with all relevant details.
     * 
     * @return A formatted string containing project information
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Project: ").append(projectName).append("\n");
        sb.append("Neighborhood: ").append(neighborhood).append("\n");
        sb.append("Application Period: ").append(applicationOpeningDate).append(" to ").append(applicationClosingDate).append("\n");
        sb.append("Manager: ").append(manager).append("\n");
        sb.append("Officer Slots: ").append(officers.size()).append("/").append(officerSlot).append("\n");
        sb.append("Visibility: ").append(visible ? "Visible" : "Hidden").append("\n");
        sb.append("Unit Types:\n");
        
        for (Map.Entry<String, UnitInfo> entry : units.entrySet()) {
            UnitInfo info = entry.getValue();
            sb.append("  - ").append(entry.getKey())
              .append(": ").append(info.getAvailableUnits()).append("/").append(info.getTotalUnits())
              .append(" units available, Price: $").append(String.format("%.2f", info.getSellingPrice())).append("\n");
        }
        
        return sb.toString();
    }
}

