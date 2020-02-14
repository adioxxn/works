package sample;

import java.util.Random;
import java.awt.Color;

public class Sparcle {

    float x;
    float y;
    Random random = new Random();
    float motionX;
    float motionY;
    Color color;
    float age;
    float speed;
    float maxAge;
    boolean dead=false;
    float speedFade;


    /**
     * This constructor will set up the sparcle by taking the location
     * @param x location in x axis
     * @param y location in y axis
     */
    public Sparcle(float x, float y ){
        this.x=x;
        this.y = y;
        this.maxAge=90F;//the age of the sparcle is 90F
        this.speed=random.nextFloat()*5+1;
        //the speed of the sparcle will change every second
        this.motionX = (random.nextFloat()-0.5F)*this.speed;
        this.motionY = (random.nextFloat()-1F)*this.speed;
        this.speedFade= 0.99F;
    }
    public void update(){
        //each time update the x and y will change by it speed
        this.x += this.motionX;
        this.y += this.motionY;

        this.motionX*=this.speedFade;
        this.motionY*=this.speedFade;
        this.motionY+=0.1F;
        this.age++;
        //the sparcle color will go down every 10F
        if(this.age%10 ==0){
            this.color=this.color.darker();
        }

        if(this.age >=this.maxAge){
            this.dead =true;
        }
    }



}
