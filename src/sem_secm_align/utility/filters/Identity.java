/*
 * Created: 2022-12-01
 * Updated: 2022-12-06
 * Nathaniel Leslie
 */
package sem_secm_align.utility.filters;

/**
 * This is the identity filter (does not have any effect on the data)
 * @author Nathaniel
 */
public class Identity implements Filter{

    /**
     * Does not affect the input in any way.
     * @param input_grid The input data.
     * @return The input data.
     */
    @Override
    public double[][] applyFilter(double[][] input_grid) {
        return input_grid;
    }

    /**
     * Returns <code>"Identity"</code>.
     * @return <code>"Identity"</code>
     */
    @Override
    public String getName() {
        return "Identity";
    }
    
}
