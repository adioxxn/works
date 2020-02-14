package sample;
import javafx.geometry.Pos;

import javax.swing.*;
import java.awt.*;

import java.util.List;


public class FireworkFrame extends JFrame{

    /**
     * This method will play the firework, the firework level will depend on the player's score
     * @param cards
     */
    public FireworkFrame(List<GameCard> cards){
        this.setTitle("Fire work");
        //this.setIconImage();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setExtendedState(MAXIMIZED_BOTH);
        this.setResizable(true);
        this.setUndecorated(false);


        new Screen(this,cards);
    }


}
