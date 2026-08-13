/*
 * The MIT License
 *
 * Copyright 2026 Nathaniel Leslie.
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

import java.awt.geom.Point2D;
import java.util.LinkedList;

/**
 *
 * @author nathaniel
 */
public class ConvexPolygon {
    
    public static ConvexPolygon getIntersection(ConvexPolygon poly1, ConvexPolygon poly2){
        LinkedList<Point2D.Double> new_vertex_list = new LinkedList<>();
        for(int i = 0; i < poly1.vertices.length; i++){
            if(poly2.inPolygon(poly1.vertices[i], true)){
                Point2D.Double newpoint = new Point2D.Double(poly1.vertices[i].getX(), poly1.vertices[i].getY());
                new_vertex_list.add(newpoint);
            }
        }
        for(int i = 0; i < poly2.vertices.length; i++){
            if(poly1.inPolygon(poly2.vertices[i], true)){
                Point2D.Double newpoint = new Point2D.Double(poly2.vertices[i].getX(), poly2.vertices[i].getY());
                new_vertex_list.add(newpoint);
            }
        }
    }
    
    public Point2D.Double getMeanVertex(){
        double xsum = 0.0;
        double ysum = 0.0;
        for(int i = 0; i < vertices.length; i++){
            xsum += vertices[i].getX();
            ysum += vertices[i].getY();
        }
        double size = vertices.length;
        return new Point2D.Double(xsum/size, ysum/size);
    }
    
    public boolean inPolygon(Point2D.Double point, boolean include_edges){
        int n = vertices.length;
        if(include_edges){
            LineSegment edge = new LineSegment(vertices[n-1].getX(), vertices[n-1].getY(), vertices[0].getX(), vertices[0].getY());
            if(LineSegment.pointOnSegment(edge, point.getX(), point.getY(), false)){
                return true;
            }
            for(int i = 1; i < vertices.length; i++){
                edge = new LineSegment(vertices[i-1].getX(), vertices[i-1].getY(), vertices[i].getX(), vertices[i].getY());
                if(LineSegment.pointOnSegment(edge, point.getX(), point.getY(), false)){
                    return true;
                }
            }
        }
        Point2D.Double mean_vertex = this.getMeanVertex();
        LineSegment p2mv = new LineSegment(mean_vertex.getX(), mean_vertex.getY(), point.getX(), point.getY());
        LineSegment edge = new LineSegment(vertices[n-1].getX(), vertices[n-1].getY(), vertices[0].getX(), vertices[0].getY());
        Point2D.Double intercept = LineSegment.findIntersection(p2mv, edge);
        if(!Double.isNaN(intercept.getX()) || LineSegment.pointOnSegment(p2mv, vertices[0].getX(), vertices[0].getY(), false)){
            return false;
        }
        
        for(int i = 1; i < vertices.length; i++){
            edge = new LineSegment(vertices[i-1].getX(), vertices[i-1].getY(), vertices[i].getX(), vertices[i].getY());
            intercept = LineSegment.findIntersection(p2mv, edge);
            if(!Double.isNaN(intercept.getX()) || LineSegment.pointOnSegment(p2mv, vertices[i].getX(), vertices[i].getY(), false)){
                return false;
            }
        }
        return true;

    }
    
    private Point2D.Double[] vertices;
}
