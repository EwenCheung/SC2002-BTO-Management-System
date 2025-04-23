package users;

import users.enums.*;

/**
 * Abstract base class representing a user in the BTO Management System.
 * Contains common attributes and behaviors shared by all user types
 * such as applicants, HDB officers, and project managers.
 */
public abstract class User {
    private String name;
    private String nric;
    private int age;
    private MaritalStatus maritalStatus;
    private UserType userType;
    private String password;

    /**
     * Constructor to create a new user with all required attributes.
     *
     * @param name           The full name of the user
     * @param nric           The NRIC (National Registration Identity Card) number of the user
     * @param age            The age of the user in years
     * @param maritalStatus  The marital status of the user
     * @param userType       The type of user (applicant, officer, or manager)
     * @param password       The password for user authentication
     */
    public User(String name, String nric, int age, MaritalStatus maritalStatus, UserType userType, String password) {
        this.name = name;
        this.nric = nric;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.userType = userType;
        this.password = password;
    }

    // Getters
    /**
     * Gets the name of the user.
     *
     * @return The user's full name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the NRIC of the user.
     *
     * @return The user's NRIC number
     */
    public String getNric() {
        return nric;
    }

    /**
     * Gets the age of the user.
     *
     * @return The user's age in years
     */
    public int getAge() {
        return age;
    }

    /**
     * Gets the marital status of the user.
     *
     * @return The user's marital status
     */
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Gets the type of the user.
     *
     * @return The user type (applicant, officer, or manager)
     */
    public UserType getUserType() {
        return userType;
    }

    /**
     * Gets the password of the user.
     *
     * @return The user's password
     */
    public String getPassword(){
        return password;
    }

    // Setters
    /**
     * Updates the name of the user.
     *
     * @param name The new name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Updates the age of the user.
     *
     * @param age The new age to set
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Updates the marital status of the user.
     *
     * @param maritalStatus The new marital status to set
     */
    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    /**
     * Updates the user type.
     *
     * @param userType The new user type to set
     */
    public void setUserType(UserType userType){
        this.userType=userType;
    }

    /**
     * Updates the password of the user.
     *
     * @param password The new password to set
     */
    public void setPassword(String password){
        this.password = password;
    }
}
