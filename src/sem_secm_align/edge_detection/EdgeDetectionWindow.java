/*
 * Created: 2022-12-01
 * Updated: 2022-12-19
 * Nathaniel Leslie
 */
package sem_secm_align.edge_detection;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import sem_secm_align.Tests;
import sem_secm_align.Visualizer;
import static sem_secm_align.Tests.export_2d_double;
import sem_secm_align.data_types.ImproperFileFormattingException;
import sem_secm_align.utility.filters.Filter;
import sem_secm_align.utility.filters.Gauss3;
import sem_secm_align.utility.filters.Gauss5;
import sem_secm_align.utility.filters.Gauss7;
import sem_secm_align.utility.filters.Identity;
import sem_secm_align.utility.filters.Median3;
import sem_secm_align.utility.filters.SobelX;
import sem_secm_align.utility.filters.SobelY;

/**
 *
 * @author Nathaniel
 */
public class EdgeDetectionWindow extends JFrame{
    
    public EdgeDetectionWindow(Visualizer parent){
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.parent = parent;
        this.getContentPane().removeAll();
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.ipadx=DEFAULT_PAD;
        c.ipady=SPACER_PAD;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        
        this.add(new JLabel("Image:"), c);
        
        c.gridx = 1;
        c.weightx = 0;
        c.ipadx=SPACER_PAD;
        
        this.add(new JLabel(""), c);
        
        c.gridx = 2;
        c.weightx = 1;
        c.ipadx=DEFAULT_PAD;
        
        image_sources = new JComboBox(new String[]{"SECM", "SEM"});
        image_sources.setSelectedIndex(0);
        this.add(image_sources,c);
        
        c.gridx = 0;
        c.gridy = 1;
        
        this.add(new JLabel("Noise Filter:"), c);
        
        String[] filter_names = new String[available_filters.length];
        for(int i = 0; i < filter_names.length; i++){
            filter_names[i] = available_filters[i].getName();
        }
        
        c.gridx = 2;
        
        filter_options = new JComboBox(filter_names);
        filter_options.setSelectedIndex(0);
        this.add(filter_options, c);
        
        c.gridx = 0;
        c.gridy = 2;
        
        apply_option = new JButton("Apply");
        apply_option.addActionListener((ActionEvent e) -> {
            applyDetection();
        });
        this.add(apply_option, c);
        
        c.gridx = 2;
        
        close_option = new JButton("Close");
        close_option.addActionListener((ActionEvent e) -> {
            closeDetection();
        });
        this.add(close_option, c);
        
        this.pack();
        this.setVisible(true);
    }
    
    public int applyDetection(){
        // get the data for edge detection
        int image_choice = image_sources.getSelectedIndex();
        double[][] data_grid;
        switch (image_choice) {
            case 0 : 
                if(parent.getSECMDisplayable()){
                    data_grid = parent.getSECMCurrents();
                    break;
                }
                else{
                    JOptionPane.showMessageDialog(this, "No SECM image is currently loaded.", "No SECM image", JOptionPane.ERROR_MESSAGE);
                    return 0;
                }
            case 1 : 
                if(parent.getSEMDisplayable()){
                    try {
                        data_grid = parent.getSEMSignals();
                    } catch (ImproperFileFormattingException ex) {
                        ex.printStackTrace();
                        return 0;
                    }
                    break;
                }
                else{
                    JOptionPane.showMessageDialog(this, "No SEM image is currently loaded.", "No SEM image", JOptionPane.ERROR_MESSAGE);
                    return 0;
                }
            default :
                return 0;
        }
        export_2d_double(data_grid, "prefilter.csv");
        // apply noise filtering
        data_grid = available_filters[filter_options.getSelectedIndex()].applyFilter(data_grid);
        export_2d_double(data_grid, "postfilter.csv");
        
        // apply sobel operators
        SobelX sx = new SobelX();
        SobelY sy = new SobelY();
        double[][] xgradients = sx.applyFilter(data_grid);
        double[][] ygradients = sy.applyFilter(data_grid);
        double[][] squares = new double[xgradients.length][xgradients[0].length];
        double[][] angles = new double[xgradients.length][xgradients[0].length];
        int[][] edges = new int[xgradients.length][xgradients[0].length];
        for(int x = 0; x < xgradients.length; x++){
            for(int y = 0; y < xgradients[0].length; y++){
                squares[x][y] = xgradients[x][y]*xgradients[x][y] + ygradients[x][y]*ygradients[x][y];
                angles[x][y] = Math.atan2(ygradients[x][y], xgradients[x][y]);
                edges[x][y] = 1;
            }
        }
        // find maximum slopes
        for(int x = 0; x < xgradients.length; x++){
            for(int y = 0; y < xgradients[0].length; y++){
                if((angles[x][y] <= Math.PI * 0.125 && angles[x][y] > Math.PI * -0.125) || (angles[x][y] > Math.PI * 0.775 || angles[x][y] <= -Math.PI * 0.775)){//0 or +-pi
                    if(x < angles.length - 1){
                        if(squares[x + 1][y] >= squares[x][y]){
                            edges[x][y] = 0;
                        }
                    }
                    if(x > 0){
                        if(squares[x - 1][y] >= squares[x][y]){
                            edges[x][y] = 0;
                        }
                    }
                }
                else if((angles[x][y] <= Math.PI * 0.375 && angles[x][y] > Math.PI * 0.125) || (angles[x][y] > -Math.PI * 0.775 && angles[x][y] <= -Math.PI * 0.625)){//pi/4 or -3pi/4
                    if(x < angles.length - 1 && y < angles[0].length - 1){
                        if(squares[x + 1][y + 1] >= squares[x][y]){
                            edges[x][y] = 0;
                        }
                    }
                    if(x > 0 && y > 0){
                        if(squares[x - 1][y - 1] >= squares[x][y]){
                            edges[x][y] = 0;
                        }
                    }
                }
                else if((angles[x][y] <= Math.PI * 0.625 && angles[x][y] > Math.PI * 0.375) || (angles[x][y] > -Math.PI * 0.625 && angles[x][y] <= -Math.PI * 0.375)){//pi/2 or -pi/2
                    if(y < angles[0].length - 1){
                        if(squares[x][y + 1] >= squares[x][y]){
                            edges[x][y] = 0;
                        }
                    }
                    if(y > 0){
                        if(squares[x][y - 1] >= squares[x][y]){
                            edges[x][y] = 0;
                        }
                    }
                }
                else if((angles[x][y] <= Math.PI * 0.775 && angles[x][y] > Math.PI * 0.625) || (angles[x][y] > -Math.PI * 0.375 && angles[x][y] <= -Math.PI * 0.125)){//3pi/4 or -pi/4
                    if(x > 0 && y < angles[0].length - 1){
                        if(squares[x - 1][y + 1] >= squares[x][y]){
                            edges[x][y] = 0;
                        }
                    }
                    if(x < angles.length - 1 && y > 0){
                        if(squares[x + 1][y - 1] >= squares[x][y]){
                            edges[x][y] = 0;
                        }
                    }
                }
            }
        }
        double[][] edge_magnitudes = new double[edges.length][edges[0].length];
        double min = Math.sqrt(squares[0][0]);
        double max = 0.0;
        int total_edge_pixels = 0;
        for(int x = 0; x < edges.length; x++){
            for(int y = 0; y < edges[0].length; y++){
                total_edge_pixels += edges[x][y];
                if(edges[x][y] > 0){
                    edge_magnitudes[x][y] = Math.sqrt(squares[x][y]);
                    if(edge_magnitudes[x][y] > max){
                        max = edge_magnitudes[x][y];
                    }
                    if(edge_magnitudes[x][y] < min){
                        min = edge_magnitudes[x][y];
                    }
                }
                else{
                    edge_magnitudes[x][y] = 0.0;
                }
            }
        }
        export_2d_double(edge_magnitudes, "edgemags.csv");
        int numbins = 20;
        double binwidth = (max - min) / ((double)numbins);
        int[] histogram = new int[numbins];
        for(int x = 0; x < edges.length; x++){
            for(int y = 0; y < edges[0].length; y++){
                total_edge_pixels += edges[x][y];
                if(edges[x][y] > 0){
                    if(edge_magnitudes[x][y] < max){
                        int index = (int)((edge_magnitudes[x][y] - min) / binwidth);
                        histogram[index] ++;
                    }
                    else{
                        histogram[numbins - 1] ++;
                    }
                }
            }
        }
        double[] mags = new double[numbins];
        for(int i = 0; i < numbins; i++){
            mags[i] = binwidth * ((double)i) + min;
        }
        
        Tests.export_histogram(mags, histogram, "hist.csv");
        
        // export edges
        parent.setSwitches(edges);
        return 1;
    }
    
    public void closeDetection(){
        this.dispose();
    }
    
    public void setHistogramData(double[] magnitudes, int[] counts){
        edge_histogram.setHistogramData(magnitudes, counts);
    }
    
    private void updateUnfilteredData(){
        // get the data for edge detection
        int image_choice = image_sources.getSelectedIndex();
        double w = parent.getReactivityGridResolutionX();
        double h = parent.getReactivityGridResolutionY();
        switch (image_choice) {
            case 0 : 
                if(parent.getSECMDisplayable()){
                    edge_display.setUnfilteredData(parent.getSECMCurrents(), w, h);
                }
                else{
                    JOptionPane.showMessageDialog(this, "No SECM image is currently loaded.", "No SECM image", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 1 : 
                if(parent.getSEMDisplayable()){
                    try {
                        edge_display.setUnfilteredData(parent.getSEMSignals(), w, h);
                    } catch (ImproperFileFormattingException ex) {
                        ex.printStackTrace();
                    }
                }
                else{
                    JOptionPane.showMessageDialog(this, "No SEM image is currently loaded.", "No SEM image", JOptionPane.ERROR_MESSAGE);
                }
                break;
            default :
                break;
        }
    }
    
    private Visualizer parent;
    private EdgeVisualizer edge_display;
    private EdgeHistogram edge_histogram;
    private JComboBox image_sources;
    private JComboBox filter_options;
    private JButton apply_option;
    private JButton close_option;
    private final Filter[] available_filters = new Filter[]{new Identity(), new Gauss3(), new Gauss5(), new Gauss7(), new Median3()};
    
    /**
     * The padding around most components, unless <code>FIELD_PAD</code> or <code>SPACER_PAD</code> is used instead.
     */
    private static final int DEFAULT_PAD = 3;
    /**
     * The padding used to separate different groups of user inputs.
     */
    private static final int SPACER_PAD = 5;
}
