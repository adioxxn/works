package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * The controller which will be used by the program to
 * to modify the play field.
 */
public class Controller implements Initializable, Obervor {
    private PlayField theModel;

    //Right side of the playField
    @FXML
    private Label fuseCounter;
    @FXML
    private Label infoCounter;

    @FXML
    private VBox fuses;

    @FXML
    private VBox infoTokens;

    @FXML
    TextField selectedCard;

    @FXML
    private Canvas img;

    @FXML
    private TableView discardPileTable;

    GraphicsContext gc;

    JsonOps jsonOps;

    /**
     *update the current playField with a new playField
     * that will be used by the controller.
     *
     * @param theModel the new playField.
     */
    public void setTheModel(PlayField theModel) {
        this.theModel = theModel;
        updateInfoTokens();
        updateFuseTokens();
        updateUserHands();
        updateDiscardPile();
    }

    /**replace the jsonOps instance that is being
     * used by the controller.
     *
     * @param jsonOps the new JsonOps instance.
     */
    public void setJsonOps(JsonOps jsonOps) {
        this.jsonOps = jsonOps;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = img.getGraphicsContext2D();
        if (this.theModel != null) {
            updateInfoTokens();
            updateFuseTokens();
            updateUserHands();
            updateDiscardPile();
        }

    }
    public void updatePlayedCard(){
        gc.clearRect(0, 0, 800, 800);

        drawCardHand(this.theModel.playedCardPile, 400, 300, "");

    }

    /**
     * update the users hand on the view.
     */
    public void updateUserHands() {

        gc.clearRect(0, 0, 800, 800);

        int numPlayers = this.theModel.playerCardsList.size();
        if (numPlayers >= 2) {
            // user's hand
            drawCardHand(this.theModel.playerCardsList.get(0), 400, 600, "P1");

            drawCardHand(this.theModel.playerCardsList.get(1), 200, 200, "P2");
        }

        if (numPlayers >= 3) {
            drawCardHand(this.theModel.playerCardsList.get(2), 200, 400, "P3");
        }
        if (numPlayers >= 4) {
            drawCardHand(this.theModel.playerCardsList.get(3), 600, 200, "P4");
        }

        if (numPlayers >= 5) {
            drawCardHand(this.theModel.playerCardsList.get(4), 600, 400, "P5");
        }


        // played card pile
        drawCardHand(this.theModel.playedCardPile, 400, 300, "");

    }

    /**redraw the given card hand in the view.
     *
     * @param cards the list of cards you wish to draw.
     * @param x the x location of the card list.
     * @param y the y location of the card list.
     * @param player the players number as a string.
     */
    public void drawCardHand(List<GameCard> cards, int x, int y, String player) {
        gc.save();
        gc.translate(x, y - 100);
        gc.setStroke(Paint.valueOf("BLACK"));

        gc.setFill(Paint.valueOf("WHITE"));
        gc.fillText(player, -20, 20);
        if(!this.theModel.playerCardsList.isEmpty()) {
            for (int i = 0; i < cards.size(); i++) {
                GameCard gameCard = cards.get(i);
                String num = gameCard.number.map(integer -> integer + "").orElse("");
                String color = gameCard.color.isPresent() ? gameCard.color.get().toString() : "GREY";
                gc.setFill(Paint.valueOf(color));
                // if conditions
                gc.fillRect((i * 30), 0, 30, 30);
                gc.strokeRect(i * 30, 0, 30, 30);
                gc.setFill(Paint.valueOf("BLACK"));
                gc.fillText(num, 10 + (i * 30), 20);
            }
        }
        gc.restore();
    }

    /**
     * updateInfoTokens will set text in the view to ensure that the model will update
     * its info token counter to the new number of info tokens.
     */
    public void updateInfoTokens() {
        infoTokens.getChildren().remove(0, infoTokens.getChildren().size());

        int numHxBoxes = (this.theModel.getInfoTokens() / 2) + (this.theModel.getInfoTokens() % 2);

        for (int i = 0; i < numHxBoxes; i++) {
            if (i == numHxBoxes - 1) {
                if (this.theModel.getInfoTokens() % 2 == 0) {
                    HBox hBox = new HBox();
                    hBox.getChildren().add(new Circle(10.0, Paint.valueOf("BLUE")));
                    hBox.getChildren().add(new Circle(10.0, Paint.valueOf("BLUE")));
                    infoTokens.getChildren().add(hBox);

                } else {
                    HBox hBox = new HBox();
                    hBox.getChildren().add(new Circle(10.0, Paint.valueOf("BLUE")));
                    infoTokens.getChildren().add(hBox);

                }

            } else {
                HBox hBox = new HBox();
                hBox.getChildren().add(new Circle(10.0, Paint.valueOf("BLUE")));
                hBox.getChildren().add(new Circle(10.0, Paint.valueOf("BLUE")));
                infoTokens.getChildren().add(hBox);

            }
        }


        this.infoCounter.setText(Integer.toString(this.theModel.getInfoTokens()));


    }


    /**
     * updateFuseTokens will set text in teh view in order to update the fuse counter to the
     * new number of info tokens in the model.
     */
    public void updateFuseTokens() {
        fuses.getChildren().remove(0, fuses.getChildren().size());
        for (int i = 0; i < this.theModel.getFuseTokens(); i++) {
            fuses.getChildren().add(new Circle(30.0, Paint.valueOf("YELLOW")));

        }


        this.fuseCounter.setText((Integer.toString(this.theModel.getFuseTokens())));
    }

    /**
     * redraw the discard pile with the current values of
     * discarded in the playField..
     */
    public void updateDiscardPile() {


        discardPileTable.getColumns().remove(0, discardPileTable.getColumns().size());
        TableColumn tableColumnColor = new TableColumn("Color");
        tableColumnColor.setCellValueFactory(new PropertyValueFactory<>("color"));


        TableColumn tableColumnNum1 = new TableColumn("1");
        tableColumnNum1.setCellValueFactory(new PropertyValueFactory<>("numCount1"));

        TableColumn tableColumnNum2 = new TableColumn("2");
        tableColumnNum2.setCellValueFactory(new PropertyValueFactory<>("numCount2"));

        TableColumn tableColumnNum3 = new TableColumn("3");
        tableColumnNum3.setCellValueFactory(new PropertyValueFactory<>("numCount3"));

        TableColumn tableColumnNum4 = new TableColumn("4");
        tableColumnNum4.setCellValueFactory(new PropertyValueFactory<>("numCount4"));

        TableColumn tableColumnNum5 = new TableColumn("5");
        tableColumnNum5.setCellValueFactory(new PropertyValueFactory<>("numCount5"));

        discardPileTable.getColumns().addAll(tableColumnColor, tableColumnNum1, tableColumnNum2, tableColumnNum3, tableColumnNum4, tableColumnNum5);

        discardPileTable.getItems().remove(0, discardPileTable.getItems().size());

        for (String s : Arrays.asList("r", "g", "b", "w", "y")) {
            int c[] = new int[5];
            for (int i = 0; i < 5; i++) {
                c[i] = this.theModel.discardPile.getOrDefault(s + (i + 1), 0);
            }

            discardPileTable.getItems().add(new GameCardTableViewCell(s, c[0], c[1], c[2], c[3], c[4]));

        }
    }


    /**
     * playFieldDiscard will call the discard funciton in the
     * playfield and update the view to the new value of info tokens.
     * update the view to the new value of info tokens.
     */
    public void playFieldDiscard() {
        this.theModel.cardPlayedPos = Integer.parseInt(selectedCard.getText());
        jsonOps.discard(this.theModel.cardPlayedPos);
    }


    public void playFieldPlay() {
        this.theModel.cardPlayedPos = Integer.parseInt(selectedCard.getText());
        jsonOps.play(this.theModel.cardPlayedPos);
    }



    public void openHintMenu(ActionEvent actionEvent) {
        try {
            this.theModel.giveHint(new HintResponse(false));
        } catch (Exception e) {
            System.out.println("Cannot give a hint with no infotokens");
            return;
        }
        int playerPosition = Integer.parseInt(selectedCard.getText());
        HintWindow hintWindow = HintWindow.getOnlyHintWin(jsonOps, playerPosition);
        hintWindow.giveHint(playerPosition, theModel.getPlayerCardsList().get(playerPosition));
        updateInfoTokens();
    }

    @Override
    public void modelChanged() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                updateInfoTokens();
                updateUserHands();
                updateFuseTokens();
                updateDiscardPile();
            }
        });

    }
}
