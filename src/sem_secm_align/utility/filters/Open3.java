/*
 * Created: 2023-04-06
 * Updated: 2023-04-06
 * Nathaniel Leslie
 */
package sem_secm_align.utility.filters;

/**
 * Morphological opening using a 3x3 square of ones as the dilater/eroder
 * This will remove 2 thick lines and blobs.
 * @author Nathaniel
 */
public class Open3 implements BinaryFilter{

    @Override
    public int[][] applyFilter(int[][] input_grid){
        int[][] mask = new int[][]{{1, 1, 1}, 
                                   {1, 1, 1}, 
                                   {1, 1, 1}};
        int[][] eroded = DilationErosion.erodeGrid(input_grid, mask);
        return DilationErosion.dilateGrid(eroded, mask);
    }

    @Override
    public String getName(){
        return "3x3 Morphological Open";
    }
    
}