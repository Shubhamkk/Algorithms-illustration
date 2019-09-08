/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.base.graph;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author baskaran
 */
public class GraphSelector {

    private BaseGraph graph;
    private List<? extends BaseGraph.Node> selectedNodes;

    public GraphSelector(BaseGraph graph) {
        this.graph = graph;
        this.selectedNodes = null;
    }

    public void mouseClicked(int x, int y, boolean multipleSelection) {
        if (graph == null) return;
        if (multipleSelection) {
            selectedNodes = hit(x, y, selectedNodes);
        }
        else {
            selectedNodes = hit(x, y, null);
        }
    }

    private <tNode extends BaseGraph.Node> List<tNode> hit(int x, int y, List<tNode> nodeList) {

        int nodeWidth = GraphPainter.NODE_WIDTH;
        int nodeHeight = GraphPainter.NODE_HEIGHT;

        int x0 = -nodeWidth/2;
        int y0 = -nodeHeight/2;

        int x1 = nodeWidth + x0;
        int y1 = nodeHeight + y0;

        for (tNode n : (List<tNode>) graph.getNodes()) {
            int dx = x - n.x;
            int dy = y - n.y;
            if (x0 <= dx && dx <= x1 && y0 <= dy && dy <= y1) {
                if (nodeList == null) nodeList = new LinkedList<tNode>();
                if (!nodeList.remove(n)) nodeList.add(n);
            }
        }
        return nodeList;
    }

    public List<? extends BaseGraph.Node> getNodes() { return selectedNodes; }
    public void clearSelection() { selectedNodes = null; }

}
