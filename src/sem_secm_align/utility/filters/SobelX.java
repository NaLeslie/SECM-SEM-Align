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
public class SobelX implements Filter{
    
    /**
     * <p>Computes the change in pixel value with respect to the x-coordinate.
     * Positive when higher x-coordinates are more positive.
     * <p>Convolves the input grid with:
     * <p>     1  0 -1
     * <p>     2  0 -2
     * <p>     1  0 -1
     * @param input_grid The grid of pixels to be filtered
     * @return the input grid convolved with the x-sobel operator
     */
    @Override
    public double[][] applyFilter(double[][] input_grid){
        double[][] kernel = new double[][]{{1,2,1}, {0,0,0}, {-1,-2,-1}};
        return convolve2D(input_grid, kernel);
    }
    
    @Override
    public String getName(){
        return "Sobel_x";
    }
}
