package io;

import users.User;
import static utils.Constants.DELIMITER;

public class UserSerializer {

    
    /**
     * Serializes a User object into a string that can be saved to a text file.
     * Format: Name||NRIC||Age||Marital Status||Password||UserType
     * 
     * @param user The User object to serialize.
     * @return A string representation of the User.
     */
    public static String serialize(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getName()).append(DELIMITER)
          .append(user.getNric()).append(DELIMITER)
          .append(user.getAge()).append(DELIMITER)
          .append(user.getMaritalStatus().toString()).append(DELIMITER)
          .append(user.getPassword()).append(DELIMITER)
          .append(user.getUserType().toString());
        return sb.toString();
    }
}
