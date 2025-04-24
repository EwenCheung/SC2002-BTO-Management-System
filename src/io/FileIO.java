package io;
import java.util.*;
import utils.*;
import users.*;
import models.*;

/**
 * Provides file input/output operations for the BTO Management System.
 * This class handles reading and writing of data files for users, applications, 
 * projects, enquiries, and other system data.
 */
public class FileIO {
    /**
     * Loads all users from their respective CSV files.
     * Combines applicants, officers, and managers into a unified user list.
     *
     * @return A list containing all users in the system
     */
    public static List<User> loadUsers() {
        List<User> allUsers = new ArrayList<>();
        
        // Load applicants
        allUsers.addAll(loadApplicants());
        
        // Load officers
        allUsers.addAll(loadOfficers());
        
        // Load managers
        allUsers.addAll(loadManagers());
        
        return allUsers;
    }
    
    /**
     * Loads all applicant users from the applicant CSV file.
     *
     * @return A list of Applicant objects
     */
    public static List<Applicant> loadApplicants() {
        List<String[]> rows = FileUtils.readFile(Constants.APPLICANT_FILE);
        List<Applicant> applicants = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) { // skip header
            String[] tokens = rows.get(i);
            if (tokens.length >= 5) { // Name, NRIC, Age, Marital Status, Password
                applicants.add((Applicant)UserFactory.createApplicant(tokens));
            }
        }
        return applicants;
    }
    
    /**
     * Loads all HDB officer users from the officer CSV file.
     *
     * @return A list of HDBOfficer objects
     */
    public static List<HDBOfficer> loadOfficers() {
        List<String[]> rows = FileUtils.readFile(Constants.OFFICER_FILE);
        List<HDBOfficer> officers = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) { // skip header
            String[] tokens = rows.get(i);
            if (tokens.length >= 5) { // Name, NRIC, Age, Marital Status, Password
                officers.add((HDBOfficer)UserFactory.createOfficer(tokens));
            }
        }
        return officers;
    }
    
    /**
     * Loads all project manager users from the manager CSV file.
     *
     * @return A list of ProjectManager objects
     */
    public static List<ProjectManager> loadManagers() {
        List<String[]> rows = FileUtils.readFile(Constants.MANAGER_FILE);
        List<ProjectManager> managers = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) { // skip header
            String[] tokens = rows.get(i);
            if (tokens.length >= 5) { // Name, NRIC, Age, Marital Status, Password
                managers.add((ProjectManager)UserFactory.createManager(tokens));
            }
        }
        return managers;
    }

    /**
     * Loads raw data from a CSV file, skipping the header row.
     *
     * @param fileName The name of the CSV file to load
     * @return A list of string arrays representing rows of data from the file
     */
    public static List<String[]> loadRaw(String fileName) {
        List<String[]> data = FileUtils.readFile(fileName);
        return data.size() > 1 ? data.subList(1, data.size()) : new ArrayList<>();
    }

    /**
     * Saves raw data to a CSV file.
     *
     * @param fileName The name of the CSV file to save to
     * @param data     The data to save
     */
    public static void saveRaw(String fileName, List<String[]> data) {
        FileUtils.writeFile(fileName, data);
    }

    /**
     * Loads all BTO projects from the project CSV file.
     *
     * @return A list of Project objects
     */
    public static List<Project> loadProjects() {
        List<String[]> rows = FileUtils.readFile(Constants.PROJECT_FILE);
        List<Project> projects = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            String[] tokens = rows.get(i);
            if (tokens.length == 16) {  // Changed from 15 to 16 to match expected column count
                projects.add(ProjectFactory.createProject(tokens));
            }
        }
        return projects;
    }

    /**
     * Loads all applications from the application CSV file.
     *
     * @return A list of Application objects
     */
    public static List<Application> loadApplications() {
        List<String[]> rows = FileUtils.readFile(Constants.APPLICATION_FILE);
        List<Application> apps = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            apps.add(ApplicationFactory.createApplication(rows.get(i)));
        }
        return apps;
    }

    /**
     * Loads all enquiries from the enquiry CSV file.
     *
     * @return A list of Enquiry objects
     */
    public static List<Enquiry> loadEnquiries() {
        List<String[]> rows = FileUtils.readFile(Constants.ENQUIRY_FILE);
        List<Enquiry> enquiries = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            enquiries.add(EnquiryFactory.createEnquiry(rows.get(i)));
        }
        return enquiries;
    }

    /**
     * Loads all withdrawal requests from the withdrawal CSV file.
     *
     * @return A list of WithdrawalRequest objects
     */
    public static List<WithdrawalRequest> loadWithdrawals() {
        List<String[]> rows = FileUtils.readFile(Constants.WITHDRAWAL_FILE);
        List<WithdrawalRequest> requests = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            requests.add(WithdrawalFactory.createRequest(rows.get(i)));
        }
        return requests;
    }

    /**
     * Loads all officer registrations from the officer registration CSV file.
     *
     * @return A list of OfficerRegistration objects
     */
    public static List<OfficerRegistration> loadOfficerRegistrations() {
        List<String[]> rows = FileUtils.readFile(Constants.OFFICER_REGISTRATION_FILE);
        List<OfficerRegistration> registrations = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            registrations.add(OfficerRegistrationFactory.createRegistration(rows.get(i)));
        }
        return registrations;
    }

    // ------------------ Save functions --------------------

    /**
     * Saves all users to their respective CSV files based on their user type.
     *
     * @param users The list of User objects to save
     */
    public static void saveUsers(List<User> users) {
        List<Applicant> applicants = new ArrayList<>();
        List<HDBOfficer> officers = new ArrayList<>();
        List<ProjectManager> managers = new ArrayList<>();
        
        // Sort users by type
        for (User user : users) {
            if (user instanceof Applicant) {
                applicants.add((Applicant) user);
            } else if (user instanceof HDBOfficer) {
                officers.add((HDBOfficer) user);
            } else if (user instanceof ProjectManager) {
                managers.add((ProjectManager) user);
            }
        }
        
        // Save each type to its respective file
        saveApplicants(applicants);
        saveOfficers(officers);
        saveManagers(managers);
    }
    
    /**
     * Saves a list of Applicant objects to the applicant CSV file.
     *
     * @param applicants The list of Applicant objects to save
     */
    public static void saveApplicants(List<Applicant> applicants) {
        List<String[]> data = new ArrayList<>();
        // Header for Applicant file
        data.add(new String[]{"Name", "NRIC", "Age", "Marital Status", "Password"});
        
        for (Applicant user : applicants) {
            String serialized = UserSerializer.serializeApplicant(user);
            // Use parseCsvLine instead of split to properly handle quoted fields with commas
            String[] tokens = FileUtils.parseCsvLine(serialized);
            data.add(tokens);
        }
        saveRaw(Constants.APPLICANT_FILE, data);
    }
    
    /**
     * Saves a list of HDBOfficer objects to the officer CSV file.
     *
     * @param officers The list of HDBOfficer objects to save
     */
    public static void saveOfficers(List<HDBOfficer> officers) {
        List<String[]> data = new ArrayList<>();
        // Header for Officer file
        data.add(new String[]{"Name", "NRIC", "Age", "Marital Status", "Password"});
        
        for (HDBOfficer user : officers) {
            String serialized = UserSerializer.serializeOfficer(user);
            // Use parseCsvLine instead of split to properly handle quoted fields with commas
            String[] tokens = FileUtils.parseCsvLine(serialized);
            data.add(tokens);
        }
        saveRaw(Constants.OFFICER_FILE, data);
    }
    
    /**
     * Saves a list of ProjectManager objects to the manager CSV file.
     *
     * @param managers The list of ProjectManager objects to save
     */
    public static void saveManagers(List<ProjectManager> managers) {
        List<String[]> data = new ArrayList<>();
        // Header for Manager file
        data.add(new String[]{"Name", "NRIC", "Age", "Marital Status", "Password"});
        
        for (ProjectManager user : managers) {
            String serialized = UserSerializer.serializeManager(user);
            // Use parseCsvLine instead of split to properly handle quoted fields with commas
            String[] tokens = FileUtils.parseCsvLine(serialized);
            data.add(tokens);
        }
        saveRaw(Constants.MANAGER_FILE, data);
    }
    
    /**
     * Saves a list of Project objects to the project CSV file.
     *
     * @param projects The list of Project objects to save
     */
    public static void saveProjects(List<Project> projects) {
        List<String[]> data = new ArrayList<>();
        // Header for Project file
        data.add(new String[]{"Project Name", "Neighborhood", "Type 1", "Total Units for Type 1", "Available Units for Type 1", "Selling price for Type 1", "Type 2", "Total Units for Type 2", "Available Units for Type 2", "Selling price for Type 2", "Application opening date", "Application closing date", "Manager", "Officer Slot", "Officer(s)", "Visibility"});
        
        for (Project project : projects) {
            String serialized = ProjectSerializer.serialize(project);
            // Use parseCsvLine instead of split to properly handle quoted fields with commas
            String[] tokens = FileUtils.parseCsvLine(serialized);
            data.add(tokens);
        }
        saveRaw(Constants.PROJECT_FILE, data);
    }
    
    /**
     * Saves a list of Application objects to the application CSV file.
     *
     * @param applications The list of Application objects to save
     */
    public static void saveApplications(List<Application> applications) {
        List<String[]> data = new ArrayList<>();
        // Header for Application file
        data.add(new String[]{"Application ID", "Applicant NRIC", "Project Name", "Unit Type", "Status", "Assigned Unit", "Assigned Officer", "Application Date", "Last Updated", "Remarks"});
        
        for (Application app : applications) {
            String serialized = ApplicationSerializer.serialize(app);
            // Use parseCsvLine instead of simple splitting to handle quoted fields with commas
            String[] tokens = FileUtils.parseCsvLine(serialized);
            data.add(tokens);
        }
        saveRaw(Constants.APPLICATION_FILE, data);
    }
    
    /**
     * Saves a list of Enquiry objects to the enquiry CSV file.
     *
     * @param enquiries The list of Enquiry objects to save
     */
    public static void saveEnquiries(List<Enquiry> enquiries) {
        List<String[]> data = new ArrayList<>();
        // Header for Enquiry file
        data.add(new String[]{"Enquiry ID", "NRIC", "Project Name", "Enquiry", "Response", "Timestamp", "Respondent NRIC", "Response Date"});
        
        for (Enquiry enquiry : enquiries) {
            String serialized = EnquirySerializer.serialize(enquiry);
            
            // Use the parseCsvLine method from FileUtils to correctly parse the CSV line
            // respecting quotes around fields that contain commas
            String[] tokens = FileUtils.parseCsvLine(serialized);
            data.add(tokens);
        }
        saveRaw(Constants.ENQUIRY_FILE, data);
    }
    
    /**
     * Saves a list of WithdrawalRequest objects to the withdrawal CSV file.
     *
     * @param withdrawals The list of WithdrawalRequest objects to save
     */
    public static void saveWithdrawals(List<WithdrawalRequest> withdrawals) {
        List<String[]> data = new ArrayList<>();
        // Header for Withdrawal file
        data.add(new String[]{"Request ID", "Application ID", "Applicant NRIC", "Project Name", "Status", "Request Date", "Process Date", "Remarks"});
        
        for (WithdrawalRequest request : withdrawals) {
            String serialized = WithdrawalRequestSerializer.serialize(request);
            // Use parseCsvLine instead of split to properly handle quotes and commas
            String[] tokens = FileUtils.parseCsvLine(serialized);
            data.add(tokens);
        }
        saveRaw(Constants.WITHDRAWAL_FILE, data);
    }
    
    /**
     * Saves a list of OfficerRegistration objects to the officer registration CSV file.
     *
     * @param registrations The list of OfficerRegistration objects to save
     */
    public static void saveOfficerRegistrations(List<OfficerRegistration> registrations) {
        List<String[]> data = new ArrayList<>();
        // Header for Officer Registration file
        data.add(new String[]{"Registration ID", "Officer NRIC", "Project Name", "Status", "Registration Date"});
        
        for (OfficerRegistration reg : registrations) {
            String serialized = OfficerRegistrationSerializer.serialize(reg);
            // Use parseCsvLine instead of split to properly handle quotes and commas
            String[] tokens = FileUtils.parseCsvLine(serialized);
            data.add(tokens);
        }
        saveRaw(Constants.OFFICER_REGISTRATION_FILE, data);
    }
}