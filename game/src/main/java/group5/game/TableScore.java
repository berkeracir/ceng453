package group5.game;

public class TableScore {

    private String username;
    private String score;
    private String date;

    public TableScore(){
        this.username = "";
        this.score = "";
        this.date = "";
    }

    public TableScore(String username, String score, String date){
        this.username = username;
        this.score = score;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getScore() {
        return this.score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
