package access.officerregistration;

import java.util.List;
import models.OfficerRegistration;

/**
 * Interface exposing officer registration operations available for Managers.
 */
public interface ManagerOfficerRegistrationFeatures {
    /**
     * Retrieves all officer registrations.
     *
     * @return a list of OfficerRegistration objects.
     */
    List<OfficerRegistration> getAllOfficerRegistrations();
    
    /**
     * Approves an officer registration by its registration ID.
     *
     * @param registrationId the ID of the registration to approve.
     */
    void approveRegistration(String registrationId);
    
    /**
     * Rejects an officer registration by its registration ID.
     *
     * @param registrationId the ID of the registration to reject.
     */
    void rejectRegistration(String registrationId);
}
