package sample;

import java.awt.Color;
import java.util.Random;

public class FireworkColors {
    /**
     * default color contain 5 color
     */
    public static Color[] colors = new Color[]{ Color.RED,Color.BLUE,Color.WHITE,Color.GREEN,Color.YELLOW};

    private static Random random=new Random();

    /**
     * get a random color from the list
     * @return
     */
    public static Color getRandomColor(){
        return colors[random.nextInt(colors.length)];
    }

    /**
     * pick a color from the colors list
     * @param p integer
     * @return a color
     */
    public static Color getTargetColor(int p){return colors[p];}



}
