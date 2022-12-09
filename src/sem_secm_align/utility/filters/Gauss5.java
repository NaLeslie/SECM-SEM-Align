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
public class Gauss5 implements Filter{
    
    /**
     * <p>Locally averages the grid with a 5x5 Gaussian.
     * The Gaussian is normalized and goes out to 3sigma at each edge
     * <p>Convolves the input grid with:
     * <p>     0.001202288	0.00828597	0.015697476	0.00828597	0.001202288
     * <p>     0.00828597	0.057105662	0.108184579	0.057105662	0.00828597
     * <p>     0.015697476	0.108184579	0.204952218	0.108184579	0.015697476
     * <p>     0.00828597	0.057105662	0.108184579	0.057105662	0.00828597
     * <p>     0.001202288	0.00828597	0.015697476	0.00828597	0.001202288
     * @param input_grid The grid of pixels to be filtered
     * @return the input grid convolved with the x-sobel operator
     */
    @Override
    public double[][] applyFilter(double[][] input_grid){
        double[][] kernel = new double[][]{
            {0.001202288, 0.00828597, 0.015697476, 0.00828597, 0.001202288}, 
            {0.00828597, 0.057105662, 0.108184579, 0.057105662, 0.00828597},
            {0.015697476, 0.108184579, 0.204952218, 0.108184579, 0.015697476},
            {0.00828597, 0.057105662, 0.108184579, 0.057105662, 0.00828597},
            {0.001202288, 0.00828597, 0.015697476, 0.00828597, 0.001202288}};
        return convolve2D(input_grid, kernel);
    }
    
    @Override
    public String getName(){
        return "Gauss 5x5";
    }
}
