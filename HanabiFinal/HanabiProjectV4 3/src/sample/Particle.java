package sample;

import java.awt.Color;

public class Particle {
    //set up the velocity and position for the particle
    public VecT position,velocity,acceleration;
    public Color color;
    public double life,age,start_time;
    public int size;
    public int x,y;
    public int getX(){
        return (int)position.x;
    }
    public int getY(){
        return (int)position.y;
    }
}

