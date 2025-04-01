package auth;

public class User {
    private String name;
    private String nric;
    private int age;
    private String maritalStatus;
    private UserType userType;

    public User(String name, String nric, int age, String maritalStatus, UserType userType) {
        this.name = name;
        this.nric = nric;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.userType = userType;
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

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public UserType getUserType() {
        return userType;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
}
