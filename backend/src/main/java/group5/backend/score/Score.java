package group5.backend.score;

import com.fasterxml.jackson.annotation.JsonFormat;
import group5.backend.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "scores")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sid;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uid")
    @NotNull
    private User user;
    private int score;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date date = new Date();

    public Score() {    }

    public Score(User user, int score) {
        this.setUser(user);
        this.setScore(score);
    }

    public Score(int sid, User user, int score) {
        this.setId(sid);
        this.setUser(user);
        this.setScore(score);
    }

    public int getId() {
        return this.sid;
    }
    public void setId(int sid) {
        this.sid = sid;
    }

    public User getUser() {
        return this.user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public int getScore() {
        return  this.score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    public Date getDate() {
        return this.date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "score{" + "sid=" + sid +
                ", uid=" + user.getUid() +
                ", score=" + score +
                "}";
    }
}
