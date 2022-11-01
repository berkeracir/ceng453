package group5.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Player extends Circle {
    public static final int DEFAULT_HP = 10;
    int HP = DEFAULT_HP * Game.LEVEL;
    boolean host = true;

    Timeline playerFire;

    double c_r;
    double c_g;
    double c_b;
    double c_o;

    public Player() {
        super();
        this.paint();
    }
    public Player(double radius) {
        super(radius);
        this.paint();
    }
    public Player(double centerX, double centerY, double radius) {
        super(centerX, centerY, radius);
        this.paint();
    }
    public Player(double centerX, double centerY, double radius, boolean host) {
        super(centerX, centerY, radius);
        this.paint();
        this.host = host;
    }
    public Player(double centerX, double centerY, double radius, Paint fill) {
        super(centerX, centerY, radius, fill);
    }
    public Player(double radius, Paint fill) {
        super(radius, fill);
    }

    public int getHP() {
        return this.HP;
    }
    public void setHP(int hp) {
        this.HP = hp;
    }

    private void paint() {
        double red = 0;
        double green = Math.random()/2;
        double blue = 1 - Math.random()/2;
        double opacity = 1 - Math.random()/2;
        this.setFill(Color.color(red, green,  blue, opacity));

        Color dead_color = Color.rgb(255,255,0,1);
        double delim = DEFAULT_HP * Game.LEVEL - 1;
        this.c_r = (dead_color.getRed()-red)/delim;
        this.c_g = (dead_color.getGreen()-green)/delim;
        this.c_b = (dead_color.getBlue())-blue/delim;
        this.c_o = (dead_color.getOpacity()-opacity)/delim;
    }

    private void updateColor(int k) {
        Color c = (Color) this.getFill();

        if (k < (HP-1))
            this.setFill(new Color(c.getRed()+k*c_r, c.getGreen()+k*c_g, c.getBlue()+k*c_b, c.getOpacity()+k*c_o));
        else
            this.setFill(Color.rgb(255,255,0,1));
    }

    public void setTimeline(Pane pane) {
        this.playerFire = new Timeline(
                new KeyFrame(Duration.millis(Bullet.PLAYER_FIRE_RATE), e -> this.fire(pane))
        );
        this.playerFire.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                while (pane.getChildren().remove(this));
            }
        });
        this.playerFire.setCycleCount(Timeline.INDEFINITE);
    }
    public void play() {
        this.playerFire.play();
    }
    public void stop() {
        this.playerFire.stop();
    }

    public void fire(Pane pane) {
        PlayerBullet bullet = new PlayerBullet(this.getCenterX(), this.getCenterY()-this.getRadius(), host);
        bullet.setBulletMovement(pane);
        pane.getChildren().add(bullet);
        bullet.play();
    }

    public boolean takeHit(MobBullet bullet, Pane pane) {
        double xmin = this.getCenterX() - this.getRadius() - bullet.getWidth();
        double xmax = this.getCenterX() + this.getRadius();

        if (bullet.getX() >= xmin && bullet.getX() <= xmax) {
            double ymin = this.getCenterY() - this.getRadius() - bullet.getHeight();
            double ymax = this.getCenterY() + this.getRadius();

            if (bullet.getY() >= ymin && bullet.getY() <= ymax) {
                this.setHP(this.getHP() - bullet.getDamage());
                if (host)
                    Game.setPlayerHP(this.getHP());
                this.updateColor(bullet.getDamage());

                if (this.getHP() < 0) {
                    this.stop();
                    pane.getChildren().remove(this);
                }

                return true;
            }
        }

        return false;
    }

    public boolean takeHit(BossBullet bullet, Pane pane) {
        double xmin = this.getCenterX() - this.getRadius() - bullet.getWidth();
        double xmax = this.getCenterX() + this.getRadius();

        if (bullet.getX() >= xmin && bullet.getX() <= xmax) {
            double ymin = this.getCenterY() - this.getRadius() - bullet.getHeight();
            double ymax = this.getCenterY() + this.getRadius();

            if (bullet.getY() >= ymin && bullet.getY() <= ymax) {
                this.setHP(this.getHP() - bullet.getDamage());
                if (host)
                    Game.setPlayerHP(this.getHP()); // TODO
                this.updateColor(bullet.getDamage());

                if (this.getHP() <= 0) {
                    this.stop();
                    pane.getChildren().remove(this);
                }

                return true;
            }
        }

        return false;
    }
}
