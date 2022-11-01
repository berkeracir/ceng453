package group5.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class BossBullet extends Bullet {
    Timeline bulletMovement;

    public BossBullet() {
        super();
        this.setFill(BOSS_BULLET_COLOR);
        this.setDamage(this.getDamage()*2);
    }
    public BossBullet(double x, double y) {
        super(x - DEFAULT_BULLET_WIDTH/2, y);
        this.setFill(BOSS_BULLET_COLOR);
        this.setDamage(this.getDamage()*2);
    }

    public void setBulletMovement(Pane pane, Boss boss, Player player, Player opponent) {
        this.bulletMovement = new Timeline(
                new KeyFrame(Duration.millis(BOSS_BULLET_SPEED), e -> this.moveBullet(DEFAULT_BULLET_HEIGHT, pane, player, opponent))
        );
        this.bulletMovement.setCycleCount(Timeline.INDEFINITE);
    }
    public void play() {
        this.bulletMovement.play();
    }
    public void stop() {
        this.bulletMovement.stop();
    }

    public void moveBullet(double value, Pane pane, Player player, Player opponent) {
        this.setY(this.getY() + value);
        boolean flag = false;

        if (this.getY() > pane.getHeight()) {//(this.getY() > Game.GAME_SCENE_HEIGHT - 20 || (player.getHP() > 0 && player.takeHit(this, pane)) || opponent.takeHit(this, pane)) {
            flag = true;
        }

        if (player.getHP() > 0 && player.takeHit(this, pane)) {
            flag = true;
        }

        if (opponent.getHP() > 0 && opponent.takeHit(this, pane)) {
            flag = true;
        }

        if (flag) {
            this.stop();
            pane.getChildren().remove(this);
        }
    }
}
