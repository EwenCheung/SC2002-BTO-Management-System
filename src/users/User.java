package users;

import users.enums.*;

public abstract class User {
    private String name;
    private String nric;
    private int age;
    private MaritalStatus maritalStatus;
    private UserType userType;
    private String password;

    public User(String name, String nric, int age, MaritalStatus maritalStatus, UserType userType, String password) {
        this.name = name;
        this.nric = nric;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.userType = userType;
        this.password = password;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getNric() {
        return nric;
    }

    public int getAge() {
        return age;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public UserType getUserType() {
        return userType;
    }

    public String getPassword(){
        return password;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public void setUserType(UserType userType){
        this.userType=userType;
    }

    public void setPassword(String password){
        this.password = password;
    }
}
