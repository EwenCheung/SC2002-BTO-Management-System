package users;

import users.enums.MaritalStatus;
import users.enums.UserType;


public class ProjectManager extends User{

    public ProjectManager(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, UserType.MANAGER, password);
    }

}
