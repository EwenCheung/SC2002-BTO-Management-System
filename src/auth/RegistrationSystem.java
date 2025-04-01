package auth;

import utils.FileUtils;
import java.util.List;
import java.util.ArrayList;

public class RegistrationSystem {
    private static final String APPLICANT_FILE = "ApplicantList.txt";
    private final AuthenticationSystem authSystem;

    public RegistrationSystem() {
        authSystem = new AuthenticationSystem();
    }

    public boolean registerApplicant(String name, String nric, int age, String maritalStatus, String password) {
        // Validate input
        if (name == null || name.trim().isEmpty() ||
            nric == null || nric.trim().isEmpty() ||
            age < 18 ||
            maritalStatus == null || maritalStatus.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            return false;
        }

        // Check if NRIC already exists
        if (authSystem.checkNricExists(nric, UserType.APPLICANT)) {
            return false;
        }

        // Read existing data
        List<String[]> applicants = FileUtils.readFile(APPLICANT_FILE);
        
        // If file is empty, create header
        if (applicants.isEmpty()) {
            applicants.add(new String[]{"Name", "NRIC", "Age", "Marital Status", "Password"});
        }

        // Create new applicant record
        String[] newApplicant = new String[]{
            name.trim(),
            nric.trim(),
            String.valueOf(age),
            maritalStatus.trim(),
            password.trim()
        };

        // Add new applicant
        applicants.add(newApplicant);

        // Write back to file
        return FileUtils.writeFile(APPLICANT_FILE, applicants);
    }

    public boolean isValidNRIC(String nric) {
        if (nric == null || nric.length() != 9) {
            return false;
        }

        // NRIC format: S1234567A
        char firstChar = nric.charAt(0);
        char lastChar = nric.charAt(8);
        String digits = nric.substring(1, 8);

        // Check first character
        if (firstChar != 'S' && firstChar != 'T') {
            return false;
        }

        // Check if middle part is numeric
        try {
            Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return false;
        }

        // Check last character is alphabetic
        return Character.isLetter(lastChar);
    }

    public boolean isValidAge(int age) {
        return age >= 18 && age <= 130;
    }

    public boolean isValidMaritalStatus(String status) {
        if (status == null) {
            return false;
        }
        String lowercaseStatus = status.toLowerCase();
        return lowercaseStatus.equals("single") || lowercaseStatus.equals("married");
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}
