/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.util;

import java.util.Comparator;

/**
 *
 * @author baskaran
 */
public class Heap {

    private int size;
    private int capacity;
    /**
     * heap[0] is not used because of 1 based indexing on the array
     * instead used as working storage (and is not part of original data).
     */
    private Element[] heap;
    private Comparator<Element> comparator;

    public Heap(int capacity, Comparator comparator) {
        this.size = 0;
        this.capacity = capacity;
        this.heap = new Element[capacity+1];
        // not used because of 1 based indexing
        // instead used as working storage
        heap[0] = new ElementImpl(0);
        this.comparator = comparator;
    }

    public void add(Element e) {
        if (size < capacity) {
            heap[++size] = e;
            e.setHeapId(size);
        }
        else {
            // throw HeapOverflowException
        }
    }

    public Element get(int i) {
        if (i > 0 && i <= size) {
            return heap[i];
        }
        return null;
    }
    public Element getLast() {
        if (size > 0) {
            return heap[size];
        }
        return null;
    }
    public int getSize() { return size; }

    private void swap(int a, int b) {
        Element tmp = heap[a];
        heap[a] = heap[b];
        heap[b] = tmp;

        // reset heap id of the elements
        heap[a].setHeapId(a);
        heap[b].setHeapId(b);
    }

    public void heapify(int i) {
        int left    = i<<1;
        int right   = left + 1;
        int largest = i;

        if (left <= size && comparator.compare(heap[left], heap[i]) > 0) {
            largest = left;
        }
        if (right <= size && comparator.compare(heap[right], heap[largest]) > 0) {
            largest = right;
        }
        if (largest != i) {
            swap(largest, i);
            heapify(largest);
        }
    }

    public void build() {
        for (int i=size>>1; i>0; --i) {
            heapify(i);
        }
    }

    public Element root() { return heap[1]; }

    public Element extractRoot_old() {
        Element root = null;
        if (size > 0) {
            root = heap[1];
            heap[1] = heap[size];
            --size;
            heapify(1);
        }
        return root;
    }

    public Element extractRoot() {
        return delete(1);
    }

    private Element delete(int i) {
        Element e = null;
        if (size > 0) {
            e = heap[i]; //e.setHeapId(-1);
            heap[i] = heap[size];
            --size;
            heapify(i);
        }
        return e;
    }

    public void delete(Element e) {
        delete(e.getHeapId());
    }

    private void changeKey(int i, int key) {
        if (i > 0 && i <= size) {
            Element ws = heap[0];
            ws.setKey(key);
            if (comparator.compare(ws, heap[i]) > 0) {
                heap[i].setKey(key);
                int parent = i >> 1;
                while ( i > 1 && comparator.compare(heap[parent], heap[i]) <= 0) {
                    swap(parent, i);
                    i = parent;
                    parent = i >> 1;
                }
            }
            else {
                heap[i].setKey(key);
                heapify(i);
            }
        }
        else {
            // throw HeapOutofBoundsException
        }
    }

    public void changeKey(Element e, int key) {
        changeKey(e.getHeapId(), key);
    }

        public void print() {
        System.out.printf("heap size = %d, capacity = %d\n", size, capacity);
        for (int i=1; i<=size; ++i) {
            int left  = i << 1;
            int right = left + 1;

            String leftstr = (left <= size ? heap[left].toString() : "null");
            String rightstr= (right<= size ? heap[right].toString() : "null");

            System.out.printf("%d  (%s  %s  %s)\n", i, heap[i].toString(), leftstr, rightstr);
        }
        System.out.println();
    }

    static public interface Element {
        public int getKey();
        public void setKey(int key);

        public int getHeapId();
        public void setHeapId(int heapId);
    }

    /**
     * needed for using comparator in changeKey() function
     */
    static private class ElementImpl implements Element {
        int key=0;
        int heapId=-1;
        public ElementImpl(int key) {
            this.key = key;
        }
        @Override
        public String toString() { return String.valueOf(key); }

        public int getKey() { return key; }
        public void setKey(int key) { this.key = key; }

        public int getHeapId() { return heapId; }
        public void setHeapId(int heapId) { this.heapId = heapId; }
    }

    static public class ComparatorLT implements Comparator<Element> {
        /**
         * if (e1.key LT e2.key) then result GT zero
         * @param e1
         * @param e2
         * @return
         */
        public int compare(Element e1, Element e2) {
            return e2.getKey() - e1.getKey();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ComparatorLT;
        }
    }

    static public class ComparatorGT implements Comparator<Element> {
        /**
         * if (e1.key GT e2.key) then result GT zero
         * @param e1
         * @param e2
         * @return
         */
        public int compare(Element e1, Element e2) {
            return e1.getKey() - e2.getKey();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ComparatorGT;
        }
    }

    static public class Tester {
        static public void main(String args[]) {
            test2();
            test1();
        }

        static private void test2() {
            int size = 10;
            Heap h = new Heap(size, new Heap.ComparatorLT());
            int key=size * 10;
            for (int i=0; i<size; ++i) {
                ElementImpl e = new ElementImpl(key--);
                h.add(e);
            }
            System.out.printf("[heap initialization]\n");
            h.print();

            h.build();
            System.out.printf("[heap build]\n");
            h.print();

            System.out.printf("[decrease first]\n");
            h.changeKey(1, h.get(1).getKey()-20);
            h.print();

            System.out.printf("[decrease last]\n");
            h.changeKey(h.getSize(), h.get(h.getSize()).getKey()-20);
            h.print();

            System.out.printf("[increase first]\n");
            h.changeKey(1, h.get(1).getKey()+100);
            h.print();

            System.out.printf("[increase last]\n");
            h.changeKey(h.getSize(), h.get(h.getSize()).getKey()+100);
            h.print();

            System.out.printf("[delete all]\n");
            h.print();
            System.out.println();
            for (int n = h.getSize(); n > 0; n = h.getSize()) {
                int i = (1+n)/2;
                System.out.printf("delete(%d) %s\n", i, h.delete(i).toString());
                h.print();
                System.out.println();
            }
            System.out.println();

            System.out.printf("delete extract all]\n");
            h.print();
        }

        static private void test1() {
            int size = 10;
            Heap h = new Heap(size, new Heap.ComparatorLT());
            int key=size * 10;
            for (int i=0; i<size; ++i) {
                ElementImpl e = new ElementImpl(key--);
                h.add(e);
            }
            System.out.printf("[heap initialization]\n");
            h.print();

            h.build();
            System.out.printf("[heap build]\n");
            h.print();

            System.out.printf("[decrease first]\n");
            h.changeKey(1, h.get(1).getKey()-20);
            h.print();

            System.out.printf("[decrease last]\n");
            h.changeKey(h.getSize(), h.get(h.getSize()).getKey()-20);
            h.print();

            System.out.printf("[increase first]\n");
            h.changeKey(1, h.get(1).getKey()+100);
            h.print();

            System.out.printf("[increase last]\n");
            h.changeKey(h.getSize(), h.get(h.getSize()).getKey()+100);
            h.print();

            System.out.printf("[extract all]\n");
            for (Element e=h.extractRoot(); e != null; e=h.extractRoot()) {
                System.out.printf("  %s", e.toString());
            }
            System.out.println();

            System.out.printf("[after extract all]\n");
            h.print();
        }

    }
}
