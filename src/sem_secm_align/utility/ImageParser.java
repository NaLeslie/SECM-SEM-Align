/*
 * Created: 2022-12-06
 * Updated: 2022-12-06
 * Nathaniel Leslie
 */
package sem_secm_align.utility;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 *
 * @author Nathaniel
 */
public class ImageParser {
    
    public static double[][] bufferedImageToGrayscale(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        
        double[][] imagedata = new double[width][height];
        
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        
        boolean alpha_channel_present = image.getAlphaRaster() != null;
        int pixelsize = 3; //size that a pixel occupies in the byte array
        int offs = 0;
        if(alpha_channel_present){
            pixelsize = 4; //alpha channel adds a byte to each pixel
            offs = 1;
        }
        
        int row = 0;
        int col = 0;
        
        for(int i = 0; i < pixels.length - pixelsize + 1; i += pixelsize){
            
            imagedata[col][row] += ((int)pixels[i+offs] & 0xff);
            imagedata[col][row] += ((int)pixels[i+offs + 1] & 0xff);
            imagedata[col][row] += ((int)pixels[i+offs + 2] & 0xff);
            imagedata[col][row] /= 3.0;
            
            col ++;
            if(col >= width){
                col = 0;
                row ++;
            }
        }
        
        return imagedata;
    }
    
}
