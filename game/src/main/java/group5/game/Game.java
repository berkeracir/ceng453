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
import java.net.ConnectException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.lang.*;

public class Game extends Application {
    private static Scene firstScene, signUpScene, mainScene, leaderBoardScene, allTimeLeaderBoardScene, lastWeekLeaderBoardScene;
    private static int loginUid;
    private static String loginUsername, loginPassword;
    private Stage window;
    private final String URL = "http://localhost:8080";
    public static int LEVEL = 1;
    public static double MOB_RADIUS = 20;
    public static double PLAYER_RADIUS = 20;
    public static final int GAME_SCENE_WIDTH = 480;
    public static final int GAME_SCENE_HEIGHT = 640;

    public static double lowerYLimit = 20;

    private static int MAX_LEVEL = 4;
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
    String GAME_SERVER = "localhost";
    int GAME_SERVER_PORT = 54555;

    String player_side = null;
    private static BooleanProperty start = new SimpleBooleanProperty(false);
    private static DoubleProperty opponentX = new SimpleDoubleProperty(GAME_SCENE_WIDTH/2);
    private static DoubleProperty opponentY = new SimpleDoubleProperty(GAME_SCENE_HEIGHT-20-PLAYER_RADIUS);

    @Override
    public void start(final Stage primaryStage) throws Exception{
        window = primaryStage;
        window.setTitle("Best Game EVER - GOTY Edition");
        window.setResizable(false);
        window.setWidth(GAME_SCENE_WIDTH);
        window.setHeight(GAME_SCENE_HEIGHT);

        mobCount.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {
                if ((int) newNumber == 0) {
                    levelScore.setValue(levelScore.getValue() + levelTimer.getValue() + LEVEL*playerHP.getValue());
                    LEVEL += 1;

                    if (LEVEL < MAX_LEVEL) {
                        levelInfoScreen(primaryStage);
                    }
                    else if (LEVEL == MAX_LEVEL) {
                        playerWaitingScreen(primaryStage);
                    }
                    else {
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

        start.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (!aBoolean && t1) {
                    primaryStage.setScene(setGameScene());
                }
            }
        });

        //FIRST SCENE
        setFirstScene(window);

        //SIGN UP SCENE
        setSignupScene(window);

        //MAIN SCENE
        setMainScene(window);

        //LEADER BOARD TABLE SCENE
        setLeaderBoardScene(window);


        window.setScene(firstScene);
        window.show();
    }

    public void setAllTimeLeaderBoardScene(Stage primaryStage){
        TableView<TableScore> table;
        //username column
        TableColumn<TableScore, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setMinWidth(150);
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        //score column
        TableColumn<TableScore, String> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setMinWidth(100);
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        //date column
        TableColumn<TableScore, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setMinWidth(2000);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        //STORE THE SCORES OF ALL TIME IN objects
        ObservableList<TableScore> scores = FXCollections.observableArrayList();
        final String url = URL + "/leaderboard_alltime";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Object[]>> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Object[]>>(){});
        List<Object[]> objects = responseEntity.getBody();

        for(int i = 0; i < Math.min(10, objects.size()); i++){
            Object object[] = objects.get(i);
            scores.add(new TableScore(object[1].toString(), object[2].toString(), object[3].toString()));
        }

        table = new TableView<>();
        table.setItems(scores);
        table.getColumns().addAll(usernameColumn, scoreColumn, dateColumn);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(leaderBoardScene));
        backButton.setPrefSize(450,10);

        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.add(table, 0, 0);
        pane.add(backButton, 0,1);
        pane.setStyle("-fx-background-color: BEIGE;");
        allTimeLeaderBoardScene = new Scene(pane);
    }

    public void setLastWeekLeaderBoardScene(Stage primaryStage){
        TableView<TableScore> table;
        //username column
        TableColumn<TableScore, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setMinWidth(150);
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        //score column
        TableColumn<TableScore, String> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setMinWidth(100);
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        //date column
        TableColumn<TableScore, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setMinWidth(200);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        //STORE THE SCORES OF LAST WEEK IN objects
        ObservableList<TableScore> scores = FXCollections.observableArrayList();
        final String url = URL + "/leaderboard_lastweek";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Object[]>> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Object[]>>(){});
        List<Object[]> objects = responseEntity.getBody();
        for(int i = 0; i < Math.min(10, objects.size()); i++){
            Object object[] = objects.get(i);
            scores.add(new TableScore(object[1].toString(), object[2].toString(), object[3].toString()));
        }

        table = new TableView<>();
        table.setItems(scores);
        table.getColumns().addAll(usernameColumn, scoreColumn, dateColumn);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(leaderBoardScene));
        backButton.setPrefSize(450,10);

        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.add(table, 0, 0);
        pane.add(backButton, 0,1);
        pane.setStyle("-fx-background-color: BEIGE;");
        lastWeekLeaderBoardScene = new Scene(pane);
    }

    public void setLeaderBoardScene(Stage primaryStage){
        Button leaderBoardLastWeek = new Button("Last Week Leaderboard");
        leaderBoardLastWeek.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setLastWeekLeaderBoardScene(primaryStage);
                primaryStage.setScene(lastWeekLeaderBoardScene);
            }
        });
        Button leaderBoardAllTime = new Button("All Time Leaderboard");
        leaderBoardAllTime.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setAllTimeLeaderBoardScene(primaryStage);
                primaryStage.setScene(allTimeLeaderBoardScene);
            }
        });
        Button back = new Button("Back");
        back.setOnAction(e -> primaryStage.setScene(mainScene));
        back.setPrefSize(180,10);
        leaderBoardAllTime.setPrefSize(180,10);
        leaderBoardLastWeek.setPrefSize(180,10);
        GridPane leaderBoardPane = new GridPane();
        leaderBoardPane.setMinSize(500,500);
        leaderBoardPane.setPadding(new Insets(10,10,10,10));
        leaderBoardPane.setVgap(15);
        leaderBoardPane.setHgap(15);
        leaderBoardPane.setAlignment(Pos.CENTER);

        leaderBoardPane.add(leaderBoardAllTime, 0,0);
        leaderBoardPane.add(leaderBoardLastWeek, 0, 1);
        leaderBoardPane.add(back, 0, 3);



        leaderBoardPane.setStyle("-fx-background-color: BEIGE;");
        leaderBoardScene = new Scene(leaderBoardPane);
    }

    public void setMainScene(Stage primaryStage){
        Button startGameButton = new Button("Start Game");
        startGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Scene initScene = welcomeScene();
                initScene.setOnKeyPressed(e -> gameScreen(primaryStage));
                initScene.setOnMouseClicked(e -> gameScreen(primaryStage));

                primaryStage.setScene(initScene);
                primaryStage.show();
            }
        });
        startGameButton.setPrefSize(150,10);
        Button showLeaderBoardButton = new Button("Show Leaderboard");
        showLeaderBoardButton.setPrefSize(150,10);
        showLeaderBoardButton.setOnAction(e -> primaryStage.setScene(leaderBoardScene));

        Button back2LoginPage = new Button("Back To Login Page");
        back2LoginPage.setPrefSize(150,10);
        back2LoginPage.setOnAction(e -> primaryStage.setScene(firstScene));

        VBox mainGridPane = new VBox();
        mainGridPane.setMinSize(500, 500);
        mainGridPane.setPadding(new Insets(10, 10, 10, 10));
        //mainGridPane.setVgap(15);
        //mainGridPane.setHgap(15);
        mainGridPane.setSpacing(15);
        mainGridPane.setAlignment(Pos.CENTER);


        mainGridPane.getChildren().addAll(startGameButton, showLeaderBoardButton, back2LoginPage);
        //mainGridPane.add(startGameButton,0,0)
        //mainGridPane.add(showLeaderBoardButton,3,0);

        //mainGridPane.add(startGameButton,0,0)
        //mainGridPane.add(showLeaderboardButton,3,0);
        //mainGridPane.add(back2LoginPage,1,1 );

        mainGridPane.setStyle("-fx-background-color: BEIGE;");
        mainScene = new Scene(mainGridPane);
    }

    public void setSignupScene(Stage primaryStage){
        //Set the username and password1 and password2 field
        Text registerNameLabel = new Text("Username");
        TextField registerNameField = new TextField();
        Text RPasswordLabel = new Text("Password");
        PasswordField RPasswordField = new PasswordField();
        Text confirmPasswordLabel = new Text("Confirm Password");
        PasswordField confirmPasswordField = new PasswordField();
        //Set the sign up button and it's event handler
        Button RSignUpButton = new Button("Sign Up");
        Button backButton = new Button("Back");
        backButton.setPrefSize(100,10);
        RSignUpButton.setPrefSize(100,10);
        RSignUpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String username =  new String(registerNameField.getText());
                String password1 = new String(RPasswordField.getText());
                String password2 = new String(confirmPasswordField.getText());
                if (username.length() < 4 || password1.length() < 4){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Username and password should be longer than 4 character");
                    alert.showAndWait();
                }

                else if (password1.length() > 12 || username.length() > 12) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Username and password should be at most 12 character");
                    alert.showAndWait();
                }

                else if (!password1.equals(password2)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Passwords Does NOT Match With Each Other");
                    alert.showAndWait();
                }

                else {
                    try {
                        final String url = URL + "/register";
                        User newUser = new User(username, password1);
                        RestTemplate restTemplate = new RestTemplate();
                        User result = restTemplate.postForObject(url, newUser, User.class);
                        primaryStage.setScene(firstScene);
                    }
                    catch (Exception e){
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("Username exist");
                        alert.showAndWait();
                    }
                }
            }
        });

        backButton.setOnAction(e -> primaryStage.setScene(firstScene));
        GridPane signUpGridPane = new GridPane();
        signUpGridPane.setMinSize(500, 500);
        signUpGridPane.setPadding(new Insets(10, 10, 10, 10));
        signUpGridPane.setVgap(15);
        signUpGridPane.setHgap(15);
        signUpGridPane.setAlignment(Pos.CENTER);

        signUpGridPane.add(registerNameLabel, 0, 0);
        signUpGridPane.add(registerNameField, 1, 0);
        signUpGridPane.add(RPasswordLabel, 0,1);
        signUpGridPane.add(RPasswordField,1,1);
        signUpGridPane.add(confirmPasswordLabel, 0,2);
        signUpGridPane.add(confirmPasswordField,1,2);
        signUpGridPane.add(RSignUpButton,1,3);
        signUpGridPane.add(backButton,1,4);

        registerNameLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        RPasswordLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        confirmPasswordLabel.setStyle("-fx-font: normal bold 15px 'serif' ");

        signUpScene = new Scene(signUpGridPane);
    }

    public void setFirstScene(Stage primaryStage){
        //Set the username and password field
        Text usernameLabel = new Text("Username");
        TextField usernameField = new TextField();
        Text passwordLabel = new Text("Password");
        PasswordField passwordField = new PasswordField();
        //Set the login button and it's event handler
        Button loginButton = new Button("Login"); //primaryStage.setScene(mainScene)
        loginButton.setPrefSize(100,10);
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String username =  new String(usernameField.getText());
                String password = new String(passwordField.getText());
                int l1 = username.length(), l2 = password.length();
                //Tell the user that username and password length sould be between 4-12 character
                if (l1 > 12 || l2 > 12 || l1 < 4 || l2 < 4){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Username and password should be between 4 and 12 character");
                    alert.showAndWait();
                }

                else {
                    //Try to login. In case of failure, give warning to user
                    try{
                        final String url = URL + "/login";
                        User newUser = new User(1,username, password);
                        RestTemplate restTemplate = new RestTemplate();

                        User result = restTemplate.postForObject(url, newUser, User.class);
                        if (result.getUsername().equals("")){
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setContentText("Username or password is wrong");
                            alert.showAndWait();
                        }

                        else {
                            loginUsername = username;
                            loginPassword = password;
                            loginUid = result.getUid();
                            primaryStage.setScene(mainScene);
                        }
                    }
                    catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("Username or password is wrong");
                        alert.showAndWait();
                    }
                }
            }
        });
        //Set the sign up button
        Button signUpButton = new Button("Sign Up");
        signUpButton.setPrefSize(100,10);
        //Change the scene to signup scene if user click the button
        signUpButton.setOnAction(e -> primaryStage.setScene(signUpScene));
        //Set positions
        GridPane firstGridPane = new GridPane();
        firstGridPane.setMinSize(500, 500);
        firstGridPane.setPadding(new Insets(10, 10, 10, 10));
        firstGridPane.setVgap(15);
        firstGridPane.setHgap(15);
        firstGridPane.setAlignment(Pos.CENTER);

        firstGridPane.add(usernameLabel, 0, 0);
        firstGridPane.add(usernameField, 1, 0);
        firstGridPane.add(passwordLabel, 0,1);
        firstGridPane.add(passwordField,1,1);
        firstGridPane.add(loginButton,0,2);
        firstGridPane.add(signUpButton,1,2);
        //Set the font styles
        usernameLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        passwordLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        firstGridPane.setStyle("-fx-background-color: BEIGE;");
        firstScene = new Scene(firstGridPane);
    }

    private Scene welcomeScene() {
        HBox gamePane = new HBox();
        Text text = new Text("How to play:\n\tShooter is moved by mouse movements" +
                "\n\tPress 'ESC' for returning to Main Menu" +
                "\n\tPress 'R' for restarting the game" +
                "\n\nScore Calculations:" +
                "\n\n  Between Level I-III:" +
                "\n\tKilled_Monster_Count + Time_Left + Level * Shooter_HP" +
                "\n  For Level IV (BOSS):" +
                "\n\tGiven_Damage_to_Boss + Boss_Slaying_Point +" +
                "\n\t\t Time_Left + Level * Shooter_HP" +
                "\n(Boss_Slaying_Point is given to alive players after slaying the boss!)" +
                "\n\nPress any key or click anywhere to continue to game!");
        gamePane.getChildren().add(text);
        gamePane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(gamePane, GAME_SCENE_WIDTH, GAME_SCENE_HEIGHT);

        return scene;
    }

    private void gameScreen(Stage stage) {
        Scene gameScreen = setGameScene();

        gameScreen.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                LEVEL = 1;
                levelScore.setValue(0);

                stage.setScene(mainScene);
                stage.show();
            }
            else if (e.getCode() == KeyCode.R) {
                LEVEL = 1;
                levelScore.setValue(0);

                Scene initScene = welcomeScene();
                initScene.setOnKeyPressed(ev -> gameScreen(stage));
                initScene.setOnMouseClicked(ev -> gameScreen(stage));

                stage.setScene(initScene);
                stage.show();
            }
        });

        stage.setScene(gameScreen);
    }

    private Scene setGameScene() {
        HBox infoPane = initInfoPane();
        Pane fieldPane;
        BorderPane gamePane = initGamePane();

        if (LEVEL < MAX_LEVEL) {
            fieldPane = initFieldPane();
        }
        else {
            fieldPane = bossFieldPane();
        }

        gamePane.setTop(infoPane);
        gamePane.setCenter(fieldPane);

        Scene scene = new Scene(gamePane, GAME_SCENE_WIDTH, GAME_SCENE_HEIGHT);

        return scene;
    }

    private void levelInfoScreen(Stage stage) {
        Scene levelTransitionScene = levelTransitionScene();

        levelTransitionScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                LEVEL = 1;
                levelScore.setValue(0);

                stage.setScene(mainScene);
            }
            else if (e.getCode() == KeyCode.R) {
                LEVEL = 1;
                levelScore.setValue(0);

                Scene initScene = welcomeScene();
                initScene.setOnKeyPressed(ev -> gameScreen(stage));
                initScene.setOnMouseClicked(ev -> gameScreen(stage));

                stage.setScene(initScene);
                stage.show();
            }
            else {
                gameScreen(stage);
            }
        });
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

        if (client != null) {
            client.sendTCP("FINISH");
        }

        //System.out.println(loginUid);
        final String url = URL + "/scores/uid:" + String.valueOf(loginUid);
        int score = levelScore.get();
        Score postScore = new Score();
        postScore.setScore(score);
        postScore.setUsername(loginUsername);
        //System.out.println(score);
        RestTemplate restTemplate = new RestTemplate();
        //System.out.println(restTemplate.postForObject(url, postScore, Score.class));
        restTemplate.postForObject(url, postScore, Score.class);
        Score result = restTemplate.postForObject(url, postScore, Score.class);
        //System.out.println(result);


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
        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LEVEL = 1;
                levelScore.setValue(0);
                stage.setScene(mainScene);
            }
        });
        buttonPane.getChildren().addAll(againButton, mainMenuButton);
        buttonPane.setSpacing(20);
        buttonPane.setAlignment(Pos.CENTER);

        gamePane.getChildren().addAll(text, buttonPane);
        gamePane.setSpacing(20);
        gamePane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(gamePane, GAME_SCENE_WIDTH, GAME_SCENE_HEIGHT);

        return scene;
    }

    private void playerWaitingScreen(Stage stage) {
        HBox waitPane = new HBox();
        Text text = new Text("Waiting player to start a session...");
        waitPane.setAlignment(Pos.CENTER);
        waitPane.getChildren().add(text);

        Scene scene = new Scene(waitPane, GAME_SCENE_WIDTH, GAME_SCENE_HEIGHT);
        stage.setScene(scene);

        start.setValue(false);

        if (client == null) {
            try {
                client = new Client();
                client.start();
                client.connect(5000, GAME_SERVER, GAME_SERVER_PORT);

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
                            } else if (msg.startsWith("START")) {
                                //System.out.println(msg);
                                String[] tokens = msg.split(" ");

                                if (tokens[1].equals("L")) {
                                    player_side = "L";
                                } else if (tokens[1].equals("R")) {
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
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Unable to reach the game server!");

                Scene finalScene = endGameScene(stage);
                stage.setScene(finalScene);
                stage.show();
            }
        }

        if (client != null) {
            client.sendTCP("READY");
        }
    }

    private Pane bossFieldPane() {
        levelTimer.setValue(TIMELIMIT);

        Pane fieldPane = new Pane();

        Boss boss = new Boss(GAME_SCENE_WIDTH/2.0, 5.0 + Boss.BOSS_SIZE/2.0);
        fieldPane.getChildren().add(boss);
        customizeBoss(boss, fieldPane);
        boss.setMovementTimeline(fieldPane);

        lowerYLimit = boss.getY() + Boss.BOSS_SIZE + 5.0;

        Player player;
        Player opponent;

        if (player_side.equals("L")) {
            player = new Player(3.0*GAME_SCENE_WIDTH/8.0, GAME_SCENE_HEIGHT-20-PLAYER_RADIUS, PLAYER_RADIUS);
            opponentX.setValue(5.0*GAME_SCENE_WIDTH/8.0);
            opponent = new Player(5.0*GAME_SCENE_WIDTH/8.0, GAME_SCENE_HEIGHT-20-PLAYER_RADIUS, PLAYER_RADIUS, false);
        }
        else if (player_side.equals("R")) {
            player = new Player(5.0*GAME_SCENE_WIDTH/8.0, GAME_SCENE_HEIGHT-20-PLAYER_RADIUS, PLAYER_RADIUS);
            opponentX.setValue(3.0*GAME_SCENE_WIDTH/8.0);
            opponent = new Player(3.0*GAME_SCENE_WIDTH/8.0, GAME_SCENE_HEIGHT-20-PLAYER_RADIUS, PLAYER_RADIUS, false);
        }
        else {
            player = new Player(GAME_SCENE_WIDTH/2.0, GAME_SCENE_HEIGHT-20-PLAYER_RADIUS, PLAYER_RADIUS);
            opponent = new Player(GAME_SCENE_WIDTH/2.0, GAME_SCENE_HEIGHT-20-PLAYER_RADIUS, PLAYER_RADIUS, false);
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
        playerHP.setValue(player.getHP());

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

        mobCount.setValue(1);

        return fieldPane;
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