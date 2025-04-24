package io;

import users.*;
import utils.FileUtils;
import static utils.Constants.DELIMITER;

/**
 * Provides serialization functionality for User objects and their subclasses.
 * This class is responsible for converting User objects to string format
 * for persistent storage in CSV files. It handles proper escaping of text fields
 * and maintains specific serialization formats for different user types.
 */
public class UserSerializer {
    /**
     * Serializes a User object into a string that can be saved to a text file.
     * Format: Name||NRIC||Age||Marital Status||Password||UserType
     * 
     * @param user The User object to serialize.
     * @return A string representation of the User.
     */
    public static String serialize(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtils.escapeCsvField(user.getName())).append(DELIMITER)
          .append(user.getNric()).append(DELIMITER)
          .append(user.getAge()).append(DELIMITER)
          .append(user.getMaritalStatus().toString()).append(DELIMITER)
          .append(user.getPassword()).append(DELIMITER)
          .append(user.getUserType().toString());
        return sb.toString();
    }
    
    /**
     * Serializes an Applicant into a string that can be saved to ApplicantList.txt.
     * Format: Name||NRIC||Age||Marital Status||Password
     * 
     * @param applicant The Applicant to serialize.
     * @return A string representation of the Applicant.
     */
    public static String serializeApplicant(Applicant applicant) {
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtils.escapeCsvField(applicant.getName())).append(DELIMITER)
          .append(applicant.getNric()).append(DELIMITER)
          .append(applicant.getAge()).append(DELIMITER)
          .append(applicant.getMaritalStatus().toString()).append(DELIMITER)
          .append(applicant.getPassword());
        return sb.toString();
    }
    
    /**
     * Serializes an HDBOfficer into a string that can be saved to OfficerList.txt.
     * Format: Name||NRIC||Age||Marital Status||Password
     * 
     * @param officer The HDBOfficer to serialize.
     * @return A string representation of the HDBOfficer.
     */
    public static String serializeOfficer(HDBOfficer officer) {
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtils.escapeCsvField(officer.getName())).append(DELIMITER)
          .append(officer.getNric()).append(DELIMITER)
          .append(officer.getAge()).append(DELIMITER)
          .append(officer.getMaritalStatus().toString()).append(DELIMITER)
          .append(officer.getPassword());
        return sb.toString();
    }
    
    /**
     * Serializes a ProjectManager into a string that can be saved to ManagerList.txt.
     * Format: Name||NRIC||Age||Marital Status||Password
     * 
     * @param manager The ProjectManager to serialize.
     * @return A string representation of the ProjectManager.
     */
    public static String serializeManager(ProjectManager manager) {
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtils.escapeCsvField(manager.getName())).append(DELIMITER)
          .append(manager.getNric()).append(DELIMITER)
          .append(manager.getAge()).append(DELIMITER)
          .append(manager.getMaritalStatus().toString()).append(DELIMITER)
          .append(manager.getPassword());
        return sb.toString();
    }
}
