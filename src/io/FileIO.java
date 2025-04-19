package io;
import java.io.*;
import java.util.*;
import utils.*;
import users.*;
import models.*;
import users.enums.UserType;

public class FileIO {
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
            if (tokens.length == 16) {  // Changed from 15 to 16 to match expected column count
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