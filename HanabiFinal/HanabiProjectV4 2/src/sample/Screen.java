package sample;

import sample.PlayField;
import sample.Particle;
import sample.VecT;

import javax.swing.*;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import sample.GameCard;




public class Screen implements Runnable {
    private ArrayList<Particle> red = new ArrayList<>();
    private ArrayList<Particle> blue = new ArrayList<>();
    private ArrayList<Particle> green = new ArrayList<>();
    private ArrayList<Particle> white = new ArrayList<>();
    private ArrayList<Particle> yellow = new ArrayList<>();
    private ArrayList<ArrayList<Particle>> total= new ArrayList<>();
    private ArrayList<Level1Firework> level1 = new ArrayList<>();
    private List<GameCard> cards;
    private Boolean isRunning;
    public Random random = new Random();//make firework in random position
    public int Counter1=0;
    private int refreshRate = 30;
    Thread thread = new Thread(this);
    Timer timer = new Timer();
    public static Color[] newcolors = new Color[]{ Color.RED,Color.BLUE,Color.WHITE,Color.GREEN,Color.YELLOW};
    protected static Color[] color1;
    public int[] list2= new int[5];

    private JPanel jpanel= new JPanel(){
        public void paintComponent(Graphics g){
            Graphics2D g2d =(Graphics2D) g;
            //draw the background
            g2d.setBackground(Color.BLACK);
            g.clearRect(0,0,frame.getWidth(),frame.getHeight());
            int sum1=0;
            for(int p=0; p<list2.length;p++) {
                sum1+=list2[p];

            }
            //draw level 3 and level 5 firework
            for(int i=0; i<fireworks.length;i++){
                if(fireworks[i]!= null){
                    for(int j =0;j<fireworks[i].sparcles.length;j++){
                        if(fireworks[i].sparcles[j]!=null){
                            g.setColor(fireworks[i].sparcles[j].color);
                            //g.drawLine((int)fireworks[i].sparcles[j].x,(int)fireworks[i].sparcles[j].y,(int)fireworks[i].sparcles[j].x,(int)fireworks[i].sparcles[j].y);
                            if(Screen.this.IsLevel5(fireworks[i].sparcles[j].color)){
                                g.drawOval((int)fireworks[i].sparcles[j].x,(int)fireworks[i].sparcles[j].y,15,15);
                                g.fillOval((int)fireworks[i].sparcles[j].x,(int)fireworks[i].sparcles[j].y,15,15);
                            }
                            else {
                                g.drawOval((int) fireworks[i].sparcles[j].x, (int) fireworks[i].sparcles[j].y, 5, 5);
                                g.fillOval((int) fireworks[i].sparcles[j].x, (int) fireworks[i].sparcles[j].y, 5, 5);
                            }
                        }
                    }
                }

            }
            //draw the white color level 2 or 4
            for (int counter=0; counter<list2.length;counter++) {
                if (list2[counter] > 1) {
                    ArrayList<Particle> color = particleMaker(total.get(counter), newcolors[counter]);//store tall the particles for onw firework in a list
                    for (int i = 0; i < color.size(); ++i) {
                        Particle p = color.get(i);
                        p.age += 0.1d;
                        if (p.age >= p.life) {
                            color.remove(i);
                        }
//                int r = p.color.getRGB(); //this part will random the color of the particle(it is for the rainbow
//                r -= 1000;
//                Color c = new Color(r);
//                p.color = c;
                        p.position = p.position.add(p.velocity.multiply(0.1d));//set the change of the position
                        p.velocity = p.velocity.add(p.acceleration.multiply(0.1d));//set the new gravity
                        g.setColor(p.color);
                        g.fillOval(p.getX(), p.getY(), p.size, p.size);//draw it
                    }
                }
            }

            if(Counter1%10==0 ) {//only draw the target color when the counter can be divided by 10 or when it in 0
                for (int counter = 0; counter < list2.length; counter++) {
                    if (list2[counter] > 0) {
                        ArrayList<Level1Firework> color = Level1Maker(newcolors[counter]);
                        for (int i = 0; i < color.size(); i++) {
                            Level1Firework p = color.get(i);
                            p.age += 0.1d;
                            if (p.age >= p.life) {
                                color.remove(i);
                            }
                            int length = 20;//the length for the line
                            g.setColor(p.color);//draw the cross firework
                            g.drawLine(p.x0, p.y0, p.x0 + length + length, p.y0);
                            g.drawLine(p.x0 + 6, p.y0 - 14, p.x0 + length + length - 6, p.y0 + 14);
                            g.drawLine(p.x0 + length, p.y0 - length, p.x0 + length, p.y0 + length);
                            g.drawLine(p.x0 + length + length - 6, p.y0 - 14, p.x0 + 6, p.y0 + length - 6);
                        }
                    }
                }
            }
            else{//one function to make a darker color to replace the old one
                for (int counter = 0; counter < list2.length; counter++) {
                    if (list2[counter] > 0) {
                        ArrayList<Level1Firework> color = Level1Maker(newcolors[counter]);
                        for (int i = 0; i < color.size(); i++) {
                            Level1Firework p = color.get(i);
                            p.age += 0.1d;
                            if (p.age >= p.life) {
                                color.remove(i);
                            }
                            int length = 20;
                            g.setColor(p.color.darker().darker());
                            g.drawLine(p.x0, p.y0, p.x0 + length + length, p.y0);
                            g.drawLine(p.x0 + 6, p.y0 - 14, p.x0 + length + length - 6, p.y0 + 14);
                            g.drawLine(p.x0 + length, p.y0 - length, p.x0 + length, p.y0 + length);
                            g.drawLine(p.x0 + length + length - 6, p.y0 - 14, p.x0 + 6, p.y0 + length - 6);
                        }
                    }}}

            Counter1+=1;


            g.setColor(Color.WHITE);
            g.setFont(new Font("TimeRoman", Font.PLAIN, 48));
            g.drawString("your Score is:"+Integer.toString(sum1), (jpanel.getWidth()/2)-200, 300);
//            JButton quit = new JButton("quit");
//            quit.addActionListener(new CloseListener());
//            quit.setBounds((jpanel.getWidth()/2)-100,600, 150,50);
//            jpanel.add(quit);



        }
    };
    public boolean IsLevel5(Color color){
        for (int i=0; i<list2.length;i++){
            if(color==newcolors[i]){
                return list2[i]==5;
            }
        }
        return false;
    }

    /**
     * this function will make the level one firework and make the target color in an Array List
     * @param color the target color
     * @return an Array list
     */
    private ArrayList<Level1Firework> Level1Maker(Color color) {
        Level1Firework tp = new Level1Firework();
        tp.color=color;
        int p=0;
        while (newcolors[p]!=color){
            p++;
        }
        tp.x0=250+p*200;
        tp.y0=60;
        tp.life = 5;
        tp.age = 1;
        level1.add(tp);
        return level1;
    }

    Firework[] fireworks;
    private Frame frame;
    public Screen (Frame frame, List<GameCard> cards){
        isRunning = true;//keep setting the isRunning is true
        this.cards=cards;
        setList();
        this.total.add(this.red);//add all 5 different arraylist into the total list
        this.total.add(this.blue);
        this.total.add(this.white);
        this.total.add(this.green);
        this.total.add(this.yellow);
        this.fireworks=new Firework[count(list2)*2];//double the size of the firework
        this.frame=frame;
        this.frame.setVisible(true);
        frame.add(jpanel);
        color1 = new Color[count(this.list2)];//make the color list
        setColor();//set pop firework color for the firework level above 3


        thread.start();
    }

    /**
     * This method will make the Array list contain the correct color particle
     * @param color the empty Array list for contain the target color particle
     * @param targetColor the target color
     * @return return the Array list with the firework
     */
    public ArrayList<Particle> particleMaker(ArrayList<Particle> color,Color targetColor){
        Particle tp = new Particle();
        int i=0;
        while (targetColor!=newcolors[i]) {
            i++;
        }
        tp.position = new VecT(50 + i * 300, jpanel.getHeight() - 10);
        tp.acceleration = sampleDirection();
        if(list2[i]>3){
            tp.size = 15;
            tp.life = 10;
            tp.velocity = new VecT(0, -20);

        }
        else{
            tp.size = 9;
            tp.life = 8;
            tp.velocity = new VecT(0, -25);
        }
        tp.age = 1;
        tp.color = targetColor;
        color.add(tp);
        return color;
    }

    /**
     * This method will set up the direction for the sparks
     * @return the veclocity for the spark
     */
    public static VecT sampleDirection() {
        double theta = Math.random() * 2 * Math.PI;
        return new VecT((Math.cos(theta)), (Math.sin(theta)));
    }


    /**
     * Create an action listener for the exit
     */
    private class CloseListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //DO SOMETHING
            System.exit(0);
        }
    }

    /**
     * set the Firework color for which level had reach to 3
     */
    public void setColor() {
        int count=0;
        for (int i = 0; i < newcolors.length; i++) {
            if(list2[i]>2){
                color1[count]=newcolors[i];
                count++;
            }
        }


        FireworkColors.colors = color1;//change the firework color only contain the correct color
    }
    public void setList(){
        //this is the function catch the playedpile in the playfield.
        this.list2[0]=cards.get(0).number.get();
        this.list2[1]=cards.get(2).number.get();
        this.list2[2]=cards.get(4).number.get();
        this.list2[3]=cards.get(1).number.get();
        this.list2[4]=cards.get(3).number.get();


    }


    /**
     * count how many firework reach to 3
     * @param list1 the list of all the level for all the colors
     * @return The count of fireworks built past 2.
     */
    public int count(int[] list1){
        int count=0;
        for(int i=0;i <list1.length;i++){
            if(list1[i]>2){
                count++;
            }
        }
        return count;
    }

    /**
     * make the function keep running
     */
    @Override
    public void run() {
        timer.scheduleAtFixedRate(task,0,1000/this.refreshRate);
    }

    /**
     * make the firework image keep updating
     */
    public void update(){
        for(int i = 0; i<fireworks.length; i++){
            if(fireworks[i]==null){//if there is no firework in spot, fill it up with new one
                fireworks[i]= new Firework();

            }
            fireworks[i].update();//make the firework explode

            if(fireworks[i].dead){//if the firework is dead, make the spot into null
                fireworks[i]=null;
            }
        }
    }

    /**
     * set the timer
     */
    private TimerTask task = new TimerTask(){
        public void run(){
            if(isRunning){
                jpanel.repaint();
                update();
            }
            else{
                System.exit(0);
            }
        }
    };

}
