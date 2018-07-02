/**
 * MIT License
 *
 * Copyright (c) 2018 Dr. Graham Alvare and Dr. Richard Gordon
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;

// javac Foxels.java && java Foxels answer.png

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * <dl>
 *     <dt>SYNOPSIS</dt>
 *     <dd>
 *          <p>Software for modeling the Foxels algorithm on a single CT slice
 *             (defined by an image JPG or PNG file).  In addition, this program
 *             also models a traditional MART CT scanner as a control.</p>
 *     </dd>
 *     <dt>PARAMETERS</dt>
 *     <dd>
 *          <p>There are 3 ways to run foxels</p>
 * 
 *          <ol>
 *              <li>
 *                  <b>java Foxels [image.png]</b><br />
 *                  <i>(this runs Foxels in GUI mode)</i>
 *              </li>
 *              <li>
 *                  <b>java Foxels image.png  [foxel_num]  [fan_width] [fspot] 
 *                                            [gantry] [det] [conv] [line]</b>
 *                                            <br />
 *                  <i>(this runs Foxels in batch mode with a given set of
 *                      parameters; replace the the text - foxel_num, fan_width,
 *                      focal_spot, gantry - with the numbers you wish to use
 *                      for each parameter).</i>
 *                  <pre> e.g. java Foxels image.png 3 54 9 1065</pre>
 *              </li>
 *          </ol>
 * 
 *          <p>When a parameter is not specified on the command line or the CSV
 *             file, the default values will be used instead.  These values are
 *             listed below:</p>
 * 
 *          <table border=1>
 *              <tr>
 *                  <th>parameter</th>
 *                  <th>default value</th>
 *                  <th>meaning</th>
 *              </tr>
 *              <tr>
 *                  <th>foxel_num</th>
 *                  <td>3 foxels</td>
 *                  <td>The number of foxels to run the algorithm with.</td>
 *              </tr>
 *              <tr>
 *                  <th>detect</th>
 *                  <td>800</td>
 *                  <td>The number of X-ray detectors in the gantry.</td>
 *              </tr>
 *              <tr>
 *                  <th>fspot</th>
 *                  <td># of foxels</td>
 *                  <td>This is the focal spot width (i.e. the size of the X-ray
 *                      source).  Note that values specified on the command line
 *                      are treated as pixel counts, not multiples of the number
 *                      of foxels.</td>
 *              </tr>
 *              <tr>
 *                  <th>gantry</th>
 *                  <td>2 x maximum(image height, image width)</td>
 *                  <td>This is the gantry radius, i.e. the distance between the
 *                      X-ray source and X-ray detectors.  Note that values
 *                      specified on the command line are treated as pixel
 *                      counts, not multiples of the image height or width.</td>
 *              </tr>
 *              <tr>
 *                  <th>line</th>
 *                  <td>Wu</td>
 *                  <td>This is the line algorithm to use for Foxels.
 *                      By default this is the Xiaolin Wu (anti-aliased) line
 *                      algorithm; however, the Bressenham line algorithm may
 *                      alternatively be used.  The Bressenham line algorithm is
 *                      much faster, but the Xiaolin Wu algorithm projects an
 *                      anti-aliased line (which should behave more like a real
 *                      X-ray projection).</td>
 *              </tr>
 *          </table>
 * 
 *          <p>When using the save feature in the GUI mode, files are saved
 *             as follows:</p>
 * 
 *          <table>
 *              <tr>
 *                  <th>Filename</th>
 *                  <th>Contents</th>
 *              </tr>
 *              <tr>
 *                  <td>foxels_image.png</td>
 *                  <td>The canvas image generated by the Foxels algorithm.</td>
 *              </tr>
 *              <tr>
 *                  <td>normal_MART_image.png</td>
 *                  <td>The canvas image generated by the control algorithm.</td>
 *              </tr>
 *              <tr>
 *                  <td>foxels_lineplot.png</td>
 *                  <td>The lineplot file for the Foxels algorithm.</td>
 *              </tr>
 *              <tr>
 *                  <td>normal_MART_lineplot.png</td>
 *                  <td>The lineplot file for the control algorithm.</td>
 *              </tr>
 *          </table>
 *     </dd>
 *     <dt>LICENSE</dt>
 *     <dd>
 *         This code is licensed under the Creative Commons 3.0<br />
 *         Attribution + ShareAlike license - for details see:<br />
 *         <a href="http://creativecommons.org/licenses/by-sa/3.0/">
 *         http://creativecommons.org/licenses/by-sa/3.0/</a><br />
 *     </dd>
 * </dl>
 **
 * @author Graham Alvare
 * @author Richard Gordon
 */
public class Foxels {
    /**
     * The height of the width CT image.
     */
    static private int      width    = 256;
    /**
     * The height of the hidden CT image.
     */
    static private int      height   = 256;
    /**
     * The hidden CT image.
     */
    private static float[]    hidden;
    
    private static final String TITLE_STR = "Foxels - v1.0 by Graham Alvare and Richard Gordon (2016)";
    
    /**
     * A variable used for stopping the Foxels algorithm.
     */
    private static boolean stop_foxels = false;
    
    /**
     * The minimum angle step to use in calculations.  This is to prevent
     * step angles that will essentially act as if they were zero, due to
     * the limitations of Java floating point math (i.e. floats have 23 bit
     * mantissas with 8 bit exponents, doubles have 52 bit mantissas with
     * 11 bit exponents -- in both cases an additional bit is used as the
     * sign bit, thus making floats 32-bits, and doubles 64-bits).
     */
    public static final double MIN_ANGLE = 0.0000000001;
    
    /**
     * The method of convergence to use.
     */
    public static enum CType {
        NONE,
        IDEAL_L1,
        IDEAL_L2,
        VARIANCE;
        
        @Override
        public String toString() {
            String text = "None - specify a number of iterations:";
            switch (this) {
                case IDEAL_L1:
                    text = "Ideal convergence (L1 mean)";
                    break;
                case IDEAL_L2:
                    text = "Ideal convergence (L2 mean)";
                    break;
                case VARIANCE:
                    text = "Variance method";
                    break;
            }
            return text;
        }
    }
        
    private static  File           ofile       = null;
    private static  BufferedImage  oimage      = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    private static  BufferedImage  tcanvas     = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    private static  WritableRaster traster     = (WritableRaster) tcanvas.getRaster();
    private static  JLabel         TBRUSH_AREA = new JLabel(new ImageIcon(tcanvas));

    private static  BufferedImage  tlplot       = new BufferedImage(width, 256, BufferedImage.TYPE_INT_RGB);
    private static  Graphics       tlgfx        = tlplot.getGraphics();
    private static  JLabel         TLPLOT_AREA  = new JLabel(new ImageIcon(tlplot));

    // Count the number of iterations performed on the working image, so far.
    private static  int           iteration_idx = 1;
    // keep track of the current scoring (for convergence)
    private static double  prev_var    = 0;
    private static double  prev_l1qscore = 0;
    private static double  prev_l2qscore = 0;
    /**
     * Stores the status bar object for the program.  This is stored so the text
     * can be updated as operations are performed on the hidden image.
     */
    private static final JLabel    STATUS_BAR   = new JLabel("Line: 0");
    
    // the progress images
    private static float[]  twork = null;
    
    /**
     * The directory in which the program was launched.
     */
    public static final File CURR_DIR = new File(".");
    /**
     * A decimal format object, for limiting the number of decimal points for 
     * all numbers printed to the screen or terminal.  Currently this decimal
     * format object limits all decimal numbers to 4 decimal points.
     */
    public static final DecimalFormat df2 = new DecimalFormat(".####");
    
    /**
     * Initializes the Foxels program.
     **
     * @param args the command line arguments to run the program with.
     */
    public static void main (String[] args) {
        // Set the default paramters for the program.
        int     L           = 8;       // the number of intersections / "levels" -- L in Guan and Gordon (1994).
        int     felem       = 3;       // the number of finite elements to use for X-ray aquisition (b in the Foxels paper).
        int     foxels      = 3;       // the number of foxels to use for reconstruction (h in the Foxels paper).
        double  dwidth      = 1.f;     // the width of detectors in the machine, specified as number of voxels.
        double  fan_width   = 57.f;    // the width of the fan-beam in degrees.
        int     focal_spot  = foxels;  // the Focal spot width in voxels
        CType   converge    = CType.VARIANCE; // the type of convergence to use for the Foxels algorithm.
        boolean wu_line     = true;
        String  file_prefix = "foxels";
        int     rounds      = 1;
        
        // Parse any command line arguments. The first command line argument
        // is always a filename.
        if (args.length > 0) {
            // Read in the file specified at the command line.
            String filename = args[0];
            
            try {
                readImage(filename);

                // If more than 1 command line argument is specified, the start
                // batch mode.
                if (args.length > 1) {
                    // Set the gantry radius to double the height or width
                    // (whichever number is greater).
                    int radius = Math.max(width, height) * 2;

                    System.err.println("BATCH MODE!");

                    // Determine if the second argument is a number (i.e. the number
                    // of foxels).  If so, then parse the command line arguments as
                    // parameters for a single run of the foxels algorithm.  If not,
                    // then try to parse the second command argument as a CSV
                    // filename.  CSV files may be used to specify multiple foxel
                    // algorithm trial runs (i.e. the equivalent of running Foxels
                    // multiple times with each row of the CSV file specifying the
                    // command line arguments).
                    if (args[1].matches("\\d\\d*")) {
                        // Update the number of foxels, and also update the focal
                        // spot width to be equal to the number of foxels (i.e.
                        // the focal spot width is, by default, equal to the number
                        // of foxels).
                        foxels = Integer.parseInt(args[1]);
                        focal_spot = foxels * 2;
                        felem = foxels;

                        // Determine if there is a third numerical command line
                        // argument to parse.  If so, then this argument will be
                        // the fanbeam width for the gantry.
                        if (args.length > 2 && args[2].matches("\\d\\d*")) {
                            fan_width = Double.parseDouble(args[2]);

                            // Determine if there is a fourth numerical command
                            // line argument to parse.  If so, then this argument
                            // will be the focal spot size.
                            if (args.length > 3 && args[3].matches("\\d\\d*")) {
                                focal_spot = Integer.parseInt(args[3]);
                                felem = focal_spot;

                                // Determine if there is a fifth numerical command
                                // line argument to parse.  If so, then this is the
                                // gantry radius, specified in number of voxels.
                                if (args.length > 4 && args[4].matches("\\d\\d*")) {
                                    radius = Integer.parseInt(args[4]);
                                        
                                    // Determine if there is a sixth command line
                                    // argument.  If so, this is the file prefix to
                                    // use for writing the image files.
                                    if (args.length > 5) {
                                        file_prefix = args[5];

                                        // Determine if there is a seventh command line
                                        // argument.  If so, this argument determines
                                        // whether to use the Bressenham or Xiaolin Wu
                                        // line algorithm.
                                        if (args.length > 6) {
                                            wu_line = args[6].toLowerCase().contains("wu")
                                                    || args[6].toLowerCase().contains("xiaolin");

                                            // Determine if there is a eighth command line
                                            // argument.  If so, this argument determines
                                            // whether to use the Ideal or Variance
                                            // convergence/stopping criteria.
                                            //
                                            // The other option is specifying a set integer
                                            // number of iterations to run the algorithm.
                                            if (args.length > 7) {
                                                if (args.length > 7 && args[7].matches("\\d\\d*")) {
                                                    rounds = Integer.parseInt(args[7]);
                                                } else {
                                                    if (args[7].toLowerCase().contains("ideal_l1")) {
                                                        converge = CType.IDEAL_L1;
                                                    } else if (args[7].toLowerCase().contains("ideal_l2")) {
                                                        converge = CType.IDEAL_L2;
                                                    }
                                                    // Default value is variance method.
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Reset the foxels program image canvases (mandatory),
                        // then run the foxels algorithm.
                        reset ();
                        foxels_commandline(null, hidden, twork, tlgfx, L, felem, foxels, fan_width, focal_spot, radius, dwidth, converge, rounds, wu_line);
                        System.out.flush();
                        System.out.flush();
                        System.out.println();

                        // Save the output images from the foxels algorithm in the
                        // current working directory.
                        try {
                            saveImage(tcanvas, CURR_DIR, file_prefix + "_image.png");
                            saveImage(tlplot,  CURR_DIR, file_prefix + "_lineplot.png");
                        } catch (IOException ioe) {
                            System.err.println("ERROR SAVING IMAGES!");
                            ioe.printStackTrace(System.err);
                        }
                    }
                } else {
                    // If there is no second command line argument specified,
                    // start the Foxels program in GUI mode.
                    foxels_window(foxels);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace(System.err);
            }
        } else {
            // If there are no command line arguments specified, then open a
            // file chooser, let the user select a file, then open the Foxels
            // program in GUI mode.
            JFileChooser openFile = new JFileChooser();

            if (openFile.showOpenDialog(null) ==  JFileChooser.APPROVE_OPTION) {
                File ofile = openFile.getSelectedFile();
                if (ofile != null) {
                    try {
                        readImage(ofile.getAbsolutePath());
                        foxels_window(foxels);
                    } catch (IOException ioe) {
                        ioe.printStackTrace(System.err);
                    }
                }
            } else {
                // If the user declines to open a file, print a help message.
                System.err.println("Foxels -- by Graham Alvare and Richard Gordon");
                System.err.println("=============================================");
                System.err.println("There are 2 ways to run foxels from the command line:");
                System.err.println("   1) java Foxels [image.png]");
                System.err.println("         (this runs Foxels in GUI mode)");
                System.err.println("");
                System.err.println("   2) java Foxels image.png  [foxel_num]  [focal_spot] [gantry] [fwidth] [conv]");
                System.err.println("         (this runs Foxels in batch mode with a given set of parameters)");
                System.err.println("");
                System.err.println("    These are the default values, which are used when not specified:");
                System.err.println("        foxel_num   = 3 foxels");
                System.err.println("        focal_spot  = number of foxels");
                System.err.println("            (this is the focal spot width, note that values specified on");
                System.err.println("             the command line are treated as pixel counts, not multiples");
                System.err.println("             of the number of foxels)");
                System.err.println("        gantry      = 2 x max(image height, image width)");
                System.err.println("            (this is the gantry radius, note that values specified on");
                System.err.println("             the command line are treated as pixel counts, not multiples");
                System.err.println("             of the image height or width)");
                System.err.println("        fwidth      = 57 degree fanbeam width");
                System.err.println("");
                System.err.println("NOTE: units should not be specified when entering these for Foxels.");
                System.err.println("");
            }
        }
    }
    
    /**
     * Open the main window for the Foxels program.
     **
     * @param foxels the default number of foxels to divide the X-ray source.
     */
    public static void foxels_window(int foxels) {
        final JFrame     main_window  = new JFrame(); // The main Foxels window.
        final JFrame     plot_window  = new JFrame(); // The line plot window.
        final JFrame     log_window   = new JFrame(); // The log text window.

        // Create the panels and an menu bars used for organizing the Foxels
        // and lineplot windows.
        final JToolBar   BRUSH_BAR    = new JToolBar(JToolBar.HORIZONTAL);
        final JToolBar   FAN_BAR      = new JToolBar(JToolBar.HORIZONTAL);
        final JToolBar   ALG_BAR      = new JToolBar(JToolBar.HORIZONTAL);
        final JPanel     MAIN_PANEL   = new JPanel();
        final JPanel     PLOT_PANEL   = new JPanel();
        final JPanel     TITLE_PANEL  = new JPanel();
        
        // Create the widgets for setting parameters to run the Foxels and
        // control algorithms.
        final JSpinner     L_SPIN     = new JSpinner(new SpinnerNumberModel(7, 1, 99, 1));
        final JSpinner     B_SPIN     = new JSpinner(new SpinnerNumberModel(foxels, 1, Math.max(9999, Math.max(width, height) * 4), 1));
        final JSpinner     H_SPIN     = new JSpinner(new SpinnerNumberModel(foxels, 1, Math.max(9999, Math.max(width, height) * 4), 1));
        final JSpinner     R_SPIN     = new JSpinner(new SpinnerNumberModel(Math.round(Math.max(width, height) * 0.85), 10, Math.max(Math.max(width, height) * 4, 8600), 1));
        final JSpinner     DW_SPIN    = new JSpinner(new SpinnerNumberModel(1.0, 0.1, Math.min(width, height), 0.1));
        final JSpinner     FANW_SPIN  = new JSpinner(new SpinnerNumberModel(57, 0.01, 180, 0.01));
        final JSpinner     FSPOT_SPIN = new JSpinner(new SpinnerNumberModel(foxels, 1, Math.max(9999, Math.max(width, height) * 4), 1));
        final JComboBox    CONVERGE   = new JComboBox(new CType[] {CType.NONE, CType.VARIANCE, CType.IDEAL_L1, CType.IDEAL_L2});
        final JSpinner     ROUND_SPIN = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        final JRadioButton WU_LINE    = new JRadioButton("Xiaolin Wu");
        final JRadioButton BRESSENHAM = new JRadioButton("Bressenham");
        final JTextArea    LOG_TEXT   = new JTextArea();
        final JLabel       TITLE_TEXT = new JLabel(TITLE_STR);

        // Arrange the radiobuttons into button groups.
        ButtonGroup lineAlg = new ButtonGroup();
        lineAlg.add(WU_LINE);
        lineAlg.add(BRESSENHAM);
        WU_LINE.setSelected(true);
        
        // Adjust the size of the parameter widgets.
        L_SPIN.setMaximumSize(L_SPIN.getPreferredSize());
        H_SPIN.setMaximumSize(H_SPIN.getPreferredSize());
        FANW_SPIN.setMaximumSize(FANW_SPIN.getPreferredSize());
        B_SPIN.setMaximumSize(B_SPIN.getPreferredSize());
        R_SPIN.setMaximumSize(R_SPIN.getPreferredSize());
        FSPOT_SPIN.setMaximumSize(FSPOT_SPIN.getPreferredSize());
        DW_SPIN.setMaximumSize(DW_SPIN.getPreferredSize());
        ROUND_SPIN.setMaximumSize(ROUND_SPIN.getPreferredSize());
        
        // Create the Foxels menu bars.
        BRUSH_BAR.add(new JLabel("Focal spot width:"));
        BRUSH_BAR.add(FSPOT_SPIN);
        BRUSH_BAR.add(new JLabel(" voxels"));
        BRUSH_BAR.addSeparator();
        BRUSH_BAR.add(new JLabel("Number of x-ray source finite elements (b):"));
        BRUSH_BAR.add(B_SPIN);
        BRUSH_BAR.addSeparator();
        BRUSH_BAR.add(new JLabel("Number of reconstruction foxels (h):"));
        BRUSH_BAR.add(H_SPIN);
        BRUSH_BAR.addSeparator();
        FAN_BAR.add(new JLabel("Gantry radius:"));
        FAN_BAR.add(R_SPIN);
        FAN_BAR.add(new JLabel(" voxels"));
        FAN_BAR.addSeparator();
        FAN_BAR.add(new JLabel("Fan beam width:"));
        FAN_BAR.add(FANW_SPIN);
        FAN_BAR.add(new JLabel(" degrees"));
        FAN_BAR.addSeparator();
        FAN_BAR.add(new JLabel("Detector width:"));
        FAN_BAR.add(DW_SPIN);
        FAN_BAR.add(new JLabel(" voxels"));
        FAN_BAR.addSeparator();
        FAN_BAR.add(new JLabel("MLS levels (# views = 2^[L+1]):"));
        FAN_BAR.add(L_SPIN);
        FAN_BAR.addSeparator();
        ALG_BAR.add(new JLabel("Line algorithm:"));
        ALG_BAR.add(WU_LINE);
        ALG_BAR.add(BRESSENHAM);
        ALG_BAR.addSeparator();
        ALG_BAR.add(new JLabel("Convergence:"));
        ALG_BAR.add(CONVERGE);
        ALG_BAR.add(ROUND_SPIN);
        ALG_BAR.addSeparator();
        
        // Create the main window panels (for viewing the images generated by
        // the Foxels and control algorithms).
        MAIN_PANEL.setLayout(new BoxLayout(MAIN_PANEL, BoxLayout.PAGE_AXIS));
        MAIN_PANEL.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JLabel("Foxels"), TBRUSH_AREA), BorderLayout.CENTER);
        
        // Create the panels for the line plot window.
        PLOT_PANEL.setLayout(new BoxLayout(PLOT_PANEL, BoxLayout.PAGE_AXIS));
        PLOT_PANEL.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JLabel("Foxels"), TLPLOT_AREA));
        
        // Create a new panel for displaying the program title information.
        TITLE_PANEL.setLayout(new BoxLayout(TITLE_PANEL, BoxLayout.LINE_AXIS));
        TITLE_PANEL.add(TITLE_TEXT);
        TITLE_TEXT.setFont(new Font("Monospace", Font.PLAIN, 18));

        // Create the "STOP" button for stopping the Foxels and control algorithms.
        final JButton STOP_BUTTON = new JButton("STOP");
        
        // Create the "GO" button for running the Foxels and control algorithms.
        final JButton GO_BUTTON = new JButton("GO");

        GO_BUTTON.setAction(new AbstractAction("GO") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                final int     L      = (Integer) L_SPIN.getValue();          // the number of "levels" to use for use in MLS
                final int     b      = (Integer) B_SPIN.getValue();          // the number of finite elements to use for X-ray aquisition (b in the Foxels paper).
                final int     h      = (Integer) H_SPIN.getValue();          // the number of foxels to use for reconstruction (h in the Foxels paper).
                final int     radius = ((Double) R_SPIN.getValue()).intValue();      // the radius of the gantry in voxels.
                final double  fanw   = (Double)  FANW_SPIN.getValue();       // the width of the fan-beam in degrees.
                final double  dnum   = (Double)  DW_SPIN.getValue();         // the width of x-ray detectors in the CT machine, specified as number of voxels.
                final int     fsw    = (Integer) FSPOT_SPIN.getValue();      // the Focal spot width in voxels
                final int     rounds = (Integer) ROUND_SPIN.getValue();      // the number of rounds to perform the algorithm if convergence is not selected.
                final CType   conv   = (CType)   CONVERGE.getSelectedItem(); // the type of convergence to use for the Foxels algorithm.
                final boolean use_wu = WU_LINE.isSelected();                 // if true, use the Wu line algorithm for projections, if false use the Bressenham line algorithm for projections.
                
                status_bar("Projection angle: " + L + "; starting MART");
                
                stop_foxels = false;
                
                // Run the Foxels and control algorithms in a separate thread.
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        STOP_BUTTON.setEnabled(true);
                        GO_BUTTON.setEnabled(false);
                        foxels_commandline(LOG_TEXT, hidden, twork, tlgfx, L, b, h, fanw, fsw, radius, dnum, conv, iteration_idx + rounds, use_wu);
                        
                        if (stop_foxels) {
                            iteration_idx--;
                        }
                        
                        status_bar("DONE! -- rounds: " + (iteration_idx - 1) + "; quality - L1: " + df2.format(L1qscore(twork, hidden))
                                + "; L2: " + df2.format(L2qscore(twork, hidden))
                                + (conv != CType.NONE ? "; convergence" : ""));
                        GO_BUTTON.setEnabled(true);
                        STOP_BUTTON.setEnabled(false);
                    }
                }).start();
            }
        });
        
        STOP_BUTTON.setAction(new AbstractAction("STOP") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                stop_foxels = true;
                GO_BUTTON.setEnabled(true);
                STOP_BUTTON.setEnabled(false);
            }
        });
        
        STOP_BUTTON.setEnabled(false);
        
        ALG_BAR.add(GO_BUTTON);
        
        ALG_BAR.add(STOP_BUTTON);

        // Create the "SAVE" button.
        ALG_BAR.add(new JButton(new AbstractAction("SAVE") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                File directory = new java.io.File(".");
                JFileChooser chooser = new JFileChooser();

                // Configure the save file chooser.
                chooser.setDialogTitle("Choose a directory to save the images");
                chooser.setCurrentDirectory(directory);
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                
                // If the user pressed the OK button, save the images (algorithm
                // generated images and line plots).
                if (chooser.showSaveDialog(null) ==  JFileChooser.APPROVE_OPTION
                        && (directory = chooser.getSelectedFile()) != null) {
                    status_bar("Saving...");
                    
                    if ((directory.exists() || directory.mkdirs()) && directory.canWrite()) {
                        // The next block of code writes the log data to a text file.
                        File logfile = new File(directory, "foxels_log.txt");
                        
                        try {
                            FileWriter  lout = new FileWriter(logfile);
                            
                            saveImage(oimage,  directory, "original_image.png");
                            saveImage(tcanvas, directory, "foxels_image.png");
                            saveImage(tlplot,  directory, "foxels_lineplot.png");
                            
                            lout.write(LOG_TEXT.getText());
                            lout.flush();
                            lout.flush();
                            lout.close();
                            JOptionPane.showMessageDialog(main_window, "Files written successfully");
                        } catch (IOException ioe) {
                            status_bar("Error saving: " + ioe.getMessage());
                            JOptionPane.showMessageDialog(main_window, "Error writing files");
                            ioe.printStackTrace(System.err);
                        }
                    } else {
                        status_bar("Directory error: " + directory);
                    }
                } else {
                    status_bar("Save cancelled");
                }
            }
        }));
        
        // Create the "PLOT" button, which opens the lineplot window.
        ALG_BAR.add(new JButton(new AbstractAction("PLOT") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                plot_window.setVisible(true);
                plot_window.pack();
                plot_window.setSize(640, 480);
            }
        }));
        
        // Create the "SHOW" button, for viewing the hidden image.
        ALG_BAR.add(new JButton(new AbstractAction("SHOW") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.arraycopy(hidden, 0, twork, 0, hidden.length);
                display();
            }
        }));
        
        // Create the "RESET IMAG" button, for resetting the canvas images for
        // both the Foxels and control algorithms.
        ALG_BAR.add(new JButton(new AbstractAction("RESET IMAGE") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                status_bar("Reset data!");
                reset ();
            }
        }));
        
        // Add an action listener to enable/disable the ROUND_SPIN object
        // depending on whether convergence is selected.
        CONVERGE.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                // the convegence type currently selected.
                final CType   conv   = (CType)   CONVERGE.getSelectedItem();
                
                ROUND_SPIN.setEnabled(CType.NONE == conv || CType.NONE.equals(conv));
            }
        });
        
        // Clear the Foxels and control canvases.
        reset ();
        LOG_TEXT.setText("");
        
        // Add the menu bars to the Foxels main window.
        JPanel BAR_PANEL = new JPanel();
        
        BAR_PANEL.setLayout(new BoxLayout(BAR_PANEL, BoxLayout.PAGE_AXIS));
        BAR_PANEL.add(TITLE_PANEL);
        BAR_PANEL.add(BRUSH_BAR);
        BAR_PANEL.add(FAN_BAR);
        BAR_PANEL.add(ALG_BAR);
        
        BRUSH_BAR.add(new Box.Filler(new Dimension(5, 5), new Dimension(5, 5),
                    new Dimension(Short.MAX_VALUE, 100)));
        ALG_BAR.add(new Box.Filler(new Dimension(5, 5), new Dimension(5, 5),
                    new Dimension(Short.MAX_VALUE, 100)));
        FAN_BAR.add(new Box.Filler(new Dimension(5, 5), new Dimension(5, 5),
                    new Dimension(Short.MAX_VALUE, 100)));
        TITLE_PANEL.add(new Box.Filler(new Dimension(5, 5), new Dimension(5, 5),
                    new Dimension(Short.MAX_VALUE, 100)));
        
        BRUSH_BAR.setFloatable(false);
        ALG_BAR.setFloatable(false);
        FAN_BAR.setFloatable(false);
        
        // Add the lineplot panel to the lineplot window.
        PLOT_PANEL.add(new Box.Filler(new Dimension(5, 5), new Dimension(5, 5),
                    new Dimension(Short.MAX_VALUE, 100)));
        
        // Add the main panel to the Foxels window.
        MAIN_PANEL.add(new Box.Filler(new Dimension(5, 5), new Dimension(5, 5),
                    new Dimension(Short.MAX_VALUE, 100)));
        
        // Configure and display the main window.
        main_window.setTitle(TITLE_STR);
        main_window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main_window.setLayout(new BorderLayout());
        main_window.add(BAR_PANEL,   BorderLayout.NORTH);
        main_window.add(new JScrollPane(MAIN_PANEL, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        main_window.add(STATUS_BAR,  BorderLayout.SOUTH);
        main_window.setVisible(true);
        main_window.pack();
        main_window.setSize(640, 480);
        main_window.setMinimumSize(BAR_PANEL.getPreferredSize());
        
        log_window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        log_window.setLayout(new BorderLayout());
        log_window.add(new JScrollPane(LOG_TEXT, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        log_window.setVisible(true);
        log_window.pack();
        log_window.setSize(300, 300);
        log_window.setLocationRelativeTo(main_window);
        log_window.setMinimumSize(new Dimension(200,200));
        
        // Configure, but do not display, the lineplot window.
        plot_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        plot_window.setLayout(new BorderLayout());
        plot_window.add(new JScrollPane(PLOT_PANEL, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
    }
    
    /**
     * Starts the foxels algorithm using a given set of command line parameters.
     * This method is a wrapper method for running not only the foxels
     * algorithm, but also the control algorithm.  In addition, this method
     * generates the quality scores and line plots for the data.
     **
     * @param log_text the JTextArea object to write log data to.  If null, the
     *                 log output is written to System.out instead.
     * @param hidden the hidden image being scanned by the CT machine.
     * @param work the array matrix to run the foxels algorithm on.
     * @param lpgfx the graphics object to write the lineplot to.
     * @param L the number of levels to use in the Guan and Gordon 1994 MLS algorithm.
     * @param felem the number of X-ray finite elements (b, in the Foxels paper).
     * @param foxels the number of foxels to divide the X-ray source, when reconstructing the image  (h, in the Foxels paper).
     * @param fan_width the width of the fan beam in degrees.
     * @param focal_spot the size (in voxels) of the X-ray source focal spot.
     * @param radius the radius of the gantry (in voxels).
     * @param dwidth the width of the detectors in the CT machine, specified as number of voxels.
     * @param converge whether or not to perform convergence.
     * @param rounds determines how many rounds to run the program (if convergence is set to CType.NONE)
     * @param wu_line  whether to use the Xiaolin Wu (true) or the Bressenham (false) line algorithm for generating the projections.
     */
    public static void foxels_commandline(final JTextArea log_text, float[] hidden, float[] work, final Graphics lpgfx, final int L, final int felem, final int foxels, final double fan_width, final int focal_spot, final int radius, final double dwidth, final CType converge, final int rounds, final boolean wu_line) {
        
        // Keep track of the total amount of time for running the algorithm.
        final long start_time = System.currentTimeMillis();
        
        // Find the centre X and Y co-ordinates of the image.
        final double cx = width / 2.;
        final double cy = height / 2.;

        // Calculate the arc length of the fan beam (in voxels).
        final double fanarc    = Math.toRadians(fan_width) * radius * 2.;
        
        // Determine the number of detectors in the CT machine.
        final int    detectors = (int) Math.floor(fanarc / dwidth);
        
        // Determine the width between detectors
        final double dstep     = Math.max((fan_width / (double) detectors), MIN_ANGLE);

        // Determine the angular distance between finite elements in the X-ray source (in degrees).
        final double fe_width  = Math.max(Math.toDegrees(focal_spot / (double) (radius * 2.)) / felem, MIN_ANGLE);

        // Determine the angular distance between foxels (in degrees).
        final double rfox_mod  = Math.max(Math.toDegrees(focal_spot / (double) (radius * 2.)) / foxels, MIN_ANGLE);

        // A normalization ratio used to make the image grayscale values
        // comparable between 
        final double normalize = ((double) felem) / foxels;
                
        // Get the angular geometry data for projection angle calculations.
        final double angles[] = easy_angles(L);
        
        // Store the sums for quick iterations (if doing multiple iterations).
        // This is helpful, because the detector sums will not change, as the
        // hidden image is static.
        final double sum_cache[] = new double[angles.length * detectors];
        Arrays.fill(sum_cache, -1);
        
        // Variables used for calculating the quality score of the images
        // generated by the Foxels and control algorithms.
        double  l1qscore       = 0;
        double  l2qscore       = 0;
        
        // A temporary variable used to store whether the algorithm has
        // converged (i.e. whether or not to stop the main foxels loop.
        boolean not_converged  = false;
        
        // If the JTextArea is set, create a string buffer to temporarily store
        // the log data before writing it to the TextArea, otherwise use
        // System.out for writing the output.  The creation of a string buffer
        // is done to streamline the code for writing log data.
        Appendable log         = System.out;
        if (log_text != null) {
            log = new StringBuffer();
        }

        // Variables used for convergence
        float[] next_work  = new float[work.length];
        double variance    = 0;
        
        System.arraycopy(work, 0, next_work, 0, work.length);
        
        // Set the position to obtain a line plot.  This should be half of the
        // height of the hidden image.
        final int cursor_y = height / 2;
        
        // Print the header information if this is the first iteration.
        if (iteration_idx == 1) {
            try {
                // Print debug information to System.out.
                
                log.append("Starting foxels...\n");
                log.append("==================\n");
                log.append("Source Image:       ").append(String.valueOf(ofile.getAbsolutePath())).append("\n");
                log.append("   - Size:          ").append(String.valueOf(width)).append("x").append(String.valueOf(height)).append(" voxels\n");
                log.append("   - Avg grayscale: ").append(String.valueOf(avg_grey(hidden))).append("\n");
                log.append("   - Entropy:       ").append(String.valueOf(entropy(hidden))).append("\n\n");
                log.append("CT machine\n");
                log.append("   - Resolution (L):    ").append(String.valueOf(L)).append(" MLS levels\n");
                log.append("   - # of foxels:       ").append(String.valueOf(foxels)).append(" foxels\n");
                log.append("   - # of finite elem.: ").append(String.valueOf(felem)).append(" elements\n");
                log.append("   - detector width:    ").append(String.valueOf(dwidth)).append(" voxels (").append(String.valueOf(dstep)).append(" degrees)\n");
                log.append("   - # of detectors:    ").append(String.valueOf(detectors)).append("\n");
                log.append("   - Fan width:         ").append(String.valueOf(fan_width)).append(" degrees\n");
                log.append("   - Gantry radius:     ").append(String.valueOf(radius)).append(" voxels\n");
                log.append("   - Focal spot width:  ").append(String.valueOf(focal_spot)).append(" voxels\n");
                log.append("Line algorithm:     ").append((wu_line ? "Xiaolin Wu" : "Bressenham\n"));
                log.append("\n\nMLS Angles:\n");
                
                for (int idx = 0; idx < angles.length; idx++) {
                    log.append("    [#").append(String.valueOf(idx + 1)).append("]: ").append(String.valueOf(angles[idx])).append(" degrees\n");
                }
                
                log.append("\n");
                
                // Flush the output (if System.out), or append the output to
                // the JTextArea (if set).  This step is necessary, so the
                // log output will display appropriately.
                if (log == System.out) {
                    System.out.flush();
                    System.out.flush();
                } else if (log_text != null) {
                    log_text.append(log.toString());
                    log = new StringBuffer();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace(System.err);
            }
        }
        
        do {
            // Keep track of the amount of time per iteration.
            final long time_loop = System.currentTimeMillis();
            
            // Back up the arrays (for convergence) - so we have Ai & Ai+1.
            System.arraycopy(next_work, 0, work, 0, work.length);
            
            // Display the working image.
            display();
            
            // Update the status bar, and run the foxels algorithm.
            status_bar("MART started (round " + iteration_idx + ")");


            // Iterate through each projection, and process it using each algorithm.
            for (int a_idx = 0; a_idx < angles.length && !stop_foxels; a_idx ++) {
                double theta = angles[a_idx];
                
                status_bar("MART started (round " + iteration_idx + ") - fan beam: " + theta + " degrees (view #" + a_idx + "/" + angles.length + ")");

                // do the foxels test
                double sum = 0, est = 0, dose = 0;

                // Handle fan beams.
                for (int d_num = 0; d_num < detectors && !stop_foxels; d_num ++) {
                    final double beta  = theta - (fan_width / 2.) + (d_num * dstep);

                    sum = 0.;
                    est = 0.;

                    // Use a sum values cache to speed up multiple iterations.
                    if (sum_cache[a_idx + d_num * angles.length] < 0) {
                        // Read the X-ray projections as if they were being read by a
                        // detector.
                        for (int spot_off = 0; spot_off < felem; spot_off++) {
                            final double spot_theta = theta - (((felem / 2.) - spot_off) * fe_width);
                            final double spot_beta  = beta;

                            double line [] = calc_line(cx, cy, spot_theta, spot_beta, radius);

                            if (wu_line) {
                                sum += xiaolin_read_sum(hidden, (int) Math.round(line[0]), (int) Math.round(line[1]),
                                        (int) Math.round(line[2]), (int) Math.round(line[3]), width);
                            } else {
                                sum += bresenham_read_sum(hidden, (int) Math.round(line[0]), (int) Math.round(line[1]),
                                        (int) Math.round(line[2]), (int) Math.round(line[3]), width);
                            }
                        }
                        sum_cache[a_idx + d_num * angles.length] = sum;
                    } else {
                        sum = sum_cache[a_idx + d_num * angles.length];
                    }

                    // Normalize the sum (i.e. to make sure the grayscale values are
                    // comparable.
                    sum = sum / normalize;

                    // Read equivalent the voxel matrix projections in the current
                    // working canvas image, as the extimate used for MART.
                    for (int rfox_off = 0; rfox_off < foxels; rfox_off++) {
                        final double fox_theta = theta - (((foxels / 2.) - rfox_off) * rfox_mod);
                        final double fox_beta  = beta;

                        double line [] = calc_line(cx, cy, fox_theta, fox_beta, radius);

                        if (wu_line) {
                            est += xiaolin_read_sum(next_work, (int) Math.round(line[0]), (int) Math.round(line[1]),
                                    (int) Math.round(line[2]), (int) Math.round(line[3]), width);
                        } else {
                            est += bresenham_read_sum(next_work, (int) Math.round(line[0]), (int) Math.round(line[1]),
                                    (int) Math.round(line[2]), (int) Math.round(line[3]), width);
                        }
                    }

                    // Write back the data using the appropriate algorithm,
                    // i.e. use foxels if applicable.
                    for (int rfox_off = 0; rfox_off < foxels; rfox_off++) {
                        final double fox_theta = theta - (((foxels / 2.) - rfox_off) * rfox_mod);
                        final double fox_beta  = beta;

                        double line [] = calc_line(cx, cy, fox_theta, fox_beta, radius);


                        if (wu_line) {
                            xiaolin_write_sum(next_work, (int) Math.round(line[0]), (int) Math.round(line[1]),
                                    (int) Math.round(line[2]), (int) Math.round(line[3]), width, sum / foxels, est / foxels);
                        } else {
                            bresenham_write_sum(next_work, (int) Math.round(line[0]), (int) Math.round(line[1]),
                                    (int) Math.round(line[2]), (int) Math.round(line[3]), width, sum / foxels, est / foxels);
                        }
                    }
                    dose += sum;
                }
                dose = (dose > 0 ? Math.log10(dose) : 0);
            }

            status_bar("Foxels MART finished, cleaning up!");

            // Evaluate the quality of the image by doing a root-mean square
            // difference from the original image, AND normalize the value by
            // multiplying it by the average grayscale value for the original image.
            status_bar("Foxels MART done!  Evaluating quality...");

            // Calculate the convergence variance.
            prev_var = variance;
            variance = calc_var(next_work);
            
            // calculate the quality scores for the image
            prev_l1qscore  = l1qscore;
            prev_l2qscore  = l2qscore;
            l1qscore       = L1qscore(next_work, hidden);
            l2qscore       = L2qscore(next_work, hidden);

            not_converged = (converge == CType.VARIANCE && !(Math.abs(variance - prev_var) < (prev_var / 100)))
                || (converge == CType.IDEAL_L1  && prev_l1qscore < l1qscore)
                || (converge == CType.IDEAL_L2  && prev_l2qscore < l2qscore);
            
            if (not_converged || converge == CType.NONE) {
                try {
                    // Prepare for the next loop iteration.
                    log.append("Round #" + iteration_idx + "\n");
                    log.append("   Time:         " + df2.format((System.currentTimeMillis() - time_loop) / 1000.) + " sec\n");
                    log.append("   Variance:     " + variance + "\n");
                    log.append("   Quality (L1): " + l1qscore + "\n");
                    log.append("   Quality (L2): " + l2qscore + "\n");
                    log.append("   Avg value:    " + avg_grey(next_work) + "\n");
                    log.append("   Entropy:      " + entropy(next_work) + "\n");
                    log.append("\n");
                    
                    // Flush the output (if System.out), or append the output to
                    // the JTextArea (if set).  This step is necessary, so the
                    // log output will display appropriately.
                    if (log == System.out) {
                        System.out.flush();
                        System.out.flush();
                    } else if (log_text != null) {
                        log_text.append(log.toString());
                        log = new StringBuffer();
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace(System.err);
                }
                
                // Increment the iteration index.
                iteration_idx++;
            }
            
            if (!stop_foxels && converge == CType.NONE) {
                // If convergence is not being performed, then update the arrays.
                System.arraycopy(next_work, 0, work, 0, work.length);
            }
        } while (!stop_foxels && (not_converged || (converge == CType.NONE && iteration_idx < rounds)));
        
        // Prevent any further functions from working on next_work.
        next_work = work;
        
        // Display the working image.
        display();

        // calculate the quality scores for the image
        l1qscore   = L1qscore(work, hidden);
        l2qscore   = L2qscore(work, hidden);
        
        // Update the line plot information.
        status_bar("Foxels MART & Quality evaluation done!  Line plotting... " + cursor_y);
        do_lineplot(work, lpgfx, cursor_y, width);
        display();
        status_bar("done lineplot: " + cursor_y);
        
        // If we are converging, print out a summary statement.
        if (converge != CType.NONE) {
            try {
                double total_secs = (System.currentTimeMillis() - start_time) / 1000.;
                double seconds    = total_secs % 60.;
                int    minutes    = (int) Math.round(total_secs / 60) % 60;
                int    hours      = (int) Math.round(total_secs / 3600);
                log.append("Final score (higher is better), " + (iteration_idx - 1) + " rounds to converge\n");
                log.append("   Variance:     " + variance + "\n");
                log.append("   Quality (L1): " + l1qscore + "\n");
                log.append("   Quality (L2): " + l2qscore + "\n");
                log.append("   Avg value:    " + avg_grey(work) + "\n");
                log.append("   Entropy:      " + entropy(next_work) + "\n");
                log.append("   Total Time:   "
                        + (hours > 0 ? hours + " hours " : "")
                        + (minutes > 0 ? minutes + " mins " : "")
                        + df2.format(seconds) + " sec\n");
                log.append("\n");
                
                // Flush the output (if System.out), or append the output to
                // the JTextArea (if set).  This step is necessary, so the
                // log output will display appropriately.
                if (log == System.out) {
                    System.out.flush();
                    System.out.flush();
                } else if (log_text != null) {
                    log_text.append(log.toString());
                    log = new StringBuffer();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace(System.err);
            }
        }
    }
    
    /**
     * Generate the array of angles using a simple intersecting algorithm.
     * This is an approximation of the multilevel scheme method described in
     * Guan and Gordon (1994).
     **
     * @param L  the total number of intersection levels.
     * @return an ordered array of gantry rotation angles (i.e. views).
     */
    public static double[] easy_angles (final int L) {
        // The current offset to write the next angle to the array.
        int offset = 4;
        
        // Create a new results array to store the angles for the current level.
        final double[] results = new double[(int) Math.pow(2, L + 1)];

        results[0] = 0;
        results[1] = 90;
        results[2] = 180;
        results[3] = 270;
        
        // Iterate through each level.
        for (int level = 1; level < L; level++) {
            // Determine how many angles exist for the current level L.
            int step   = (int) Math.pow(2, level - 1);

            // Itearate through all of the angles for the current level (L).
            for (int curr = 0; curr < step; curr ++) {
                double angle = (((1 + 2 * curr) * 45.) / step) % 90;
                
                // Write the next angle in the series to the end of the array.
                results[offset]     = angle;
                results[offset + 1] = 90 + angle;
                results[offset + 2] = 180 + angle;
                results[offset + 3] = 270 + angle;
                
                // Make sure we update the offset, such that we do not overwrite
                // any indices in the array of angles.
                offset += 4;
            }
        }

        return results;
    }
    
    /**
     * Determine the total intersection area between three views.
     * The formula used is derived from the Guan and Gordon 1994 paper on "the
     * multilevel scheme" (MLS) method.  The formula is as follows:
     * 
     *   A(theta) = |1 / sin(theta)| + |1 / sin(t0 - theta|.
     **
     * @param t0 the angle between two views.
     * @param theta the angle of a third view, relative to the first view.
     * @returns the total intersection area between three views.
     */
    public static double angle_area (final double t0, final double theta) {
        double sin1 = Math.abs(Math.sin(Math.toRadians(theta)));
        double sin2 = Math.abs(Math.sin(Math.toRadians(t0 - theta)));
        
        return 1.d / (sin1 != 0 ? sin1 : 0.0000001)
             + 1.d / (sin2 != 0 ? sin2 : 0.0000001);
    }
    
    
    
    /**
     * Saves an image to a file.
     **
     * @param canvas the buffered image object to save.
     * @param directory the path to save the file to.
     * @param filename the name of the file to save.
     * @throws IOException
     */
    public static void saveImage (BufferedImage canvas, File directory, String filename) throws IOException {
        File imagef = new File(directory, filename);
                
        if (imagef.exists() || imagef.createNewFile()) {
            ImageIO.write(canvas, "png", imagef);
        } else {
            throw new IOException("Error creating file: " + imagef);
        }
    }
    
    /**
     * Clears the canvases, so the user can re-run the Foxels and control
     * algorithms &quot;from scratch&quot;.
     */
    public static void reset () {
        // Reset the Foxels canvas image.
        twork = new float[width * height];
        Arrays.fill(twork, 128);
        
        // set-up the line plot area
        tlgfx.setColor(Color.WHITE);
        tlgfx.fillRect(0, 0, width, 256);
        
        iteration_idx = 1;
        prev_var    = 0;
        prev_l1qscore = 0;
        prev_l2qscore = 0;

        // refresh the display
        display();
    }
    
    /**
     * Read in a new image to process.
     **
     * @param filename the filename of the image to read.
     * @throws IOException is thrown if there are any problems reading the file.
     */
    private static void readImage(String filename) throws IOException {
        // Create a new file object for the image file.
        ofile   = new File(filename);
        
        // Read in the image;
        oimage  = ImageIO.read(ofile);
        
        WritableRaster raster = (WritableRaster) oimage.getRaster();
        final int num_bands   = raster.getNumBands();

        // Obtain the dimensions of the image.
        width  = oimage.getWidth();
        height = oimage.getHeight();

        // Create arrays to store all of the image pixel information.
        int[] reds = new int[width * height];
        int[] greens = new int[width * height];
        int[] blues = new int[width * height];

        // Generate new objects for storing the image information,
        // running the Foxels algorithm, and running the control algorithm.
        hidden = new float[width * height];
        twork  = new float[width * height];

        tcanvas     = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        traster     = (WritableRaster) tcanvas.getRaster();
        TBRUSH_AREA = new JLabel(new ImageIcon(tcanvas));

        // Generate objects for storing the lineplot information for the
        // Foxels algorithm.
        tlplot      = new BufferedImage(width, 256, BufferedImage.TYPE_INT_RGB);
        tlgfx       = tlplot.getGraphics();
        TLPLOT_AREA = new JLabel(new ImageIcon(tlplot));

        iteration_idx = 1;
        prev_var    = 0;
        prev_l1qscore = 0;
        prev_l2qscore = 0;

        // Determine whether or not the image is an RGB image.
        if (num_bands >= 3) {
            // For RGB images, read in all of the sample bands, and
            // average them to create a grayscale image.
            raster.getSamples(0, 0, width, height, 0, reds);
            raster.getSamples(0, 0, width, height, 1, greens);
            raster.getSamples(0, 0, width, height, 2, blues);

            for (int idx = 0; idx < hidden.length; idx++) {
                hidden[idx] = (int) Math.round((reds[idx] + greens[idx] + blues[idx]) / 3.);
            }
        } else {
            // For non-RGB images, determine how many bands.
            // For two band images, average each band to generate a
            // grayscale version of the image.  For single band images,
            // read it directly as grayscale. (not for code simplicity, we
            // treat a 1 band image as a 2 band image, where both bands are
            // the same - because we need to convert the bands from doubles
            // to integers, the big-O is the same as if we just read the
            // single bands directly).
            System.err.println("less than 3 bands!");
            raster.getSamples(0, 0, width, height, 0, reds);
            if (num_bands > 1) {
                System.err.println("    2 bands!");
                raster.getSamples(0, 0, width, height, 1, blues);
            } else {
                // For single band images, make the 
                blues = reds;
            }

            for (int idx = 0; idx < hidden.length; idx++) {
                hidden[idx] = (int) Math.round((reds[idx] + blues[idx]) / 2.);
            }
        }
    }
    
    /**
     * Update the canvases and line plots.
     */
    public static void display() {
        traster.setSamples(0, 0, width, height, 0, twork);
        traster.setSamples(0, 0, width, height, 1, twork);
        traster.setSamples(0, 0, width, height, 2, twork);

        TBRUSH_AREA.doLayout();
        TBRUSH_AREA.validate();
        TBRUSH_AREA.repaint(50L);

        TLPLOT_AREA.doLayout();
        TLPLOT_AREA.validate();
        TLPLOT_AREA.repaint(50L);
    }
    
    /**
     * Update the status bar.
     **
     * @param status the new text to display in the status bar.
     */
    public static void status_bar (String status) {
        STATUS_BAR.setText(status);
        STATUS_BAR.setBackground(Color.WHITE);
        STATUS_BAR.doLayout();
        STATUS_BAR.validate();
        STATUS_BAR.repaint(50L);
    }
    
/******************************************************************************/
    
    //public static class GFXMath {

        /**
         * <p>Calculate an image quality score for a given array.</p>
         * 
         * <p>The formula used is the L1 difference between each
         *      voxel in the hidden image and the working canvas.</p>
         * 
         * <p>This corresponds to equation #4 in the manuscript.
         *    work is <i>A<sub>f</sub></i>, and hidden is <i>U</i>.</p>
         **
         * @param hidden the hidden image being scanned by the CT machine.
         * @param work the working canvas image being constructed by Foxels or MART.
         * @return the quality score for the image.
         */
        public static double L1qscore(float[] work, float[] hidden) {
            double rms = 0;

            // Loop through each index of the hidden and work matrices.
            for (int idx = 0; idx < work.length && idx < hidden.length; idx++) {
                // Calculate the difference between the work matrix and hidden
                // matrix for a given index.
                double delta = Math.abs(work[idx] - hidden[idx]);

                // Square the value of 'delta', and add it to rms.  Note that
                // 255 is the maximum grayscale value for a pixel in Java
                rms += delta / 255;
            }

            // Finally, multiply by the average grayscale value of the hidden
            // image, so as to make the number larger, and more visible.
            return (rms != 0 ? (100000 / rms) : Double.MAX_VALUE);
        }
        
        /**
         * <p>Calculate an image quality score for a given array.</p>
         * 
         * <p>The formula used is the root mean square difference between each
         *      voxel in the hidden image and the working canvas.</p>
         * 
         * <p>This corresponds to equation #4 in the manuscript.
         *    work is <i>A<sub>f</sub></i>, and hidden is <i>U</i>.</p>
         **
         * @param hidden the hidden image being scanned by the CT machine.
         * @param work the working canvas image being constructed by Foxels or MART.
         * @return the quality score for the image.
         */
        public static double L2qscore(float[] work, float[] hidden) {
            double rms = 0;

            // Loop through each index of the hidden and work matrices.
            for (int idx = 0; idx < work.length && idx < hidden.length; idx++) {
                // Calculate the difference between the work matrix and hidden
                // matrix for a given index.
                double delta = (work[idx] - hidden[idx]);

                // Square the value of 'delta', and add it to rms.  Note that
                // 255 is the maximum grayscale value for a pixel in Java
                rms += (delta * delta) / 255;
            }

            // Take the square root of rms, and divide it by the number of
            // total number of indices in the matrix squared.  Because the
            // arrays were implemented as 1D arrays instead of 2D arrays, for
            // speed purposes, hidden.length is the same as:
            // x_max * y_max * z_max, when comparing to the manuscript.
            rms = Math.sqrt(rms) / hidden.length;

            // Finally, multiply by the average grayscale value of the hidden
            // image, so as to make the number larger, and more visible.
            return (rms != 0 ? (1 / rms) : Double.MAX_VALUE);
        }

        /**
         * <p>Calculates the start and end X and Y co-ordinates for a line,
         *      given the centre X and Y co-ordinates, a radius, and two
         *      angles: (1) the angle of rotation of the X-ray source, and (2)
         *      the angle around the circle, where the X-ray detector receiving
         *      the data is located.</p>
         **
         * @param cx the centre X co-ordinate for the line (in voxels).
         * @param cy the centre Y co-ordinate for the line (in voxels).
         * @param alpha the angle around the circle where the X-ray source is located (in degrees).
         * @param beta the angle around the circle where the detector is located (in degrees).
         * @param radius the CT scanner gantry radius (i.e. the length of the line in voxels).
         * @return an array containing [x0, y0, x1, y1], where x0 and y0 are the
         *          starting co-ordinates for the line, and x1 and y1 are the
         *          finishing co-ordinates for the line.
         */
        public static double[] calc_line (double cx, double cy, double alpha, double beta, int radius) {
            // Convert the angles to radians.
            final double radsa   = Math.toRadians(alpha);
            final double radsb   = Math.toRadians(beta);
            
            // Calculate the sin and cosing values for each angle.
            final double sina   = Math.sin(radsa);
            final double cosa   = Math.cos(radsa);
            final double sinb   = Math.sin(radsb);
            final double cosb   = Math.cos(radsb);
            
            // Determine the starting point of the line.
            double x0 = cx + radius * cosa;
            double y0 = cy + radius * sina;

            // Simplification -- to speed up the algorithm:
            // fit the circle to the screen math (i.e. circle to square).
            double  line_x0 = 0, line_y0 = 0, line_x1 = 0, line_y1 = 0;

            // Handle the first part of the projection.
            if ((beta >= 225 && beta < 315) || (beta >= 45 && beta < 135)) {
                // x_alter is also known as c, the X-intercept of the line.
                // i.e. X = nY + c
                // the slope is the cotangent of the angle
                double slope    = cosb / sinb;
                double x_alter  = x0 - slope * y0;
                line_y1 = height - 1;
                line_x1 = line_y1 * slope + x_alter;

                line_x0 = x_alter;
            } else {
                // y_alter is also known as b, the Y-intercept of the line.
                // i.e. Y = mX + b
                // the slope is the tangent of the angle
                double slope   = sinb / cosb;
                double y_alter = y0 - slope * x0;
                line_x1 = width - 1;
                line_y1 = line_x1 * slope + y_alter;

                line_y0 = y_alter;
            }
            
            return new double[] { line_x0, line_y0, line_x1, line_y1 };
        }
        
        /**
         * Constructs a lineplot for a given canvas image.
         **
         * @param work the canvas image to construct the lineplot for.
         * @param gfx the graphics object to write the lineplot to.
         * @param y the Y co-ordinate of the horizontal line to get data for the
         *          lineplot from.
         * @param awidth the width of each row in the array (this is so we can
         *                  use 1D arrays to represent a 2D image - i.e. every
         *                  'awidth' values corresponds to a new row in the
         *                  pseudo-2D image matrix.
         */
        public static void do_lineplot(float[] work, Graphics gfx, int y, int awidth) {
            int pvalue = 0;
            float  max   = 0;

            // reset the lineplot graphics
            gfx.setColor(Color.WHITE);
            gfx.fillRect(0, 0, awidth, 256);
            gfx.setColor(Color.BLACK);
            

            for (int x = 0; x < awidth; x++) {
                int idx = Math.round(y) * awidth + Math.round(x);
                max = Math.max(max, work[idx]);
            }

            for (int x = 0; x < awidth; x++) {
                int idx = Math.round(y) * awidth + Math.round(x);
                int value = (int) Math.round(work[idx]);
                
                if (x > 0) {
                    gfx.drawLine(x - 1, 256 - pvalue, x, 256 - value);
                } else {
                    gfx.drawLine(x, 256 - value, x, 256 - value);
                }
                pvalue = value;
            }
        }
        
        /**
         * Determine the average grayscale value for a given array.
         **
         * @param matrix the float array to calculate the grayscale average for.
         * @return the average grayscale value for the matrix.
         */
        public static double avg_grey(float[] matrix) {
            double avg_grey = 0;

            for (int idx = 0; idx < matrix.length; idx++) {
                avg_grey += matrix[idx];
            }

            return (avg_grey / matrix.length);
        }
        
        /**
         * Determine the entropy for a given grayscale array.
         **
         * @param matrix the float array to calculate the entropy for.
         * @return the entropy of the matrix.
         */
        public static double entropy(float[] matrix) {
            double entropy = 0;

            for (int idx = 0; idx < matrix.length; idx++) {
                double value = (matrix[idx] / 255.d);
                entropy += value * Math.log(value);
            }

            return (0 - entropy) / matrix.length;
        }
        //One image measure especially relevant to MART is the entropy S = -sum Vij ln Vij where Viv = voxel value for (i,j) divided by 255. If not much fuss, please report this value too.
        
        /**
         * Determine the grayscale variance score for a given array.
         **
         * @param matrix the array to calculate the grayscale variance for.
         * @retun the variance for the matrix.
         */
        public static double calc_var(float[] matrix) {
            double variance = 0;
            final double average  = avg_grey(matrix);

            // Calculate the squared difference of all values in the matrix
            // from the average grayscale value in the matrix.
            for (int idx = 0; idx < matrix.length; idx++) {
                double tmp = (matrix[idx] - average);
                variance += (tmp * tmp);
            }

            // Variance is defined as the above squared difference divided by
            // the number of entries in the matrix squared (n^2).
            return variance / (matrix.length * (double) matrix.length);
        }
        
         /**
         * <p>This method emulates a single X-ray projection through the hidden
         *      image.  This method also returns the value for the same
         *      projection through the current working canvas matrix.</p>
         * 
         * <p>Adapted from Xiaolin Wu's algorithm:
         * http://rosettacode.org/wiki/Xiaolin_Wu's_line_algorithm#Java
         * </p>
         **
         * @param array the matrix to calculate the projection for.
         * @param x0 the starting X co-ordinate for the projection line.
         * @param y0 the starting Y co-ordinate for the projection line.
         * @param x1 the ending X co-ordinate for the projection line.
         * @param y1 the ending Y co-ordinate for the projection line.
         * @param awidth the width of each row in the array (this is so we can
         *                  use 1D arrays to represent a 2D image - i.e. every
         *                  'awidth' values corresponds to a new row in the
         *                  pseudo-2D image matrix.
         * @return the projection sum for the hidden image.
         */
        public static long xiaolin_read_sum(float[] array, double x0, double y0, 
                                              double x1, double y1, int awidth) {

            long sum = 0;
            boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
            
            if (steep) {
                // xiaolin_read_sum(hidden, work, y0, x0, y1, x1, awidth);
                double temp;
                
                temp = x0;
                x0 = y0;
                y0 = temp;
                temp = x1;
                x1 = y1;
                y1 = temp;
            }

            if (x0 > x1) {
                // xiaolin_read_sum(hidden, work, x1, y1, x0, y0, awidth);
                double temp;
                
                temp = x0;
                x0 = x1;
                x1 = temp;
                temp = y0;
                y0 = y1;
                y1 = temp;
            }

            double dx = x1 - x0;
            double dy = y1 - y0;
            double gradient = dy / dx;

            // handle first endpoint
            double xend = Math.round(x0);
            double yend = y0 + gradient * (xend - x0);
            double xgap = rfpart(x0 + 0.5);
            double xpxl1 = xend; // this will be used in the main loop
            double ypxl1 = ipart(yend);

            if (steep) {
                sum += get_idx(array, ypxl1, xpxl1, rfpart(yend) * xgap,    awidth);
                sum += get_idx(array, ypxl1 + 1, xpxl1, fpart(yend) * xgap, awidth);
            } else {
                sum += get_idx(array, xpxl1, ypxl1, rfpart(yend) * xgap,    awidth);
                sum += get_idx(array, xpxl1, ypxl1 + 1, fpart(yend) * xgap, awidth);
            }

            // first y-intersection for the main loop
            double intery = yend + gradient;

            // handle second endpoint
            xend = Math.round(x1);
            yend = y1 + gradient * (xend - x1);
            xgap = fpart(x1 + 0.5);
            double xpxl2 = xend; // this will be used in the main loop
            double ypxl2 = ipart(yend);

            if (steep) {
                sum += get_idx(array, ypxl2, xpxl2, rfpart(yend) * xgap,    awidth);
                sum += get_idx(array, ypxl2 + 1, xpxl2, fpart(yend) * xgap, awidth);
            } else {
                sum += get_idx(array, xpxl2, ypxl2, rfpart(yend) * xgap,    awidth);
                sum += get_idx(array, xpxl2, ypxl2 + 1, fpart(yend) * xgap, awidth);
            }

            // main loop
            for (double x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
                if (steep) {
                    sum += get_idx(array, ipart(intery), x, rfpart(intery),    awidth);
                    sum += get_idx(array, ipart(intery) + 1, x, fpart(intery), awidth);
                } else {
                    sum += get_idx(array, x, ipart(intery), rfpart(intery),    awidth);
                    sum += get_idx(array, x, ipart(intery) + 1, fpart(intery), awidth);
                }
                intery = intery + gradient;
            }
            
            return sum;
        }
    
         /**
         * <p>This method uses MART to adjust the values for a given projection
         *      in the image being constructed by either the plain MART or
         *      Foxels algorithm.</p>
         * 
         * <p>Adapted from Xiaolin Wu's algorithm:
         * http://rosettacode.org/wiki/Xiaolin_Wu's_line_algorithm#Java
         * </p>
         **
         * @param work the working canvas image being constructed by Foxels or MART.
         * @param x0 the starting X co-ordinate for the projection line.
         * @param y0 the starting Y co-ordinate for the projection line.
         * @param x1 the ending X co-ordinate for the projection line.
         * @param y1 the ending Y co-ordinate for the projection line.
         * @param awidth the width of each row in the array (this is so we can
         *                  use 1D arrays to represent a 2D image - i.e. every
         *                  'awidth' values corresponds to a new row in the
         *                  pseudo-2D image matrix.
         * @param sum the X-ray sum read by the 'detector'.
         * @param est the raysum for the same projection in the working memory matrix (i.e. 'work').
         * @return the new sum for the projection.
         */
        public static float xiaolin_write_sum(float[] work, double x0, double y0, 
                                              double x1, double y1, int awidth,
                                              double sum, double est) {

            final double ratio = (est > 0 ? sum / est : 1);
            float new_sum = 0.f;
            boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
            if (steep) {
                // xiaolin_read_sum(hidden, work, y0, x0, y1, x1, awidth);
                double temp;
                
                temp = x0;
                x0 = y0;
                y0 = temp;
                temp = x1;
                x1 = y1;
                y1 = temp;
            }

            if (x0 > x1) {
                // xiaolin_read_sum(hidden, work, x1, y1, x0, y0, awidth);
                double temp;
                
                temp = x0;
                x0 = x1;
                x1 = temp;
                temp = y0;
                y0 = y1;
                y1 = temp;
            }

            double dx = x1 - x0;
            double dy = y1 - y0;
            double gradient = dy / dx;

            // handle first endpoint
            double xend = Math.round(x0);
            double yend = y0 + gradient * (xend - x0);
            double xgap = rfpart(x0 + 0.5);
            double xpxl1 = xend; // this will be used in the main loop
            double ypxl1 = ipart(yend);

            if (steep) {
                new_sum += mart_idx(work, ratio, ypxl1, xpxl1, rfpart(yend) * xgap,    awidth);
                new_sum += mart_idx(work, ratio, ypxl1 + 1, xpxl1, fpart(yend) * xgap, awidth);
            } else {
                new_sum += mart_idx(work, ratio, xpxl1, ypxl1, rfpart(yend) * xgap,    awidth);
                new_sum += mart_idx(work, ratio, xpxl1, ypxl1 + 1, fpart(yend) * xgap, awidth);
            }

            // first y-intersection for the main loop
            double intery = yend + gradient;

            // handle second endpoint
            xend = Math.round(x1);
            yend = y1 + gradient * (xend - x1);
            xgap = fpart(x1 + 0.5);
            double xpxl2 = xend; // this will be used in the main loop
            double ypxl2 = ipart(yend);

            if (steep) {
                new_sum += mart_idx(work, ratio, ypxl2, xpxl2, rfpart(yend) * xgap,    awidth);
                new_sum += mart_idx(work, ratio, ypxl2 + 1, xpxl2, fpart(yend) * xgap, awidth);
            } else {
                new_sum += mart_idx(work, ratio, xpxl2, ypxl2, rfpart(yend) * xgap,    awidth);
                new_sum += mart_idx(work, ratio, xpxl2, ypxl2 + 1, fpart(yend) * xgap, awidth);
            }

            // main loop
            for (double x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
                if (steep) {
                    new_sum += mart_idx(work, ratio, ipart(intery), x, rfpart(intery),    awidth);
                    new_sum += mart_idx(work, ratio, ipart(intery) + 1, x, fpart(intery), awidth);
                } else {
                    new_sum += mart_idx(work, ratio, x, ipart(intery), rfpart(intery),    awidth);
                    new_sum += mart_idx(work, ratio, x, ipart(intery) + 1, fpart(intery), awidth);
                }
                intery = intery + gradient;
            }
            return new_sum;
        }
        
        /**
         * Support function for Xiaolin Wu's algorithm.
         * Returns the integer portion of a double.
         **
         * @param x the value to parse.
         * @return the integer portion of a double.
         */
        private static int ipart(double x) {
            return (int) x;
        }

        /**
         * Support function for Xiaolin Wu's algorithm.
         * Returns the decimal portion of a double.
         **
         * @param x the value to parse.
         * @return the decimal portion of a double.
         */
        private static double fpart(double x) {
            return x - Math.floor(x);
        }

        /**
         * Support function for Xiaolin Wu's algorithm.
         * Returns one minus the decimal portion of a double.
         **
         * @param x the value to parse.
         * @return one minus the decimal portion of a double.
         */
        private static double rfpart(double x) {
            return 1.0 - fpart(x);
        }
        
        /**
         * Support function for Xiaolin Wu's algorithm.
         * Returns the weighted value for a given index in an array matrix.
         **
         * @param array the matrix to process.
         * @param x the X co-ordinate within the matrix.
         * @param y the Y co-ordinate within the matrix.
         * @param weight the weight of the index within the matrix.
         * @param awidth the width of each row in the array (this is so we can
         *                  use 1D arrays to represent a 2D image - i.e. every
         *                  'awidth' values corresponds to a new row in the
         *                  pseudo-2D image matrix.
         * @return the weighted value for a given index.  This method will return 0 if the index is out of bounds.
         */
        private static double get_idx(float[] array, double x, double y, double weight, int awidth) {
            int idx = ((int) Math.round(x)) * awidth + (int) Math.round(y);
            return ((x >= 0 && x < width && y >= 0 && y < height)? (array[idx] * weight) : 0);
        }
        
        /**
         * Support function for Xiaolin Wu's algorithm.
         * Alters a given index in an array matrix, using the MART algorithm.
         * This is useful for the Foxels algorithm.
         **
         * @param array the matrix to process.
         * @param ratio the ratio between the X-ray sum read by the 'detector'
         *              and the raysum for the same projection in the working
         *              memory matrix (i.e. 'work').
         * @param x the X co-ordinate within the matrix.
         * @param y the Y co-ordinate within the matrix.
         * @param weight the weight of the index within the matrix.
         * @param awidth the width of each row in the array (this is so we can
         *                  use 1D arrays to represent a 2D image - i.e. every
         *                  'awidth' values corresponds to a new row in the
         *                  pseudo-2D image matrix.
         * @return the new (weighted) value for a given index.
         */
        private static float mart_idx(float[] array, double ratio, double x, double y, double weight, int awidth) {
            int   idx = ((int) Math.round(x)) * awidth + (int) Math.round(y);
            float val = 0.f;
            
            if (x >= 0 && x < width && y >= 0 && y < height) {
                val = (float) Math.min(255., array[idx] * ratio * weight + (1 - weight) * array[idx]);
                array[idx] = val;
            }
            return val;
        }
        
        /**
         * <p>This method emulates a single X-ray projection through the hidden
         *      image.  This method also returns the value for the same
         *      projection through the current working canvas matrix.</p>
         * 
         * <p>Adapted from Digital Differential Analyzer line algorithm:
         * http://www.sunshine2k.de/coding/java/Bresenham/RasterisingLinesCircles.pdf
         * </p>
         **
         * @param work the matrix to calculate the projection for.
         * @param x1 the starting X co-ordinate for the projection line.
         * @param y1 the starting Y co-ordinate for the projection line.
         * @param x2 the ending X co-ordinate for the projection line.
         * @param y2 the ending Y co-ordinate for the projection line.
         * @param awidth the width of each row in the array (this is so we can
         *                  use 1D arrays to represent a 2D image - i.e. every
         *                  'awidth' values corresponds to a new row in the
         *                  pseudo-2D image matrix.
         * @return the projection sum for the hidden image.
         */
        public static long bresenham_read_sum(float[] work, int x1, int y1, 
                                              int x2, int y2, int awidth) {
             long sum = 0;

            // MULTIPLE OCTANTS -- ACTIVE CODE
            // Bresenham algorithm for all 8 octants.
            /* Note:  in four octants, including the entire first quadrant, 
               my code produces exactly the same results as yours.  In the
               other four octants, it effectively makes the opposite decisions
               about the error = .5 case mentioned in Damian's e-mail. */

            // If slope is outside the range [-1,1], swap x and y
            boolean xy_swap = false;
            if (Math.abs(y2 - y1) > Math.abs(x2 - x1)) {
                xy_swap = true;
                int temp = x1;
                x1 = y1;
                y1 = temp;
                temp = x2;
                x2 = y2;
                y2 = temp;
            }

            // If line goes from right to left, swap the endpoints
            if (x2 - x1 < 0) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }

            int x,                             // Current x position
                y = y1,                        // Current y position
                e = 0;                         // Current error
            final int m_num = y2 - y1,         // Numerator of slope
                      m_denom = x2 - x1,       // Denominator of slope
                      threshold  = m_denom/2;  // Threshold between E and NE increment 

            for (x = x1; x < x2; x++) {
                if (x >= 0 && y >= 0) {
                    if (xy_swap) {
                        if (y < width && x < height) {
                            int idx = x * awidth + y;
                            sum += work[idx];
                        }
                    } else {
                        if (x < width && y < height) {
                            int idx = y * awidth + x;
                            sum += work[idx];
                        }
                    }
                }

                e += m_num;

                // Deal separately with lines sloping upward and those
                // sloping downward
                if (m_num < 0) {
                    if (e < -threshold) {
                        e += m_denom;
                        y--;
                    }
                }
                else if (e > threshold) {
                    e -= m_denom;
                    y++;
                }
            }

            if (x >= 0 && y >= 0) {
                if (xy_swap) {
                    if (y < width && x < height) {
                        int idx = x * awidth + y;
                        sum += work[idx];
                    }
                } else {
                    if (x < width && y < height) {
                        int idx = y * awidth + x;
                        sum += work[idx];
                    }
                }
            }

            if (sum < 0) {
                System.err.println("NUMBER OVERFLOW!!!");
            }
            return sum;
        }
        
        /**
         * <p>This method uses MART to adjust the values for a given projection
         *      in the image being constructed by either the plain MART or
         *      Foxels algorithm.</p>
         * 
         * <p>Adapted from Digital Differential Analyzer line algorithm:
         * http://www.sunshine2k.de/coding/java/Bresenham/RasterisingLinesCircles.pdf
         * </p>
         **
         * @param work the working canvas image being constructed by Foxels or MART.
         * @param x1 the starting X co-ordinate for the projection line.
         * @param y1 the starting Y co-ordinate for the projection line.
         * @param x2 the ending X co-ordinate for the projection line.
         * @param y2 the ending Y co-ordinate for the projection line.
         * @param awidth the width of each row in the array (this is so we can
         *                  use 1D arrays to represent a 2D image - i.e. every
         *                  'awidth' values corresponds to a new row in the
         *                  pseudo-2D image matrix.
         * @param sum the X-ray sum read by the 'detector'.
         * @param est the raysum for the same projection in the working memory matrix (i.e. 'work').
         * @return the new sum for the projection.
         */
        public static float bresenham_write_sum(float[] work, int x1, int y1, 
                                              int x2, int y2, int awidth,
                                              double sum, double est) {
            float new_sum = 0;
            
            // MULTIPLE OCTANTS -- ACTIVE CODE
            // Bresenham algorithm for all 8 octants.
            /* Note:  in four octants, including the entire first quadrant, 
               my code produces exactly the same results as yours.  In the
               other four octants, it effectively makes the opposite decisions
               about the error = .5 case mentioned in Damian's e-mail. */

            // If slope is outside the range [-1,1], swap x and y
            boolean xy_swap = false;
            if (Math.abs(y2 - y1) > Math.abs(x2 - x1)) {
                xy_swap = true;
                int temp = x1;
                x1 = y1;
                y1 = temp;
                temp = x2;
                x2 = y2;
                y2 = temp;
            }

            // If line goes from right to left, swap the endpoints
            if (x2 - x1 < 0) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }

            int x,                             // Current x position
                y = y1,                        // Current y position
                e = 0;                       // Current array index
            final int m_num = y2 - y1,         // Numerator of slope
                      m_denom = x2 - x1,       // Denominator of slope
                      threshold  = m_denom/2;  // Threshold between E and NE increment 

            final double ratio = (est > 0 ? sum / est : 1);

            for (x = x1; x < x2; x++) {
                if (x >= 0 && y >= 0) {
                    if (xy_swap) {
                        if (y < width && x < height) {
                            int   idx = Math.round(x) * awidth + Math.round(y);
                            float new_value  = (float) Math.min(255.f, work[idx] * ratio);
                            work[idx] = new_value;
                            new_sum  += new_value;
                        }
                    } else {
                        if (x < width && y < height) {
                            int   idx = Math.round(y) * awidth + Math.round(x);
                            float new_value  = (float) Math.min(255.f, work[idx] * ratio);
                            work[idx] = new_value;
                            new_sum  += new_value;
                        }
                    }
                }

                e += m_num;

                // Deal separately with lines sloping upward and those
                // sloping downward
                if (m_num < 0) {
                    if (e < -threshold) {
                        e += m_denom;
                        y--;
                    }
                }
                else if (e > threshold) {
                    e -= m_denom;
                    y++;
                }
            }

            if (x >= 0 && y >= 0) {
                if (xy_swap) {
                    if (y < width && x < height) {
                        int   idx = Math.round(x) * awidth + Math.round(y);
                        float new_value  = (float) Math.min(255.f, work[idx] * ratio);
                        work[idx] = new_value;
                        new_sum  += new_value;
                    }
                } else {
                    if (x < width && y < height) {
                        int   idx = Math.round(y) * awidth + Math.round(x);
                        float new_value  = (float) Math.min(255.f, work[idx] * ratio);
                        work[idx] = new_value;
                        new_sum  += new_value;
                    }
                }
            }
            
            return new_sum;
        }
    //} -- end of GFXMath
}
