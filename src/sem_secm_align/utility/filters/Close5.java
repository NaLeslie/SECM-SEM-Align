/*
 * Created: 2023-04-06
 * Updated: 2023-04-06
 * Nathaniel Leslie
 */
package sem_secm_align.utility.filters;

/**
 * Morphological closure using a 5x5 square of ones as the dilater/eroder
 * This can fill in 4x4 holes or 4-wide gaps between lines.
 * @author Nathaniel
 */
public class Close5 implements BinaryFilter{

    @Override
    public int[][] applyFilter(int[][] input_grid){
        int[][] mask = new int[][]{{1, 1, 1, 1, 1}, 
                                   {1, 1, 1, 1, 1}, 
                                   {1, 1, 1, 1, 1}, 
                                   {1, 1, 1, 1, 1}, 
                                   {1, 1, 1, 1, 1}};
        int[][] dilated = DilationErosion.dilateGrid(input_grid, mask);
        return DilationErosion.erodeGrid(dilated, mask);
    }

    @Override
    public String getName(){
        return "5x5 Morphological Close";
    }
    
}

