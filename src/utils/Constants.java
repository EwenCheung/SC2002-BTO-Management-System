package utils;

public class Constants {
    // CSV format settings
    public static final String DELIMITER = ",";
    // Regex that handles values containing commas within quotes
    public static final String DELIMITER_REGEX = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    // Quote character for escaping values containing commas
    public static final String QUOTE = "\"";
    public static final String PROJECT_FILE = "ProjectList.csv";
    public static final String APPLICATION_FILE = "ApplicationList.csv";
    public static final String ENQUIRY_FILE = "EnquiryList.csv";
    public static final String WITHDRAWAL_FILE = "WithdrawalRequests.csv";
    public static final String OFFICER_REGISTRATION_FILE = "OfficerRegistrations.csv";

    // Changed file extensions to CSV
    public static final String APPLICANT_FILE = "ApplicantList.csv";
    public static final String OFFICER_FILE = "OfficerList.csv"; 
    public static final String MANAGER_FILE = "ManagerList.csv";
    
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String DATE_TIME_FORMAT = "M/d/yyyy HH:mm:ss";
    public static final String DEFAULT_PASSWORD = "password";
}
