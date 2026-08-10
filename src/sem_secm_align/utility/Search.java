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
package sem_secm_align.utility;

/**
 * A class containing functions for searching lists efficiently.
 * @author Nathaniel
 */
public class Search {
    /**
     * Returns the index in the array that has the greatest value that is strictly less than the key.
     * @param key the value against which array elements will be compared.
     * @param array an array of values sorted in order of lowest to highest.
     * @param min the minimum index to be considered
     * @param max the maximum index to be considered
     * @return -1 if all values in the array above the minimum index are greater than or equal to the key. Otherwise returns the highest index for which array[index] &lt; key is true.
     */
    private static int FindSmaller(double key, double[] array, int min, int max){
        if(array[max] < key){
            return max;
        }
        else if(array[min] >= key){
            return -1;
        }
        else if(max-min <=1){
            return min;
        }
        else{
            int mid = (max - min) / 2 + min;
            if(array[mid] == key){
                return mid - 1;
            }
            else if(array[mid] < key){
                return FindSmaller(key, array, mid, max);
            }
            else{
                return FindSmaller(key, array, min, mid);
            }
        }
    }
    
    /**
     * Returns the index in the array that has the greatest value that is strictly less than the key. Employs a binary-search.
     * @param key the value against which array elements will be compared.
     * @param array an array of values sorted in order of lowest to highest.
     * @return -1 if all values in the array are greater than or equal to the key. Otherwise returns the highest index for which array[index] &lt; key is true.
     */
    public static int FindSmaller(double key, double[] array){
        return FindSmaller(key, array, 0, array.length - 1);
    }
}
