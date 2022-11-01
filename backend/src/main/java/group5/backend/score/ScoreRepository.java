package group5.backend.score;

import group5.backend.user.UserController;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Integer> {

    /**
     * Return the query, which is the each user's maximum score, result.
     *
     * Query:
     *      SELECT s.user.uid, s.user.username, s.score, MAX(s.date)
     *      FROM score s
     *      WHERE (s.user.uid, s.score) IN (
     *         SELECT ss.user.uid, MAX(ss.score)
     *         FROM score ss
     *         GROUP BY ss.user)
     *      GROUP BY s.user
     *      ORDER BY s.score DESC, MAX(s.date) DESC
     *
     * @return          List of Object Array which is equal to: {@code List<[int, String, int, Date]>}
     */
    @Query("SELECT s.user.uid, s.user.username, s.score, MAX(s.date) " +
            "FROM Score s " +
            "WHERE (s.user.uid, s.score) IN " +
                "(SELECT ss.user.uid, MAX(ss.score) " +
                "FROM Score ss " +
                "GROUP BY ss.user) "  +
            "GROUP BY s.user " +
            "ORDER BY s.score DESC, MAX(s.date) DESC")
    List<Object[]> getLeaderboardAllTime();

    /**
     * Return the query, which is the each user's maximum score that is after the given date, result.
     *
     * Query:
     *      SELECT s.user.uid, s.user.username, s.score, MAX(s.date)
     *      FROM score s
     *      WHERE (s.user.uid, s.score, s.date) IN (
     *         SELECT ss.user.uid, MAX(ss.score), MAX(ss.date)
     *         FROM score ss
     *         WHERE ss.date >= {@code date}
     *         GROUP BY ss.user)
     *      GROUP BY s.user
     *      ORDER BY MAX(s.score) DESC, s.date DESC
     *
     * @param date      Date after which max user scores are listed
     * @return          List of Object Array which is equal to: {@code List<[int, String, int, Date]>}
     */
    @Query("SELECT s.user.uid, s.user.username, s.score, MAX(s.date) " +
            "FROM Score s " +
            "WHERE (s.user.uid, s.score) IN " +
                "(SELECT ss.user.uid, MAX(ss.score) " +
                "FROM Score ss " +
                "WHERE ss.date >= :start " +
                "GROUP BY ss.user) " +
            "GROUP BY s.user " +
            "ORDER BY MAX(s.score) DESC, MAX(s.date) DESC")
    List<Object[]> getLeaderboardLastWeek(@Param("start") Date date);


    /**
     * Set the AUTO_INCREMENT value to 1 for `scores` table.
     * It is used when the `users` and `scores` tables resetted (i.e. {@link ScoreController#deleteAllScores()
     * deleteAllScores} and {@link UserController#deleteAllUsers()}).
     */
    @Query(value = "ALTER TABLE scores AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}
