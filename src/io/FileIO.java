package io;
import java.io.*;
import java.util.*;
import utils.*;
import users.*;
import models.*;

public class FileIO {
    public static List<User> loadUsers() {
        List<String[]> rows = FileUtils.readFile(Constants.USER_FILE);
        List<User> users = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) { // skip header
            String[] tokens = rows.get(i);
            if (tokens.length == 6) {
                users.add(UserFactory.createUser(tokens));
            }
        }
        return users;
    }

    public static List<String[]> loadRaw(String fileName) {
        List<String[]> data = FileUtils.readFile(fileName);
        return data.size() > 1 ? data.subList(1, data.size()) : new ArrayList<>();
    }

    public static void saveRaw(String fileName, List<String[]> data) {
        FileUtils.writeFile(fileName, data);
    }

    public static List<Project> loadProjects() {
        List<String[]> rows = FileUtils.readFile(Constants.PROJECT_FILE);
        List<Project> projects = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            String[] tokens = rows.get(i);
            if (tokens.length == 15) {
                projects.add(ProjectFactory.createProject(tokens));
            }
        }
        return projects;
    }

    public static List<Application> loadApplications() {
        List<String[]> rows = FileUtils.readFile(Constants.APPLICATION_FILE);
        List<Application> apps = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            apps.add(ApplicationFactory.createApplication(rows.get(i)));
        }
        return apps;
    }

    public static List<Enquiry> loadEnquiries() {
        List<String[]> rows = FileUtils.readFile(Constants.ENQUIRY_FILE);
        List<Enquiry> enquiries = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            enquiries.add(EnquiryFactory.createEnquiry(rows.get(i)));
        }
        return enquiries;
    }

    public static List<WithdrawalRequest> loadWithdrawals() {
        List<String[]> rows = FileUtils.readFile(Constants.WITHDRAWAL_FILE);
        List<WithdrawalRequest> requests = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            requests.add(WithdrawalFactory.createRequest(rows.get(i)));
        }
        return requests;
    }

    public static List<OfficerRegistration> loadOfficerRegistrations() {
        List<String[]> rows = FileUtils.readFile(Constants.OFFICER_REGISTRATION_FILE);
        List<OfficerRegistration> registrations = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            registrations.add(OfficerRegistrationFactory.createRegistration(rows.get(i)));
        }
        return registrations;
    }

    // ------------------ Save functions --------------------

    public static void saveUsers(List<User> users) {
        List<String[]> data = new ArrayList<>();
        // Header for User file
        data.add(new String[]{"Name", "NRIC", "Age", "Marital Status", "Password", "UserType"});
        
        for (User user : users) {
            String serialized = UserSerializer.serialize(user);
            // Split using the delimiter pattern; Constants.DELIMITER is defined as "\\|\\|"
            String[] tokens = serialized.split(Constants.DELIMITER);
            data.add(tokens);
        }
        saveRaw(Constants.USER_FILE, data);
    }
    
    public static void saveProjects(List<Project> projects) {
        List<String[]> data = new ArrayList<>();
        // Header for Project file
        data.add(new String[]{"Project Name", "Neighborhood", "Type 1", "Total Units for Type 1", "Available Units for Type 1", "Selling price for Type 1", "Type 2", "Total Units for Type 2", "Available Units for Type 2", "Selling price for Type 2", "Application opening date", "Application closing date", "Manager", "Officer Slot", "Officer(s)", "Visibility"});
        
        for (Project project : projects) {
            String serialized = ProjectSerializer.serialize(project);
            String[] tokens = serialized.split(Constants.DELIMITER);
            data.add(tokens);
        }
        saveRaw(Constants.PROJECT_FILE, data);
    }
    
    public static void saveApplications(List<Application> applications) {
        List<String[]> data = new ArrayList<>();
        // Header for Application file
        data.add(new String[]{"Application ID", "Applicant NRIC", "Project Name", "Unit Type", "Status", "Assigned Unit", "Assigned Officer", "Application Date", "Last Updated", "Remarks"});
        
        for (Application app : applications) {
            String serialized = ApplicationSerializer.serialize(app);
            String[] tokens = serialized.split(Constants.DELIMITER);
            data.add(tokens);
        }
        saveRaw(Constants.APPLICATION_FILE, data);
    }
    
    public static void saveEnquiries(List<Enquiry> enquiries) {
        List<String[]> data = new ArrayList<>();
        // Header for Enquiry file
        data.add(new String[]{"Enquiry ID", "NRIC", "Project Name", "Enquiry", "Response", "Timestamp", "Respondent NRIC", "Response Date"});
        
        for (Enquiry enquiry : enquiries) {
            String serialized = EnquirySerializer.serialize(enquiry);
            String[] tokens = serialized.split(Constants.DELIMITER);
            data.add(tokens);
        }
        saveRaw(Constants.ENQUIRY_FILE, data);
    }
    
    public static void saveWithdrawals(List<WithdrawalRequest> withdrawals) {
        List<String[]> data = new ArrayList<>();
        // Header for Withdrawal file
        data.add(new String[]{"Request ID", "Application ID", "Applicant NRIC", "Project Name", "Status", "Request Date", "Process Date", "Remarks"});
        
        for (WithdrawalRequest request : withdrawals) {
            String serialized = WithdrawalRequestSerializer.serialize(request);
            String[] tokens = serialized.split(Constants.DELIMITER);
            data.add(tokens);
        }
        saveRaw(Constants.WITHDRAWAL_FILE, data);
    }
    
    public static void saveOfficerRegistrations(List<OfficerRegistration> registrations) {
        List<String[]> data = new ArrayList<>();
        // Header for Officer Registration file
        data.add(new String[]{"Registration ID", "Officer NRIC", "Project Name", "Status", "Registration Date"});
        
        for (OfficerRegistration reg : registrations) {
            String serialized = OfficerRegistrationSerializer.serialize(reg);
            String[] tokens = serialized.split(Constants.DELIMITER);
            data.add(tokens);
        }
        saveRaw(Constants.OFFICER_REGISTRATION_FILE, data);
    }
}