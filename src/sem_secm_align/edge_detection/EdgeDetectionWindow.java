/*
 * Created: 2022-12-01
 * Updated: 2023-01-10
 * Nathaniel Leslie
 */
package sem_secm_align.edge_detection;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import sem_secm_align.Visualizer;
import sem_secm_align.data_types.ImproperFileFormattingException;
import sem_secm_align.settings.EdgeDetectionSettings;

/**
 * The component that handles user-input pertaining to edge detection.
 * @author Nathaniel
 * @see EdgeDetectionSettings
 * @see EdgeHistogram
 * @see EdgeVisualizer
 */
public class EdgeDetectionWindow extends JFrame{
    
    /**
     * Insantiates this JFrame component and user inputs
     * @param parent the {@link Visualizer} component from which SECM and or SEM image information will be requested.
     */
    public EdgeDetectionWindow(Visualizer parent){
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.parent = parent;
        ed_settings = new EdgeDetectionSettings();
        this.getContentPane().removeAll();
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        
        //Set-up the control panel (user input controls in the top-left of the window
        JPanel control_panel = new JPanel();
        control_panel.setLayout(new GridBagLayout());
        
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.ipadx=DEFAULT_PAD;
        c.ipady=SPACER_PAD;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        
        control_panel.add(new JLabel("Image:"), c);
        
        c.gridx = 1;
        c.weightx = 0;
        c.ipadx=SPACER_PAD;
        
        control_panel.add(new JLabel(""), c);
        
        c.gridx = 2;
        c.weightx = 1;
        c.ipadx=DEFAULT_PAD;
        
        image_sources = new JComboBox(ed_settings.IMAGE_SOURCE_OPTIONS);
        image_sources.setSelectedIndex(ed_settings.DEFAULT_IMAGE_SOURCE);
        image_sources.addActionListener((ActionEvent e) -> {
            imageSourceChange();
        });
        control_panel.add(image_sources,c);
        
        c.gridx = 0;
        c.gridy = 1;
        
        control_panel.add(new JLabel("Noise Filter:"), c);
        
        String[] filter_names = new String[ed_settings.FILTER_OPTIONS.length];
        for(int i = 0; i < filter_names.length; i++){
            filter_names[i] = ed_settings.FILTER_OPTIONS[i].getName();
        }
        
        c.gridx = 2;
        
        filter_options = new JComboBox(filter_names);
        filter_options.setSelectedIndex(ed_settings.DEFAULT_FILTER);
        filter_options.addActionListener((ActionEvent e) -> {
            filterChange();
        });
        control_panel.add(filter_options, c);
        
        c.gridx = 0;
        c.gridy = 2;
        
        control_panel.add(new JLabel("Threshold Minimum"), c);
        
        c.gridx = 2;
        
        threshold_min = new JTextField("Min");
        threshold_min.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                thresholdMinChange();
            }
        });
        threshold_min.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                thresholdMinFocusLost();
            }
        });
        control_panel.add(threshold_min, c);
        
        c.gridx = 0;
        c.gridy = 3;
        
        control_panel.add(new JLabel("Threshold Maximum"), c);
        
        c.gridx = 2;
        
        threshold_max = new JTextField("Max");
        threshold_max.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                thresholdMaxChange();
            }
        });
        threshold_max.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                thresholdMaxFocusLost();
            }
        });
        control_panel.add(threshold_max, c);
        
        c.gridx = 0;
        c.gridy = 4;
        
        control_panel.add(new JLabel("View"), c);
        
        c.gridx = 2;
        display_options = new JComboBox(ed_settings.DISPLAY_OPTIONS);
        display_options.setSelectedIndex(ed_settings.DEFAULT_DISPLAY_MODE);
        display_options.addActionListener((ActionEvent e) -> {
            displayChange();
        });
        control_panel.add(display_options, c);
        
        c.gridx = 0;
        c.gridy = 5;
        
        apply_option = new JButton("Apply");
        apply_option.addActionListener((ActionEvent e) -> {
            applyDetection();
        });
        control_panel.add(apply_option, c);
        
        c.gridx = 2;
        
        close_option = new JButton("Close");
        close_option.addActionListener((ActionEvent e) -> {
            closeDetection();
        });
        control_panel.add(close_option, c);
        
        c.gridy = 6;
        c.weighty = 1;
        
        control_panel.add(new JLabel(""), c);
        
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.ipadx = DEFAULT_PAD;
        c.ipady = DEFAULT_PAD;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        
        
        //set up the displays (the edge visualizer and histogram)
        JPanel display_panel = new JPanel();
        display_panel.setLayout(new GridBagLayout());
        
        edge_display = new EdgeVisualizer(this, ed_settings);
        display_panel.add(edge_display, c);
        
        c.gridy = 1;
        c.weighty = 0.5;
        
        edge_histogram = new EdgeHistogram(ed_settings);
        display_panel.add(edge_histogram, c);
        
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.ipadx = DEFAULT_PAD;
        c.ipady = DEFAULT_PAD;
        c.weightx = 0;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        
        this.add(control_panel, c);
        
        c.gridx = 1;
        c.weightx = 1;
        
        this.add(display_panel, c);
        this.pack();
        this.setSize(600, 600);
        
        //initialize inputs
        threshold_max_accepted = 0;
        threshold_min_accepted = 0;
        last_display = ed_settings.DEFAULT_DISPLAY_MODE;
        last_filter = ed_settings.DEFAULT_FILTER;
        last_image_source = ed_settings.DEFAULT_IMAGE_SOURCE;
        max = 0.0;
        min = 0.0;
        
        //initialize currents for edge detection
        double w = parent.getReactivityGridResolutionX();
        double h = parent.getReactivityGridResolutionY();
        if(parent.getSECMDisplayable()){
            edge_display.setUnfilteredData(parent.getSECMCurrents(), w, h);
            threshold_max_accepted = max;
            threshold_min_accepted = min;
            edge_histogram.setThresholdDomain(min, max);
        }
        else{
            JOptionPane.showMessageDialog(this, "No SECM image is currently loaded.", "No SECM image", JOptionPane.ERROR_MESSAGE);
        }
        
        
        this.setVisible(true);
    }
    
    public void setHistogramData(double[] magnitudes, int[] counts, double minimum, double maximum){
        edge_histogram.setHistogramData(magnitudes, counts);
        max = maximum;
        min = minimum;
    }
    
    private int applyDetection(){
        int[][] edges = edge_display.getEdges();
        
        // export edges
        parent.setSwitches(edges);
        return 1;
    }
    
    private void closeDetection(){
        this.dispose();
    }
    
    private void displayChange(){
        int di = display_options.getSelectedIndex();
        if(di >=0 && di < 4 && di != last_display){
            edge_display.setDisplayMode(di);
            last_display = di;
        }
    }
    
    private void filterChange(){
        int selected_filter = filter_options.getSelectedIndex();
        if(selected_filter != last_filter){
            last_filter = selected_filter;
            edge_display.setFilter(ed_settings.FILTER_OPTIONS[selected_filter]);
            forceThresholdUpdate();
        }
    }
    
    private void imageSourceChange(){
        int image_choice = image_sources.getSelectedIndex();
        if(image_choice != last_image_source){
            last_image_source = image_choice;
            double w = parent.getReactivityGridResolutionX();
            double h = parent.getReactivityGridResolutionY();
            switch (image_choice) {
                case EdgeDetectionSettings.IMAGE_SOURCE_SECM : 
                    if(parent.getSECMDisplayable()){
                        edge_display.setUnfilteredData(parent.getSECMCurrents(), w, h);
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "No SECM image is currently loaded.", "No SECM image", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case EdgeDetectionSettings.IMAGE_SOURCE_SEM : 
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
            forceThresholdUpdate();
        }
        
    }
    
    private void thresholdMaxChange(){
        try{
            String text = threshold_max.getText();
            if(text.equalsIgnoreCase("max")){
                threshold_max_accepted = max;
            }
            else{
                double newmax = Double.parseDouble(text);
                if(newmax < 0.0){
                    throw new NumberFormatException("max cannot be negative.");
                }
                threshold_max_accepted = newmax;
            }
            edge_display.setUpperThreshold(threshold_max_accepted);
            edge_histogram.setThresholdDomain(threshold_min_accepted, threshold_max_accepted);
        }
        catch(Exception ex){
            
        }
    }
    
    private void thresholdMaxFocusLost(){
        try{
            String text = threshold_max.getText();
            if(text.equalsIgnoreCase("max")){
                threshold_max_accepted = max;
            }
            else{
                double newmax = Double.parseDouble(text);
                if(newmax < 0.0){
                    throw new NumberFormatException("max cannot be negative.");
                }
                threshold_max_accepted = newmax;
            }
            edge_display.setUpperThreshold(threshold_max_accepted);
            edge_histogram.setThresholdDomain(threshold_min_accepted, threshold_max_accepted);
            if(threshold_max_accepted >= max){
                threshold_max.setText("Max");
            }
            else{
                threshold_max.setText(threshold_max_accepted + "");
            }
        }
        catch(Exception ex){
            
        }
    }
    
    private void thresholdMinChange(){
        try{
            String text = threshold_min.getText();
            if(text.equalsIgnoreCase("min")){
                threshold_min_accepted = min;
            }
            else{
                double newmin = Double.parseDouble(text);
                if(newmin < 0.0){
                    throw new NumberFormatException("min cannot be negative.");
                }
                threshold_min_accepted = newmin;
            }
            edge_display.setLowerThreshold(threshold_min_accepted);
            edge_histogram.setThresholdDomain(threshold_min_accepted, threshold_max_accepted);
        }
        catch(Exception ex){
            
        }
    }
    
    private void thresholdMinFocusLost(){
        try{
            String text = threshold_min.getText();
            if(text.equalsIgnoreCase("min")){
                threshold_min_accepted = min;
            }
            else{
                double newmin = Double.parseDouble(text);
                if(newmin < 0.0){
                    throw new NumberFormatException("min cannot be negative.");
                }
                threshold_min_accepted = newmin;
            }
            edge_display.setLowerThreshold(threshold_min_accepted);
            edge_histogram.setThresholdDomain(threshold_min_accepted, threshold_max_accepted);
            if(threshold_min_accepted <= min){
                threshold_min.setText("Min");
            }
            else{
                threshold_min.setText(threshold_min_accepted + "");
            }
        }
        catch(Exception ex){
            
        }
    }
    
    private void forceThresholdUpdate(){
        if(threshold_max_accepted >= max){
            threshold_max.setText("Max");
            threshold_max_accepted = max;
        }
        if(threshold_min_accepted <= min){
            threshold_min.setText("Min");
            threshold_min_accepted = min;
        }
        String text = threshold_max.getText();
        if(text.equalsIgnoreCase("max")){
            threshold_max_accepted = max;
        }
        text = threshold_min.getText();
        if(text.equalsIgnoreCase("min")){
            threshold_min_accepted = min;
        }
        
        edge_histogram.setThresholdDomain(threshold_min_accepted, threshold_max_accepted);
    }
    
    private double threshold_max_accepted;
    private double threshold_min_accepted;
    private int last_display;
    private int last_filter;
    private int last_image_source;
    private double max;
    private double min;
    private EdgeDetectionSettings ed_settings;
    
    private Visualizer parent;
    private EdgeVisualizer edge_display;
    private EdgeHistogram edge_histogram;
    private JComboBox image_sources;
    private JComboBox filter_options;
    private JButton apply_option;
    private JButton close_option;
    private JTextField threshold_max;
    private JTextField threshold_min;
    private JComboBox display_options;
    
    
    /**
     * The padding around most components, unless {@link #SPACER_PAD} is used instead.
     */
    private static final int DEFAULT_PAD = 3;
    /**
     * The padding used to separate different groups of user inputs.
     */
    private static final int SPACER_PAD = 5;
}
