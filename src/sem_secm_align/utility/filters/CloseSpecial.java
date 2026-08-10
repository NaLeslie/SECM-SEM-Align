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
 * Morphological closure using a 7x7 square of ones as the dilater/eroder
 * This closure will only apply to squares within a 7x7 of a line segment end
 * @author Nathaniel
 */
public class CloseSpecial implements BinaryFilter{

    public CloseSpecial(int size){
        this.size = size;
    }
    
    @Override
    public int[][] applyFilter(int[][] input_grid){
        BinaryFilter lef = new LineEndFilter();
        int[][] ends = lef.applyFilter(input_grid);
        int[][] mask = new int[size][size];
        for(int i = 0; i < size; i++){
            for(int ii = 0; ii < size; ii++){
                mask[i][ii] = 1;
            }
        }
        int[][] dilated = DilationErosion.dilateGrid(input_grid, mask);
        int[][] closed = DilationErosion.erodeGrid(dilated, mask);
        int[][] dilated_ends = DilationErosion.dilateGrid(ends, mask);
        int[][] output = new int[input_grid.length][input_grid[0].length];
        for(int x = 0; x < input_grid.length; x++){
            for(int y = 0; y < input_grid[0].length; y++){
                output[x][y] = dilated_ends[x][y] * closed[x][y] - dilated_ends[x][y] * input_grid[x][y] + input_grid[x][y];
            }
        }
        return DilationErosion.erodeGrid(dilated, mask);
    }

    @Override
    public String getName(){
        return size + "x" + size + " Morphological Close (special)";
    }
    
    final int size;
    
}
