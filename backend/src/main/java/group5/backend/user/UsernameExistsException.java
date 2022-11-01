package group5.backend.user;

import javax.persistence.EntityExistsException;

public class UsernameExistsException extends EntityExistsException {

    public UsernameExistsException (String username) {
        super("Username: '" + username + "' already exists!");
    }
}
