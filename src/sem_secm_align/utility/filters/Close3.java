/*
 * Created: 2023-04-06
 * Updated: 2023-04-06
 * Nathaniel Leslie
 */
package sem_secm_align.utility.filters;

/**
 * Morphological closure using a 3x3 square of ones as the dilater/eroder
 * This can fill in 2x2 holes or 2-wide gaps between lines.
 * @author Nathaniel
 */
public class Close3 implements BinaryFilter{

    @Override
    public int[][] applyFilter(int[][] input_grid){
        int[][] mask = new int[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};
        int[][] dilated = DilationErosion.dilateGrid(input_grid, mask);
        return DilationErosion.erodeGrid(dilated, mask);
    }

    @Override
    public String getName(){
        return "3x3 Morphological Close";
    }
    
}
