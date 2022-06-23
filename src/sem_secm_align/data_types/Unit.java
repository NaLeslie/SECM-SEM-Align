/*
 * Created: 2022-03-30
 * Updated: 2022-03-30
 * Nathaniel Leslie
 */
package sem_secm_align.data_types;

/**
 * Holds relevant data for a unit of measurement
 * @author Nathaniel
 */
public class Unit {
    
    /**
     * Creates a new instance of Unit.
     * @param label the unit's label (e.g. nA)
     * @param factor the unit's factor (e.g. 1e-9)
     */
    public Unit(String label, double factor){
        unit_label = label;
        unit_factor = factor;
    }
    
    /**
     * Changes the Unit's label
     * @param label the new label (e.g. pA)
     */
    public void setLabel(String label){
        unit_label = label;
    }
    
    /**
     * Changes the Unit's factor
     * @param factor the new label (e.g. 1e-3)
     */
    public void setFactor(double factor){
        unit_factor = factor;
    }
    
    /**
     * Fetches the Unit's label
     * @return the Unit's label as a string
     */
    public String getLabel(){
        return unit_label;
    }
    
    /**
     * Fetches the Unit's factor
     * @return the Unit's factor as a double
     */
    public double getFactor(){
        return unit_factor;
    }
    
    /**
     * The name of the unit
     */
    private String unit_label;
    
    /**
     * The scaling factor of the unit (preferably relative to the base SI unit)
     */
    private double unit_factor;
}
