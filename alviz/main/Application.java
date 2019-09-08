/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.main;

import alviz.base.algorithm.Algorithm;
import alviz.base.algorithm.AlgorithmFactory;
import alviz.base.algorithm.AlgorithmType;
import alviz.base.graph.BaseGraph;
import alviz.base.graph.GraphClass;
import alviz.base.graph.GraphPainter;
import alviz.graph.factory.GraphFactory;
import alviz.base.graph.GraphSelector;
import alviz.graph.factory.GraphShape;
import alviz.util.PaintDelay;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;
import javax.swing.JPanel;

/**
 *
 * @author baskaran
 */
public class Application {

    static public enum ExecState {
        APPLICATION_STARTED,
        ALGO_SELECTED,
        SIZE_SELECTED,
        SEARCH_SPACE_LOADED,
        START_NODE_APPLIED,
        GOAL_NODE_APPLIED,
        STEPPING_THROUGH,
        RUNNING,
        STOPPED,
        RERUN_ALGO,
        PIPE_ALGO,
        RESET_GRAPH;
    }
    public boolean isSearchSpaceLoaded() { return execState == ExecState.SEARCH_SPACE_LOADED; }

    public PaintDelay refreshRate;

    public int nodeCountGiven; // asked by user
    public int nodeCount; // produced by graph generator

    public int branchingFactor;
    public int density;
    public int densityMax;
    
    public boolean sizeUpdatable;
    public boolean computeGridDimension;

    public void setNodeCount(int nodeCountGiven) {
        this.nodeCountGiven = nodeCountGiven;
        this.nodeCount = nodeCountGiven;
        if (!sizeUpdatable) {
            computeGridDimension = true;
        }
    }

    public ExecState execState;

    public GraphClass graphClass;
    public BaseGraph graph;
    public GraphSelector graphSelector;

    public String algoName;
    public AlgorithmType algoType;
    public Algorithm algo;
    public Thread algoThread;

    public int displayWidth;
    public int displayHeight;
    public void setDisplayDimension(int width, int height) {
        displayWidth = width;
        displayHeight = height;
    }

    private boolean pipeMode;
    private GraphClass pipeFromGraphClass;
    private BaseGraph pipeFromGraph;

    public void setPipeMode(boolean mode) { pipeMode = mode; }
    public boolean isPipeMode() { return pipeMode; }

    static private Application instance=null;
    static public Application getInstance(int nodeCountGiven) {
        if (instance == null) {
            instance = new Application(nodeCountGiven);
        }
        return instance;
    }
    
    private Application(int nodeCountGiven) {
        resetApp();
        refreshRate = new PaintDelay();
        branchingFactor = 2;
        setNodeCount(nodeCountGiven);
        this.sizeUpdatable = false;
        this.computeGridDimension = true;
    }

    public void resetApp() {
        execState = ExecState.APPLICATION_STARTED;

        //graphClass = null;
        graph = null;
        graphSelector = null;

        //algoName = null;
        //algoType = null;
        algo = null;
        algoThread = null;

        setPipeMode(false);
        pipeFromGraphClass = null;
        pipeFromGraph = null;
    }

    public boolean isNextState(ExecState targetState) {
        switch (targetState) {
            case APPLICATION_STARTED:
                switch (execState) {
                    case APPLICATION_STARTED:
                        return true;
                    default:
                        return false;
                }
            case ALGO_SELECTED:
                switch (execState) {
                    case APPLICATION_STARTED:
                    case ALGO_SELECTED:
                    case STOPPED:
                    case PIPE_ALGO:
                        return true;
                    default:
                        return false;
                }
            case SIZE_SELECTED:
                switch (execState) {
                    case ALGO_SELECTED:
                    case SIZE_SELECTED:
                        return true;
                    default:
                        return false;
                }
            case SEARCH_SPACE_LOADED:
                switch (execState) {
                    case ALGO_SELECTED:
                    case SIZE_SELECTED:
                    case SEARCH_SPACE_LOADED: // can load another graph shape
                        return true;
                    default:
                        return false;
                }
            case START_NODE_APPLIED:
                switch (execState) {
                    case SEARCH_SPACE_LOADED:
                    case START_NODE_APPLIED:
                    {
                        //if (isGameTree()) return false;
                        
                        List<? extends BaseGraph.Node> nodes = graphSelector.getNodes();
                        if (nodes != null && nodes.size() == 1)
                            return true;
                        else
                            return false;
                    }
                    default:
                        return false;
                }
            case GOAL_NODE_APPLIED:
                switch (execState) {
                    case START_NODE_APPLIED:
                    case GOAL_NODE_APPLIED:
                    {
                        //if (isGameTree()) return false;

                        List<? extends BaseGraph.Node> nodes = graphSelector.getNodes();
                        if (nodes != null)
                            return true;
                        else
                            return false;
                    }
                    default:
                        return false;
                }
            case STEPPING_THROUGH:
                switch (execState) {
                    case ALGO_SELECTED:
                        return isPipeMode();
                    //case SEARCH_SPACE_LOADED:
                    //    return isGameTree();
                    case GOAL_NODE_APPLIED:
                    case STEPPING_THROUGH:
                    case RUNNING:
                        return true;
                    default:
                        return false;
                }
            case RUNNING:
                switch (execState) {
                    case ALGO_SELECTED:
                        return isPipeMode();
                    //case SEARCH_SPACE_LOADED:
                    //    return isGameTree();
                    case GOAL_NODE_APPLIED:
                    case STEPPING_THROUGH:
                        return true;
                    default:
                        return false;
                }
            case STOPPED:
                switch (execState) {
                    case ALGO_SELECTED:
                    case SIZE_SELECTED:
                    case SEARCH_SPACE_LOADED:
                    case START_NODE_APPLIED:
                    case GOAL_NODE_APPLIED:
                    case STEPPING_THROUGH:
                    case RUNNING:
                    case STOPPED:
                    case RERUN_ALGO:
                    case RESET_GRAPH:
                        return true;
                    default:
                        return false;
                }
            case PIPE_ALGO:
                switch (execState) {
                    case STOPPED:
                        return true;
                    default:
                        return false;
                }
            default: return false;
        }

    }

    public boolean inNodeSelectionState() {
        switch (execState) {
            case SEARCH_SPACE_LOADED:
            case RESET_GRAPH:
            case START_NODE_APPLIED:
                return true;
            default:
                return false;
        }
    }

    public void recreateGraph() {
        if (pipeFromGraphClass != graphClass) {
//            System.out.println("recreateGraph(): gc=" + graphClass + ", from gc=" + pipeFromGraphClass);
            graph = GraphFactory.createGraph(graphClass, pipeFromGraph);
        }
    }

    public void resetPipeMode() {
        setPipeMode(false);
        pipeFromGraphClass = null;
        pipeFromGraph = null;
    }

    static private Map<String, AlgorithmType> algoMap;
    static {
        algoMap = new HashMap<String, AlgorithmType>();
        for (AlgorithmType t : AlgorithmType.values()) {
            algoMap.put(t.getMenuItemName(), t);
        }
    }

    private boolean setAlgoType(String algo) {
        algoType = algoMap.get(algo);
        return algoType != null;
    }

    public boolean selectAlgo(String algoname) {
//        System.out.println("selectAlgo> " + algoname);
        boolean rc = false;
        if (algoname != null) {
            if (isNextState(ExecState.ALGO_SELECTED)  && setAlgoType(algoname)) {
                this.algoName = algoname;
                this.graphClass = algoType.getGraphClass();
                execState = ExecState.ALGO_SELECTED;
                rc = true;
            }
        }
        return rc;
    }

    //public boolean isGameTree() {
    //    return (algoType != null ? algoType.isGameTree() : false);
    //}
    //public boolean isSeqAlign() {
    //    return (algoType != null ? algoType.isSeqAlign() : false);
    //}

    public boolean selectSize(int nodeCount) {
        boolean rc = false;
        if (isNextState(ExecState.SIZE_SELECTED)) {
            setNodeCount(nodeCount);
            execState = ExecState.SIZE_SELECTED;
            rc = true;
        }
        return rc;
    }

    public boolean selectBranchingFactor(String brFactor) {
        boolean rc = false;
        if (brFactor != null) {
            if (isNextState(ExecState.SIZE_SELECTED)) {
                branchingFactor = Integer.valueOf(brFactor);
                execState = ExecState.SIZE_SELECTED;
                rc = true;
            }
        }
        return rc;
    }

    public boolean selectDensity(String xdensity) {
        boolean rc = false;
        if (xdensity != null) {
            if (isNextState(ExecState.SIZE_SELECTED)) {
                density = Integer.valueOf(xdensity);
                execState = ExecState.SIZE_SELECTED;
                rc = true;
            }
        }
        return rc;
    }

    private void setSizeUpdatableTrue() {
        if (nodeCount != nodeCountGiven) {
            sizeUpdatable = true;
        }
    }

    public boolean loadGraph(String command) {
        boolean rc = false;
        int width = displayWidth;
        int height = displayHeight;
        if (isNextState(ExecState.SEARCH_SPACE_LOADED)) {
            if (command.equals("Game Tree-1")) {
                resetApp();
                graph = GraphFactory.createGraph(graphClass);
                graph = GraphFactory.createTree(graph, branchingFactor, width, height, 2, 2*(densityMax-density), false);
                nodeCount = graph.getNodeCount();
                setSizeUpdatableTrue();
                graphSelector = new GraphSelector(graph);
                execState = ExecState.SEARCH_SPACE_LOADED;
                rc = true;
            }
            else if (command.equals("Game Tree-2")) {
                resetApp();
                graph = GraphFactory.createGraph(graphClass);
                graph = GraphFactory.createTree2DRect(graph, branchingFactor, width, height, 2, 2*(densityMax-density), false);
                nodeCount = graph.getNodeCount();
                setSizeUpdatableTrue();
                graphSelector = new GraphSelector(graph);
                execState = ExecState.SEARCH_SPACE_LOADED;
                rc = true;
            }
            else if (command.equals("Game Tree-3")) {
                resetApp();
                graph = GraphFactory.createGraph(graphClass);
                graph = GraphFactory.createTree2D(graph, branchingFactor, width, height, 2, 2*(densityMax-density), false);
                nodeCount = graph.getNodeCount();
                setSizeUpdatableTrue();
                graphSelector = new GraphSelector(graph);
                execState = ExecState.SEARCH_SPACE_LOADED;
                rc = true;
            }
            else if (command.equals("Graph Sparse")) {
                resetApp();
                graph = GraphFactory.createGraph(graphClass);
                graph = GraphFactory.createRandomGraph(graph, nodeCountGiven, width, height, 6, false);
                nodeCount = graph.getNodeCount();
                setSizeUpdatableTrue();
                graphSelector = new GraphSelector(graph);
                execState = ExecState.SEARCH_SPACE_LOADED;
                rc = true;
            }
            else if (command.equals("Graph Dense")) {
                resetApp();
                graph = GraphFactory.createGraph(graphClass);
                graph = GraphFactory.createRandomGraphTriangulate(graph, nodeCountGiven, width, height, 6, false);
                nodeCount = graph.getNodeCount();
                setSizeUpdatableTrue();
                graphSelector = new GraphSelector(graph);
                execState = ExecState.SEARCH_SPACE_LOADED;
                rc = true;
            }
            else if (command.equals("Tree")) {
                resetApp();
                graph = GraphFactory.createGraph(graphClass);
                graph = GraphFactory.createRandomMST(graph, nodeCountGiven, width, height, 6, false);
                nodeCount = graph.getNodeCount();
                setSizeUpdatableTrue();
                graphSelector = new GraphSelector(graph);
                execState = ExecState.SEARCH_SPACE_LOADED;
                rc = true;
            }
            else if (command.equals("Grid-1")) {
                resetApp();
                graph = GraphFactory.createGraph(graphClass);
                graph = GraphFactory.createGrid(graph, width, height, nodeCountGiven, GraphShape.GRID_1);
                nodeCount = graph.getNodeCount();
                setSizeUpdatableTrue();
                graphSelector = new GraphSelector(graph);
                execState = ExecState.SEARCH_SPACE_LOADED;
                rc = true;
            }
            else if (command.equals("Grid-2")) {
                resetApp();
                graph = GraphFactory.createGraph(graphClass);
                graph = GraphFactory.createGrid(graph, width, height, nodeCountGiven, GraphShape.GRID_2);
                nodeCount = graph.getNodeCount();
                setSizeUpdatableTrue();
                graphSelector = new GraphSelector(graph);
                execState = ExecState.SEARCH_SPACE_LOADED;
                rc = true;
            }
            else if (command.equals("Grid-3")) {
                resetApp();
                graph = GraphFactory.createGraph(graphClass);
                graph = GraphFactory.createGrid(graph, width, height, nodeCountGiven, GraphShape.GRID_3);
                nodeCount = graph.getNodeCount();
                setSizeUpdatableTrue();
                graphSelector = new GraphSelector(graph);
                execState = ExecState.SEARCH_SPACE_LOADED;
                rc = true;
            }
            else if (command.equals("Grid-4")) {
                resetApp();
                graph = GraphFactory.createGraph(graphClass);
                graph = GraphFactory.createGrid(graph, width, height, nodeCountGiven, GraphShape.GRID_4);
                nodeCount = graph.getNodeCount();
                setSizeUpdatableTrue();
                graphSelector = new GraphSelector(graph);
                execState = ExecState.SEARCH_SPACE_LOADED;
                rc = true;
            }
            else if (command.equals("Grid-1 MST")) {
                resetApp();
                graph = GraphFactory.createGraph(graphClass);
                graph = GraphFactory.createGridMST(graph, width, height, nodeCountGiven, GraphShape.GRID_1_MST, false);
                nodeCount = graph.getNodeCount();
                setSizeUpdatableTrue();
                graphSelector = new GraphSelector(graph);
                execState = ExecState.SEARCH_SPACE_LOADED;
                rc = true;
            }
            else if (command.equals("Grid-2 MST")) {
                resetApp();
                graph = GraphFactory.createGraph(graphClass);
                graph = GraphFactory.createGridMST(graph, width, height, nodeCountGiven, GraphShape.GRID_2_MST, false);
                nodeCount = graph.getNodeCount();
                setSizeUpdatableTrue();
                graphSelector = new GraphSelector(graph);
                execState = ExecState.SEARCH_SPACE_LOADED;
                rc = true;
            }
            else if (command.equals("Grid-3 MST")) {
                resetApp();
                graph = GraphFactory.createGraph(graphClass);
                graph = GraphFactory.createGridMST(graph, width, height, nodeCountGiven, GraphShape.GRID_3_MST, false);
                nodeCount = graph.getNodeCount();
                setSizeUpdatableTrue();
                graphSelector = new GraphSelector(graph);
                execState = ExecState.SEARCH_SPACE_LOADED;
                rc = true;
            }
            else if (command.equals("Grid-4 MST")) {
                resetApp();
                graph = GraphFactory.createGraph(graphClass);
                graph = GraphFactory.createGridMST(graph, width, height, nodeCountGiven, GraphShape.GRID_4_MST, false);
                nodeCount = graph.getNodeCount();
                setSizeUpdatableTrue();
                graphSelector = new GraphSelector(graph);
                execState = ExecState.SEARCH_SPACE_LOADED;
                rc = true;
            }
            else {
                //System.out.printf("loadGraph(%s), graph not found...\n", command);
            }
            //if (isGameTree()) {
            //    GraphPainter.setPrintLabelBoth();
            //}
            //else {
            //    GraphPainter.setPrintLabelNone();
            //}
        }
        return rc;
    }

    public boolean assignStartNode() {
        boolean rc = false;
        if (isNextState(ExecState.START_NODE_APPLIED)) {
            graph.setStartNode(graphSelector.getNodes().get(0));
            graphSelector.clearSelection();
            execState = ExecState.START_NODE_APPLIED;
            rc = true;
        }
        return rc;
    }

    public boolean assignGoalNode() {
        boolean rc = false;
        if (isNextState(ExecState.GOAL_NODE_APPLIED)) {
            List<? extends BaseGraph.Node> nodes = graphSelector.getNodes();
            nodes.remove(graph.getStartNode());
            if (nodes.size() > 0) {
                graph.setGoalNode(nodes);
                graphSelector.clearSelection();
                execState = ExecState.GOAL_NODE_APPLIED;
                rc = true;
            }
        }
        return rc;
    }

    private void captureDetailsForPiping() {
        pipeFromGraphClass = graphClass;
        pipeFromGraph = graph;
    }

    public boolean stepThroughAlgo(JPanel panel) {
        boolean rc = false;
        if (isNextState(ExecState.STEPPING_THROUGH)) {
            // first time invocation
            if (algo == null) {
//                System.out.println("stepThroughAlgo> algo is null");
                if (isPipeMode()) {
                    recreateGraph();
                    resetPipeMode();
                    panel.repaint();
                }
                algo = AlgorithmFactory.createAlgorithm(panel, algoType, graph, true, refreshRate);
                algoThread = new Thread(algo);
                algoThread.start();
                execState = ExecState.STEPPING_THROUGH;
                rc = true;
            }
            // otherwise already in step through mode
            else {
//                System.out.println("stepThroughAlgo> algo is not null");
                if (algoThread.isAlive()) {
                    algo.setStepThroughTrue();
                    LockSupport.unpark(algoThread);
                    execState = ExecState.STEPPING_THROUGH;
                    rc = true;
                }
                else {
                    // algorithm might have ended...
                }
            }
        }

        if (rc) captureDetailsForPiping();
        return rc;
    }

    public boolean runAlgo(JPanel panel) {
        boolean rc = false;
        if (isNextState(ExecState.RUNNING)) {
            // first time invocation
            if (algo == null) {
//                System.out.println("runAlgo> algo is null");
                if (isPipeMode()) {
                    recreateGraph();
                    resetPipeMode();
                    panel.repaint();
                }
                algo = AlgorithmFactory.createAlgorithm(panel, algoType, graph, false, refreshRate);
                algoThread = new Thread(algo);
                algoThread.start();
                execState = ExecState.RUNNING;
                rc = true;
            }
            // otherwise execution is in step through mode
            else {
//                System.out.println("runAlgo> algo is not null");
                algo.setRunnableTrue();
                LockSupport.unpark(algoThread);
                execState = ExecState.RUNNING;
                rc = true;
            }
        }

        if (rc) captureDetailsForPiping();
        return rc;
    }

    // cleanup the stop, pipe, rerun and reset logic
    // rest is running fine...
    public boolean stopAlgo() {
        boolean rc = false;
        if (isNextState(ExecState.STOPPED)) {
            switch (execState) {
                case ALGO_SELECTED:
                    execState = ExecState.ALGO_SELECTED;
                    rc = true;
                    break;
                case SIZE_SELECTED:
                    execState = ExecState.SIZE_SELECTED;
                    rc = true;
                    break;
                case START_NODE_APPLIED:
                    if (graph != null) {
                        graph.resetStartNode();
                    }
                    execState = ExecState.SEARCH_SPACE_LOADED;
                    rc = true;
                    break;
                case GOAL_NODE_APPLIED:
                    if (graph != null) {
                        graph.resetStartNode();
                        graph.resetGoalNodes();

                    }
                    execState = ExecState.SEARCH_SPACE_LOADED;
                    rc = true;
                    break;
                default:
                    if (algoThread != null) {
                        if (algoThread.isAlive() && !algoThread.isInterrupted()) {
                            algoThread.interrupt();
                            // change made
                            execState = ExecState.STOPPED;
                            rc = true;
                        }
                        else {
                            // <TODO> check for other states that needs stopping
                            execState = ExecState.STOPPED;
                            rc = true;
                        }
                    }
                    else {
                        execState = ExecState.STOPPED;
                        rc = true;
                    }
                    // algorithm object looses meaning at this point
                    // and so does algoThread
                    algo = null;
                    algoThread = null;
            }
        }
        return rc;
    }

    public boolean pipeAlgo(JPanel panel) {
//        System.out.println("pipeAlgo> from gc = " + pipeFromGraphClass);
        boolean rc = false;
        if (isNextState(ExecState.PIPE_ALGO)) {
            if (graph != null) {
                setPipeMode(true);
                graph.setGraphToOld();
                execState = ExecState.PIPE_ALGO;
                rc = true;
            }
        }
        return rc;
    }

    public boolean rerunAlgo(JPanel panel) {
        boolean rc = false;
        if (isNextState(ExecState.RERUN_ALGO)) {
            if (graph != null) {
                graph.resetGraph();
                // change made
                if (algo == null) {
                algo = AlgorithmFactory.createAlgorithm(panel, algoType, graph, false, refreshRate);
                algoThread = new Thread(algo);
                algoThread.start();
                execState = ExecState.RUNNING;
                rc = true;
                }

            }
            execState = ExecState.RERUN_ALGO;
            rc = true;
        }
        return rc;
    }

    public boolean resetGraph() {
        boolean rc = false;
        if (isNextState(ExecState.RESET_GRAPH)) {
            if (graph != null) {
                graph.resetGraph();
                graph.resetStartNode();
                graph.resetGoalNodes();
            }
            execState = ExecState.RESET_GRAPH;
            rc = true;
        }
        return rc;
    }

}
