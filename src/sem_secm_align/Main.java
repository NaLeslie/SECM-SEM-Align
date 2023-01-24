/*
 * Created: 2022-01-14
 * Updated: 2022-01-14
 * Nathaniel Leslie
 */
package sem_secm_align;

import java.io.IOException;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Nathaniel
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            MainWindow mw = new MainWindow();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException | IOException ex) {
        
        }
        
    }
    
}
