/*
 * The MIT License
 *
 * Copyright 2026 Nathaniel Leslie.
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
package sem_secm_align.utility;

import java.awt.geom.Point2D;

/**
 *
 * @author Nathaniel
 */
public class LineSegment {
    
    public LineSegment(double x1, double y1, double x2, double y2){
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }
    
    /**
     * Finds the unique intersection between line segments, if one exists. This function will return (Double.NaN, Double.NaN) if there are 0 or infinite solutions.
     * This function excludes the termination points of the line segments from the check.
     * @param ls1 The first line segment
     * @param ls2 The second line segment
     * @return 
     */
    public static Point2D.Double findIntersection(LineSegment ls1, LineSegment ls2){
        /*
        LS1 = [ls1.x1, ls1.y1] + t1[ls1.x2-ls1.x1, ls1.y2-ls1.y1]
        LS2 = [ls2.x1, ls2.y1] + t2[ls2.x2-ls2.x1, ls2.y2-ls2.y1]
        t1,t2 are real numbers bounded between 0 and 1
        
        intersection at:
        LS1 - LS2 = 0 = [ls1.x1-ls2.x1, ls1.y1-ls2.y1] + t1[ls1.x2-ls1.x1, ls1.y2-ls1.y1] - t2[ls2.x2-ls2.x1, ls2.y2-ls2.y1]
        
        [ls1.x1-ls2.x1] = [ls1.x2-ls1.x1   -ls2.x2-ls2.x1][t1]
        [ls1.y1-ls2.y1] = [ls1.y2-ls1.y1   -ls2.y2-ls2.y1][t2]
        
        [t1] = 1.0 /  [-ls2.y2-ls2.y1   ls2.x2-ls2.x1][ls1.x1-ls2.x1]
        [t2] =  det   [-ls1.y2-ls1.y1   ls1.x2-ls1.x1][ls1.y1-ls2.y1]
        */
        double ls1dx = ls1.x2 - ls1.x1;//ls1.x2-ls1.x1
        double ls1dy = ls1.y2 - ls1.y1;//ls1.y2-ls1.y1
        double ls2dx = ls2.x2 - ls2.x1;//ls2.x2-ls2.x1
        double ls2dy = ls2.y2 - ls2.y1;//ls2.y2-ls2.y1
        double determinant = -ls1dx*ls2dy + ls1dy*ls2dx;
        if(determinant == 0.0){//chack case of parallell lines
            return new Point2D.Double(Double.NaN, Double.NaN);
        }
        double detinv = 1.0 / determinant;
        double x1dls = ls1.x1 - ls2.x1;
        double y1dls = ls1.y1 - ls2.y1;
        double t1 = (-ls2dy*x1dls + ls2dx*y1dls)*detinv;
        double t2 = (-ls1dy*x1dls + ls1dx*y1dls)*detinv;
        if(t1 < 1.0 && t1 > 0.0 && t2 < 1.0 && t2 > 0.0){//check that intersection occurs within the line segments
            double xint = ls1.x1 + t1*ls1dx;
            double yint = ls1.y1 + t1*ls1dy;
            return new Point2D.Double(xint, yint);
        }
        return new Point2D.Double(Double.NaN, Double.NaN);
    }
    
    /**
     * Quick determination if the point (px,py) is on the line segment ls (or on the line of which ls is a segment if extendline is set to TRUE)
     * @param ls the line segment
     * @param px the x coordinate of the point
     * @param py the y coordinate of the point
     * @param extendline calculates if the point is anywhere along the line of which the segment is a part if this flag is TRUE. A value of FALSE will restrict the algorithm to look for solutions within the line segment.
     * @return 
     */
    public static boolean pointOnSegment(LineSegment ls, double px, double py, boolean extendline){
        /*
        px = ls.x1 + tx*lsdx
        py = ls.y1 + ty*lsdy
        if true, tx=ty
        */
        double lsdx = ls.x2 - ls.x1;
        double lsdy = ls.y2 - ls.y1;
        if(lsdx != 0.0){
            double tx = (px - ls.x1) / lsdx;
            boolean within_extent = extendline || (tx >= 0.0 && tx <= 1.0);
            double y = ls.y1 + tx*lsdy;
            return within_extent && y == py;
        }
        else{
            if(px != ls.x1){
                return false;
            }
            
            if(lsdy != 0.0){
                double ty = 1;
                boolean within_extent = extendline || (ty >= 0.0 && ty <= 1.0);
                return within_extent;//NOTE: the LS is vertical and we already know that px = ls.x1, so we don't need to check it again.
            }
            else{
                return (py == ls.y1);
            }
        }
    }
    
    protected double x1,x2,y1,y2;
}
