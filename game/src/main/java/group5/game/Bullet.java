package group5.game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Bullet extends Rectangle {
    public static int DEFAULT_BULLET_DAMAGE = 1;
    public static final Color BULLET_COLOR = Color.rgb(255,255, 0);
    public static final Color MOB_BULLET_COLOR = Color.rgb(255,0, 0);
    public static final Color BOSS_BULLET_COLOR = Color.rgb(255,127,0);
    public static final double DEFAULT_BULLET_WIDTH = 5;
    public static final double DEFAULT_BULLET_HEIGHT = 15;

    public static final int PLAYER_FIRE_RATE = 80;
    public static final int PLAYER_BULLET_SPEED = 40;

    public static final int MOB_FIRE_RATE = 1200;
    public static final int MOB_BULLET_SPEED = 80;

    public static final int BOSS_FIRE_RATE = 600;
    public static final int BOSS_BULLET_SPEED = 100;

    int damage = DEFAULT_BULLET_DAMAGE;// * Game.LEVEL;

    public Bullet() {
        super();
        this.setWidth(DEFAULT_BULLET_WIDTH);
        this.setHeight(DEFAULT_BULLET_HEIGHT);
    }
    public Bullet(double x, double y) {
        super(x, y, DEFAULT_BULLET_WIDTH, DEFAULT_BULLET_HEIGHT);
    }

    public int getDamage() {
        return this.damage;
    }
    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void moveBullet(double value, boolean fromMob) {
        if (fromMob)
            this.setY(this.getY() + value);
        else
            this.setY(this.getY() - value);
    }
}
