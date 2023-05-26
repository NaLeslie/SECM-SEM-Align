/*
 * Created: 2022-12-15
 * Updated: 2023-05-26
 * Nathaniel Leslie
 */
package sem_secm_align.edge_detection;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import sem_secm_align.settings.ColourSettings;
import sem_secm_align.settings.EdgeDetectionSettings;
import sem_secm_align.utility.filters.Filter;
import sem_secm_align.utility.filters.SobelX;
import sem_secm_align.utility.filters.SobelY;

/**
 * Component responsible for rendering the selected image.
 * This class also handles the filtering and Canny edge detection operations.
 * Edge histogram results are transmitted to the {@link EdgeHistogram} via this component's {@link #parent} {@link EdgeDetectionWindow}.
 * @author Nathaniel
 * @see <a href="https://en.wikipedia.org/wiki/Canny_edge_detector">Wikipedia: Canny Edge Detection</a> 
 */
public class EdgeVisualizer extends JPanel{
    
    /**
     * Instantiates the EdgeVisualizer component.
     * @param parent The parent {@link EdgeDetectionWindow} for this component.
     * @param ed_set The common settings between this component, the {@link EdgeDetectionWindow} and the {@link EdgeHistogram}.
     */
    public EdgeVisualizer(EdgeDetectionWindow parent, EdgeDetectionSettings ed_set){
        this.parent = parent;
        this.setMinimumSize(new Dimension(400,400));
        ed_settings = ed_set;
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e){
                updateGraphics();
            }
        });
        
        display_image = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);;
        display_mode = ed_settings.DEFAULT_DISPLAY_MODE;
        edge_detector_mode = ed_settings.DEFAULT_EDGE_DETECTOR;
        filtered_data = new double[1][1];
        filter = ed_settings.FILTER_OPTIONS[ed_settings.DEFAULT_FILTER];
        lower_threshold = 1.0;
        pixel_width_over_height = 1.0;
        unfiltered_data = filtered_data;
        unthresholded_edges = filtered_data;
        upper_threshold = 2.0;
    }
    
    /**
     * The paint method for this component. Draws {@link #display_image} to this component.
     * {@link #display_image} is updated with the {@link #updateGraphics()} method.
     * @param g the graphics context for this component.
     */
    @Override
    public void paint(Graphics g){
        g.drawImage(display_image, 0, 0, this.getWidth(), this.getHeight(), this);
    }
    
    /**
     * Changes the display mode of this component and calls {@link #updateGraphics()}.
     * @param display_mode The display mode for this component. Options are:
     * <ul>
     * <li>{@link EdgeDetectionSettings#DISPLAY_MODE_PRE_FILTER}: draw the image before the filter is applied.</li>
     * <li>{@link EdgeDetectionSettings#DISPLAY_MODE_POST_FILTER}: draw the image after the filter is applied.</li>
     * <li>{@link EdgeDetectionSettings#DISPLAY_MODE_EDGE_MAGNITUDES}: draw the detected edge magnitudes.</li>
     * <li>{@link EdgeDetectionSettings#DISPLAY_MODE_THRESHOLDED_EDGES}: draw only the detected edge magnitudes that fall within the thresholded range.</li>
     * </ul>
     */
    public void setDisplayMode(int display_mode){
        this.display_mode = display_mode;
        updateGraphics();
    }
    
    /**
     * Sets the edge or feature detector to be used.
     * @param edge_detector 
     */
    public void setEdgeDetector(int edge_detector){
        edge_detector_mode = edge_detector;
        updateEdges();
        if(display_mode != EdgeDetectionSettings.DISPLAY_MODE_PRE_FILTER && display_mode != EdgeDetectionSettings.DISPLAY_MODE_POST_FILTER){
            updateGraphics();
        }
    }
    
    /**
     * Updates {@link #display_image} using the latest settings and repaints this component.
     */
    private void updateGraphics(){
        switch(display_mode){
            case EdgeDetectionSettings.DISPLAY_MODE_PRE_FILTER:
                drawPreFilter();
                break;
            case EdgeDetectionSettings.DISPLAY_MODE_POST_FILTER:
                drawPostFilter();
                break;
            case EdgeDetectionSettings.DISPLAY_MODE_EDGE_MAGNITUDES:
                drawEdges();
                break;
            case EdgeDetectionSettings.DISPLAY_MODE_THRESHOLDED_EDGES:
                drawThresholdedEdges();
                break;
            default:
                break;
        }
        this.repaint();
    }
    
    /**
     * Sets {@link #display_image} to the image before the filter is applied.
     */
    private void drawPreFilter(){
        display_image = plotImage(unfiltered_data, pixel_width_over_height, this.getWidth(), this.getHeight());
    }
    
    /**
     * Sets {@link #display_image} to the image after the filter is applied.
     */
    private void drawPostFilter(){
        display_image = plotImage(filtered_data, pixel_width_over_height, this.getWidth(), this.getHeight());
    }
    
    /**
     * Sets {@link #display_image} to the detected edge magnitudes.
     */
    private void drawEdges(){
        display_image = plotImage(unthresholded_edges, pixel_width_over_height, this.getWidth(), this.getHeight());
    }
    
    /**
     * Sets {@link #display_image} to the detected edge magnitudes that fall within the thresholded range.
     */
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
    
    /**
     * Creates an image with the same width and height as this component (so long as length and width >= 1)
     * This image contains a heatmap of <code>data[][]</code>. 
     * This heatmap comes with a labelled colour scale and plots along the x and y axes with the same scale (width in real space represented by a pixel is the same as the height).
     * Uses colour and font settings specified by {@link #ed_settings}.
     * @param data The data to be plotted as <code>data[x][y]</code>
     * @param pixel_width_over_height The width divided by height in real space represented by a data point.
     * @param width The desired width of the image in pixels.
     * @param height The desired height of the image in pixels.
     * @return a {@link BufferedImage#TYPE_3BYTE_BGR} image containing a plot and its associated heat map. 
     * There are some exceptions to this:
     * <ul>
     * <li>If either <code>width</code> or <code>height</code> are less than 1, a blank 1x1 image will be returned.</li>
     * <li>If <code>data[][]</code> has any zero-dimensions, an empty image with the same dimensions as this component will be returned.</li>
     * <li>If <code>data[][]</code> contains any {@link Double#NaN}, an empty image with the same dimensions as this component will be returned.</li>
     * </ul>
     * @see #ed_settings
     */
    private Image plotImage(double[][] data, double pixel_width_over_height, int width, int height){
        int nan_behaviour = NAN_BEHAVIOUR_ZERO;
        if(width < 1 || height < 1){
            return new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
        }
        
        Image plot = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        //check to make sure data[][] is not a dummy
        if(data.length <= 1 && data[0].length <= 1){
            return plot;
        }
        
        //find the maximum also check for NaN values.
        double max = data[0][0];
        for(int x = 0; x < data.length; x++){
            for(int y = 0; y < data[0].length; y++){
                if(Double.isNaN(data[x][y])){
                    if(nan_behaviour == NAN_BEHAVIOUR_ZERO){
                        data[x][y] = 0;
                    }
                    
//                    System.out.println("NaN detected.");
//                    return plot;
                }
                if(Math.abs(data[x][y]) > max){
                    max = Math.abs(data[x][y]);
                }
            }
        }
        
        //Determine the maximum value for the colour scale
        double plotmax = roundedMax(max);
        
        //get the graphics context
        Graphics g = plot.getGraphics();
        g.setColor(ed_settings.BACKGROUND_COLOR);
        g.fillRect(0, 0, width, height);
        g.setFont(ed_settings.LABEL_FONT);
        final int TEXT_HEIGHT = g.getFontMetrics().getAscent();
        int max_text_width = 0;
        for(int i = 0; i <= COLOUR_BAR_TICS; i++){
            double fraction = ((double)i)/((double)COLOUR_BAR_TICS - 1.0);
            double ticval = fraction*plotmax;
            String ticlabel = String.format("%1.2e", ticval);
            int text_width = g.getFontMetrics().stringWidth(ticlabel);
            if(text_width > max_text_width){
                max_text_width = text_width;
            }
        }
        final int TEXT_WIDTH = max_text_width;
        
        //determine plot size and position
        int available_width = width - PADDING_E - PADDING_W - TEXT_WIDTH - 3*PADDING_INTERTEXT - COLOUR_BAR_WIDTH;
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
        
        
        
        //draw the image
        for(int x_index = 0; x_index < data.length; x_index++){
            int xn0 = (int)(((double)x_index)*pixel_width);
            int xn1 = (int)(((double)x_index + 1)*pixel_width);
            for(int y_index = 0; y_index < data[0].length; y_index++){
                int yn0 = (int)(((double)y_index)*pixel_height);
                int yn1 = (int)(((double)y_index + 1)*pixel_height);
                double dataval = Math.abs(data[x_index][y_index])/plotmax;
                g.setColor(ColourSettings.colourScale(dataval, ed_settings.COLOR_MODE));
                g.fillRect(x0+xn0, y0+yn0, xn1-xn0, yn1-yn0);
            }
        }
        g.setColor(ed_settings.AXES_COLOR);
        g.drawRect(x0, y0, plot_width, plot_height);
        
        //draw the colour scale
        int x0_cscale = x0 + plot_width + PADDING_INTERTEXT;
        for(int y = 0; y <= plot_height; y++){
            double complement = plot_height - y;
            double brightness = complement/((double)plot_height);
            g.setColor(ColourSettings.colourScale(brightness, ed_settings.COLOR_MODE));
            g.fillRect(x0_cscale, y + y0, COLOUR_BAR_WIDTH, 1);
        }
        g.setColor(ed_settings.AXES_COLOR);
        g.drawRect(x0_cscale, y0, COLOUR_BAR_WIDTH, plot_height);
        
        //label the colour scale
        int tic_label_x = x0_cscale + COLOUR_BAR_WIDTH + PADDING_INTERTEXT;
        for(int i = 0; i <= COLOUR_BAR_TICS; i++){
            double fraction = ((double)i)/((double)COLOUR_BAR_TICS - 1.0);
            double ticval = fraction*plotmax;
            String ticlabel = String.format("%1.2e", ticval);
            int tic_label_y = y0 + plot_height - (int)(((double)(i * plot_height)) / ((double)COLOUR_BAR_TICS));
            g.setColor(ed_settings.TEXT_COLOR);
            g.drawString(ticlabel, tic_label_x + PADDING_INTERTEXT, tic_label_y + (TEXT_HEIGHT)/3);
            g.setColor(ed_settings.AXES_COLOR);
            g.fillRect(tic_label_x - PADDING_INTERTEXT, tic_label_y, PADDING_INTERTEXT, 1);
        }
        
        
        return plot;
    }
    
    /**
     * Sets the noise filter to be used in the Canny edge detector and calls {@link #updateFilteredData()}.
     * {@link #updateGraphics()} will also be called if {@link #display_mode} is set to anything other than {@link EdgeDetectionSettings#DISPLAY_MODE_PRE_FILTER}.
     * @param f The filter to be used.
     * @see EdgeDetectionSettings#FILTER_OPTIONS
     */
    public void setFilter(Filter f){
        filter = f;
        updateFilteredData();
        if(display_mode != EdgeDetectionSettings.DISPLAY_MODE_PRE_FILTER){
            updateGraphics();
        }
    }
    
    /**
     * Sets the lower threshold to be used in the Canny edge detector.
     * If {@link #display_mode} is set to {@link EdgeDetectionSettings#DISPLAY_MODE_THRESHOLDED_EDGES}, {@link #updateGraphics()} will be called.
     * @param l_threshold the new lower threshold.
     */
    public void setLowerThreshold(double l_threshold){
        lower_threshold = l_threshold;
        if(display_mode == EdgeDetectionSettings.DISPLAY_MODE_THRESHOLDED_EDGES){
            updateGraphics();
        }
    }
    
    /**
     * Sets {@link #unfiltered_data} to new data and updates {@link #pixel_width_over_height}. 
     * This will call {@link #updateFilteredData()} and then {@link #updateGraphics()}.
     * @param udata The new unfiltered data to be used.
     * @param pixel_width The width in real space represented by one data point in <code>udata</code>.
     * @param pixel_height The height in real space represented by one data point in <code>udata</code>.
     */
    public void setUnfilteredData(double[][] udata, double pixel_width, double pixel_height){
        pixel_width_over_height = pixel_width/pixel_height;
        unfiltered_data = udata;
        updateFilteredData();
        updateGraphics();
    }
    
    /**
     * Sets the upper threshold to be used in the Canny edge detector.
     * If {@link #display_mode} is set to {@link EdgeDetectionSettings#DISPLAY_MODE_THRESHOLDED_EDGES}, {@link #updateGraphics()} will be called.
     * @param u_threshold the new upper threshold.
     */
    public void setUpperThreshold(double u_threshold){
        upper_threshold = u_threshold;
        if(display_mode == EdgeDetectionSettings.DISPLAY_MODE_THRESHOLDED_EDGES){
            updateGraphics();
        }
    }
    
    /**
     * Applies {@link #filter} to {@link #unfiltered_data} and writes the result to {@link #filtered_data}.
     * Then calls {@link #updateEdges()}.
     * @return 
     */
    private int updateFilteredData(){
        filtered_data = filter.applyFilter(unfiltered_data);
        return updateEdges();
    }
    
    /**
     * 
     * @param edge_detector
     * @return 
     */
    private int updateEdges(){
        if(edge_detector_mode == EdgeDetectionSettings.EDGE_DETECTOR_CANNY){
            return computeEdgesCanny();
        }
        else if(edge_detector_mode == EdgeDetectionSettings.EDGE_DETECTOR_NONE){
            return computeEdgesNone();
        }
        return 1;
    }
    
    /**
     * Applies Sobel operators to the {@link #filtered_data filtered data} to find the direction and magnitude of its gradients.
     * Local maxima along gradient directions are then written to {@link #unthresholded_edges}.
     * The strength of the edges are binned into a histogram which is sent to the {@link #parent} of this component via {@link EdgeDetectionWindow#setHistogramData(double[], int[], double, double)}.
     * @return 0 if there are no errors, 1 otherwise.
     * @see EdgeDetectionWindow#setHistogramData(double[], int[], double, double) 
     * @see #roundedMax(double) 
     * @see SobelX
     * @see SobelY
     */
    private int computeEdgesCanny(){
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
    
    /**
     * Sends {@link #filtered_data filtered data} to {@link #unthresholded_edges}.
     * The data is binned into a histogram which is sent to the {@link #parent} of this component via {@link EdgeDetectionWindow#setHistogramData(double[], int[], double, double)}.
     * @return 0 if there are no errors, 1 otherwise.
     * @see EdgeDetectionWindow#setHistogramData(double[], int[], double, double) 
     */
    private int computeEdgesNone(){
        unthresholded_edges = new double[filtered_data.length][filtered_data[0].length];
        double max = filtered_data[0][0];
        double min = filtered_data[0][0];
        for(int x = 0; x < unthresholded_edges.length; x++){
            for(int y = 0; y < unthresholded_edges[0].length; y++){
                unthresholded_edges[x][y] = filtered_data[x][y];
                if(unthresholded_edges[x][y] > max){
                    max = unthresholded_edges[x][y];
                }
                if(unthresholded_edges[x][y] < min){
                    min = unthresholded_edges[x][y];
                }
            }
        }
        double unrounded_max = max;
        //Round the extreme values
        max = roundedMax(max);
        int numbins = 20;
        double binwidth = (max) / ((double)numbins);
        int[] histogram_counts = new int[numbins];
        for(int x = 0; x < unthresholded_edges.length; x++){
            for(int y = 0; y < unthresholded_edges[0].length; y++){
                if(unthresholded_edges[x][y] > 0){
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
        parent.setHistogramData(histogram_mags, histogram_counts, min, unrounded_max);
        return 0;
    }
    
    /**
     * Returns the thresholded edges computed by this component.
     * @return The thresholded edges.
     */
    public int[][] getEdges(){
        int w = unthresholded_edges.length;
        int h = unthresholded_edges[0].length;
        int[][] thresholded_edges = new int[w][h];
        for(int x = 0; x < w; x++){
            for(int y = 0; y < h; y++){
                if(unthresholded_edges[x][y] < lower_threshold){
                    thresholded_edges[x][y] = 0;
                }
                else if(unthresholded_edges[x][y] <= upper_threshold){
                    thresholded_edges[x][y] = 1;
                }
                else{
                    thresholded_edges[x][y] = 0;
                }
            }
        }
        return thresholded_edges;
    }
    
    /**
     * Rounds up to the nearest number with two significant figures
     * @param true_max The number to be rounded
     * @return The rounded number. 
     * If the given number already had exactly 2 significant figures, the next highest number with two significant figures will be returned.
     */
    private static double roundedMax(double true_max){
        //Determine the maximum value for the colour scale
        double logmax = Math.floor(Math.log10(true_max));
        double rounding = Math.pow(10, logmax - 1.0);
        double roundmax = (int)(true_max / rounding) + 1.0;
        return roundmax*rounding;
    }
    
    /**
     * The image that is painted to this component.
     * This is a {@link BufferedImage#TYPE_3BYTE_BGR} encoded image
     * @see #paint(java.awt.Graphics) 
     */
    private Image display_image;
    /**
     * The display mode for this component.
     * Detrmined which set of data is plotted.
     * @see #setDisplayMode(int) 
     */
    private int display_mode;
    /**
     * The edge detector mode for this component.
     * Determines how edge/feature detection is performed
     * @see #updateEdges() 
     * 
     */
    private int edge_detector_mode;
    /**
     * The filtered data
     */
    private double[][] filtered_data;
    /**
     * The noise filter used in the Canny Edge Detection
     */
    private Filter filter;
    /**
     * The lower threshold for the detected edges
     */
    private double lower_threshold;
    /**
     * The parent of this component
     */
    private EdgeDetectionWindow parent;
    /**
     * The ratio of the width to the height of a data point in real space.
     * @see #filtered_data
     * @see #unfiltered_data
     * @see #unthresholded_edges
     */
    private double pixel_width_over_height;
    /**
     * The unfiltered data whose edges are to be detected
     */
    private double[][] unfiltered_data;
    /**
     * The detected edges before any thresholding has taken place
     */
    private double[][] unthresholded_edges;
    /**
     * The upper threshold for the detected edges
     */
    private double upper_threshold;
    /**
     * The settings that are shared between this component, its {@link EdgeDetectionWindow parent} as well as the {@link EdgeDetectionWindow#edge_histogram parent's edge histogram}.
     */
    private EdgeDetectionSettings ed_settings;
    
    /**
     * Minimum space between the heat map and the top of this component.
     * @see #plotImage(double[][], double, int, int) 
     */
    private static final int PADDING_N = 10;
    /**
     * Minimum space between the heat map's colour scale tic labels and the right of this component.
     * @see #plotImage(double[][], double, int, int) 
     */
    private static final int PADDING_E = 10;
    /**
     * Minimum space between the heat map and the bottom of this component.
     * @see #plotImage(double[][], double, int, int) 
     */
    private static final int PADDING_S = 10;
    /**
     * Minimum space between the heat map and the left of this component.
     * @see #plotImage(double[][], double, int, int) 
     */
    private static final int PADDING_W = 10;
    /**
     * Minimum space between the heat map, colour bar and colour bar tic labels.
     * @see #plotImage(double[][], double, int, int) 
     */
    private static final int PADDING_INTERTEXT = 5;
    /**
     * The number of tics for the heatmap colourbar.
     * @see #plotImage(double[][], double, int, int)
     */
    private static final int COLOUR_BAR_TICS = 6;
    /**
     * The width of the colour bar
     * @see #plotImage(double[][], double, int, int)
     */
    private static final int COLOUR_BAR_WIDTH = 15;
    /**
     * Will replace NaN values with zero when plotting an image in {@link #plotImage(double[][], double, int, int) }
     */
    private static final int NAN_BEHAVIOUR_ZERO = 0;
}
