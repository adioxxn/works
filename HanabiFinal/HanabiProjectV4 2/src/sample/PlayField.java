package sample;

import java.util.*;

/**
 * The class which will
 * encapsulate the play field of our game.
 */
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

    /**getPlayerCardsList will return list of Every players cards from the
     * perspective of the local user who will be limited in what he/she
     * sees by the information he has.
     *
     * @return A List of List of game cards which represent each players hands.
     */
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

    /**Given a discard response we will either decrement the info tokens
     * and update the view if the discard was accepted by the
     * server. Otherwise nothing is done. Notifying the obsevers
     * in the end.
     *
     * @param discardResponse A discard response object formed
     *                        form a Json message recieved form the server
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
     * Other discard will be called if our client receives a
     * notice message from the server. If that is the case we
     * update the discarding players hand with the newly drawn
     * card and increment our info tokens. notifying the view
     * in the end.
     *
     * @param pos The position in the hand of the card discarded
     * @param card The card which was drawn from the deck.
     */
    public void otherDiscard(int pos, GameCard card){
        this.infoTokens += 1;
        int count = this.discardPile.getOrDefault(card.toString(), 0);
        this.discardPile.put(card.toString(), count + 1);
        playerCardsList.get(turnCounter % playerCardsList.size()).set(pos, card);
        notifyObservors();
    }


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
     * In the event of someone else playing a card will
     * call otherPlay. given the position of the card played
     * and the updated card. We will update the playFile with
     * the played card, update the playing players hand with a
     * new card.
     *
     * @param pos The position of the card which was played by
     *            the playing player.
     *
     * @param card  The card which was drawn by the server and
     *             given to the playing player
     */
    public void otherPlay(int pos, GameCard card){
        System.out.println(pos);
        int cardColor = playerCardsList.get(turnCounter % playerCardsList.size()).get(pos).color.get().ordinal();
        int cardNumber = playerCardsList.get(turnCounter % playerCardsList.size()).get(pos).number.get();
        int number = playedCardPile.get(cardColor).number.get();
        if (cardNumber - 1 ==number){
            playedCardPile.set(cardColor, playerCardsList.get(turnCounter % playerCardsList.size()).get(pos));}
        playerCardsList.get(turnCounter % playerCardsList.size()).set(pos, card);
        notifyObservors();

    }

    /**
     * adding an observer to the playField model which will
     * be used to provide a view to the user of the playField.
     *
     * @param obervor the observer who will be observing the model.
     */
    public void addObervor(Obervor obervor) {
        this.obervors.add(obervor);
    }

    /**
     * have each observer update because the model has changed.
     */
    private void notifyObservors() {
        this.obervors.forEach(Obervor::modelChanged);
    }


//    /**
//     * opponentDiscard will be responsible for updating the the users client playField when an opponent discards a card.
//     *
//     * @param handPosition  The hand position of the card discarded by the opponent.
//     * @param discardedcard The card that will be discarded.
//     * @param player        The player who is taking the discard action.
//     * @param newCard       The new card that is given to the user by the server on discard.
//     * @throws Exception The exception is thrown if all 8 info tokens on the field prohibiting discards.
//     */
//    public void opponentDiscard(int handPosition, String discardedcard, int player, GameCard newCard) throws Exception {
//        infoTokens += 1;
//        playerCardsList.get(player).set(handPosition, newCard);
//        if (this.discardPile.containsKey(discardedcard)) {
//            this.discardPile.put(discardedcard, this.discardPile.get(discardedcard) + 1);
//        } else {
//            this.discardPile.put(discardedcard, 1);
//        }
//    }

    /**
     * Changes the fields of playfield for an outgoing hint action
     * The only thing that changes is an info token is used up
     *
     * @param hintResponse hintResponse - A hintResponse intance
     *                     created from a message received from the server in response to our hint.
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



        //playerCardsList.add(Arrays.asList(new GameCard(), new GameCard(), new GameCard(), new GameCard(), new GameCard()));
        //playerCardsList.add(Arrays.asList(new GameCard(CardColor.RED, 2), new GameCard(CardColor.BLUE, 2), new GameCard(CardColor.WHITE, 3), new GameCard(CardColor.RED, 4), new GameCard(CardColor.YELLOW, 5)));
        //playerCardsList.add(Arrays.asList(new GameCard(CardColor.GREEN, 1), new GameCard(CardColor.BLUE, 2), new GameCard(CardColor.WHITE, 3), new GameCard(CardColor.RED, 4), new GameCard(CardColor.YELLOW, 5)));
        //playerCardsList.add(Arrays.asList(new GameCard(CardColor.GREEN, 1), new GameCard(CardColor.BLUE, 2), new GameCard(CardColor.WHITE, 3), new GameCard(CardColor.RED, 4), new GameCard(CardColor.YELLOW, 5)));
        //playerCardsList.add(Arrays.asList(new GameCard(CardColor.GREEN, 1), new GameCard(CardColor.BLUE, 2), new GameCard(CardColor.WHITE, 3), new GameCard(CardColor.RED, 4), new GameCard(CardColor.YELLOW, 5)));


        this.playedCardPile = new ArrayList<>();
        //Setup the playPile to have the proper colors and set the number to 0.
        for (int i = 0; i < CardColor.values().length; i++) {
            this.playedCardPile.add(new GameCard(CardColor.values()[i], 0));
        }

    }


//    public static void main(String[] args) {
//        List<List<GameCard>> cards = new ArrayList<>();
//
//        cards.add(Arrays.asList((new GameCard(CardColor.GREEN, 1)), new GameCard(CardColor.BLUE, 2), new GameCard(CardColor.WHITE, 3), new GameCard(CardColor.RED, 4), new GameCard(CardColor.YELLOW, 5)));
//        cards.add(Arrays.asList((new GameCard(CardColor.GREEN, 1)), new GameCard(CardColor.BLUE, 2), new GameCard(CardColor.WHITE, 3), new GameCard(CardColor.RED, 4), new GameCard(CardColor.YELLOW, 5)));
//        cards.add(Arrays.asList((new GameCard(CardColor.GREEN, 1)), new GameCard(CardColor.BLUE, 2), new GameCard(CardColor.WHITE, 3), new GameCard(CardColor.RED, 4), new GameCard(CardColor.YELLOW, 5)));
//        cards.add(new ArrayList<GameCard>());
//
//
//        PlayField P = new PlayField(cards, 1);
//
//        //Tests for initialization
//        System.out.println(P.playedCardPile.toString()); //Should be a list of five 0's
//        System.out.println(P.playerSeatNumber); //Should be 3
//
//
//        //Test for discarding a card.
//        System.out.println("\n\nTests for discarding a card");
//        try {
//            P.discard(1, "R3");
//            System.out.println("Error Test failed");
//        } catch (Exception e) {
//            //Test Passes because it throws exception.
//        }
//
//        System.out.println(P.infoTokens);
//        System.out.println(P.discardPile.keySet() + "\n\n");
//
//        P.infoTokens -= 2;
//
//        System.out.println("\n\n" + P.infoTokens);
//        try {
//            P.discard(1, "R3");
//        } catch (Exception e) {
//            System.out.println("Error Test failed");
//        }
//
//        System.out.println(P.infoTokens);
//        System.out.println(P.discardPile.keySet());
//        System.out.println(P.discardPile.values());
//
//        //Testing an opponentDiscard action.
//        System.out.println("Testing an opponentDiscard action:\n");
//
//        try {
//            P.opponentDiscard(1, "r2", 0, new GameCard(CardColor.RED, 3));
//        } catch (Exception e) {
//            System.out.println("Error: exception thrown for opponent discard test.");
//        }
//
//        for (GameCard i : P.playerCardsList.get(0)) {
//            System.out.print(i + " ");
//        }
//
//        System.out.println("\n" + P.infoTokens);
//        System.out.println(P.discardPile.get("r2"));
//
//    }
}
