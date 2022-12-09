/*
 * Created: 2022-12-01
 * Updated: 2022-12-01
 * Nathaniel Leslie
 */
package sem_secm_align.utility.filters;

/**
 *
 * @author Nathaniel
 */
public interface Filter {
    double[][] applyFilter(double[][] input_grid);
    String getName();
}
