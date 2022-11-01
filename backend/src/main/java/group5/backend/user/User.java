package group5.backend.user;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uid;
    @Column(length = 24, nullable = false, unique = true)
    private String username;
    @Column(length = 255)
    private String password = "";

    /*@OneToMany(mappedBy = "user")
    private List<score> scores;*/

    public User() {    }

    public User(String username) {
        this.setUsername(username);
    }

    public User(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public User(int id, String username, String password) {
        this.setUid(id);
        this.setUsername(username);
        this.setPassword(password);
    }

    public int getUid() {
        return this.uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "user{" + "id=" + uid +
                ", username='" + username + "'" +
                ", password='" + password + "'" +
                "}";
    }
}
