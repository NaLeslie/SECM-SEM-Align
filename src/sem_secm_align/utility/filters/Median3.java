/*
 * The MIT License
 *
 * Copyright 2022 Nathaniel LESLIE.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package sem_secm_align.utility.filters;

import java.util.Arrays;
import static sem_secm_align.utility.filters.Convolution.extrapolatePad;

/**
 * A 3x3 median filter. This type of filter is most successful against "salt and pepper" or "shot" noise.
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
    
    /**
     * Returns <code>"Median 3x3"</code>.
     * @return <code>"Median 3x3"</code>
     */
    @Override
    public String getName(){
        return "Median 3x3";
    }
}
