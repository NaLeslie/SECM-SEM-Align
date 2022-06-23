/*
 * Created: 2022-06-22
 * Updated: 2022-06-22
 * Nathaniel Leslie
 */
package sem_secm_align.utility;

/**
 * Methods useful for bicubic interpolation
 * @author Nathaniel Leslie
 */
public class Bicubic {
    
    /**
     * <p>Computes the inverse of the interpolation matrix, X:</p>
     * <p><code>    [ ll<sup>3</sup> ll<sup>2</sup> ll   1 ]<sup>-1</sup></code></p>
     * <p><code>    |   l<sup>3</sup>   l<sup>2</sup>   l    1 |</code></p>
     * <p><code>    |   u<sup>3</sup>   u<sup>2</sup>   u    1 |</code></p>
     * <p><code>    [ uu<sup>3</sup> uu<sup>2</sup> uu   1 ]</code></p>
     * <p></p>
     * <p>The parameters for bicubic interpolation, A, are related to the interpolation matrix and the y-values by:</p>
     * <p> Y = X*A</p>
     * <p> Y,A are R-4 vectors</p>
     * <p>The inverse, X<sup>-1</sup> is needed to solve for A</p>
     * @param ll the coordinate lower than <code>l</code> 
     * @param l the coordinate immediately lower than the point at which the interpolation is to take place
     * @param u the coordinate immediately greater than the point at which the interpolation is to take place
     * @param uu the coordinate greater than <code>u</code> 
     * @return the inverse of the matrix:
     * <p><code>[0][0] [0][1] [0][2] [0][3]</code></p>
     * <p><code>[1][0] [1][1] [1][2] [1][3]</code></p>
     * <p><code>[2][0] [2][1] [2][2] [2][3]</code></p>
     * <p><code>[3][0] [3][1] [3][2] [3][3]</code></p>
     * @throws sem_secm_align.utility.SingularMatrixException if two of <code>ll, l, u, uu</code> are not unique. 
     */
    public static double[][] cInverse(double ll, double l, double u, double uu) throws SingularMatrixException{
        // test inputs
        if(ll == l || ll == u || ll == uu || l == u || l == uu || u == uu){
            throw new SingularMatrixException("The interpolation matrix cannot be inverted. Ensure ll, l, u, uu are all unique.");
        }
        //construct the inverted matrix
        double[][] inv = new double[4][4];
        //https://www.wolframalpha.com/input?i2d=true&i=Power%5B%7B%7BPower%5By%2C3%5D%2CPower%5By%2C2%5D%2Cy%2C1%7D%2C%7BPower%5Bv%2C3%5D%2CPower%5Bv%2C2%5D%2Cv%2C1%7D%2C%7BPower%5Bw%2C3%5D%2CPower%5Bw%2C2%5D%2Cw%2C1%7D%2C%7BPower%5Bx%2C3%5D%2CPower%5Bx%2C2%5D%2Cx%2C1%7D%7D%2C-1%5D
        inv[0][0] = -1.0/((l - ll)*(ll - u)*(ll - uu));
        inv[1][0] = (l + u + uu)/((l - ll)*(ll - u)*(ll - uu));
        inv[2][0] = -(l*(u + uu) + u*uu)/((l - ll)*(ll - u)*(ll - uu));
        inv[3][0] = (l*u*uu)/((l - ll)*(ll - u)*(ll - uu));
        inv[0][1] = 1.0/((l - u)*(l - uu)*(l - ll));
        inv[1][1] = -(u + uu + ll)/((l - u)*(l - uu)*(l - ll));
        inv[2][1] = (u*(uu + ll) + uu*ll)/((l - u)*(l - uu)*(l - ll));
        inv[3][1] = -(u*uu*ll)/((l - u)*(l - uu)*(l - ll));
        inv[0][2] = -1.0/((l - u)*(u - uu)*(u - ll));
        inv[1][2] = (l + uu + ll)/((l - u)*(u - uu)*(u - ll));
        inv[2][2] = -(l*(uu + ll) + uu*ll)/((l - u)*(u - uu)*(u - ll));
        inv[3][2] = (l*uu*ll)/((l - u)*(u - uu)*(u - ll));
        inv[0][3] = 1.0/((l - uu)*(u - uu)*(uu - ll));
        inv[1][3] = -(l + u + ll)/((l - uu)*(u - uu)*(uu - ll));
        inv[2][3] = (l*(u + ll) + u*ll)/((l - uu)*(u - uu)*(uu - ll));
        inv[3][3] = -(l*u*ll)/((l - uu)*(u - uu)*(uu - ll));
        return inv;
    }
    
}
