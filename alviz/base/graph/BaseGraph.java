/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.base.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author baskaran
 */
public abstract class BaseGraph <tNode extends BaseGraph.Node, tEdge extends BaseGraph.Edge> {

    public List<tNode> moveGen(tNode n) { return n.getChildren(); }
    public boolean goalTest(tNode n) { return n.isGoalKind(); }

//    static public double euclideanDistance(Node u, Node v) {
//        int dx = u.x - v.x;
//        int dy = u.y - v.y;
//        return Math.sqrt(dx*dx+dy*dy);
//    }

    static public double euclideanDistance(int ux, int uy, int vx, int vy) {
        int dx = ux - vx;
        int dy = uy - vy;
        return Math.sqrt(dx*dx+dy*dy);
    }

    private tNode startNode;
    private List<tNode> goalNodes;

    private boolean directedGraph=false;

    public tNode getStartNode() { return startNode; }
    public List<tNode> getGoalNodes() { return goalNodes; }
    public double getHuristic(tNode start){
        tNode n=goalNodes.get(0);
        int ux=start.x;
        int uy=start.y;
        int vx=n.x;
        int vy=n.y;
        double h=euclideanDistance(ux,uy,vx,vy);
        return h;
    }
    public double getCost(tNode node_1, tNode node_2){
        int ux=node_1.x;
        int uy=node_1.y;
        int vx=node_2.x;
        int vy=node_2.y; 
        double c=euclideanDistance(ux,uy,vx,vy);
        return c;
    }
    public void setStartNode(tNode node) {
        if (node != null) {
            resetStartNode();
            startNode = node;
            startNode.setStartKind();
        }
    }
    public void resetStartNode(){
        if (startNode != null) {
            startNode.setDefaultKind();
            startNode = null;
        }
    }
    public void setGoalNode(List<tNode> nodes) {
        if (nodes != null) {
            resetGoalNodes();
            goalNodes = nodes;
            for (tNode n : goalNodes) {
                n.setGoalKind();
            }
        }
    }
    public void resetGoalNodes(){
        if (goalNodes != null) {
            for (tNode g : goalNodes) {
                g.setDefaultKind();
            }
            goalNodes = null;
        }
    }

    public tNode getNode(int id) {
        return nodes.get(id);
    }

    private List<tNode> nodes;
    private List<tEdge> edges;

    public List<tNode> getNodes() { return nodes; }
    public List<tEdge> getEdges() { return edges; }

    private List<tEdge> backPointerEdges;
    public List<tEdge> getBackPointerEdges() { return backPointerEdges; }

    private List<tEdge> savedEdges;
    public List<tEdge> getSavedEdges() { return savedEdges; }
    public void saveEdge() {
        if (savedEdges != null) {
            edges.addAll(savedEdges);
        }
        savedEdges = edges;
        edges = null;
    }

    protected BaseGraph() {
        nodes = null;
        edges = null;
        startNode = null;
        goalNodes = null;
    }

    protected BaseGraph(int nodeCount) {
        this();
        nodes = new ArrayList<tNode>(nodeCount);
    }

    public int getNodeCount() {
        if (nodes != null) {
            return nodes.size();
        }
        return 0;
    }

    public int getEdgeCount() {
        if (edges != null) {
            return edges.size();
        }
        return 0;
    }

    public void resetGraph() {
        resetNodes();
        resetEdges();
    }
    
    public void destroyEdges(){
        if(edges == null)
            return;
        Vector<tEdge> edgesCopy = new Vector<tEdge>();
        edgesCopy.addAll(edges);
        for(int i = 0;i < edgesCopy.size();i++)
            destroyEdge(edgesCopy.get(i));
    }
    
    public void destroyEdge(tEdge edge)
    {
        Node node_1 = edge.node_1;
        Node node_2 = edge.node_2;
        node_1.delEdge(edge);
        node_2.delEdge(edge);
        node_1.delNeighbor((tNode) node_2);
        node_2.delNeighbor((tNode) node_1);
        breakEdge(edge);
    }
    
    private void resetNodes() {
        if (nodes != null) {
            for (tNode n : nodes) {
                n.setDefault();
                n.reset_mg_attributes();
            }
        }
    }
    private void resetEdges() {
        if (edges != null) {
            for (tEdge e : edges) {
                e.setDefault();
            }
        }
    }

    public void setGraphToOld(){
        if (nodes != null) {
            for (tNode n : nodes) {
                n.setOld();
                n.setOldPath();
                n.reset_mg_attributes();
            }
        }

        if (edges != null) {
            for (tEdge e : edges) {
                e.setOld();
                e.setOldPath();
            }
        }
    }

    public void addNode(tNode n) {
        // lazy initialization of node list
        if (nodes == null) nodes = new LinkedList<tNode>();
        nodes.add(n);
    }
    
    public void addBackPointerEdge(tEdge e) {
        if (backPointerEdges == null) backPointerEdges = new LinkedList<tEdge>();
        backPointerEdges.add(e);
    }
    public void addEdge(tEdge e) {
        // lazy initialization
        if (edges == null) edges = new LinkedList<tEdge>();

        e.node_1.addEdge(e);
        e.node_2.addEdge(e);

        e.node_1.addNeighbor(e.node_2);
        e.node_2.addNeighbor(e.node_1);

        edges.add(e);
    }
    
    public tEdge insertEdge(tEdge e) {
        // lazy initialization
        if (edges == null) edges = new LinkedList<tEdge>();
        edges.add(e);
        return e;
    }
    
    public void breakEdge(tEdge edge)
    {
        if(edges == null)
            return;
        if(edges.contains(edge))
            edges.remove(edge);
    }
    
    static public Object createGraph() { return null; } // { return new BaseGraph<BaseGraph.Node, BaseGraph.Edge>(); }
    abstract public Object createGraph(int nodeCount); // { return new BaseGraph<BaseGraph.Node, BaseGraph.Edge>(); }
    abstract public tNode createNode(int x, int y);
    abstract public tEdge createEdge(tNode n1, tNode n2);

    protected void createNode(tNode n) {
        // lazy initialization of node list
        if (nodes == null) nodes = new LinkedList<tNode>();
        nodes.add(n);
    }
    protected void createEdge(tEdge e) {
        // lazy initialization
        if (edges == null) edges = new LinkedList<tEdge>();

        e.node_1.addEdge(e);
        e.node_2.addEdge(e);

        e.node_1.addNeighbor(e.node_2);
        e.node_2.addNeighbor(e.node_1);

        edges.add(e);
    }
    public tEdge makeEdge(tNode node_1, tNode node_2) {
        tEdge e = createEdge(node_1, node_2);
        insertEdge(e);
        return e;
    }

    public void openNode(tNode n, tNode parent) {
       tEdge mgedge = getEdge(n, parent);
        n.mgParent = parent;
        n.mgEdge = (tEdge) mgedge;

        n.setOpen();
        if (mgedge != null) mgedge.setOpen();
    }
    public void closeNode(tNode n) {
        n.setClosed();
        if (n.mgEdge != null) n.mgEdge.setClosed();
    }

    public void deleteNode(tNode n) {
        n.setDefault();
        n.mgParent = null;
        n.mgEdge = null;
    }

    public tEdge getEdge(tNode n1, tNode n2) {
        if (n1 == null || n2 == null) return null;
        // search the edge list of n1
        for (tEdge x : (List<tEdge>) n1.getEdges())
            if (x.isEdgeOn(n1, n2)) return x;

        // else search the edge list of n2
        // may not be needed for the current undirected graph implementation...
        // needed only for finding bugs in the edge assignment...
        // for (Edge x : n2.edges) if (x.isEdgeOn(n1, n2)) return x;

        return null;
    }

    private int[] tmp_abc=new int[3];
    public boolean edgeCrossing(int line[]) {
        if (edges != null) {
            computeLineParameters(tmp_abc, line[0], line[1], line[2], line[3]);
            for (Edge e : edges) {
                if (edgeCross(tmp_abc, line, e)) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean edgeCross(int[] abc, int[] line, Edge e) {
        int dc1 = dC(abc, e.node_1.x, e.node_1.y);
        int dc2 = dC(abc, e.node_2.x, e.node_2.y);

        if (dc1 == 0) {
            if (dc2 == 0) return true;
            else return false;
        }
        else if (dc2 == 0) return false;
        else if (dc1 > 0 && dc2 > 0) return false;
        else if (dc1 < 0 && dc2 < 0) return false;

        dc1 = dC(e.abc, line[0], line[1]);
        dc2 = dC(e.abc, line[2], line[3]);

        if (dc1 == 0) {
            if (dc2 == 0) return true;
            else return false;
        }
        else if (dc2 == 0) return false;
        else if (dc1 > 0 && dc2 > 0) return false;
        else if (dc1 < 0 && dc2 < 0) return false;

        return true;
    }

    private int dC(int[] abc, int x, int y) {
        return abc[0] * x + abc[1] * y - abc[2];
    }

    public enum NodeKind {
        DEFAULT,
        START,
        GOAL
        /*add algorithm dependent NodeKind here, as per requirement...for example*/
        
        //MAX,
        //MIN,
        ;
    }
    

    public enum RuntimeState {

        /* Needs to be same for all groups*/
        DEFAULT(Color.LIGHT_GRAY, null),
        OPEN(Color.GREEN, new BasicStroke(2f)),
        CLOSED(Color.RED, new BasicStroke(2f)),
        BOUNDARY(Color.RED, new BasicStroke(2f)),
        PATH(Color.BLUE, new BasicStroke(4f)),
        START(Color.BLUE , null),
        GOAL(Color.BLUE, null),
        
        /*agorithm specific... can use below ones or add more*/
        
        HIGH(Color.BLACK, new BasicStroke(2f)),
        MEDIUM(Color.CYAN, new BasicStroke(2f)),
        L0(Color.white, new BasicStroke(2f)),
        L1(Color.BLUE, new BasicStroke(2f)),
        L2(Color.CYAN, new BasicStroke(2f)),
        L3(Color.darkGray, new BasicStroke(2f)),
        L4(Color.GREEN, new BasicStroke(2f)),
        L5(Color.MAGENTA, new BasicStroke(3f)),
        L6(Color.ORANGE, new BasicStroke(3f)),
        L7(Color.PINK, new BasicStroke(3f)),
        L8(Color.YELLOW, new BasicStroke(1f)),
        L9(Color.YELLOW, new BasicStroke(3f)),
        L10(Color.yellow, new BasicStroke(5f)),

        RELAY(Color.MAGENTA, null), // modify if required
        WAITING(Color.MAGENTA, null), // modify if required

        // old open, closed, rolled-back( reset to default)
        OLD(Color.DARK_GRAY, new BasicStroke(2f)),
        OLD_PATH(Color.BLACK, new BasicStroke(4f)),

        BACKPOINTER_EDGE(Color.BLACK, null), // modify if required
        
        // Can use if required
        
        //ALPHA_CUT_EDGE(Color.DARK_GRAY, new BasicStroke(2f, BasicStroke.CAP_BUTT , BasicStroke.JOIN_MITER, 1f, new float[] {10.0F, 3.0F, 3.0F, 3.0F},0)), //G1_alphaDashes , 0)),
        //BETA_CUT_EDGE(Color.GRAY, new BasicStroke(2f, BasicStroke.CAP_BUTT , BasicStroke.JOIN_MITER, 1f, new float[] {2.0F, 3.0F, 2.0F, 4.0F},0)); //G1_betaDashes , 0)); // modify it to purple and dashed line
        ;
        
        final public Color color;
        final public Stroke stroke;

        private RuntimeState(Color color, Stroke stroke) {
            this.color = color;
            this.stroke = stroke;
        }
        
        public boolean G7_isNotPrint() {
        	return this == L0;
        }
    }

    static private int uidMaxNode=0;
    static private int getNextNodeId() { return uidMaxNode++; }

    abstract public class Node {

        public void setLeaf() { }
        public boolean isLeaf() { return false; }

        // begin node data...
        public int x;
        public int y;
        // end node data

        private int id;

        public int getId() { return id; }

        protected String idStr=null;

        public String getIdString() {
            if (idStr == null) idStr = String.format("%d:(%d,%d)", id, x, y);
            return idStr;
        }

        protected NodeKind kind; //TODO kind must be private
        public void setDefaultKind() { kind = NodeKind.DEFAULT; }
        public void setStartKind() { kind = NodeKind.START; }
        public void setGoalKind() { kind = NodeKind.GOAL; }
        public boolean isGoalKind() { return kind == NodeKind.GOAL; }
        public boolean isStartKind() {return kind == NodeKind.START; }

        protected RuntimeState state;
        public RuntimeState getState() { return state; }
        public void setDefault() { state = RuntimeState.DEFAULT; }
        public void setStart() { state = RuntimeState.START; }
        public void setGoal() { state = RuntimeState.GOAL; }
        public void setOpen() { state = RuntimeState.OPEN; }
        public void setClosed() { state = RuntimeState.CLOSED; }
        public void setBoundary() { state = RuntimeState.BOUNDARY; }
        public void setPath() { state = RuntimeState.PATH; }

        public boolean isDefault() { return state == RuntimeState.DEFAULT; }
        public boolean isClosed() { return state == RuntimeState.CLOSED; }
        public boolean isGoal() { return state == RuntimeState.GOAL; }
        public boolean isPath() { return state == RuntimeState.PATH; }
        public boolean isOpen() { return state == RuntimeState.OPEN; }

        public boolean isCandidate() {
            switch(state) {
                case DEFAULT:
                case OLD:
                case OLD_PATH:
                    return true;
                default:
                    return false;
            }
        }

        public void setOld() {
            switch (state) {
                case CLOSED:
                case OPEN:
                //case ROLLEDBACK:      /* use it if define ROLLEDBACK runtimestate*/
                    state = RuntimeState.OLD;
            }
        }

        public void setOldPath() {
            switch (state) {
                case PATH:
                    state = RuntimeState.OLD_PATH;
            }
        }

        List<tEdge> edges;
        private List<tNode> neighbors;

        public List<tNode> getChildren() { return neighbors; }
        public List<tEdge> getEdges() { return edges; }

        public tNode mgParent;
        public tEdge mgEdge;

        public void set_mg_attributes(tNode parent, tEdge edge) {
            mgParent = parent;
            mgEdge = edge;
        }

        public void reset_mg_attributes() {
            mgParent = null;
            mgEdge = null;
        }

        public Node(int x, int y) {
            id = getNextNodeId();
            this.x = x;
            this.y = y;
            this.kind = NodeKind.DEFAULT;
            this.state = RuntimeState.DEFAULT;

            edges = null;
            neighbors = null;

            mgParent = null;
            mgEdge = null;
        }
        
        public void copyData(BaseGraph.Node node) {
            this.x = node.x;
            this.y = node.y;
            this.kind = node.kind;
            this.state = node.state;
        }

        public void addNeighbor(tNode node) {
            if (neighbors == null) neighbors = new LinkedList<tNode>();
            if (!neighbors.contains(node)) neighbors.add(node);
        }
        public void addEdge(tEdge edge) {
            if (edges == null) edges = new LinkedList<tEdge>();
            if (!edges.contains(edge)) edges.add(edge);
        }
        public boolean isNeighbour(int n_id) {
            for (Node n : neighbors) {
                if (n.id == n_id) {
                    return true;
                }
            }
            return false;
        }
        
        public void delNeighbor(tNode node){
            if(neighbors.contains(node))
                neighbors.remove(node);

        }
        
    public void delEdge(tEdge edge){
            if(edges.contains(edge))
                edges.remove(edge);
        }
    }

    static private int uidMaxEdge=0;
    static private int getNextEdgeId() { return ++uidMaxEdge; }

    abstract public class Edge {

        private int id;
        protected String idStr=null;
        public String getIdString() { return idStr; }

        protected RuntimeState state;
        public tNode node_1;
        public tNode node_2;

        public double cost;

        public void setDefault() { state = RuntimeState.DEFAULT; }
        public void setOpen() { state = RuntimeState.OPEN; }
        public void setClosed() { state = RuntimeState.CLOSED; }
        public void setBoundary() { state = RuntimeState.BOUNDARY; }
        public void setPath() { state = RuntimeState.PATH; }

        /*public void setOld() {
            if(state == RuntimeState.CLOSED || state == RuntimeState.OPEN || state == RuntimeState.ROLLEDBACK)
                state = RuntimeState.OLD;
        }
       */

        public void setOld() {
            if(state == RuntimeState.CLOSED || state == RuntimeState.OPEN)
                state = RuntimeState.OLD;
        }
        
        public void setOldPath() {
            if(state == RuntimeState.PATH)
                state = RuntimeState.OLD_PATH;
        }

        public Edge(tNode node_1, tNode node_2) {
            assert node_1 != null;
            assert node_2 != null;
            assert node_1 != node_2; // no self loops...

            id = getNextEdgeId();
            this.state = RuntimeState.DEFAULT;
            this.node_1 = node_1;
            this.node_2 = node_2;

            //this.cost = euclideanDistance(node_1, node_2) * (1.0 + Math.random());

            this.cost = euclideanDistance(node_1.x, node_1.y, node_2.x, node_2.y) * (1.0 + Math.random());
            computeLineParameters();
        }
        
        public void copyData(BaseGraph.Edge edge) {
            this.state = edge.state;
            this.cost = edge.cost;
        }

        public boolean isEdgeOn(tNode n1, tNode n2) {
            if (node_1 == n1 && node_2 == n2) return true;
            if (node_2 == n1 && node_1 == n2) return true;
            return false;
        }

        // used for edge crossing computation
        // equation of line in matrix form
        // a*x + b*y = c
        // a = -dy, b = dx, c = b*y1 + a*x1
        public int[] abc;
        public void computeLineParameters() {
            abc = new int[3];
            BaseGraph.computeLineParameters(abc, node_1.x, node_1.y, node_2.x, node_2.y);
        }
    }
    
    static private void computeLineParameters(int[] abc, int x1, int y1, int x2, int y2) {
        abc[0] = -(y1-y2);
        abc[1] = x1-x2;
        abc[2] = abc[1]*y1 + abc[0]*x1;
    }

}
