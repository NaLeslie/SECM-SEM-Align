/*
 * Created: 2020-05-07
 * Updated: 2020-09-11
 * Nathaniel Leslie
 */
package sem_secm_align.settings;

/**
 *
 * @author Nathaniel
 */
public class Constants {
    /**
     * The relative error between two values that will be interpreted as noise introduced by floating point imprecision.
     */
    public static final double RELATIVE_ERR_CUTOFF = 0.0001;
    /**
     * The complement of the relative error between two values that will be interpreted as noise introduced by floating point imprecision.
     */
    public static final double COMP_RELATIVE_ERR_CUTOFF = 1 - RELATIVE_ERR_CUTOFF;
}
