package group5.game;


import group5.game.Bullet;
import group5.game.Game;
import group5.game.Mob;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class mobClassTest extends Application {
    @Override
    public void start(Stage primaryStage) {
        Pane pane = new Pane();

        Mob monster1 = new Mob();
        monster1.setRadius(20);
        monster1.setCenterX(20);
        monster1.setCenterY(20);

        pane.getChildren().add(monster1);

        System.out.println("Default HP: " + Mob.DEFAULT_HP);
        System.out.println("Monster1 HP: " + monster1.getHP() + " at level " +  Game.LEVEL);

        //Game.setLEVEL(2);

        Mob monster2 = new Mob(50, 20, 10);
        pane.getChildren().add(monster2);
        System.out.println("Monster2 HP: " + monster2.getHP() + " at level " +  Game.LEVEL);

        //Game.setLEVEL(3);

        Mob monster3 = new Mob(75, 20, 15, Bullet.BULLET_COLOR);
        pane.getChildren().add(monster3);
        System.out.println("Monster3 HP: " + monster3.getHP() + " at level " +  Game.LEVEL);

        Bullet bullet1 = new Bullet(100, 20);
        System.out.println("Bullet1 at x:" + bullet1.getX() + " y:" + bullet1.getY());
        pane.getChildren().add(bullet1);

        Bullet bullet2 = new Bullet(105, 20);
        System.out.println("Bullet2 at x:" + bullet2.getX() + " y:" + bullet2.getY());
        bullet2.moveBullet(15, true);
        pane.getChildren().add(bullet2);
        Scene scene = new Scene(pane, 400, 400);

        primaryStage.setTitle("Mob Class Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main method is only needed for the IDE with limited
     * JavaFX support. Not needed for running from the command line.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
