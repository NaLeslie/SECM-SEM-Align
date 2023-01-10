/*
 * Created: 2022-12-15
 * Updated: 2023-01-10
 * Nathaniel Leslie
 */
package sem_secm_align.edge_detection;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author Nathaniel
 */
public class EdgeHistogram extends JPanel{
    
    public EdgeHistogram(){
        
    }
    
    @Override
    public void paint(Graphics g){
        g.drawImage(display_image, 0, 0, this.getWidth(), this.getHeight(), this);
    }
    
    public void setHistogramData(double[] mags, int[] counts){
        if(counts.length == mags.length){
            this.counts = counts;
            this.magnitudes = mags;
            updateGraphics();
        }
    }
    
    public void setThresholdDomain(double min, double max){
        this.min = min;
        this.max = max;
        updateGraphics();
    }
    
    public int updateGraphics(){
        int w = this.getWidth();
        int h = this.getHeight();
        display_image = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        //begin drawing
        Graphics g = display_image.getGraphics();
        g.setColor(BACKGROUND_COLOR);
        g.setFont(LABEL_FONT);
        g.fillRect(0, 0, w, h);
        
        if(counts.length < 1){
            return 1;
        }
        final int TEXT_HEIGHT = g.getFontMetrics().getHeight();
        int plotwidth = w - E_PADDING - W_PADDING - 2*INTER_TEXT_PADDING - Y_LABEL_WIDTH - Y_TICS_WIDTH;
        int x0 = E_PADDING + 2*INTER_TEXT_PADDING + Y_LABEL_WIDTH + Y_TICS_WIDTH;
        int plotheight = h - N_PADDING - S_PADDING - 2*INTER_TEXT_PADDING - 2*TEXT_HEIGHT;
        int y0 = N_PADDING + plotheight;
        
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
        
        //draw threshold domain
        int minx = (int)(min / domain_maximum * ((double)plotwidth));
        int maxx = (int)(max / domain_maximum * ((double)plotwidth));
        g.setColor(DOMAIN_SELECTION_COLOR);
        g.fillRect(x0 + minx, y0 - plotheight, maxx-minx, plotheight);
        
        //compute axes tics
        String[] xticlabels = new String[counts.length + 1];
        String[] yticlabels = new String[6];
        for(int i = 0; i < magnitudes.length; i++){
            xticlabels[i] = String.format("%1.2e", magnitudes[i]);
        }
        xticlabels[xticlabels.length - 1] = String.format("%1.2e", range_maximum);
        
        for(int i = 0; i < 6; i++){
            double di = i;
            double tc = di / 5.0 * range_maximum;
            yticlabels[i] = String.format("%1.2e", tc);
        }
        
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
            
            g.setColor(BAR_FILL_COLOR);
            g.fillRect(x0 + barx0, y0 - barh, barx1 - barx0, barh);
            g.setColor(BAR_OUTLINE_COLOR);
            g.drawRect(x0 + barx0, y0 - barh, barx1 - barx0, barh);
            
            if(i % 2 == 0){
                int tx = x0 + barx0 - getPixelWidth(g, xticlabels[i])/2;
                g.drawString(xticlabels[i], tx, y0 + INTER_TEXT_PADDING);
            }
        }
        if(counts.length % 2 == 0){
            int tx = x0 + plotwidth - getPixelWidth(g, xticlabels[counts.length])/2;
            g.setColor(TEXT_COLOR);
            g.drawString(xticlabels[counts.length], tx, y0 + INTER_TEXT_PADDING);
        }
        
        g.setColor(TEXT_COLOR);
        for(int i = 0; i < yticlabels.length; i++){
            double di = i;
            double dph = plotheight;
            double ytn = yticlabels.length;
            int tx = x0 - INTER_TEXT_PADDING - getPixelWidth(g, yticlabels[i]);
            int ty = y0 - (int)(di * dph / ytn) - TEXT_HEIGHT/2;
            g.drawString(yticlabels[i], tx, ty);
        }
        
        //draw border
        g.setColor(AXES_COLOR);
        g.drawRect(x0, y0 - plotheight, plotwidth, plotheight);
        
        //draw axes labels
        g.setColor(TEXT_COLOR);
        String ylabel = "C\nO\nU\nN\nT\nS";
        g.drawString(ylabel, E_PADDING, y0 - (plotheight + getPixelHeight(g, ylabel))/2);
        String xlabel = "EDGE MAGNITUDE";
        g.drawString(xlabel, x0 + (plotwidth - getPixelWidth(g, xlabel))/2, y0 + 2*INTER_TEXT_PADDING + getPixelHeight(g, xlabel));
        repaint();
        return 0;
    }
    
    private int getPixelHeight(Graphics g, String input){
        int pixels_per_line = g.getFontMetrics().getHeight();
        char[] cin = input.toCharArray();
        int lines = 1;
        for(char c : cin){
            if(c == '\n'){
                lines ++;
            }
        }
        return lines*pixels_per_line;
    }
    
    private int getPixelWidth(Graphics g, String input){
        return g.getFontMetrics().stringWidth(input);
    }
    
    /**
     * Gives an upwards-rounded version of the argument such that the first two significant digits are divisible by 5.
     * @param true_max
     * @return 
     */
    private static double roundedMax(int true_max){
        //Determine the maximum value for the colour scale
        double logmax = Math.floor(Math.log10(true_max));
        double rounding = Math.pow(10, logmax)*0.5;
        double roundmax = (int)(true_max / rounding) + 1.0;
        return roundmax*rounding;
    }
    
    private int[] counts;
    private Image display_image;
    private double[] magnitudes;
    private double min;
    private double max;
    
    private static final int E_PADDING = 10;
    private static final int W_PADDING = 10;
    private static final int N_PADDING = 10;
    private static final int S_PADDING = 10;
    private static final int INTER_TEXT_PADDING = 5;
    private static final int Y_LABEL_WIDTH = 10;
    private static final int Y_TICS_WIDTH = 40;
    private static final Color DOMAIN_SELECTION_COLOR = Color.LIGHT_GRAY;
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BAR_FILL_COLOR = Color.YELLOW;
    private static final Color BAR_OUTLINE_COLOR = Color.BLACK;
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color AXES_COLOR = Color.BLACK;
    private static final Font LABEL_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 10);
}
