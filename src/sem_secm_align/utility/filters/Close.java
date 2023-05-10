/*
 * Created: 2023-04-06
 * Updated: 2023-05-10
 * Nathaniel Leslie
 */
package sem_secm_align.utility.filters;

/**
 * Morphological closure using a nxn square of ones as the dilater/eroder
 * This can fill in (n-1)x(n-1) holes or ((n-1))-wide gaps between lines.
 * @author Nathaniel
 */
public class Close implements BinaryFilter{

    public Close(int size){
        this.size = size;
    }
    
    @Override
    public int[][] applyFilter(int[][] input_grid){
        int[][] mask = new int[size][size];
        for(int i = 0; i < size; i++){
            for(int ii = 0; ii < size; ii++){
                mask[i][ii] = 1;
            }
        }
        int[][] dilated = DilationErosion.dilateGrid(input_grid, mask);
        return DilationErosion.erodeGrid(dilated, mask);
    }

    @Override
    public String getName(){
        return size + "x" + size + " Morphological Close";
    }
    
    final int size;
    
}
