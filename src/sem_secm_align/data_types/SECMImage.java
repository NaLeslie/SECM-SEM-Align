/*
 * Created: 2022-03-30
 * Updated: 2022-04-12
 * Nathaniel Leslie
 */
package sem_secm_align.data_types;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import sem_secm_align.settings.Constants;
import sem_secm_align.utility.Search;

/**
 *
 * @authorNathaniel
 */
public class SECMImage {
    
    public SECMImage(){
        displayable = false;
        current_minimum = 0.0;
        current_maximum = 1.0;
        data = new double[][]{{0,0.25},{0.5,0.75}};
        x_coordinates = new double[]{1,2};
        y_coordinates = new double[]{1,2};
    }
    
    public SECMImage(String filepath){
        try {
            readFileFlux(filepath);
            displayable = true;
        } catch (FileNotFoundException | ImproperFileFormattingException ex) {
            ex.printStackTrace();
            displayable = false;
            current_minimum = 0.0;
            current_maximum = 1.0;
            data = new double[][]{{0,0.25},{0.5,0.75}};
            x_coordinates = new double[]{1,2};
            y_coordinates = new double[]{1,2};
        }
    }
    
    /**
     * Determines if the SECM image has all of the necessary information to be displayed.
     * @return true if the SECM image has all of the necessary information to be displayed, false if otherwise.
     */
    public boolean isDisplayable(){
        return displayable;
    }
    
    public double[][] getData(){
        return data;
    }
    
    public double getScaledCurrent(double x, double y, int interpolation_mode){
        if(interpolation_mode == INTERPOLATION_NN){
            return getScaledCurrentNN(x, y);
        }
        return Double.NaN;
    }
    
    private double getScaledCurrentNN(double x, double y){
        //find bounds
        int x_lower = Search.FindSmaller(x, x_coordinates);
        int x_upper = x_lower + 1;
        int y_lower = Search.FindSmaller(y, y_coordinates);
        int y_upper = y_lower + 1;
        
        int x_index;
        int y_index;
        //find x-index
        if(x_lower == -1){
            x_index = 0;
        }
        else if(x_lower == x_coordinates.length - 1){
            x_index = x_coordinates.length - 1;
        }
        else if(Math.abs(x - x_coordinates[x_lower]) < Math.abs(x - x_coordinates[x_upper])){
            x_index = x_lower;
        }
        else{
            x_index = x_upper;
        }
        
        //find y-index
        if(y_lower == -1){
            y_index = 0;
        }
        else if(y_lower == y_coordinates.length - 1){
            y_index = y_coordinates.length - 1;
        }
        else if(Math.abs(y - y_coordinates[y_lower]) < Math.abs(y - y_coordinates[y_upper])){
            y_index = y_lower;
        }
        else{
            y_index = y_upper;
        }
        
        return data[x_index][y_index];
    }
    
    public double getCurrent(double x, double y, int interpolation_mode){
        return Double.NaN;
    }
    
    public double getXMin(){
        return x_coordinates[0];
    }
    
    public double getXMax(){
        return x_coordinates[x_coordinates.length -1];
    }
    
    public double getYMin(){
        return y_coordinates[0];
    }
    
    public double getYMax(){
        return y_coordinates[y_coordinates.length -1];
    }
    
    private void readFileFlux(String filepath) throws FileNotFoundException, ImproperFileFormattingException{
        File f = new File(filepath);
        Scanner scan = new Scanner(f);
        ArrayList<Double> xs = new ArrayList();
        ArrayList<Double> ys = new ArrayList();
        while(scan.hasNextLine()){//scan through the file
            String readline = scan.nextLine();
            if(!readline.startsWith("#")){//ignore "comment" lines
                String[] readsplit = readline.split(",");
                double x = Double.parseDouble(readsplit[0]);//read in the X-coordinate
                double y = Double.parseDouble(readsplit[1]);//read in the Y-coordinate
                double cur = Double.parseDouble(readsplit[2]);//read in the current
                current_minimum = cur;
                current_maximum = cur;
                boolean xexists = false;
                for(int i = 0; i< xs.size(); i++){
                    double relative = Math.abs((x - xs.get(i))/xs.get(i));
                    if(xs.get(i) == 0){
                        relative = Math.abs(x - xs.get(i));
                    }

                    if(relative < Constants.RELATIVE_ERR_CUTOFF){//see if the x already exists
                        xexists = true;
                        break;//if the x already exists, we do not need to iterate any further in the array
                    }
                }
                if(!xexists){
                    xs.add(x);//add the new x to the list if it is not already
                }
                boolean yexists = false;
                for(int i = 0; i< ys.size(); i++){
                    double relative = Math.abs((y - ys.get(i))/ys.get(i));
                    if(ys.get(i) == 0){
                        relative = Math.abs(y - ys.get(i));
                    }
                    if(relative < Constants.RELATIVE_ERR_CUTOFF){//see if the y already exists
                        yexists = true;
                        break;//if the y already exists, we do not need to iterate any further in the array
                    }
                }
                if(!yexists){
                    ys.add(y);//add the new y to the list if it is not already
                }
            }
        }
        //sort the x&y coordinates and create arrays out of them
        Collections.sort(xs);
        Collections.sort(ys);
        x_coordinates = new double[xs.size()];
        y_coordinates = new double[ys.size()];
        for(int i = 0; i < x_coordinates.length; i++){
            x_coordinates[i] = xs.get(i);
        }
        for(int i = 0; i < y_coordinates.length; i++){
            y_coordinates[i] = ys.get(i);
        }
        int counter = 0;
        //second pass over the file
        data = new double[x_coordinates.length][y_coordinates.length]; // This holds the relative current at each point x,y
        scan.close();
        scan = new Scanner(f);
        while(scan.hasNextLine()){//scan through the file
            String readline = scan.nextLine();
            if(!readline.startsWith("#")){//ignore "comment" lines
                String[] readsplit = readline.split(",");
                double x = Double.parseDouble(readsplit[0]);//read in the X-coordinate
                double y = Double.parseDouble(readsplit[1]);//read in the Y-coordinate
                double cur = Double.parseDouble(readsplit[2]);//read in the current
                int xaddr = Search.FindSmaller(x, x_coordinates) + 1;//determine the x-address
                int yaddr = Search.FindSmaller(y, y_coordinates) + 1;//determine the y-address
                counter ++;
                data[xaddr][yaddr] = cur;
                if(cur < current_minimum){
                    current_minimum = cur;
                }
                if(cur > current_maximum){
                    current_maximum = cur;
                }
            }
        }
        double current_amplitude = current_maximum - current_minimum;
        for(int i = 0; i < data.length; i++){
            for(int ii = 0; ii < data[0].length; ii++){
                data[i][ii] = (data[i][ii] - current_minimum)/current_amplitude;
            }
        }
        scan.close();
        if(counter != x_coordinates.length*y_coordinates.length){
            throw new ImproperFileFormattingException("Number of points in file does not match file dimensions.\nExpected: " + x_coordinates.length*y_coordinates.length + " Found: " + counter);
        }
        
    }
    
    private double current_maximum;
    private double current_minimum;
    private double[][] data;
    private boolean displayable;
    private double[] x_coordinates;
    private double[] y_coordinates;
    
    public static final int INTERPOLATION_NN = 0;
    public static final int INTERPOLATION_BILINEAR = 1;
    public static final int INTERPOLATION_BICUBIC = 2;
}
