/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sem_secm_align;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import sem_secm_align.data_types.ImproperFileFormattingException;
import sem_secm_align.settings.ColourSettings;
import static sem_secm_align.utility.ImageParser.bufferedImageToGrayscale;
import sem_secm_align.utility.filters.Filter;
import sem_secm_align.utility.filters.SobelX;
import sem_secm_align.utility.filters.SobelY;

/**
 *
 * @author Nathaniel
 */
public class Tests {
    public static void testSobel() throws ImproperFileFormattingException{
        BufferedImage sem_image = null;
        boolean readcorrectly;
        try{
            sem_image = ImageIO.read(new File("C:\\Users\\Nathaniel\\Pictures\\Sadgewbg.png"));
            readcorrectly = true;
        } catch (IOException ex) {
            ex.printStackTrace();
            readcorrectly = false;
            sem_image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics def = sem_image.getGraphics();
            def.setColor(ColourSettings.BACKGROUND_COLOUR);
            def.fillRect(0, 0, 1, 1);
        }
        if(readcorrectly){
            double[][] imgdat = bufferedImageToGrayscale(sem_image);
            Filter fsx = new SobelX();
            Filter fsy = new SobelY();
            double[][] sx = fsx.applyFilter(imgdat);
            double[][] sy = fsy.applyFilter(imgdat);
            
            try {
                PrintWriter pw = new PrintWriter(new File("datout.txt"));
                pw.println("x,y,gray,sx,sy");
                for(int x = 0; x < imgdat.length; x++){
                    for(int y = 0; y < imgdat[0].length; y++){
                        pw.print("\n" + x + "," + y + "," + imgdat[x][y] + "," + sx[x][y] + "," + sy[x][y]);
                    }
                }
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            
        }
    }
    
    public static void testIntegerDataBuffers() throws ImproperFileFormattingException{
        BufferedImage sem_image = null;
        boolean readcorrectly;
        try{
            sem_image = ImageIO.read(new File("C:\\Users\\Malak\\Documents\\NetBeansProjects\\SEM-SECM Align\\Ex\\Al-AR.tif"));
            readcorrectly = true;
        } catch (IOException ex) {
            ex.printStackTrace();
            readcorrectly = false;
            sem_image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics def = sem_image.getGraphics();
            def.setColor(ColourSettings.BACKGROUND_COLOUR);
            def.fillRect(0, 0, 1, 1);
        }
        if(readcorrectly){
            double[][] imgdat = bufferedImageToGrayscale(sem_image);
            Filter fsx = new SobelX();
            Filter fsy = new SobelY();
            double[][] sx = fsx.applyFilter(imgdat);
            double[][] sy = fsy.applyFilter(imgdat);
            
            try {
                PrintWriter pw = new PrintWriter(new File("datout.txt"));
                pw.println("x,y,gray,sx,sy");
                for(int x = 0; x < imgdat.length; x++){
                    for(int y = 0; y < imgdat[0].length; y++){
                        pw.print("\n" + x + "," + y + "," + imgdat[x][y] + "," + sx[x][y] + "," + sy[x][y]);
                    }
                }
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            
        }
    }
    
    public static void export_2d_double(double[][] data, String filename){
        try {
                PrintWriter pw = new PrintWriter(new File(filename));
                pw.println("x,y,data");
                for(int x = 0; x < data.length; x++){
                    for(int y = 0; y < data[0].length; y++){
                        pw.print("\n" + x + "," + y + "," + data[x][y]);
                    }
                }
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
