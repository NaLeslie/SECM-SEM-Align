/*
 * The MIT License
 *
 * Copyright 2023 Nathaniel LESLIE.
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

/**
 * Handles methods for dilating or eroding a grid of integers (0 or 1) by given patterns.
 * @author Nathaniel
 */
public class DilationErosion {
    
    /**
     * 
     * @param input_grid The input grid to be dilated.
     * @param dilation_mask a grid of 1s or 0s where '1' indicates that a pixel should be considered as an active part of the dilation mask. This should have odd-lengthed dimensions, since it must have a centre.
     * @return The dilated grid.
     */
    public static int[][] dilateGrid(int[][] input_grid, int[][] dilation_mask){
        int xmid = dilation_mask.length/2;//The middle x-index of the dilation mask
        int ymid = dilation_mask.length/2;//The middle y-index of the dilation mask
        int[][] output_grid = new int[input_grid.length][input_grid[0].length];
        for(int x = 0; x < input_grid.length; x++){
            for(int y = 0; y < input_grid[0].length; y++){
                boolean nochange = true;
                if(input_grid[x][y] == 0){
                    for(int xm = 0; xm < dilation_mask.length && nochange; xm++){
                        for(int ym = 0; ym < dilation_mask[0].length && nochange; ym++){
                            int prod = zeroPad(input_grid, x + xm - xmid, y + ym - ymid) * dilation_mask[xm][ym];//will be 1 if the pixel is active in the dilation mask and is active in the input
                            nochange = prod == 0;
                        }
                    }
                }
                if(nochange){
                    output_grid[x][y] = input_grid[x][y];
                }
                else{
                    output_grid[x][y] = 1;
                }
            }
        }
        return output_grid;
    }
    
    /**
     * 
     * @param input_grid The input grid to be eroded.
     * @param erosion_mask a grid of 1s or 0s where '1' indicates that a pixel should be considered as an active part of the erosion mask. This should have odd-lengthed dimensions, since it must have a centre.
     * @return The eroded grid.
     */
    public static int[][] erodeGrid(int[][] input_grid, int[][] erosion_mask){
        int xmid = erosion_mask.length/2;//The middle x-index of the dilation mask
        int ymid = erosion_mask.length/2;//The middle y-index of the dilation mask
        int[][] output_grid = new int[input_grid.length][input_grid[0].length];
        for(int x = 0; x < input_grid.length; x++){
            for(int y = 0; y < input_grid[0].length; y++){
                boolean nochange = true;
                if(input_grid[x][y] == 1){
                    for(int xm = 0; xm < erosion_mask.length && nochange; xm++){
                        for(int ym = 0; ym < erosion_mask[0].length && nochange; ym++){
                            int prod = (1 - zeroPad(input_grid, x + xm - xmid, y + ym - ymid)) * erosion_mask[xm][ym];//will be 1 if the pixel is active in the dilation mask and is inactive in the input
                            nochange = prod == 0;
                        }
                    }
                }
                if(nochange){
                    output_grid[x][y] = input_grid[x][y];
                }
                else{
                    output_grid[x][y] = 0;
                }
            }
        }
        return output_grid;
    }
    
    /**
     * Simulates matrix data with an infinitely thick padding of 0s outside the bounds of the matrix.
     * @param data The data matrix
     * @param x The x address
     * @param y The y address
     * @return If x,y are in range of data[][], data[x][y] is returned. Otherwise, 0.0f is returned.
     */
    public static int zeroPad(int[][] data, int x, int y){
        if(x >=0 && y >=0 && x < data.length && y < data[0].length){
            return data[x][y]; //return data if x and y have a "legal" value
        }
        else{
            return 0;
        }
    }
    
}
