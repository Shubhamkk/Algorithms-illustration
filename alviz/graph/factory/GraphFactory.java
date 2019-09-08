/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.graph.factory;

import alviz.base.graph.GraphClass;
import alviz.base.graph.BaseGraph;
import alviz.graph.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author baskaran
 */
public class GraphFactory {

    // disable instantiation of graph factory
    private GraphFactory() {}

    static public BaseGraph createGraph(GraphClass gc) {
        switch (gc) {
            case GRAPH: return Graph.createGraph();
            default: return null;
        }
    }
    
    static public BaseGraph createGraph(GraphClass gc, BaseGraph source) {

        Map<BaseGraph.Node, BaseGraph.Node> nodeMap = new HashMap<BaseGraph.Node, BaseGraph.Node>();
        BaseGraph g = createGraph(gc);

        for (Object x : source.getNodes()) {
            BaseGraph.Node s = (BaseGraph.Node) x;
            BaseGraph.Node gnode = g.createNode(s.x, s.y);
            gnode.copyData(s);
            nodeMap.put(s, gnode);
        }

        for (Object x : source.getEdges()) {
            BaseGraph.Edge s = (BaseGraph.Edge) x;
            BaseGraph.Edge gedge = g.createEdge(nodeMap.get(s.node_1), nodeMap.get(s.node_2));
            gedge.copyData(s);
        }

        g.setStartNode(nodeMap.get(source.getStartNode()));

        if (source.getGoalNodes() != null) {
            List<BaseGraph.Node> nl = new LinkedList<BaseGraph.Node>();
            for (Object x : source.getGoalNodes()) {
                BaseGraph.Node s = (BaseGraph.Node) x;
                BaseGraph.Node gnode = nodeMap.get(s);
                nl.add(gnode);
            }
            g.setGoalNode(nl);
        }

//        System.out.println("createGraph> from  = " + source.getClass().getName());
//        System.out.println("createGraph> to    = " + g.getClass().getName());
//        System.out.println("createGraph> nodes = " + g.getNodeCount());
//        System.out.println("createGraph> edges = " + g.getEdgeCount());

        return g;
    }

    static public BaseGraph createTree(BaseGraph g, int branchingFactor, int width, int height, int nodeWidth, int nodeclearance, boolean debug) {
        return new Tree(g, branchingFactor, width, height, nodeWidth, nodeclearance, debug).create();
    }

    static public BaseGraph createTree2D(BaseGraph g, int branchingFactor, int width, int height, int nodeWidth, int nodeclearance, boolean debug) {
        return new Tree2D(g, branchingFactor, width, height, nodeWidth, nodeclearance, debug).create();
    }

    static public BaseGraph createTree2DRect(BaseGraph g, int branchingFactor, int width, int height, int nodeWidth, int nodeclearance, boolean debug) {
        return new Tree2DRectangle(g, branchingFactor, width, height, nodeWidth, nodeclearance, debug).create();
    }

    static public BaseGraph createGrid(BaseGraph g, int width, int height, int nodeCountGiven, GraphShape shape) {
        return Grid.create(g, width, height, nodeCountGiven, shape);
    }

    static public BaseGraph createGridMST(BaseGraph g, int width, int height, int nodeCountGiven, GraphShape shape, boolean debug) {
        return new GridMST(g, width, height, nodeCountGiven, shape, debug).create();
    }

    static public BaseGraph createRandomMST(BaseGraph g, int nodes, int width, int height, int nodeWidth, boolean debug) {
        return new RandomGraph(g, nodes, width, height, nodeWidth, debug).createMST(true);
    }

    static public BaseGraph createRandomGraph(BaseGraph g, int nodes, int width, int height, int nodeWidth, boolean debug) {
        return new RandomGraph(g, nodes, width, height, nodeWidth, debug).createGraph(true, false);
    }

    static public BaseGraph createRandomGraphTriangulate(BaseGraph g, int nodes, int width, int height, int nodeWidth, boolean debug) {
        return new RandomGraph(g, nodes, width, height, nodeWidth, debug).createGraph(true, true);
    }
    
    static public BaseGraph createRandomcompleteGraph(BaseGraph g, int nodes, int width, int height, int nodeWidth, boolean debug) {
         return new RandomGraph(g, nodes, width, height, nodeWidth, debug).createcompleteGraph(true, false);
     }

}
