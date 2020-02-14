package sample;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.util.Pair;

import java.util.List;
import java.util.Optional;

public class PlayFieldPane extends BorderPane implements Obervor{

    Canvas myCanvas;
    PlayField playField;
    GraphicsContext gc;
    HBox buttons = new HBox();
    TextField cardPosInput = new TextField();

    //Buttons for game interaction.
    Button play = new Button("PLAY");
    Button discard = new Button("DISCARD");

    JsonOps jsonOps;


    private double width = 1000;
    private double height = 600;


    public PlayFieldPane(PlayField theField) {
        this.playField = theField;
        this.myCanvas = new Canvas(width, height);

        this.gc = myCanvas.getGraphicsContext2D();
        this.setCenter(myCanvas);

        draw();

        buttons.getChildren().add(cardPosInput);


        play.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int pos = Integer.parseInt(cardPosInput.getText());
                // Hnadle errorssss

                playField.cardPlayedPos = pos;
                jsonOps.play(pos);

//                if (playResponse.built) {
//                    playField.playedCardPile.set(playResponse.gameCard.color.get().ordinal(), playResponse.gameCard);
//                    if (playResponse.replaced) {
//                        playField.playerCardsList.get(0).set(pos, new GameCard());
//                    } else {
//                        playField.playerCardsList.get(0).remove(pos);
//                    }
//                } else if (playResponse.burnt) {
//                    // TODO discard pile
//                    if (playResponse.replaced) {
//                        playField.playerCardsList.get(0).set(pos, new GameCard());
//                    } else {
//                        playField.playerCardsList.get(0).remove(pos);
//                    }
//                }
//                draw();
            }
        });

        buttons.getChildren().add(play);
        buttons.getChildren().add(discard);
        buttons.setAlignment(Pos.CENTER);
        this.setBottom(buttons);

    }

    public void setJsonOps(JsonOps jsonOps) {
        this.jsonOps = jsonOps;
    }

    public void draw() {

        gc.clearRect(0, 0, width, height);
        // user's hand

        drawCardHand(playField.playerCardsList.get(0), 400, 600);

        // other player's hand
        for (int i=1; i<playField.playerCardsList.size();i++) {
            if(i<2) {
                drawCardHand(playField.playerCardsList.get(i), 200, 200*i);
            }
            else{
            drawCardHand(playField.playerCardsList.get(3), 600, 200*(i/2));
            }
        }

        // played card pile


        drawCardHand(playField.playedCardPile, 400, 300);

    }


    public void drawCardHand(List<GameCard> cards, int x, int y) {
        gc.save();
        gc.translate(x, y - 100);
        gc.setStroke(Paint.valueOf("BLACK"));
        for (int i = 0; i < playField.playerCardsList.get(0).size(); i++) {
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
        gc.restore();
    }


    @Override
    public void modelChanged() {
        draw();
    }
}
