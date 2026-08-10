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

/**
 *
 * @author Nathaniel
 */
public class ImageDomain {
    
    /**
     * Creates a new instance of an ImageDomain by copying the x and y coordinates passed into this constructor as arguments.
     * @param x_coordinates the x positions of the points in the image
     * @param y_coordinates the y positions of the points in the image
     */
    public ImageDomain(double[] x_coordinates, double[] y_coordinates){
        int xlen = x_coordinates.length;
        int ylen = y_coordinates.length;
        this.x_coordinates = new double[xlen];
        this.y_coordinates = new double[ylen];
        System.arraycopy(x_coordinates, 0, this.x_coordinates, 0, x_coordinates.length);
        System.arraycopy(y_coordinates, 0, this.y_coordinates, 0, y_coordinates.length);
    }
    
    /**
     * Performs a deep copy of the argument.
     * @param to_copy the image domain to be copied
     */
    public ImageDomain(ImageDomain to_copy){
        int xlen = to_copy.x_coordinates.length;
        int ylen = to_copy.y_coordinates.length;
        this.x_coordinates = new double[xlen];
        this.y_coordinates = new double[ylen];
        System.arraycopy(to_copy.x_coordinates, 0, this.x_coordinates, 0, to_copy.x_coordinates.length);
        System.arraycopy(to_copy.y_coordinates, 0, this.y_coordinates, 0, to_copy.y_coordinates.length);
    }
    
    /**
     * Fetches the number of points along the X-direction
     * @return x_coordinates.length
     */
    public int getXDimension(){
        return x_coordinates.length;
    }
    
    /**
     * Fetches the largest x-coordinate represented by the SECM image's raw data.
     * @return the largest x-coordinate of this image.
     */
    public double getXMax(){
        return x_coordinates[x_coordinates.length -1];
    }
    
    /**
     * Fetches the smallest x-coordinate represented by the SECM image's raw data.
     * @return the smallest x-coordinate of this image.
     */
    public double getXMin(){
        return x_coordinates[0];
    }
    
    /**
     * Fetches the number of points along the Y-direction
     * @return y_coordinates.length
     */
    public int getYDimension(){
        return y_coordinates.length;
    }
    
    /**
     * Fetches the largest y-coordinate represented by the SECM image's raw data.
     * @return the largest y-coordinate of this image.
     */
    public double getYMax(){
        return y_coordinates[y_coordinates.length -1];
    }
    
    /**
     * Fetches the smallest y-coordinate represented by the SECM image's raw data.
     * @return the smallest y-coordinate of this image.
     */
    public double getYMin(){
        return y_coordinates[0];
    }
    
    protected double[] x_coordinates;
    protected double[] y_coordinates;
}
