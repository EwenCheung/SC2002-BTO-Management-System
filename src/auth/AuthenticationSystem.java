package auth;

import utils.FileUtils;
import java.util.List;

public class AuthenticationSystem {
    private static final String APPLICANT_FILE = "ApplicantList.txt";
    private static final String OFFICER_FILE = "OfficerList.txt";
    private static final String MANAGER_FILE = "ManagerList.txt";

    public User login(String nric, String password, UserType type) {
        if (!isValidNRIC(nric)) {
            return null;
        }

        String fileName;
        switch (type) {
            case APPLICANT:
                fileName = APPLICANT_FILE;
                break;
            case OFFICER:
                fileName = OFFICER_FILE;
                break;
            case MANAGER:
                fileName = MANAGER_FILE;
                break;
            default:
                return null;
        }

        List<String[]> users = FileUtils.readFile(fileName);
        
        // Skip header row
        for (int i = 1; i < users.size(); i++) {
            String[] userData = users.get(i);
            if (userData[1].equals(nric) && userData[4].equals(password)) {
                return new User(
                    userData[0], // name
                    userData[1], // nric
                    Integer.parseInt(userData[2]), // age
                    userData[3], // marital status
                    type
                );
            }
        }
        return null;
    }

    public boolean checkNricExists(String nric, UserType type) {
        if (!isValidNRIC(nric)) {
            return true; // Invalid NRIC is considered as "exists" to prevent registration
        }

        String fileName;
        switch (type) {
            case APPLICANT:
                fileName = APPLICANT_FILE;
                break;
            case OFFICER:
                fileName = OFFICER_FILE;
                break;
            case MANAGER:
                fileName = MANAGER_FILE;
                break;
            default:
                return true; // Unknown type is considered as "exists" to prevent registration
        }

        return checkNricInFile(nric, fileName);
    }

    private boolean checkNricInFile(String nric, String fileName) {
        List<String[]> users = FileUtils.readFile(fileName);
        
        // Skip header row
        for (int i = 1; i < users.size(); i++) {
            if (users.get(i)[1].equals(nric)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidNRIC(String nric) {
        if (nric == null || nric.length() != 9) {
            return false;
        }

        // First character must be 'S' or 'T'
        char firstChar = nric.charAt(0);
        if (firstChar != 'S' && firstChar != 'T') {
            return false;
        }

        // Middle 7 characters must be digits
        String digits = nric.substring(1, 8);
        if (!digits.matches("\\d{7}")) {
            return false;
        }

        // Last character must be a letter
        return Character.isLetter(nric.charAt(8));
    }

    public boolean isManager(String nric) {
        List<String[]> managers = FileUtils.readFile(MANAGER_FILE);
        
        // Skip header row
        for (int i = 1; i < managers.size(); i++) {
            if (managers.get(i)[1].equals(nric)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOfficer(String nric) {
        List<String[]> officers = FileUtils.readFile(OFFICER_FILE);
        
        // Skip header row
        for (int i = 1; i < officers.size(); i++) {
            if (officers.get(i)[1].equals(nric)) {
                return true;
            }
        }
        return false;
    }
}
