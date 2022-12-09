/*
 * Created: 2022-01-14
 * Updated: 2022-01-14
 * Nathaniel Leslie
 */
package sem_secm_align;

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
        
        Tests.testSobel();
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ex) {
        }
        MainWindow mw = new MainWindow();
    }
    
}
