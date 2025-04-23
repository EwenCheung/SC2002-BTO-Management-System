package models;

/**
 * Represents information about a specific unit type within a BTO project.
 * Contains details about unit quantity and pricing.
 */
public class UnitInfo {
    private int totalUnits;
    private int availableUnits;
    private double sellingPrice;
    
    /**
     * Constructor to create a new UnitInfo object.
     * 
     * @param totalUnits     The total number of units of this type in the project
     * @param availableUnits The number of units still available for application
     * @param sellingPrice   The selling price of this unit type
     */
    public UnitInfo(int totalUnits, int availableUnits, double sellingPrice) {
        this.totalUnits = totalUnits;
        this.availableUnits = availableUnits;
        this.sellingPrice = sellingPrice;
    }
    
    /**
     * Gets the total number of units of this type in the project.
     * 
     * @return The total number of units
     */
    public int getTotalUnits() {
        return totalUnits;
    }
    
    /**
     * Gets the number of units still available for application.
     * 
     * @return The number of available units
     */
    public int getAvailableUnits() {
        return availableUnits;
    }
    
    /**
     * Gets the selling price of this unit type.
     * 
     * @return The selling price
     */
    public double getSellingPrice() {
        return sellingPrice;
    }
    
    /**
     * Updates the number of available units.
     * 
     * @param availableUnits The new number of available units
     */
    public void setAvailableUnits(int availableUnits) {
        this.availableUnits = availableUnits;
    }
}
