package sample;
import javafx.geometry.Pos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static com.sun.glass.ui.Cursor.setVisible;

public class FireworkFrame extends JFrame{

    /**
     * This method will play the firework, the firework level will depend on the player's score
     * @param cards A list of GameCard objects.
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
