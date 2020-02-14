package sample;

import java.util.Optional;

public class PlayResponse {
    boolean burnt;
    boolean built;
    boolean replaced;
    GameCard gameCard;

    public PlayResponse(boolean burnt, boolean built, boolean replaced, GameCard gameCard) {
        this.burnt = burnt;
        this.built = built;
        this.replaced = replaced;
        this.gameCard = gameCard;
    }
}
