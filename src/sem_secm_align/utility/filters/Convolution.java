/*
 * Created: 2020-09-11
 * Updated: 2022-12-06
 * Nathaniel Leslie
 */
package sem_secm_align.utility.filters;

/**
 * 
 * @author Nathaniel
 */
public class Convolution {
    /**
     * <pre>
     * Convolves two matrices and returns the result. return = a(x)b. 
     * Assumptions:
     *  - b is zero beyond the bounds of its matrix.
     *  - a is the outermost value of its matrix beyond the bounds of its matrix (see Windowing.ExtrapolatePad)
     *  - b,a are invariant i.e. they are constant for all cases.
     *  - b,a have the same spatial resolution
     *  - b is centred about its centre.
     *  - b is normalized [sum(b) = 1].
     * Follows: https://en.wikipedia.org/wiki/Convolution#Discrete_convolution
     * </pre>
     * @param a The first matrix to be convolved
     * @param b The second matrix to be convolved
     * @return The convolved matrix, a(x)b. Will have the same dimensions as a.
     */
    public static double[][] convolve2D(double[][] a, double[][] b){
        //set up an empty matrix of size a.
        int xmax = a.length;
        int ymax = a[0].length;
        double[][] convolved = new double[xmax][ymax];
        //populate the matrix by iterating over a,b convolved[X][Y] = Sum(a[x-u][y-v]*b[u][v])
        int xbcent = (int)( (float)(b.length) * 0.5f); // Centre of matrix b in x
        int ybcent = (int)( (float)(b[0].length) * 0.5f); // Centre of matrix b in y
        int xbbreadth = b.length - xbcent;
        int ybbreadth = b[0].length - ybcent;
        for(int x = 0; x < xmax; x++){
            for(int y = 0; y < ymax; y++){
                //Sum(a[x-u][y-v]*b[u][v])
                for(int u = -xbbreadth; u <= xbbreadth; u++){
                    for(int v = -ybbreadth; v <= ybbreadth; v++){
                        convolved[x][y] += extrapolatePad(a,x-u,y-v)*zeroPad(b,xbcent + u,ybcent + v);
                    }
                }
            }
        }
        return convolved;
    }
    
    /**
     * Simulates matrix data with an infinitely thick padding of 0s outside the bounds of the matrix.
     * @param data The data matrix
     * @param x The x address
     * @param y The y address
     * @return If x,y are in range of data[][], data[x][y] is returned. Otherwise, 0.0f is returned.
     */
    public static double zeroPad(double[][] data, int x, int y){
        if(x >=0 && y >=0 && x < data.length && y < data[0].length){
            return data[x][y]; //return data if x and y have a "legal" value
        }
        else{
            return 0;
        }
    }
        
    /**
     * Simulates array data with an infinitely thick padding of the most extreme values outside the bounds of the array:
 aaa abcd ddd
 aaa abcd ddd
 aaa abcd ddd
     ----
 aaa|abcd|ddd
 eee|efgh|hhh
 iii|ijkl|lll
 mmm|mnop|ppp
     ----
 mmm mnop ppp
 mmm mnop ppp
 mmm mnop ppp
 This has an advantage over zeroPad since this is more likely to preserve the overall energy or flux of the image while also reducing artefacts at the edges of the image.
     * @param data
     * @param x
     * @param y
     * @return 
     */
    public static double extrapolatePad(double[][] data, int x, int y){
        if(x >=0 && x < data.length){
            if(y < 0){
                //botton side
                return data[x][0];
            }
            else if(y >= data[0].length){
                //top side
                return data[x][data[0].length - 1];
            }
            else{
                //in range
                return data[x][y];
            }
        }
        else if(x < 0){
            if(y < 0){
                //botton left corner
                return data[0][0];
            }
            else if(y >= data[0].length){
                //top left corner
                return data[0][data[0].length - 1];
            }
            else{
                //left side
                return data[0][y];
            }
        }
        else{
            if(y < 0){
                //botton right corner
                return data[data.length - 1][0];
            }
            else if(y >= data[0].length){
                //top right corner
                return data[data.length - 1][data[0].length - 1];
            }
            else{
                //right side
                return data[data.length - 1][y];
            }
        }
    }
    
}
