/*
 * Created: 2022-12-01
 * Updated: 2022-12-06
 * Nathaniel Leslie
 */
package sem_secm_align.utility.filters;

import static sem_secm_align.utility.filters.Convolution.convolve2D;

/**
 *
 * @author Nathaniel
 */
public class Gauss3 implements Filter{
    
    /**
     * <p>Locally averages the grid with a 3x3 Gaussian.
     * The Gaussian is normalized and goes out to 3sigma at each edge
     * <p>Convolves the input grid with:
     * <p>     0.024879098	0.107973018	0.024879098
     * <p>     0.107973018	0.468591532	0.107973018
     * <p>     0.024879098	0.107973018	0.024879098
     * @param input_grid The grid of pixels to be filtered
     * @return the input grid convolved with the x-sobel operator
     */
    @Override
    public double[][] applyFilter(double[][] input_grid){
        double[][] kernel = new double[][]{
            {0.024879098, 0.107973018, 0.024879098}, 
            {0.107973018, 0.468591532, 0.107973018}, 
            {0.024879098, 0.107973018, 0.024879098}};
        return convolve2D(input_grid, kernel);
    }
    
    @Override
    public String getName(){
        return "Gauss 3x3";
    }
}
