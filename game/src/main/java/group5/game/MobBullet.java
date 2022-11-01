package group5.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class MobBullet extends Bullet {
    Timeline bulletMovement;

    public MobBullet() {
        super();
        this.setFill(MOB_BULLET_COLOR);
    }
    public MobBullet(double x, double y) {
        super(x - DEFAULT_BULLET_WIDTH/2, y);
        this.setFill(MOB_BULLET_COLOR);
    }

    public void setBulletMovement(Pane pane, Mob mob, Player player) {
        this.bulletMovement = new Timeline(
                new KeyFrame(Duration.millis(MOB_BULLET_SPEED*(1+Math.random())), e -> this.moveBullet(DEFAULT_BULLET_HEIGHT, pane, player))
        );
        this.bulletMovement.setCycleCount(Timeline.INDEFINITE);
    }
    public void play() {
        this.bulletMovement.play();
    }
    public void stop() {
        this.bulletMovement.stop();
    }

    public void moveBullet(double value, Pane pane, Player player) {
        this.setY(this.getY() + value);

        if (this.getY() > Game.GAME_SCENE_HEIGHT - 20 || player.takeHit(this, pane)) {
            this.stop();
            pane.getChildren().remove(this);
        }
    }
}
