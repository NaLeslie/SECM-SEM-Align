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

/**
 * Identifies where the ends of lines occur (i.e. pixels where there is less than 2 immediately neighboring pixels
 * @author Nathaniel
 */
public class LineEndFilter implements BinaryFilter{

    @Override
    public int[][] applyFilter(int[][] input_grid){
        int[][] ends = new int[input_grid.length][input_grid[0].length];
        for(int x = 0; x < input_grid.length; x++){
            for(int y = 0; y < input_grid[0].length; y++){
                if(input_grid[x][y] == 1){
                    int sum = 0;
                    for(int u = -1; u < 2; u++){
                        for(int v = -1; v < 2; v++){
                            sum += DilationErosion.zeroPad(input_grid, x + u, y + v);
                        }
                    }
                    if(sum > 1){
                        ends[x][y] = 1;
                    }
                    else{
                        ends[x][y] = 0;
                    }
                }
                else{
                    ends[x][y] = 0;
                }
            }
        }
        return ends;
    }

    @Override
    public String getName(){
        return "Line end filter";
    }
    
}
