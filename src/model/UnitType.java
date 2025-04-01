package model;

import java.util.ArrayList;
import java.util.List;

public class UnitType {
    private String type;
    private int totalUnits;
    private double price;
    private List<String> availableUnits;
    private List<String> assignedUnits;

    public UnitType(String type, int totalUnits, double price) {
        this.type = type;
        this.totalUnits = totalUnits;
        this.price = price;
        this.availableUnits = new ArrayList<>();
        this.assignedUnits = new ArrayList<>();
        
        // Initialize available units
        for (int i = 1; i <= totalUnits; i++) {
            availableUnits.add(String.format("%s-%03d", type, i));
        }
    }

    public boolean assignUnit(String applicantNric) {
        if (availableUnits.isEmpty()) {
            return false;
        }
        String unit = availableUnits.remove(0);
        assignedUnits.add(unit);
        return true;
    }

    public boolean unassignUnit(String unit) {
        if (assignedUnits.remove(unit)) {
            availableUnits.add(unit);
            return true;
        }
        return false;
    }

    public int getAvailableUnits() {
        return availableUnits.size();
    }

    public int getTotalUnits() {
        return totalUnits;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getAvailableUnitNumbers() {
        return new ArrayList<>(availableUnits);
    }

    public List<String> getAssignedUnitNumbers() {
        return new ArrayList<>(assignedUnits);
    }

    @Override
    public String toString() {
        return String.format("%s: %d units at $%.2f each (%d available)",
                type, totalUnits, price, getAvailableUnits());
    }
}
