package group5.game;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.util.Duration;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.lang.*;

import static java.lang.Thread.sleep;


public class concurrencyTest extends Application {
    public static int LEVEL = 1;
    public static double MOB_RADIUS = 20;
    public static double PLAYER_RADIUS = 20;
    public static final int GAME_SCENE_WIDTH = 480;
    public static final int GAME_SCENE_HEIGHT = 640;

    public static double lowerYLimit = 20;

    private static int MAX_LEVEL = 3;
    private static int TIMELIMIT = 120;

    private static IntegerProperty levelTimer = new SimpleIntegerProperty(TIMELIMIT);
    private static IntegerProperty levelScore = new SimpleIntegerProperty(0);
    private static IntegerProperty mobCount = new SimpleIntegerProperty(0);
    private static IntegerProperty playerHP = new SimpleIntegerProperty(0);

    public static void setLevelScore(int score) {
        levelScore.setValue(score);
    }
    public static int getLevelScore() {
        return levelScore.getValue();
    }
    public static void setMobCount(int count) {
        mobCount.setValue(count);
    }
    public static int getMobCount() {
        return mobCount.getValue();
    }
    public static void setPlayerHP(int hp) {
        playerHP.setValue(hp);
    }
    public static int getPlayerHP() {
        return playerHP.getValue();
    }

    Client client = null;
    private static BooleanProperty start = new SimpleBooleanProperty(false);
    String player_side = null;

    private static DoubleProperty playerX = new SimpleDoubleProperty(0);
    private static DoubleProperty playerY = new SimpleDoubleProperty(0);
    private static DoubleProperty opponentX = new SimpleDoubleProperty(GAME_SCENE_WIDTH/2);
    private static DoubleProperty opponentY = new SimpleDoubleProperty(GAME_SCENE_HEIGHT-20-PLAYER_RADIUS);

    @Override
    public void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle("Best Game EVER - GOTY Edition");
        primaryStage.setResizable(false);
        primaryStage.setWidth(GAME_SCENE_WIDTH);
        primaryStage.setHeight(GAME_SCENE_HEIGHT);

        start.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (!aBoolean && t1) {
                    Scene scene = setGameScene();
                    primaryStage.setScene(scene);
                    primaryStage.show();
                }
            }
        });

        /*Scene scene = waitScene();
        primaryStage.setScene(scene);
        try {
            sleep(100);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        primaryStage.show();*/
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Scene scene = waitScene();
                primaryStage.setScene(scene);
                primaryStage.show();
            }
        });
    }

    public Scene waitScene() {
        HBox waitPane = new HBox();
        Text text = new Text("Waiting...");
        waitPane.setAlignment(Pos.CENTER);
        waitPane.getChildren().add(text);

        Scene scene = new Scene(waitPane, GAME_SCENE_WIDTH, GAME_SCENE_HEIGHT);

        try {
            client = new Client();
            client.start();
            client.connect(5000, "localhost", 54555);

            Kryo kryo = client.getKryo();
            kryo.register(String.class);

            client.addListener(new Listener() {

                @Override
                public void received(Connection connection, Object o) {

                    if (o instanceof String) {
                        String msg = (String) o;
                        //System.out.println(msg);

                        if (msg.startsWith("PLAYER")) {
                            String[] tokens = msg.split(" ");

                            double x = Double.parseDouble(tokens[1]);
                            double y = Double.parseDouble(tokens[2]);

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    opponentX.setValue(x);
                                    opponentY.setValue(y);
                                }
                            });
                        }
                        else if (msg.startsWith("START")) {
                            System.out.println(msg);
                            String[] tokens = msg.split(" ");

                            if (tokens[1].equals("L")) {
                                player_side = "L";
                            }
                            else if (tokens[1].equals("R")) {
                                player_side = "R";
                            }

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    start.setValue(true);
                                }
                            });
                        }
                    }
                }
            });

            client.sendTCP("READY");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return scene;
    }

    private void gameScreen(Stage stage) {
        Scene gameScreen = setGameScene();

        stage.setScene(gameScreen);
    }

    private Scene setGameScene() {
        HBox infoPane = initInfoPane();
        Pane fieldPane = initFieldPane();
        BorderPane gamePane = initGamePane();

        gamePane.setTop(infoPane);
        gamePane.setCenter(fieldPane);

        Scene scene = new Scene(gamePane, GAME_SCENE_WIDTH, GAME_SCENE_HEIGHT);

        return scene;
    }

    private HBox initInfoPane() {
        HBox infoPane = new HBox();

        infoPane.setStyle("-fx-background-color: #c7c7c7;");
        infoPane.setPadding(new Insets(2,2,2,2));
        infoPane.setSpacing(10);
        infoPane.setAlignment(Pos.CENTER);


        Text level_text = new Text("Level:" + LEVEL);

        Text timer_text = new Text("Time:" + levelTimer.getValue());
        levelTimer.addListener(ov -> {
            timer_text.setText("Time:" + levelTimer.getValue());
        });

        Text score_text = new Text("Score:" + levelScore.getValue());
        levelScore.addListener(ov -> {
            score_text.setText("Score:" + levelScore.getValue());
        });

        infoPane.getChildren().add(level_text);
        infoPane.getChildren().add(timer_text);
        infoPane.getChildren().add(score_text);

        return infoPane;
    }

    private Pane initFieldPane() {
        Pane fieldPane = new Pane();

        Boss boss = new Boss(GAME_SCENE_WIDTH/2.0, 5.0 + Boss.BOSS_SIZE/2.0);
        fieldPane.getChildren().add(boss);
        customizeBoss(boss, fieldPane);
        boss.setMovementTimeline(fieldPane);

        lowerYLimit += boss.getY() + Boss.BOSS_SIZE;

        Player player;
        Player opponent;

        if (player_side.equals("L")) {
            player = new Player(3.0*GAME_SCENE_WIDTH/8.0, GAME_SCENE_HEIGHT-20-PLAYER_RADIUS, PLAYER_RADIUS);
            opponentX.setValue(5.0*GAME_SCENE_WIDTH/8.0);
            opponent = new Player(5.0*GAME_SCENE_WIDTH/8.0, GAME_SCENE_HEIGHT-20-PLAYER_RADIUS, PLAYER_RADIUS);
        }
        else if (player_side.equals("R")) {
            player = new Player(5.0*GAME_SCENE_WIDTH/8.0, GAME_SCENE_HEIGHT-20-PLAYER_RADIUS, PLAYER_RADIUS);
            opponentX.setValue(3.0*GAME_SCENE_WIDTH/8.0);
            opponent = new Player(3.0*GAME_SCENE_WIDTH/8.0, GAME_SCENE_HEIGHT-20-PLAYER_RADIUS, PLAYER_RADIUS);
        }
        else {
            player = new Player(GAME_SCENE_WIDTH/2.0, GAME_SCENE_HEIGHT-20-PLAYER_RADIUS, PLAYER_RADIUS);
            opponent = new Player(GAME_SCENE_WIDTH/2.0, GAME_SCENE_HEIGHT-20-PLAYER_RADIUS, PLAYER_RADIUS);
        }

        Circle playerIndicator = new Circle(PLAYER_RADIUS/3);
        playerIndicator.setFill(Color.WHITE);
        playerIndicator.centerXProperty().bind(player.centerXProperty());
        playerIndicator.centerYProperty().bind(player.centerYProperty());

        fieldPane.setOnMouseMoved(e -> {
            player.setCenterX(e.getX());

            if (e.getY() >= lowerYLimit)
                player.setCenterY(e.getY());

            client.sendTCP("PLAYER " + player.getCenterX() + " " + player.getCenterY());
        });
        player.setTimeline(fieldPane);

        opponent.centerXProperty().bind(opponentX);
        opponent.centerYProperty().bind(opponentY);
        opponent.setTimeline(fieldPane);

        fieldPane.getChildren().add(player);
        fieldPane.getChildren().add(opponent);
        fieldPane.getChildren().add(playerIndicator);

        boss.playMovement();
        boss.setFireTimeline(fieldPane, player, opponent);
        boss.playFire();
        player.play();
        opponent.play();

        return fieldPane;
    }

    private BorderPane initGamePane() {
        BorderPane gamePane = new BorderPane();
        gamePane.setStyle("-fx-background-color: rgb(0,0,0);");

        return gamePane;
    }

    private void customizeBoss(Boss boss, Pane pane) {
        double bossCenterX = boss.getCenterX();
        double bossCenterY = boss.getCenterY();

        double EYE_RADIUS = 7.5;

        for (int i=0; i<2; i++) {
            for (int j=0; j<2; j++) {
                Circle leftExtraEye = new Circle(EYE_RADIUS, Color.DARKSLATEGRAY);
                leftExtraEye.centerXProperty().bind(boss.xProperty().add(Boss.BOSS_SIZE/4.0-1*EYE_RADIUS+i*2*EYE_RADIUS));
                leftExtraEye.centerYProperty().bind(boss.yProperty().add(Boss.BOSS_SIZE/4.0-1*EYE_RADIUS+j*2*EYE_RADIUS));
                pane.getChildren().add(leftExtraEye);
            }
        }

        Circle leftEye = new Circle(EYE_RADIUS, Color.DARKBLUE);
        leftEye.centerXProperty().bind(boss.xProperty().add(Boss.BOSS_SIZE/4.0));
        leftEye.centerYProperty().bind(boss.yProperty().add(Boss.BOSS_SIZE/4.0));
        pane.getChildren().add(leftEye);

        for (int i=0; i<2; i++) {
            for (int j=0; j<2; j++) {
                Circle rightExtraEye = new Circle(EYE_RADIUS, Color.DARKSLATEGRAY);
                rightExtraEye.centerXProperty().bind(boss.xProperty().add(3.0*Boss.BOSS_SIZE/4.0-1*EYE_RADIUS+i*2*EYE_RADIUS));
                rightExtraEye.centerYProperty().bind(boss.yProperty().add(Boss.BOSS_SIZE/4.0-1*EYE_RADIUS+j*2*EYE_RADIUS));
                pane.getChildren().add(rightExtraEye);
            }
        }

        Circle rightEye = new Circle(EYE_RADIUS, Color.DARKBLUE);
        rightEye.centerXProperty().bind(boss.xProperty().add(3.0*Boss.BOSS_SIZE/4.0));
        rightEye.centerYProperty().bind(boss.yProperty().add(Boss.BOSS_SIZE/4.0));
        pane.getChildren().add(rightEye);

        Rectangle mouth = new Rectangle(3.0*Boss.BOSS_SIZE/4.0, 1.5*Boss.BOSS_SIZE/10.0, Color.DARKRED);
        mouth.xProperty().bind(boss.xProperty().add(Boss.BOSS_SIZE/8.0));
        mouth.yProperty().bind(boss.yProperty().add(7.0*Boss.BOSS_SIZE/10.0));
        pane.getChildren().add(mouth);
    }

    public static void main(String[] args) {
        launch(args);
    }
}