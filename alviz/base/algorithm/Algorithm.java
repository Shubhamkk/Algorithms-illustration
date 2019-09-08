/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.base.algorithm;

import alviz.util.PaintDelay;
import java.util.concurrent.locks.LockSupport;

/**
 *
 * @author baskaran
 */
abstract public class Algorithm implements Runnable {

    public enum ExecState {
        INITIALIZED,
        RUNNING,
        ENDED,
        TERMINATED;
    }

    // refresh rate (painting rate)
    private PaintDelay refreshRate;

    // step through or run with delay
    private boolean stepThrough=true;
    public void setRunnableTrue() { stepThrough = false; }
    public void setStepThroughTrue() { stepThrough = true; }

    private ExecState execState;
    public boolean hasEnded() {
        switch (execState) {
            case ENDED:
            case TERMINATED:
                return true;
            default:
                return false;
        }
    }
    public void setStateRunning() { execState = ExecState.RUNNING; }
    public void setStateEnded() { execState = ExecState.ENDED; }
    public void setStateTerminated() { execState = ExecState.TERMINATED; }

    public Algorithm() {
        this.execState = ExecState.INITIALIZED;
        this.refreshRate = null;
        this.stepThrough = false;
    }

    void InitAlgorithmParameters(boolean stepThrough, PaintDelay refreshRate) {
        this.stepThrough = stepThrough;
        this.refreshRate = refreshRate;
    }

    // to be implemented by the derived classes...
    abstract protected void execute() throws Exception;

    // implementation specific code do not overwrite...
    public void paint() {}
    final public void run() {
        try {
            setStateRunning();
            execute();
            setStateEnded();
            return;
        }
        catch(InterruptedException e) {
            setStateTerminated();
        }
        catch(Exception e) {
            e.printStackTrace();
            setStateTerminated();
        }
    }
    final public void show() throws Exception {
        paint();
        if (!hasEnded()) {
            if (stepThrough) {
                LockSupport.park(this);
            }
            else {
                Thread.sleep(refreshRate.getDelay());
            }
        }
    }

}
