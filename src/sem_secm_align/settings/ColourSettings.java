/*
 * Created: 2022-04-12
 * Updated: 2022-06-15
 * Nathaniel Leslie
 */
package sem_secm_align.settings;

import java.awt.Color;

/**
 *
 * @author Nathaniel
 */
public class ColourSettings {
    
    public static Color colorScale(double input, int mode){
        if(mode == CSCALE_GRAY){
            int v = (int)(255.0*input);
            return new Color(v,v,v);
        }
        else{
            return BACKGROUND_COLOUR;
        }
    }
    
    public static final Color BACKGROUND_COLOUR = new Color(0,0,0);
    public static final Color ACTIVE_COLOUR = new Color(255,0,0);
    public static final Color INACTIVE_COLOUR = new Color(0,0,0);
    public static final Color GRID_COLOUR = new Color(0,0,255);
    public static final Color SELECTION_COLOUR = new Color(255,255,0);
    public static final int CSCALE_GRAY = 0;
}
