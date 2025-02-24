package sample;

import sample.JsonOps;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;

import java.util.List;


public class HintWindow extends BorderPane {
    private double width = 720;     //the default hint window width
    private double height = 200;    //the default hint window height
    int counter;
    int counter1;

    int hintToPlayer;

    JsonOps jsonOps;

    String hintMessage;
    Stage stage = new Stage();

    Button[] numButtons = new Button[5];
    Button[] colButtons = new Button[5];
    String[] colors = {CardColor.RED.toString(), CardColor.GREEN.toString(), CardColor.BLUE.toString(),
            CardColor.YELLOW.toString(), CardColor.WHITE.toString()};

    private static HintWindow onlyHintWin;

    public HintWindow(JsonOps jsonOps, int player){
        this.jsonOps = jsonOps;
        this.hintToPlayer = player;

        VBox vBox = new VBox();
        vBox.setStyle("-fx-background-color: BLACK");
        vBox.getChildren().add(new HBox());//One hBox for numbered hints.
        vBox.getChildren().add(new HBox());// One hBox for color hints.
        Scene scene = new Scene(vBox);


        for(int counter = 0; counter < 5; counter++){
            numButtons[counter] = new Button(Integer.toString(counter + 1));
            numButtons[counter].setPrefWidth(120);
            ((HBox)vBox.getChildren().get(0)).getChildren().add(numButtons[counter]);
            colButtons[counter] = new Button(colors[counter]);
            colButtons[counter].setPrefWidth(120);
            ((HBox)vBox.getChildren().get(1)).getChildren().add(colButtons[counter]);
            colButtons[counter].setStyle("-fx-background-color:" + colors[counter]);
        }

        stage.setScene(scene);
        stage.setHeight(this.height);
        stage.setWidth(this.width);
        stage.setTitle("Hint Menu");
        stage.setIconified(false);
        stage.requestFocus();
        stage.toFront();
    }

    /**
     * This method will make sure that only one hint window can be opened at a time
     * @param jsonOps
     * @param player
     * @return
     */
    public static HintWindow getOnlyHintWin(JsonOps jsonOps, int player) {
        if (onlyHintWin == null) { //if there is no instance available .. create new one
            onlyHintWin = new HintWindow(jsonOps, player);
        }

        onlyHintWin.jsonOps = jsonOps;
        onlyHintWin.hintToPlayer = player;
        return onlyHintWin;
    }

    /**
     * when user clicked a hint in the hint window, this method will generate a json message and send to the server
     * @param player
     * @param hand
     */
    public void giveHint(int player, List<GameCard> hand ){
        int player1 = player + 1;

        //functionality for number buttons
        for(counter = 0; counter < numButtons.length; counter++){
            numButtons[counter].setDisable(true);
            for (GameCard j : hand){
                if (numButtons[counter].getText().equals(Integer.toString(j.number.get()))) {
                    numButtons[counter].setDisable(false);
                }
                switch (counter) {
                    case 0:
                        numButtons[counter].setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                jsonOps.hintRank(player1, 1);
                            }
                        });

                        break;
                    case 1:
                        numButtons[counter].setOnAction((ActionEvent e) -> {
                            jsonOps.hintRank(player1, 2);
                        });
                        stage.close();
                        break;
                    case 2:
                        numButtons[counter].setOnAction((ActionEvent e) -> {
                            jsonOps.hintRank(player1, 3);
                        });
                        stage.close();
                        break;
                    case 3:
                        numButtons[counter].setOnAction((ActionEvent e) -> {jsonOps.hintRank(player1, 4);});
                        stage.close();
                        break;
                    case 4:
                        numButtons[counter].setOnAction((ActionEvent e) -> {jsonOps.hintRank(player1, 5);});
                        stage.close();
                        break;
                }
            }
        }

        //functionality for the color buttons
        for(counter1 = 0; counter1 < colButtons.length; counter1++) {
            colButtons[counter1].setDisable(true);
            for (GameCard j : hand) {
                if (colButtons[counter1].getText().equals(j.color.get().toString())) {
                    colButtons[counter1].setDisable(false);
                }
                switch (counter1) {
                    case 0:
                        colButtons[counter1].setOnAction((ActionEvent e) -> {
                            jsonOps.hintSuit(player1, "r");
                        });
                        stage.close();
                        break;
                    case 1:
                        colButtons[counter1].setOnAction((ActionEvent e) -> {
                            jsonOps.hintSuit(player1, "g");
                        });
                        stage.close();
                        break;
                    case 2:
                        colButtons[counter1].setOnAction((ActionEvent e) -> {
                            jsonOps.hintSuit(player1, "b");
                        });
                        stage.close();
                        break;
                    case 3:
                        colButtons[counter1].setOnAction((ActionEvent e) -> {
                            jsonOps.hintSuit(player1, "y");
                        });
                        stage.close();
                        break;
                    case 4:
                        colButtons[counter1].setOnAction((ActionEvent e) -> {
                            jsonOps.hintSuit(player1, "w");
                        });
                        stage.close();
                        break;
                }
            }
        }

        this.stage.show();

    }
}
