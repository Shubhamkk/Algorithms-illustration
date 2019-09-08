/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
import java.util.Stack;
import java.util.Random;
/**
 *
 * @author baskaran
 */
public class IDA extends Algorithm {

    private BaseGraph graph;
    private Stack<Pair<BaseGraph.Node,BaseGraph.Node>> open;
    private LinkedList<Pair<BaseGraph.Node,BaseGraph.Node>> closed;
    private boolean done=false;

    public IDA(BaseGraph graph) {
        super();
        this.graph = graph;
        this.open = null;
    }

    public void execute() throws Exception {
        
        double bound=graph.getHuristic(graph.getStartNode());
        while(!done){
        open = new Stack<Pair<BaseGraph.Node,BaseGraph.Node>>();
        closed = new LinkedList<Pair<BaseGraph.Node,BaseGraph.Node>>();
        
        
        openNode(graph.getStartNode(), null);
        
        double t=ida(0,bound);
        
        if(!done){
            graph.resetGraph();
            
            bound=t;
            this.open=null;
            this.closed=null;
            }
        }
        generatePath();
        setStateEnded();
        show();
    }

    private void openNode(BaseGraph.Node n, BaseGraph.Node p) {
        if (n != null) {
            graph.openNode(n, p);
            open.push(new Pair(n, p));
        }
    }
    private void closeNode(Pair<BaseGraph.Node,BaseGraph.Node> pn) {
        BaseGraph.Node n = pn.fst;
        if (n != null) {
            graph.closeNode(n);
            closed.add(pn);
        }
    }
    private double cost(BaseGraph.Node p, BaseGraph.Node n){
        return graph.getCost(p, n);
        
    }
    private double ida(double g,double bound) throws Exception {
        double min=Double.POSITIVE_INFINITY;
        
        if (!open.isEmpty()) {
            Pair<BaseGraph.Node, BaseGraph.Node> ph = open.pop();
            BaseGraph.Node h = ph.fst;
            double f=g+graph.getHuristic(h);
            if(f>bound){
                return f;
            }
            
            if (graph.goalTest(h)) {
                closeNode(ph);
                h.setGoal();
                done = true;
                //generatePath();
                return Double.NaN;
            }
            else { 
                if(!done){
                  List<BaseGraph.Node> neighbours = graph.moveGen(h);
                  if (neighbours != null) {
                    for (BaseGraph.Node n : neighbours) {
                        if (n.isCandidate()) {
                            openNode(n, h);
                            double t=ida(g+cost(h,n),bound);
                            if(t<min){
                                min=t;
                            }
                           //show();
                          if(!closed.contains(ph)){ 
                            closeNode(ph);
                          }
                          
                        }
                        
                        if(!closed.contains(ph)){ 
                            closeNode(ph);
                          }
                        show();
                        //closeNode(ph);
                        if(done){
                             //generatePath();
                             return Double.NaN;
                          }
                    }
                    
                    
                }
                
                }
            }
            //show();
            
        }
        return min;
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
