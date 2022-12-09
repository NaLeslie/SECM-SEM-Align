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
public class Gauss7 implements Filter{
    
    /**
     * <p>Locally averages the grid with a 3x3 Gaussian.
     * The Gaussian is normalized and goes out to 3sigma at each edge
     * <p>Convolves the input grid with:
     * <p>      0.000218	0.001231	0.003474	0.004908	0.003474	0.001231	0.000218
     * <p>      0.001231	0.006961	0.019647	0.027756	0.019647	0.006961	0.001231
     * <p>      0.003474	0.019647	0.055452	0.078336	0.055452	0.019647	0.003474
     * <p>      0.004908	0.027756	0.078336	0.110665	0.078336	0.027756	0.004908
     * <p>      0.003474	0.019647	0.055452	0.078336	0.055452	0.019647	0.003474
     * <p>      0.001231	0.006961	0.019647	0.027756	0.019647	0.006961	0.001231
     * <p>      0.000218	0.001231	0.003474	0.004908	0.003474	0.001231	0.000218
     * 
     * @param input_grid The grid of pixels to be filtered
     * @return the input grid convolved with the x-sobel operator
     */
    @Override
    public double[][] applyFilter(double[][] input_grid){
        double[][] kernel = new double[][]{
            {0.000218, 0.001231, 0.003474, 0.004908, 0.003474, 0.001231, 0.000218}, 
            {0.001231, 0.006961, 0.019647, 0.027756, 0.019647, 0.006961, 0.001231},
            {0.003474, 0.019647, 0.055452, 0.078336, 0.055452, 0.019647, 0.003474},
            {0.004908, 0.027756, 0.078336, 0.110665, 0.078336, 0.027756, 0.004908},
            {0.003474, 0.019647, 0.055452, 0.078336, 0.055452, 0.019647, 0.003474},
            {0.001231, 0.006961, 0.019647, 0.027756, 0.019647, 0.006961, 0.001231},
            {0.000218, 0.001231, 0.003474, 0.004908, 0.003474, 0.001231, 0.000218}};
        return convolve2D(input_grid, kernel);
    }
    
    @Override
    public String getName(){
        return "Gauss 7x7";
    }
}
