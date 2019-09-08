/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.base.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.List;

/**
 *
 * @author baskaran
 */
public class GraphPainter {

    static public enum PrintLabel {
        NONE,
        NODE,
        EDGE,
        BOTH;
    }

    static private PrintLabel printLabelStatus = PrintLabel.NONE;
    static public void setPrintLabelBoth() { printLabelStatus = PrintLabel.BOTH; }
    static public void setPrintLabelNode() { printLabelStatus = PrintLabel.NODE; }
    static public void setPrintLabelEdge() { printLabelStatus = PrintLabel.EDGE; }
    static public void setPrintLabelNone() { printLabelStatus = PrintLabel.NONE; }

    static final public int NODE_WIDTH = 6;
    static final public int NODE_HEIGHT= 6;

    // pre-calculated values...
    static final private int NODE_WIDTH_BY_2    = NODE_WIDTH/2;
    static final private int NODE_HEIGHT_BY_2   = NODE_HEIGHT/2;
    static final private int NODE_WIDTH_PLUS_1  = NODE_WIDTH + 1;
    static final private int NODE_HEIGHT_PLUS_1 = NODE_HEIGHT + 1;

    // size for nodes selected by the user...
    static final private int BIG_NODE_WIDTH = NODE_WIDTH * 2;
    static final private int BIG_NODE_HEIGHT= NODE_HEIGHT * 2;

    // pre-calculated values...
    static final private int BIG_NODE_WIDTH_BY_2    = BIG_NODE_WIDTH/2;
    static final private int BIG_NODE_HEIGHT_BY_2   = BIG_NODE_HEIGHT/2;
    static final private int BIG_NODE_WIDTH_PLUS_1  = BIG_NODE_WIDTH + 1;
    static final private int BIG_NODE_HEIGHT_PLUS_1 = BIG_NODE_HEIGHT + 1;

    private GraphPainter() {}

    static public <tNode extends BaseGraph.Node, tEdge extends BaseGraph.Edge>
    void paint (BaseGraph<tNode,tEdge> graph, Graphics gc, List<tNode> selectedNodes) {
        if (graph == null) return;

        Graphics2D gc2d = (Graphics2D) gc;

        paint(graph, gc2d);

        if (selectedNodes != null) {
            for (tNode n : selectedNodes) {
                gc2d.setColor(Color.RED);
                gc2d.drawOval(n.x-BIG_NODE_WIDTH_BY_2, n.y-BIG_NODE_HEIGHT_BY_2, BIG_NODE_WIDTH, BIG_NODE_HEIGHT);
                gc2d.fillOval(n.x-BIG_NODE_WIDTH_BY_2, n.y-BIG_NODE_HEIGHT_BY_2, BIG_NODE_WIDTH_PLUS_1, BIG_NODE_HEIGHT_PLUS_1);
            }
        }
    }

    static public <tNode extends BaseGraph.Node, tEdge extends BaseGraph.Edge>
    void paint(BaseGraph<tNode,tEdge> graph, Graphics2D gc) {
        if (graph == null) return;

        paintEdges(graph.getEdges(), gc);
        paintEdges(graph.getSavedEdges(), gc);
        paintEdges(graph.getBackPointerEdges(), gc);

        List<tNode> nodes = graph.getNodes();
        if (nodes != null) {
            for (tNode n : nodes) paintNode(n, gc);
            switch (printLabelStatus) {
                case NODE:
                case BOTH:
                    for (tNode n : nodes) paintNodeLabel(n, gc);
                    break;
            }
        }
    }
    
    static private <tEdge extends BaseGraph.Edge>
    void paintEdges(List<tEdge> edges, Graphics2D gc) {
        if (edges != null) {
            for (tEdge e : edges) paintEdge(e, gc);
            switch (printLabelStatus) {
                case EDGE:
                case BOTH:
                    for (tEdge e : edges) paintEdgeLabel(e, gc);
                    break;
            }
        }
    }

    static private <tEdge extends BaseGraph.Edge>
    void paintEdge(tEdge e, Graphics2D gc) {
        if (e.state.stroke != null)  {
            Stroke t = gc.getStroke();
            gc.setStroke(e.state.stroke);
            gc.setColor(e.state.color);
            gc.drawLine(e.node_1.x, e.node_1.y, e.node_2.x, e.node_2.y);
            gc.setStroke(t);
        }
        else {
            gc.setColor(e.state.color);
            gc.drawLine(e.node_1.x, e.node_1.y, e.node_2.x, e.node_2.y);
        }
    }
    
    static public <tEdge extends BaseGraph.Edge>
    void paintEdgeLabel(tEdge e, Graphics2D gc) {
        String str = e.getIdString();
        if (str != null) {
            gc.setColor(Color.BLACK);
            gc.drawString(str, (e.node_1.x + e.node_2.x) / 2, (e.node_1.y + e.node_2.y) / 2);
        }
    }

    static private <tNode extends BaseGraph.Node>
    void paintNode(tNode n, Graphics2D gc) {
        switch (n.kind) {
            case DEFAULT:
                gc.setColor(n.state.color);
                gc.drawOval(n.x-NODE_WIDTH_BY_2, n.y-NODE_HEIGHT_BY_2, NODE_WIDTH, NODE_HEIGHT);
                gc.fillOval(n.x-NODE_WIDTH_BY_2, n.y-NODE_HEIGHT_BY_2, NODE_WIDTH_PLUS_1, NODE_HEIGHT_PLUS_1);
                break;
            case START:
                gc.setColor(n.state.color);
                gc.drawOval(n.x-BIG_NODE_WIDTH_BY_2, n.y-BIG_NODE_HEIGHT_BY_2, BIG_NODE_WIDTH, BIG_NODE_HEIGHT);
                gc.fillOval(n.x-BIG_NODE_WIDTH_BY_2, n.y-BIG_NODE_HEIGHT_BY_2, BIG_NODE_WIDTH_PLUS_1, BIG_NODE_HEIGHT_PLUS_1);
                //gc.drawRect(n.x-BIG_NODE_WIDTH_BY_2, n.y-BIG_NODE_HEIGHT_BY_2, BIG_NODE_WIDTH, BIG_NODE_HEIGHT);
                //gc.fillRect(n.x-BIG_NODE_WIDTH_BY_2, n.y-BIG_NODE_HEIGHT_BY_2, BIG_NODE_WIDTH, BIG_NODE_HEIGHT);
                break;
            case GOAL:
                gc.setColor(n.state.color);
                gc.drawRect(n.x-BIG_NODE_WIDTH_BY_2, n.y-BIG_NODE_HEIGHT_BY_2, BIG_NODE_WIDTH, BIG_NODE_HEIGHT);
                gc.fillRect(n.x-BIG_NODE_WIDTH_BY_2, n.y-BIG_NODE_HEIGHT_BY_2, BIG_NODE_WIDTH, BIG_NODE_HEIGHT);
                //gc.fill3DRect(n.x-BIG_NODE_WIDTH_BY_2, n.y-BIG_NODE_HEIGHT_BY_2, BIG_NODE_WIDTH, BIG_NODE_HEIGHT, true);
                break;
                
                //if more members in NodeKind are added, Need to be placed here
                
            /*case MAX:                                // Changed
                gc.setColor(n.state.color);
                gc.drawRect(n.x - NODE_WIDTH_BY_2, n.y - NODE_HEIGHT_BY_2, NODE_WIDTH, NODE_HEIGHT);
                gc.fillRect(n.x - NODE_WIDTH_BY_2, n.y - NODE_HEIGHT_BY_2, NODE_WIDTH, NODE_HEIGHT);
                break;
            case MIN:                                //Changed
                gc.setColor(n.state.color);
                gc.drawOval(n.x - NODE_WIDTH_BY_2, n.y - NODE_HEIGHT_BY_2, NODE_WIDTH, NODE_HEIGHT);
                gc.fillOval(n.x - NODE_WIDTH_BY_2, n.y - NODE_HEIGHT_BY_2, NODE_WIDTH_PLUS_1, NODE_HEIGHT_PLUS_1);
                break;
*/
        }
        //gc.setColor(Color.BLACK);
        //gc.drawString(n.getIdString(), n.x, n.y);
    }
    
    static public <tNode extends BaseGraph.Node>
    void paintNodeLabel(tNode n, Graphics2D gc) {
        String str = n.getIdString();
        if (str != null) {
            gc.setColor(Color.BLACK);
            gc.drawString(str, n.x, n.y);
        }
    }

}
