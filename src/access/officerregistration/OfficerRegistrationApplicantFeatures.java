package access.officerregistration;

import java.util.List;
import models.OfficerRegistration;

/**
 * Interface exposing officer registration operations available for Officers.
 */
public interface OfficerRegistrationApplicantFeatures {
    /**
     * Applies for officer registration.
     * 
     * This method must enforce that:
     * 1. The officer does not have an intention to apply as an Applicant for the same project.
     * 2. The officer is not already approved (or has a pending approved registration) for another project 
     *    within an overlapping application period.
     *
     * @param registration the OfficerRegistration object to submit.
     */
    void applyForOfficerRegistration(OfficerRegistration registration);
    
    /**
     * Retrieves the officer registration applications submitted by a specific officer.
     *
     * @param officerNric the NRIC of the officer.
     * @return a list of OfficerRegistration objects for that officer.
     */
    List<OfficerRegistration> getRegistrationsForOfficer(String officerNric);
}
