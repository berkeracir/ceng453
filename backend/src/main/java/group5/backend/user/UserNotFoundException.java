package group5.backend.user;

import javax.persistence.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException (int id) {
        super("user (id:" + id + ") not found!");
    }
}
