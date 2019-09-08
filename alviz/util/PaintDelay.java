/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package alviz.util;

/**
 *
 * @author kevin
 */
public class PaintDelay {

    private int duration;

    public PaintDelay() {
        duration = 0;
    }

    public int getDelay() {
        return duration;
    }

    public String setDelay(int dur) {
        double u;

        if(dur != 0) u = (int) Math.pow(10, ((double)(dur-1000))/1000.0) ;
        else u = 0;

        this.duration = (int) u;

        if(u<1000) return String.format("%4.2fms",u);
        else return String.format("%5.2fs",u/1000.0);

    }
}
