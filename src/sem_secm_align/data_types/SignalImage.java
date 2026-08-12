/*
 * The MIT License
 *
 * Copyright 2026 Nathaniel LESLIE.
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

import java.awt.geom.AffineTransform;

/**
 *
 * @author Nathaniel
 */
public class SignalImage {
    
    public SignalImage(ImageDomain dom, double[][] data){
        domain = new ImageDomain(dom);
        int xlen = data.length;
        int ylen = data[0].length;
        this.data = new double[xlen][ylen];
        for(int x = 0; x < xlen; x++){
            System.arraycopy(data[x], 0, this.data[x], 0, ylen);
        }
    }
    
//    public static SignalImage bilinearInterpolateToNewDomain(ImageDomain new_domain, AffineTransform transform, SignalImage to_interpolate){
//        //apply `transform` to `to_interpolate`
//        //perform bilinear interpolation between `to_interpolate` points
//        //integrate under this interpolation within the areas of new_domain to assign values.
//        
//        for(int ndx = 0; ndx < new_domain.getXDimension()-1; ndx ++){
//            for(int ndy = 0; ndy < new_domain.getYDimension()-1; ndy ++){
//                //check any overlaps between transformed image and rectangle [ndx <-> ndx+1],[ndy <-> ndy+1]
//                    //special case if transformed square is entirely within the rectangle since the signal is now the full integration of the transformed quadrilateral
//            }
//        }
//    }
    
    protected double[][] data;    
    protected ImageDomain domain;
}
