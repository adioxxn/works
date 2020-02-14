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

public class StartMenu {




    public TextField serverNameBox;
    public PasswordField secretToken;

    public ChoiceBox numPlayers;
    public ChoiceBox gameType;
    public ChoiceBox waitTime;


    public void endGame() {
        System.exit(0);
    }

    public void runTutorial() throws Exception{
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("tutorial.fxml"));
        stage.setScene(new Scene(root, 600, 425));
        stage.show();

    }

    public void returnToMenu(ActionEvent event){
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void goToCreateMenu(ActionEvent event) throws Exception {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("createMenu.fxml"));
        stage.setScene(new Scene(root, 600, 425));
        stage.show();
    }

    public void goToJoinMenu() throws Exception {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("joinMenu.fxml"));
        stage.setScene(new Scene(root, 600, 425));
        stage.show();
    }

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
