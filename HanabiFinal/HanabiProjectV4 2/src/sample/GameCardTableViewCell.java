package sample;

/**
 * The table used as a discard pile in the
 * GUI for the playField.
 */
public class GameCardTableViewCell {
    private String color;
    private int numCount1;
    private int numCount2;
    private int numCount3;
    private int numCount4;
    private int numCount5;


    public GameCardTableViewCell(String color, int numCount1, int numCount2, int numCount3, int numCount4, int numCount5) {
        this.color = color;
        this.numCount1 = numCount1;
        this.numCount2 = numCount2;
        this.numCount3 = numCount3;
        this.numCount4 = numCount4;
        this.numCount5 = numCount5;
    }

    public String getColor() {
        return color;
    }

    public int getNumCount1() {
        return numCount1;
    }

    public int getNumCount2() {
        return numCount2;
    }

    public int getNumCount3() {
        return numCount3;
    }

    public int getNumCount4() {
        return numCount4;
    }

    public int getNumCount5() {
        return numCount5;
    }
}
