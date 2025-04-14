package models;

public class UnitInfo {
    private int totalUnits;
    private int availableUnits;
    private double sellingPrice;
    
    public UnitInfo(int totalUnits, int availableUnits, double sellingPrice) {
        this.totalUnits = totalUnits;
        this.availableUnits = availableUnits;
        this.sellingPrice = sellingPrice;
    }
    
    public int getTotalUnits() {
        return totalUnits;
    }
    
    public int getAvailableUnits() {
        return availableUnits;
    }
    
    public double getSellingPrice() {
        return sellingPrice;
    }
    
    public void setAvailableUnits(int availableUnits) {
        this.availableUnits = availableUnits;
    }
}
