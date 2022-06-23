/*
 * Created: 2022-06-22
 * Updated: 2022-06-22
 * Nathaniel Leslie
 */
package sem_secm_align.utility;

/**
 * Used to indicate that a matrix is singular
 * @author Nathaniel Leslie
 */
public class SingularMatrixException extends Exception{
    /**
     * Creates an instance of <code>SingularMatrixException</code>.
     */
    public SingularMatrixException(){
        super();
    }
    
    /**
     * Creates an instance of <code>SingularMatrixException</code>.
     * @param message The message to be displayed.
     */
    public SingularMatrixException(String message){
        super(message);
    }
}
