package group5.backend.score;

import javax.persistence.EntityNotFoundException;

public class ScoreNotFoundException extends EntityNotFoundException {

    public ScoreNotFoundException (int id) {

        super("score (id:" + id + ") not found!");
    }
}
