package sample;

public class DiscardResponse {
    //boolean invalid;
    boolean accepted;
    boolean replaced;
    GameCard gameCard;

    public DiscardResponse(boolean accepted, boolean replaced, GameCard gameCard) {
        //this.invalid = invalid;
        this.accepted = accepted;
        this.replaced = replaced;
        this.gameCard = gameCard;
    }
}
