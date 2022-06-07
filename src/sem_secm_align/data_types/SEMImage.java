/*
 * Created: 2022-03-30
 * Updated: 2022-03-30
 * Nathaniel Leslie
 */
package sem_secm_align.data_types;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import sem_secm_align.settings.ColourSettings;

/**
 *
 * @author Nathaniel
 */
public class SEMImage {
    public SEMImage(String filepath){
        sem_image = null;
        try{
            sem_image = ImageIO.read(new File(filepath));
            displayable = true;
        } catch (IOException ex) {
            ex.printStackTrace();
            displayable = false;
            sem_image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics def = sem_image.getGraphics();
            def.setColor(ColourSettings.BACKGROUND_COLOUR);
            def.fillRect(0, 0, 1, 1);
        }
    }
    
    public SEMImage(){
        displayable = false;
        sem_image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics def = sem_image.getGraphics();
        def.setColor(ColourSettings.BACKGROUND_COLOUR);
        def.fillRect(0, 0, 1, 1);
    }
    
    public int getHeight(){
        return sem_image.getHeight();
    }
    
    public BufferedImage getImage(){
        return sem_image;
    }
    
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
    
    private BufferedImage sem_image;
    private boolean displayable;
}
