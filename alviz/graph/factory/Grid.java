/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.graph.factory;

import alviz.base.graph.BaseGraph;
import alviz.graph.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author baskaran
 */
public class Grid {

    // disable instantiation of graph factory
    private Grid() {}

    static private int rows;
    static private int cols;
    static private int nodesCreated;

    static private void computeGridDimension(int width, int height, int nodeCountGiven) {

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

    static public BaseGraph create(BaseGraph g, int width, int height, int nodeCountGiven, GraphShape shape) {

        computeGridDimension(width, height, nodeCountGiven);
        
        Object nodes[][] = new Object[cols][rows];

        int dx = width/(cols+1);
        int dy = height/(rows+1);

        int dx4= dx/4;
        int dy4= dy/4;

        int x0 = (width  - dx * (cols-1))/2;
        int y0 = (height - dy * (rows-1))/2;

        for (int y=y0, iy=0, xsign=1; iy<rows; ++iy, xsign*=-1) {
            int x = x0;
            switch(shape){ case GRID_2: case GRID_3: case GRID_4: x += xsign * dx4; }
            for (int ix=0,ysign=1; ix<cols; ++ix, ysign*=-1) {
                int y1 = y;
                switch(shape){ case GRID_2: case GRID_4: y1 += ysign* dy4; }
                nodes[ix][iy] = g.createNode(x, y1);
                x += dx;
            }
            y += dy;
        }

        for (int iy=0, xsign=1; iy<rows; ++iy, xsign *=-1) {
            for (int ix=0; ix<cols; ++ix) {
                if (ix < (cols-1)) {
                    g.createEdge((BaseGraph.Node)nodes[ix][iy], (BaseGraph.Node)nodes[ix+1][iy]);
                }
                if (iy < (rows-1)) {
                    g.createEdge((BaseGraph.Node)nodes[ix][iy], (BaseGraph.Node)nodes[ix][iy+1]);
                }
                switch(shape){ case GRID_3: case GRID_4:
                    if (ix < (cols-1) && iy < (rows-1)) {
                        if (xsign > 0)
                            g.createEdge((BaseGraph.Node)nodes[ix][iy], (BaseGraph.Node)nodes[ix+1][iy+1]);
                        else
                            g.createEdge((BaseGraph.Node)nodes[ix+1][iy], (BaseGraph.Node)nodes[ix][iy+1]);
                    }
                }
            }
        }

        return g;
    }

    static public BaseGraph create(BaseGraph g, int cols, int rows, int width, int height, GraphShape shape) {

        Object nodes[][] = new Object[cols][rows];

        int dx = width/(cols+1);
        int dy = height/(rows+1);

        int dx4= dx/4;
        int dy4= dy/4;

        int x0 = (width  - dx * (cols-1))/2;
        int y0 = (height - dy * (rows-1))/2;

        for (int y=y0, iy=0, xsign=1; iy<rows; ++iy, xsign*=-1) {
            int x = x0;
            switch(shape){ case GRID_2: case GRID_3: case GRID_4: x += xsign * dx4; }
            for (int ix=0,ysign=1; ix<cols; ++ix, ysign*=-1) {
                int y1 = y;
                switch(shape){ case GRID_2: case GRID_4: y1 += ysign* dy4; }
                nodes[ix][iy] = g.createNode(x, y1);
                x += dx;
            }
            y += dy;
        }

        for (int iy=0, xsign=1; iy<rows; ++iy, xsign *=-1) {
            for (int ix=0; ix<cols; ++ix) {
                if (ix < (cols-1)) {
                    g.createEdge((BaseGraph.Node)nodes[ix][iy], (BaseGraph.Node)nodes[ix+1][iy]);
                }
                if (iy < (rows-1)) {
                    g.createEdge((BaseGraph.Node)nodes[ix][iy], (BaseGraph.Node)nodes[ix][iy+1]);
                }
                switch(shape){ case GRID_3: case GRID_4:
                    if (ix < (cols-1) && iy < (rows-1)) {
                        if (xsign > 0)
                            g.createEdge((BaseGraph.Node)nodes[ix][iy], (BaseGraph.Node)nodes[ix+1][iy+1]);
                        else
                            g.createEdge((BaseGraph.Node)nodes[ix+1][iy], (BaseGraph.Node)nodes[ix][iy+1]);
                    }
                }
            }
        }

        return g;
    }

    static public Graph createWithClockWiseEdgeOrder(int cols, int rows, int width, int height) {
        Graph g = new Graph();

        Graph.Node nodes[][] = new Graph.Node[rows][cols];
        Queue<Graph.Edge> topQueue = new LinkedList<Graph.Edge>();
        Queue<Graph.Edge> leftQueue = new LinkedList<Graph.Edge>();

        int dx = width/(cols+1);
        int dy = height/(rows+1);

        int x0 = (width  - dx * (cols-1))/2;
        int y0 = (height - dy * (rows-1))/2;

        int y = y0;
        for (int iy=0; iy<rows; ++iy) {
            int x = x0;
            for (int ix=0; ix<cols; ++ix) {
                nodes[iy][ix] = g.createNode(x, y);
                x += dx;
            }
            y += dy;
        }

        for (int iy=0; iy<rows; ++iy) {
            for (int ix=0; ix<cols; ++ix) {
                if (iy > 0) {
                    Graph.Edge topedge = topQueue.poll();
                    nodes[iy][ix].addEdge(topedge);
                    nodes[iy][ix].addNeighbor(nodes[iy-1][ix]);
                }
                if (ix+1 < cols) {
                    Graph.Edge rightedge = g.makeEdge(nodes[iy][ix], nodes[iy][ix+1]);
                    leftQueue.add(rightedge);
                    nodes[iy][ix].addEdge(rightedge);
                    nodes[iy][ix].addNeighbor(nodes[iy][ix+1]);
                }
                if (iy+1 < rows) {
                    Graph.Edge bottomedge = g.makeEdge(nodes[iy][ix], nodes[iy+1][ix]);
                    topQueue.add(bottomedge);
                    nodes[iy][ix].addEdge(bottomedge);
                    nodes[iy][ix].addNeighbor(nodes[iy+1][ix]);
                }
                if (ix > 0) {
                    Graph.Edge leftedge = leftQueue.poll();
                    nodes[iy][ix].addEdge(leftedge);
                    nodes[iy][ix].addNeighbor(nodes[iy][ix-1]);
                }

            }
        }

        return g;
    }

}
