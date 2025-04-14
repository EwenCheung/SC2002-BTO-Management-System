package models;

import java.time.LocalDate;
import java.util.*;

public class Project {
    private String projectName;
    private String neighborhood;
    private LocalDate applicationOpeningDate;
    private LocalDate applicationClosingDate;
    private String manager;
    private int officerSlot;
    private List<String> officers;
    private boolean visible;
    private Map<String, UnitInfo> units; // New: mapping unit types to their unit info

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
    public String getProjectName() {
        return projectName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public LocalDate getApplicationOpeningDate() {
        return applicationOpeningDate;
    }

    public LocalDate getApplicationClosingDate() {
        return applicationClosingDate;
    }

    public String getManager() {
        return manager;
    }

    public int getOfficerSlot() {
        return officerSlot;
    }

    public List<String> getOfficers() {
        return officers;
    }

    public boolean isVisible() {
        return visible;
    }

    public Map<String, UnitInfo> getUnits() {
        return units;
    }

    // Setter methods
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Adds a new unit type to the project. The initial available units are set equal to total units.
     * @param type The unit type (e.g. "2-Room" or "3-Room").
     * @param totalUnits Total number of units.
     * @param sellingPrice Selling price for this type.
     */
    public void addUnitType(String type, int totalUnits, double sellingPrice) {
        this.units.put(type, new UnitInfo(totalUnits, totalUnits, sellingPrice));
    }
    
    /**
     * Sets the number of available units for a given unit type.
     * @param type The unit type.
     * @param availableUnits The available number of units.
     */
    public void setAvailableUnits(String type, int availableUnits) {
        if (this.units.containsKey(type)) {
            UnitInfo info = this.units.get(type);
            info.setAvailableUnits(availableUnits);
        }
    }
    
    /**
     * Adds an officer to the project.
     * @param officer The officer's identifier.
     */
    public void addOfficer(String officer) {
        this.officers.add(officer);
    }

    /**
     * adjust application opening date.
     * @param openingdate The updated opening date.
     */
    public void setOpeningDate(LocalDate openingDate){
        this.applicationOpeningDate = openingDate;
    }

    /**
     * adjust application closing date.
     * @param closingdate The updated closing date.
     */
    public void setClosingDate(LocalDate closingDate){
        this.applicationClosingDate = closingDate;
    }


    /**
     * adjust number of officer slots .
     * @param officerslots The updated officer slots.
     */

     public void setOfficerSlots(int officerSlot){
        this.officerSlot = officerSlot;
     }

    /**
     * Gets the number of available units for a specified unit type.
     * @param type The unit type (e.g., "2-Room", "3-Room").
     * @return The number of available units, or 0 if the unit type doesn't exist.
     */
    public int getAvailableUnits(String type) {
        if (this.units.containsKey(type)) {
            return this.units.get(type).getAvailableUnits();
        }
        return 0;
    }
    
    /**
     * Decrements the available units count for a specified unit type.
     * @param type The unit type.
     * @return true if units were successfully decremented, false if no units are available.
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
     * @param type The unit type.
     * @return true if units were successfully incremented, false if max capacity reached.
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
     * @return true if there are available slots, false otherwise.
     */
    public boolean hasAvailableOfficerSlots() {
        return officers.size() < officerSlot;
    }
    
    /**
     * Gets the number of remaining officer slots.
     * @return The number of remaining slots.
     */
    public int getRemainingOfficerSlots() {
        return officerSlot - officers.size();
    }
    
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

