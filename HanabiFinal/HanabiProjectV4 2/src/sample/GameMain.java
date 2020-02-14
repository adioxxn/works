package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        PlayField playField = new PlayField();

        JsonOps jsonOps = new JsonOps();

        jsonOps.setPlayField(playField);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("playField.fxml"));
        Parent root = fxmlLoader.load();
        Controller controller = fxmlLoader.<Controller>getController();
        controller.setJsonOps(jsonOps);
        controller.setTheModel(playField);
        playField.addObervor(controller);


        Scene scene = new Scene(root);
        //We could try to replace the create and join stages with a playField stage.
        primaryStage.setTitle("Play Field");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
