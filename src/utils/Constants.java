package utils;

/**
 * Constants used throughout the BTO Management System.
 * Contains file paths, delimiters, date formats, and other configuration constants.
 */
public class Constants {
    /** CSV field delimiter character */
    public static final String DELIMITER = ",";
    
    /** Regular expression for parsing CSV fields that handles values containing commas within quotes */
    public static final String DELIMITER_REGEX = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    
    /** Quote character for escaping values containing commas in CSV files */
    public static final String QUOTE = "\"";
    
    /** Filename for project data */
    public static final String PROJECT_FILE = "ProjectList.csv";
    
    /** Filename for application data */
    public static final String APPLICATION_FILE = "ApplicationList.csv";
    
    /** Filename for enquiry data */
    public static final String ENQUIRY_FILE = "EnquiryList.csv";
    
    /** Filename for withdrawal request data */
    public static final String WITHDRAWAL_FILE = "WithdrawalRequests.csv";
    
    /** Filename for officer registration data */
    public static final String OFFICER_REGISTRATION_FILE = "OfficerRegistrations.csv";

    /** Filename for applicant user data */
    public static final String APPLICANT_FILE = "ApplicantList.csv";
    
    /** Filename for officer user data */
    public static final String OFFICER_FILE = "OfficerList.csv"; 
    
    /** Filename for manager user data */
    public static final String MANAGER_FILE = "ManagerList.csv";
    
    /** Date format pattern used for displaying and parsing dates */
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    
    /** Date and time format pattern used for timestamps */
    public static final String DATE_TIME_FORMAT = "M/d/yyyy HH:mm:ss";
    
    /** Default password for new user accounts */
    public static final String DEFAULT_PASSWORD = "password";
}
