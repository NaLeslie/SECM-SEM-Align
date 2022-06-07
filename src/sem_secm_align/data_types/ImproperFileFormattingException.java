/*
 * Created: 2019-12-04
 * Updated: 2020-09-11
 * Nathaniel Leslie
 */
package sem_secm_align.data_types;

/**
 * Used to indicate that something is wrong with the file.
 * @author Nathaniel
 */
public class ImproperFileFormattingException extends Exception{
    /**
     * Creates a new instance of ImproperFileFormattingException. 
     */
    public ImproperFileFormattingException(){
        super();
    }
    
    /**
     * Creates a new instance of ImproperFileFormattingException.
     * @param Message The Message to be included when this exception is thrown.
     */
    public ImproperFileFormattingException(String Message){
        super(Message);
    }
}