package sample;

import java.util.*;

public class PlayField {
    public List<List<GameCard>> playerCardsList;
    public List<GameCard> playedCardPile;
    public Map<String, Integer> discardPile;
    public List<Obervor> obervors = new ArrayList<>();

    private int gameType;
    public int infoTokens;
    private int fuseTokens;
    public int playerSeatNumber;

    int cardPlayedPos;
    int turnCounter;

    public PlayField(List<List<GameCard>> playerCardsList, int gameType) {
        int handLength = 0;

        this.playerCardsList = playerCardsList;
        this.playedCardPile = new ArrayList<>();
        this.discardPile = new HashMap<>();

        this.gameType = gameType; //0 is normal, 1 is firework, and 2 is wild.
        this.infoTokens = 8;
        this.fuseTokens = 3;


        //Checking the list of lists to get the users seat number.
        for (int i = 0; i < this.playerCardsList.size(); i++) {
            if (this.playerCardsList.get(i).size() == 0) {
                this.playerSeatNumber = i;
            } else {
                handLength = this.playerCardsList.get(i).size();
            }
        }

        //Initialize the users hand as hand of blank cards (with not info).
        for (int i = 0; i < handLength; i++) {
            this.playerCardsList.get(this.playerSeatNumber).add(new GameCard());
        }


        //Setup the playPile to have the proper colors and set the number to 0.
        for (int i = 0; i < CardColor.values().length; i++) {
            this.playedCardPile.add(new GameCard(CardColor.values()[i], 0));
        }


    }

    public List<List<GameCard>> getPlayerCardsList() {
        return this.playerCardsList;
    }



    public int getPlayerSeatNumber() {
        return this.playerSeatNumber;
    }

    /**
     * getInfoTokens will return the number of info tokens in the playField.
     *
     * @return An integer which is the number of info tokens in the playfield.
     */
    public int getInfoTokens() {
        return this.infoTokens;
    }


    /**
     * getInfoTokens will return the number of info tokens in the playField.
     *
     * @return An integer which is the number of info tokens in the playfield.
     */
    public int getFuseTokens() {
        return this.fuseTokens;
    }


    /**
     * This method will handle the situation when the user discard a card
     * @param discardResponse
     */
    public void discard(DiscardResponse discardResponse){
        int pos = cardPlayedPos;
        if (discardResponse.accepted){
            this.infoTokens += 1;
            int count = this.discardPile.getOrDefault(discardResponse.gameCard.toString(), 0);
            this.discardPile.put(discardResponse.gameCard.toString(), count + 1);
            playerCardsList.get(0).set(pos, new GameCard());
        }
        notifyObservors();
    }


    /**
     * This method will handle the situation when the server send us a message to notice when other player discarded a card
     * @param pos
     * @param card
     */
    public void otherDiscard(int pos, GameCard card){
        this.infoTokens += 1;
        int count = this.discardPile.getOrDefault(card.toString(), 0);
        this.discardPile.put(card.toString(), count + 1);
        playerCardsList.get(turnCounter % playerCardsList.size()).set(pos, card);
        notifyObservors();
    }

    /**
     * This method will handle the situation when user played a card
     * @param playResponse
     */
    public void play(PlayResponse playResponse) {
        int pos = cardPlayedPos;
        if (playResponse.built) {
            playedCardPile.set(playResponse.gameCard.color.get().ordinal(), playResponse.gameCard);
            if (playResponse.replaced) {
                playerCardsList.get(0).set(pos, new GameCard());
            } else {
                playerCardsList.get(0).remove(pos);
            }
        } else if (playResponse.burnt) {
            int count = this.discardPile.getOrDefault(playResponse.gameCard.toString(), 0);
            this.discardPile.put(playResponse.gameCard.toString(), count + 1);

            if (playResponse.replaced) {
                playerCardsList.get(0).set(pos, new GameCard());
            } else {
                playerCardsList.get(0).remove(pos);
            }
            this.fuseTokens--;
        }

        notifyObservors();
    }

    /**
     * This method will handle the situation when the server send us a message to notice when other player played a card
     * @param pos
     * @param card
     */
    public void otherPlay(int pos, GameCard card){
        System.out.println(pos);
        System.out.println(turnCounter);
        System.out.println(turnCounter % playerCardsList.size());

        int cardColor = playerCardsList.get(turnCounter % playerCardsList.size()).get(pos).color.get().ordinal();
        int cardNumber = playerCardsList.get(turnCounter % playerCardsList.size()).get(pos).number.get();
        int number = playedCardPile.get(cardColor).number.get();
        if (cardNumber - 1 ==number){
            playedCardPile.set(cardColor, playerCardsList.get(turnCounter % playerCardsList.size()).get(pos));}
        playerCardsList.get(turnCounter % playerCardsList.size()).set(pos, card);
        notifyObservors();

    }

    public void addObervor(Obervor obervor) {
        this.obervors.add(obervor);
    }

    private void notifyObservors() {
        this.obervors.forEach(Obervor::modelChanged);
    }

    /**
     * Changes the fields of playfield for an outgoing hint action
     * The only thing that changes is an info token is used up
     *
     * @throws Exception if there are no information tokens remaining
     */
    public void giveHint(HintResponse hintResponse) throws Exception {
        if (hintResponse.invalid) {
            throw new Exception("Cannot give hint when there are no info tokens remaining");
        } else {
            infoTokens = infoTokens - 1;
        }
    }



    /**
     * Receives a hint from another player
     * Updates the players hand if there are still hint tokens available
     *
     * @param info      The suit or rank hinted at, given as a string
     * @param positions An array of booleans containing true if the card in that position satisfies the hint condition
     *                  and false otherwise
     */
    public void receiveHint(GameCard info, boolean[] positions) {

        this.infoTokens = this.infoTokens - 1;
        System.out.println(positions.length);
        String num = info.number.map(integer -> integer + "").orElse("");
        for (int i = 0; i < positions.length; i++) {

            if (positions[i]) {
                if (num.equals("0")) {
                    this.playerCardsList.get(0).get(i).color = info.color;
                } else {
                    this.playerCardsList.get(0).get(i).number = info.number;
                }
            }
        }
    }


    public PlayField() {

        this.playedCardPile = new ArrayList<>();
        this.discardPile = new HashMap<>();

        this.gameType = 0; //0 is normal, 1 is firework, and 2 is wild.
        this.infoTokens = 8;
        this.fuseTokens = 3;

        playerCardsList = new ArrayList<>();
        GameCard n = new GameCard();

        this.playedCardPile = new ArrayList<>();
        //Setup the playPile to have the proper colors and set the number to 0.
        for (int i = 0; i < CardColor.values().length; i++) {
            this.playedCardPile.add(new GameCard(CardColor.values()[i], 0));
        }

    }
}
