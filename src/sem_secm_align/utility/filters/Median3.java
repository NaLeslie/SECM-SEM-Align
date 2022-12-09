/*
 * Created: 2022-12-01
 * Updated: 2022-12-06
 * Nathaniel Leslie
 */
package sem_secm_align.utility.filters;

import java.util.Arrays;
import static sem_secm_align.utility.filters.Convolution.extrapolatePad;

/**
 *
 * @author Nathaniel
 */
public class Median3 implements Filter{
    
    /**
     * Replaces each data point in the input grid with the median of the point and its 8 nearest neighbor points.
     * @param input_grid the data to be filtered
     * @return the filtered data
     */
    @Override
    public double[][] applyFilter(double[][] input_grid){
        double[][] filtered_grid = new double[input_grid.length][input_grid[0].length];
        for(int x = 0; x < input_grid.length; x++){
            for(int y = 0; y < input_grid[0].length; y++){
                double[] points = new double[9];
                for(int u = 0; u < 3; u++){
                    for(int v = 0; v < 3; v++){
                        points[u*3 + v] = extrapolatePad(input_grid, x + u - 1, y + v - 1);
                    }
                }
                Arrays.sort(points);
                filtered_grid[x][y] = points[4];
            }
        }
        return filtered_grid;
    }
    
    @Override
    public String getName(){
        return "Median 3x3";
    }
}
