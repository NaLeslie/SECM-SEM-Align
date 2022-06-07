/*
 * Created: 2022-01-14
 * Updated: 2022-03-29
 * Nathaniel Leslie
 */
package sem_secm_align;

import java.awt.AlphaComposite;
import java.awt.Color;
import sem_secm_align.data_types.SECMImage;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import sem_secm_align.data_types.SEMImage;
import sem_secm_align.data_types.Unit;
import sem_secm_align.settings.ColourSettings;
import sem_secm_align.settings.Settings;

/**
 * Handles rendering of the visualising panel
 * @author Nathaniel
 */
public class Visualizer extends JPanel{
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Visualizer(MainWindow parent, Settings settings){
        PARENT = parent;
        this.setMinimumSize(new Dimension(300,300));
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent evt) {
                mouseExit(evt);
            }
            @Override
            public void mousePressed(MouseEvent evt) {
                mousePress(evt);
            }
            @Override
            public void mouseReleased(MouseEvent evt) {
                mouseRelease(evt);
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent evt){
                mouseDrag(evt);
            }
        });
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e){
                updateGraphics();
            }
        });
        //secm variables
        secm_image = new SECMImage();
        secm_scale_factor = settings.UNITS_DISTANCE[settings.DEFAULT_DISTANCE_UNIT_SELECTION].getFactor();
        //sem variables
        sem_image = new SEMImage();
        sem_scale = settings.DEFAULT_SEM_SCALE;
        sem_xoffs = settings.DEFAULT_SEM_XOFFSET;
        sem_yoffs = settings.DEFAULT_SEM_YOFFSET;
        sem_rotation = settings.DEFAULT_SEM_ROTATION;
        sem_transparency = 0.5f;
        //sem movement variables
        extra_x_offset = 0.0;
        extra_y_offset = 0.0;
        extra_rotation = 0.0;
        initial_mouse_x = 0;
        initial_mouse_y = 0;
        initial_mouse_phi = 0;
        working_scale = 1;
    }
    
    /**
     * Forces the graphics of this visualizer to update
     */
    public void updateGraphics(){
        switch (render_mode) {
            case 1://draw SECM and SEM
                if(secm_image.isDisplayable()){
                    if(sem_image.isDisplayable()){
                        base_image = drawSEM(sem_transparency);
                    }else{
                        base_image = drawSECM();
                    }
                }else{
                    base_image = defaultImage();
                }
                break;
            case 2://draw reactivity
                break;
            case 3://draw sampling
                break;
            default: //just draw SECM
                if(secm_image.isDisplayable()){
                    base_image = drawSECM();
                }else{
                    base_image = defaultImage();
                }
                break;
        }
        repaint();
    }
    
    private BufferedImage defaultImage(){
        int width = this.getWidth();
        int height = this.getHeight();
        BufferedImage def = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics def_graphics = def.getGraphics();
        def_graphics.setColor(ColourSettings.BACKGROUND_COLOUR);
        def_graphics.fillRect(0, 0, width, height);
        return def;
    }
    
    private BufferedImage drawSECM(){
        int width = this.getWidth();
        int height = this.getHeight();
        BufferedImage secm = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics secm_graphics = secm.getGraphics();
        secm_graphics.setColor(ColourSettings.BACKGROUND_COLOUR);
        secm_graphics.fillRect(0, 0, width, height);
        
        double secm_width = secm_image.getXMax() - secm_image.getXMin();
        double secm_height = secm_image.getYMax() - secm_image.getYMin();
        
        int image_width, image_height;
        working_scale = (double)width/secm_width/secm_scale_factor; //pixels per metre
        
        if(secm_width/width < secm_height/height){
            image_width = (int)(secm_width/secm_height*(double)height);
            image_height = height;
            working_scale = (double)height/secm_height/secm_scale_factor;
        }
        else{
            image_width = width;
            image_height = (int)(secm_height/secm_width*(double)width);
        }
        
        int x0 = (width - image_width)/2;
        int y0 = (height - image_height)/2;
        
        //render the image
        for(int x = 0; x <= image_width; x++){
            double xcoord = (double)x / (double)image_width * secm_width;
            for(int y = 0; y <= image_height; y++){
                double ycoord = (double)y / (double)image_height * secm_height;
                double current = secm_image.getScaledCurrent(xcoord, ycoord, SECMImage.INTERPOLATION_NN);
                secm_graphics.setColor(ColourSettings.colorScale(current, ColourSettings.CSCALE_GRAY));
                secm_graphics.fillRect(x + x0, y + y0, 1, 1);
            }
        }
        
        return secm;
    }
    
    private BufferedImage drawSEM(float transparency){
        int width = this.getWidth();
        int height = this.getHeight();
        BufferedImage sem = drawSECM();
        Graphics2D sem_graphics = sem.createGraphics();
        
        double secm_width = secm_image.getXMax() - secm_image.getXMin();
        double secm_height = secm_image.getYMax() - secm_image.getYMin();
        
        int image_width, image_height;
        working_scale = (double)width/secm_width/secm_scale_factor; //pixels per metre
        
        if(secm_width/width < secm_height/height){
            image_width = (int)(secm_width/secm_height*(double)height);
            image_height = height;
            working_scale = (double)height/secm_height/secm_scale_factor;
        }
        else{
            image_width = width;
            image_height = (int)(secm_height/secm_width*(double)width);
        }
        
        
        int x0 = (width - image_width)/2;
        int y0 = (height - image_height)/2;
        
        sem_graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,transparency));
        double sem_cx = (double)image_width*0.5;
        double sem_cy = (double)image_height*0.5;
        
        //AffineTransform does stuff backwards!!!! (somebody made the matrices multiply in the wrong order)
        
        double rotation_rad = Math.toRadians(sem_rotation + extra_rotation);
        
        AffineTransform sem_transform = new AffineTransform();
        sem_transform.translate(working_scale*(sem_xoffs + extra_x_offset), working_scale*(sem_yoffs + extra_y_offset));
        sem_transform.rotate(rotation_rad, sem_cx, sem_cy);
        sem_transform.scale(working_scale/sem_scale, working_scale/sem_scale);
        if(sem_mirrorx){
            sem_transform.translate(sem_image.getWidth(), 0.0);
            sem_transform.scale(-1, 1);
        }
        if(sem_mirrory){
            sem_transform.translate(0.0, sem_image.getHeight());
            sem_transform.scale(1, -1);
        }
        AffineTransformOp ato = new AffineTransformOp(sem_transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        sem_graphics.drawImage(sem_image.getImage(), ato, x0, y0);
//        sem_graphics.drawImage(sem_image.getImage(), sem_transform, this);
//        sem_graphics.drawImage(sem_image.getImage(), x0, y0, this);
//        sem_graphics.drawImage(sem_image.getImage(), ato, x0, y0);
        
        return sem;
    }
    
    private Image drawReactivity(){
        Image reac = defaultImage();
        
        return reac;
    }
    
    private Image drawSampling(){
        Image sam = drawReactivity();
        
        return sam;
    }
    
    private void mouseDrag(MouseEvent e){
        if(render_mode == 1){
            semMouseDrag(e);
            updateGraphics();
        }
    }
    
    private void semMouseDrag(MouseEvent e){
        int mousex = e.getX();
        int mousey = e.getY();
        if(pan_in_progress){
            double delta_x = mousex - initial_mouse_x;
            double delta_y = mousey - initial_mouse_y;
            extra_x_offset = delta_x / working_scale;
            extra_y_offset = delta_y / working_scale;
            PARENT.setSEMXOffsetField(sem_xoffs + extra_x_offset);
            PARENT.setSEMYOffsetField(sem_yoffs + extra_y_offset);
        }
        
        if(rotation_in_progress){
            double phi = getPhi(mousex, mousey);
            extra_rotation = phi - initial_mouse_phi;
            PARENT.setSEMRotationField(sem_rotation + extra_rotation);
        }
    }
    
    private void mouseExit(MouseEvent e){
        if(render_mode == 1 && rotation_in_progress){
            cancelRotation();
        }

        if(render_mode == 1 && pan_in_progress){
            cancelPan();
        }
    }
    
    private void mousePress(MouseEvent e){
        if(e.getButton() == MouseEvent.BUTTON1){
            if(render_mode == 1 && rotation_in_progress){
                cancelRotation();
            }
            else if(render_mode == 1){
                pan_in_progress = true;
                initial_mouse_x = e.getX();
                initial_mouse_y = e.getY();
            }
        }
        else if(e.getButton() == MouseEvent.BUTTON3){
            if(render_mode == 1 && pan_in_progress){
                cancelPan();
            }
            else if(render_mode == 1){
                rotation_in_progress = true;
                initial_mouse_x = e.getX();
                initial_mouse_y = e.getY();
                initial_mouse_phi = getPhi(initial_mouse_x, initial_mouse_y);
            }
        }
    }
    
    private void mouseRelease(MouseEvent e){
        if(e.getButton() == MouseEvent.BUTTON1){
            if(render_mode == 1 && pan_in_progress){
                stopPan();
            }
        }
        else if(e.getButton() == MouseEvent.BUTTON3){
            if(render_mode == 1 && rotation_in_progress){
                stopRotation();
            }
        }
    }
    
    private void stopRotation(){
        rotation_in_progress = false;
        sem_rotation += extra_rotation;
        double cycles = Math.round(sem_rotation / 360.0);
        sem_rotation -= 360.0*cycles;
        extra_rotation = 0;
        initial_mouse_phi = 0;
        initial_mouse_x = 0;
        initial_mouse_y = 0;
    }
    
    private void stopPan(){
        pan_in_progress = false;
        sem_xoffs += extra_x_offset;
        sem_yoffs += extra_y_offset;
        extra_x_offset = 0;
        extra_y_offset = 0;
        initial_mouse_x = 0;
        initial_mouse_y = 0;
    }
    
    private void cancelRotation(){
        rotation_in_progress = false;
        extra_rotation = 0;
        initial_mouse_phi = 0;
        initial_mouse_x = 0;
        initial_mouse_y = 0;
        PARENT.setSEMRotationField(sem_rotation);
        updateGraphics();
    }
    
    private void cancelPan(){
        pan_in_progress = false;
        extra_x_offset = 0;
        extra_y_offset = 0;
        initial_mouse_x = 0;
        initial_mouse_y = 0;
        PARENT.setSEMXOffsetField(sem_xoffs);
        PARENT.setSEMYOffsetField(sem_yoffs);
        updateGraphics();
    }
    
    @Override
    public void paint(Graphics g){
        g.drawImage(base_image, 0, 0, this);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * 
     * @param rg 
     */
    public void setReactivityGrid(boolean rg){
        reac_grid = rg;
        updateGraphics();
    }
    
    /**
     * 
     * @param t 
     */
    public void setReactivitySelectionTransparency(float t){
        reac_selection_transparency = t;
        updateGraphics();
    }
    
    /**
     * 
     * @param t 
     */
    public void setReactivitySEMTransparency(float t){
        reac_sem_transparency = t;
        updateGraphics();
    }
    
    /**
     * 
     * @param toolid 
     */
    public void setReactivityTool(int toolid){
        reac_tool = toolid;
        updateGraphics();
    }
    
    /**
     * 
     * @param xres 
     */
    public void setReactivityXResolution(double xres){
        reac_xresolution = xres;
        updateGraphics();
    }
    
    /**
     * 
     * @param yres 
     */
    public void setReactivityYResolution(double yres){
        reac_yresolution = yres;
        updateGraphics();
    }
    
    /**
     * 
     * @param mode 
     */
    public void setRenderMode(int mode){
        render_mode = mode;
        updateGraphics();
    }
    
    /**
     * 
     * @param num 
     */
    public void setSamplingNumberXSteps(int num){
        sam_num_steps_x = num;
        updateGraphics();
    }
    
    /**
     * 
     * @param num 
     */
    public void setSamplingNumberYSteps(int num){
        sam_num_steps_y = num;
        updateGraphics();
    }
    
    /**\
     * 
     * @param start 
     */
    public void setSamplingStartingX(int start){
        sam_start_x = start;
        updateGraphics();
    }
    
    /**
     * 
     * @param start 
     */
    public void setSamplingStartingY(int start){
        sam_start_y = start;
        updateGraphics();
    }
    
    /**
     * 
     * @param step 
     */
    public void setSamplingStepSizeX(int step){
        sam_step_size_x = step;
        updateGraphics();
    }
    
    /**
     * 
     * @param step 
     */
    public void setSamplingStepSizeY(int step){
        sam_step_size_y = step;
        updateGraphics();
    }
    
    /**
     * 
     * @param secm 
     */
    public void setSECMImage(SECMImage secm){
        secm_image = secm;
        updateGraphics();
    }
    
    /**
     * 
     * @param scale_factor 
     */
    public void setSECMScale(double scale_factor){
        secm_scale_factor = scale_factor;
        updateGraphics();
    }
    
    /**
     * 
     * @param sem 
     */
    public void setSEMImage(SEMImage sem){
        sem_image = sem;
        updateGraphics();
    }
    
    /**
     * 
     * @param mirror 
     */
    public void setSEMMirrorX(boolean mirror){
        sem_mirrorx = mirror;
        updateGraphics();
    }
    
    /**
     * 
     * @param mirror 
     */
    public void setSEMMirrorY(boolean mirror){
        sem_mirrory = mirror;
        updateGraphics();
    }
    
    /**
     * 
     * @param rot 
     */
    public void setSEMRotation(double rot){
        sem_rotation = rot;
        updateGraphics();
    }
    
    /**
     * 
     * @param scale 
     */
    public void setSEMScale(double scale){
        sem_scale = scale;
        updateGraphics();
    }
    
    /**
     * 
     * @param t 
     */
    public void setSEMTransparency(float t){
        sem_transparency = t;
        updateGraphics();
    }
    
    /**
     * 
     * @param offs 
     */
    public void setSEMXOffs(double offs){
        sem_xoffs = offs;
        updateGraphics();
    }
    
    /**
     * 
     * @param offs 
     */
    public void setSEMYOffs(double offs){
        sem_yoffs = offs;
        updateGraphics();
    }
    //</editor-fold>
    
    private double getPhi(int mx, int my){
        double center_x = (double)this.getWidth()*0.5;
        double center_y = (double)this.getHeight()*0.5;
        
        double x = (double)mx - center_x;
        double y = (double)my - center_y;
        
        if(x == 0.0 && y == 0.0){
            return 0.0;
        }
        
        return Math.toDegrees(Math.atan2(y, x));
    }
    
    //Fields
    private Image base_image;
    private boolean reac_grid;
    private float reac_selection_transparency;
    private float reac_sem_transparency;
    private int reac_tool;
    private double reac_xresolution;
    private double reac_yresolution;
    private int render_mode;
    private double sam_num_steps_x;
    private double sam_num_steps_y;
    private double sam_start_x;
    private double sam_start_y;
    private double sam_step_size_x;
    private double sam_step_size_y;
    private SECMImage secm_image;
    private double secm_scale_factor;
    private SEMImage sem_image;
    private boolean sem_mirrorx;
    private boolean sem_mirrory;
    private double sem_rotation;
    private double sem_scale;
    private float sem_transparency;
    private double sem_xoffs;
    private double sem_yoffs;
    private boolean pan_in_progress;
    private boolean rotation_in_progress;
    private double extra_x_offset;
    private double extra_y_offset;
    private double extra_rotation;
    private int initial_mouse_x;
    private int initial_mouse_y;
    private double initial_mouse_phi;
    private double working_scale;

    
    
    //constants
    private final MainWindow PARENT;
    public static final int PECNCIL = 0;
    public static final int CROP = 1;
    public static final int FILL = 2;

}
