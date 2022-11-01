package group5.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class playerFireTest extends Application {
    double monsterHP = 10;

    @Override
    public void start(Stage primaryStage) {
        HBox top_pane = new HBox();
        top_pane.setStyle("-fx-background-color: #cbcbcb;");
        top_pane.setPadding(new Insets(2,2,2,2));
        top_pane.setSpacing(10);
        top_pane.setAlignment(Pos.CENTER);

        top_pane.getChildren().add(new Text("test1"));
        top_pane.getChildren().add(new Text("test2"));
        top_pane.getChildren().add(new Text("test3"));

        Pane lower_pane = new Pane();

        Circle monster = new Circle();
        monster.setRadius(50);
        monster.centerXProperty().bind(lower_pane.widthProperty().divide(2));
        monster.setCenterY(monster.getRadius());
        monster.setStroke(Color.WHITE);
        monster.setFill(Color.BLACK);
        lower_pane.getChildren().add(monster);

        Rectangle player = new Rectangle();
        player.setWidth(100);
        player.setHeight(100);
        player.xProperty().bind(lower_pane.widthProperty().divide(2).subtract(player.getWidth()/2));
        player.yProperty().bind(lower_pane.heightProperty().subtract(player.getHeight()));
        player.setStroke(Color.WHITE);
        player.setFill(Color.BLUE);
        lower_pane.getChildren().add(player);

        HBox bot_pane = new HBox();
        bot_pane.setAlignment(Pos.CENTER);
        Button btFIRE = new Button("Fire");
        //FireHandlerClass fireHandler = new FireHandlerClass();
        btFIRE.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Circle bullet = new Circle(player.getX() + player.getWidth()/2, player.getY(), 5, Color.RED);

                lower_pane.getChildren().add(bullet);

                Timeline bullet_movement = new Timeline(
                        new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
                                if (bullet.getCenterY() > 0) {
                                    bullet.setCenterY(bullet.getCenterY() - bullet.getRadius());

                                    if (bullet.getCenterY() <= 0) {
                                        lower_pane.getChildren().remove(bullet);
                                    }
                                    else if (bullet.getCenterY() <= monster.getCenterY() + monster.getRadius()) {
                                        lower_pane.getChildren().remove(bullet);

                                        bullet.setCenterY(-1);

                                        monsterHP = monsterHP - 1;
                                        double r = (10 - monsterHP) / 10.0 * 255.0;
                                        double g = (10 - monsterHP) / 10.0 * 255.0;
                                        double b = (10 - monsterHP) / 10.0 * 255.0;

                                        if (monsterHP > 0) {
                                            Color c = Color.rgb((int) r, (int) g, (int) b);
                                            monster.setFill(c);
                                        }
                                        else {
                                            lower_pane.getChildren().remove(monster);
                                        }
                                    }
                                }
                            }
                        })
                );
                bullet_movement.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        lower_pane.getChildren().remove(bullet);
                    }
                });
                bullet_movement.setCycleCount(Timeline.INDEFINITE);
                bullet_movement.play();
            }
        });

        bot_pane.getChildren().add(btFIRE);

        BorderPane pane = new BorderPane();
        pane.setTop(top_pane);
        pane.setCenter(lower_pane);
        pane.setBottom(bot_pane);
        pane.setStyle("-fx-background-color: #8d8d8d;");
        Scene scene = new Scene(pane, 480, 640);


        primaryStage.setTitle("Fire Action"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        //primaryStage.setResizable(false);
        primaryStage.show(); // Display the stage
    }

    /**
     * The main method is only needed for the IDE with limited
     * JavaFX support. Not needed for running from the command line.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
