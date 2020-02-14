package sample;

import java.util.Optional;

/**
 * The class used to represent cards
 * in the playField.
 */
public class GameCard {

    Optional<CardColor> color;
    Optional<Integer> number;

    public GameCard() {
        this.color = Optional.empty();
        this.number = Optional.empty();
    }

    public GameCard(CardColor color, int number) {
        this.color = Optional.of(color);
        this.number = Optional.of(number);
    }


    public GameCard(String color, int num) {

        CardColor cardColor = null;
        switch (color.toLowerCase()) {
            case "g":
                cardColor = CardColor.GREEN;
                break;
            case "b":
                cardColor = CardColor.BLUE;
                break;
            case "w":
                cardColor = CardColor.WHITE;
                break;
            case "y":
                cardColor = CardColor.YELLOW;
                break;
            case "r":
                cardColor = CardColor.RED;
                break;
        }

        this.color = Optional.ofNullable(cardColor);
        this.number = Optional.of(num);


    }

    @Override
    public String toString() {
        return (color.isPresent() ? color.get().toString().toLowerCase().charAt(0) : "?") + (number.map(integer -> integer + "").orElse("?"));
    }
}
