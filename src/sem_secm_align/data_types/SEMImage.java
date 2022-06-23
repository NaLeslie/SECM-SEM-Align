/*
 * Created: 2022-03-30
 * Updated: 2022-06-23
 * Nathaniel Leslie
 */
package sem_secm_align.data_types;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import sem_secm_align.settings.ColourSettings;

/**
 * Holds information pertaining to a scanning electron microscopy image
 * @author Nathaniel
 */
public class SEMImage {
    
    /**
     * Reads an SEM image that is stored at filepath. If there is an error, the image will be a black pixel and <code>isDisplayable()</code> will return <code>false</code>.
     * @param filepath The path to the SEM image
     */
    public SEMImage(String filepath){
        sem_image = null;
        boolean readcorrectly;
        try{
            sem_image = ImageIO.read(new File(filepath));
            readcorrectly = true;
        } catch (IOException ex) {
            ex.printStackTrace();
            readcorrectly = false;
            sem_image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics def = sem_image.getGraphics();
            def.setColor(ColourSettings.BACKGROUND_COLOUR);
            def.fillRect(0, 0, 1, 1);
        }
        displayable = readcorrectly;
    }
    
    /**
     * Creates a 1x1 image consisting of a black pixel.
     * <strong>Do not use this.</strong>
     */
    public SEMImage(){
        displayable = false;
        sem_image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics def = sem_image.getGraphics();
        def.setColor(ColourSettings.BACKGROUND_COLOUR);
        def.fillRect(0, 0, 1, 1);
    }
    
    /**
     * Returns the height of the SEM image in pixels.
     * @return the height of the SEM image in pixels.
     */
    public int getHeight(){
        return sem_image.getHeight();
    }
    
    /**
     * Returns the SEM image as a <code>BufferedImage</code>.
     * @return the SEM image
     */
    public BufferedImage getImage(){
        return sem_image;
    }
    
    /**
     * Returns the width of the SEM image in pixels.
     * @return the width of the SEM image in pixels.
     */
    public int getWidth(){
        return sem_image.getWidth();
    }
    
    /**
     * Determines if the SEM image has all of the necessary information to be displayed.
     * @return true if the SEM image has all of the necessary information to be displayed, false if otherwise.
     */
    public boolean isDisplayable(){
        return displayable;
    }
    
    /**
     * The SEM image proper.
     */
    private BufferedImage sem_image;
    /**
     * Whether or not this image is a valid SEM image that can be displayed.
     */
    private final boolean displayable;
}
