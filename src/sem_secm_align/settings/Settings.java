/*
 * Created: 2022-03-30
 * Updated: 2022-06-15
 * Nathaniel Leslie
 */
package sem_secm_align.settings;

import sem_secm_align.data_types.Unit;

/**
 * Holds settings information for the program
 * @author Nathaniel
 */
public class Settings {
    
    /**
     * Gives default values to the settings.
     */
    public Settings(){
        UNITS_DISTANCE = new Unit[]{new Unit("nm", 1e-9), new Unit("um", 1e-6), new Unit("mm", 1e-3), new Unit("m", 1.0)};
        UNITS_CURRENT = new Unit[]{new Unit("pA", 1e-12), new Unit("nA", 1e-9), new Unit("uA", 1e-6), new Unit("mA", 1e-3), new Unit("A", 1.0)};
        UNITS_RESOLUTION = new Unit[]{new Unit("pixels/nm", 1e9), new Unit("pixels/um", 1e6), new Unit("pixels/mm", 1e3)};
        DEFAULT_CURRENT_UNIT_SELECTION = 1;
        DEFAULT_DISTANCE_UNIT_SELECTION = 1;
        DEFAULT_RESOLUTION_UNIT_SELECTION = 1;
        DEFAULT_REAC_SELECTION_TRANSPARENCY = 0.5;
        DEFAULT_REAC_SEM_TRANSPARENCY = 0.5;
        DEFAULT_REAC_SHOW_GRID = true;
        DEFAULT_REAC_XRESOLUTION = 5e-6;
        DEFAULT_REAC_YRESOLUTION = 5e-6;
        DEFAULT_SAM_NUM_XSTEPS = 1;
        DEFAULT_SAM_NUM_YSTEPS = 1;
        DEFAULT_SAM_XSTART_INDEX = 0;
        DEFAULT_SAM_XSTEP = 1;
        DEFAULT_SAM_YSTART_INDEX = 0;
        DEFAULT_SAM_YSTEP = 1;
        DEFAULT_SEM_ROTATION = 0;
        DEFAULT_SEM_SCALE = 1e6;
        DEFAULT_SEM_Transparency = 0.5;
        DEFAULT_SEM_XMIRROR = false;
        DEFAULT_SEM_XOFFSET = 0;
        DEFAULT_SEM_YMIRROR = false;
        DEFAULT_SEM_YOFFSET = 0;
    }
    
    /**
     * Fetches the labels for the distance units. This is intended for populating combo boxes
     * @return The labels for the distance units as an array of Strings
     */
    public String[] getDistanceUnitLabels(){
        String[] labels = new String[UNITS_DISTANCE.length];
        for(int i = 0; i < labels.length; i++){
            labels[i] = UNITS_DISTANCE[i].getLabel();
        }
        return labels;
    }
    
    /**
     * Fetches the labels for the current units. This is intended for populating combo boxes
     * @return The labels for the current units as an array of Strings
     */
    public String[] getCurrentUnitLabels(){
        String[] labels = new String[UNITS_CURRENT.length];
        for(int i = 0; i < labels.length; i++){
            labels[i] = UNITS_CURRENT[i].getLabel();
        }
        return labels;
    }
    
    /**
     * Fetches the labels for the resolution units. This is intended for populating combo boxes
     * @return The labels for the resolution units as an array of Strings
     */
    public String[] getResolutionUnitLabels(){
        String[] labels = new String[UNITS_RESOLUTION.length];
        for(int i = 0; i < labels.length; i++){
            labels[i] = UNITS_RESOLUTION[i].getLabel();
        }
        return labels;
    }
    
    
    /**
     * The current units used by this program
     */
    public final Unit[] UNITS_CURRENT;
    
    /**
     * The distance units used by this program
     */
    public final Unit[] UNITS_DISTANCE;
    
    /**
     * The resolution units used by this program
     */
    public final Unit[] UNITS_RESOLUTION;

    /**
     * The default selection for units of current
     */
    public final int DEFAULT_CURRENT_UNIT_SELECTION;
    
    /**
     * The default selection for units of distance
     */
    public final int DEFAULT_DISTANCE_UNIT_SELECTION;
    
    /**
     * The default transparency of the reactivity screen's grid-section selection layer 
     */
    public final double DEFAULT_REAC_SELECTION_TRANSPARENCY;
    
    /**
     * The default transparency of the reactivity screen's SEM layer
     */
    public final double DEFAULT_REAC_SEM_TRANSPARENCY;
    
    /**
     * The default state of the reactivity screen's grid toggle
     */
    public final boolean DEFAULT_REAC_SHOW_GRID;
    
    /**
     * the default distance in x between adjacent grid-sections
     */
    public final double DEFAULT_REAC_XRESOLUTION;
    
    /**
     * the default distance in y between adjacent grid-sections
     */
    public final double DEFAULT_REAC_YRESOLUTION;
    
    /**
     * The default selection for units of resolution/scale
     */
    public final int DEFAULT_RESOLUTION_UNIT_SELECTION;
    
    /**
     * The default number of steps in x that will be sampled
     */
    public final int DEFAULT_SAM_NUM_XSTEPS;
    
    /**
     * The default number of steps in y that will be sampled
     */
    public final int DEFAULT_SAM_NUM_YSTEPS;
    
    /**
     * x-address of the first grid-section to be sampled
     */
    public final int DEFAULT_SAM_XSTART_INDEX;
    
    /**
     * The distance to the next sampled points along the x-direction in grid-sections
     */
    public final int DEFAULT_SAM_XSTEP;
    
    /**
     * y-address of the first grid-section to be sampled
     */
    public final int DEFAULT_SAM_YSTART_INDEX;
    
    /**
     * The distance to the next sampled points along the y-direction in grid-sections
     */
    public final int DEFAULT_SAM_YSTEP;
    
    /**
     * Initial rotation of the SEM image relative to the SECM image in degrees
     */
    public final double DEFAULT_SEM_ROTATION;
    
    /**
     * Initial scale of the SEM image in pixels per metre
     */
    public final double DEFAULT_SEM_SCALE;
    
    /**
     * Initial transparency of the SEM layer in the SEM tab
     */
    public final double DEFAULT_SEM_Transparency;
    
    /**
     * Whether or not the SEM image is to be mirrored in the x-direction initially
     */
    public final boolean DEFAULT_SEM_XMIRROR;
    
    /**
     * The initial translation of the SEM image relative to the SECM image in the x-direction in metres
     */
    public final double DEFAULT_SEM_XOFFSET;
    
    /**
     * Whether or not the SEM image is to be mirrored in the x-direction initially
     */
    public final boolean DEFAULT_SEM_YMIRROR;
    
    /**
     * The initial translation of the SEM image relative to the SECM image in the y-direction in metres
     */
    public final double DEFAULT_SEM_YOFFSET;
    
}
