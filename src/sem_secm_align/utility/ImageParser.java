/*
 * Created: 2022-12-06
 * Updated: 2022-12-06
 * Nathaniel Leslie
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
