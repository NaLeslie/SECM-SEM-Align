/*
 * Created: 2023-04-06
 * Updated: 2023-04-06
 * Nathaniel Leslie
 */
package sem_secm_align.edge_detection;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sem_secm_align.Visualizer;
import sem_secm_align.utility.filters.BinaryFilter;
import sem_secm_align.utility.filters.Close3;
import sem_secm_align.utility.filters.Close5;
import sem_secm_align.utility.filters.Close7;
import sem_secm_align.utility.filters.Open3;
import sem_secm_align.utility.filters.Open5;
import sem_secm_align.utility.filters.Open7;

/**
 * A dialog box that allows the user to perform morphological operations on the reactivity grid.
 * @author Nathaniel
 */
public class MorphologicalTransformationDialog extends JFrame{
    
    /**
     * Instantiate the morphological transformation dialog
     * @param parent The visualizer to which this dialog is to report
     * @throws IOException 
     */
    public MorphologicalTransformationDialog(Visualizer parent) throws IOException{
        this.parent = parent;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        
        control_panel.add(new JLabel("Operation:"), c);
        
        c.gridx = 1;
        c.weightx = 0;
        c.ipadx=SPACER_PAD;
        
        control_panel.add(new JLabel(""), c);
        
        c.gridx = 2;
        c.weightx = 1;
        c.ipadx=DEFAULT_PAD;
        
        String[] filter_names = new String[available_filters.length];
        for(int i = 0; i < filter_names.length; i++){
            filter_names[i] = available_filters[i].getName();
        }
        filter_options = new JComboBox(filter_names);
        control_panel.add(filter_options, c);
        
        c.gridy = 1;
        control_panel.add(new JLabel(""), c);
        
        c.gridx = 0;
        c.gridy = 2;
        
        apply_button = new JButton("Apply");
        apply_button.addActionListener((ActionEvent e) -> {
            applyDetection();
        });
        control_panel.add(apply_button, c);
        
        c.gridx = 2;
        
        cancel_button = new JButton("Close window");
        cancel_button.addActionListener((ActionEvent e) -> {
            cancelDetection();
        });
        control_panel.add(cancel_button, c);
        
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.ipadx = DEFAULT_PAD;
        c.ipady = DEFAULT_PAD;
        c.weightx = 0;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        
        this.add(control_panel, c);
        this.pack();
        this.setSize(300, 100);
        this.setTitle("SEM-SECM Image Aligner");
        Image icon = ImageIO.read(parent.getClass().getResource("IAlogo32.png"));
        this.setIconImage(icon);
        this.setVisible(true);
    }
    
    /**
     * Triggered when the apply button is pressed. This will send a filter to its parent that will be applied to the reactivity grid.
     * @see Visualizer#filterSwitches(sem_secm_align.utility.filters.BinaryFilter) 
     */
    private void applyDetection(){
        int selectedfilter = filter_options.getSelectedIndex();
        if(selectedfilter >= 0){
            parent.filterSwitches(available_filters[selectedfilter]);
        }
    }
    
    /**
     * Closes the window
     */
    private void cancelDetection(){
        this.dispose();
    }
    
    /**
     * Combo box for presenting the user with the list of {@link #available_filters}.
     */
    private JComboBox filter_options;
    
    /**
     * Button that applies the selected {@link #filter_options filter option} when pressed.
     */
    private JButton apply_button;
    
    /**
     * Button to close the window
     */
    private JButton cancel_button;
    
    /**
     * The list of filters and their labels
     */
    private BinaryFilter[] available_filters = new BinaryFilter[]{new Close3(), new Close5(), new Close7(), new Open3(), new Open5(), new Open7()};
    
    /**
     * The {@link Visualizer} element to which this window reports.
     */
    private Visualizer parent;
    
    /**
     * The padding around most components, unless {@link #SPACER_PAD} is used instead.
     */
    private static final int DEFAULT_PAD = 3;
    /**
     * The padding used to separate different groups of user inputs.
     */
    private static final int SPACER_PAD = 5;
}
