/*
 * The MIT License
 *
 * Copyright 2022 Nathaniel LESLIE.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
