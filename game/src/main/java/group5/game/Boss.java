package group5.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Boss extends Rectangle {
    public static final double BOSS_SIZE = 100;
    public static final int DEFAULT_HP = 100;

    int HP = 5 * DEFAULT_HP + 5 * Game.LEVEL;
    double MOVEMENT_SPEED = 25;
    double MOVEMENT_DELTA = BOSS_SIZE/20.0;

    double centerX;
    double centerY;

    double c_r, c_g, c_b, c_o;

    Timeline bossMovement, bossFire;

    public Boss(double centerX, double centerY) {
        super(centerX-BOSS_SIZE/2.0, centerY-BOSS_SIZE/2.0, BOSS_SIZE, BOSS_SIZE);
        this.centerX = centerX;
        this.centerY = centerY;

        this.paint();
    }

    public double getCenterX() {
        return this.centerX;
    }
    public void setCenterX(double x) {
        this.centerX = x;
    }
    public double getCenterY() {
        return this.centerY;
    }
    public void setCenterY(double y) {
        this.centerY = y;
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
        double delim = 5 * DEFAULT_HP + 5 * Game.LEVEL - 1;
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

    public void setMovementTimeline(Pane pane) {
        this.bossMovement = new Timeline(
                new KeyFrame(Duration.millis(MOVEMENT_SPEED), e -> this.move(pane))
        );
        bossMovement.setCycleCount(Timeline.INDEFINITE);
    }
    public void playMovement() {
        this.bossMovement.play();
    }
    public void stopMovement() {
        this.bossMovement.stop();
    }
    private void move(Pane pane) {
        if (MOVEMENT_DELTA >= 0) {
            if (this.getX() + MOVEMENT_DELTA + this.getWidth() >= pane.getWidth() - 5) {
                MOVEMENT_DELTA *= -1;
            }
            else {
                this.setX(this.getX() + MOVEMENT_DELTA);
            }
        }
        else {
            if (this.getX() + MOVEMENT_DELTA <= 0 + 5) {
                MOVEMENT_DELTA *= -1;
            }
            else {
                this.setX(this.getX() + MOVEMENT_DELTA);
            }
        }
    }

    public void setFireTimeline(Pane pane, Player player, Player opponent) {
        this.bossFire = new Timeline(
                new KeyFrame(Duration.millis(Bullet.BOSS_FIRE_RATE), e -> this.fire(pane, player, opponent))
        );
        this.bossFire.setCycleCount(Timeline.INDEFINITE);
    }
    public void playFire() {
        this.bossFire.play();
    }
    public void stopFire() {
        this.bossFire.stop();
    }
    private void fire(Pane pane, Player player, Player opponent) {
        int bulletNumber = 5;
        double delim = BOSS_SIZE/bulletNumber;
        double x = this.getX();
        double y = this.getY() + BOSS_SIZE;
        for (int i=0; i<bulletNumber; i++) {
            BossBullet bullet = new BossBullet(x+i*delim, y);
            bullet.setBulletMovement(pane, this, player, opponent);
            pane.getChildren().add(bullet);
            bullet.play();
        }
    }

    public boolean takeHit(PlayerBullet bullet, Pane pane) {
        double xmin = this.getX() - bullet.getWidth();
        double xmax = this.getX() + BOSS_SIZE;

        if (bullet.getX() >= xmin && bullet.getX() <= xmax) {
            double ymin = this.getY() - bullet.getHeight();
            double ymax = this.getY() + BOSS_SIZE;

            if (bullet.getY() >= ymin && bullet.getY() <= ymax) {
                this.setHP(this.getHP() - bullet.getDamage());
                this.updateColor(bullet.getDamage());
                if (bullet.isHost())
                    Game.setLevelScore(Game.getLevelScore() + bullet.getDamage());

                if (this.getHP() < 0) {
                    this.stopMovement();
                    this.stopFire();
                    pane.getChildren().remove(this);
                    Game.setMobCount(Game.getMobCount()-1);
                    if (bullet.isHost())
                        Game.setLevelScore(Game.getLevelScore() + 100);
                }

                return true;
            }
        }

        return false;
    }
}
