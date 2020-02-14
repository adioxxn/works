package sample;

public class VecT {
    public double x, y;

    /**
     * set the new velocity for the firework
     * @param x location in x axis
     * @param y location in y axis
     */
    public VecT(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // add the velocity
    public VecT add(VecT p) {
        return new VecT(this.x + p.x, this.y + p.y);
    }

    // multiply the velocity
    public VecT multiply(double f) {
        return new VecT(this.x * f, this.y * f);
    }

}

