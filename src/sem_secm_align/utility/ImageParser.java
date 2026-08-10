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
package sem_secm_align.utility;

import java.awt.Color;
import java.awt.image.BufferedImage;
import sem_secm_align.data_types.ImproperFileFormattingException;

/**
 * A class containing methods to parse images into double[][] arrays.
 * @author Nathaniel
 */
public class ImageParser {
    
    /**
     * Parses a given image and converts it into a double[][] that reflects the grayscale values for the image.
     * @param image The image to be converted.
     * @return The grayscale values of the image (the average of the red green and blue channels).
     * @see BufferedImage#getRGB(int, int) 
     * @see Color#Color(int) 
     */
    public static double[][] bufferedImageToGrayscale(BufferedImage image){
        
        int width = image.getWidth();
        int height = image.getHeight();
        
        double[][] imagedata = new double[width][height];
        
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                Color c = new Color(image.getRGB(x, y));
                imagedata[x][y] += c.getBlue() + c.getGreen() + c.getRed();
                imagedata[x][y] /= 3;
            }
        }
        
        return imagedata;
        
    }
       
}
