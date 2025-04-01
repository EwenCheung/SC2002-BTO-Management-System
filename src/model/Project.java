package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Project {
    private String projectName;
    private String neighborhood;
    private Map<String, UnitType> unitTypes;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private String assignedManager;
    private int officerSlots;
    private List<String> assignedOfficers;
    private boolean isVisible;  // Added visibility field

    public Project(String projectName, String neighborhood, LocalDate openingDate, 
                  LocalDate closingDate, String assignedManager, int officerSlots) {
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.assignedManager = assignedManager;
        this.officerSlots = officerSlots;
        this.unitTypes = new HashMap<>();
        this.assignedOfficers = new ArrayList<>();
        this.isVisible = false;  // Projects are hidden by default
    }

    public void addUnitType(String type, int totalUnits, double price) {
        unitTypes.put(type, new UnitType(type, totalUnits, price));
    }

    public boolean addOfficer(String officerNric) {
        if (assignedOfficers.size() < officerSlots) {
            assignedOfficers.add(officerNric);
            return true;
        }
        return false;
    }

    public boolean removeOfficer(String officerNric) {
        return assignedOfficers.remove(officerNric);
    }

    public boolean hasAvailableUnits(String type) {
        UnitType unitType = unitTypes.get(type);
        return unitType != null && unitType.getAvailableUnits() > 0;
    }

    public boolean isApplicationOpen() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(openingDate) && !now.isAfter(closingDate);
    }

    // Getters
    public String getProjectName() {
        return projectName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public Map<String, UnitType> getUnitTypes() {
        return new HashMap<>(unitTypes);
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public String getAssignedManager() {
        return assignedManager;
    }

    public int getOfficerSlots() {
        return officerSlots;
    }

    public List<String> getAssignedOfficers() {
        return new ArrayList<>(assignedOfficers);
    }

    public boolean isVisible() {
        return isVisible;
    }

    // Setters
    public void setOpeningDate(LocalDate openingDate) {
        this.openingDate = openingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public void setAssignedManager(String assignedManager) {
        this.assignedManager = assignedManager;
    }

    public void setOfficerSlots(int officerSlots) {
        this.officerSlots = officerSlots;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Project: ").append(projectName)
          .append("\nNeighborhood: ").append(neighborhood)
          .append("\nApplication Period: ").append(openingDate).append(" to ").append(closingDate)
          .append("\nManager: ").append(assignedManager)
          .append("\nVisibility: ").append(isVisible ? "Visible" : "Hidden")
          .append("\nUnit Types:");

        for (UnitType type : unitTypes.values()) {
            sb.append("\n").append(type.toString());
        }

        if (!assignedOfficers.isEmpty()) {
            sb.append("\nAssigned Officers: ").append(String.join(", ", assignedOfficers));
        }

        return sb.toString();
    }
}
