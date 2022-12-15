/*
 * Created: 2022-12-15
 * Updated: 2022-12-15
 * Nathaniel Leslie
 */
package sem_secm_align.edge_detection;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import sem_secm_align.data_types.ImproperFileFormattingException;
import sem_secm_align.utility.filters.Filter;
import sem_secm_align.utility.filters.SobelX;
import sem_secm_align.utility.filters.SobelY;

/**
 *
 * @author Nathaniel
 */
public class EdgeVisualizer extends JPanel{
    
    public EdgeVisualizer(){
        
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
    }
    
    public void updateGraphics(){
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
        plotImage();
    }
    
    private void drawPreFilter(){
        
    }
    
    private void drawPostFilter(){
        
    }
    
    private void drawEdges(){
        
    }
    
    private void drawThresholdedEdges(){
        
    }
    
    private void plotImage(){
        
    }
    
    public void setFilter(Filter f){
        filter = f;
        updateFilteredData();
    }
    
    public void setLowerThreshold(double l_threshold){
        lower_threshold = l_threshold;
    }
    
    public void setUnfilteredData(double[][] udata){
        unfiltered_data = udata;
        updateFilteredData();
    }
    
    public void setUpperThreshold(double u_threshold){
        upper_threshold = u_threshold;
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
        double min = Math.sqrt(squares[0][0]);
        double max = 0.0;
        for(int x = 0; x < edges.length; x++){
            for(int y = 0; y < edges[0].length; y++){
                if(edges[x][y] > 0){
                    unthresholded_edges[x][y] = Math.sqrt(squares[x][y]);
                    if(unthresholded_edges[x][y] > max){
                        max = unthresholded_edges[x][y];
                    }
                    if(unthresholded_edges[x][y] < min){
                        min = unthresholded_edges[x][y];
                    }
                }
                else{
                    unthresholded_edges[x][y] = 0.0;
                }
            }
        }
        int numbins = 20;
        double binwidth = (max - min) / ((double)numbins);
        histogram_counts = new int[numbins];
        for(int x = 0; x < edges.length; x++){
            for(int y = 0; y < edges[0].length; y++){
                if(edges[x][y] > 0){
                    if(unthresholded_edges[x][y] < max){
                        int index = (int)((unthresholded_edges[x][y] - min) / binwidth);
                        histogram_counts[index] ++;
                    }
                    else{
                        histogram_counts[numbins - 1] ++;
                    }
                }
            }
        }
        histogram_mags = new double[numbins];
        for(int i = 0; i < numbins; i++){
            histogram_mags[i] = binwidth * ((double)i) + min;
        }
        return 0;
    }
    
    private double[][] filtered_data;
    private Filter filter;
    private int[] histogram_counts;
    private double[] histogram_mags;
    private double lower_threshold;
    private double[][] unfiltered_data;
    private double[][] unthresholded_edges;
    private double upper_threshold;
    
    private Image display_image;
    
    private int display_mode;
    
    public static final int DISPLAY_MODE_PRE_FILTER = 0;
    public static final int DISPLAY_MODE_POST_FILTER = 1;
    public static final int DISPLAY_MODE_EDGE_MAGNITUDES = 2;
    public static final int DISPLAY_MODE_THRESHOLDED_EDGES = 3;
}
