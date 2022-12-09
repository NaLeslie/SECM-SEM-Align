/*
 * Created: 2022-01-14
 * Updated: 2022-06-22
 * Nathaniel Leslie
 */
package sem_secm_align;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.MathContext;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import sem_secm_align.data_types.SECMImage;
import sem_secm_align.data_types.SEMImage;
import sem_secm_align.data_types.Unit;
import sem_secm_align.settings.Settings;

/**
 * The main window for the program
 * @author Nathaniel
 */
public class MainWindow extends JFrame{
    
    /**
     * Launches a new instance of the main window
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public MainWindow(){
        
        SETTINGS = new Settings();
        view_screen = new Visualizer(this, SETTINGS);
        control_panel = new JTabbedPane();
        position_indicator = new JLabel("");
        
        //initialize internal settings
        secm_current_scale_factor   =   SETTINGS.UNITS_CURRENT[SETTINGS.DEFAULT_CURRENT_UNIT_SELECTION].getFactor();
        secm_distance_unit          =   SETTINGS.UNITS_DISTANCE[SETTINGS.DEFAULT_DISTANCE_UNIT_SELECTION];
        sem_scale_accepted          =   SETTINGS.DEFAULT_SEM_SCALE;
        sem_xoffs_accepted          =   SETTINGS.DEFAULT_SEM_XOFFSET;
        sem_yoffs_accepted          =   SETTINGS.DEFAULT_SEM_YOFFSET;
        sem_rotation_accepted       =   SETTINGS.DEFAULT_SEM_ROTATION;
        reac_xresolution_accepted   =   SETTINGS.DEFAULT_REAC_XRESOLUTION;
        reac_yresolution_accepted   =   SETTINGS.DEFAULT_REAC_YRESOLUTION;
        sam_start_x_accepted        =   SETTINGS.DEFAULT_SAM_XSTART_INDEX;
        sam_step_size_x_accepted    =   SETTINGS.DEFAULT_SAM_XSTEP;
        sam_num_steps_x_accepted    =   SETTINGS.DEFAULT_SAM_NUM_XSTEPS;
        sam_start_y_accepted        =   SETTINGS.DEFAULT_SAM_YSTART_INDEX;
        sam_step_size_y_accepted    =   SETTINGS.DEFAULT_SAM_YSTEP;
        sam_num_steps_y_accepted    =   SETTINGS.DEFAULT_SAM_NUM_YSTEPS;
        
        double def;
        double def_dist_fact = SETTINGS.UNITS_DISTANCE[SETTINGS.DEFAULT_DISTANCE_UNIT_SELECTION].getFactor();
        double def_reso_fact = SETTINGS.UNITS_RESOLUTION[SETTINGS.DEFAULT_RESOLUTION_UNIT_SELECTION].getFactor();
        
        this.getContentPane().removeAll();
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        // <editor-fold defaultstate="collapsed" desc="Main window">
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        this.add(view_screen, c);
        c.gridy = 1;
        c.weighty = 0;
        this.add(position_indicator, c);
        c.gridy = 2;
        this.add(control_panel, c);// </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="SECM tab">
        JPanel secm_tab = new JPanel();
        secm_tab.setLayout(new GridBagLayout());
        c.ipadx=DEFAULT_PAD;
        c.ipady=SPACER_PAD;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 0;
        secm_tab.add(new JLabel("File:"), c);
        c.gridx=0;
        c.gridy=1;
        c.fill = GridBagConstraints.NONE;
        secm_open_button = new JButton("Browse");
        secm_open_button.addActionListener((ActionEvent e) -> {
            secmOpen();
        });
        secm_tab.add(secm_open_button, c);
        c.gridx=1;
        c.fill = GridBagConstraints.BOTH;
        secm_file_field = new JLabel("No File.");
        secm_tab.add(secm_file_field, c);
        c.weightx=1;
        c.gridx=2;
        secm_tab.add(new JLabel(), c);
        c.weightx=0;
        c.gridx=0;
        c.gridy=2;
        secm_tab.add(new JLabel("Distance units:"), c);
        c.gridx=1;
        c.fill = GridBagConstraints.NONE;
        secm_distance_units = new JComboBox(SETTINGS.getDistanceUnitLabels());
        secm_distance_units.setSelectedIndex(SETTINGS.DEFAULT_DISTANCE_UNIT_SELECTION);
        secm_tab.add(secm_distance_units, c);
        secm_distance_units.addActionListener((ActionEvent e) -> {
            secmDistanceUnitsChange(e);
        });
        c.fill = GridBagConstraints.BOTH;
        c.gridx=0;
        c.gridy=3;
        secm_tab.add(new JLabel("Current units:"), c);
        c.gridx=1;
        c.fill = GridBagConstraints.NONE;
        secm_current_units = new JComboBox(SETTINGS.getCurrentUnitLabels());
        secm_current_units.setSelectedIndex(SETTINGS.DEFAULT_CURRENT_UNIT_SELECTION);
        secm_tab.add(secm_current_units, c);
        secm_current_units.addActionListener((ActionEvent e) -> {
            secmCurrentUnitsChange(e);
        });
        c.fill = GridBagConstraints.BOTH;
        c.gridy=4;
        c.weighty=1;
        secm_tab.add(new JLabel(),c);
        control_panel.addTab("SECM Image", secm_tab);// </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="SEM tab">
        JPanel sem_tab = new JPanel();
        sem_tab.setLayout(new GridBagLayout());
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        sem_tab.add(new JLabel("Left click to move; right click to rotate"), c);
        c.gridx=1;
        c.weightx=1;
        sem_tab.add(new JLabel(), c);

        JPanel sem_tab_inner = new JPanel();
        sem_tab_inner.setLayout(new GridBagLayout());
        c.weightx=0;
        c.gridx=0;
        c.gridy=0;
        c.fill = GridBagConstraints.NONE;
        sem_open_button = new JButton("Browse");
        sem_open_button.addActionListener((ActionEvent e) -> {
            semOpen();
        });
        sem_tab_inner.add(sem_open_button, c);
        c.gridx=1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx=1;
        sem_file_field = new JLabel("No File.");
        sem_tab_inner.add(sem_file_field, c);
        c.weightx=0;
        c.gridy=1;
        c.gridx=0;
        sem_tab_inner.add(new JLabel("Transparency:"), c);
        c.gridx=1;
        c.weightx=1;
        sem_transparency = new JSlider(0, 100, 50);
        sem_transparency.setValue((int)(SETTINGS.DEFAULT_SEM_Transparency*100));
        sem_transparency.addChangeListener((ChangeEvent e) -> {
            semTransparencyChanged(e);
        });
        sem_tab_inner.add(sem_transparency, c);
        c.weightx=0;
        JPanel sem_tab_sizepos = new JPanel();
        sem_tab_sizepos.setLayout(new GridBagLayout());
        c.gridy=0;
        c.gridx=0;
        sem_tab_sizepos.add(new JLabel("Scale:"),c);
        c.gridx=1;
        c.ipadx=FIELD_PAD;
        sem_scale_field = new JTextField("1");
        def = SETTINGS.DEFAULT_SEM_SCALE / def_reso_fact;
        sem_scale_field.setText("" + def);
        sem_scale_field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                semScaleFieldChange(e);
            }
        });
        sem_scale_field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                semScaleFieldFocusLost();
            }
        });
        sem_tab_sizepos.add(sem_scale_field, c);
        c.ipadx=DEFAULT_PAD;
        c.gridx=2;
        sem_scale_units = new JComboBox(SETTINGS.getResolutionUnitLabels());
        sem_scale_units.setSelectedIndex(SETTINGS.DEFAULT_RESOLUTION_UNIT_SELECTION);
        sem_scale_units.addActionListener((ActionEvent e) -> {
            semScaleUnitsChanged();
        });
        sem_tab_sizepos.add(sem_scale_units, c);
        c.gridx=3;
        c.ipadx=SPACER_PAD;
        sem_tab_sizepos.add(new JLabel(),c);
        c.ipadx=DEFAULT_PAD;
        c.gridx=4;
        sem_tab_sizepos.add(new JLabel("Rotation:"),c);
        c.gridx=5;
        c.ipadx=FIELD_PAD;
        sem_rotation_field = new JTextField("0");
        def = SETTINGS.DEFAULT_SEM_ROTATION;
        sem_rotation_field.setText("" + def);
        sem_rotation_field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                semRotationFieldChange(e);
            }
        });
        sem_rotation_field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                semRotationFieldFocusLost();
            }
        });
        sem_tab_sizepos.add(sem_rotation_field, c);
        c.ipadx=DEFAULT_PAD;
        c.gridy=1;
        c.gridx=0;
        sem_tab_sizepos.add(new JLabel("X-offset:"), c);
        c.gridx=1;
        sem_xoffset_field = new JTextField("0");
        def = SETTINGS.DEFAULT_SEM_XOFFSET / def_dist_fact;
        sem_xoffset_field.setText("" + def);
        sem_xoffset_field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                semXOffsFieldChange(e);
            }
        });
        sem_xoffset_field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                semXOffsFieldFocusLost();
            }
        });
        sem_tab_sizepos.add(sem_xoffset_field, c);
        c.gridx=2;
        sem_xoffset_units = new JComboBox(SETTINGS.getDistanceUnitLabels());
        sem_xoffset_units.setSelectedIndex(SETTINGS.DEFAULT_DISTANCE_UNIT_SELECTION);
        sem_xoffset_units.addActionListener((ActionEvent e) -> {
            semXOffsUnitsChanged();
        });
        sem_tab_sizepos.add(sem_xoffset_units, c);
        c.gridx=4;
        sem_tab_sizepos.add(new JLabel("Mirror x:"),c);
        c.gridx=5;
        sem_mirrorx = new JCheckBox("", SETTINGS.DEFAULT_SEM_XMIRROR);
        sem_mirrorx.addActionListener((ActionEvent e) -> {
            semMirrorXClicked();
        });
        sem_tab_sizepos.add(sem_mirrorx, c);
        c.gridx=0;
        c.gridy=2;
        sem_tab_sizepos.add(new JLabel("Y-offset:"), c);
        c.gridx=1;
        sem_yoffset_field = new JTextField("0");
        def = SETTINGS.DEFAULT_SEM_YOFFSET / def_dist_fact;
        sem_yoffset_field.setText("" + def);
        sem_yoffset_field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                semYOffsFieldChange(e);
            }
        });
        sem_yoffset_field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                semYOffsFieldFocusLost();
            }
        });
        sem_tab_sizepos.add(sem_yoffset_field, c);
        c.gridx=2;
        sem_yoffset_units = new JComboBox(SETTINGS.getDistanceUnitLabels());
        sem_yoffset_units.setSelectedIndex(SETTINGS.DEFAULT_DISTANCE_UNIT_SELECTION);
        sem_yoffset_units.addActionListener((ActionEvent e) -> {
            semYOffsUnitsChanged();
        });
        sem_tab_sizepos.add(sem_yoffset_units, c);
        c.gridx=4;
        sem_tab_sizepos.add(new JLabel("Mirror y:"),c);
        c.gridx=5;
        sem_mirrory = new JCheckBox("", SETTINGS.DEFAULT_SEM_YMIRROR);
        sem_mirrory.addActionListener((ActionEvent e) -> {
            semMirrorYClicked();
        });
        sem_tab_sizepos.add(sem_mirrory, c);
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 1;
        sem_tab.add(sem_tab_inner, c);
        c.gridy = 2;
        sem_tab.add(sem_tab_sizepos, c);
        c.gridy=2;
        c.weighty=1;
        sem_tab.add(new JLabel(), c);
        control_panel.addTab("SEM Image", sem_tab);// </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="Reactivity tab">
        JPanel reactivity_tab = new JPanel();
        reactivity_tab.setLayout(new GridBagLayout());
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        
        JPanel reac_resgroup = new JPanel(new GridBagLayout());
        reac_resgroup.add(new JLabel("X-resolution:"), c);
        c.gridy=1;
        c.ipadx=FIELD_PAD;
        reac_xres_field = new JTextField("0.1");
        def = SETTINGS.DEFAULT_REAC_XRESOLUTION / def_dist_fact;
        reac_xres_field.setText("" + def);
        reac_xres_field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                reacXResolutionFieldChange(e);
            }
        });
        reac_xres_field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                reacXResolutionFieldFocusLost();
            }
        });
        reac_resgroup.add(reac_xres_field, c);
        c.ipadx=DEFAULT_PAD;
        c.gridx=1;
        reac_xres_units = new JComboBox(SETTINGS.getDistanceUnitLabels());
        reac_xres_units.setSelectedIndex(SETTINGS.DEFAULT_DISTANCE_UNIT_SELECTION);
        reac_xres_units.addActionListener((ActionEvent e) -> {
            reacXResolutionUnitsChanged();
        });
        reac_resgroup.add(reac_xres_units, c);
        c.gridx = 2;
        c.gridy = 0;
        c.ipadx=SPACER_PAD;
        reac_resgroup.add(new JLabel(), c);
        c.ipadx=DEFAULT_PAD;
        c.gridx = 3;
        reac_resgroup.add(new JLabel("Y-resolution:"), c);
        c.gridy=1;
        c.ipadx=FIELD_PAD;
        reac_yres_field = new JTextField("0.1");
        def = SETTINGS.DEFAULT_REAC_YRESOLUTION / def_dist_fact;
        reac_yres_field.setText("" + def);
        reac_yres_field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                reacYResolutionFieldChange(e);
            }
        });
        reac_yres_field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                reacYResolutionFieldFocusLost();
            }
        });
        reac_resgroup.add(reac_yres_field, c);
        c.ipadx=DEFAULT_PAD;
        c.gridx=4;
        reac_yres_units = new JComboBox(SETTINGS.getDistanceUnitLabels());
        reac_yres_units.setSelectedIndex(SETTINGS.DEFAULT_DISTANCE_UNIT_SELECTION);
        reac_yres_units.addActionListener((ActionEvent e) -> {
            reacYResolutionUnitsChanged();
        });
        reac_resgroup.add(reac_yres_units, c);
        c.gridx = 0;
        c.gridy = 0;
        reactivity_tab.add(reac_resgroup, c);
        
        JPanel reac_gtgroup = new JPanel(new GridBagLayout());
        reac_gtgroup.add(new JLabel("Show grid: "), c);
        c.gridx=1;
        reac_grid = new JCheckBox("", true);
        reac_grid.addActionListener((ActionEvent e) -> {
            reacShowGridChecked();
        });
        reac_gtgroup.add(reac_grid, c);
        c.gridx=0;
        c.gridy=1;
        reac_gtgroup.add(new JLabel("SEM Transparency: "), c);
        c.gridx=1;
        reac_semtransparency = new JSlider(0, 100, 50);
        reac_semtransparency.setValue((int)(SETTINGS.DEFAULT_REAC_SEM_TRANSPARENCY*100));
        reac_semtransparency.addChangeListener((ChangeEvent e) -> {
            reacSemTransparencyChanged(e);
        });
        reac_gtgroup.add(reac_semtransparency, c);
        c.gridx=0;
        c.gridy=2;
        reac_gtgroup.add(new JLabel("Selection Transparency: "), c);
        c.gridx=1;
        reac_seltransparency = new JSlider(0, 100, 50);
        reac_semtransparency.setValue((int)(SETTINGS.DEFAULT_REAC_SELECTION_TRANSPARENCY*100));
        reac_seltransparency.addChangeListener((ChangeEvent e) -> {
            reacSelectTransparencyChanged(e);
        });
        reac_gtgroup.add(reac_seltransparency, c);
        c.gridx = 0;
        c.gridy = 1;
        reactivity_tab.add(reac_gtgroup, c);
        
        c.gridy=2;
        reac_tool_select = new JLabel("Active tool: pencil");
        reactivity_tab.add(reac_tool_select,c);
        
        JPanel reac_toolbar = new JPanel(new GridBagLayout());
        c.gridx = 0;
        c.gridy = 0;
        c.weightx=1;
        reac_toolbar.add(new JLabel(), c);
        c.weightx=0;
        c.gridx=1;
        reac_crop = new JButton("Crop");
        reac_crop.addActionListener((ActionEvent e) -> {
            cropSelect();
        });
        reac_toolbar.add(reac_crop, c);
        c.gridx=2;
        reac_pencil = new JButton("Pencil");
        reac_pencil.addActionListener((ActionEvent e) -> {
            pencilSelect();
        });
        reac_pencil.setEnabled(false);
        reac_toolbar.add(reac_pencil, c);
        c.gridx=3;
        reac_fill = new JButton("Fill");
        reac_fill.addActionListener((ActionEvent e) -> {
            fillSelect();
        });
        reac_toolbar.add(reac_fill, c);
        c.gridx=4;
        c.weightx=1;
        reac_toolbar.add(new JLabel(), c);
        c.weightx=0;
        c.gridx = 0;
        c.gridy = 3;
        reactivity_tab.add(reac_toolbar,c);
        c.gridx = 1;
        c.weightx=1;
        reactivity_tab.add(new JLabel(),c);
        c.gridy = 4;
        c.weighty=1;
        reactivity_tab.add(new JLabel(),c);
        control_panel.addTab("Reactivity Selection", reactivity_tab);// </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="Sampling tab">
        //Sampling tab
        JPanel sampling_tab = new JPanel();
        sampling_tab.setLayout(new GridBagLayout());
        JPanel sampling_fields = new JPanel();
        sampling_fields.setLayout(new GridBagLayout());
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        sampling_fields.add(new JLabel("Starting x-index: "), c);
        c.gridx = 1;
        c.ipadx = FIELD_PAD;
        sam_xstart = new JTextField("" + SETTINGS.DEFAULT_SAM_XSTART_INDEX);
        sam_xstart.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                samStartXFieldChange(e);
            }
        });
        sam_xstart.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                samStartXFieldFocusLost();
            }
        });
        sampling_fields.add(sam_xstart, c);
        c.gridx = 2;
        c.ipadx=SPACER_PAD;
        sampling_fields.add(new JLabel(), c);
        c.ipadx=DEFAULT_PAD;
        c.gridx = 3;
        sampling_fields.add(new JLabel("X-step size: "), c);
        c.gridx = 4;
        c.ipadx = FIELD_PAD;
        sam_xsize = new JTextField("" + SETTINGS.DEFAULT_SAM_XSTEP);
        sam_xsize.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                samStepSizeXFieldChange(e);
            }
        });
        sam_xsize.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                samStepSizeXFieldFocusLost();
            }
        });
        sampling_fields.add(sam_xsize, c);
        c.gridx=5;
        c.ipadx= SPACER_PAD;
        sampling_fields.add(new JLabel(), c);
        c.ipadx = DEFAULT_PAD;
        c.gridx = 6;
        sampling_fields.add(new JLabel("Number of x-steps: "), c);
        c.gridx = 7;
        c.ipadx = FIELD_PAD;
        sam_xsteps = new JTextField("" + SETTINGS.DEFAULT_SAM_NUM_XSTEPS);
        sam_xsteps.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                samNumStepsXFieldChange(e);
            }
        });
        sam_xsteps.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                samNumStepsXFieldFocusLost();
            }
        });
        sampling_fields.add(sam_xsteps, c);
        c.ipadx = DEFAULT_PAD;
        c.gridx = 0;
        c.gridy = 1;
        sampling_fields.add(new JLabel("Starting y-index: "), c);
        c.gridx = 1;
        c.ipadx = FIELD_PAD;
        sam_ystart = new JTextField("" + SETTINGS.DEFAULT_SAM_YSTART_INDEX);
        sam_ystart.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                samStartYFieldChange(e);
            }
        });
        sam_ystart.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                samStartYFieldFocusLost();
            }
        });
        sampling_fields.add(sam_ystart, c);
        c.ipadx = DEFAULT_PAD;
        c.gridx = 3;
        sampling_fields.add(new JLabel("Y-step size: "), c);
        c.gridx = 4;
        c.ipadx = FIELD_PAD;
        sam_ysize = new JTextField("" + SETTINGS.DEFAULT_SAM_YSTEP);
        sam_ysize.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                samStepSizeYFieldChange(e);
            }
        });
        sam_ysize.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                samStepSizeYFieldFocusLost();
            }
        });
        sampling_fields.add(sam_ysize, c);
        c.ipadx = DEFAULT_PAD;
        c.gridx = 6;
        sampling_fields.add(new JLabel("Number of y-steps: "), c);
        c.gridx = 7;
        c.ipadx = FIELD_PAD;
        sam_ysteps = new JTextField("" + SETTINGS.DEFAULT_SAM_NUM_YSTEPS);
        sam_ysteps.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                samNumStepsYFieldChange(e);
            }
        });
        sam_ysteps.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                samNumStepsYFieldFocusLost();
            }
        });
        sampling_fields.add(sam_ysteps, c);
        c.ipadx = DEFAULT_PAD;
        c.gridx = 0;
        c.gridy = 0;
        sampling_tab.add(sampling_fields, c);
        c.weightx=1;
        JPanel sam_button_mount = new JPanel();
        sam_button_mount.setLayout(new GridBagLayout());
        sam_button_mount.add(new JLabel(), c);
        c.gridx=2;
        sam_button_mount.add(new JLabel(), c);
        c.weightx=0;
        c.gridx=1;
        sam_generate = new JButton("Generate instruction files");
        sam_generate.addActionListener((ActionEvent e) -> {
            samGenerate();
        });
        sam_button_mount.add(sam_generate, c);
        c.gridx = 0;
        c.gridy = 1;
        sampling_tab.add(sam_button_mount, c);
        c.gridx=1;
        c.gridy=2;
        c.weightx=1;
        c.weighty=1;
        sampling_tab.add(new JLabel(), c);
        
        control_panel.addTab("Fit Sampling", sampling_tab);// </editor-fold>
        
        this.pack();
        this.setSize(600, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        control_panel.addChangeListener((ChangeEvent e) -> {
            tabChanged();
        });
        view_screen.updateGraphics();
    }
 
    //Event Handlers
    /**
     * Triggers when the <code>control_panel</code> tab focus changes.
     * Updates the <code>view_screen</code> render mode.
     */
    private void tabChanged(){
        view_screen.setRenderMode(control_panel.getSelectedIndex());
    }
     
    //<editor-fold defaultstate="collapsed" desc="SECM event methods">
    /**
     * Triggered when <code>secm_open_button</code> is pressed.
     * Launches a <code>JFileChooser</code> and reads the selected file into an 
     * <code>SECMImage</code> which gets sent to the <code>view_screen</code>.
     */
    private void secmOpen(){
        FileFilter ff = new FileNameExtensionFilter( "Text files", "txt");
        JFileChooser filedialog = new JFileChooser();
        filedialog.addChoosableFileFilter(ff);
        filedialog.setFileFilter(ff);
        int response = filedialog.showOpenDialog(this);
        if(response == JFileChooser.APPROVE_OPTION){
            String flpth = "" + filedialog.getSelectedFile().getPath();
            view_screen.setSECMImage(new SECMImage(flpth));
            secm_file_field.setText(flpth);
        }
    }
    
    /**
     * Triggered when the user changes the distance units selection for the SECM image.
     * @param e the triggering event (not currently used)
     */
    private void secmDistanceUnitsChange(ActionEvent e){
        int selection = secm_distance_units.getSelectedIndex();
        if(selection > -1){
            secm_distance_unit = SETTINGS.UNITS_CURRENT[selection];
            view_screen.setSECMScale(secm_distance_unit.getFactor());
        }
    }
    
    /**
     * Triggered when the user changes the current units selection for the SECM image.
     * @param e the triggering event (not currently used)
     */
    private void secmCurrentUnitsChange(ActionEvent e){
        int selection = secm_current_units.getSelectedIndex();
        if(selection > -1){
            secm_current_scale_factor = SETTINGS.UNITS_CURRENT[selection].getFactor();
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="SEM event methods">
    /**
     * Triggered when <code>sem_open_button</code> is pressed.
     * Launches a <code>JFileChooser</code> and reads the selected file into an 
     * <code>SEMImage</code> which gets sent to the <code>view_screen</code>.
     */
    private void semOpen(){
        FileFilter ff = new FileNameExtensionFilter( "TIFF", "tif", "tiff");
        JFileChooser filedialog = new JFileChooser();
        filedialog.addChoosableFileFilter(ff);
        filedialog.addChoosableFileFilter(new FileNameExtensionFilter( "PNG", "png"));
        filedialog.setFileFilter(ff);
        int response = filedialog.showOpenDialog(this);
        if(response == JFileChooser.APPROVE_OPTION){
            String flpth = "" + filedialog.getSelectedFile().getPath();
            view_screen.setSEMImage(new SEMImage(flpth));
            sem_file_field.setText(flpth);
        }
    }
    
    /**
     * Triggered when the state of the <code>sem_transparency</code> slider is changed,
     * sending the new transparency information on to the <code>view_screen</code>.
     * @param e the triggering event (not currently used)
     */
    private void semTransparencyChanged(ChangeEvent e){
        view_screen.setSEMTransparency((float)(sem_transparency.getValue())*0.01f);
    }
    
    /**
     * Triggered when the user presses a key when focussing on the <code>sem_scale_field</code>.
     * If the value of the field is a valid double that is <code>>=0</code>, then the value is written to <code>sem_scale_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is ignored for now.
     * @param e the triggering event (not currently used)
     */
    private void semScaleFieldChange(KeyEvent e){
        try{
            double input_val = Double.parseDouble(sem_scale_field.getText());
            if(input_val <= 0.0){
                throw new NumberFormatException();
            }
            double reso_fact = SETTINGS.UNITS_RESOLUTION[sem_scale_units.getSelectedIndex()].getFactor();
            sem_scale_accepted = input_val*reso_fact;
            view_screen.setSEMScale(sem_scale_accepted);
        }
        catch(Exception ex){
            
        }
    }
    /**
     * Triggered when the user moves focus away from <code>sem_scale_field</code>.
     * If the value of the field is a valid double that is <code>>=0</code>, then the value is written to <code>sem_scale_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is reverted to the last accepted value and the user is notified of the input restrictions via a <code>JOptionPane</code>.
     */
    private void semScaleFieldFocusLost(){
        try{
            double input_val = Double.parseDouble(sem_scale_field.getText());
            if(input_val <= 0.0){
                throw new NumberFormatException();
            }
            double reso_fact = SETTINGS.UNITS_RESOLUTION[sem_scale_units.getSelectedIndex()].getFactor();
            sem_scale_accepted = input_val*reso_fact;
            view_screen.setSEMScale(sem_scale_accepted);
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this, "The SEM scale must be a positive valued real number.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            double reso_fact = SETTINGS.UNITS_RESOLUTION[sem_scale_units.getSelectedIndex()].getFactor();
            double field_val = sem_scale_accepted / reso_fact;
            sem_scale_field.setText(field_val + "");
        }
    }
    /**
     * Triggered when the <code>sem_scale_units</code> selection is changed.
     * This does not affect the value of the <code>sem_scale_accepted</code> field, but does change the text of <code>sem_scale_field</code> to reflect the new unit.
     */
    private void semScaleUnitsChanged(){
        double reso_fact = SETTINGS.UNITS_RESOLUTION[sem_scale_units.getSelectedIndex()].getFactor();
        double field_val = sem_scale_accepted / reso_fact;
        sem_scale_field.setText(field_val + "");
    }
    
    /**
     * Triggered when the user presses a key when focussing on the <code>sem_xoffset_field</code>.
     * If the value of the field is a valid double that is <code>>=0</code>, then the value is written to <code>sem_xoffs_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is ignored for now.
     * @param e the triggering event (not currently used)
     */
    private void semXOffsFieldChange(KeyEvent e){
        try{
            double input_val = Double.parseDouble(sem_xoffset_field.getText());
            double reso_fact = SETTINGS.UNITS_DISTANCE[sem_xoffset_units.getSelectedIndex()].getFactor();
            sem_xoffs_accepted = input_val*reso_fact;
            view_screen.setSEMXOffs(sem_xoffs_accepted);
        }
        catch(Exception ex){
            
        }
    }
    /**
     * Triggered when the user moves focus away from <code>sem_xoffset_field</code>.
     * If the value of the field is a valid double that is <code>>=0</code>, then the value is written to <code>sem_xoffs_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is reverted to the last accepted value and the user is notified of the input restrictions via a <code>JOptionPane</code>.
     */
    private void semXOffsFieldFocusLost(){
        try{
            double input_val = Double.parseDouble(sem_xoffset_field.getText());
            double reso_fact = SETTINGS.UNITS_DISTANCE[sem_xoffset_units.getSelectedIndex()].getFactor();
            sem_xoffs_accepted = input_val*reso_fact;
            view_screen.setSEMXOffs(sem_xoffs_accepted);
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this, "The SEM X-offset must be a real number.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            double reso_fact = SETTINGS.UNITS_DISTANCE[sem_xoffset_units.getSelectedIndex()].getFactor();
            double field_val = sem_xoffs_accepted / reso_fact;
            sem_xoffset_field.setText(field_val + "");
        }
    }
    /**
     * Triggered when the <code>sem_xoffset_units</code> selection is changed.
     * This does not affect the value of the <code>sem_xoffs_accepted</code> field, but does change the text of <code>sem_xoffset_field</code> to reflect the new unit.
     */
    private void semXOffsUnitsChanged(){
        double reso_fact = SETTINGS.UNITS_DISTANCE[sem_xoffset_units.getSelectedIndex()].getFactor();
        double field_val = sem_xoffs_accepted / reso_fact;
        sem_xoffset_field.setText(field_val + "");
    }
    
    /**
     * Triggered when the user presses a key when focussing on the <code>sem_yoffset_field</code>.
     * If the value of the field is a valid double, then the value is written to <code>sem_yoffs_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is ignored for now.
     * @param e the triggering event (not currently used)
     */
    private void semYOffsFieldChange(KeyEvent e){
        try{
            double input_val = Double.parseDouble(sem_yoffset_field.getText());
            double reso_fact = SETTINGS.UNITS_DISTANCE[sem_yoffset_units.getSelectedIndex()].getFactor();
            sem_yoffs_accepted = input_val*reso_fact;
            view_screen.setSEMYOffs(sem_yoffs_accepted);
        }
        catch(Exception ex){
            
        }
    }
    /**
     * Triggered when the user moves focus away from <code>sem_yoffset_field</code>.
     * If the value of the field is a valid double, then the value is written to <code>sem_yoffs_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is reverted to the last accepted value and the user is notified of the input restrictions via a <code>JOptionPane</code>.
     */
    private void semYOffsFieldFocusLost(){
        try{
            double input_val = Double.parseDouble(sem_yoffset_field.getText());
            double reso_fact = SETTINGS.UNITS_DISTANCE[sem_yoffset_units.getSelectedIndex()].getFactor();
            sem_yoffs_accepted = input_val*reso_fact;
            view_screen.setSEMYOffs(sem_yoffs_accepted);
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this, "The SEM Y-offset must be a real number.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            double reso_fact = SETTINGS.UNITS_DISTANCE[sem_yoffset_units.getSelectedIndex()].getFactor();
            double field_val = sem_yoffs_accepted / reso_fact;
            sem_yoffset_field.setText(field_val + "");
        }
    }
    /**
     * Triggered when the <code>sem_yoffset_units</code> selection is changed.
     * This does not affect the value of the <code>sem_yoffs_accepted</code> field, but does change the text of <code>sem_yoffset_field</code> to reflect the new unit.
     */
    private void semYOffsUnitsChanged(){
        double reso_fact = SETTINGS.UNITS_DISTANCE[sem_yoffset_units.getSelectedIndex()].getFactor();
        double field_val = sem_xoffs_accepted / reso_fact;
        sem_yoffset_field.setText(field_val + "");
    }
    
    /**
     * Triggered when the user presses a key when focussing on the <code>sem_rotation_field</code>.
     * If the value of the field is a valid double, then the value is written to <code>sem_rotation_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is ignored for now.
     * @param e the triggering event (not currently used)
     */
    private void semRotationFieldChange(KeyEvent e){
        try{
            double input_val = Double.parseDouble(sem_rotation_field.getText());
            sem_rotation_accepted = input_val;
            view_screen.setSEMRotation(sem_rotation_accepted);
        }
        catch(Exception ex){
            
        }
    }
    /**
     * Triggered when the user moves focus away from <code>sem_rotation_field</code>.
     * If the value of the field is a valid double, then the value is written to <code>sem_rotation_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is reverted to the last accepted value and the user is notified of the input restrictions via a <code>JOptionPane</code>.
     */
    private void semRotationFieldFocusLost(){
        try{
            double input_val = Double.parseDouble(sem_rotation_field.getText());
            sem_rotation_accepted = input_val;
            view_screen.setSEMRotation(sem_rotation_accepted);
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this, "The SEM rotation must be a real number.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            double field_val = sem_yoffs_accepted;
            sem_rotation_field.setText(field_val + "");
        }
    }
    
    /**
     * Triggers when the state of <code>sem_mirrorx</code> changes.
     * Sends the new state to the <code>view_screen</code>.
     */
    private void semMirrorXClicked(){
        boolean state = sem_mirrorx.isSelected();
        view_screen.setSEMMirrorX(state);
    }
    /**
     * Triggers when the state of <code>sem_mirrory</code> changes.
     * Sends the new state to the <code>view_screen</code>.
     */
    private void semMirrorYClicked(){
        boolean state = sem_mirrory.isSelected();
        view_screen.setSEMMirrorY(state);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Reactivity event methods">
    /**
     * Triggered when the user presses a key when focussing on the <code>reac_xres_field</code>.
     * If the value of the field is a valid double that is <code>>=0</code>, then the value is written to <code>reac_xresolution_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is ignored for now.
     * @param e the triggering event (not currently used)
     */
    private void reacXResolutionFieldChange(KeyEvent e){
        try{
            double input_val = Double.parseDouble(reac_xres_field.getText());
            if(input_val <= 0.0){
                throw new NumberFormatException();
            }
            double reso_fact = SETTINGS.UNITS_DISTANCE[reac_xres_units.getSelectedIndex()].getFactor();
            reac_xresolution_accepted = input_val*reso_fact;
            view_screen.setReactivityXResolution(reac_xresolution_accepted);
        }
        catch(Exception ex){
            
        }
    }
    /**
     * Triggered when the user moves focus away from <code>reac_xres_field</code>.
     * If the value of the field is a valid double, then the value is written to <code>reac_xresolution_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is reverted to the last accepted value and the user is notified of the input restrictions via a <code>JOptionPane</code>.
     */
    private void reacXResolutionFieldFocusLost(){
        try{
            double input_val = Double.parseDouble(reac_xres_field.getText());
            if(input_val <= 0.0){
                throw new NumberFormatException();
            }
            double reso_fact = SETTINGS.UNITS_DISTANCE[reac_xres_units.getSelectedIndex()].getFactor();
            reac_xresolution_accepted = input_val*reso_fact;
            view_screen.setReactivityXResolution(reac_xresolution_accepted);
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this, "The x-resolution must be a positive valued real number.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            double reso_fact = SETTINGS.UNITS_DISTANCE[reac_xres_units.getSelectedIndex()].getFactor();
            double field_val = reac_xresolution_accepted / reso_fact;
            reac_xres_field.setText(field_val + "");
        }
    }
    /**
     * Triggered when the <code>reac_xres_units</code> selection is changed.
     * This does not affect the value of the <code>reac_xresolution_accepted</code> field, but does change the text of <code>reac_xres_field</code> to reflect the new unit.
     */
    private void reacXResolutionUnitsChanged(){
        double reso_fact = SETTINGS.UNITS_DISTANCE[reac_xres_units.getSelectedIndex()].getFactor();
        double field_val = reac_xresolution_accepted / reso_fact;
        reac_xres_field.setText(field_val + "");
    }
    
    /**
     * Triggered when the user presses a key when focussing on the <code>reac_xres_field</code>.
     * If the value of the field is a valid double that is <code>>=0</code>, then the value is written to <code>reac_xresolution_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is ignored for now.
     * @param e the triggering event (not currently used)
     */
    private void reacYResolutionFieldChange(KeyEvent e){
        try{
            double input_val = Double.parseDouble(reac_yres_field.getText());
            if(input_val <= 0.0){
                throw new NumberFormatException();
            }
            double reso_fact = SETTINGS.UNITS_DISTANCE[reac_yres_units.getSelectedIndex()].getFactor();
            reac_yresolution_accepted = input_val*reso_fact;
            view_screen.setReactivityYResolution(reac_yresolution_accepted);
        }
        catch(Exception ex){
            
        }
    }
    /**
     * Triggered when the user moves focus away from <code>reac_xres_field</code>.
     * If the value of the field is a valid double, then the value is written to <code>reac_xresolution_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is reverted to the last accepted value and the user is notified of the input restrictions via a <code>JOptionPane</code>.
     */
    private void reacYResolutionFieldFocusLost(){
        try{
            double input_val = Double.parseDouble(reac_yres_field.getText());
            if(input_val <= 0.0){
                throw new NumberFormatException();
            }
            double reso_fact = SETTINGS.UNITS_DISTANCE[reac_yres_units.getSelectedIndex()].getFactor();
            reac_yresolution_accepted = input_val*reso_fact;
            view_screen.setReactivityYResolution(reac_yresolution_accepted);
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this, "The y-resolution must be a positive valued real number.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            double reso_fact = SETTINGS.UNITS_DISTANCE[reac_yres_units.getSelectedIndex()].getFactor();
            double field_val = reac_yresolution_accepted / reso_fact;
            reac_yres_field.setText(field_val + "");
        }
    }
    /**
     * Triggered when the <code>reac_xres_units</code> selection is changed.
     * This does not affect the value of the <code>reac_xresolution_accepted</code> field, but does change the text of <code>reac_xres_field</code> to reflect the new unit.
     */
    private void reacYResolutionUnitsChanged(){
        double reso_fact = SETTINGS.UNITS_DISTANCE[reac_yres_units.getSelectedIndex()].getFactor();
        double field_val = reac_yresolution_accepted / reso_fact;
        reac_yres_field.setText(field_val + "");
    }
    
    /**
     * Triggers when the state of <code>reac_grid</code> changes.
     * Sends the new state to the <code>view_screen</code>.
     */
    private void reacShowGridChecked(){
        boolean state = reac_grid.isSelected();
        view_screen.setReactivityGrid(state);
    }
    
    /**
     * Triggered when the state of the <code>reac_semtransparency</code> slider is changed,
     * sending the new transparency information on to the <code>view_screen</code>.
     * @param e the triggering event (not currently used)
     */
    private void reacSemTransparencyChanged(ChangeEvent e){
        view_screen.setReactivitySEMTransparency((float)(reac_semtransparency.getValue())*0.01f);
    }
    /**
     * Triggered when the state of the <code>reac_seltransparency</code> slider is changed,
     * sending the new transparency information on to the <code>view_screen</code>.
     * @param e the triggering event (not currently used)
     */
    private void reacSelectTransparencyChanged(ChangeEvent e){
        view_screen.setReactivitySelectionTransparency((float)(reac_seltransparency.getValue())*0.01f);
    }
    
    /**
     * Triggered when <code>reac_pencil</code> is pressed.
     * Sends the new tool information to the <code>view_screen</code> and updates the <code>reac_tool_select</code> label.
     * <code>reac_pencil</code> is disabled and the other tool buttons are enabled.
     */
    private void pencilSelect(){
        reac_pencil.setEnabled(false);
        reac_crop.setEnabled(true);
        reac_fill.setEnabled(true);
        reac_tool_select.setText("Active tool: pencil");
        view_screen.setReactivityTool(Visualizer.PENCIL);
    }
    /**
     * Triggered when <code>reac_crop</code> is pressed.
     * Sends the new tool information to the <code>view_screen</code> and updates the <code>reac_tool_select</code> label.
     * <code>reac_crop</code> is disabled and the other tool buttons are enabled.
     */
    private void cropSelect(){
        reac_pencil.setEnabled(true);
        reac_crop.setEnabled(false);
        reac_fill.setEnabled(true);
        reac_tool_select.setText("Active tool: crop");
        view_screen.setReactivityTool(Visualizer.CROP);
    }
    /**
     * Triggered when <code>reac_fill</code> is pressed.
     * Sends the new tool information to the <code>view_screen</code> and updates the <code>reac_tool_select</code> label.
     * <code>reac_fill</code> is disabled and the other tool buttons are enabled.
     */
    private void fillSelect(){
        reac_pencil.setEnabled(true);
        reac_crop.setEnabled(true);
        reac_fill.setEnabled(false);
        reac_tool_select.setText("Active tool: fill");
        view_screen.setReactivityTool(Visualizer.FILL);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Sampling event methods">
    /**
     * Triggered when <code>sam_generate</code> is pressed.
     * Prompts the user for a file name, then sends the file to the <code>view_screen</code> so that information can be written to it.
     */
    private void samGenerate(){
        try{
            FileFilter ffcsv = new FileNameExtensionFilter( "Comma Separated Values", "csv");
            FileFilter fftsv = new FileNameExtensionFilter( "Tab Separated Values", "tsv");
            JFileChooser filedialog = new JFileChooser();
            filedialog.addChoosableFileFilter(ffcsv);
            filedialog.addChoosableFileFilter(fftsv);
            filedialog.setFileFilter(ffcsv);
            int response = filedialog.showSaveDialog(this);
            if(response == JFileChooser.APPROVE_OPTION){
                String flpth = "" + filedialog.getSelectedFile().getPath();
                FileFilter selected = filedialog.getFileFilter();
                int filetype = Visualizer.FILETYPE_NOT_SPECIFIED;
                if(selected.equals(ffcsv)){
                    filetype = Visualizer.FILETYPE_CSV;
                }
                else if(selected.equals(fftsv)){
                    filetype = Visualizer.FILETYPE_TSV;
                }
                view_screen.saveData(flpth, secm_current_scale_factor, filetype, SECMImage.INTERPOLATION_BILINEAR);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Triggered when the user presses a key when focussing on the <code>sam_xstart</code>.
     * If the value of the field is a valid integer that is <code>>=0</code>, then the value is written to <code>sam_start_x_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is ignored for now.
     * @param e the triggering event (not currently used)
     */
    private void samStartXFieldChange(KeyEvent e){
        try{
            int input_val = Integer.parseInt(sam_xstart.getText());
            if(input_val < 0){
                throw new NumberFormatException();
            }
            sam_start_x_accepted = input_val;
            view_screen.setSamplingStartingX(sam_start_x_accepted);
        }
        catch(Exception ex){
            
        }
    }
    /**
     * Triggered when the user moves focus away from <code>sam_xstart</code>.
     * If the value of the field is a valid double, then the value is written to <code>sam_start_x_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is reverted to the last accepted value and the user is notified of the input restrictions via a <code>JOptionPane</code>.
     */
    private void samStartXFieldFocusLost(){
        try{
            int input_val = Integer.parseInt(sam_xstart.getText());
            if(input_val < 0){
                throw new NumberFormatException();
            }
            sam_start_x_accepted = input_val;
            view_screen.setSamplingStartingX(sam_start_x_accepted);
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this, "The starting x-coordinate must be an integer that is >= 0.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            int field_val = sam_start_x_accepted;
            sam_xstart.setText(field_val + "");
        }
    }
    
    /**
     * Triggered when the user presses a key when focussing on the <code>sam_ystart</code>.
     * If the value of the field is a valid integer that is <code>>=0</code>, then the value is written to <code>sam_start_y_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is ignored for now.
     * @param e the triggering event (not currently used)
     */
    private void samStartYFieldChange(KeyEvent e){
        try{
            int input_val = Integer.parseInt(sam_ystart.getText());
            if(input_val < 0){
                throw new NumberFormatException();
            }
            sam_start_y_accepted = input_val;
            view_screen.setSamplingStartingY(sam_start_y_accepted);
        }
        catch(Exception ex){
            
        }
    }
    /**
     * Triggered when the user moves focus away from <code>sam_ystart</code>.
     * If the value of the field is a valid double, then the value is written to <code>sam_start_y_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is reverted to the last accepted value and the user is notified of the input restrictions via a <code>JOptionPane</code>.
     */
    private void samStartYFieldFocusLost(){
        try{
            int input_val = Integer.parseInt(sam_ystart.getText());
            if(input_val < 0){
                throw new NumberFormatException();
            }
            sam_start_y_accepted = input_val;
            view_screen.setSamplingStartingY(sam_start_y_accepted);
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this, "The starting y-coordinate must be an integer that is >= 0.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            int field_val = sam_start_y_accepted;
            sam_ystart.setText(field_val + "");
        }
    }
   
    /**
     * Triggered when the user presses a key when focussing on the <code>sam_xsize</code>.
     * If the value of the field is a valid integer that is <code>>=0</code>, then the value is written to <code>sam_step_size_x_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is ignored for now.
     * @param e the triggering event (not currently used)
     */
    private void samStepSizeXFieldChange(KeyEvent e){
        try{
            int input_val = Integer.parseInt(sam_xsize.getText());
            if(input_val <= 0){
                throw new NumberFormatException();
            }
            sam_step_size_x_accepted = input_val;
            view_screen.setSamplingStepSizeX(sam_step_size_x_accepted);
        }
        catch(Exception ex){

        }
    }
    /**
     * Triggered when the user moves focus away from <code>sam_xsize</code>.
     * If the value of the field is a valid double, then the value is written to <code>sam_step_size_x_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is reverted to the last accepted value and the user is notified of the input restrictions via a <code>JOptionPane</code>.
     */
    private void samStepSizeXFieldFocusLost(){
        try{
            int input_val = Integer.parseInt(sam_xsize.getText());
            if(input_val <= 0){
                throw new NumberFormatException();
            }
            sam_step_size_x_accepted = input_val;
            view_screen.setSamplingStepSizeX(sam_step_size_x_accepted);
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this, "The x-step must be a positive valued integer.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            int field_val = sam_step_size_x_accepted;
            sam_xsize.setText(field_val + "");
        }
    }
    
    /**
     * Triggered when the user presses a key when focussing on the <code>sam_ysize</code>.
     * If the value of the field is a valid integer that is <code>>=0</code>, then the value is written to <code>sam_step_size_y_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is ignored for now.
     * @param e the triggering event (not currently used)
     */
    private void samStepSizeYFieldChange(KeyEvent e){
        try{
            int input_val = Integer.parseInt(sam_ysize.getText());
            if(input_val <= 0){
                throw new NumberFormatException();
            }
            sam_step_size_y_accepted = input_val;
            view_screen.setSamplingStepSizeY(sam_step_size_y_accepted);
        }
        catch(Exception ex){
            
        }
    }
    /**
     * Triggered when the user moves focus away from <code>sam_ysize</code>.
     * If the value of the field is a valid double, then the value is written to <code>sam_step_size_y_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is reverted to the last accepted value and the user is notified of the input restrictions via a <code>JOptionPane</code>.
     */
    private void samStepSizeYFieldFocusLost(){
        try{
            int input_val = Integer.parseInt(sam_ysize.getText());
            if(input_val <= 0){
                throw new NumberFormatException();
            }
            sam_step_size_y_accepted = input_val;
            view_screen.setSamplingStepSizeY(sam_step_size_y_accepted);
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this, "The y-step must be a positive valued integer.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            int field_val = sam_step_size_y_accepted;
            sam_ysize.setText(field_val + "");
        }
    }
    
    /**
     * Triggered when the user presses a key when focussing on the <code>sam_xsteps</code>.
     * If the value of the field is a valid integer that is <code>>=0</code>, then the value is written to <code>sam_num_steps_x_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is ignored for now.
     * @param e the triggering event (not currently used)
     */
    private void samNumStepsXFieldChange(KeyEvent e){
        try{
            int input_val = Integer.parseInt(sam_xsteps.getText());
            if(input_val <= 0){
                throw new NumberFormatException();
            }
            sam_num_steps_x_accepted = input_val;
            view_screen.setSamplingNumberXSteps(sam_num_steps_x_accepted);
        }
        catch(Exception ex){
            
        }
    }
    /**
     * Triggered when the user moves focus away from <code>sam_xsteps</code>.
     * If the value of the field is a valid double, then the value is written to <code>sam_num_steps_x_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is reverted to the last accepted value and the user is notified of the input restrictions via a <code>JOptionPane</code>.
     */
    private void samNumStepsXFieldFocusLost(){
        try{
            int input_val = Integer.parseInt(sam_xsteps.getText());
            if(input_val <= 0){
                throw new NumberFormatException();
            }
            sam_num_steps_x_accepted = input_val;
            view_screen.setSamplingNumberXSteps(sam_num_steps_x_accepted);
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this, "The number of x-steps must be a positive valued integer.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            int field_val = sam_num_steps_x_accepted;
            sam_xsteps.setText(field_val + "");
        }
    }
    
    /**
     * Triggered when the user presses a key when focussing on the <code>sam_ysteps</code>.
     * If the value of the field is a valid integer that is <code>>=0</code>, then the value is written to <code>sam_num_steps_y_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is ignored for now.
     * @param e the triggering event (not currently used)
     */
    private void samNumStepsYFieldChange(KeyEvent e){
        try{
            int input_val = Integer.parseInt(sam_ysteps.getText());
            if(input_val <= 0){
                throw new NumberFormatException();
            }
            sam_num_steps_y_accepted = input_val;
            view_screen.setSamplingNumberYSteps(sam_num_steps_y_accepted);
        }
        catch(Exception ex){
            
        }
    }
    /**
     * Triggered when the user moves focus away from <code>sam_ysteps</code>.
     * If the value of the field is a valid double, then the value is written to <code>sam_num_steps_y_accepted</code> and sent to the <code>view_screen</code>.
     * If the value is invalid, then the input is reverted to the last accepted value and the user is notified of the input restrictions via a <code>JOptionPane</code>.
     */
    private void samNumStepsYFieldFocusLost(){
        try{
            int input_val = Integer.parseInt(sam_ysteps.getText());
            if(input_val <= 0){
                throw new NumberFormatException();
            }
            sam_num_steps_y_accepted = input_val;
            view_screen.setSamplingNumberYSteps(sam_num_steps_y_accepted);
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this, "The number of y-steps must be a positive valued integer.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            int field_val = sam_num_steps_y_accepted;
            sam_ysteps.setText(field_val + "");
        }
    }
    //</editor-fold>
        
    //Setters
    /**
     * Called by the <code>view_screen</code> when the user rotates the SEM image.
     * The rotation is rounded to <code>SIGFIGS</code> significant digits.
     * <code>sem_rotation_field</code> and <code>sem_rotation_accepted</code> are updated to reflect the new value.
     * @param rotation The rotation of the SEM image in degrees.
     */
    public void setSEMRotationField(double rotation){
        BigDecimal bd = new BigDecimal(rotation);
        bd = bd.round(new MathContext(SIGFIGS));
        rotation = bd.doubleValue();
        sem_rotation_field.setText(rotation + "");
        sem_rotation_accepted = rotation;
    }
    
    /**
     * Called by the <code>view_screen</code> when the user moves the SEM image.
     * The x-offset is rounded to <code>SIGFIGS</code> significant digits.
     * <code>sem_xoffset_field</code> and <code>sem_xoffs_accepted</code> are updated to reflect the new value.
     * @param xoffs The x-offset of the SEM image in metres.
     */
    public void setSEMXOffsetField(double xoffs){
        double reso_fact = SETTINGS.UNITS_DISTANCE[sem_xoffset_units.getSelectedIndex()].getFactor();
        double display = xoffs / reso_fact;
        BigDecimal bd = new BigDecimal(display);
        bd = bd.round(new MathContext(SIGFIGS));
        display = bd.doubleValue();
        sem_xoffset_field.setText(display + "");
        sem_xoffs_accepted = xoffs;
    }
    
    /**
     * Called by the <code>view_screen</code> when the user moves the SEM image.
     * The y-offset is rounded to <code>SIGFIGS</code> significant digits.
     * <code>sem_yoffset_field</code> and <code>sem_yoffs_accepted</code> are updated to reflect the new value.
     * @param yoffs The y-offset of the SEM image in metres.
     */
    public void setSEMYOffsetField(double yoffs){
        double reso_fact = SETTINGS.UNITS_DISTANCE[sem_yoffset_units.getSelectedIndex()].getFactor();
        double display = yoffs / reso_fact;
        BigDecimal bd = new BigDecimal(display);
        bd = bd.round(new MathContext(SIGFIGS));
        display = bd.doubleValue();
        sem_yoffset_field.setText(display + "");
        sem_yoffs_accepted = yoffs;
    }
    
    /**
     * Called by the <code>view_screen</code> when the user's mouse moves over it.
     * @param x_pos The x-position of the mouse in metres
     * @param y_pos The y-position of the mouse in metres
     * @param x_index The x-index of the reactivity grid-section that the mouse is over
     * @param y_index The y-index of the reactivity grid-section that the mouse is over
     * @param informationMode The type of information to be displayed, which can change depending on the render mode.
     * The options are:
     * <ul>
     * <li><code>POSITION_INFO_NONE</code>:</li> Do not display any coordinate information.
     * <li><code>POSITION_INFO_TRUE</code>:</li> Only display the mouse position in real units.
     * <li><code>POSITION_INFO_TRUE_AND_INDEX</code>:</li> Display both the mouse position in real units and in reactivity grid-section indices.
     * <li><code>POSITION_INFO_INDEX</code>:</li> Only display the reactivity grid-section indices.
     * </ul>
     */
    public void updatePositionIndicator(double x_pos, double y_pos, int x_index, int y_index, int informationMode){
        double factor = secm_distance_unit.getFactor();
        String unit_text = secm_distance_unit.getLabel();
        String text = "";
        switch(informationMode){
            case POSITION_INFO_NONE:
                position_indicator.setText("");
                break;
            case POSITION_INFO_TRUE:
                x_pos /= factor;
                y_pos /= factor;
                text = String.format(" x: %1.2f %s, y: %1.2f %s", x_pos, unit_text, y_pos, unit_text);
                position_indicator.setText(text);
                break;
            case POSITION_INFO_TRUE_AND_INDEX:
                x_pos /= factor;
                y_pos /= factor;
                text = String.format(" x: %1.2f %s index: %d, y: %1.2f %s index: %d", x_pos, unit_text, x_index, y_pos, unit_text, y_index);
                position_indicator.setText(text);
                break;
            case POSITION_INFO_INDEX:
                text = String.format(" x index: %d, y index: %d", x_index, y_index);
                position_indicator.setText(text);
                break;
            default:
                position_indicator.setText("");
                break;
        }
    }
    
    //Fields
    /**
     * The scale factor for the current in the SECM image. 
     * Multiplying the SECM image's current data with this value will give currents in Amperes.
     */
    private double secm_current_scale_factor;
    /**
     * The units of distance used by the SECM image. 
     */
    private Unit secm_distance_unit;
    
    /**
     * The most recent acceptable SEM scaling factor input by the user in pixels/metre.
     */
    private double sem_scale_accepted;
    /**
     * The most recent acceptable SEM x-offset input by the user in metres.
     */
    private double sem_xoffs_accepted;
    /**
     * The most recent acceptable SEM y-offset input by the user in metres.
     */
    private double sem_yoffs_accepted;
    /**
     * The most recent acceptable SEM rotation input by the user in degrees.
     */
    private double sem_rotation_accepted;
    
    /**
     * The most recent acceptable reactivity grid width input by the user in metres.
     */
    private double reac_xresolution_accepted;
    /**
     * The most recent acceptable reactivity grid height input by the user in metres.
     */
    private double reac_yresolution_accepted;
    
    /**
     * The most recent acceptable starting x-index for the sampling
     */
    private int sam_start_x_accepted;
    /**
     * The most recent acceptable step-size in the x-direction for the sampling
     */
    private int sam_step_size_x_accepted;
    /**
     * The most recent acceptable number of points in the x-direction for the sampling
     */
    private int sam_num_steps_x_accepted;
    /**
     * The most recent acceptable starting y-index for the sampling
     */
    private int sam_start_y_accepted;
    /**
     * The most recent acceptable step-size in the y-direction for the sampling
     */
    private int sam_step_size_y_accepted;
    /**
     * The most recent acceptable number of points in the y-direction for the sampling
     */
    private int sam_num_steps_y_accepted;
    

    //Widgets
    //SECM
    JButton secm_open_button;
    JComboBox secm_distance_units;
    JComboBox secm_current_units;
    JLabel secm_file_field;
    //SEM
    JButton sem_open_button;
    JLabel sem_file_field;
    JTextField sem_scale_field;
    JComboBox sem_scale_units;
    JTextField sem_xoffset_field;
    JComboBox sem_xoffset_units;
    JTextField sem_yoffset_field;
    JComboBox sem_yoffset_units;
    JTextField sem_rotation_field;
    JSlider sem_transparency;
    JCheckBox sem_mirrorx;
    JCheckBox sem_mirrory;
    //Reactivity
    JLabel reac_tool_select;
    JTextField reac_xres_field;
    JComboBox reac_xres_units;
    JTextField reac_yres_field;
    JComboBox reac_yres_units;
    JButton reac_crop;
    JButton reac_pencil;
    JButton reac_fill;
    JCheckBox reac_grid;
    JSlider reac_semtransparency;
    JSlider reac_seltransparency;
    //Sampling
    JTextField sam_xstart;
    JTextField sam_ystart;
    JTextField sam_xsize;
    JTextField sam_ysize;
    JTextField sam_xsteps;
    JTextField sam_ysteps;
    JButton sam_generate;
    
    //Global
    Visualizer view_screen;
    JLabel position_indicator;
    JTabbedPane control_panel;
    
    //Statics
    //padding
    /**
     * The width padding for text fields.
     */
    private static final int FIELD_PAD = 50;
    /**
     * The padding around most components, unless <code>FIELD_PAD</code> or <code>SPACER_PAD</code> is used instead.
     */
    private static final int DEFAULT_PAD = 3;
    /**
     * The padding used to separate different groups of user inputs.
     */
    private static final int SPACER_PAD = 5;
    
    //significant figures
    /**
     * The significant figures to be used when values from the <code>view_screen</code> are reported.
     */
    private static final int SIGFIGS = 5;
    
    //information
    /**
     * No information about the mouse position is to be displayed.
     */
    public static final int POSITION_INFO_NONE = 0;
    /**
     * Only the position of the mouse in physical units is to be displayed.
     */
    public static final int POSITION_INFO_TRUE = 1;
    /**
     * Both the position of the mouse in physical units and the indices of the grid-section that the mouse is in are to be displayed.
     */
    public static final int POSITION_INFO_TRUE_AND_INDEX = 2;
    /**
     * Only the indices of the grid-section that the mouse is in is to be displayed.
     */
    public static final int POSITION_INFO_INDEX = 3;
    
    /**
     * Holds the default values for all of the user inputs.
     */
    public Settings SETTINGS;
}
