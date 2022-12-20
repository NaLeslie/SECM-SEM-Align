/*
 * Created: 2022-12-15
 * Updated: 2022-12-15
 * Nathaniel Leslie
 */
package sem_secm_align.edge_detection;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 *
 * @author Nathaniel
 */
public class EdgeHistogram extends JPanel{
    
    public EdgeHistogram(){
        
    }
    
    @Override
    public void paint(Graphics g){
        g.drawImage(display_image, 0, 0, this.getWidth(), this.getHeight(), this);
    }
    
    
    public void setHistogramData(double[] mags, int[] counts){
        this.counts = counts;
        this.magnitudes = mags;
        updateGraphics();
    }
    
    public void updateGraphics(){
        
        repaint();
    }
    
    private int[] counts;
    private Image display_image;
    private double[] magnitudes;
}
