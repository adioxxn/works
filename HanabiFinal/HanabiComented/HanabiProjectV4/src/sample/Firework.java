package sample;


import java.awt.*;
import java.util.Random;

/**
 * The class used to create fireworks.
 */
public class Firework {


    public Sparcle[] sparcles;
    public Random random = new Random();//make firework in random position
    public Toolkit toolkit = Toolkit.getDefaultToolkit();
    public boolean exploded;
    public int x;
    public int y;
    public int fuse;
    public boolean dead;
    Color fireworkColor;

    /**
     * This constructor will make the firework in a random positon and set a random color for the firework
     */
    public Firework(){
        this.x = random.nextInt(toolkit.getScreenSize().width - 200)+100;
        this.y = random.nextInt(toolkit.getScreenSize().height-150)+100;
        this.fuse =random.nextInt(100);
        this.fireworkColor=FireworkColors.getRandomColor();
        this.sparcles = new Sparcle[100];

    }


    /**
     * This method will set the old sparcle in the list into a new position
     */
    public void explode(){
        for(int i=0; i<this.sparcles.length;i++){
            this.sparcles[i]= new Sparcle(x, y);
            this.sparcles[i].color =this.fireworkColor;

        }
        this.exploded =true;
    }

    /**
     * This method will update the firework when it was dead
     */
    public void update() {
        if (this.exploded) {
            int amountOfSparcleDead = 0;

            for (int i = 0; i < sparcles.length; i++) {
                if (sparcles[i] != null) {
                    this.sparcles[i].update();

                    if (this.sparcles[i].dead) {
                        this.sparcles[i] = null;
                    }
                } else {
                    amountOfSparcleDead++;
                }
            }
            if (amountOfSparcleDead == this.sparcles.length) {
                this.dead = true;
            }
        }else{
            this.fuse--;
            if(this.fuse<=0){
                explode();
            }
        }
    }



}
