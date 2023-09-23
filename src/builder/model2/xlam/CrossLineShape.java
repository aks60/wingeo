package builder.model2.xlam;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

//https://www.bilee.com/java-%D1%82%D0%BE%D1%87%D0%BA%D0%B0-%D0%BF%D0%B5%D1%80%D0%B5%D1%81%D0%B5%D1%87%D0%B5%D0%BD%D0%B8%D1%8F-%D0%BC%D0%BD%D0%BE%D0%B3%D0%BE%D1%83%D0%B3%D0%BE%D0%BB%D1%8C%D0%BD%D0%B8%D0%BA%D0%B0-%D0%B8.html
public class CrossLineShape {

    public static void main(String[] args) throws Exception {
        
//        final Polygon poly = new Polygon(new int[]{1, 2, 2, 1}, new int[]{1, 1, 2, 2}, 4);
//        final Line2D.Double line = new Line2D.Double(2.5, 1.3, 1.3, 2.5);
//        final Set intersections = getIntersections(poly., line);
//        for (Iterator it = intersections.iterator(); it.hasNext();) {
//            final Point2D point = (Point2D) it.next();
//            System.out.println("Intersection: " + point.toString());
//        }
    }

    public static Point2D[] getIntersections(final Shape poly, final Line2D.Double line) { //throws Exception {
        
        final PathIterator polyIt = poly.getPathIterator(null); //Getting an iterator along the polygon path 
        final double[] coords = new double[6]; //Double array with length 6 needed by iterator 
        final double[] firstCoords = new double[2]; //First point (needed for closing polygon path) 
        final double[] lastCoords = new double[2]; //Previously visited point 
        final Set<Point2D> intersections = new HashSet(); //List to hold found intersections 
        polyIt.currentSegment(firstCoords); //Getting the first coordinate pair 
        lastCoords[0] = firstCoords[0]; //Priming the previous coordinate pair 
        lastCoords[1] = firstCoords[1];
        polyIt.next();
        while (!polyIt.isDone()) {
            final int type = polyIt.currentSegment(coords);
            switch (type) {
                case PathIterator.SEG_LINETO: {
                    final Line2D.Double currentLine = new Line2D.Double(lastCoords[0], lastCoords[1], coords[0], coords[1]);
                    if (currentLine.intersectsLine(line)) {
                        intersections.add(getIntersection(currentLine, line));
                    }
                    lastCoords[0] = coords[0];
                    lastCoords[1] = coords[1];
                    break;
                }
                case PathIterator.SEG_CLOSE: {
                    final Line2D.Double currentLine = new Line2D.Double(coords[0], coords[1], firstCoords[0], firstCoords[1]);
                    if (currentLine.intersectsLine(line)) {
                        intersections.add(getIntersection(currentLine, line));
                    }
                    break;
                }
                default: {
                    return null;
                    //throw new Exception("Unsupported PathIterator segment type.");
                }
            }
            polyIt.next();
        }
        return intersections.toArray(new Point2D[0]);
        //return intersections;
    }

    public static Point2D getIntersection(final Line2D.Double line1, final Line2D.Double line2) {
        final double x1, y1, x2, y2, x3, y3, x4, y4;
        x1 = line1.x1;
        y1 = line1.y1;
        x2 = line1.x2;
        y2 = line1.y2;
        x3 = line2.x1;
        y3 = line2.y1;
        x4 = line2.x2;
        y4 = line2.y2;
        final double x = ((x2 - x1) * (x3 * y4 - x4 * y3) - (x4 - x3) * (x1 * y2 - x2 * y1)) / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
        final double y = ((y3 - y4) * (x1 * y2 - x2 * y1) - (y1 - y2) * (x3 * y4 - x4 * y3)) / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
        return new Point2D.Double(x, y);
    }
}
