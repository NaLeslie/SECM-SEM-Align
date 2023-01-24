/*
 * Created: 2022-12-01
 * Updated: 2022-12-01
 * Nathaniel Leslie
 */
package sem_secm_align.utility.filters;

/**
 * Interface for a filter that can be applied to an image with double[][] signals.
 * @author Nathaniel
 */
public interface Filter {
    /**
     * The method that applies the filter to a given <code>input_grid</code>.
     * @param input_grid the data to be filtered.
     * @return The filtered data.
     */
    double[][] applyFilter(double[][] input_grid);
    /**
     * Method for obtaining the name of the filter
     * @return the filter's name
     */
    String getName();
}
