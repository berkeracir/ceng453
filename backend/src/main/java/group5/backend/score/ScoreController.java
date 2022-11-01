package group5.backend.score;

import group5.backend.user.*;

import group5.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class ScoreController {

    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private UserRepository userRepository;

    public ScoreController(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    /**
     * Get all score entries in the database.
     *
     * @return                      List of score objects
     */
    @GetMapping("/scores")
    public List<Score> getScores() {
        return scoreRepository.findAll();
    }

    /**
     * Get the score with given score ID.
     *
     * @param sid                       score ID
     * @return                          score object with the given sid
     * @throws ScoreNotFoundException   If score with given sid is not found
     */
    @GetMapping("/scores/{sid}")
    public Score getScore(@PathVariable int sid) throws ScoreNotFoundException {

        return scoreRepository.findById(sid)
                .orElseThrow(() -> new ScoreNotFoundException(sid));
    }

    /**
     * Get all time leaderboard.
     *
     * @return                          List of array of objects: size of object array and the object types can be
     *                                  found from {@link ScoreRepository#getLeaderboardAllTime() getLeaderboardAllTime}
     * @see                             ScoreRepository#getLeaderboardAllTime()
     */
    @GetMapping("/leaderboard_alltime")
    public List<Object[]> leaderboardAllTime() {

        return scoreRepository.getLeaderboardAllTime();
    }

    /**
     * Get last week's leaderboard.
     *
     * @return                          List of array of objects: size of object array and the object types can be
     *                                  found from {@link ScoreRepository#getLeaderboardLastWeek(Date)
     *                                  getLeaderboardLastWeek}
     * @see                             ScoreRepository#getLeaderboardLastWeek(Date)
     */
    @GetMapping("/leaderboard_lastweek")
    public List<Object[]> leaderboardLastWeek() {

        int msOneWeek = 1000 * 60 * 60 * 24 * 7;
        Date date = new Date(System.currentTimeMillis()- msOneWeek);

        return scoreRepository.getLeaderboardLastWeek(date);
    }

    /**
     * Set a score with given score object.
     *
     * @param newScore                  score object to be inserted into the database
     * @return                          score object that is successfully inserted into the database
     * @throws UserNotFoundException    If user with the given score object's user's uid is not found
     * @throws UserNullException        If score object's user is <code>NULL</code>
     */
    @PostMapping("/scores")
    public Score setScore(@RequestBody Score newScore) throws UserNotFoundException, UserNullException {

        if (newScore.getUser() != null) {
            ;
            newScore.setUser(userRepository.findById(newScore.getUser().getUid())
                    .orElseThrow(() -> new UserNotFoundException(newScore.getUser().getUid())));
            return scoreRepository.save(newScore);
        }
        else {
            throw new UserNullException();
        }
    }

    /**
     * Set a score with given score object with given user ID.
     *
     * @param newScore                  score object to be inserted into the database
     * @param uid                       user ID of the score
     * @return                          score object that is successfully inserted into the database with the given uid
     * @throws UserNotFoundException    If user with given uid is not found
     * @throws WrongScoreEntryException If the given score object's user's uid is not equal to the given uid
     */
    @PostMapping("/scores/uid:{uid}")
    public Score setScore(@RequestBody Score newScore, @PathVariable int uid) throws UserNotFoundException, WrongScoreEntryException {

        if (newScore.getUser() == null) {

            User user = userRepository.findById(uid)
                    .orElseThrow(() -> new UserNotFoundException(uid));
            newScore.setUser(user);
            return scoreRepository.save(newScore);
        }
        else if (newScore.getUser().getUid() == uid) {

            userRepository.findById(newScore.getUser().getUid())
                    .orElseThrow(() -> new UserNotFoundException(newScore.getUser().getUid()));
            return scoreRepository.save(newScore);
        }
        else {

            throw new WrongScoreEntryException(newScore.getUser().getUid(), uid);
        }

    }

    /**
     * Delete the score with given sid from the database.
     *
     * @param sid
     * @throws ScoreNotFoundException   If score with given sid is not found
     */
    @DeleteMapping("/scores/{sid}")
    public void deleteScore(@PathVariable int sid) throws ScoreNotFoundException {

        scoreRepository.findById(sid)
                .orElseThrow(() -> new ScoreNotFoundException(sid));
        scoreRepository.deleteById(sid);
    }

    /**
     * Delete each score entry in the database and alter the AUTO_INCREMENT of `scores` table to 0.
     */
    @DeleteMapping("/scores/*")
    public void deleteAllScores() {

        scoreRepository.deleteAll();
        scoreRepository.resetAutoIncrement();
    }
}
