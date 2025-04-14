package io;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import models.Project;
import utils.Constants;

public class ProjectFactory {
    public static Project createProject(String[] tokens) {
        // Format: Project Name,Neighborhood,Type 1,Num 1,Avail 1,Price 1,
        //         Type 2,Num 2,Avail 2,Price 2,OpenDate,CloseDate,Manager,Officer Slot,Officer(s),Visibility
        if (tokens.length < 16 || tokens[0].isEmpty() || tokens[1].isEmpty() ||
            tokens[2].isEmpty() || tokens[6].isEmpty() || tokens[10].isEmpty() ||
            tokens[11].isEmpty() || tokens[12].isEmpty()) {
            throw new IllegalArgumentException("Missing required fields for Project");
        }

        String name = tokens[0];
        String neighborhood = tokens[1];

        String type1 = tokens[2];
        int num1 = Integer.parseInt(tokens[3]);
        int avail1 = Integer.parseInt(tokens[4]);
        double price1 = Double.parseDouble(tokens[5]);

        String type2 = tokens[6];
        int num2 = Integer.parseInt(tokens[7]);
        int avail2 = Integer.parseInt(tokens[8]);
        double price2 = Double.parseDouble(tokens[9]);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);
        LocalDate openDate = LocalDate.parse(tokens[10], formatter);
        LocalDate closeDate = LocalDate.parse(tokens[11], formatter);
        String managerNric = tokens[12];
        int officerSlot = Integer.parseInt(tokens[13]);
        List<String> officers = tokens[14].isEmpty() ? new ArrayList<>() : Arrays.asList(tokens[14].split(";"));
        boolean visible = Boolean.parseBoolean(tokens[15]);

        Project project = new Project(name, neighborhood, openDate, closeDate, managerNric, officerSlot);
        project.addUnitType(type1, num1, price1);
        project.addUnitType(type2, num2, price2);

        // Overwrite available units after creation
        project.setAvailableUnits(type1, avail1);
        project.setAvailableUnits(type2, avail2);

        for (String officer : officers) {
            project.addOfficer(officer);
        }

        project.setVisible(visible);
        return project;
    }
}