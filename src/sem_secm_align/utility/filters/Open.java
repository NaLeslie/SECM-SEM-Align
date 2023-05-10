/*
 * Created: 2023-04-06
 * Updated: 2023-05-10
 * Nathaniel Leslie
 */
package sem_secm_align.utility.filters;

/**
 * Morphological opening using a 3x3 square of ones as the dilater/eroder
 * This will remove 2 thick lines and blobs.
 * @author Nathaniel
 */
public class Open implements BinaryFilter{

    public Open(int size){
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
        int[][] eroded = DilationErosion.erodeGrid(input_grid, mask);
        return DilationErosion.dilateGrid(eroded, mask);
    }

    @Override
    public String getName(){
        return size + "x" + size + " Morphological Open";
    }
    
    final int size;
    
}
