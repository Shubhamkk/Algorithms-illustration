/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.graph.factory;

import alviz.base.graph.BaseGraph;
import alviz.graph.Graph;
import alviz.util.Ellipse;
import java.util.Random;

/**
 *
 * @author baskaran
 */
public class Tree2D {

    private BaseGraph graph;

    private int screenClearance=10;

    private boolean debug;
    private int branchingFactor;
    private int width;
    private int height;
    private int nodeWidth;

    private int effWidth;
    private int effHeight;

    private int leafsAllowed;
    private int nodeClearance;

    private double majorAxis, minorAxis;
    private double xOffset, yOffset;

    private int nodeDim;

    private double delta_a;
    private double delta_b;

    private tree root;
    private int levels;

    public Tree2D(BaseGraph graph, int branchingfactor, int width, int height, int nodewidth, int nodeclearance, boolean debug) {
        //width  = 300;
        //height = 300;

        this.graph = graph;
        this.debug = debug;
        this.branchingFactor = branchingfactor;
        this.width = width;
        this.height = height;
        this.nodeWidth = nodewidth;

        this.nodeClearance = nodeclearance;

        this.effWidth = width - 2*screenClearance;
        this.effHeight = height - 2*screenClearance;

        // center of the screen
        this.majorAxis = effWidth/2;
        this.minorAxis = effHeight/2;

        this.xOffset = width/2;
        this.yOffset = height/2;
        
        this.nodeDim = nodeWidth + nodeClearance;
        this.leafsAllowed = (int) (Ellipse.perimeter(majorAxis, minorAxis) / nodeDim);

        this.delta_a = majorAxis/Math.log(leafsAllowed);
        this.delta_b = minorAxis/Math.log(leafsAllowed);

        this.root = null;
        this.levels = 0;
    }
    private void print() {
        System.out.printf("branchingFactor = %d\n", branchingFactor);
        System.out.printf("width = %d\n", width);
        System.out.printf("height = %d\n", height);
        System.out.printf("nodeWidth = %d\n", nodeWidth);
        System.out.printf("leafsAllowed = %d\n", leafsAllowed);
        System.out.printf("nodeClearance = %d\n", nodeClearance);
        System.out.printf("nodeDim = %d\n", nodeDim);
        System.out.printf("xOffset = %f\n", xOffset);
        System.out.printf("yOffset = %f\n", yOffset);
        System.out.printf("majorAxis = %f\n", majorAxis);
        System.out.printf("minorAxis = %f\n", minorAxis);
    }

    public BaseGraph create() {
        //print();
        
        root = maketree();
        BaseGraph g = buildGraph(root);
        tree.clear(root);

        //System.out.printf("graph nodes = %d, edges=%d\n", g.getNodeCount(), g.getEdgeCount());

        return g;
    }

    private tree maketree() {

        tree[] htree = new tree[leafsAllowed];
        {
            double radius = (majorAxis < minorAxis ? majorAxis : minorAxis);
            double dt = 2 * Math.PI / leafsAllowed;
            double theta = 0;

            tree hprev = null;
            for (int i=0; i<leafsAllowed; ++i) {
                
                double x = radius * Math.cos(theta);
                double y = radius * Math.sin(theta);
                computeParentCoordinate(x, y, x, y, majorAxis, minorAxis);

                tree t = new tree(0);
                t.setPoint(tmp_parent_x, tmp_parent_y);
                htree[i] = t;
                if (hprev != null) hprev.hnext = t;
                hprev = t;

                theta += dt;

                //System.out.printf("%d, (%f, %f)\n", i, t.x, t.y);

            }

            //System.out.printf("level=%d, a=%f, b=%f, nodes=%d\n", levels, majorAxis, minorAxis, leafsAllowed);
        }

        
        Random rand = new Random(System.nanoTime());
        int n = leafsAllowed;

        double prev_a = majorAxis;
        double prev_b = minorAxis;

        // do until root is reached...
        // i.e. number of nodes in the level is 1
        while (n > 1) {
            ++levels;

            int levelnodes = 0;
            int base=0;
            tree t=null, hprev=null;
            while (n > 0) {
                // fetch no of children
                int nchild = rand.nextInt(branchingFactor) + 1;
                if (nchild > n) nchild = n;

                // create parent and add children
                t = new tree(htree, base, nchild);
                htree[levelnodes] = t;
                base += nchild;
                n -= nchild;
                ++levelnodes;

                // assign level pointer and maintain prev tree
                if (hprev != null) hprev.hnext = t;
                hprev = t;
            }

            double perimeter = levelnodes * nodeDim;
            double axisratio = minorAxis/majorAxis;
            double a = Ellipse.majorAxis(perimeter, axisratio);
            double b = axisratio * a;

            // reestimate the ellipse size based on nodes at the current level...
            this.delta_a = prev_a/Math.log(levelnodes)/2;
            this.delta_b = prev_b/Math.log(levelnodes)/2;

            // apply correction
            if (prev_a - a > delta_a) {
                a = prev_a - delta_a;
                b = prev_b - delta_b;
            }

            //System.out.printf("level=%d, a=%f, b=%f, nodes=%d\n", levels, a, b, levelnodes);

            // assign coordinates for the current level
            for (int i=0; i<levelnodes; ++i) {
                tree u = htree[i];
                tree c0, c1;
                int childCount = u.children.length;
                if (childCount%2 == 0) {
                    int k = childCount>>1;
                    c0 = u.children[k-1];
                    c1 = u.children[k];
                }
                else {
                    int k = childCount>>1;
                    c0 = u.children[k];
                    c1 = u.children[k];
                }
                computeParentCoordinate(c0.x, c0.y, c1.x, c1.y, a, b);
                u.setPoint(tmp_parent_x, tmp_parent_y);

                //System.out.printf("%d, (%f, %f)\n", i, u.x, u.y);
            }

            n = levelnodes;
            prev_a = a;
            prev_b = b;
            
            // collapse the level if the number of nodes is less than or equal to the branching factor...
            // this level will be the root level
            if (levelnodes <= branchingFactor) {
                ++levels;
                // create parent and add children
                int nchild = levelnodes;
                levelnodes = 0;

                t = new tree(htree, 0, nchild);
                t.setPoint(0, 0);
                htree[levelnodes] = t;
                base += nchild;
                n -= nchild;
                ++levelnodes;
            }

            n = levelnodes;
        }

        return htree[0];
    }

    private BaseGraph buildGraph(tree t) {

        BaseGraph g = graph;
        int level=0;

        BaseGraph.Node n = g.createNode( (int)(t.x + xOffset), (int)(t.y + yOffset));
        t.gnode = n;

        g.setStartNode(n);
        buildGraph(g, t, level+1);
        return g;
    }

    private void buildGraph(BaseGraph g, tree t, int level) {
        if (t != null) {
            if (!t.isLeaf()) {
                for (int i=0; i<t.children.length; ++i) {
                    tree c = t.children[i];
                    BaseGraph.Node n = g.createNode((int)(c.x + xOffset), (int)(c.y + yOffset));
                    c.gnode = n;
                    g.createEdge(n, t.gnode);
                }
                for (int i=0; i<t.children.length; ++i) {
                    buildGraph(g, t.children[i], level+1);
                }
            }
            else {
                t.gnode.setLeaf();
            }
        }
    }

    private double tmp_parent_x;
    private double tmp_parent_y;

    private void computeParentCoordinate(double x1, double y1, double x2, double y2, double a, double b) {

        double x0 = 0;
        double y0 = 0;

        double xm = (x1 + x2)/2;
        double ym = (y1 + y2)/2;

        double y;
        double x;

        if (y0 == ym) {
            y = y0;
            x = (a/b) * Math.sqrt(b*b-y*y);
            tmp_parent_x = ((xm >= 0) ? x : -x);
            tmp_parent_y = ((ym >=0) ? y : -y);
        }
        else if (x0 == xm) {
            x = x0;
            y = (b/a) * Math.sqrt(a*a-x*x);
            tmp_parent_x = ((xm >= 0) ? x : -x);
            tmp_parent_y = ((ym >=0) ? y : -y);
        }
        else {
            double m = (y0 - ym) / (x0 - xm);
            double c = y0 - m * x0;

            double r2 = (a/b)*(a/b);
            double A = 1 + r2 * m * m;
            double B = 2 * r2 * m * c;
            double C = r2 * c * c - a * a;

            double det = Math.sqrt(B*B-4*A*C);
            double xroot1 = (-B + det) / (2 * A);
            double xroot2 = (-B - det) / (2 * A);

            double yroot1 = m * xroot1 + c;
            double yroot2 = m * xroot2 + c;

            if (xm >= 0) {
                if (xroot1 >= 0) {
                    tmp_parent_x = xroot1;
                    tmp_parent_y = yroot1;
                }
                else {
                    tmp_parent_x = xroot2;
                    tmp_parent_y = yroot2;
                }
            }
            else {
                if (xroot1 < 0) {
                    tmp_parent_x = xroot1;
                    tmp_parent_y = yroot1;
                }
                else {
                    tmp_parent_x = xroot2;
                    tmp_parent_y = yroot2;
                }
            }
            
        }
    }

    static private class tree {
        
        public double x;
        public double y;
        public int descendents;
        public tree[] children;
        public tree hnext;
        public BaseGraph.Node gnode=null;

        public tree(int n) {
            x = 0;
            y = 0;
            descendents = 0;
            if (n > 0){
                children = new tree[n];
            }
            else {
                children = null;
            }
            hnext = null;
        }

        public tree(tree[] list, int base, int n) {
            this(n);
            for (int i=0; i<n; ++i, ++base) {
                children[i] = list[base];
                descendents += children[i].descendents;
            }
        }
        static public void clear(tree t) {
            if (t != null) {
                if (t.children != null) {
                    for (int i=0; i<t.children.length; ++i) {
                        clear(t.children[i]);
                        t.children[i] = null;
                    }
                    t.children = null;
                }
                t.hnext = null;
                t.gnode = null;
            }
        }

        public boolean isLeaf() { return children == null; }
        public void setPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public tree getFirstChild() {
            if (children != null) {
                return children[0];
            }
            return null;
        }

        public tree getLastChild() {
            if (children != null) {
                return children[children.length-1];
            }
            return null;
        }
    }
    
}
