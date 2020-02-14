package sample;

import java.awt.Color;
import java.util.Random;

/**
 * the class containing the firework colors we use.
 */
public class FireworkColors {

    /**
     * default color contain 5 color
     */
    public static Color[] colors = new Color[]{ Color.RED,Color.BLUE,Color.WHITE,Color.GREEN,Color.YELLOW};

    private static Random random=new Random();

    /**
     * get a random color from the list
     * @return a random Color object from the list.
     */
    public static Color getRandomColor(){
        return colors[random.nextInt(colors.length)];
    }

    /**
     * pick a color from the colors list
     * @param p The index of the color you would like.
     * @return the color at index p.
     */
    public static Color getTargetColor(int p){return colors[p];}



}
