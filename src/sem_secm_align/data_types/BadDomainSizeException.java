/*
 * The MIT License
 *
 * Copyright 2022 Nathaniel LESLIE.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
