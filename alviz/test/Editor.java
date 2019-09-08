/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alviz.test;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import javax.swing.*;
import java.io.*;
import java.util.Locale;
import java.text.MessageFormat;
import java.util.Properties;

//font can be sansserif or SansSerif

class Editor extends JFrame {
  private JMenuItem savetext, getTextMenuItem, cleardisplay,
            cut, copy, paste, cancelwrite, cancelread;
  private JTextArea disptext;
  private Painter panel1paint;
  private static Properties messages;
  private String initStyles[];
  private static Editor frame;
  private Thread readThread, writeThread;

  private Editor(Locale currentLocale) throws Exception {
    //Internationalization variables
    //messages = ResourceBundle.getBundle("MessagesBundle", currentLocale);
    InputStream in = Editor.class.getClassLoader().getResourceAsStream("alviz/test/MessagesBundle.properties");
    messages = new Properties();
    messages.load(in);
    buildGUI();
    hookupEvents();
  }

  private void buildGUI() {
    JMenuBar menubar = new JMenuBar();
    setJMenuBar(menubar);

    JMenu filemenu   = new JMenu(messages.getProperty("FileMenu"), false);
    JMenu editmenu   = new JMenu(messages.getProperty("EditMenu"), false);
    JMenu cancelmenu = new JMenu(messages.getProperty("CancelMenu"), false);

    menubar.add(filemenu);
    menubar.add(editmenu);
    menubar.add(cancelmenu);

    savetext        = new JMenuItem(messages.getProperty("SaveMenuItem"));
    getTextMenuItem = new JMenuItem(messages.getProperty("GetMenuItem"));
    cleardisplay    = new JMenuItem(messages.getProperty("ClearMenuItem"));
    cut             = new JMenuItem(messages.getProperty("Cut"));
    copy            = new JMenuItem(messages.getProperty("Copy"));
    paste           = new JMenuItem(messages.getProperty("Paste"));
    cancelwrite     = new JMenuItem(messages.getProperty("CancelWrite"));
    cancelread      = new JMenuItem(messages.getProperty("CancelRead"));

    filemenu.add(savetext);
    filemenu.add(getTextMenuItem);
    filemenu.add(cleardisplay);
    editmenu.add(cut);
    editmenu.add(copy);
    editmenu.add(paste);
    cancelmenu.add(cancelwrite);
    cancelmenu.add(cancelread);

    getContentPane().setLayout(new GridLayout(1,3));

    JPanel panel1 = new JPanel();
    panel1.setLayout(new BorderLayout());
    panel1.setBackground(Color.white);

    getContentPane().add(panel1);

    JPanel panel2 = new JPanel();
    panel2.setLayout(new FlowLayout());
    panel2.setBackground(Color.white);

    getContentPane().add(panel2);

    //Create message area from color and text string
    panel1paint = new Painter(Color.blue);
    Dimension dimension = new Dimension();
    dimension.setSize(200, 25);
    panel1paint.setSize(dimension);
    //Add message area to Panel 1
    panel1.add(BorderLayout.CENTER, panel1paint);

    //Create text area for panel 2
    disptext = new JTextArea();
    disptext.setFont(new Font("sansserif",Font.ITALIC, 12));
    disptext.setLineWrap(true);
    disptext.setWrapStyleWord(true);
    disptext.setEditable(true);
    
    JScrollPane areaScrollPane = new JScrollPane(disptext);
    areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    areaScrollPane.setPreferredSize(new Dimension(200, 175));
    areaScrollPane.setBorder(BorderFactory.createTitledBorder(messages.getProperty("EnterEditText")));

    panel2.add(areaScrollPane);
  }

  private void clearDisplay() {
    panel1paint.setColor(Color.white);
    disptext.setText("");
    cancelread.setEnabled(true);
    cancelwrite.setEnabled(true);
  }

  private void hookupEvents() {
    cleardisplay.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent ev) {
         clearDisplay();
      }
    });

    cut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        disptext.cut();
      }
    });

    copy.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        disptext.copy();
      }
    });

    paste.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        disptext.paste();
      }
    });

    savetext.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        panel1paint.setText(messages.getProperty("WriteNotification"));
        String returned = disptext.getText();
        cancelwrite.setEnabled(true);
        cancelread.setEnabled(false);
        writeToFile(returned);
        panel1paint.setColor(Color.blue);
      }
    });

    getTextMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        panel1paint.setText(messages.getProperty("ReadNotification"));
        cancelwrite.setEnabled(false);
        cancelread.setEnabled(true);
        readFromFile();
        panel1paint.setColor(Color.blue);
      }
    });

    cancelread.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        panel1paint.setText(messages.getProperty("CancelNotification"));
        cancelRead();
        panel1paint.setColor(Color.blue);
      }
    });

    cancelwrite.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        panel1paint.setText(messages.getProperty("CancelNotification"));
        cancelWrite();
        panel1paint.setColor(Color.blue);
      }
    });

  }

  private void cancelRead() {
    ensureEventThread();
    if(readThread != null) {
      readThread.interrupt();
      readThread = null;
    }
  }

  private void cancelWrite() {
    ensureEventThread();
    if(writeThread != null) {
      writeThread.interrupt();
      writeThread = null;
    }
  }

  private void readFromFile() {
    ensureEventThread();
    Runnable readRun = new Runnable() {
      public void run() {
        FileInputStream in=null;
        String text = null;
        try{
          //Simulate a slow read
          Thread.sleep(5000);
          String inputFileName = System.getProperty("user.home") + File.separatorChar + "text.txt";
          File inputFile = new File(inputFileName);
          in = new FileInputStream(inputFile);
          byte bt[] = new byte[(int)inputFile.length()];
          in.read(bt);
          text = new String(bt);
          setReadTextSafely(text);
        }
        catch(java.lang.InterruptedException ex) {
        }
        catch(java.io.IOException e) {
          Object[] messageArguments = { messages.getProperty("file") };
          MessageFormat formatter= new MessageFormat(messages.getProperty("CannotReadError"));
          panel1paint.setColor(Color.blue);
          String cannotread = formatter.format(messageArguments);
          panel1paint.setText(messages.getProperty("CannotReadError"));
        }
        finally {
          if(in != null) {
            try {
              in.close();
            }
            catch(java.io.IOException e) {
              panel1paint.setColor(Color.blue);
              panel1paint.setText(messages.getProperty("CannotCloseError"));
            }
          }
        }
      }
    };
    readThread = new Thread(readRun);
    readThread.start();
  }

  private void setReadTextSafely(String readtext) {
    final String text = readtext;
    Runnable r = new Runnable() {
      public void run() {
        try {
          setReadText(text);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    };
    SwingUtilities.invokeLater(r);
  }

  private void setReadText(String text) {
    ensureEventThread();
    disptext.setText(text);
    panel1paint.setColor(Color.blue);
    panel1paint.setText(messages.getProperty("TextReadNotification"));
    cancelread.setEnabled(true);
    cancelwrite.setEnabled(true);
  }

  private void writeToFile(final String text) {
    ensureEventThread();
    Runnable writeRun = new Runnable() {
      String writetext = text;
      public void run() {
        if(writetext != null) {
          //Write to file
          FileOutputStream out=null;
          try {
            //Simulate a slow write
            Thread.sleep(5000);
            byte b[] = writetext.getBytes();
            String outputFileName = System.getProperty(
                "user.home")
                + File.separatorChar + "text.txt";
            out = new FileOutputStream(outputFileName);
            out.write(b);
            getWriteTextSafely();
          } catch(java.lang.InterruptedException ex) {
          } catch(java.io.IOException e) {
            Object[] messageArguments = { messages.getProperty("file") };
            MessageFormat formatter= new MessageFormat(
                messages.getProperty("CannotWriteError"));
            String cannotwrite = formatter.format(messageArguments);
            panel1paint.setColor(Color.blue);
            panel1paint.setText(messages.getProperty(
                "CannotWriteError"));
          } finally {
            if(out != null) {
              try {
                out.close();
              } catch(java.io.IOException e) {
        	panel1paint.setColor(Color.blue);
                panel1paint.setText(messages.getProperty(
                  "CannotCloseError"));
              }
            }
          }
        } else {
          panel1paint.setColor(Color.blue);
          panel1paint.setText(messages.getProperty(
            "NoTextError"));
        }
      }
    };
    writeThread = new Thread(writeRun);
    writeThread.start();
  }

  private void getWriteTextSafely() {
    Runnable r = new Runnable() {
      public void run() {
        try {
          getWriteText();
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    };
    SwingUtilities.invokeLater(r);
  }

  private void getWriteText() {
    ensureEventThread();
    panel1paint.setColor(Color.blue);
    panel1paint.setText(messages.getProperty("TextSavedNotification"));
    cancelread.setEnabled(true);
    cancelwrite.setEnabled(true);
  }

  private void ensureEventThread() {
    if(SwingUtilities.isEventDispatchThread()) {
      return;
    }
    throw new RuntimeException(messages.getProperty("file"));
  }

  public static void main(String[] args) throws Exception {
    String language, country, variant;
    Locale currentLocale;
    if(args.length == 3) {
      language = new String(args[0]);
      country = new String(args[1]);
      variant = new String(args[2]);
      currentLocale = new Locale(language, country, variant);
    } else if (args.length == 2) {
      language = new String(args[0]);
      country = new String(args[1]);
      currentLocale = new Locale(language, country);
    } else {
      currentLocale = Locale.getDefault();
    }
    frame = new Editor(currentLocale);
    frame.setTitle(messages.getProperty( "Title"));
    WindowListener l = new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    };
    frame.addWindowListener(l);
    frame.pack();
    frame.setVisible(true);
  }
}

class Painter extends JPanel {
  Color c;
  String s;


  Painter(Color c) {
    super();
    this.s = null;
    this.c = c;
  }
  protected synchronized void setColor(Color c) {
    this.c = c;
    repaint();
  }
  protected synchronized Color getColor() {
    return this.c;
  }
  protected synchronized void setText(String s) {
     this.s = s;
     repaint();
  }
  protected synchronized String getText() {
     return this.s;
  }
  public void paintComponent(Graphics g) {
    Graphics2D g2;
    String msg = getText();
    Color color = getColor();
    g2 = (Graphics2D) g;

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
    //Make background white
    g2.setColor(Color.white);
    g2.fillRect(0, 0, getSize().width -1, getSize().height -1);
    //Set font rendering context and font
    FontRenderContext frc = g2.getFontRenderContext();
    Font f = new Font("sansserif", Font.PLAIN, 15);
    //Create styled text from font and string
    if(msg != null) {
      TextLayout tl = new TextLayout(msg, f, frc);
      //Get the size of the drawing area
      Dimension theSize = getSize();
      //Set the 2D graphics context color for drawing the text
      g2.setColor(color);
      //Draw the text into the drawing area
      tl.draw(g2, theSize.width/30, theSize.height/2);
    }
  }
}
