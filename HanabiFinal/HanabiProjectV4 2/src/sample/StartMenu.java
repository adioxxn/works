package sample;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * StartMenu is the class that will represent the first
 * menu that is seen when the game is started up.
 * Each method in the class will open another sub menu which
 * are opened by the buttons in the GUI.
 */
public class StartMenu {




    public TextField serverNameBox;
    public PasswordField secretToken;

    public ChoiceBox numPlayers;
    public ChoiceBox gameType;
    public ChoiceBox waitTime;

    /**
     * endGame will be the linked to
     * the exit game button in the start menu.
     */
    public void endGame() {
        System.exit(0);
    }

    /**
     * Run tutorial will be linked to the tutorial button.
     * The funciton willopen a new stage and scene which
     * will represent the tutorial.
     *
     * @throws Exception Throws an exception if the resource
     * fxml file cannot be found.
     */
    public void runTutorial() throws Exception{
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("tutorial.fxml"));
        stage.setScene(new Scene(root, 600, 425));
        stage.show();

    }

    /**
     * returnToMenu will be linked to the exit button on each submenu which
     * will take us back to the mainMenu.
     *
     * @param event The event which will be used to signal the close
     *             the current stage.
     */
    public void returnToMenu(ActionEvent event){
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * goToCreateMenu will be linked with the create server button
     * in the main menu. The method will create a new scene and stage
     * which will be the create sub menu.
     *
     * @param event the event which will trigger the function.
     * @throws Exception  Throws an exception if the fxml for create menu cannot be found.
     */
    public void goToCreateMenu(ActionEvent event) throws Exception {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("createMenu.fxml"));
        stage.setScene(new Scene(root, 600, 425));
        stage.show();
    }

    /**
     * goToJoinMenu will open a stage and scene which will be the join submenu
     * It is linked to the choose server button in the menu.
     *
     * @throws Exception Throws an exception if the fxml for join menu cannot be found.
     */
    public void goToJoinMenu() throws Exception {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("joinMenu.fxml"));
        stage.setScene(new Scene(root, 600, 425));
        stage.show();
    }

    /**
     *
     * Within the JoinMenu we will join a pre created server
     * if there is a server name and token specified by the user.
     *
     * @throws Exception  joinGame throws an expection if playField.fxml cannot be found.
     */
    public void joinGame() throws Exception{
        JsonOps J = new JsonOps();
        int A = Integer.valueOf(serverNameBox.getText());
        String B = secretToken.getText();
        J.sendMessageToServer(J.generateJoinMessage(A,B));

        PlayField playField = new PlayField();


        J.setPlayField(playField);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("playField.fxml"));
        Parent root = fxmlLoader.load();
        Controller controller = fxmlLoader.<Controller>getController();
        controller.setJsonOps(J);
        controller.setTheModel(playField);
        playField.addObervor(controller);
        J.setController(controller);
        playField.addObervor(controller);


        Stage stage =new Stage();
        Scene scene = new Scene(root);
        //We could try to replace the create and join stages with a playField stage.
        stage.setTitle("Play Field");
        stage.setScene(scene);
        stage.show();


    }

    /**
     * createGame will create a game based on the options that are specified
     * by the user in the menu. Once a game is created a playField is created
     * as we wait for the other players to join the game.
     *
     * @throws Exception  createGame throws an expection if playField.fxml cannot be found.
     */
    public void createGame() throws Exception{
        JsonOps J = new JsonOps();
        int A = Integer.valueOf(numPlayers.getValue().toString());
        int B = Integer.valueOf(waitTime.getValue().toString());
        String C = gameType.getValue().toString();
        J.sendMessageToServer(J.generateCreateMsg(A,B,C));

        PlayField playField = new PlayField();


        J.setPlayField(playField);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("playField.fxml"));
        Parent root = fxmlLoader.load();
        Controller controller = fxmlLoader.<Controller>getController();
        controller.setJsonOps(J);
        controller.setTheModel(playField);
        playField.addObervor(controller);
        J.setController(controller);
        playField.addObervor(controller);

        Stage stage =new Stage();
        Scene scene = new Scene(root);
        //We could try to replace the create and join stages with a playField stage.
        stage.setTitle("Play Field");
        stage.setScene(scene);
        stage.show();


    }
}
