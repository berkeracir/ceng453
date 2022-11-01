package group5.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


public class gameplayTest extends Application {
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

    private static boolean nextLevel = false;
    private static boolean levelFailed = true;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Best Game EVER - GOTY Edition");
        primaryStage.setResizable(false);

        mobCount.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {
                if ((int) newNumber == 0) {
                    LEVEL += 1;

                    if (LEVEL <= MAX_LEVEL) {
                        levelInfoScreen(primaryStage);
                        levelScore.setValue(levelScore.getValue() + levelTimer.getValue() + playerHP.getValue());
                    }
                    else {
                        // TODO: FINAL SCORE SCENE
                        levelScore.setValue(levelScore.getValue() + levelTimer.getValue() + playerHP.getValue());
                        Scene finalScene = endGameScene(primaryStage);

                        primaryStage.setScene(finalScene);
                        primaryStage.show();
                    }
                }
            }
        });
        playerHP.addListener(new ChangeListener<Number>() { // TODO
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {
                if ((int) newNumber == 0) {
                    // TODO: FINAL SCORE SCENE
                    Scene finalScene = endGameScene(primaryStage);

                    primaryStage.setScene(finalScene);
                    primaryStage.show();
                }
            }
        });
        Timeline counter = new Timeline(
                new KeyFrame(Duration.millis(1000), e -> {
                    levelTimer.setValue(levelTimer.getValue()-1);
                })
        );
        counter.setCycleCount(Timeline.INDEFINITE);
        counter.play();

        Scene initScene = welcomeScene();
        initScene.setOnKeyPressed(e -> gameScreen(primaryStage));
        initScene.setOnMouseClicked(e -> gameScreen(primaryStage));

        primaryStage.setScene(initScene);
        primaryStage.show();
    }

    /**
     * The main method is only needed for the IDE with limited
     * JavaFX support. Not needed for running from the command line.
     */
    public static void main(String[] args) {
        launch(args);
    }

    private Scene welcomeScene() {
        HBox gamePane = new HBox();
        Text text = new Text("TODO: Gameplay Tutorial\n\nPress any key/Click anywhere to continue.");
        gamePane.getChildren().add(text);
        gamePane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(gamePane, GAME_SCENE_WIDTH, GAME_SCENE_HEIGHT);

        return scene;
    }

    private void gameScreen(Stage stage) {
        Scene gameScreen = setGameScene();

        // TODO: add ESC key pressed to leave/restart the game early
        // TODO: add RETURN key pressed to return main menu
        gameScreen.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                Scene returnScene = welcomeScene();
                returnScene.setOnKeyPressed(ev -> gameScreen(stage));
                returnScene.setOnMouseClicked(ev -> gameScreen(stage));

                LEVEL = 1;
                levelScore.setValue(0);

                stage.setScene(returnScene);
                stage.show();
            }
        });

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

    private void levelInfoScreen(Stage stage) {
        Scene levelTransitionScene = levelTransitionScene();

        levelTransitionScene.setOnKeyPressed(e -> gameScreen(stage));
        levelTransitionScene.setOnMouseClicked(e -> gameScreen(stage));

        stage.setScene(levelTransitionScene);
    }

    private Scene levelTransitionScene() {
        HBox gamePane = new HBox();

        Text text;
        text = new Text("LEVEL " + LEVEL);

        gamePane.getChildren().add(text);
        gamePane.setAlignment(Pos.CENTER);

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
        levelTimer.setValue(TIMELIMIT);

        Pane fieldPane = new Pane();

        Player player = new Player(GAME_SCENE_WIDTH/2, GAME_SCENE_HEIGHT-20-PLAYER_RADIUS, PLAYER_RADIUS);
        fieldPane.setOnMouseMoved(e -> {
            player.setCenterX(e.getX());

            if (e.getY() >= lowerYLimit)
                player.setCenterY(e.getY());
        });
        player.setTimeline(fieldPane);
        fieldPane.getChildren().add(player);
        player.play();
        playerHP.setValue(player.getHP());

        lowerYLimit = 20 + MOB_RADIUS/5 + LEVEL * (2 * MOB_RADIUS + MOB_RADIUS/5);

        double y = MOB_RADIUS + MOB_RADIUS/5;

        int count = 0;

        for (int lvl=1; lvl<=LEVEL; lvl++) {
            int mob_count = 10 - 2 * (lvl - 1);
            double mob_spacing = (GAME_SCENE_WIDTH - 2 * MOB_RADIUS * mob_count) / (mob_count + 1);
            double x = mob_spacing + MOB_RADIUS;

            for (int i = 0; i < mob_count; i++) {
                Mob mob = new Mob(x, y, MOB_RADIUS);
                mob.setTimeline(fieldPane, player);
                count = count + 1;
                mob.play();

                fieldPane.getChildren().add(mob);

                x += mob_spacing + 2 * MOB_RADIUS;
            }

            y += 2 * MOB_RADIUS + MOB_RADIUS/5;
        }

        mobCount.setValue(count);
        return fieldPane;
    }

    private BorderPane initGamePane() {
        BorderPane gamePane = new BorderPane();
        gamePane.setStyle("-fx-background-color: rgb(0,0,0);");

        return gamePane;
    }

    private Scene endGameScene(Stage stage) {
        VBox gamePane = new VBox();

        Text text = new Text("Score: " + levelScore.get());

        // TODO: SEND SCORE TO DATABASE

        HBox buttonPane = new HBox();
        Button againButton = new Button("Restart");
        againButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LEVEL = 1;
                levelScore.setValue(0);
                gameScreen(stage);
            }
        });
        // TODO: ADD Game Menu Button Action
        Button mainMenuButton = new Button("Game Menu");
        buttonPane.getChildren().addAll(againButton, mainMenuButton);
        buttonPane.setSpacing(20);
        buttonPane.setAlignment(Pos.CENTER);

        gamePane.getChildren().addAll(text, buttonPane);
        gamePane.setSpacing(20);
        gamePane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(gamePane, GAME_SCENE_WIDTH, GAME_SCENE_HEIGHT);

        return scene;
    }
}