/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
