/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.graph;

import alviz.base.graph.BaseGraph;

/**
 *
 * @author baskaran
 */
public class Graph extends BaseGraph<Graph.Node, Graph.Edge> {

    public Graph() {
        super();
    }
    public Graph(int nodeCount) {
        super(nodeCount);
    }

    static public Graph createGraph() {
        return new Graph();
    }
    public Graph createGraph(int nodeCount) {
        return new Graph(nodeCount);
    }
    public Node createNode(int x, int y) {
        Node n = new Node(x, y);
        super.createNode(n);
        return n;
    }
    public Edge createEdge(Node n1, Node n2) {
        Edge e = new Edge(n1, n2);
        super.createEdge(e);
        return e;
    }
    
    public class Node extends BaseGraph<Graph.Node, Graph.Edge>.Node {

        public Node (int x, int y) {
            super(x,y);
        }
    }

    public class Edge extends BaseGraph<Graph.Node, Graph.Edge>.Edge {

        public Edge(Node n1, Node n2) {
            super(n1, n2);
        }
    }

}
