package alviz.algorithm;

import alviz.base.algorithm.Algorithm;
import alviz.base.graph.BaseGraph;
import alviz.graph.Graph;
import alviz.util.Pair;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author baskaran
 */
public class DFID extends Algorithm {

    private BaseGraph graph;
    private BaseGraph graph1;
    private Stack<Pair<BaseGraph.Node,BaseGraph.Node>> open;
    private LinkedList<Pair<BaseGraph.Node,BaseGraph.Node>> closed;
    private boolean done=false;

    public DFID(BaseGraph graph) {
        super();
        
        this.graph=graph;
        this.open = null;
        this.closed=null;
    }

    public void execute() throws Exception {
        int depth=1;
        while(!done){
            //int depth=1;
            //BaseGraph graph=new BaseGraph(this.graph1);
            open = new Stack<Pair<BaseGraph.Node,BaseGraph.Node>>();
            closed = new LinkedList<Pair<BaseGraph.Node,BaseGraph.Node>>();
            openNode(graph.getStartNode(), null);
            dfs(depth);
            //graph.deleteNode(n);
            if(!done){
             graph.resetGraph();
            
            depth=depth+1;
            //System.out.println(depth);
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
    private void deleteNode(Pair<BaseGraph.Node,BaseGraph.Node> pn) {
        BaseGraph.Node n = pn.fst;
        graph.deleteNode(n);
        
    }
    
    private void dfs(int depth) throws Exception {
        int count=0; 
        Stack<Integer> stack = new Stack<Integer>();
        stack.push(0);
        System.out.println(depth);
        while(!open.isEmpty()) {
            
            Pair<BaseGraph.Node, BaseGraph.Node> ph = open.pop();
            BaseGraph.Node h = ph.fst;
            int y=(int)stack.pop();
            if (graph.goalTest(h)) {
                closeNode(ph);
                h.setGoal();
                done = true;
                
            }

            else { 
                if(((depth-y)>=0)&&!done){ 
                     List<BaseGraph.Node> neighbours = graph.moveGen(h);
                     if(neighbours==null){
                         count=count-1;
                     }
                     if (neighbours != null) {
                      // c=0;
                       for (BaseGraph.Node n : neighbours) {
                          if (n.isCandidate()) {
                            openNode(n, h);
                            stack.push(y+1);
                         }
                       }
                      //count=count+1;
                     }
                     
                    closeNode(ph);
                    
                 }
            }
            //c1=c1-c;
           
                show();
                
            
        }
        //graph.resetGraph();
        //deleteNode(ph);
        
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
