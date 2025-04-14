package io;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import models.Project;
import models.UnitInfo;

import static utils.Constants.DELIMITER;
import static utils.Constants.DATE_FORMAT;

public class ProjectSerializer {  
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    /**
     * Serializes a Project object into a string.
     * Expected format:
     * Project Name||Neighborhood||
     * Type 1||Total Units for Type 1||Available Units for Type 1||Selling price for Type 1||
     * Type 2||Total Units for Type 2||Available Units for Type 2||Selling price for Type 2||
     * Application opening date||Application closing date||Manager||Officer Slot||Officer(s)||Visibility
     *
     * @param project The Project object to serialize.
     * @return A string representation of the Project.
     */
    public static String serialize(Project project) {
        StringBuilder sb = new StringBuilder();
        sb.append(project.getProjectName()).append(DELIMITER)
          .append(project.getNeighborhood()).append(DELIMITER);
        
        // Process unit info from the map.
        Map<String, UnitInfo> units = project.getUnits();
        
        // Process the "2-Room" details.
        if (units.containsKey("2-Room")) {
            UnitInfo type1 = units.get("2-Room");
            sb.append("2-Room").append(DELIMITER)
              .append(type1.getTotalUnits()).append(DELIMITER)
              .append(type1.getAvailableUnits()).append(DELIMITER)
              .append(type1.getSellingPrice()).append(DELIMITER);
        } else {
            sb.append("N/A").append(DELIMITER)
              .append("0").append(DELIMITER)
              .append("0").append(DELIMITER)
              .append("0.0").append(DELIMITER);
        }
        
        // Process the "3-Room" details.
        if (units.containsKey("3-Room")) {
            UnitInfo type2 = units.get("3-Room");
            sb.append("3-Room").append(DELIMITER)
              .append(type2.getTotalUnits()).append(DELIMITER)
              .append(type2.getAvailableUnits()).append(DELIMITER)
              .append(type2.getSellingPrice()).append(DELIMITER);
        } else {
            sb.append("N/A").append(DELIMITER)
              .append("0").append(DELIMITER)
              .append("0").append(DELIMITER)
              .append("0.0").append(DELIMITER);
        }
        
        sb.append(DATE_FORMATTER.format(project.getApplicationOpeningDate())).append(DELIMITER)
          .append(DATE_FORMATTER.format(project.getApplicationClosingDate())).append(DELIMITER)
          .append(project.getManager()).append(DELIMITER)
          .append(project.getOfficerSlot()).append(DELIMITER)
          // Convert the list of officers to a semicolon-separated string.
          .append(String.join(";", project.getOfficers())).append(DELIMITER)
          .append(project.isVisible());
          
        return sb.toString();
    }
}
