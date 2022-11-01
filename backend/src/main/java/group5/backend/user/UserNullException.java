package group5.backend.user;

public class UserNullException extends NullPointerException {

    public UserNullException() {
        super("user cannot be null!");
    }
}