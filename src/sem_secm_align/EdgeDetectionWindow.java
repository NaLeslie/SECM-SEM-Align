/*
 * Created: 2022-12-01
 * Updated: 2022-12-06
 * Nathaniel Leslie
 */
package sem_secm_align;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import sem_secm_align.utility.filters.Filter;
import sem_secm_align.utility.filters.Gauss3;
import sem_secm_align.utility.filters.Gauss5;
import sem_secm_align.utility.filters.Gauss7;
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
        filter_options = new JComboBox(new String[]{"Test"});
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
        
        cancel_option = new JButton("Close");
        cancel_option.addActionListener((ActionEvent e) -> {
            cancelDetection();
        });
        this.add(cancel_option, c);
        
        this.pack();
        this.setVisible(true);
    }
    
    public int applyDetection(){
        // get the data for edge detection
        int image_choice = image_sources.getSelectedIndex();
        double[][] data_grid;
        switch (image_choice) {
            case 0 -> data_grid = parent.getSECMCurrents();
            case 1 -> data_grid = parent.getSEMSignals();
            default -> {
                    return 0;
            }
        }
        
        // apply noise filtering
        data_grid = available_filters[filter_options.getSelectedIndex()].applyFilter(data_grid);
        
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
                if(angles[x][y] <= Math.PI * 0.125 && angles[x][y] > Math.PI * -0.125){//0
                    if(angles[x + 1][y] <= Math.PI * 0.125 && angles[x + 1][y] > Math.PI * -0.125 && squares[x + 1][y] > squares[x][y]){
                        edges[x][y] = 0;
                    }
                    if(angles[x - 1][y] <= Math.PI * 0.125 && angles[x - 1][y] > Math.PI * -0.125 && squares[x - 1][y] > squares[x][y]){
                        edges[x][y] = 0;
                    }
                }
                else if(angles[x][y] <= Math.PI * 0.375 && angles[x][y] > Math.PI * 0.125){//pi/4
                    if(angles[x + 1][y + 1] <= Math.PI * 0.125 && angles[x + 1][y + 1] > Math.PI * -0.125 && squares[x + 1][y + 1] > squares[x][y]){
                        edges[x][y] = 0;
                    }
                    if(angles[x - 1][y - 1] <= Math.PI * 0.125 && angles[x - 1][y - 1] > Math.PI * -0.125 && squares[x - 1][y - 1] > squares[x][y]){
                        edges[x][y] = 0;
                    }
                }
                else if(angles[x][y] <= Math.PI * 0.625 && angles[x][y] > Math.PI * 0.375){//pi/2
                    if(angles[x][y + 1] <= Math.PI * 0.125 && angles[x][y + 1] > Math.PI * -0.125 && squares[x][y + 1] > squares[x][y]){
                        edges[x][y] = 0;
                    }
                    if(angles[x][y - 1] <= Math.PI * 0.125 && angles[x][y - 1] > Math.PI * -0.125 && squares[x][y - 1] > squares[x][y]){
                        edges[x][y] = 0;
                    }
                }
                else if(angles[x][y] <= Math.PI * 0.775 && angles[x][y] > Math.PI * 0.625){//3pi/4
                    if(angles[x - 1][y + 1] <= Math.PI * 0.125 && angles[x - 1][y + 1] > Math.PI * -0.125 && squares[x - 1][y + 1] > squares[x][y]){
                        edges[x][y] = 0;
                    }
                    if(angles[x + 1][y - 1] <= Math.PI * 0.125 && angles[x + 1][y - 1] > Math.PI * -0.125 && squares[x + 1][y - 1] > squares[x][y]){
                        edges[x][y] = 0;
                    }
                }
                else if(angles[x][y] > Math.PI * 0.775 || angles[x][y] <= -Math.PI * 0.775){//+-pi
                    if(angles[x + 1][y] <= Math.PI * 0.125 && angles[x + 1][y] > Math.PI * -0.125 && squares[x + 1][y] > squares[x][y]){
                        edges[x][y] = 0;
                    }
                    if(angles[x - 1][y] <= Math.PI * 0.125 && angles[x - 1][y] > Math.PI * -0.125 && squares[x - 1][y] > squares[x][y]){
                        edges[x][y] = 0;
                    }
                }
                else if(angles[x][y] < -Math.PI * 0.375 && angles[x][y] >= -Math.PI * 0.125){//-pi/4
                    if(angles[x - 1][y + 1] <= Math.PI * 0.125 && angles[x - 1][y + 1] > Math.PI * -0.125 && squares[x - 1][y + 1] > squares[x][y]){
                        edges[x][y] = 0;
                    }
                    if(angles[x + 1][y - 1] <= Math.PI * 0.125 && angles[x + 1][y - 1] > Math.PI * -0.125 && squares[x + 1][y - 1] > squares[x][y]){
                        edges[x][y] = 0;
                    }
                }
                else if(angles[x][y] < -Math.PI * 0.625 && angles[x][y] >= -Math.PI * 0.375){//-pi/2
                    if(angles[x][y + 1] <= Math.PI * 0.125 && angles[x][y + 1] > Math.PI * -0.125 && squares[x][y + 1] > squares[x][y]){
                        edges[x][y] = 0;
                    }
                    if(angles[x][y - 1] <= Math.PI * 0.125 && angles[x][y - 1] > Math.PI * -0.125 && squares[x][y - 1] > squares[x][y]){
                        edges[x][y] = 0;
                    }
                }
                else if(angles[x][y] < -Math.PI * 0.775 && angles[x][y] >= -Math.PI * 0.625){//-3pi/4
                    if(angles[x + 1][y + 1] <= Math.PI * 0.125 && angles[x + 1][y + 1] > Math.PI * -0.125 && squares[x + 1][y + 1] > squares[x][y]){
                        edges[x][y] = 0;
                    }
                    if(angles[x - 1][y - 1] <= Math.PI * 0.125 && angles[x - 1][y - 1] > Math.PI * -0.125 && squares[x - 1][y - 1] > squares[x][y]){
                        edges[x][y] = 0;
                    }
                }
            }
        }
        // export edges
        parent.setSwitches(edges);
        return 1;
    }
    
    public void cancelDetection(){
        this.dispose();
    }
    
    private Visualizer parent;
    private JComboBox image_sources;
    private JComboBox filter_options;
    private JButton apply_option;
    private JButton cancel_option;
    private final Filter[] available_filters = new Filter[]{new Gauss3(), new Gauss5(), new Gauss7(), new Median3()};
    
    /**
     * The padding around most components, unless <code>FIELD_PAD</code> or <code>SPACER_PAD</code> is used instead.
     */
    private static final int DEFAULT_PAD = 3;
    /**
     * The padding used to separate different groups of user inputs.
     */
    private static final int SPACER_PAD = 5;
}
