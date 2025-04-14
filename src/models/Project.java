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
}

