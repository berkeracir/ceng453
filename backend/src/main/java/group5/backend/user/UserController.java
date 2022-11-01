package group5.backend.user;

import group5.backend.score.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ScoreRepository scoreRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get all user entries in the database.
     *
     * @return                          List of user objects
     */
    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * Register an user into the database and return the user object.
     *
     * @param newUser                   user object to be registered into database
     * @return                          user object that is successfully registered into database
     * @throws UsernameExistsException  If the given username already exists
     * @throws UserNullException        If the user is <code>NULL</code> or the username is <code>""</code>
     */
    @PostMapping("/register")
    public User register(@RequestBody User newUser) throws UsernameExistsException, UserNullException {

        if (newUser.getUsername() != null && !newUser.getUsername().equals("") && newUser.getUsername().length() <= 24) {
            if (userRepository.existsByUsername(newUser.getUsername())) {

                throw new UsernameExistsException(newUser.getUsername());
            }
            else {
                newUser.setPassword(new BCryptPasswordEncoder().encode(newUser.getPassword()));
                return userRepository.save(newUser);
            }
        }
        else {
            throw new UserNullException();
        }
    }

    /**
     * Login into the system with given user object.
     *
     * @param user                  user object to login into the system
     * @return                      <code>true</code> if the given username and password correct,
     *                              <code>false</code> otherwise
     * @throws UserNullException    If the user is <code>NULL</code> or the username is <code>""</code>
     */
    @PostMapping("/login")
    public User login(@RequestBody User user) throws UserNullException {
        User defaultUser = new User("", "");
        if (user.getUsername() != null && !user.getUsername().equals("") && user.getUsername().length() <= 24) {
            if (userRepository.existsByUsername(user.getUsername())) {

                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                if (user.getPassword() != null) {

                    if (encoder.matches(user.getPassword(), userRepository.findByUsername(user.getUsername()).getPassword())){
                        return userRepository.findByUsername(user.getUsername());
                    }

                    else {
                        return defaultUser;
                    }
                }
                else {

                    return defaultUser;
                }
            }
            else {
                return defaultUser;
            }
        }
        else {
            throw new UserNullException();
        }
    }

    /**
     * Get the user with given user ID.
     *
     * @param uid                       user ID
     * @return                          user object with the given uid
     * @throws UserNotFoundException    If user with given uid is not found
     */
    @GetMapping("/users/{uid}")
    public User getUser(@PathVariable int uid) throws UserNotFoundException {

        return userRepository.findById(uid)
                .orElseThrow(() -> new UserNotFoundException(uid));
    }

    /**
     * Update the user with given uid to given user object.
     *
     * @param newUser                   user object to be inserted into the database
     * @param uid                       user ID of the object, which will be updated, in the database
     * @return                          Successfully updated user object
     * @throws UserNotFoundException    If user with that uid is not found
     * @throws UsernameExistsException  If the new username already exists
     */
    @PutMapping("/users/{uid}")
    public User updateUser(@RequestBody User newUser, @PathVariable int uid) throws UserNotFoundException, UsernameExistsException {

        if (userRepository.findById(uid).orElseThrow(() -> new UserNotFoundException(uid))
                .getUsername().equals(newUser.getUsername()) ||
                !userRepository.existsByUsername(newUser.getUsername())) {

            return userRepository.findById(uid)
                    .map(user -> {
                        user.setUsername(newUser.getUsername());
                        user.setPassword(new BCryptPasswordEncoder().encode(newUser.getPassword()));
                        return userRepository.save(user);
                    })
                    .orElseGet(() -> {
                        newUser.setUid(uid);
                        return userRepository.save(newUser);
                    });
        }
        else {
            throw new UsernameExistsException(newUser.getUsername());
        }
    }

    /**
     * Delete the user with given uid from the database.
     *
     * @param uid
     * @throws UserNotFoundException    If user with given uid is not found
     */
    @DeleteMapping("/users/{uid}")
    public void deleteUser(@PathVariable int uid) throws UserNotFoundException {

        userRepository.findById(uid)
                .orElseThrow(() -> new UserNotFoundException(uid));
        userRepository.deleteById(uid);
    }

    /**
     *  Delete each user entry in the database and alter the AUTO_INCREMENT of `scores` and `users` tables to 0.
     */
    @DeleteMapping("/users/*")
    public void deleteAllUsers() {

        userRepository.deleteAll();
        userRepository.resetAutoIncrement();
        scoreRepository.resetAutoIncrement();
    }
}