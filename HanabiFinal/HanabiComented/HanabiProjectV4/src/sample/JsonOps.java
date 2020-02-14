package sample;


import com.google.gson.Gson;
import com.json.generators.JSONGenerator;
import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;
import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.security.MessageDigest;
import java.math.*;
import java.util.*;

/**
 * The class resoponsible for receiving and
 * sending messages from and to the game server.
 */
public class JsonOps {


    BufferedReader in;
    Socket gameServer;
    PlayField playField;
    Controller controller;

    Gson gson = new Gson();

    public void setPlayField(PlayField playField) {
        this.playField = playField;
    }
    public void setController(Controller controller){this.controller = controller;}
    public JsonOps() {
        final String host = "GPU2.USASK.CA";
        final int portNumber = 10219;
        try {

            gameServer = new Socket(host, portNumber);


            in = new BufferedReader(new InputStreamReader(gameServer.getInputStream()));
            new Thread(() -> receiveMessageFromServer(in)).start();

        } catch (IOException e) {
            System.out.println("Can't connect to server");
            System.out.println(e);

        }
    }


    //server's reply
    class Built {
        String reply;
        String card;
        boolean replaced;
    }

    class Burned {
        String reply;
        String card;
    }

    class Accepted{
        String reply;
        String card;
        boolean replaced;
    }

    //server's notices
    class Discarded{
        String notice;
        int position;
        String drew;
    }

    class Played{
        String notice;
        int position;
        String firework;
        String card;
    }

    class End {
        String notice;
    }

    class Create {
        String [][] hands;
    }

    class Invalid{
        boolean invalid;
    }

    class InformByRank{
        String notice;
        String rank;
        boolean[] info;
    }

    class InformBySuit{
        String notice;
        String suit;
        boolean[] info;
    }

    class InformOtherByRank{
        String notice;
        int player;
        int rank;
    }

    class InformOtherBySuit{
        String notice;
        int player;
        String suit;
    }

    public void receiveMessageFromServer(Reader reader) {

        while (gameServer.isConnected()) {
            try {
                char[] buffer = new char[1024];
                int bytesdRead = reader.read(buffer);
                String message = new String(buffer).substring(0, bytesdRead);
                System.out.println(message);
                String[] messages = message.split("}");
                System.out.print(messages);
                for (String m : messages) {
                    String actualMessage = m + "}";

                    if (actualMessage.contains("built")) {
                        Built built = gson.fromJson(message, Built.class);
                        playField.play(new PlayResponse(
                                false, true, built.replaced, new GameCard(built.card.charAt(0) + "", Integer.parseInt(built.card.charAt(1) + ""))
                        ));
                        controller.modelChanged();


                    } else if (actualMessage.contains("burned")) {
                        Burned burned = gson.fromJson(message, Burned.class);
                        playField.play(new PlayResponse(
                                true, false, true, new GameCard(burned.card.charAt(0) + "", Integer.parseInt(burned.card.charAt(1) + ""))
                        ));
                        controller.modelChanged();
                    }
                    else if (actualMessage.contains("accepted") && actualMessage.contains("card")){
                        Accepted accepted = gson.fromJson(message, Accepted.class);
                        playField.turnCounter += 1;
                        playField.discard(new DiscardResponse(
                                true, true, new GameCard(accepted.card.charAt(0) + "", Integer.parseInt(accepted.card.charAt(1) + ""))
                        ));
                        controller.modelChanged();
                    }
                    else if(actualMessage.contains("invalid")) {
                        Invalid invalid = gson.fromJson(message, Invalid.class);
                        try {
                            playField.giveHint(new HintResponse(true));
                        }catch (Exception e){
                        }

                    }else if(actualMessage.contains("inform")) {
                        GameCard infoCard;
                        if (message.contains("rank")){
                            if (message.contains("player")){
                                InformOtherByRank informOtherByRank = gson.fromJson(message, InformOtherByRank.class);
                            }
                            else{

                                InformByRank informByRank = gson.fromJson(message, InformByRank.class);
                                infoCard = new GameCard("", Integer.parseInt(informByRank.rank));
                                playField.receiveHint(infoCard, informByRank.info);
                            }
                        }
                        else{
                            if (message.contains("player")){
                                InformOtherBySuit informOtherBySuit = gson.fromJson(message, InformOtherBySuit.class);
                            }
                            else{
                                InformBySuit informBySuit = gson.fromJson(message, InformBySuit.class);
                                infoCard = new GameCard(informBySuit.suit, 0);
                                playField.receiveHint(infoCard, informBySuit.info);
                            }
                        }
                        controller.modelChanged();
                    }

                    //notices
                    else if (actualMessage.contains("played")){
                        Played played = gson.fromJson(message, Played.class);
//                        System.out.println(played.notice);
//                        System.out.println(played.pos);
//                        System.out.println(played.firework);
                        playField.turnCounter += 1;
//                        System.out.println(played.card);
                        playField.otherPlay(played.position -1 , new GameCard(played.card.charAt(0) + "", Integer.parseInt(played.card.charAt(1) + "")));
                        controller.modelChanged();
                    } else if(actualMessage.contains("discarded")){
                        Discarded discarded = gson.fromJson(message, Discarded.class);
                        playField.turnCounter += 1;
                        playField.otherDiscard(discarded.position -1 , new GameCard(discarded.drew.charAt(0) + "", Integer.parseInt(discarded.drew.charAt(1) + "")));
                        controller.modelChanged();
                    }else if(actualMessage.contains("game ends")){
                            FireworkFrame FF=new FireworkFrame(playField.playedCardPile);
                    }else if(actualMessage.contains("game cancelled")){
                        FireworkFrame FF=new FireworkFrame(playField.playedCardPile);
                    }
                    else if (actualMessage.contains("game starts")) {

                        Create create = gson.fromJson(message, Create.class);
                        int position=0;
                        for (String[] P : create.hands){
                            if(P.length==0){
                                playField.playerSeatNumber=position;
                            }
                            position++;
                        }

                        for (String[] L : create.hands) {
                            List<GameCard> P = new ArrayList<>();


                            if (create.hands.length < 4) {
                                for (int i = 0; i < 5; i++) {
                                    GameCard N = new GameCard();
                                    P.add(N);
                                }
                            } else {
                                for (int i = 0; i < 4; i++) {
                                    GameCard N = new GameCard();
                                    P.add(N);
                                    }
                                }
                            playField.playerCardsList.add(P);
                        }

                        int travel = 0;
                        for (String[] L : create.hands) {
                            List<GameCard> P = new ArrayList<>();

                            if(L.length==0){

                            }else {
                                for (String s : L) {
                                    GameCard N = new GameCard(Character.toString(s.charAt(0)), Character.getNumericValue(s.charAt(1)));
                                    P.add(N);
                                }
                                if(travel<playField.playerSeatNumber){
                                    playField.playerCardsList.set(position+travel-1,P);
                                }
                                else{
                                    playField.playerCardsList.set(travel,P);
                                }
                            }

                            travel++;
                        }


//                        for (String[] L : create.hands) {
//                            List<GameCard> P = new ArrayList<>();
//
//                            if(L.length==0){
//                                if (create.hands.length < 4) {
//                                    for (int i = 0; i < 5; i++) {
//                                        GameCard N = new GameCard();
//                                        P.add(N);
//                                    }
//                                } else {
//                                    for (int i = 0; i < 4; i++) {
//                                        GameCard N = new GameCard();
//                                        P.add(N);
//                                    }
//                                }
//                            }else {
//                                for (String s : L) {
//                                    GameCard N = new GameCard(Character.toString(s.charAt(0)), Character.getNumericValue(s.charAt(1)));
//                                    P.add(N);
//                                }
//                            }
//                            playField.playerCardsList.add(P);
//                        }
                        controller.modelChanged();





                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    public void sendMessageToServer(String message) {
        System.out.println(message);
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(gameServer.getOutputStream()));
            out.write(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public
    class PlayMessage {
        String action;
        int position;

        public PlayMessage(int pos) {
            this.action = "play";
            this.position = pos;
        }
    }

    public
    class DiscardMessage {
        String action;
        int position;

        public DiscardMessage(int pos) {
            this.action = "discard";
            this.position = pos;
        }
    }

    public class HintRank {
        String action;
        int player;
        int rank;

        public HintRank(int player, int rank){
            this.action = "inform";
            this.player = player;
            this.rank = rank;
        }
    }

    public class HintSuit {
        String action;
        int player;
        String suit;

        public HintSuit(int player, String suit){
            this.action = "inform";
            this.player = player;
            this.suit = suit;
        }

    }


    class JsonCreateMessage {
        String cmd;
        String nsid;
        int players;
        int timeout;
        String rainbow;
        int timestamp;
        String md5hash;
        boolean force;

        public JsonCreateMessage(int numPlayers, int timeout, String rainbow, String md5hash) {
            this.cmd = "create";
            this.nsid = "hos540";
            this.players = numPlayers;
            this.timeout = timeout;
            this.rainbow = rainbow;
            this.timestamp = (int) Instant.now().getEpochSecond();
            this.md5hash = md5hash;
            this.force = true;
        }
    }



    /**
     * Generates a string containing the JSON for a create game outgoing message
     *
     * @param numberOfPlayers the number of players in the game (2 - 5)
     * @param timeoutLength   timeout length in seconds
     * @param fireworkType    A string containing the use of rainbow cards
     * @return A json message containing all information required to be sent to the server
     */
    public String generateCreateMsg(int numberOfPlayers, int timeoutLength, String fireworkType) {

        //HashMap H = new HashMap();
        JsonCreateMessage jsonCreateMessage =
                new JsonCreateMessage(numberOfPlayers, timeoutLength, fireworkType, "3ac6041ed819cc230ad8b4e4da641b4a");




        /*
        H.put("cmd", "create");
        H.put("nsid", info[0]);
        H.put("players", numberOfPlayers);
        H.put("timeout", timeoutLength);
        H.put("rainbow", fireworkType);
        H.put("timestamp", (int) Instant.now().getEpochSecond());
        H.put("md5hash", info[1]);
        */
        Gson j = new Gson();

        String jsonMessage = j.toJson(jsonCreateMessage);
        String newMd5Hash = computeHash(jsonMessage);

        JsonCreateMessage jsonCreateMessageWithMd5Hash =
                new JsonCreateMessage(numberOfPlayers, timeoutLength, fireworkType, newMd5Hash);

        return j.toJson(jsonCreateMessageWithMd5Hash);

    }


    /**
     * Generates a JSON message for a join game action
     *
     * @param gameID The integer ID of the game to be joined
     * @param token  The secret token of the game as a string
     * @return A String containing the JSON used to create a game
     */
    public String generateJoinMessage(int gameID, String token) {
        JSONGenerator j = new JSONGenerator();
        HashMap<String, Object> H = new HashMap<>();
        H.put("cmd", "join");
        H.put("nsid", "zhz028");
        H.put("game-id", gameID);
        H.put("token", token);
        H.put("timestamp", (int) Instant.now().getEpochSecond());
        H.put("md5hash", "4df9f5e1d0edd37bf6943cecce6191a5");
        String js = j.generateJson(H);
        String jsToBeHash = js.substring(1, js.length()-1);
        String newHash = computeHash(jsToBeHash);
        H.put("md5hash", newHash);


        js = j.generateJson(H);
        return js.substring(1, js.length()-1);

    }

    private String hasher(Object K, Gson j) {
        String json = j.toJson(K);
        String newMD5 = computeHash(json);


        json.replace("md5hash", newMD5);
        json = j.toJson(K);
        return json;
    }


    /**
     * Translates an incoming Json into a hashmap
     * Required for using with quick json for reasons unknown
     *
     * @param jsonMessage A json to be converted into a hashmap
     * @return A hashmap containing the information from a Json
     */
    public Map parse(String jsonMessage) {
        JsonParserFactory factory = JsonParserFactory.getInstance();
        JSONParser parser = factory.newJsonParser();
        Map jsonData = parser.parseJson(jsonMessage);

        List rootJson = (List) jsonData.get("root");
        return ((Map) rootJson.get(0));
    }

    public void whatDo(Map M) {
        if (M.containsKey("reply")) {
            switch ((String) M.get("reply")) {
                case "extant":
                    System.out.println("Game ID: " + M.get("game-id")
                            + "\nToken: " + M.get("token"));
                    break;
                case "created":
                    System.out.println("Game ID: " + M.get("game-id")
                            + "\nToken: " + M.get("token"));
                    break;
                case "no such game":
                    System.out.println("No such game to join");
                    break;
                case "game full":
                    System.out.println("The game is full. Try another one.");
                    break;
                case "joined":
                    System.out.println("Still need:" + M.get("needed") + " players to join"
                            + "\nThe timeout is: " + M.get("timeout"));
                    break;
                case "accepted":
                    System.out.println("card to discard is: " + M.get("card")
                            + "\nThe new card to reply is: " + M.get("replaced"));
                    break;
                case "built":
                    System.out.println("Card to build: " + M.get("card") + "\nTo replace: " + M.get("replaced"));
                    break;
                case "burned":
                    System.out.println("card to burn: " + M.get("card"));
                    break;
                default:
                    System.out.println("Something went wrong!");
            }
        } else if (M.containsKey("notice")) {
            switch ((String) M.get("notice")) {
                case "player joined":
                    System.out.println("Players Needed: " + M.get("needed"));
                    break;
                case "player left":
                    System.out.println("Players Needed: " + M.get("needed"));
                    break;
                case "game start":
                    System.out.println("Hands: " + M.get("hands"));
                    break;
                case "your turn":
                    System.out.println("It's your turn!");
                    break;
                case "your move":
                    System.out.println("It's your turn!");
                    break;
                case "game cancelled":
                    System.out.println("The game is over");
                    break;
                case "game ended":
                    System.out.println("The game is over");
                    break;
                case "discarded":
                    System.out.println("The position of the discarded card: " + M.get("position")
                            + "\nWas the card replaced? " + M.get("replaced"));
                    break;
                case "played":
                    System.out.println("The position of the played card: " + M.get("position")
                            + "\nThe replacement card is " + M.get("drew"));
                    break;
                case "inform":
                    if (M.containsKey("cards")) {
                        if (M.containsKey("rank")) {
                            System.out.println("The cards in positions " + M.get("cards") + " are rank " + M.get("rank"));
                        } else {
                            System.out.println("The cards in positions " + M.get("cards") + " are suit " + M.get("suit"));
                        }
                    } else {
                        if (M.containsKey("rank")) {
                            System.out.println("Player " + M.get("player") + " was informed of his/her " + M.get("rank"));
                        } else {
                            System.out.println("Player " + M.get("player") + " was informed of his/her " + M.get("suit"));
                        }
                    }
                    break;
                default:
                    System.out.println("Something went wrong");
            }
        }
    }

    //Computes the md5 hash for the outgoing json message
    private static String computeHash(String msg) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(msg.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            return ("MD5 ... what's MD5?");
        }
    }


    /**
     * Collects the player's nsid and secret token from a text file
     *
     * @return A string list with NSID and secret.
     */
    public String[] getPlayerInfo() {
        String[] info = new String[2];
        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader("/home/brendan/Documents/CMPT370/gitRepo/h2/CodeImplementationBrendan/src/sample/playerInfo.txt");
            br = new BufferedReader(fr);

            info = br.readLine().split(" ");


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return info;
    }


    public void tester(HashMap H) {
        JsonOps j = new JsonOps();
        JSONGenerator js = new JSONGenerator();
        String json = js.generateJson(H);
        System.out.println(json + "\n");
        Map W = j.parse(json);
        j.whatDo(W);
    }


    /**
     * TODO
     * This should connect you to the server
     * Currently not working
     * Might be my computer not being a school computer
     * Might also be the server is not up yet
     *
     * @return The socket that is connected to the server
     */
    public Socket connectToServer() {
        final String host = "GPU2.USASK.CA";
        final int portNumber = 10219;
        try {

            Socket gameServer = new Socket(host, portNumber);

            PrintWriter out = new PrintWriter(gameServer.getOutputStream(), true);

            BufferedReader in = new BufferedReader(new InputStreamReader(gameServer.getInputStream()));

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            return gameServer;

        } catch (IOException e) {
            System.out.println("Can't connect to server");
            System.out.println(e);
            return null;
        }
    }

    /**
     * This should in theory send a json message over to the specified port
     * Once again, I have no way to test this, so it could very well be useless
     *
     * @param jsonMessage The json message we would like to send.
     * @param gameServer The socket that is connected to the server.
     */
    public void sendMessage(String jsonMessage, Socket gameServer) {
        try (OutputStreamWriter out = new OutputStreamWriter(gameServer.getOutputStream())) {
            out.write(jsonMessage);
        } catch (IOException e) {
            System.out.println("connection to server failed\n" + e);
        }

    }

    /**
     * And in theory, this should be what we need to receive a message
     *
     * @param gameServer The socket of the server as given by connectToServer
     * @return The string containing the most recent JSON message
     */
    public String receiveMessage(Socket gameServer) {
        try {
            InputStream input = gameServer.getInputStream();
            return input.toString();
        } catch (IOException e) {
            System.out.println("read failed\n" + e);
        }
        return null;
    }

    public void play(int pos) {
        PlayMessage playMessage = new PlayMessage(pos);
        this.sendMessageToServer(new Gson().toJson(playMessage));
    }

    public void discard(int pos){
        DiscardMessage discardMessage = new DiscardMessage(pos);
        this.sendMessageToServer(new Gson().toJson(discardMessage));

    }

    public void hintRank(int player, int rank){
        HintRank hintRank = new HintRank(player,rank);
        this.sendMessageToServer(new Gson().toJson(hintRank));

    }

    public void hintSuit(int player, String suit){
        HintSuit hintSuit = new HintSuit(player,suit);
        this.sendMessageToServer(new Gson().toJson(hintSuit));

    }


    public static void main(String[] args) {
        JsonOps J = new JsonOps();

        //System.out.println(j.generateCreateMsg(3,30,"none"));
        //System.out.println(j.generateJoinMessage(1234, ";kajsdhf"));
        J.sendMessageToServer(
                J.generateCreateMsg(2, 60, "none")
        );
        /*
        Socket gameServer = J.connectToServer();
        String json = J.generateCreateMsg(3,40,"none");
        J.sendMessage(json,gameServer);
        */
        /*
        Socket gameServer = J.connectToServer();
        System.out.println(J.receiveMessage(gameServer) + "\n");

        String msg = J.generateCreateMsg(3,40,"none");
        J.sendMessage(msg, gameServer);
        System.out.println(msg);
        System.out.println(J.receiveMessage(gameServer));
        try {
            gameServer.close();
        } catch (IOException e){
            System.out.println("Something went Wrong");
        }
        */

    }

}

