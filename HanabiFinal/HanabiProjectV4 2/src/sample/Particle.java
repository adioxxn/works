package sample;

import java.awt.Color;

public class Particle {
    //粒子的起点，速度，重力
    public VecT position,velocity,acceleration;
    public Color color;
    public double life,age,start_time;
    public int size;
    //在界面上绘制时的x,y坐标
    public int x,y;
    public int getX(){
        return (int)position.x;
    }
    public int getY(){
        return (int)position.y;
    }
}

