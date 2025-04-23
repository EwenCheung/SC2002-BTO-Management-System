package io;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import models.Project;
import models.UnitInfo;
import utils.Constants;
import utils.FileUtils;

/**
 * Provides serialization functionality for Project objects.
 * Responsible for converting Project objects to string format for persistent storage
 * in CSV files. Handles complex project data including unit information, dates, officers,
 * and other project attributes.
 */
public class ProjectSerializer {  
    // Create a separate formatter for LocalDate objects without time components
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
    
    /**
     * Serializes a Project object into a string.
     * Expected format:
     * Project Name,Neighborhood,Type 1,Number of Units for Type 1,Available Units for Type 1,Selling price for Type 1,
     * Type 2,Number of Units for Type 2,Available Units for Type 2,Selling price for Type 2,
     * Application opening date,Application closing date,Manager,Officer Slot,Officer(s),Visibility
     *
     * @param project The Project object to serialize.
     * @return A string representation of the Project.
     */
    public static String serialize(Project project) {
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtils.escapeCsvField(project.getProjectName())).append(Constants.DELIMITER)
          .append(FileUtils.escapeCsvField(project.getNeighborhood())).append(Constants.DELIMITER);
        
        // Process unit info from the map.
        Map<String, UnitInfo> units = project.getUnits();
        
        // Process the "2-Room" details.
        if (units.containsKey("2-Room")) {
            UnitInfo type1 = units.get("2-Room");
            sb.append("2-Room").append(Constants.DELIMITER)
              .append(type1.getTotalUnits()).append(Constants.DELIMITER)
              .append(type1.getAvailableUnits()).append(Constants.DELIMITER)
              .append(type1.getSellingPrice()).append(Constants.DELIMITER);
        } else {
            sb.append("N/A").append(Constants.DELIMITER)
              .append("0").append(Constants.DELIMITER)
              .append("0").append(Constants.DELIMITER)
              .append("0.0").append(Constants.DELIMITER);
        }
        
        // Process the "3-Room" details.
        if (units.containsKey("3-Room")) {
            UnitInfo type2 = units.get("3-Room");
            sb.append("3-Room").append(Constants.DELIMITER)
              .append(type2.getTotalUnits()).append(Constants.DELIMITER)
              .append(type2.getAvailableUnits()).append(Constants.DELIMITER)
              .append(type2.getSellingPrice()).append(Constants.DELIMITER);
        } else {
            sb.append("N/A").append(Constants.DELIMITER)
              .append("0").append(Constants.DELIMITER)
              .append("0").append(Constants.DELIMITER)
              .append("0.0").append(Constants.DELIMITER);
        }
        
        // Use DATE_ONLY_FORMATTER for LocalDate objects which do not have time components
        sb.append(DATE_ONLY_FORMATTER.format(project.getApplicationOpeningDate())).append(Constants.DELIMITER)
          .append(DATE_ONLY_FORMATTER.format(project.getApplicationClosingDate())).append(Constants.DELIMITER)
          .append(project.getManager()).append(Constants.DELIMITER)
          .append(project.getOfficerSlot()).append(Constants.DELIMITER)
          // Convert the list of officers to a semicolon-separated string.
          .append(String.join(";", project.getOfficers())).append(Constants.DELIMITER)
          .append(project.isVisible());
          
        return sb.toString();
    }
}
