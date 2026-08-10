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
 * A 3x3 gaussian filter.
 * The Gaussian is normalized and goes out to 3sigma at each edge
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
    
    /**
     * Returns <code>"Gauss 3x3"</code>.
     * @return <code>"Gauss 3x3"</code>
     */
    @Override
    public String getName(){
        return "Gauss 3x3";
    }
}
