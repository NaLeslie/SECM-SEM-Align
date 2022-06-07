/*
 * Created: 2022-04-12
 * Updated: 2022-04-12
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
    public static final int CSCALE_GRAY = 0;
}
