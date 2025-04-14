package access.officerregistration;

import java.util.ArrayList;
import java.util.List;
import models.OfficerRegistration;
import models.enums.OfficerRegistrationStatus;
import io.FileIO;

public class OfficerRegistrationHandler implements ManagerOfficerRegistrationFeatures, OfficerRegistrationApplicantFeatures {
    
    private List<OfficerRegistration> registrations;
    
    public OfficerRegistrationHandler(List<OfficerRegistration> registrations) {
        this.registrations = registrations;
    }
    
    // ----- ManagerOfficerRegistrationFeatures Implementation -----
    
    @Override
    public List<OfficerRegistration> getAllOfficerRegistrations() {
        return registrations;
    }
    
    @Override
    public void approveRegistration(String registrationId) {
        OfficerRegistration reg = findRegistrationById(registrationId);
        if (reg == null) {
            throw new IllegalArgumentException("Officer Registration not found: " + registrationId);
        }
        // Use the enum value for approved.
        reg.setStatus(OfficerRegistrationStatus.APPROVED);
    }
    
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
    
    public void saveChanges() {
        FileIO.saveOfficerRegistrations(registrations);
    }
    
    private OfficerRegistration findRegistrationById(String registrationId) {
        for (OfficerRegistration reg : registrations) {
            if (reg.getRegistrationId().equalsIgnoreCase(registrationId)) {
                return reg;
            }
        }
        return null;
    }
    
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
    
    private String generateUniqueId(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }
}
