package io;

import users.*;
import users.enums.MaritalStatus;
import users.enums.UserType;

public class UserFactory {
    public static User createUser(String[] tokens) {
        // Format: Name,NRIC,Age,Marital Status,Password,UserType
        String name = tokens[0];
        String nric = tokens[1];
        int age = Integer.parseInt(tokens[2]);
        MaritalStatus maritalStatus = MaritalStatus.valueOf(tokens[3].toUpperCase());
        String password = tokens[4];
        UserType role = UserType.valueOf(tokens[5].toUpperCase());

        switch (role) {
            case APPLICANT:
                return new Applicant(name, nric, age, maritalStatus, password);
            case OFFICER:
                return new HDBOfficer(name, nric, age, maritalStatus, password);
            case MANAGER:
                return new ProjectManager(name, nric, age, maritalStatus, password);
            default:
                throw new IllegalArgumentException("Unknown user role: " + role);
        }
    }
} 
