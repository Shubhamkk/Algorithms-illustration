/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.base.algorithm;

import alviz.algorithm.*;
import alviz.base.graph.BaseGraph;
import alviz.graph.*;
import alviz.util.PaintDelay;
import javax.swing.JPanel;

/*
 *
 * @author baskaran
 */
public class AlgorithmFactory {

    private AlgorithmFactory() {}

    static public Algorithm createAlgorithm(final JPanel graphCanvas, AlgorithmType algoType, BaseGraph graph, boolean stepThrough, PaintDelay refreshRate) {
        
        Algorithm al = null;

        switch (algoType) {
            case BFS:
                al = new BFS((Graph)graph) {
                    @Override
                    public void paint() { graphCanvas.repaint(); }
                };
                break;
            case DFS:
                al = new DFS((Graph)graph) {
                    @Override
                    public void paint() { graphCanvas.repaint(); }
                };
                break;
            case DFID:
                al=new DFID((Graph)graph){
                   @Override
                   public void paint() { graphCanvas.repaint(); }
                };
                break;
            case IDA:
                al=new IDA((Graph)graph){
                   @Override
                   public void paint() { graphCanvas.repaint(); }
                };
                
            default:
                //System.out.println("createAlgorithm> null");
        }

        if (al != null) {
            al.InitAlgorithmParameters(stepThrough, refreshRate);
        }
        return al;
    }
}
