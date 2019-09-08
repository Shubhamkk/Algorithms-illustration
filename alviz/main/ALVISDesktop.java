/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ALVISDesktop.java
 *
 * Created on Aug 25, 2009, 6:47:18 PM
 */

package alviz.main;

import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author baskaran
 */
public class ALVISDesktop extends javax.swing.JFrame {

    private Application app;

    /** Creates new form ALVISDesktop */
    public ALVISDesktop() {
        //super();
        initComponents();

        app = Application.getInstance((Integer)sizeSpinner.getValue());

        jToolBar1.setFloatable(false);

        addKeyListener(graphCanvas1); // graph panel listens to key strokes in ALVISDesktop
        graphCanvas1.addKeyListener(graphCanvas1); // graph panel listens to its own key strokes
        graphCanvas1.addMouseListener(graphCanvas1); // graph panel listens to its own mouse moves

        graphCanvas1.setApp(app);

        refreshRateValueLabel.setText(app.refreshRate.setDelay(refreshRateSlider.getValue()));
        app.branchingFactor = Integer.valueOf((String)branchingFactorComboBox.getItemAt(branchingFactorComboBox.getSelectedIndex()));
        app.density = Integer.valueOf((String)densityComboBox.getItemAt(densityComboBox.getSelectedIndex()));
        app.densityMax = Integer.valueOf((String)densityComboBox.getItemAt(densityComboBox.getItemCount()-1));

        enableComponents();
    }

    private void enableComponents() {
        // based on current state enable/disable components
        switch (app.execState) {
            case APPLICATION_STARTED: {

                fileMenu.setEnabled(true);
                saveGraphButton.setEnabled(false); // <TODO>
                algoMenu.setEnabled(true);
                    algoBFSButton.setEnabled(true);        // enabled after integration
                    algoDFSButton.setEnabled(true);
                    algoDFIDButton.setEnabled(true);
                    algoIDAButton.setEnabled(true);
                    // enabled after integration
                sizeLabel.setEnabled(false);
                sizeSpinner.setEnabled(false);
                branchingFactorLabel.setEnabled(false);
                branchingFactorComboBox.setEnabled(false);
                densityLabel.setEnabled(false);
                densityComboBox.setEnabled(false);

                graphMenu.setEnabled(false);
                readGraphButton.setEnabled(false); // <TODO>
                startNodeButton.setEnabled(false);
                goalNodesButton.setEnabled(false);

                stepThroughButton.setEnabled(false);
                runButton2.setEnabled(false);
                stopButton2.setEnabled(true);
                pipeButton.setEnabled(false);
            }
            break;
            case ALGO_SELECTED: {
                algoMenu.setEnabled(false);
                if (app.isPipeMode()) {
                    stepThroughButton.setEnabled(true);
                    runButton2.setEnabled(true);
                }
                else {
                    graphMenu.setEnabled(true);
                    /*if (app.isGameTree()) {
                        branchingFactorLabel.setEnabled(true);
                        branchingFactorComboBox.setEnabled(true);
                        densityLabel.setEnabled(true);
                        densityComboBox.setEnabled(true);

                        algoBFSButton.setEnabled(false);
                        algoDFSButton.setEnabled(false);
                        

                        graphGameTree_1Button.setEnabled(true);
                        graphGameTree_2Button.setEnabled(true);
                        graphGameTree_3Button.setEnabled(true);

                        graphSparseButton.setEnabled(false);
                        graphDenseButton.setEnabled(false);
                        graphTreeButton.setEnabled(false);

                        graphGrid_1Button.setEnabled(false);
                        graphGrid_2Button.setEnabled(false);
                        graphGrid_3Button.setEnabled(false);
                        graphGrid_4Button.setEnabled(false);
                        graphGrid_1MSTButton.setEnabled(false);
                        graphGrid_2MSTButton.setEnabled(false);
                        graphGrid_3MSTButton.setEnabled(false);
                        graphGrid_4MSTButton.setEnabled(false);

                    }*/
                    //else {
                        sizeLabel.setEnabled(true);
                        sizeSpinner.setEnabled(true);

                        branchingFactorLabel.setEnabled(true);
                        branchingFactorComboBox.setEnabled(true);

                        densityLabel.setEnabled(true);
                        densityComboBox.setEnabled(true);

                        //algoMinMaxButton.setEnabled(false);
                        //algoAlphaBetaButton.setEnabled(false);
                        //algoSSS_StarButton.setEnabled(false);

                        graphGameTree_1Button.setEnabled(true);
                        graphGameTree_2Button.setEnabled(true);
                        graphGameTree_3Button.setEnabled(true);

                        graphSparseButton.setEnabled(true);
                        graphDenseButton.setEnabled(true);
                        graphTreeButton.setEnabled(true);

                        graphGrid_1Button.setEnabled(true);
                        graphGrid_2Button.setEnabled(true);
                        graphGrid_3Button.setEnabled(true);
                        graphGrid_4Button.setEnabled(true);
                        graphGrid_1MSTButton.setEnabled(true);
                        graphGrid_2MSTButton.setEnabled(true);
                        graphGrid_3MSTButton.setEnabled(true);
                        graphGrid_4MSTButton.setEnabled(true);
                    //}
                }
            }
            break;
            case SIZE_SELECTED: {
            }
            break;
            case SEARCH_SPACE_LOADED: {
                graphMenu.setEnabled(false);
               /* if (app.isGameTree()) {
                    branchingFactorLabel.setEnabled(false);
                    branchingFactorComboBox.setEnabled(false);
                    densityLabel.setEnabled(false);
                    densityComboBox.setEnabled(false);
                    stepThroughButton.setEnabled(true);
                    runButton2.setEnabled(true);
                }*/
                //else {
                    sizeLabel.setEnabled(false);
                    sizeSpinner.setEnabled(false);
                    branchingFactorLabel.setEnabled(false);
                    branchingFactorComboBox.setEnabled(false);
                    densityLabel.setEnabled(false);
                    densityComboBox.setEnabled(false);

                    startNodeButton.setEnabled(true);
                //}
            }
            break;
            case START_NODE_APPLIED: {
                startNodeButton.setEnabled(false);
                goalNodesButton.setEnabled(true);
            }
            break;
            case GOAL_NODE_APPLIED: {
                goalNodesButton.setEnabled(false);
                stepThroughButton.setEnabled(true);
                runButton2.setEnabled(true);
            }
            break;
            case STEPPING_THROUGH: {
                stepThroughButton.setEnabled(true);
            }
            break;
            case RUNNING: {
                stepThroughButton.setEnabled(false);
                runButton2.setEnabled(false);
            }
            break;
            case STOPPED: {
                if (app.isSearchSpaceLoaded()) {
                    startNodeButton.setEnabled(true);
                }
                else {
                    algoMenu.setEnabled(true);
                }

                stepThroughButton.setEnabled(false);
                runButton2.setEnabled(false);
                pipeButton.setEnabled(true);
            }
            break;
            case PIPE_ALGO: {
                algoMenu.setEnabled(true);
                startNodeButton.setEnabled(false);
                goalNodesButton.setEnabled(false);

                stepThroughButton.setEnabled(false);
                runButton2.setEnabled(false);
                pipeButton.setEnabled(false);
            }
            break;
        }
    }

    private void newButtonActionHelper(java.awt.event.ActionEvent evt) {
        app.resetApp();
        enableComponents();
        graphCanvas1.repaint();
    }

    private void refreshRateSliderStateChangedHelper(javax.swing.event.ChangeEvent evt) {
        // TODO add your handling code here:
        JSlider source = (JSlider)evt.getSource();
        //if (!source.getValueIsAdjusting()) {
            int fps = (int)source.getValue();
            refreshRateValueLabel.setText(app.refreshRate.setDelay(fps));
        //}
    }

    private void algoActionPerformedHelper(java.awt.event.ActionEvent evt) {
        if (app.selectAlgo(evt.getActionCommand())) {
            enableComponents();
            if (app.isPipeMode()) {
                graphCanvas1.repaint();
            }
        }
    }

    private void setGraphSize() {
        app.setDisplayDimension(graphCanvas1.getWidth(), graphCanvas1.getHeight());
    }

    private void sizeSpinnerStateChangedHelper(javax.swing.event.ChangeEvent evt) {
        if (app.selectSize((Integer) sizeSpinner.getValue())) {
            enableComponents();
            //graphPanel1.repaint();
        }
    }

    private void branchingFactorComboBoxActionPerformedHelper(java.awt.event.ActionEvent evt) {
        if (app.selectBranchingFactor((String)branchingFactorComboBox.getSelectedItem())) {
            enableComponents();
            //graphPanel1.repaint();
        }
    }

    private void densityComboBoxActionPerformedHelper(java.awt.event.ActionEvent evt) {
        JComboBox source = (JComboBox) evt.getSource();
        app.density = Integer.valueOf((String)source.getItemAt(source.getSelectedIndex()));
    }

    private void graphActionHelper(java.awt.event.ActionEvent evt) {
        this.setGraphSize();
        if(app.loadGraph(evt.getActionCommand())) {
            if (app.sizeUpdatable) {
                sizeSpinner.setValue(new Integer(app.nodeCount));
                app.sizeUpdatable = false;
            }
            enableComponents();
            graphCanvas1.repaint();
        }
    }

    private void startActionHelper(java.awt.event.ActionEvent evt) {
        if (app.assignStartNode()) {
            enableComponents();
            graphCanvas1.repaint();
        }
    }

    private void goalButtonActionHelper(java.awt.event.ActionEvent evt) {
        if (app.assignGoalNode()) {
            enableComponents();
            graphCanvas1.repaint();
        }
    }

    private void stepButtonActionHelper(java.awt.event.ActionEvent evt) {
        if (app.stepThroughAlgo(graphCanvas1)) {
            enableComponents();
            graphCanvas1.repaint();
            if (app.algo.hasEnded()) {
                stopButton2ActionPerformed(new ActionEvent(stopButton2, ActionEvent.ACTION_PERFORMED, stopButton2.getText()));
            }
        }
    }

    private void runButtonActionHelper(java.awt.event.ActionEvent evt) {
        if (app.runAlgo(graphCanvas1)) {
            enableComponents();
            graphCanvas1.repaint();
            if (app.algo.hasEnded()) {
                stopButton2ActionPerformed(new ActionEvent(stopButton2, ActionEvent.ACTION_PERFORMED, stopButton2.getText()));
            }
        }
    }

    private void stopButtonActionHelper(java.awt.event.ActionEvent evt) {
        if (app.stopAlgo()) {
            enableComponents();
            graphCanvas1.repaint();
        }
    }

    private void pipeActionHelper(java.awt.event.ActionEvent evt) {
        if (app.pipeAlgo(graphCanvas1)) {
            enableComponents();
            graphCanvas1.repaint();
        }
    }

    private void helpDemoButtonActionPerformedHelper(java.awt.event.ActionEvent evt) {
        final String message =
                "1. Press File>New to start a new demo.\n" +
                "2. Select Algorithm.\n" +
                "3. Enter number of nodes or branching factor.\n" +
                "4. Select graph.\n" +
                "5. Select one node on the graph and press Start button.\n" +
                "6. Select one or more nodes (holding shift key for multiple selections) and press Goal button.\n" +
                "7. Press Step Through or Run button to run demo.\n" +
                "8. Press Stop button\n" +
                "9. After pressing Stop button, press Pipe button to try another algorithm on the same graph.\n" +
                "10. Use File>Exit to Exit the application."
                ;
        final String title =
                "How to run demo?"
                ;

        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void helpCreditsButtonActionPerformedHelper(java.awt.event.ActionEvent evt) {
        final String message =
                "Baskaran Sankaranarayanan, IITM\n" +

                "TO DO: list <students, algorithms> details here\n"
                ;
        final String title =
                "Credits"
                ;

        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void helpAboutButtonActionPerformedHelper(java.awt.event.ActionEvent evt) {
        final String message =
                "AI Algorithm Visualization, Version 0.1, April 2010\n" +
                "Dept. of Computer Science & Engineering\n" +
                "Indian Institute of Technology Madras\n" +
                "Chennai, India\n"
                ;
        final String title =
                "About"
                ;

        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        sizeLabel = new javax.swing.JLabel();
        sizeSpinner = new javax.swing.JSpinner(new SpinnerNumberModel(1000, 2, 3000, 50));
        branchingFactorLabel = new javax.swing.JLabel();
        branchingFactorComboBox = new javax.swing.JComboBox();
        densityLabel = new javax.swing.JLabel();
        densityComboBox = new javax.swing.JComboBox();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        startNodeButton = new javax.swing.JButton();
        goalNodesButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        stepThroughButton = new javax.swing.JButton();
        runButton2 = new javax.swing.JButton();
        stopButton2 = new javax.swing.JButton();
        pipeButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        refreshRateLabel = new javax.swing.JLabel();
        refreshRateSlider = new javax.swing.JSlider();
        refreshRateValueLabel = new javax.swing.JLabel();
        graphCanvas1 = new alviz.main.GraphCanvas();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newButton = new javax.swing.JMenuItem();
        saveGraphButton = new javax.swing.JMenuItem();
        exit = new javax.swing.JMenuItem();
        algoMenu = new javax.swing.JMenu();
        algoBFSButton = new javax.swing.JMenuItem();
        algoDFSButton = new javax.swing.JMenuItem();
        algoDFIDButton = new javax.swing.JMenuItem();
        algoIDAButton = new javax.swing.JMenuItem();
        graphMenu = new javax.swing.JMenu();
        readGraphButton = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        graphGameTree_1Button = new javax.swing.JMenuItem();
        graphGameTree_2Button = new javax.swing.JMenuItem();
        graphGameTree_3Button = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        graphSparseButton = new javax.swing.JMenuItem();
        graphDenseButton = new javax.swing.JMenuItem();
        graphTreeButton = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JSeparator();
        graphGrid_1Button = new javax.swing.JMenuItem();
        graphGrid_2Button = new javax.swing.JMenuItem();
        graphGrid_3Button = new javax.swing.JMenuItem();
        graphGrid_4Button = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JSeparator();
        graphGrid_1MSTButton = new javax.swing.JMenuItem();
        graphGrid_2MSTButton = new javax.swing.JMenuItem();
        graphGrid_3MSTButton = new javax.swing.JMenuItem();
        graphGrid_4MSTButton = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpDemoButton = new javax.swing.JMenuItem();
        helpCreditsButton = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JSeparator();
        helpAboutButton = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar1.setRollover(true);

        sizeLabel.setText("Size");
        jToolBar1.add(sizeLabel);

        sizeSpinner.setMaximumSize(new java.awt.Dimension(100, 20));
        sizeSpinner.setMinimumSize(new java.awt.Dimension(50, 20));
        sizeSpinner.setPreferredSize(new java.awt.Dimension(60, 20));
        sizeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sizeSpinnerStateChanged(evt);
            }
        });
        jToolBar1.add(sizeSpinner);

        branchingFactorLabel.setText("  Branching Factor");
        jToolBar1.add(branchingFactorLabel);

        branchingFactorComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2", "3", "5", "10", "20" }));
        branchingFactorComboBox.setMaximumSize(new java.awt.Dimension(80, 20));
        branchingFactorComboBox.setMinimumSize(new java.awt.Dimension(50, 20));
        branchingFactorComboBox.setPreferredSize(new java.awt.Dimension(50, 20));
        branchingFactorComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                branchingFactorComboBoxActionPerformed(evt);
            }
        });
        jToolBar1.add(branchingFactorComboBox);

        densityLabel.setText("  Density");
        jToolBar1.add(densityLabel);

        densityComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8" }));
        densityComboBox.setSelectedIndex(3);
        densityComboBox.setMaximumSize(new java.awt.Dimension(80, 20));
        densityComboBox.setMinimumSize(new java.awt.Dimension(50, 20));
        densityComboBox.setPreferredSize(new java.awt.Dimension(50, 20));
        densityComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                densityComboBoxActionPerformed(evt);
            }
        });
        jToolBar1.add(densityComboBox);
        jToolBar1.add(jSeparator2);

        startNodeButton.setText("Start Node");
        startNodeButton.setFocusable(false);
        startNodeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        startNodeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        startNodeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startNodeButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(startNodeButton);

        goalNodesButton.setText("Goal Node(s)");
        goalNodesButton.setFocusable(false);
        goalNodesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        goalNodesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        goalNodesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goalNodesButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(goalNodesButton);
        jToolBar1.add(jSeparator3);

        stepThroughButton.setText("Step Through");
        stepThroughButton.setFocusable(false);
        stepThroughButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stepThroughButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        stepThroughButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepThroughButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(stepThroughButton);

        runButton2.setText("Run");
        runButton2.setFocusable(false);
        runButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        runButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        runButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(runButton2);

        stopButton2.setText("Stop");
        stopButton2.setFocusable(false);
        stopButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stopButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        stopButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(stopButton2);

        pipeButton.setText("Pipe");
        pipeButton.setFocusable(false);
        pipeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pipeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pipeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pipeButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(pipeButton);
        jToolBar1.add(jSeparator4);

        refreshRateLabel.setText("Delay");
        refreshRateLabel.setFocusable(false);
        jToolBar1.add(refreshRateLabel);

        refreshRateSlider.setMajorTickSpacing(1000);
        refreshRateSlider.setMaximum(5000);
        refreshRateSlider.setMinorTickSpacing(500);
        refreshRateSlider.setPaintTicks(true);
        refreshRateSlider.setPaintTrack(false);
        refreshRateSlider.setValue(4000);
        refreshRateSlider.setMaximumSize(new java.awt.Dimension(200, 25));
        refreshRateSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                refreshRateSliderStateChanged(evt);
            }
        });
        jToolBar1.add(refreshRateSlider);

        refreshRateValueLabel.setText("jLabel3");
        refreshRateValueLabel.setFocusable(false);
        refreshRateValueLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        refreshRateValueLabel.setMaximumSize(new java.awt.Dimension(80, 20));
        refreshRateValueLabel.setMinimumSize(new java.awt.Dimension(80, 20));
        refreshRateValueLabel.setPreferredSize(new java.awt.Dimension(80, 20));
        jToolBar1.add(refreshRateValueLabel);

        javax.swing.GroupLayout graphCanvas1Layout = new javax.swing.GroupLayout(graphCanvas1);
        graphCanvas1.setLayout(graphCanvas1Layout);
        graphCanvas1Layout.setHorizontalGroup(
            graphCanvas1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 833, Short.MAX_VALUE)
        );
        graphCanvas1Layout.setVerticalGroup(
            graphCanvas1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 363, Short.MAX_VALUE)
        );

        fileMenu.setText("File");

        newButton.setText("New");
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        fileMenu.add(newButton);

        saveGraphButton.setText("Save Graph <TO DO>");
        fileMenu.add(saveGraphButton);

        exit.setText("Exit");
        exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });
        fileMenu.add(exit);

        jMenuBar1.add(fileMenu);

        algoMenu.setText("Algorithm");

        algoBFSButton.setText("BFS");
        algoBFSButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                algoBFSButtonActionPerformed(evt);
            }
        });
        algoMenu.add(algoBFSButton);

        algoDFSButton.setText("DFS");
        algoDFSButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                algoDFSButtonActionPerformed(evt);
            }
        });
        algoMenu.add(algoDFSButton);

        algoDFIDButton.setText("DFID");
        algoDFIDButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                algoDFIDButtonActionPerformed(evt);
            }
        });
        algoMenu.add(algoDFIDButton);

        algoIDAButton.setText("IDA");
        algoIDAButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                algoIDAButtonActionPerformed(evt);
            }
        });
        algoMenu.add(algoIDAButton);

        jMenuBar1.add(algoMenu);

        graphMenu.setText("Graph");

        readGraphButton.setText("Read From File <TO DO>");
        graphMenu.add(readGraphButton);
        graphMenu.add(jSeparator9);

        graphGameTree_1Button.setText("Game Tree-1");
        graphGameTree_1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphGameTree_1ButtonActionPerformed(evt);
            }
        });
        graphMenu.add(graphGameTree_1Button);

        graphGameTree_2Button.setText("Game Tree-2");
        graphGameTree_2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphGameTree_2ButtonActionPerformed(evt);
            }
        });
        graphMenu.add(graphGameTree_2Button);

        graphGameTree_3Button.setText("Game Tree-3");
        graphGameTree_3Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphGameTree_3ButtonActionPerformed(evt);
            }
        });
        graphMenu.add(graphGameTree_3Button);
        graphMenu.add(jSeparator5);

        graphSparseButton.setText("Graph Sparse");
        graphSparseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphSparseButtonActionPerformed(evt);
            }
        });
        graphMenu.add(graphSparseButton);

        graphDenseButton.setText("Graph Dense");
        graphDenseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphDenseButtonActionPerformed(evt);
            }
        });
        graphMenu.add(graphDenseButton);

        graphTreeButton.setText("Tree");
        graphTreeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphTreeButtonActionPerformed(evt);
            }
        });
        graphMenu.add(graphTreeButton);
        graphMenu.add(jSeparator13);

        graphGrid_1Button.setText("Grid-1");
        graphGrid_1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphGrid_1ButtonActionPerformed(evt);
            }
        });
        graphMenu.add(graphGrid_1Button);

        graphGrid_2Button.setText("Grid-2");
        graphGrid_2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphGrid_2ButtonActionPerformed(evt);
            }
        });
        graphMenu.add(graphGrid_2Button);

        graphGrid_3Button.setText("Grid-3");
        graphGrid_3Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphGrid_3ButtonActionPerformed(evt);
            }
        });
        graphMenu.add(graphGrid_3Button);

        graphGrid_4Button.setText("Grid-4");
        graphGrid_4Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphGrid_4ButtonActionPerformed(evt);
            }
        });
        graphMenu.add(graphGrid_4Button);
        graphMenu.add(jSeparator14);

        graphGrid_1MSTButton.setText("Grid-1 MST");
        graphGrid_1MSTButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphGrid_1MSTButtonActionPerformed(evt);
            }
        });
        graphMenu.add(graphGrid_1MSTButton);

        graphGrid_2MSTButton.setText("Grid-2 MST");
        graphGrid_2MSTButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphGrid_2MSTButtonActionPerformed(evt);
            }
        });
        graphMenu.add(graphGrid_2MSTButton);

        graphGrid_3MSTButton.setText("Grid-3 MST");
        graphGrid_3MSTButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphGrid_3MSTButtonActionPerformed(evt);
            }
        });
        graphMenu.add(graphGrid_3MSTButton);

        graphGrid_4MSTButton.setText("Grid-4 MST");
        graphGrid_4MSTButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphGrid_4MSTButtonActionPerformed(evt);
            }
        });
        graphMenu.add(graphGrid_4MSTButton);

        jMenuBar1.add(graphMenu);

        helpMenu.setText("Help");

        helpDemoButton.setText("How To Run Demo?");
        helpDemoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpDemoButtonActionPerformed(evt);
            }
        });
        helpMenu.add(helpDemoButton);

        helpCreditsButton.setText("Credits");
        helpCreditsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpCreditsButtonActionPerformed(evt);
            }
        });
        helpMenu.add(helpCreditsButton);
        helpMenu.add(jSeparator12);

        helpAboutButton.setText("About");
        helpAboutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpAboutButtonActionPerformed(evt);
            }
        });
        helpMenu.add(helpAboutButton);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 833, Short.MAX_VALUE)
            .addComponent(graphCanvas1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphCanvas1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_exitActionPerformed

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        newButtonActionHelper(evt);
    }//GEN-LAST:event_newButtonActionPerformed

    private void helpDemoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpDemoButtonActionPerformed
        // TODO add your handling code here:
        helpDemoButtonActionPerformedHelper(evt);
    }//GEN-LAST:event_helpDemoButtonActionPerformed

    private void refreshRateSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_refreshRateSliderStateChanged
        // TODO add your handling code here:
        refreshRateSliderStateChangedHelper(evt);
    }//GEN-LAST:event_refreshRateSliderStateChanged

    private void sizeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sizeSpinnerStateChanged
        // TODO add your handling code here:
        sizeSpinnerStateChangedHelper(evt);
        //sizeSpinner
    }//GEN-LAST:event_sizeSpinnerStateChanged

    private void branchingFactorComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_branchingFactorComboBoxActionPerformed
        // TODO add your handling code here:
        branchingFactorComboBoxActionPerformedHelper(evt);
    }//GEN-LAST:event_branchingFactorComboBoxActionPerformed

    private void startNodeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startNodeButtonActionPerformed
        // TODO add your handling code here:
        startActionHelper(evt);
    }//GEN-LAST:event_startNodeButtonActionPerformed

    private void goalNodesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goalNodesButtonActionPerformed
        // TODO add your handling code here:
        goalButtonActionHelper(evt);
    }//GEN-LAST:event_goalNodesButtonActionPerformed

    private void stepThroughButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepThroughButtonActionPerformed
        // TODO add your handling code here:
        stepButtonActionHelper(evt);
    }//GEN-LAST:event_stepThroughButtonActionPerformed

    private void runButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButton2ActionPerformed
        // TODO add your handling code here:
        runButtonActionHelper(evt);
    }//GEN-LAST:event_runButton2ActionPerformed

    private void stopButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButton2ActionPerformed
        // TODO add your handling code here:
        stopButtonActionHelper(evt);
    }//GEN-LAST:event_stopButton2ActionPerformed

    private void pipeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pipeButtonActionPerformed
        // TODO add your handling code here:
        pipeActionHelper(evt);
    }//GEN-LAST:event_pipeButtonActionPerformed

    private void graphGameTree_1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphGameTree_1ButtonActionPerformed
        // TODO add your handling code here:
        graphActionHelper(evt);
    }//GEN-LAST:event_graphGameTree_1ButtonActionPerformed

    private void graphGameTree_2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphGameTree_2ButtonActionPerformed
        // TODO add your handling code here:
        graphActionHelper(evt);
    }//GEN-LAST:event_graphGameTree_2ButtonActionPerformed

    private void graphGameTree_3ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphGameTree_3ButtonActionPerformed
        // TODO add your handling code here:
        graphActionHelper(evt);
    }//GEN-LAST:event_graphGameTree_3ButtonActionPerformed

    private void graphSparseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphSparseButtonActionPerformed
        // TODO add your handling code here:
        graphActionHelper(evt);
    }//GEN-LAST:event_graphSparseButtonActionPerformed

    private void graphDenseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphDenseButtonActionPerformed
        // TODO add your handling code here:
        graphActionHelper(evt);
    }//GEN-LAST:event_graphDenseButtonActionPerformed

    private void graphTreeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphTreeButtonActionPerformed
        // TODO add your handling code here:
        graphActionHelper(evt);
    }//GEN-LAST:event_graphTreeButtonActionPerformed

    private void graphGrid_1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphGrid_1ButtonActionPerformed
        // TODO add your handling code here:
        graphActionHelper(evt);
    }//GEN-LAST:event_graphGrid_1ButtonActionPerformed

    private void graphGrid_1MSTButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphGrid_1MSTButtonActionPerformed
        // TODO add your handling code here:
        graphActionHelper(evt);
    }//GEN-LAST:event_graphGrid_1MSTButtonActionPerformed

    private void densityComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_densityComboBoxActionPerformed
        // TODO add your handling code here:
        densityComboBoxActionPerformedHelper(evt);
    }//GEN-LAST:event_densityComboBoxActionPerformed

    private void helpCreditsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpCreditsButtonActionPerformed
        // TODO add your handling code here:
        helpCreditsButtonActionPerformedHelper(evt);
    }//GEN-LAST:event_helpCreditsButtonActionPerformed

    private void helpAboutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpAboutButtonActionPerformed
        // TODO add your handling code here:
        helpAboutButtonActionPerformedHelper(evt);
    }//GEN-LAST:event_helpAboutButtonActionPerformed

    private void graphGrid_2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphGrid_2ButtonActionPerformed
        // TODO add your handling code here:
        graphActionHelper(evt);
    }//GEN-LAST:event_graphGrid_2ButtonActionPerformed

    private void graphGrid_3ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphGrid_3ButtonActionPerformed
        // TODO add your handling code here:
        graphActionHelper(evt);
    }//GEN-LAST:event_graphGrid_3ButtonActionPerformed

    private void graphGrid_4ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphGrid_4ButtonActionPerformed
        // TODO add your handling code here:
        graphActionHelper(evt);
    }//GEN-LAST:event_graphGrid_4ButtonActionPerformed

    private void graphGrid_2MSTButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphGrid_2MSTButtonActionPerformed
        // TODO add your handling code here:
        graphActionHelper(evt);
    }//GEN-LAST:event_graphGrid_2MSTButtonActionPerformed

    private void graphGrid_3MSTButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphGrid_3MSTButtonActionPerformed
        // TODO add your handling code here:
        graphActionHelper(evt);
    }//GEN-LAST:event_graphGrid_3MSTButtonActionPerformed

    private void graphGrid_4MSTButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphGrid_4MSTButtonActionPerformed
        // TODO add your handling code here:
        graphActionHelper(evt);
    }//GEN-LAST:event_graphGrid_4MSTButtonActionPerformed

    private void algoDFSButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_algoDFSButtonActionPerformed
        // TODO add your handling code here:
        algoActionPerformedHelper(evt);
    }//GEN-LAST:event_algoDFSButtonActionPerformed

    private void algoBFSButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_algoBFSButtonActionPerformed
        // TODO add your handling code here:
        algoActionPerformedHelper(evt);
    }//GEN-LAST:event_algoBFSButtonActionPerformed

    private void algoDFIDButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        algoActionPerformedHelper(evt);
    }
    private void algoIDAButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        algoActionPerformedHelper(evt);
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {

        final ALVISDesktop f = new ALVISDesktop();
        f.setExtendedState(ALVISDesktop.MAXIMIZED_BOTH); // set frame to maximum displayable size
        f.pack();

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                f.setVisible(true);
            }
        });
    }
    private javax.swing.JMenuItem algoDFIDButton;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem algoBFSButton;
    private javax.swing.JMenuItem algoDFSButton;
    private javax.swing.JMenuItem algoIDAButton;
    private javax.swing.JMenu algoMenu;
    private javax.swing.JComboBox branchingFactorComboBox;
    private javax.swing.JLabel branchingFactorLabel;
    private javax.swing.JComboBox densityComboBox;
    private javax.swing.JLabel densityLabel;
    private javax.swing.JMenuItem exit;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JButton goalNodesButton;
    private alviz.main.GraphCanvas graphCanvas1;
    private javax.swing.JMenuItem graphDenseButton;
    private javax.swing.JMenuItem graphGameTree_1Button;
    private javax.swing.JMenuItem graphGameTree_2Button;
    private javax.swing.JMenuItem graphGameTree_3Button;
    private javax.swing.JMenuItem graphGrid_1Button;
    private javax.swing.JMenuItem graphGrid_1MSTButton;
    private javax.swing.JMenuItem graphGrid_2Button;
    private javax.swing.JMenuItem graphGrid_2MSTButton;
    private javax.swing.JMenuItem graphGrid_3Button;
    private javax.swing.JMenuItem graphGrid_3MSTButton;
    private javax.swing.JMenuItem graphGrid_4Button;
    private javax.swing.JMenuItem graphGrid_4MSTButton;
    private javax.swing.JMenu graphMenu;
    private javax.swing.JMenuItem graphSparseButton;
    private javax.swing.JMenuItem graphTreeButton;
    private javax.swing.JMenuItem helpAboutButton;
    private javax.swing.JMenuItem helpCreditsButton;
    private javax.swing.JMenuItem helpDemoButton;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuItem newButton;
    private javax.swing.JButton pipeButton;
    private javax.swing.JMenuItem readGraphButton;
    private javax.swing.JLabel refreshRateLabel;
    private javax.swing.JSlider refreshRateSlider;
    private javax.swing.JLabel refreshRateValueLabel;
    private javax.swing.JButton runButton2;
    private javax.swing.JMenuItem saveGraphButton;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JSpinner sizeSpinner;
    private javax.swing.JButton startNodeButton;
    private javax.swing.JButton stepThroughButton;
    private javax.swing.JButton stopButton2;
    // End of variables declaration//GEN-END:variables


}
