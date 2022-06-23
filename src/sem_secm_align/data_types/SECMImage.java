/*
 * Created: 2022-03-30
 * Updated: 2022-06-23
 * Nathaniel Leslie
 */
package sem_secm_align.data_types;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import sem_secm_align.settings.Constants;
import sem_secm_align.utility.Bicubic;
import sem_secm_align.utility.Search;
import sem_secm_align.utility.SingularMatrixException;

/**
 *
 * @authorNathaniel
 */
public class SECMImage {
    
    /**
     * Creates a dummy SECM image.
     * <strong>This constructor is not intended for use.</strong>
     */
    public SECMImage(){
        displayable = false;
        current_minimum = 0.0;
        current_maximum = 1.0;
        data = new double[][]{{0,0.25},{0.5,0.75}};
        x_coordinates = new double[]{1,2};
        y_coordinates = new double[]{1,2};
    }
    
    /**
     * Loads an SECM image in from a file. If the loading fails, this instance will be initialized to the dummy SECM image from <code>SECMImage()</code> and <code>isDisplayable()</code> will return <code>false</code>.
     * @param filepath The path to the SECM image data.
     */
    public SECMImage(String filepath){
        boolean readcorrectly;
        try {
            readFileFlux(filepath);
            readcorrectly = true;
        } catch (FileNotFoundException | ImproperFileFormattingException ex) {
            ex.printStackTrace();
            readcorrectly = false;
            current_minimum = 0.0;
            current_maximum = 1.0;
            data = new double[][]{{0,0.25},{0.5,0.75}};
            x_coordinates = new double[]{1,2};
            y_coordinates = new double[]{1,2};
        }
        displayable = readcorrectly;
    }
    
    /**
     * Determines if the SECM image has all of the necessary information to be displayed.
     * @return true if the SECM image has all of the necessary information to be displayed, false if otherwise.
     */
    public boolean isDisplayable(){
        return displayable;
    }
    
    /**
     * Returns the scaled data for this SECM image All values will be between <code>0</code> and <code>1</code> inclusively.
     * @return The data for the SECM image scaled from <code>0</code> to <code>1</code>.
     */
    public double[][] getData(){
        return data;
    }
    
    /**
     * Fetches the scaled current at the given <code>(x,y)</code> coordinate using the specified interpolation method to evaluate the current.
     * @param x the x-coordinate of the SECM image expressed in the same units as the raw data that was read initially.
     * @param y the y-coordinate of the SECM image expressed in the same units as the raw data that was read initially.
     * @param interpolation_mode the interpolation method to be used. The following options are available:
     * <ul>
     * <li><code>INTERPOLATION_NN</code>: Nearest-neighbor interpolation. (Constant function)</li>
     * <li><code>INTERPOLATION_BILINEAR</code>: Bilinear interpolation. (Linear function)</li>
     * <li><code>INTERPOLATION_BICUBIC</code>: Bicubic interpolation. (Cubic function). <strong>This mode requires an image larger than 5x5 to work</strong></li>
     * </ul>
     * @return The interpolated current at <code>(x,y)</code>. The return value will be between <code>0</code> and <code>1</code> inclusively. 
     * If an invalid <code>interpolation_mode</code> is entered or if an error occurs, <code>Double.NaN</code> will be returned.
     */
    public double getScaledCurrent(double x, double y, int interpolation_mode){
        switch (interpolation_mode) {
            case INTERPOLATION_NN:
                return getScaledCurrentNN(x, y);
            case INTERPOLATION_BILINEAR:
                return getScaledCurrentBilinear(x, y);
            case INTERPOLATION_BICUBIC:
                try{
                    return getScaledCurrentBicubic(x, y);
                }
                catch(BadDomainSizeException | SingularMatrixException e){
                    
                }

            default:
                return Double.NaN;
        }
    }
    
    /**
     * Uses nearest-neighbor interpolation to evaluate the current at a given <code>(x,y)</code>.
     * @param x The x-coordinate of interest
     * @param y The y-coordinate of interest
     * @return The scaled current at <code>(x,y)</code> according to nearest-neighbor interpolation. Value will be between 0 and 1 inclusively.
     */
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
    
    /**
     * Uses bilinear interpolation to evaluate the current at a given <code>(x,y)</code>.
     * @param x The x-coordinate of interest
     * @param y The y-coordinate of interest
     * @return The scaled current at <code>(x,y)</code> according to bilinear interpolation. Value will be between 0 and 1 inclusively.
     */
    private double getScaledCurrentBilinear(double x, double y){
        //find bounds
        int x_lower = Search.FindSmaller(x, x_coordinates);
        int x_upper = x_lower + 1;
        int y_lower = Search.FindSmaller(y, y_coordinates);
        int y_upper = y_lower + 1;
        
        //limit bounds
        if(x_lower < 0){
            x_lower = 0;
        }
        if(x_upper >= x_coordinates.length){
            x_upper = x_coordinates.length - 1;
        }
        if(y_lower < 0){
            y_lower = 0;
        }
        if(y_upper >= y_coordinates.length){
            y_upper = y_coordinates.length - 1;
        }
        
        //begin finding the current along x at y_lower
        double current_x_lower = data[x_lower][y_lower];
        double current_x_upper = data[x_upper][y_lower];
        double slope;
        if(x_upper != x_lower){
            slope = (current_x_upper - current_x_lower)/(x_coordinates[x_upper] - x_coordinates[x_lower]);
        }
        else{
            slope = 0;
        }
        double current_y_lower = slope*(x - x_coordinates[x_lower]) + current_x_lower;
        //if necessary find current along y_upper
        if(y_upper != y_lower){
            current_x_lower = data[x_lower][y_upper];
            current_x_upper = data[x_upper][y_upper];
            if(x_upper != x_lower){
                slope = (current_x_upper - current_x_lower)/(x_coordinates[x_upper] - x_coordinates[x_lower]);
            }
            else{
                slope = 0;
            }
            double current_y_upper = slope*(x - x_coordinates[x_lower]) + current_x_lower;
            slope = (current_y_upper - current_y_lower)/(y_coordinates[y_upper] - y_coordinates[y_lower]);
        }
        else{
            slope = 0;
        }
        double current = slope*(y-y_coordinates[y_lower]) + current_y_lower;
        return current;
    }
    
    /**
     * Uses bicubic interpolation to evaluate the current at a given <code>(x,y)</code>.
     * @param x The x-coordinate of interest
     * @param y The y-coordinate of interest
     * @return The scaled current at <code>(x,y)</code> according to bicubic interpolation. Value will be between 0 and 1 inclusively.
     */
    private double getScaledCurrentBicubic(double x, double y) throws BadDomainSizeException, SingularMatrixException{
        //test size
        if(x_coordinates.length < 5 || y_coordinates.length < 5){
            throw new BadDomainSizeException("Insufficient data for bicubic interpolation.");
        }
        //find bounds
        int x_index_l = Search.FindSmaller(x, x_coordinates);
        int x_index_ll = x_index_l - 1;
        int x_index_u = x_index_l + 1;
        int x_index_uu = x_index_l + 2;
        int y_index_l = Search.FindSmaller(y, y_coordinates);
        int y_index_ll = y_index_l - 1;
        int y_index_u = y_index_l + 1;
        int y_index_uu = y_index_l + 2;
        
        //handle out of bounds coordinates
        double x_ll;
        double x_l;
        double x_u;
        double x_uu;
        double y_ll;
        double y_l;
        double y_u;
        double y_uu;
        
        if(x_index_ll >= 0 && x_index_uu < x_coordinates.length){
            x_ll = x_coordinates[x_index_ll];
            x_l = x_coordinates[x_index_l];
            x_u = x_coordinates[x_index_u];
            x_uu = x_coordinates[x_index_uu];
        }
        else if(x_index_l < 0){
            x_index_l = 0;
            x_index_ll = 0;
            x_u = x_coordinates[x_index_u];
            x_uu = x_coordinates[x_index_uu];
            double diff = x_uu - x_u;
            x_l = x_u - diff;
            x_ll = x_l - diff;
        }
        else if(x_index_ll < 0){
            x_index_ll = 0;
            x_l = x_coordinates[x_index_l];
            x_u = x_coordinates[x_index_u];
            x_uu = x_coordinates[x_index_uu];
            double diff = x_u - x_l;
            x_ll = x_l - diff;
        }
        else if(x_index_u >= x_coordinates.length){
            x_index_u = x_coordinates.length - 1;
            x_index_uu = x_coordinates.length - 1;
            x_ll = x_coordinates[x_index_ll];
            x_l = x_coordinates[x_index_l];
            double diff = x_l - x_ll;
            x_u = x_l + diff;
            x_uu = x_u + diff;
        }
        else{
            x_index_uu = x_coordinates.length - 1;
            x_ll = x_coordinates[x_index_ll];
            x_l = x_coordinates[x_index_l];
            x_u = x_coordinates[x_index_u];
            double diff = x_u - x_l;
            x_uu = x_u + diff;
        }
        
        double[][] xinv = Bicubic.cInverse(x_ll, x_l, x_u, x_uu);
        
        if(y_index_ll >= 0 && y_index_uu < y_coordinates.length){
            y_ll = y_coordinates[y_index_ll];
            y_l = y_coordinates[y_index_l];
            y_u = y_coordinates[y_index_u];
            y_uu = y_coordinates[y_index_uu];
        }
        else if(y_index_l < 0){
            y_index_l = 0;
            y_index_ll = 0;
            y_u = y_coordinates[y_index_u];
            y_uu = y_coordinates[y_index_uu];
            double diff = y_uu - y_u;
            y_l = y_u - diff;
            y_ll = y_l - diff;
        }
        else if(y_index_ll < 0){
            y_index_ll = 0;
            y_l = y_coordinates[y_index_l];
            y_u = y_coordinates[y_index_u];
            y_uu = y_coordinates[y_index_uu];
            double diff = y_u - y_l;
            y_ll = y_l - diff;
        }
        else if(y_index_u >= y_coordinates.length){
            y_index_u = y_coordinates.length - 1;
            y_index_uu = y_coordinates.length - 1;
            y_ll = y_coordinates[y_index_ll];
            y_l = y_coordinates[y_index_l];
            double diff = y_l - y_ll;
            y_u = y_l + diff;
            y_uu = y_u + diff;
        }
        else{
            y_index_uu = y_coordinates.length - 1;
            y_ll = y_coordinates[y_index_ll];
            y_l = y_coordinates[y_index_l];
            y_u = y_coordinates[y_index_u];
            double diff = y_u - y_l;
            y_uu = y_u + diff;
        }
        
        double[][] yinv = Bicubic.cInverse(y_ll, y_l, y_u, y_uu);
        
        //current along x at y_uu
        //get a*x**3 + b*x**2 + c*x + d parameters by operating xinv on the currents
        double a = xinv[0][0]*data[x_index_ll][y_index_uu] + xinv[0][1]*data[x_index_l][y_index_uu] + xinv[0][2]*data[x_index_u][y_index_uu] + xinv[0][3]*data[x_index_uu][y_index_uu];
        double b = xinv[1][0]*data[x_index_ll][y_index_uu] + xinv[1][1]*data[x_index_l][y_index_uu] + xinv[1][2]*data[x_index_u][y_index_uu] + xinv[1][3]*data[x_index_uu][y_index_uu];
        double c = xinv[2][0]*data[x_index_ll][y_index_uu] + xinv[2][1]*data[x_index_l][y_index_uu] + xinv[2][2]*data[x_index_u][y_index_uu] + xinv[2][3]*data[x_index_uu][y_index_uu];
        double d = xinv[3][0]*data[x_index_ll][y_index_uu] + xinv[3][1]*data[x_index_l][y_index_uu] + xinv[3][2]*data[x_index_u][y_index_uu] + xinv[3][3]*data[x_index_uu][y_index_uu];
        double i_yuu = a*Math.pow(x, 3) + b*x*x + c*x + d;
        
        //current along x at y_u
        //get a*x**3 + b*x**2 + c*x + d parameters by operating xinv on the currents
        a = xinv[0][0]*data[x_index_ll][y_index_u] + xinv[0][1]*data[x_index_l][y_index_u] + xinv[0][2]*data[x_index_u][y_index_u] + xinv[0][3]*data[x_index_uu][y_index_u];
        b = xinv[1][0]*data[x_index_ll][y_index_u] + xinv[1][1]*data[x_index_l][y_index_u] + xinv[1][2]*data[x_index_u][y_index_u] + xinv[1][3]*data[x_index_uu][y_index_u];
        c = xinv[2][0]*data[x_index_ll][y_index_u] + xinv[2][1]*data[x_index_l][y_index_u] + xinv[2][2]*data[x_index_u][y_index_u] + xinv[2][3]*data[x_index_uu][y_index_u];
        d = xinv[3][0]*data[x_index_ll][y_index_u] + xinv[3][1]*data[x_index_l][y_index_u] + xinv[3][2]*data[x_index_u][y_index_u] + xinv[3][3]*data[x_index_uu][y_index_u];
        double i_yu = a*Math.pow(x, 3) + b*x*x + c*x + d;
        
        //current along x at y_l
        //get a*x**3 + b*x**2 + c*x + d parameters by operating xinv on the currents
        a = xinv[0][0]*data[x_index_ll][y_index_l] + xinv[0][1]*data[x_index_l][y_index_l] + xinv[0][2]*data[x_index_u][y_index_l] + xinv[0][3]*data[x_index_uu][y_index_l];
        b = xinv[1][0]*data[x_index_ll][y_index_l] + xinv[1][1]*data[x_index_l][y_index_l] + xinv[1][2]*data[x_index_u][y_index_l] + xinv[1][3]*data[x_index_uu][y_index_l];
        c = xinv[2][0]*data[x_index_ll][y_index_l] + xinv[2][1]*data[x_index_l][y_index_l] + xinv[2][2]*data[x_index_u][y_index_l] + xinv[2][3]*data[x_index_uu][y_index_l];
        d = xinv[3][0]*data[x_index_ll][y_index_l] + xinv[3][1]*data[x_index_l][y_index_l] + xinv[3][2]*data[x_index_u][y_index_l] + xinv[3][3]*data[x_index_uu][y_index_l];
        double i_yl = a*Math.pow(x, 3) + b*x*x + c*x + d;
        
        //current along x at y_l
        //get a*x**3 + b*x**2 + c*x + d parameters by operating xinv on the currents
        a = xinv[0][0]*data[x_index_ll][y_index_ll] + xinv[0][1]*data[x_index_l][y_index_ll] + xinv[0][2]*data[x_index_u][y_index_ll] + xinv[0][3]*data[x_index_uu][y_index_ll];
        b = xinv[1][0]*data[x_index_ll][y_index_ll] + xinv[1][1]*data[x_index_l][y_index_ll] + xinv[1][2]*data[x_index_u][y_index_ll] + xinv[1][3]*data[x_index_uu][y_index_ll];
        c = xinv[2][0]*data[x_index_ll][y_index_ll] + xinv[2][1]*data[x_index_l][y_index_ll] + xinv[2][2]*data[x_index_u][y_index_ll] + xinv[2][3]*data[x_index_uu][y_index_ll];
        d = xinv[3][0]*data[x_index_ll][y_index_ll] + xinv[3][1]*data[x_index_l][y_index_ll] + xinv[3][2]*data[x_index_u][y_index_ll] + xinv[3][3]*data[x_index_uu][y_index_ll];
        double i_yll = a*Math.pow(x, 3) + b*x*x + c*x + d;
        
        //current at x and y
        a = yinv[0][0]*i_yll + yinv[0][1]*i_yl + yinv[0][2]*i_yu + yinv[0][3]*i_yuu;
        b = yinv[1][0]*i_yll + yinv[1][1]*i_yl + yinv[1][2]*i_yu + yinv[1][3]*i_yuu;
        c = yinv[2][0]*i_yll + yinv[2][1]*i_yl + yinv[2][2]*i_yu + yinv[2][3]*i_yuu;
        d = yinv[3][0]*i_yll + yinv[3][1]*i_yl + yinv[3][2]*i_yu + yinv[3][3]*i_yuu;
        double i_xy = a*Math.pow(y, 3) + b*y*y + c*y + d;
        //regularize i_xy
        i_xy = Math.max(0.0, i_xy);
        i_xy = Math.min(1.0, i_xy);
        return i_xy;
    }
    
    /**
     * Fetches the current at the given <code>(x,y)</code> coordinate using the specified interpolation method to evaluate the current.
     * @param x the x-coordinate of the SECM image expressed in the same units as the raw data that was read initially.
     * @param y the y-coordinate of the SECM image expressed in the same units as the raw data that was read initially.
     * @param interpolation_mode the interpolation method to be used. The following options are available:
     * <ul>
     * <li><code>INTERPOLATION_NN</code>: Nearest-neighbor interpolation. (Constant function)</li>
     * <li><code>INTERPOLATION_BILINEAR</code>: Bilinear interpolation. (Linear function)</li>
     * <li><code>INTERPOLATION_BICUBIC</code>: Bicubic interpolation. (Cubic function). <strong>This mode requires an image larger than 5x5 to work</strong></li>
     * </ul>
     * @return The interpolated current at <code>(x,y)</code>.
     * If an invalid <code>interpolation_mode</code> is entered or if an error occurs, <code>Double.NaN</code> will be returned.
     */
    public double getCurrent(double x, double y, int interpolation_mode){
        return getScaledCurrent(x, y, interpolation_mode)*current_amplitude + current_minimum;
    }
    
    /**
     * Fetches the smallest x-coordinate represented by the SECM image's raw data.
     * @return the smallest x-coordinate of this image.
     */
    public double getXMin(){
        return x_coordinates[0];
    }
    
    /**
     * Fetches the largest x-coordinate represented by the SECM image's raw data.
     * @return the largest x-coordinate of this image.
     */
    public double getXMax(){
        return x_coordinates[x_coordinates.length -1];
    }
    
    /**
     * Fetches the smallest y-coordinate represented by the SECM image's raw data.
     * @return the smallest y-coordinate of this image.
     */
    public double getYMin(){
        return y_coordinates[0];
    }
    
    /**
     * Fetches the largest y-coordinate represented by the SECM image's raw data.
     * @return the largest y-coordinate of this image.
     */
    public double getYMax(){
        return y_coordinates[y_coordinates.length -1];
    }
    
    /**
     * Reads SECM data in from a file created by Flux: https://github.com/LaboratoryForBioelectrochemicalImaging/fluxproject
     * <p>Formatted as x,y,current.</p>
     * <p>Lines beginning with <code>#</code> are ignored.</p>
     * @param filepath The path to the file.
     * @throws FileNotFoundException If the file is not found.
     * @throws ImproperFileFormattingException If the data in the file is not rectilinear.
     */
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
                if(Double.isFinite(cur)){
                    if(cur < current_minimum){
                        current_minimum = cur;
                    }
                    if(cur > current_maximum){
                        current_maximum = cur;
                    }
                }
            }
        }
        current_amplitude = current_maximum - current_minimum;
        if(current_amplitude == 0 || !Double.isFinite(current_amplitude)){
            current_amplitude = 1;
        }
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
    
    /**
     * The maximum current. Used to convert between scaled and unscaled current.
     */
    private double current_maximum;
    /**
     * The minimum current. Used to convert between scaled and unscaled current.
     */
    private double current_minimum;
    /**
     * The difference <code>current_maximum - current_minimum</code>. Used to convert between scaled and unscaled current.
     */
    private double current_amplitude;
    /**
     * The scaled SECM data
     */
    private double[][] data;
    /**
     * Holds whether or not the SECM image can be displayed
     */
    private final boolean displayable;
    /**
     * The x-coordinates of the data points of this image
     */
    private double[] x_coordinates;
    /**
     * The y-coordinates of the data points of this image
     */
    private double[] y_coordinates;
    
    //Statics
    /**
     * Nearest neighbor interpolation. Places falling between defined <code>(x,y)</code> 
     * coordinates will have the same current as that for the nearest defined <code>(x,y)</code> 
     * coordinate.
     */
    public static final int INTERPOLATION_NN = 0;
    /**
     * Bilinear interpolation. Interpolates the current linearly along x at defined 
     * <code>(x,y)</code> coordinates just above and below the requested <code>(x,y)</code> 
     * point and then interpolates along y between the two lines along x to obtain 
     * the current at the requested the requested <code>(x,y)</code> point.
     */
    public static final int INTERPOLATION_BILINEAR = 1;
    /**
     * Bicubic interpolation. Interpolates the current along x using cubic functions. 
     * Then interpolates along y between those curves.
     */
    public static final int INTERPOLATION_BICUBIC = 2;
}
