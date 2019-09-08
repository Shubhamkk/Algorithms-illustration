/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.graph.factory;

import alviz.base.graph.BaseGraph;
import alviz.graph.factory.MSTPrim.Vertex;
import java.util.List;

/**
 *
 * @author baskaran
 */
public class GridMST {

    private BaseGraph graph;
    private int rows;
    private int cols;
    private int width;
    private int height;
    private boolean debug;
    private Vertex vList[];
    private int nodesCreated;
    private GraphShape shape;

    private void computeGridDimension(int width, int height, int nodeCountGiven) {

        double r = ((double) width)/((double) height);
        double w = Math.sqrt(nodeCountGiven*r);
        double h = Math.sqrt(nodeCountGiven/r);

        if ( width > height) {
            cols = (int) w; //Math.floor(w);
            rows = (int) h; //Math.ceil(h);
        }
        else {
            cols = (int) w; //Math.ceil(w);
            rows = (int) h; //Math.floor(h);
        }

        nodesCreated = rows * cols;
    }

    public GridMST(BaseGraph graph, int width, int height, int nodeCountGiven, GraphShape shape, boolean debug) {
        this.graph = graph;

        this.width = width;
        this.height = height;

        computeGridDimension(width, height, nodeCountGiven);

        this.shape = shape;
        this.debug = debug;

        vList = new Vertex[nodesCreated];
    }
    public GridMST(BaseGraph graph, int cols, int rows, int width, int height, GraphShape shape, boolean debug) {
        this.graph = graph;
        this.rows = rows;
        this.cols = cols;
        this.nodesCreated = rows * cols;

        this.width = width;
        this.height = height;

        this.shape = shape;
        this.debug = debug;
        
        vList = new Vertex[nodesCreated];
    }

    public BaseGraph create() {
        generateVertex();
        buildMST(true);
        return buildGraph();
    }

    public void generateVertex() {
        int dx = width/(cols+1);
        int dy = height/(rows+1);

        int dx4= dx/4;
        int dy4= dy/4;

        int x0 = (width  - dx * (cols-1))/2;
        int y0 = (height - dy * (rows-1))/2;

        int n=0;
        for (int y=y0, iy=0, xsign=1; iy<rows; ++iy, xsign*=-1) {
            int x = x0;
            switch(shape){ case GRID_2_MST: case GRID_3_MST: case GRID_4_MST: x += xsign * dx4; }
            for (int ix=0,ysign=1; ix<cols; ++ix, ysign*=-1, ++n) {
                int y1 = y;
                switch(shape){ case GRID_2_MST: case GRID_4_MST: y1 += ysign* dy4; }
                vList[n] = new Vertex(n, x, y1);
                x += dx;
            }
            y += dy;
        }

//        int dx = width/(rows+1);
//        int dy = height/(cols+1);
//
//        int dx4= dx/4;
//        int dy4= dy/4;
//
//        int x0 = (width  - dx * (rows-1))/2;
//        int y0 = (height - dy * (cols-1))/2;
//
//        int n=0;
//        int y = y0;
//        for (int iy=0; iy<cols; ++iy) {
//            int x = x0;
//            for (int ix=0; ix<rows; ++ix, ++n) {
//                vList[n] = new Vertex(n, x, y);
//                x += dx;
//            }
//            y += dy;
//        }
    }

    public void buildMST(boolean fullyConnected) {
        MSTPrim mst = new MSTPrim(vList, debug);
        if (fullyConnected) {
            mst.run2();
        }
        else {
            mst.run();
        }
        if (debug) mst.print();
    }

    private BaseGraph buildGraph() {

        // create graph
        BaseGraph g = (BaseGraph) graph.createGraph(nodesCreated);

        // create nodes
        for (int i=0; i<nodesCreated; ++i) {
            Vertex v = vList[i];
            g.createNode(v.x, v.y);
        }

        // create edges
        List<BaseGraph.Node> nodes = g.getNodes();
        for (int i=0; i<nodesCreated; ++i) {
            Vertex v = vList[i];
            int parent = v.parent;
            if (parent >= 0) {
                BaseGraph.Node vn = nodes.get(v.id);
                BaseGraph.Node un = nodes.get(parent);
                g.createEdge(vn, un);
            }
        }

        return g;
    }

}
