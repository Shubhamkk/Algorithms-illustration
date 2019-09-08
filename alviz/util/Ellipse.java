/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.util;

/**
 *
 * @author baskaran
 */
public class Ellipse {

    private Ellipse() {}

    static public double perimeter(double a, double b) {
        return perimeter_Ramanujan_2(a, b);
    }
    
    static private function dp = new ramanujan_2();
    static public double majorAxis(double p, double r) {
        return majorAxis(p, r, dp);
    }
    
    static private double perimeter_Ramanujan_1(double a, double b) {
        double s  = a+b;
        double ds = (a-b)/s;
        double h  = ds*ds;

        double p = Math.PI * s * (3 - Math.sqrt(4-h));
        return p;

    }

    static private double perimeter_Ramanujan_2(double a, double b) {
        double s  = a+b;
        double ds = (a-b)/s;
        double h  = ds*ds;

        double h3 = 3 * h;
        double p = Math.PI * s * (1 + h3 / (10 + Math.sqrt(4-h3)));
        return p;

    }

    static private double perimeter_Zafary(double a, double b) {
        double s  = a+b;
        double ds = (a-b)/s;
        double h  = ds*ds;

        double p = Math.PI * s * Math.pow((4 / Math.PI), h);
        return p;

    }

    static private interface function {
        public abstract double value(double p, double a, double b);
    }
    static private class ramanujan_1 implements function {
        public double value(double p, double a, double b) {
            return p - Ellipse.perimeter_Ramanujan_1(a, b);
        }
    }
    static private class ramanujan_2 implements function {
        public double value(double p, double a, double b) {
            return p - Ellipse.perimeter_Ramanujan_2(a, b);
        }
    }
    static private class zafary implements function {
        public double value(double p, double a, double b) {
            return p - Ellipse.perimeter_Zafary(a, b);
        }
    }

    /**
     * p - perimeter of ellipse
     * r - ration = b/a or b = r*a or (height/width) in screen dimension
     * @param p
     * @param r
     * @return
     */
    static private double majorAxis(double p, double r, function f) {

        // initial estimate
        double x0 = p/(Math.PI*(1+r));
        double y0 = f.value(p, x0, r*x0);

        // bracket the value
        double x1 = x0;
        double y1 = y0;

        //System.out.printf("p0=(%f, %f), p1(%f, %f) - estimate\n", x0, y0, x1, y1);

        if (y0 > 0) {
            while (y1 > 0) {
                x1  = x1 * 1.1;
                y1 = f.value(p, x1, r*x1);
                //System.out.printf("p0=(%f, %f), p1(%f, %f)\n", x0, y0, x1, y1);
            }
        }
        else if (y0 < 0) {
            while (y1 < 0) {
                x1  = x1 * 0.9;
                y1 = f.value(p, x1, r*x1);
                //System.out.printf("p0=(%f, %f), p1(%f, %f)\n", x0, y0, x1, y1);
            }
        }
        else {
            return x0;
        }

        //System.out.printf("p0=(%f, %f), p1(%f, %f) - interval\n", x0, y0, x1, y1);

        // reduce the interval
        double dx = x0 - x1;
        double dy = y0 - y1;

        int loop = 0;
        while (Math.abs(dx) > 1.0e-6) {

            //System.out.printf("p0=(%f, %f), p1(%f, %f) - reduce\n", x0, y0, x1, y1);

            //if (loop++ > 10) break;

            double x = x0 - y0 * dx / dy;
            double y = f.value(p, x, r*x);

            if(y > 0) {
                if (y0 > 0) {
                    x0 = x;
                    y0 = y;
                }
                else {
                    x1 = x;
                    y1 = y;
                }
            }
            else if (y < 0) {
                if (y0 < 0) {
                    x0 = x;
                    y0 = y;
                }
                else {
                    x1 = x;
                    y1 = y;
                }
            }
            else {
                return x;
            }

            dx = x0 - x1;
            dy = y0 - y1;

        }

        //System.out.printf("p0=(%f, %f), p1(%f, %f) - final\n", x0, y0, x1, y1);
        return 0.5 * (x0 + x1);
    }

    static public void main(String args[]) {

        System.out.printf("a\tb\tRamanujan-I\tRamanujan-II\tZafary\ta\ta1\ta2\ta3\n");

        function f1 = new ramanujan_1();
        function f2 = new ramanujan_2();
        function f3 = new zafary();

        for (int a=1; a <=5; ++a) {
            for (double r=0; r<=2.0; r+=0.1) {

                double b = r * a;
                
                double p1 = perimeter_Ramanujan_1(a, b);
                double p2 = perimeter_Ramanujan_2(a, b);
                double p3 = perimeter_Zafary(a, b);

                System.out.printf("%d\t%f\t%f\t%f\t%f\t%d\t%f\t%f\t%f\n",
                    a, b,
                    p1, p2, p3,
                    a,
                    majorAxis(p1, r, f1),
                    majorAxis(p2, r, f2),
                    majorAxis(p3, r, f3)
                    );
            }
        }
        
    }
}
