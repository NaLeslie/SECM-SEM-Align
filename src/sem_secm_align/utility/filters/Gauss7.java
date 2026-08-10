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

import static sem_secm_align.utility.filters.Convolution.convolve2D;

/**
 * A 7x7 gaussian filter.
 * The Gaussian is normalized and goes out to 3sigma at each edge
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
    
    /**
     * Returns <code>"Gauss 7x7"</code>.
     * @return <code>"Gauss 7x7"</code>
     */
    @Override
    public String getName(){
        return "Gauss 7x7";
    }
}
