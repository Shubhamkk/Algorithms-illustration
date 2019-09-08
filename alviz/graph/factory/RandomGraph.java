/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.graph.factory;

import alviz.base.graph.GraphClass;
import alviz.base.graph.BaseGraph;
import alviz.graph.Graph;
import alviz.graph.factory.MSTPrim.TopKEdges;
import alviz.graph.factory.MSTPrim.Vertex;
import alviz.graph.factory.MSTPrim.ncEdge;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author baskaran
 */
public class RandomGraph {

    private BaseGraph graph;

    private int maxNodes;
    private int nodesCreated;
    private int totalFailedAttempts=10;

    private int width;
    private int height;
    private int nodeWidth;

    private int nodeSide;
    private int nodeArea;
    private int nodesAllowed=0;

    private int edgeSpacing;

    private int effWidth;
    private int effHeight;

    private int rows;
    private int cols;

    private int cellWidth;
    private int cellHeight;
    private int cellArea;
    private int nodesPerCell=0;

    private Cell cell[][];

    private Vertex vertexArray[];
    private TopKEdges topKEdges[];

    private boolean debug=false;

    public RandomGraph(BaseGraph graph, int xmaxnodes, int xwidth, int xheight, int xnodeWidth, boolean debug) {
        this.graph = graph;
        this.debug = debug;
        this.maxNodes = xmaxnodes;
        this.nodesCreated = 0;
        // failures cannot be more than half the number of nodes.
        this.totalFailedAttempts = maxNodes/2;

        this.width = xwidth;
        this.height = xheight;

        this.nodeWidth = xnodeWidth;

        // the factor 2 allows for edges to exist...
        // between 2 nodes the gap will be one node wide
        this.nodeSide = nodeWidth * 2;
        this.nodeArea = nodeSide*nodeSide;

        // if maxnodes is GT nodesAllowed then the graph will be very dense
        // edges will not be visible, nodes will hide the edges.
        this.nodesAllowed = width*height/nodeArea;

        // leave free space at the edges of display area
        this.edgeSpacing = nodeWidth;

        // effective display area
        this.effWidth  = width - 2 * edgeSpacing;
        this.effHeight = height - 2 * edgeSpacing;

        // compute rows & cols in the ratio of width & height
        // keep the smaller dimension to be 10 parts
        int parts = 10;
        if (width > height) {
            rows = parts;
            cols = (int) (parts*width/height);
        }
        else {
            cols = parts;
            rows = (int)(parts*height/width);
        }

        // cell dimensions
        this.cellWidth = effWidth / cols;
        this.cellHeight = effHeight / rows;

        this.cellArea = cellWidth * cellHeight;
        this.nodesPerCell = cellArea / nodeArea;

        this.cell = null;
        this.vertexArray = null;
    }

    public BaseGraph createMST(boolean fullyConnected) {

        if (nodesAllowed < maxNodes) {
            // throw DenseGraphException
        }

        if (nodesPerCell > 0) {
            initCells();
            generateVertex();
            if (debug) print();
            assignVertexId();
            if (!fullyConnected) assignNeighbours();
            createVertexArray();
            buildMST(fullyConnected, false);
            return buildGraph();
        }

        return null;
    }

    public BaseGraph createcompleteGraph(boolean fullyConnected, boolean triangulate) {

        if (nodesAllowed < maxNodes) {
            // throw DenseGraphException
        }

        if (nodesPerCell > 0) {
            initCells();
            generateVertex();
            if (debug) print();
            assignVertexId();
            if (!fullyConnected) assignNeighbours();
            createVertexArray();
            buildMST(fullyConnected, true);
            return (Graph) addEdgesToMST(buildcompleteGraph(), triangulate);
        }
        
        return null;
    }
    
    public BaseGraph createGraph(boolean fullyConnected, boolean triangulate) {

        if (nodesAllowed < maxNodes) {
            // throw DenseGraphException
        }

        if (nodesPerCell > 0) {
            initCells();
            generateVertex();
            if (debug) print();
            assignVertexId();
            if (!fullyConnected) assignNeighbours();
            createVertexArray();
            buildMST(fullyConnected, true);
            return addEdgesToMST(buildGraph(), triangulate);
        }
        
        return null;
    }

    private void initCells() {
        cell = new Cell[rows][cols];
        for (int i=0; i<rows; ++i) {
            for (int j=0; j<cols; ++j) {
                cell[i][j] = new Cell();
            }
        }
    }

    private void generateVertex() {
        Random rand = new Random(System.nanoTime());
        int failed=0;
        int n = maxNodes;
        while (n > 0 && failed < totalFailedAttempts) {
            int x = rand.nextInt(effWidth);
            int c = x/cellWidth;
            //if (x > c * cellWidth) ++c;
            if (c >= cols) c = cols-1;

            int y = rand.nextInt(effHeight);
            int r = y/cellHeight;
            //if (y > r * cellHeight) ++r;
            if (r >= rows) r = rows-1;

            if (cell[r][c].vCount < nodesPerCell) {
                cell[r][c].vCount++;
                --n;
                ++nodesCreated;
                cell[r][c].vList.add(new Vertex(-1, x+edgeSpacing, y+edgeSpacing));
            }
            else {
                ++failed;
            }
        }
        //print();
    }

    private void assignVertexId() {
        int vid=0;
        for (int i=0; i<rows; ++i) {
            for (int j=0; j<cols; ++j) {
                for (Vertex v : cell[i][j].vList) {
                    v.id = vid++;
                }
            }
        }
    }

    private void assignNeighbours() {
        for (int i=0; i<rows; ++i) {
            for (int j=0; j<cols; ++j) {
                List<Vertex> vlist = cell[i][j].vList;
                // top row
                addNeighbours(vlist, i-1, j-1);
                addNeighbours(vlist, i-1, j);
                addNeighbours(vlist, i-1, j+1);
                // middle row
                addNeighbours(vlist, i, j-1);
                addNeighbours(vlist, i, j); // self
                addNeighbours(vlist, i, j+1);
                // bottom row
                addNeighbours(vlist, i+1, j-1);
                addNeighbours(vlist, i+1, j);
                addNeighbours(vlist, i+1, j+1);
            }
        }
    }
    private void addNeighbours(List<Vertex> vlist, int ni, int nj) {
        if (0 <= ni && ni < rows && 0 <= nj && nj < cols) {
            addNeighbours(vlist, cell[ni][nj].vList);
        }
    }
    private void addNeighbours(List<Vertex> vlist, List<Vertex> nlist) {
        for (Vertex v : vlist) {
            v.addNeighbour(nlist);
        }
    }

    private void createVertexArray() {
        vertexArray = new Vertex[nodesCreated];
        for (int i=0; i<nodesCreated; ++i) vertexArray[i] = null;

        int vAssigned=0;
        for (int i=0; i<rows; ++i) {
            for (int j=0; j<cols; ++j) {
                for (Vertex v : cell[i][j].vList) {
                    if (vertexArray[v.id] == null) {
                        vertexArray[v.id] = v;
                        ++vAssigned;
                    }
                }
            }
        }

        if (vAssigned != nodesCreated) {
            // throw InternalError
        }
    }

    private void buildMST(boolean fullyConnected, boolean topKEdgesFlag) {
        MSTPrim mst = new MSTPrim(vertexArray, topKEdgesFlag);
        if (fullyConnected) {
            mst.run2();
        }
        else {
            mst.run();
        }
        if (topKEdgesFlag) {
            topKEdges = mst.getTopKEdges();
        }
        if (debug) mst.print();
    }

    private BaseGraph buildGraph() {

        // create graph
        BaseGraph g = (BaseGraph) graph.createGraph(nodesCreated);

        // create nodes
        for (int i=0; i<nodesCreated; ++i) {
            Vertex v = vertexArray[i];
            g.createNode(v.x, v.y);
        }

        // create edges
        List<BaseGraph.Node> nodes = g.getNodes();
        for (int i=0; i<nodesCreated; ++i) {
            Vertex v = vertexArray[i];
            int parent = v.parent;
            if (parent >= 0) {
                BaseGraph.Node vn = nodes.get(v.id);
                BaseGraph.Node un = nodes.get(parent);
                g.createEdge(vn, un);
            }
        }

        return g;
    }
    
    private BaseGraph buildcompleteGraph() {

        // create graph
        Graph g = new Graph(nodesCreated);

        // create nodes
        for (int i=0; i<nodesCreated; ++i) {
            Vertex v = vertexArray[i];
            g.createNode(v.x, v.y);
        }

        // create edges
        List<Graph.Node> nodes = g.getNodes();
        for (int i=0; i<nodesCreated; ++i) {
            Vertex v = vertexArray[i];
            for(int j=i+1;j<nodesCreated;j++)
            {
            	 Graph.Node vn = nodes.get(v.id);
                 Graph.Node un = nodes.get(vertexArray[j].id);
                 g.createEdge(vn, un);
            }
            
        }

        return g;
    }

    private BaseGraph addEdgesToMST(BaseGraph g, boolean triangulate) {

        int[] line = new int[4];
        
        List<BaseGraph.Node> gnodes = g.getNodes();
        for (int i=0; i<nodesCreated; ++i) {
            Vertex v = vertexArray[i];
            BaseGraph.Node gv = gnodes.get(i);

            //System.out.printf("%4d u vertex=(%d,x=%d,y=%d) node=(%d,x=%d,y=%d)\n",i, v.id, v.x, v.y, gv.getId(), gv.x, gv.y);

            TopKEdges ke = topKEdges[i];
            ncEdge ncEdges[] = ke.getEdges();
            int ncSize = ke.getEdgeSize();
            for (int e=0; e<ncSize; ++e) {
                ncEdge nce = ncEdges[e];
                if (!gv.isNeighbour(nce.n.id)) {
                    line[0] = gv.x;
                    line[1] = gv.y;
                    line[2] = nce.n.x;
                    line[3] = nce.n.y;
                    if (!g.edgeCrossing(line)) {
                        Vertex n = nce.n;
                        BaseGraph.Node gn = gnodes.get(nce.n.id);
                        //System.out.printf("     n vertex=(%d,x=%d,y=%d) node=(%d,x=%d,y=%d)\n",n.id, n.x, n.y, gn.getId(), gn.x, gn.y);
                        BaseGraph.Edge ge = g.createEdge(gv, gn);
                        //ge.setOpen();
                        if (!triangulate) {
                            break;
                        }
                    }
                }
            }
        }
        return g;
    }

    public void print() {
        
        System.out.printf("maxNodes=%d\n",maxNodes);
        System.out.printf("totalFailedAttempts=%d\n",totalFailedAttempts);

        System.out.printf("width=%d\n",width);
        System.out.printf("height=%d\n",height);
        System.out.printf("nodeWidth=%d\n",nodeWidth);

        System.out.printf("nodeSide=%d\n",nodeSide);
        System.out.printf("nodeArea=%d\n",nodeArea);
        System.out.printf("nodesAllowed=%d\n",nodesAllowed);

        System.out.printf("edgeSpacing=%d\n",edgeSpacing);
        System.out.printf("effWidth=%d\n",effWidth);
        System.out.printf("effHeight=%d\n",effHeight);

        System.out.printf("rows=%d\n",rows);
        System.out.printf("cols=%d\n",cols);

        System.out.printf("cellWidth=%d\n",cellWidth);
        System.out.printf("cellHeight=%d\n",cellHeight);
        System.out.printf("cellArea=%d\n",cellArea);
        System.out.printf("nodesPerCell=%d\n",nodesPerCell);
        System.out.printf("nodesCreated=%d\n",nodesCreated);

        if (cell != null) {
            System.out.printf("matrix: cell(%s, %s)\n", rows, cols);
            int total=0;
            for (int i=0; i<rows; ++i) {
                for (int j=0; j<cols; ++j) {
                    if (j > 0) System.out.printf("\t%d", cell[i][j].vCount);
                    else System.out.printf("%d", cell[i][j].vCount);
                    total += cell[i][j].vCount;
                }
                System.out.printf("\n");
            }
            System.out.printf("Total points = %d\n", total);
        }
    }

    static private class Cell {
        public int vCount;
        public List<Vertex> vList;
        public Cell() {
            vCount = 0;
            vList = new LinkedList<Vertex>();
        }
    }

    static public class Tester {
        static public void main(String args[]) {
            BaseGraph bg = GraphFactory.createGraph(GraphClass.GRAPH);
            RandomGraph g = new RandomGraph(bg,1000, 600, 600, 6, true);
            g.createGraph(true, false);
            g.print();
        }
    }
}
