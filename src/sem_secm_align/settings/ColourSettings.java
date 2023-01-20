/*
 * Created: 2022-04-12
 * Updated: 2023-01-13
 * Nathaniel Leslie
 */
package sem_secm_align.settings;

import java.awt.Color;
import static sem_secm_align.settings.Constants.RELATIVE_ERR_CUTOFF;

/**
 * Holds colours that are used by the visualizer in a centralized place.
 * @author Nathaniel
 */
public class ColourSettings {
    
    /**
     * Returns a colour according to <code>input</code> and a colour scale.
     * @param input The input data. Must be a value between <code>0</code> and <code>1</code>.
     * @param mode Selector for the colour scale to be used. Options are:
     * <ul>
     * <li>{@link #CSCALE_GREY}: Greyscale where <code>0</code> is black and <code>1</code> is white.</li>
     * <li>{@link #CSCALE_GREY_MAGENTAZERO}: Greyscale where less than {@link sem_secm_align.settings.Constants#RELATIVE_ERR_CUTOFF} is magenta, otherwise the colour scales from black at just greater than {@link sem_secm_align.settings.Constants#RELATIVE_ERR_CUTOFF} to white at <code>1</code>.</li>
     * <li>{@link #CSCALE_RED_BLACKZERO}: Redscale where less than {@link sem_secm_align.settings.Constants#RELATIVE_ERR_CUTOFF} is black, otherwise the colour scales from red at just greater than {@link sem_secm_align.settings.Constants#RELATIVE_ERR_CUTOFF} to white at <code>1</code>.</li>
     * </ul>
     * @return The scaled colour or {@link #BACKGROUND_COLOUR} if an invalid mode is selected.
     */
    public static Color colourScale(double input, int mode){
        int v;
        switch(mode){          
            case CSCALE_GREY:
                v = (int)(255.0*input);
                return new Color(v,v,v);
            case CSCALE_GREY_MAGENTAZERO:
                v = (int)(255.0*input);
                if(input < RELATIVE_ERR_CUTOFF){
                    return new Color(255,0,255);
                }
                return new Color(v,v,v);
            case CSCALE_RED_BLACKZERO:
                v = (int)(255.0*input);
                if(input < RELATIVE_ERR_CUTOFF){
                    return new Color(0,0,0);
                }
                return new Color(255,v,v);
            default:
                return BACKGROUND_COLOUR;
        }
    }
    
    /**
     * The background colour for the visualizer where no data is present. Black.
     */
    public static final Color BACKGROUND_COLOUR = new Color(0,0,0);
    
    /**
     * The colour of active grid-sections in the reactivity and sampling windows. Red.
     */
    public static final Color ACTIVE_COLOUR = new Color(255,0,0);
    
    /**
     * The colour of grid-sections that will be sampled when instruction files are to be generated. Orange.
     */
    public static final Color SAMPLE_COLOUR = new Color(255,127,0);
    
    /**
     * The colour of the grid in the reactivity and sampling windows. Blue.
     */
    public static final Color GRID_COLOUR = new Color(0,0,255);
    
    /**
     * The colour of the cropping box in the reactivity box. Yellow.
     */
    public static final Color SELECTION_COLOUR = new Color(255,255,0);
    
    /**
     * A greyscale {@link #colourScale(double, int) } mode. <code>0</code> is black and <code>1</code> is white.
     */
    public static final int CSCALE_GREY = 0;
    
    /**
     * A greyscale {@link #colourScale(double, int) } mode. For inputs greater than {@link sem_secm_align.settings.Constants#RELATIVE_ERR_CUTOFF RELATIVE_ERR_CUTOFF} the colour scales as <code>0</code> is black and <code>1</code> is white.
     * Otherwise, {@link #colourScale(double, int) } will output magenta.
     */
    public static final int CSCALE_GREY_MAGENTAZERO = 1;
    
    /**
     * The redscale {@link #colourScale(double, int) } mode. For inputs greater than {@link sem_secm_align.settings.Constants#RELATIVE_ERR_CUTOFF RELATIVE_ERR_CUTOFF} the colour scales as <code>0</code> is red and <code>1</code> is white.
     * Otherwise, {@link #colourScale(double, int) } will output magenta.
     */
    public static final int CSCALE_RED_BLACKZERO = 2;
}
