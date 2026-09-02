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
public class Triangle {
    
    public Triangle(Point2D.Double v0, Point2D.Double v1, Point2D.Double v2){
        vertices = new Point2D.Double[3];
        vertices[0] = new Point2D.Double(v0.getX(), v0.getY());
        vertices[1] = new Point2D.Double(v1.getX(), v1.getY());
        vertices[2] = new Point2D.Double(v2.getX(), v2.getY());
    }
    
    private Point2D.Double[] vertices;
}
