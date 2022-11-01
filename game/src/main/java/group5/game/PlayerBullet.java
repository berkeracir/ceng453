package group5.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.Iterator;

public class PlayerBullet extends Bullet {
    Timeline bulletMovement;
    boolean host = true;

    public PlayerBullet() {
        super();
        this.setFill(BULLET_COLOR);
        this.setDamage(DEFAULT_BULLET_DAMAGE*Game.LEVEL);
    }
    public PlayerBullet(double x, double y) {
        super(x - DEFAULT_BULLET_WIDTH/2, y - DEFAULT_BULLET_HEIGHT);
        this.setFill(BULLET_COLOR);
        this.setDamage(DEFAULT_BULLET_DAMAGE*Game.LEVEL);
    }
    public PlayerBullet(double x, double y, boolean host) {
        super(x - DEFAULT_BULLET_WIDTH/2, y - DEFAULT_BULLET_HEIGHT);
        this.setFill(BULLET_COLOR);
        this.setDamage(DEFAULT_BULLET_DAMAGE*Game.LEVEL);
        this.host = host;
    }

    public boolean isHost() {
        return  this.host;
    }

    public void setBulletMovement(Pane pane) {
        this.bulletMovement = new Timeline(
                new KeyFrame(Duration.millis(PLAYER_BULLET_SPEED), e -> this.moveBullet(DEFAULT_BULLET_HEIGHT, pane))
        );
        this.bulletMovement.setCycleCount(Timeline.INDEFINITE);
    }
    public void play() {
        this.bulletMovement.play();
    }
    public void stop() {
        this.bulletMovement.stop();
    }

    public void moveBullet(double value, Pane pane) {
        this.setY(this.getY() - value);

        if (this.getY() < 0) {
            this.stop();
            pane.getChildren().remove(this);
        }
        else if (this.getY() <= Game.lowerYLimit) {
            for (Iterator<Node> iterator=pane.getChildren().iterator(); iterator.hasNext();) {
                Node node = iterator.next();

                if ((node instanceof Mob && ((Mob) node).takeHit(this, pane)) || (node instanceof Boss && ((Boss) node).takeHit(this, pane))) {
                    this.stop();
                    pane.getChildren().remove(this);
                    break;
                }
            }
        }
    }
}
