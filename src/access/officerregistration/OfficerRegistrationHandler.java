package access.officerregistration;

import java.util.ArrayList;
import java.util.List;
import models.OfficerRegistration;
import models.enums.OfficerRegistrationStatus;
import io.FileIO;

/**
 * Handles all officer registration operations in the BTO Management System.
 * Implements interfaces for managers and officers to provide
 * role-appropriate access to registration functionality and data.
 */
public class OfficerRegistrationHandler implements ManagerOfficerRegistrationFeatures, OfficerRegistrationApplicantFeatures {
    
    private List<OfficerRegistration> registrations;
    
    /**
     * Constructs an OfficerRegistrationHandler with the given list of registrations.
     * 
     * @param registrations The list of officer registrations to manage
     */
    public OfficerRegistrationHandler(List<OfficerRegistration> registrations) {
        this.registrations = registrations;
    }
    
    // ----- ManagerOfficerRegistrationFeatures Implementation -----
    
    /**
     * Returns all officer registrations in the system.
     * Available to managers for oversight of officer assignments.
     * 
     * @return A list of all OfficerRegistration objects
     */
    @Override
    public List<OfficerRegistration> getAllOfficerRegistrations() {
        return registrations;
    }
    
    /**
     * Approves an officer's registration for a project.
     * Available to managers as part of the officer assignment process.
     * 
     * @param registrationId The ID of the registration to approve
     * @throws IllegalArgumentException if the registration is not found
     */
    @Override
    public void approveRegistration(String registrationId) {
        OfficerRegistration reg = findRegistrationById(registrationId);
        if (reg == null) {
            throw new IllegalArgumentException("Officer Registration not found: " + registrationId);
        }
        // Use the enum value for approved.
        reg.setStatus(OfficerRegistrationStatus.APPROVED);
    }
    
    /**
     * Rejects an officer's registration for a project.
     * Available to managers as part of the officer assignment process.
     * 
     * @param registrationId The ID of the registration to reject
     * @throws IllegalArgumentException if the registration is not found
     */
    @Override
    public void rejectRegistration(String registrationId) {
        OfficerRegistration reg = findRegistrationById(registrationId);
        if (reg == null) {
            throw new IllegalArgumentException("Officer Registration not found: " + registrationId);
        }
        // Use the enum value for rejected.
        reg.setStatus(OfficerRegistrationStatus.REJECTED);
    }
    
    // ----- Officer Registration Applicant Features Implementation -----
    
    /**
     * Submits a new officer registration application.
     * Available to officers to register for project assignment.
     * 
     * @param registration The registration to submit
     * @throws IllegalArgumentException if the officer is not eligible for the project
     */
    @Override
    public void applyForOfficerRegistration(OfficerRegistration registration) {
        if (!canApplyForProject(registration)) {
            throw new IllegalArgumentException("Officer is not eligible to apply for project: " + registration.getProjectName());
        }
        // Generate a unique registration ID if not set.
        if (registration.getRegistrationId() == null || registration.getRegistrationId().isEmpty()) {
            registration.setRegistrationId(generateUniqueId("OFR"));
        }
        registrations.add(registration);
    }
    
    /**
     * Returns all registrations submitted by a specific officer.
     * Available to officers to view their registration history.
     * 
     * @param officerNric The NRIC of the officer
     * @return A list of OfficerRegistration objects submitted by the specified officer
     */
    @Override
    public List<OfficerRegistration> getRegistrationsForOfficer(String officerNric) {
        List<OfficerRegistration> result = new ArrayList<>();
        for (OfficerRegistration reg : registrations) {
            if (reg.getOfficerNric().equalsIgnoreCase(officerNric)) {
                result.add(reg);
            }
        }
        return result;
    }
    
    /**
     * Saves current registration data to persistent storage.
     * Called after operations that modify registration data.
     */
    public void saveChanges() {
        FileIO.saveOfficerRegistrations(registrations);
    }
    
    /**
     * Finds a registration by its ID.
     * Helper method used by various public methods that require finding specific registrations.
     * 
     * @param registrationId The ID of the registration to find
     * @return The OfficerRegistration object with the specified ID, or null if not found
     */
    private OfficerRegistration findRegistrationById(String registrationId) {
        for (OfficerRegistration reg : registrations) {
            if (reg.getRegistrationId().equalsIgnoreCase(registrationId)) {
                return reg;
            }
        }
        return null;
    }
    
    /**
     * Checks if an officer is eligible to apply for a project.
     * Prevents officers from being approved for multiple projects simultaneously.
     * 
     * @param newReg The new registration to check
     * @return true if the officer can apply, false otherwise
     */
    private boolean canApplyForProject(OfficerRegistration newReg) {
        String officerNric = newReg.getOfficerNric();
        String targetProject = newReg.getProjectName();
        
        // Placeholder check: ensure officer does not already have an approved registration
        // for another project.
        for (OfficerRegistration reg : registrations) {
            if (reg.getOfficerNric().equalsIgnoreCase(officerNric)) {
                // Compare using the enum rather than string; if there's an approved registration for a different project, disallow.
                if (!reg.getProjectName().equalsIgnoreCase(targetProject) &&
                    reg.getStatus() == OfficerRegistrationStatus.APPROVED) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Generates a unique ID for a new registration.
     * Creates IDs in the format: [prefix]-[timestamp] to ensure uniqueness.
     * 
     * @param prefix The prefix to use for the ID (e.g., "OFR")
     * @return A unique ID string
     */
    private String generateUniqueId(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }
}
