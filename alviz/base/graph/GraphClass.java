/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.base.graph;

/**
 *
 * @author baskaran
 */
public enum GraphClass {
    GRAPH("graph")
    ;

    private String gcname;
    private GraphClass(String name) {
        gcname = name;
    }
    @Override
    public String toString() {
        return gcname;
    }
}
