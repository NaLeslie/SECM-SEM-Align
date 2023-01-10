/*
 * Created: 2022-12-15
 * Updated: 2023-01-10
 * Nathaniel Leslie
 */
package sem_secm_align.edge_detection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import sem_secm_align.data_types.ImproperFileFormattingException;
import sem_secm_align.settings.ColourSettings;
import sem_secm_align.utility.filters.Filter;
import sem_secm_align.utility.filters.Identity;
import sem_secm_align.utility.filters.SobelX;
import sem_secm_align.utility.filters.SobelY;

/**
 *
 * @author Nathaniel
 */
public class EdgeVisualizer extends JPanel{
    
    public EdgeVisualizer(EdgeDetectionWindow parent){
        this.parent = parent;
        this.setMinimumSize(new Dimension(400,400));
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e){
                updateGraphics();
            }
        });
        
        display_image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_3BYTE_BGR);;
        display_mode = DISPLAY_MODE_PRE_FILTER;
        filtered_data = new double[1][1];
        filter = new Identity();
        lower_threshold = 1.0;
        pixel_width_over_height = 1.0;
        unfiltered_data = filtered_data;
        unthresholded_edges = filtered_data;
        upper_threshold = 2.0;
    }
    
    /**
     * 
     * @param g 
     */
    @Override
    public void paint(Graphics g){
        g.drawImage(display_image, 0, 0, this.getWidth(), this.getHeight(), this);
    }
    
    public void setDisplayMode(int display_mode){
        this.display_mode = display_mode;
        updateGraphics();
    }
    
    private void updateGraphics(){
        switch(display_mode){
            case DISPLAY_MODE_PRE_FILTER:
                drawPreFilter();
                break;
            case DISPLAY_MODE_POST_FILTER:
                drawPostFilter();
                break;
            case DISPLAY_MODE_EDGE_MAGNITUDES:
                drawEdges();
                break;
            case DISPLAY_MODE_THRESHOLDED_EDGES:
                drawThresholdedEdges();
                break;
            default:
                break;
        }
        this.repaint();
    }
    
    private void drawPreFilter(){
        display_image = plotImage(unfiltered_data, pixel_width_over_height, this.getWidth(), this.getHeight());
    }
    
    private void drawPostFilter(){
        display_image = plotImage(filtered_data, pixel_width_over_height, this.getWidth(), this.getHeight());
    }
    
    private void drawEdges(){
        display_image = plotImage(unthresholded_edges, pixel_width_over_height, this.getWidth(), this.getHeight());
    }
    
    private void drawThresholdedEdges(){
        int w = unthresholded_edges.length;
        int h = unthresholded_edges[0].length;
        double[][] thresholded_edges = new double[w][h];
        for(int x = 0; x < w; x++){
            for(int y = 0; y < h; y++){
                if(unthresholded_edges[x][y] < lower_threshold){
                    thresholded_edges[x][y] = 0;
                }
                else if(unthresholded_edges[x][y] <= upper_threshold){
                    thresholded_edges[x][y] = unthresholded_edges[x][y];
                }
                else{
                    thresholded_edges[x][y] = 0;
                }
            }
        }
        display_image = plotImage(thresholded_edges, pixel_width_over_height, this.getWidth(), this.getHeight());
    }
    
    private static Image plotImage(double[][] data, double pixel_width_over_height, int width, int height){
        Image plot = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        //check to make sure data[][] is not a dummy
        if(data.length <= 1 && data[0].length <= 1){
            return plot;
        }
        
        //find the maximum also check for NaN values.
        double max = data[0][0];
        for(int x = 0; x < data.length; x++){
            for(int y = 0; y < data[0].length; y++){
                if(Math.abs(data[x][y]) > max){
                    max = Math.abs(data[x][y]);
                }
                if(Double.isNaN(max)){
                    return plot;
                }
            }
        }
        
        //Determine the maximum value for the colour scale
        double plotmax = roundedMax(max);
        
        //determine plot size and position
        int available_width = width - PADDING_E - PADDING_W - LEGEND_WIDTH;
        int available_height = height - PADDING_N - PADDING_S;
        int plot_width = available_width;
        int plot_height = available_height;
        if(pixel_width_over_height*((double)(available_height*data.length)) / ((double)(data[0].length)) > (double)available_width){
            plot_height = (int)(((double)(available_width*data.length)) / (pixel_width_over_height * ((double)data[0].length))); 
        }
        else{
            plot_width = (int)(pixel_width_over_height*((double)(available_height*data.length)) / ((double)(data[0].length)));
        }
        double pixel_width = ((double)plot_width) / ((double)data.length);
        double pixel_height = ((double)plot_height) / ((double)data[0].length);
        int x0 = PADDING_W + (available_width - plot_width)/2;
        int y0 = PADDING_N + (available_height - plot_height)/2;
        
        //get the graphics context
        Graphics g = plot.getGraphics();
        int colour_mode = ColourSettings.CSCALE_GREY;
        
        //draw the image
        for(int x_index = 0; x_index < data.length; x_index++){
            int xn0 = (int)(((double)x_index)*pixel_width);
            int xn1 = (int)(((double)x_index + 1)*pixel_width);
            for(int y_index = 0; y_index < data.length; y_index++){
                int yn0 = (int)(((double)y_index)*pixel_height);
                int yn1 = (int)(((double)y_index + 1)*pixel_height);
                double dataval = Math.abs(data[x_index][y_index])/plotmax;
                g.setColor(ColourSettings.colourScale(dataval, colour_mode));
                g.fillRect(x0+xn0, y0+yn0, xn1-xn0, yn1-yn0);
            }
        }
        g.setColor(Color.BLACK);
        g.drawRect(x0, y0, plot_width, plot_height);
        
        //draw the colour scale
        int x0_cscale = x0 + plot_width + PADDING_E;
        for(int y = 0; y <= plot_height; y++){
            double complement = plot_height - y;
            double brightness = complement/((double)plot_height);
            g.setColor(ColourSettings.colourScale(brightness, colour_mode));
            g.fillRect(x0_cscale, y + y0, COLOUR_BAR_WIDTH, 1);
        }
        g.setColor(Color.BLACK);
        g.drawRect(x0_cscale, y0, COLOUR_BAR_WIDTH, plot_height);
        
        //label the colour scale
        int tic_label_x = x0_cscale + COLOUR_BAR_WIDTH + PADDING_E;
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        for(int i = 0; i < COLOUR_BAR_TICS; i++){
            double fraction = ((double)i)/((double)COLOUR_BAR_TICS - 1.0);
            double ticval = fraction*plotmax;
            String ticlabel = String.format("%1.2e", ticval);
            int tic_label_y = y0 + plot_height - (int)(((double)(i * plot_height)) / ((double)COLOUR_BAR_TICS)) + 5;
            g.drawString(ticlabel, tic_label_x, tic_label_y);
            g.fillRect(tic_label_x - PADDING_E - 1, tic_label_y, PADDING_E, 1);
        }
        
        
        return plot;
    }
    
    public void setFilter(Filter f){
        filter = f;
        updateFilteredData();
        if(display_mode != DISPLAY_MODE_PRE_FILTER){
            updateGraphics();
        }
    }
    
    public void setLowerThreshold(double l_threshold){
        lower_threshold = l_threshold;
        if(display_mode == DISPLAY_MODE_THRESHOLDED_EDGES){
            updateGraphics();
        }
    }
    
    public void setUnfilteredData(double[][] udata, double pixel_width, double pixel_height){
        pixel_width_over_height = pixel_width/pixel_height;
        unfiltered_data = udata;
        updateFilteredData();
        updateGraphics();
    }
    
    public void setUpperThreshold(double u_threshold){
        upper_threshold = u_threshold;
        if(display_mode == DISPLAY_MODE_THRESHOLDED_EDGES){
            updateGraphics();
        }
    }
    
    private int updateFilteredData(){
        filtered_data = filter.applyFilter(unfiltered_data);
        return updateEdges();
    }
    
    private int updateEdges(){
        // apply sobel operators
        SobelX sx = new SobelX();
        SobelY sy = new SobelY();
        double[][] xgradients = sx.applyFilter(filtered_data);
        double[][] ygradients = sy.applyFilter(filtered_data);
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
        unthresholded_edges = new double[edges.length][edges[0].length];
        double max = 0.0;
        for(int x = 0; x < edges.length; x++){
            for(int y = 0; y < edges[0].length; y++){
                if(edges[x][y] > 0){
                    unthresholded_edges[x][y] = Math.sqrt(squares[x][y]);
                    if(unthresholded_edges[x][y] > max){
                        max = unthresholded_edges[x][y];
                    }
                }
                else{
                    unthresholded_edges[x][y] = 0.0;
                }
            }
        }
        double unrounded_max = max;
        //Round the maximum value
        max = roundedMax(max);
        int numbins = 20;
        double binwidth = (max) / ((double)numbins);
        int[] histogram_counts = new int[numbins];
        for(int x = 0; x < edges.length; x++){
            for(int y = 0; y < edges[0].length; y++){
                if(edges[x][y] > 0){
                    if(unthresholded_edges[x][y] < max){
                        int index = (int)((unthresholded_edges[x][y]) / binwidth);
                        histogram_counts[index] ++;
                    }
                    else{
                        histogram_counts[numbins - 1] ++;
                    }
                }
            }
        }
        double[] histogram_mags = new double[numbins];
        for(int i = 0; i < numbins; i++){
            histogram_mags[i] = binwidth * ((double)i);
        }
        parent.setHistogramData(histogram_mags, histogram_counts, 0, unrounded_max);
        return 0;
    }
    
    private static double roundedMax(double true_max){
        //Determine the maximum value for the colour scale
        double logmax = Math.floor(Math.log10(true_max));
        double rounding = Math.pow(10, logmax - 1.0);
        double roundmax = (int)(true_max / rounding) + 1.0;
        return roundmax*rounding;
    }
    
    private Image display_image;
    private int display_mode;
    private double[][] filtered_data;
    private Filter filter;
    private double lower_threshold;
    private EdgeDetectionWindow parent;
    private double pixel_width_over_height;
    private double[][] unfiltered_data;
    private double[][] unthresholded_edges;
    private double upper_threshold;
    
    public static final int DISPLAY_MODE_PRE_FILTER = 0;
    public static final int DISPLAY_MODE_POST_FILTER = 1;
    public static final int DISPLAY_MODE_EDGE_MAGNITUDES = 2;
    public static final int DISPLAY_MODE_THRESHOLDED_EDGES = 3;
    
    private static final int PADDING_N = 5;
    private static final int PADDING_E = 5;
    private static final int PADDING_S = 5;
    private static final int PADDING_W = 5;
    private static final int COLOUR_BAR_TICS = 6;
    private static final int COLOUR_BAR_WIDTH = 15;
    private static final int LEGEND_WIDTH = PADDING_E*2 + COLOUR_BAR_WIDTH + 55;
}
