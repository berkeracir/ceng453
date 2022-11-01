package group5.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Test the database whether the user with given username exists or not.
     *
     * @param username  String for the username
     * @return          <code>true</code> if the user with given username exists,
     *                  <code>false</code> otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Find the user object with given username from the database.
     *
     * @param username  String for the username
     * @return          Return user object if the user with given username found,
     *                  <code>NULL</code> otherwise
     */
    User findByUsername(String username);

    /**
     * Sets the AUTO_INCREMENT value to 1 for `users` table.
     * It is used when the `users` table resetted (i.e. {@link UserController#deleteAllUsers() deleteAllUsers}).
     */
    @Query(value = "ALTER TABLE users AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
    @Query(value = "ALTER TABLE users AUTO_INCREMENT = :uid", nativeQuery = true)
    void resetAutoIncrement(@Param("uid") int uid);
}
