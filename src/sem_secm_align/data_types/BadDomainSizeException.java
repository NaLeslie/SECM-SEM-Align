/*
 * Created: 2022-06-22
 * Updated: 2022-06-22
 * Nathaniel Leslie
 */
package sem_secm_align.data_types;

/**
 * An exception for indicating that a domain is insufficient for performing a certain action.
 * Some examples include:
 * <ul>
 * <li>Trying to perform bicubic interpolation when one or more dimensions are too small to fit a cubic function.</li>
 * </ul>
 * @author Nathaniel
 */
public class BadDomainSizeException extends Exception{
    /**
     * Creates an instance of <code>BadDomainSizeException</code>.
     */
    public BadDomainSizeException(){
        super();
    }
    
    /**
     * Creates an instance of <code>BadDomainSizeException</code>.
     * @param message The message to be displayed.
     */
    public BadDomainSizeException(String message){
        super(message);
    }
}
