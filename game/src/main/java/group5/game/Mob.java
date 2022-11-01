package group5.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Mob extends Circle {
    public static final int DEFAULT_HP = 0;
    int HP = DEFAULT_HP + 3 * Game.LEVEL;

    Timeline mobFire;

    double c_r;
    double c_g;
    double c_b;
    double c_o;

    public Mob() {
        super();
        this.paint();
    }
    public Mob(double radius) {
        super(radius);
        this.paint();
    }
    public Mob(double centerX, double centerY, double radius) {
        super(centerX, centerY, radius);
        this.paint();
    }
    public Mob(double centerX, double centerY, double radius, Paint fill) {
        super(centerX, centerY, radius, fill);
    }
    public Mob(double radius, Paint fill) {
        super(radius, fill);
    }

    public int getHP() {
        return this.HP;
    }
    public void setHP(int hp) {
        this.HP = hp;
    }

    private void paint() {
        double red = Math.random()/4;
        double green = 1 - Math.random()/2;
        double blue = Math.random()/2;
        double opacity = 1 - Math.random()/2;
        this.setFill(Color.color(red, green, blue, opacity));

        Color dead_color = Color.rgb(255,0,0,1);
        double delim = DEFAULT_HP + 3 * Game.LEVEL - 1;
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
            this.setFill(Color.rgb(255,0,0,1));
    }

    public void setTimeline(Pane pane, Player player) {
        this.mobFire = new Timeline(
                new KeyFrame(Duration.millis(Bullet.MOB_FIRE_RATE*(1+2*Math.random())), e -> this.fire(pane, player))
        );
        mobFire.setCycleCount(Timeline.INDEFINITE);
    }
    public void play() {
        this.mobFire.play();
    }
    public void stop() {
        this.mobFire.stop();
    }

    public void fire(Pane pane, Player player) {
        MobBullet bullet = new MobBullet(this.getCenterX(), this.getCenterY()+this.getRadius());
        bullet.setBulletMovement(pane, this, player);
        pane.getChildren().add(bullet);
        bullet.play();
    }

    public boolean takeHit(PlayerBullet bullet, Pane pane) {
        double xmin = this.getCenterX() - this.getRadius() - bullet.getWidth();
        double xmax = this.getCenterX() + this.getRadius();

        if (bullet.getX() >= xmin && bullet.getX() <= xmax) {
            double ymin = this.getCenterY() - this.getRadius() - bullet.getHeight();
            double ymax = this.getCenterY() + this.getRadius();

            if (bullet.getY() >= ymin && bullet.getY() <= ymax) {
                this.setHP(this.getHP() - bullet.getDamage());
                this.updateColor(bullet.getDamage());

                if (this.getHP() < 0) {
                    this.stop();
                    pane.getChildren().remove(this);
                    Game.setMobCount(Game.getMobCount()-1);
                    Game.setLevelScore(Game.getLevelScore()+1);
                }

                return true;
            }
        }

        return false;
    }
}
