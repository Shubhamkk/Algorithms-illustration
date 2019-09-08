/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.algorithm;

import alviz.base.algorithm.Algorithm;
import alviz.base.graph.BaseGraph;
import alviz.graph.Graph;
import alviz.util.Pair;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author baskaran
 */
public class BFS extends Algorithm {

    private BaseGraph graph;
    private Queue<Pair<BaseGraph.Node,BaseGraph.Node>> open;
    private LinkedList<Pair<BaseGraph.Node,BaseGraph.Node>> closed;
    private boolean done=false;

    public BFS(BaseGraph graph) {
        super();
        this.graph = graph;
        this.open = null;
        this.closed = null;
    }

    public void execute() throws Exception {
        open = new LinkedList<Pair<BaseGraph.Node,BaseGraph.Node>>();
        closed = new LinkedList<Pair<BaseGraph.Node,BaseGraph.Node>>();
        openNode(graph.getStartNode(), null);
        bfs();
        generatePath();
        setStateEnded();
        show();
    }

    private void openNode(BaseGraph.Node n, BaseGraph.Node p) {
        if (n != null) {
            graph.openNode(n, p);
            open.offer(new Pair(n, p));
        }
    }
    private void closeNode(Pair<BaseGraph.Node,BaseGraph.Node> pn) {
        BaseGraph.Node n = pn.fst;
        if (n != null) {
            graph.closeNode(n);
            closed.add(pn);
        }
    }

    private void bfs() throws Exception {
        if (!open.isEmpty()) {
            Pair<BaseGraph.Node, BaseGraph.Node> ph = open.poll();
            BaseGraph.Node h = ph.fst;
            if (graph.goalTest(h)){
                closeNode(ph);
                h.setGoal();
                done = true;
            }
            else {
                List<BaseGraph.Node> neighbours = graph.moveGen(h);
                if (neighbours != null) {
                    for (BaseGraph.Node n : neighbours) {
                        if (n.isCandidate()) {
                            openNode(n, h);
                        }
                    }
                }
                closeNode(ph);
            }
            show();
            if (!done) {
                bfs();
            }
        }
    }

    private List<BaseGraph.Node> generatePath() {
        List<BaseGraph.Node> path=null;
        if (closed == null) return path;
        if (closed.isEmpty()) return path;

        Iterator<Pair<BaseGraph.Node,BaseGraph.Node>> closedList = ((LinkedList)closed).descendingIterator();
        if (!closedList.hasNext()) return path;

        Pair<BaseGraph.Node,BaseGraph.Node> pair = closedList.next();
        if (!pair.fst.isGoal()) {
            //System.out.printf("generatePath: pair.fst %s\n", pair.fst.getState().toString());
            return path;
        }

        path = new LinkedList<BaseGraph.Node>();
        // add to path
        pair.fst.setPath();
        path.add(pair.fst);
        while (pair.snd != null) {

            // add to path
            pair.snd.setPath();
            path.add(pair.snd);

            // add edge to path
            BaseGraph.Edge e = graph.getEdge(pair.fst, pair.snd);
            if (e != null) {
                e.setPath();
            }

            // search for predecessor pair
            BaseGraph.Node n = pair.snd;
            while (closedList.hasNext()) {
                pair = closedList.next();
                if (pair.fst == n) break;
            }
        }

        return path;
    }
    
}
