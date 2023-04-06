/*
 * Created: 2023-04-06
 * Updated: 2023-04-06
 * Nathaniel Leslie
 */
package sem_secm_align.utility.filters;

/**
 *
 * @author Nathaniel
 */
public interface BinaryFilter {
    /**
     * The method that applies the binary filter to a given <code>input_grid</code>.
     * @param input_grid the data to be filtered.
     * @return The filtered data.
     */
    int[][] applyFilter(int[][] input_grid);
    /**
     * Method for obtaining the name of the filter
     * @return the filter's name
     */
    String getName();
}
