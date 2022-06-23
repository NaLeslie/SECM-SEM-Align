/*
 * Created: 2022-04-12
 * Updated: 2022-06-23
 * Nathaniel Leslie
 */
package sem_secm_align.settings;

import java.awt.Color;

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
     * <li><code>CSCALE_GREY</code>: Greyscale where <code>0</code> is black and <code>1</code> is white.</li>
     * </ul>
     * @return The scaled colour or <code>BACKGROUND_COLOUR</code> if an invalid mode is selected.
     */
    public static Color colorScale(double input, int mode){
        if(mode == CSCALE_GREY){
            int v = (int)(255.0*input);
            return new Color(v,v,v);
        }
        else{
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
     * The greyscale <code>colorScale()</code> mode. <code>0</code> is black and <code>1</code> is white.
     */
    public static final int CSCALE_GREY = 0;
}
