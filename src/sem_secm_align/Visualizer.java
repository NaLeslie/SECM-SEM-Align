/*
 * Created: 2022-01-14
 * Updated: 2022-06-15
 * Nathaniel Leslie
 */
package sem_secm_align;

import java.awt.AlphaComposite;
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
        sem_scale                   =        settings.DEFAULT_SEM_SCALE;
        sem_xoffs                   =        settings.DEFAULT_SEM_XOFFSET;
        sem_yoffs                   =        settings.DEFAULT_SEM_YOFFSET;
        sem_rotation                =        settings.DEFAULT_SEM_ROTATION;
        sem_transparency            = (float)settings.DEFAULT_SEM_Transparency;
        //sem movement variables
        extra_x_offset = 0.0;
        extra_y_offset = 0.0;
        extra_rotation = 0.0;
        initial_mouse_x = 0;
        initial_mouse_y = 0;
        initial_mouse_phi = 0;
        working_scale = 1;
        //reactivity
        reac_sem_transparency       = (float)settings.DEFAULT_REAC_SEM_TRANSPARENCY;
        reac_selection_transparency = (float)settings.DEFAULT_REAC_SELECTION_TRANSPARENCY;
        reac_xresolution            =        settings.DEFAULT_REAC_XRESOLUTION;
        reac_yresolution            =        settings.DEFAULT_REAC_YRESOLUTION;
        reac_tool = PENCIL;
        reac_grid                   =        settings.DEFAULT_REAC_SHOW_GRID;
        crop_x1 = 0;
        crop_x2 = 1;
        crop_y1 = 0;
        crop_y2 = 1;
        tentative_crop_x1 = 0;
        tentative_crop_x2 = 1;
        tentative_crop_y1 = 0;
        tentative_crop_y2 = 1;
        switches = new int[1][1];
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
                if(secm_image.isDisplayable()){
                    base_image = drawReactivity();
                }else{
                    base_image = defaultImage();
                }
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
            double xcoord = (double)x / (double)image_width * secm_width + secm_image.getXMin();
            for(int y = 0; y <= image_height; y++){
                double ycoord = (double)y / (double)image_height * secm_height + secm_image.getYMin();
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
        BufferedImage sem = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        if(transparency < 1.0f){
            sem = drawSECM();
        }
        else{
            Graphics sem_ugraphics = sem.getGraphics();
            sem_ugraphics.setColor(ColourSettings.BACKGROUND_COLOUR);
            sem_ugraphics.fillRect(0, 0, width, height);
        }
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
    
    private BufferedImage drawReactivity(){
        /////////////////////////////
        //draw the cropped SECM image
        int width = this.getWidth();
        int height = this.getHeight();
        BufferedImage reac = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D reac_graphics = reac.createGraphics();
        reac_graphics.setColor(ColourSettings.BACKGROUND_COLOUR);
        reac_graphics.fillRect(0, 0, width, height);
        
        //Evaluate the scaling and offsets for the cropped image
        double crop_width = (crop_x2 - crop_x1)/secm_scale_factor;
        double crop_height = (crop_y2 - crop_y1)/secm_scale_factor;
        double crop_scale = (double)width/crop_width/secm_scale_factor; //pixels per metre
        
        int image_width, image_height;
        
        if(crop_width/width < crop_height/height){
            image_width = (int)(crop_width/crop_height*(double)height);
            image_height = height;
            crop_scale = (double)height/crop_height/secm_scale_factor;
        }
        else{
            image_width = width;
            image_height = (int)(crop_height/crop_width*(double)width);
        }
        
        int x0 = (width - image_width)/2;
        int y0 = (height - image_height)/2;
        
        //render the SECM image
        for(int x = 0; x <= image_width; x++){
            double xcoord = (double)x / (double)image_width * crop_width + crop_x1/secm_scale_factor;
            for(int y = 0; y <= image_height; y++){
                double ycoord = (double)y / (double)image_height * crop_height + crop_y1/secm_scale_factor;
                double current = secm_image.getScaledCurrent(xcoord, ycoord, SECMImage.INTERPOLATION_NN);
                reac_graphics.setColor(ColourSettings.colorScale(current, ColourSettings.CSCALE_GRAY));
                reac_graphics.fillRect(x + x0, y + y0, 1, 1);
            }
        }
        
        //////////////////////////////
        //Render the cropped SEM image
        reac_graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,reac_sem_transparency));
        BufferedImage semimage = drawSEM(1.0f);
        AffineTransform sem_transform = new AffineTransform();
        
        double scalefactor = crop_scale / working_scale;
        sem_transform.scale(scalefactor, scalefactor);
       
        
        double secm_width = secm_image.getXMax() - secm_image.getXMin();
        double secm_height = secm_image.getYMax() - secm_image.getYMin();
        
        int precrop_imagewidth, precrop_imageheight;
        
        if(secm_width/width < secm_height/height){
            precrop_imagewidth = (int)(secm_width/secm_height*(double)height);
            precrop_imageheight = height;
        }
        else{
            precrop_imagewidth = width;
            precrop_imageheight = (int)(secm_height/secm_width*(double)width);
        }
        
        int precrop_x0 = (width - precrop_imagewidth) / 2;
        int precrop_y0 = (height - precrop_imageheight) / 2;
        
        double x0s = ((double)precrop_x0)*scalefactor;
        double y0s = ((double)precrop_y0)*scalefactor;
        
        int xdisp = getRenderX(secm_image.getXMin()*secm_scale_factor) - getRenderX(crop_x1) - (int)x0s + x0;
        int ydisp = getRenderY(secm_image.getYMin()*secm_scale_factor) - getRenderY(crop_y1) - (int)y0s + y0;
        
        AffineTransformOp ato = new AffineTransformOp(sem_transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        reac_graphics.drawImage(semimage, ato, xdisp, ydisp);
        
        ////////////////////////////////
        // reactivity-specific rendering
        //reac_cellwidth = reac_xresolution;
        //reac_cellheight = reac_yresolution;
        
        double wstart = Math.floor((crop_x1 - secm_image.getXMin()*secm_scale_factor) / reac_xresolution);
        double hstart = Math.floor((crop_y1 - secm_image.getYMin()*secm_scale_factor) / reac_yresolution);
        double wstop = Math.ceil((crop_x2 - secm_image.getXMin()*secm_scale_factor) / reac_xresolution);
        double hstop = Math.ceil((crop_y2 - secm_image.getYMin()*secm_scale_factor) / reac_yresolution);
        
        reac_graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,reac_selection_transparency));
        reac_graphics.setColor(ColourSettings.ACTIVE_COLOUR);
        for(int w_index = (int)wstart; w_index < (int)wstop; w_index ++){
            double x_m = secm_image.getXMin()*secm_scale_factor + reac_xresolution*(double)w_index;
            x_m = Math.max(x_m, crop_x1);
            double x2_m = secm_image.getXMin()*secm_scale_factor + reac_xresolution*((double)w_index + 1.0);
            x2_m = Math.min(x2_m, crop_x2);
            int cellwidth = getRenderX(x2_m) - getRenderX(x_m);
            for(int h_index = (int)hstart; h_index < (int)hstop; h_index ++){
                if(switches[w_index][h_index] > 0){
                    double y_m = secm_image.getYMin()*secm_scale_factor + reac_yresolution*(double)h_index;
                    y_m = Math.max(y_m, crop_y1);
                    double y2_m = secm_image.getYMin()*secm_scale_factor + reac_yresolution*((double)h_index + 1.0);
                    y2_m = Math.min(y2_m, crop_y2);
                    int cellheight = getRenderY(y2_m) - getRenderY(y_m);
                    reac_graphics.fillRect(getRenderX(x_m), getRenderY(y_m), cellwidth, cellheight);
                }
            }
        }

        if(reac_grid){
            reac_graphics.setColor(ColourSettings.GRID_COLOUR);
            for(double w_index = wstart + 1; w_index < wstop; w_index ++){
                double linex_m = secm_image.getXMin()*secm_scale_factor + reac_xresolution*w_index;
                int linex_p = getRenderX(linex_m);
                reac_graphics.drawLine(linex_p, y0, linex_p, height - y0);
            }

            for(double h_index = hstart + 1; h_index < hstop; h_index ++){
                double liney_m = secm_image.getYMin()*secm_scale_factor + reac_yresolution*h_index;
                int liney_p = getRenderY(liney_m);
                reac_graphics.drawLine(x0, liney_p, width - x0, liney_p);
            }
        }
        
        //render the selection box if a crop is in progress
        if(crop_in_progress){
            reac_graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,1.0f));
            reac_graphics.setColor(ColourSettings.SELECTION_COLOUR);
            reac_graphics.drawRect(tentative_crop_x1, tentative_crop_y1, tentative_crop_x2 - tentative_crop_x1, tentative_crop_y2 - tentative_crop_y1);
        }
        return reac;
    }
    
    private Image drawSampling(BufferedImage background_image){
        Image sam = drawReactivity();
        
        return sam;
    }
    
    private void mouseDrag(MouseEvent e){
        if(render_mode == 1){
            semMouseDrag(e);
            updateGraphics();
        }
        else if(render_mode == 2){
            reacMouseDrag(e);
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
    
    private void reacMouseDrag(MouseEvent e){
        if(crop_in_progress){
            tentative_crop_x2 = e.getX();
            tentative_crop_y2 = e.getY();
        }
        else if(drawing && !erasing){
            int mx = e.getX();
            int my = e.getY();
            System.out.println(getIndexX(mx));
            switches[getIndexX(mx)][getIndexY(my)] = 1;
        }
        else if(erasing){
            int mx = e.getX();
            int my = e.getY();
            switches[getIndexX(mx)][getIndexY(my)] = 0;
        }
    }
    
    private void mouseExit(MouseEvent e){
        if(render_mode == 1 && rotation_in_progress){
            cancelRotation();
        }

        if(render_mode == 1 && pan_in_progress){
            cancelPan();
        }
        
        if(render_mode == 2 && crop_in_progress){
            cancelCrop();
        }
    }
    
    private void mousePress(MouseEvent e){
        if(render_mode == 1){
            semMousePress(e);
        }
        else if(render_mode == 2){
            reacMousePress(e);
            updateGraphics();
        }
    }
    
    private void semMousePress(MouseEvent e){
        if(e.getButton() == MouseEvent.BUTTON1){
            if(rotation_in_progress){
                cancelRotation();
            }
            else{
                pan_in_progress = true;
                initial_mouse_x = e.getX();
                initial_mouse_y = e.getY();
            }
        }
        else if(e.getButton() == MouseEvent.BUTTON3){
            if(pan_in_progress){
                cancelPan();
            }
            else{
                rotation_in_progress = true;
                initial_mouse_x = e.getX();
                initial_mouse_y = e.getY();
                initial_mouse_phi = getPhi(initial_mouse_x, initial_mouse_y);
            }
        }
    }
    
    private void reacMousePress(MouseEvent e){
        if(e.getButton() == MouseEvent.BUTTON1){
            if(reac_tool == CROP){
                crop_in_progress = true;
                tentative_crop_x1 = e.getX();
                tentative_crop_x2 = e.getX();
                tentative_crop_y1 = e.getY();
                tentative_crop_y2 = e.getY();
            }
            else if(reac_tool == PENCIL){
                drawing = true;
                int mx = e.getX();
                int my = e.getY();
                switches[getIndexX(mx)][getIndexY(my)] = 1;
            }
        }
        else if(e.getButton() == MouseEvent.BUTTON3){
            if(reac_tool == CROP){
                undoCrop();
            }
            else if(reac_tool == PENCIL){
                erasing = true;
                int mx = e.getX();
                int my = e.getY();
                switches[getIndexX(mx)][getIndexY(my)] = 0;
            }
        }
    }
    
    private void mouseRelease(MouseEvent e){
        if(e.getButton() == MouseEvent.BUTTON1){
            if(render_mode == 1 && pan_in_progress){
                stopPan();
            }
            if(render_mode == 2 && crop_in_progress){
                stopCrop();
            }
            if(render_mode == 2 && drawing){
                drawing = false;
            }
        }
        else if(e.getButton() == MouseEvent.BUTTON3){
            if(render_mode == 1 && rotation_in_progress){
                stopRotation();
            }
            if(render_mode == 2 && erasing){
                erasing = false;
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
    
    private void stopCrop(){
        crop_in_progress = false;
        double cx1 = getTrueX(tentative_crop_x1);
        double cx2 = getTrueX(tentative_crop_x2);
        double cy1 = getTrueY(tentative_crop_y1);
        double cy2 = getTrueY(tentative_crop_y2);
        
        crop_x1 = cx1;
        crop_x2 = cx2;
        crop_y1 = cy1;
        crop_y2 = cy2;
        System.out.println("x: " + getRenderX(secm_image.getXMin()));
        System.out.println("y: " + getRenderY(secm_image.getYMin()));
        System.out.println("x: " + (0.0 - crop_x1 + secm_image.getXMin()));
        System.out.println("y: " + (0.0 - crop_y1 + secm_image.getYMin()));
        System.out.println("x: " + working_scale*(0.0 - crop_x1 + secm_image.getXMin()));
        System.out.println("y: " + working_scale*(0.0 - crop_y1 + secm_image.getYMin()));
        System.out.println("==============================================================");
        updateGraphics();
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
    
    private void cancelCrop(){
        crop_in_progress = false;
        tentative_crop_x1 = getRenderX(crop_x1);
        tentative_crop_x2 = getRenderX(crop_x2);
        tentative_crop_y1 = getRenderY(crop_y1);
        tentative_crop_y2 = getRenderY(crop_y2);
        updateGraphics();
    }
    
    private void undoCrop(){
        crop_x1 = secm_image.getXMin()*secm_scale_factor;
        crop_x2 = secm_image.getXMax()*secm_scale_factor;
        crop_y1 = secm_image.getYMin()*secm_scale_factor;
        crop_y2 = secm_image.getYMax()*secm_scale_factor;
        cancelCrop();
        System.out.println("x: " + getRenderX(secm_image.getXMin()));
        System.out.println("y: " + getRenderY(secm_image.getYMin()));
        //graphics update is called in cancel crop so we don't need to call it again
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
        double xbins = Math.ceil((secm_image.getXMax() - secm_image.getXMin())/reac_xresolution*secm_scale_factor);
        double ybins = Math.ceil((secm_image.getYMax() - secm_image.getYMin())/reac_yresolution*secm_scale_factor);
        switches = new int[(int)xbins][(int)ybins];
        updateGraphics();
    }
    
    /**
     * 
     * @param yres 
     */
    public void setReactivityYResolution(double yres){
        reac_yresolution = yres;
        double xbins = Math.ceil((secm_image.getXMax() - secm_image.getXMin())/reac_xresolution*secm_scale_factor);
        double ybins = Math.ceil((secm_image.getYMax() - secm_image.getYMin())/reac_yresolution*secm_scale_factor);
        switches = new int[(int)xbins][(int)ybins];
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
        crop_x1 = secm_image.getXMin()*secm_scale_factor;
        crop_x2 = secm_image.getXMax()*secm_scale_factor;
        crop_y1 = secm_image.getYMin()*secm_scale_factor;
        crop_y2 = secm_image.getYMax()*secm_scale_factor;
        double xbins = Math.ceil((secm_image.getXMax() - secm_image.getXMin())/reac_xresolution*secm_scale_factor);
        double ybins = Math.ceil((secm_image.getYMax() - secm_image.getYMin())/reac_yresolution*secm_scale_factor);
        switches = new int[(int)xbins][(int)ybins];
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
    
    private double getTrueX(int mx){
        int width = this.getWidth();
        int height = this.getHeight();
        //Evaluate the scaling and offsets for the cropped image
        double crop_width = (crop_x2 - crop_x1)/secm_scale_factor;
        double crop_height = (crop_y2 - crop_y1)/secm_scale_factor;
        
        int image_width;
        
        if(crop_width/width < crop_height/height){
            image_width = (int)(crop_width/crop_height*(double)height);
        }
        else{
            image_width = width;
        }
        
        int x0 = (width - image_width)/2;
        double xcoord = (double)(mx-x0) / (double)image_width * crop_width + crop_x1/secm_scale_factor;
        return xcoord*secm_scale_factor;
    }
    
    private double getTrueY(int my){
        int width = this.getWidth();
        int height = this.getHeight();
        //Evaluate the scaling and offsets for the cropped image
        double crop_width = (crop_x2 - crop_x1)/secm_scale_factor;
        double crop_height = (crop_y2 - crop_y1)/secm_scale_factor;
        
        int image_height;
        
        if(crop_width/width < crop_height/height){
            image_height = height;
        }
        else{
            image_height = (int)(crop_height/crop_width*(double)width);
        }
        
        int y0 = (height - image_height)/2;
        double ycoord = (double)(my-y0) / (double)image_height * crop_height + crop_y1/secm_scale_factor;
        return ycoord*secm_scale_factor;
    }
    
    private int getRenderX(double tx){
        int width = this.getWidth();
        int height = this.getHeight();
        //Evaluate the scaling and offsets for the cropped image
        double crop_width = (crop_x2 - crop_x1)/secm_scale_factor;
        double crop_height = (crop_y2 - crop_y1)/secm_scale_factor;
        
        int image_width;
        
        if(crop_width/width < crop_height/height){
            image_width = (int)(crop_width/crop_height*(double)height);
        }
        else{
            image_width = width;
        }
        
        int x0 = (width - image_width)/2;
        int xcoord = (int)((tx - crop_x1)/secm_scale_factor / crop_width * (double)image_width) + x0;
        return xcoord;
    }
    
    private int getRenderY(double ty){
        int width = this.getWidth();
        int height = this.getHeight();
        //Evaluate the scaling and offsets for the cropped image
        double crop_width = (crop_x2 - crop_x1)/secm_scale_factor;
        double crop_height = (crop_y2 - crop_y1)/secm_scale_factor;
        
        int image_height;
        
        if(crop_width/width < crop_height/height){
            image_height = height;
        }
        else{
            image_height = (int)(crop_height/crop_width*(double)width);
        }
        
        int y0 = (height - image_height)/2;
        int ycoord = (int)((ty - crop_y1)/secm_scale_factor / crop_height * (double)image_height) + y0;
        return ycoord;
    }
    
    private int getIndexX(int mx){
        double tx = getTrueX(mx);
        double index = Math.floor((tx - secm_image.getXMin()*secm_scale_factor) / reac_xresolution);
        return (int)index;
    }
    
    private int getIndexY(int my){
        double ty = getTrueY(my);
        double index = Math.floor((ty - secm_image.getYMin()*secm_scale_factor) / reac_yresolution);
        return (int)index;
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
    private double crop_x1;
    private double crop_x2;
    private double crop_y1;
    private double crop_y2;
    private boolean crop_in_progress;
    private int tentative_crop_x1;
    private int tentative_crop_y1;
    private int tentative_crop_x2;
    private int tentative_crop_y2;
    private int[][] switches;
    private boolean drawing;
    private boolean erasing;
    
    
    //constants
    private final MainWindow PARENT;
    public static final int PENCIL = 0;
    public static final int CROP = 1;
    public static final int FILL = 2;

}
