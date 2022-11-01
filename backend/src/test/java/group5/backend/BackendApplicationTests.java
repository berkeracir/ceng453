package group5.backend;

import group5.backend.user.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static java.lang.Math.min;
import static org.junit.Assert.assertEquals;
import static sun.swing.MenuItemLayoutHelper.max;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BackendApplicationTests {

    @Autowired
    UserController userController;
    @Autowired
    UserRepository userRepository;

    private BCryptPasswordEncoder passEncoder = new BCryptPasswordEncoder();

    private int min_uid = Integer.MAX_VALUE;
    private int max_uid = 0;
    private int count = 0;

    @Test // Successfull Register & Login Case
    public void registerAndLoginUserTest() throws Exception {

        System.out.println("######## TEST REGISTER AND LOGIN");
        String username = UUID.randomUUID().toString().substring(0,20);
        String password = UUID.randomUUID().toString().substring(0,20);
        User user = new User(username, password);

        User registered_user = userController.register(user);
        System.out.println("[TEST-1] user created: " + user);
        min_uid = min(min_uid, registered_user.getUid());
        max_uid = max(max_uid, registered_user.getUid());

        assertEquals(user.getUsername(), registered_user.getUsername());
        assertEquals(true, userController.login(new User(registered_user.getUsername(), password)));

        System.out.println("[TEST-1] OKAY");
    }

    @Test   // Extreme user Cases:
    //          (1) username is null
    //          (2) username is ""
    //          (3) existing username
    //          (4) long username
    //          (5) password is null
    //          (6) password is ""
    public void extremeUserTest() throws Exception {

        boolean okay_flag = true;

        System.out.println("######## TEST EXTREME USER CASES");
        System.out.println("(1)- null username, (2)- \"\" username, (3)- existing username, (4)- long username, (5)- null password");
        String username = UUID.randomUUID().toString().substring(0,20);
        String password = UUID.randomUUID().toString().substring(0,20);
        //user user = new user(null, password);

        try {
            User user = new User(null, password);
            User registered_user = userController.register(user);
            min_uid = min(min_uid, registered_user.getUid());
            max_uid = max(max_uid, registered_user.getUid());
            System.out.println("[TEST-2 (1)] FAIL");
        } catch (UserNullException ex) {
            System.out.println("[TEST-2 (1)] OKAY with Exception: " + ex.getMessage());
        }
        catch (Exception ex) {
            System.out.println("[TEST-2 (1)] Unexpected Exception: " + ex.getMessage());
        }

        try {
            User user = new User("", password);
            User registered_user = userController.register(user);
            min_uid = min(min_uid, registered_user.getUid());
            max_uid = max(max_uid, registered_user.getUid());
            System.out.println("[TEST-2 (2)] FAIL");
            okay_flag = false;
        } catch (UserNullException ex) {
            System.out.println("[TEST-2 (2)] OKAY with Exception: " + ex.getMessage());
        }
        catch (Exception ex) {
            System.out.println("[TEST-2 (2)] Unexpected Exception: " + ex.getMessage());
            okay_flag = false;
        }

        try {
            User user = new User(username, password);
            User registered_user = userController.register(user);
            System.out.println("[TEST-2 (3)] user created: " + user);
            min_uid = min(min_uid, registered_user.getUid());
            max_uid = max(max_uid, registered_user.getUid());

            User testUser = new User(username, password);
            User test_registeredUser = userController.register(testUser);
            min_uid = min(min_uid, test_registeredUser.getUid());
            max_uid = max(max_uid, test_registeredUser.getUid());

            System.out.println("[TEST-2 (3)] FAIL");
            okay_flag = false;
        } catch (UsernameExistsException ex) {
            System.out.println("[TEST-2 (3)] OKAY with Exception: " + ex.getMessage());
        }
        catch (Exception ex) {
            System.out.println("[TEST-2 (3)] Unexpected Exception: " + ex.getMessage());
            okay_flag = false;
        }

        try {
            String long_username = UUID.randomUUID().toString().substring(0,30);
            User user = new User(long_username, password);
            User registered_user = userController.register(user);
            min_uid = min(min_uid, registered_user.getUid());
            max_uid = max(max_uid, registered_user.getUid());

            System.out.println("[TEST-2 (4)] FAIL");
            okay_flag = false;
        } catch (UserNullException ex) {
            System.out.println("[TEST-2 (4)] OKAY with Exception: " + ex.getMessage());
        }
        catch (Exception ex) {
            System.out.println("[TEST-2 (4)] Unexpected Exception: " + ex.getMessage());
            okay_flag = false;
        }

        try {
            username = UUID.randomUUID().toString().substring(0,30);
            User user = new User(username, null);
            User registered_user = userController.register(user);
            min_uid = min(min_uid, registered_user.getUid());
            max_uid = max(max_uid, registered_user.getUid());

            System.out.println("[TEST-2 (5)] FAIL");
            okay_flag = false;
        } catch (UserNullException ex) {
            System.out.println("[TEST-2 (5)] OKAY with Exception: " + ex.getMessage());
        }
        catch (Exception ex) {
            System.out.println("[TEST-2 (5)] Unexpected Exception: " + ex.getMessage());
            okay_flag = false;
        }

        try {
            username = UUID.randomUUID().toString().substring(0,20);
            User user = new User(username, "");
            User registered_user = userController.register(user);
            System.out.println("[TEST-2 (6)] user created: " + user);
            min_uid = min(min_uid, registered_user.getUid());
            max_uid = max(max_uid, registered_user.getUid());

            System.out.println("[TEST-2 (6)] OKAY");
            okay_flag = true;
        }
        catch (Exception ex) {
            System.out.println("[TEST-2 (6)] Unexpected Exception: " + ex.getMessage());
            okay_flag = false;
        }


        if (okay_flag)
            System.out.println("[TEST-2] OKAY");
        else
            System.out.println("[TEST-2] FAIL");
    }

    @Test // Update user Cases, change:
    //          (1) Only Username
    //          (2) Only Password
    //          (3) Nothing
    //          (4) Username and password
    //          (5) Different given uid
    public void updateUserTest() throws Exception {

        System.out.println("######## TEST UPDATE AND LOGIN");
        System.out.println("(1) Only Username, (2) Only Password, (3) Nothing, (4) Username and password, (5) Different given uid");
        String username = UUID.randomUUID().toString().substring(0,20);
        String password = UUID.randomUUID().toString().substring(0,20);
        String new_username = UUID.randomUUID().toString().substring(0,20);
        String new_password = UUID.randomUUID().toString().substring(0,20);

        User user = new User(username, password);
        User registered_user = userController.register(user);
        System.out.println("[TEST-3] user created: " + user);
        assertEquals(true, userController.login(new User(username, password)));
        min_uid = min(min_uid, registered_user.getUid());
        max_uid = max(max_uid, registered_user.getUid());
        int uid = registered_user.getUid();

        User only_name = new User(new_username, password);
        User only_pass = new User(new_username, new_password);
        User nothing   = new User(new_username, new_password);
        User both      = new User(username, password);

        User updated_user_1 = userController.updateUser(only_name, uid);
        assertEquals(updated_user_1.getUid(), uid);
        assertEquals(new_username, updated_user_1.getUsername());
        assertEquals(true, userController.login(new User(new_username, password)));
        System.out.println("[TEST-3 (1)] OKAY");

        User updated_user_2 = userController.updateUser(only_pass, uid);
        assertEquals(updated_user_2.getUid(), uid);
        assertEquals(true, userController.login(new User(new_username, new_password)));
        System.out.println("[TEST-3 (2)] OKAY");

        User updated_user_3 = userController.updateUser(nothing, uid);
        assertEquals(updated_user_3.getUid(), uid);
        assertEquals(new_username, updated_user_3.getUsername());
        assertEquals(true, userController.login(new User(new_username, new_password)));
        System.out.println("[TEST-3 (3)] OKAY");

        User updated_user_4 = userController.updateUser(both, uid);
        assertEquals(updated_user_4.getUid(), uid);
        assertEquals(username, updated_user_4.getUsername());
        assertEquals(true, userController.login(new User(username, password)));
        System.out.println("[TEST-3 (4)] OKAY");


        try {
            User updated_user_5 = userController.updateUser(both, uid + 1);
            min_uid = min(min_uid, updated_user_5.getUid());
            max_uid = max(max_uid, updated_user_5.getUid());

            System.out.println("[TEST-3 (5)] FAIL");
            System.out.println("[TEST-3] FAIL");
        }
        catch (UserNotFoundException ex) {
            System.out.println("[TEST-3 (5)] OKAY with Exception " + ex.getMessage());
            System.out.println("[TEST-3] OKAY");
        }
        catch (UsernameExistsException ex) {
            System.out.println("[TEST-3 (5)] OKAY with Exception " + ex.getMessage());
            System.out.println("[TEST-3] OKAY");
        }
        catch (Exception ex) {
            System.out.println("[TEST-3 (5)] Unexpected Exception: " + ex.getMessage());
            System.out.println("[TEST-3] FAIL");
        }
    }

    @Test
    public void cleanUpTest() throws Exception {

        System.out.println("######## CLEAN ALL TEST ENTRIES");

        for (int i = min_uid; i <= max_uid; i++) {

            if (userRepository.existsById(i)) {
                System.out.println("[CLEAN UP] user deleted:" + userController.getUser(i));
                userController.deleteUser(i);
            }

            userRepository.resetAutoIncrement(min_uid);
        }
    }

}