package users;

import users.enums.MaritalStatus;
import users.enums.UserType;

public class Applicant extends User{

    public Applicant(String name, String nric, int age, MaritalStatus maritalStatus, String password){
        super(name, nric, age, maritalStatus, UserType.APPLICANT, password);
    }

}
