package group5.backend.score;

public class WrongScoreEntryException extends Exception {

    public WrongScoreEntryException (int score_uid, int uid) {

        super("Wrong use of user ID: user.uid=" + score_uid + " uid=" + uid + "!");
    }
}
