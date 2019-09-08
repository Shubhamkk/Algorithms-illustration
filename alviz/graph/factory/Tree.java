/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.graph.factory;

import alviz.base.graph.BaseGraph;
import alviz.graph.Graph;
import java.util.Random;

/**
 *
 * @author baskaran
 */
public class Tree {

    private BaseGraph graph;

    private int screenClearance=10;

    private boolean debug;
    private int branchingFactor;
    private int width;
    private int height;
    private int nodeWidth;

    private int leafsAllowed;
    private int nodeClearance;
    private int dx, dx0;
    private int dy, dy0;

    private tree root;
    private int levels;

    public Tree(BaseGraph graph, int branchingfactor, int width, int height, int nodewidth, int nodeclearance, boolean debug) {
        this.graph = graph;
        this.debug = debug;
        this.branchingFactor = branchingfactor;
        this.width = width;
        this.height = height;
        this.nodeWidth = nodewidth;

        this.nodeClearance = nodeclearance;
        
        this.dx = nodeWidth + nodeClearance;
        this.leafsAllowed = (width - 2*screenClearance) / dx;
        this.dy = (height - 2*screenClearance)/(int)Math.log(leafsAllowed);

        this.dx0 = screenClearance;
        this.dy0 = height - screenClearance;

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
        System.out.printf("dx = %d\n", dx);
        System.out.printf("dy = %d\n", dy);
        System.out.printf("dx0 = %d\n", dx0);
        System.out.printf("dy0 = %d\n", dy0);
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

        int x = dx0;
        int y = dy0;

        tree[] htree = new tree[leafsAllowed];
        {
            tree hprev = null;
            for (int i=0; i<leafsAllowed; ++i) {
                tree t = new tree(0);
                t.setPoint(x, y);
                htree[i] = t;
                if (hprev != null) hprev.hnext = t;
                hprev = t;

                x += dx;
            }
        }

        
        Random rand = new Random(System.nanoTime());
        int n = leafsAllowed;

        // do until root is reached...
        // i.e. number of nodes in the level is 1
        while (n > 1) {
            ++levels;
            y -= dy;

            int levelnodes = 0;
            int base=0;
            tree t=null, hprev=null;
            while (n > 0) {
                // fetch no of children
                int nchild = rand.nextInt(branchingFactor) + 1;
                if (nchild > n) nchild = n;

                // create parent and add children
                t = new tree(htree, base, nchild, y);
                htree[levelnodes] = t;
                base += nchild;
                n -= nchild;
                ++levelnodes;

                // assign level pointer and maintain prev tree
                if (hprev != null) hprev.hnext = t;
                hprev = t;
            }

            n = levelnodes;
        }

        return htree[0];
    }

    private BaseGraph buildGraph(tree t) {
        dy = (height - 2*screenClearance)/(levels);
        dy0 = screenClearance;

        BaseGraph g = graph;
        int level=0;
        BaseGraph.Node n = g.createNode(t.x, dy0 + level*dy);
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
                    BaseGraph.Node n = g.createNode(c.x, dy0 + level*dy);
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

    static private class tree {
        
        public int x;
        public int y;
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

        public tree(tree[] list, int base, int n, int y) {
            this(n);
            for (int i=0; i<n; ++i, ++base) {
                children[i] = list[base];
                descendents += children[i].descendents;
            }
            
            tree c1 = children[0];
            tree cn = children[n-1];
            setPoint((c1.x + cn.x)/2, y);
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
        public void setPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }
    /* The team implementing game trees needs to add random values to the leaf nodes
     * in the tree
     */
}
