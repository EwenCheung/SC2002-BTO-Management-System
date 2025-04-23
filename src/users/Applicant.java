package users;

import users.enums.MaritalStatus;
import users.enums.UserType;

/**
 * Represents an applicant user in the BTO Management System.
 * Applicants can apply for BTO projects, submit enquiries, and request withdrawals.
 * This class extends the User base class with specific applicant behavior.
 */
public class Applicant extends User{

    /**
     * Constructor for creating a new Applicant.
     * Sets the user type to APPLICANT automatically.
     *
     * @param name           The applicant's full name
     * @param nric           The applicant's NRIC (National Registration Identity Card) number
     * @param age            The applicant's age in years
     * @param maritalStatus  The applicant's marital status
     * @param password       The applicant's login password
     */
    public Applicant(String name, String nric, int age, MaritalStatus maritalStatus, String password){
        super(name, nric, age, maritalStatus, UserType.APPLICANT, password);
    }

}
