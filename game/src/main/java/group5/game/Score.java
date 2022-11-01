package group5.game;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
public class Score {
    private String username;
    private int score;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date date = new Date();

    public Score(){
        this.username = "";
        this.score = 0;
    }

    public Score(String username, int score, Date date){
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
