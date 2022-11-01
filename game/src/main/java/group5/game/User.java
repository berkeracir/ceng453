package group5.game;

public class User {
    private int uid;

    private String username;

    private String password = "";


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
