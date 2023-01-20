/*
 * Created: 2022-12-15
 * Updated: 2023-01-20
 * Nathaniel Leslie
 */
package sem_secm_align.edge_detection;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import sem_secm_align.settings.EdgeDetectionSettings;

/**
 * Component responsible for rendering the edge histogram.
 * Histogram data is sent to this component from its parent via {@link EdgeDetectionWindow#setHistogramData(double[], int[], double, double)} which calls {@link #setHistogramData(double[], int[])}.
 * @author Nathaniel
 */
public class EdgeHistogram extends JPanel{
    
    /**
     * Instantiates this component with the common settings between this component, its {@link EdgeDetectionWindow parent} and its {@link EdgeDetectionWindow#edge_display parent's EdgeVisualizer}.
     * @param ed_set The edge detection settings.
     * @see EdgeDetectionWindow#ed_settings
     * @see EdgeVisualizer#ed_settings
     */
    public EdgeHistogram(EdgeDetectionSettings ed_set){
        this.setMinimumSize(new Dimension(400,200));
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e){
                updateGraphics();
            }
        });
        ed_settings = ed_set;
        counts = new int[1];
        int w = 1;
        int h = 1;
        //initialize display_image
        display_image = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = display_image.getGraphics();
        g.setColor(ed_settings.BACKGROUND_COLOR);
        g.fillRect(0, 0, w, h);
        magnitudes = new double[1];
        min = 0.0;
        max = 1.0;
    }
    
    /**
     * The paint method for this component. Draws {@link #display_image} to this component.
     * @param g The graphics context for this component
     */
    @Override
    public void paint(Graphics g){
        g.drawImage(display_image, 0, 0, this.getWidth(), this.getHeight(), this);
    }
    
    /**
     * Sets the histogram data to be plotted and calls {@link #updateGraphics()}.
     * @param mags The lower limit of the edge magnitude that falls within each bin
     * @param counts The number of edge pixels that fall within the magnitude range of each bin
     * @see EdgeDetectionWindow#setHistogramData(double[], int[], double, double) 
     */
    public void setHistogramData(double[] mags, int[] counts){
        if(counts.length == mags.length){
            this.counts = counts;
            this.magnitudes = mags;
            updateGraphics();
        }
    }
    
    /**
     * Sets the threshold range (the threshold is displayed as a gray background behind the histogram bars)
     * @param min The threshold minimum
     * @param max The threshold maximum
     * @see EdgeDetectionWindow#forceThresholdUpdate() 
     * @see EdgeDetectionWindow#thresholdMaxChange() 
     * @see EdgeDetectionWindow#thresholdMaxFocusLost()  
     * @see EdgeDetectionWindow#thresholdMinChange() 
     * @see EdgeDetectionWindow#thresholdMinFocusLost()  
     */
    public void setThresholdDomain(double min, double max){
        this.min = min;
        this.max = max;
        updateGraphics();
    }
    
    /**
     * Renders the histogram in {@link #display_image}. 
     * The output of this image may be modified by {@link #ed_settings} and this component's various padding constants.
     * @return 0 if the update executes correctly, 1 otherwise.
     * @see #ed_settings
     * @see #E_PADDING
     * @see #W_PADDING
     * @see #N_PADDING
     * @see #S_PADDING
     * @see #INTER_TEXT_PADDING
     */
    public int updateGraphics(){
        int w = this.getWidth();
        int h = this.getHeight();
        
        if(w < 1 || h < 1){
            display_image = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
            return 1;
        }
        
        display_image = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        //begin drawing
        Graphics g = display_image.getGraphics();
        g.setColor(ed_settings.BACKGROUND_COLOR);
        g.setFont(ed_settings.LABEL_FONT);
        g.fillRect(0, 0, w, h);
        
        if(counts.length < 2){
            return 1;
        }
        //determine the domain and range of the histogram
        //this takes advantage of magnitudes[0] being 0 and magnitudes[1] being the width of a magnitude bin.
        int maxcounts = 1;
        double domain_maximum = magnitudes[magnitudes.length - 1] + magnitudes[1];
        int start = (int)(min / magnitudes[1]);
        int stop = (int)(max / magnitudes[1]);
        if(start < 0){
            start = 0;
        }
        else if(start >= counts.length){
            start = counts.length - 1;
        }
        if(stop < 0){
            stop = 0;
        }
        else if(stop >= counts.length){
            stop = counts.length - 1;
        }
        if(stop < start){
            stop = start;
        }
        for(int i = start; i <= stop; i++){
            if(counts[i] > maxcounts){
                maxcounts = counts[i];
            }
        }
        double range_maximum = roundedMax(maxcounts);
        //compute axes tics
        String[] xticlabels = new String[counts.length + 1];
        String[] yticlabels = new String[6];
        for(int i = 0; i < magnitudes.length; i++){
            xticlabels[i] = String.format("%1.2e", magnitudes[i]);
        }
        xticlabels[xticlabels.length - 1] = String.format("%1.2e", domain_maximum);
        
        for(int i = 0; i < 6; i++){
            double di = i;
            double tc = di / 5.0 * range_maximum;
            yticlabels[i] = String.format("%1.2e", tc);
        }
        //compute size of tic labels
        final int TEXT_HEIGHT = g.getFontMetrics().getAscent();
        String ylabel = "COUNTS";
        int maxw = 0;
        for(char c : ylabel.toCharArray()){
            int cw = g.getFontMetrics().charWidth(c);
            if(cw > maxw){
                maxw = cw; 
            }
        } 
        final int Y_LABEL_WIDTH = maxw;
        maxw = 0;
        for(String ytl : yticlabels){
            int lw = g.getFontMetrics().stringWidth(ytl);
            if(lw > maxw){
                maxw = lw;
            }
        }
        final int Y_TICS_WIDTH = maxw;
        final int LAST_XTIC_WIDTH = g.getFontMetrics().stringWidth(xticlabels[xticlabels.length - 1]);
        //compute the space to be occupied by the plot
        int plotwidth = w - E_PADDING - W_PADDING - 2*INTER_TEXT_PADDING - Y_LABEL_WIDTH - Y_TICS_WIDTH - LAST_XTIC_WIDTH/2;
        int x0 = E_PADDING + 2*INTER_TEXT_PADDING + Y_LABEL_WIDTH + Y_TICS_WIDTH;
        int plotheight = h - N_PADDING - S_PADDING - 2*INTER_TEXT_PADDING - 2*TEXT_HEIGHT;
        int y0 = N_PADDING + plotheight;
        
        //compute which xticlabels will be rendered
        maxw = 0;
        for(String xtl : xticlabels){
            int lw = g.getFontMetrics().stringWidth(xtl);
            if(lw > maxw){
                maxw = lw;
            }
        }
        double totallength = (maxw + maxw/10)*(xticlabels.length);
        double skip = Math.ceil(totallength / ((double)plotwidth));
        if(skip < 1){
            skip = 1;
        }
        final int XTIC_MOD = (int)skip;// only every XTIC_MOD xticlabels will be rendered
        
        //draw threshold domain
        int minx = (int)(min / domain_maximum * ((double)plotwidth));
        int maxx = (int)(max / domain_maximum * ((double)plotwidth));
        g.setColor(ed_settings.DOMAIN_SELECTION_COLOR);
        g.fillRect(x0 + minx, y0 - plotheight, maxx-minx, plotheight);
        
        
        
        //draw bars and axes tics
        for(int i = 0; i < counts.length; i++){
            double di = i;
            double dl = counts.length;
            double dpw = plotwidth;
            double dc = counts[i];
            double dph = plotheight;
            int barx0 = (int)(di * dpw / dl);
            int barx1 = (int)((di + 1.0) * dpw / dl);
            int barh = (int)(dc * dph / range_maximum);
            if(barh > plotheight){
                barh = plotheight;
            }
            
            g.setColor(ed_settings.BAR_FILL_COLOR);
            g.fillRect(x0 + barx0, y0 - barh, barx1 - barx0, barh);
            g.setColor(ed_settings.BAR_OUTLINE_COLOR);
            g.drawRect(x0 + barx0, y0 - barh, barx1 - barx0, barh);
            
            if(i % XTIC_MOD == 0){
                int tx = x0 + barx0 - getPixelWidth(g, xticlabels[i])/2;
                g.drawString(xticlabels[i], tx, y0 + INTER_TEXT_PADDING + TEXT_HEIGHT);
            }
        }
        if(counts.length % XTIC_MOD == 0){
            int tx = x0 + plotwidth - getPixelWidth(g, xticlabels[counts.length])/2;
            g.setColor(ed_settings.TEXT_COLOR);
            g.drawString(xticlabels[counts.length], tx, y0 + INTER_TEXT_PADDING + TEXT_HEIGHT);
        }
        
        g.setColor(ed_settings.TEXT_COLOR);
        for(int i = 0; i < yticlabels.length; i++){
            double di = i;
            double dph = plotheight;
            double ytn = yticlabels.length - 1;
            int tx = x0 - INTER_TEXT_PADDING - getPixelWidth(g, yticlabels[i]);
            int ty = y0 - (int)(di * dph / ytn) + TEXT_HEIGHT/3;
            g.drawString(yticlabels[i], tx, ty);
        }
        
        //draw border
        g.setColor(ed_settings.AXES_COLOR);
        g.drawRect(x0, y0 - plotheight, plotwidth, plotheight);
        
        //draw axes labels
        g.setColor(ed_settings.TEXT_COLOR);
        for(int i = 0; i < ylabel.length(); i++){
            int cy = y0 - (plotheight + TEXT_HEIGHT*ylabel.length())/2 + i*TEXT_HEIGHT;
            String ytxt = ylabel.charAt(i) + "";
            g.drawString(ytxt, W_PADDING, cy);
        }
        String xlabel = "EDGE MAGNITUDE";
        g.drawString(xlabel, x0 + (plotwidth - getPixelWidth(g, xlabel))/2, y0 + 2*INTER_TEXT_PADDING + TEXT_HEIGHT + TEXT_HEIGHT);
        repaint();
        return 0;
    }
    
    /**
     * Computes the width of a given string in pixels.
     * @param g the graphics context that the string may be rendered with.
     * @param input the string to be measured
     * @return The width of the string in pixels
     * @see Graphics#getFontMetrics() 
     * @see FontMetrics#stringWidth(java.lang.String) 
     */
    private int getPixelWidth(Graphics g, String input){
        return g.getFontMetrics().stringWidth(input);
    }
    
    /**
     * Gives an upwards-rounded version of the argument such that the first two significant digits are divisible by 5.
     * @param true_max the value to be rounded
     * @return The rounded value. If the input's first two significant digits are already divisible by 5, the next value that fits this criteria will be returned.
     */
    private static double roundedMax(int true_max){
        //Determine the maximum value for the colour scale
        double logmax = Math.floor(Math.log10(true_max));
        double rounding = Math.pow(10, logmax)*0.5;
        double roundmax = (int)(true_max / rounding) + 1.0;
        return roundmax*rounding;
    }
    
    /**
     * The populations of each of the histogram bins
     */
    private int[] counts;
    /**
     * The image that is painted to this component.
     * This is a {@link BufferedImage#TYPE_3BYTE_BGR} encoded image
     * @see #paint(java.awt.Graphics) 
     */
    private Image display_image;
    /**
     * The lowest magnitudes for each of the histogram bins
     */
    private double[] magnitudes;
    /**
     * The threshold minimum
     */
    private double min;
    /**
     * The threshold maximum.
     */
    private double max;
    /**
     * The settings that are shared between this component, its {@link EdgeDetectionWindow parent} as well as the {@link EdgeDetectionWindow#edge_display parent's EdgeVisualizer}.
     */
    private EdgeDetectionSettings ed_settings;
    
    /**
     * Space between the right of this component and the edge of the last xtic label
     * @see #updateGraphics() 
     */
    private static final int E_PADDING = 10;
    /**
     * Space between the left of this component and the ylabel
     * @see #updateGraphics() 
     */
    private static final int W_PADDING = 10;
    /**
     * Space between the top of this component and the plot
     * @see #updateGraphics() 
     */
    private static final int N_PADDING = 10;
    /**
     * Space between the bottom of this component and the xlabel
     * @see #updateGraphics() 
     */
    private static final int S_PADDING = 10;
    /**
     * Space between the axes labels, tic labels and the plot
     * @see #updateGraphics() 
     */
    private static final int INTER_TEXT_PADDING = 5;
}
