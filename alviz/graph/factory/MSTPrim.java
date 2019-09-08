/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.graph.factory;

import alviz.util.Heap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author baskaran
 */
public class MSTPrim {

    static private int topK = 5;

    private Vertex vList[];

    private boolean topKEdgesFlag;
    private TopKEdges topkEdges[];
    public TopKEdges[] getTopKEdges() { return topkEdges; }

    private int qNodes;
    private Heap qHeap;
    private boolean debug;

    public MSTPrim(Vertex vList[], boolean topKEdgesFlag) {
        this.vList = vList;
        this.topKEdgesFlag = topKEdgesFlag;
        if (topKEdgesFlag) {
            this.topkEdges = new TopKEdges[vList.length];
            for (int i=0; i<vList.length; ++i) {
                topkEdges[i] = new TopKEdges(topK);
            }
        }
        this.qNodes = 0;
        this.qHeap = null;
        this.debug = false;
    }
    public MSTPrim(Vertex vList[], boolean topKEdgesFlag, boolean debug) {
        this(vList, topKEdgesFlag);
        this.debug = debug;
    }

    private void init() {
        // all nodes are in Q
        qNodes = vList.length;

        // create min heap
        qHeap = new Heap(qNodes, new Heap.ComparatorLT());

        // initialize parent, key and Q_flag
        // populate the heap
        for (int i=0; i<qNodes; ++i) {
            Vertex v = vList[i];
            v.setInQTrue();
            v.parent = -1;
            if (i > 0) {
                v.key = Integer.MAX_VALUE;
            }
            else {
                v.key = 0;
            }
            qHeap.add(v);
        }

        // build heap
        qHeap.build();
        if (debug) qHeap.print();
    }

    private Vertex extractMin() {
        Vertex u = (Vertex) qHeap.extractRoot();
        if (u != null) {
            vList[u.id].setInQFalse();
            --qNodes;
        }
        return u;
    }

    private int euclideanDistance(Vertex u, Vertex v) {
        int dx = u.x - v.x;
        int dy = u.y - v.y;
        return (int)Math.sqrt(dx*dx+dy*dy);
    }

    public void run() {

        init();

        while(qNodes > 0) {
            Vertex u = extractMin();
            List<Vertex> neighbours = u.getNeighbours();
            if (neighbours != null) {
                for (Vertex v : neighbours) {
                    if (v.isInQ())  {
                        int w = euclideanDistance(u, v);
                        //addTopKEdge(u, v, w);
                        if(w < v.key) {
                            v.parent = u.id;
                            qHeap.changeKey(v, w);
                        }
                        else {
                            addTopKEdge(u, v, w);
                        }
                    }
                }
            }
        }
    }

    public void run2() {

        init();

        int loopCounter=0;
        while(qNodes > 0) {
            ++loopCounter;
            Vertex u = extractMin();
            for (int i=0; i<vList.length; ++i) {
                Vertex v = vList[i];
                if (u != v && v.isInQ()) {
                    int w = euclideanDistance(u, v);
                    int keyold=v.key;
                    if(w < v.key) {
                        v.parent = u.id;
                        qHeap.changeKey(v, w);
                        if (debug) {
                            System.out.printf("%3d u(%d,x=%d,y=%d,p=%d,key=%d)\n", loopCounter, u.id, u.x, u.y, u.parent, u.key);
                            System.out.printf("    v(%d,x=%d,y=%d,p=%d,key=%d,keyOld=%d)\n", v.id, v.x, v.y, v.parent, v.key, keyold);
                            qHeap.print();
                            System.out.printf("    ---v\n");
                        }
                    }
                    else {
                        addTopKEdge(u, v, w);
                    }
                }
            }
            
            if (debug) {
                qHeap.print();
                System.out.printf("---u\n");
            }
        }
    }

    private void addTopKEdge(Vertex u, Vertex v, int cost) {
        if (topKEdgesFlag) topkEdges[u.id].add(v, cost);
    }

    public void print() {
        System.out.printf("MST Prim nodes=%d\n", vList.length);
        for (int i=0; i<vList.length; ++i) {
            Vertex v = vList[i];
            System.out.printf("%4d %4d (x=%d,y=%d) P=%4d  key=%d  inQ=%s\n", i, v.id, v.x, v.y, v.parent, v.key, v.inQ);
        }
        System.out.println();
        if (topKEdgesFlag) {
            System.out.printf("top %d edges[%d] \n", topK, vList.length);
            for (int i=0; i<vList.length; ++i) {
                Vertex v = vList[i];
                TopKEdges ke = topkEdges[i];
                for (int j=0; j<ke.size; ++j) {
                    ncEdge ne = ke.ncList[j];
                    Vertex n = ne.n;
                    int cost = ne.cost;
                    System.out.printf("%4d %4d (x=%d,y=%d) n:%4d (x=%d,y=%d) cost=%d\n", i, v.id, v.x, v.y, n.id, n.x, n.y, cost);
                }
            }
            System.out.println();
        }
    }

    static public class Vertex implements Heap.Element {

        public int getKey() { return key; }
        public void setKey(int key) { this.key = key; }

        public int heapId=0;
        public int getHeapId() { return heapId; }
        public void setHeapId(int heapId) { this.heapId = heapId; }

        public int id;
        public int x;
        public int y;

        public int parent;
        public int key;

        public boolean inQ;
        public void setInQFalse() { inQ = false; }
        public void setInQTrue() { inQ = true; }
        public boolean isInQ() { return inQ; }

        public List<Vertex> neighbours;

        public Vertex(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
            parent = -1;
            key = Integer.MAX_VALUE;
            inQ = true; // all vertices are considered to be in Q to start with.
            neighbours = null; // lazy initialization
        }

        public void addNeighbour(Vertex n) {
            // no self loops allowed
            if (this == n) return;

            if (neighbours == null) {
                neighbours = new LinkedList<Vertex>();
            }
            if (!neighbours.contains(n)) {
                neighbours.add(n);
            }
        }

        public void addNeighbour(List<Vertex> nList) {
            if (neighbours == null) {
                neighbours = new LinkedList<Vertex>();
            }
            neighbours.addAll(nList);
            // remove self loop
            neighbours.remove(this);
        }

        public List<Vertex> getNeighbours() { return neighbours; }

        @Override
        public String toString() {
            return String.format("[id=%d,x=%d,y=%d,p=%d,key=%d]", id, x, y, parent, key);
        }
    }
    
    static public class ncEdge {
        public Vertex n;
        public int cost;
        public ncEdge(Vertex n, int cost) {
            this.n = n;
            this.cost = cost;
        }
    }

    static public class TopKEdges {
        private int size;
        private ncEdge ncList[];

        public ncEdge[] getEdges() { return ncList; }
        public int getEdgeSize() { return size; }

        public TopKEdges(int k) {
            size = 0;
            ncList = new ncEdge[k];
            for (int i=0; i<k; ++i) {
                ncList[i] = null;
            }
        }

        public void add(Vertex n, int cost) {

            int j = -1;
            for (int i=0; i<size; ++i) {
                if (cost <= ncList[i].cost) {
                    j = i;
                    break;
                }
            }

            if (j >= 0) {
                if (size < ncList.length) {
                    // move the elements 1 element right
                    ++size;
                    for (int i=size-1; i>j; --i) {
                        ncList[i]= ncList[i-1];
                    }
                    ncList[j] = new ncEdge(n, cost);
                }
                else {
                    for (int i=size-1; i>j; --i) {
                        ncList[i].n = ncList[i-1].n;
                        ncList[i].cost = ncList[i-1].cost;
                    }
                    ncList[j].n = n;
                    ncList[j].cost = cost;
                }
            }
            else {
                if (size < ncList.length) {
                    ncList[size] = new ncEdge(n, cost);
                    ++size;
                }
            }
        }

    }

}
