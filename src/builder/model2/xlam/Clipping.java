package builder.model2.xlam;

import builder.model2.UGeo;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Clipping {
    
    public static GeneralPath clipping(Graphics2D gc2D, Point2D p1, Point2D p2, boolean side) {

        GeneralPath clipPath = new GeneralPath();
        Rectangle2D r = gc2D.getClipBounds().getBounds2D();
        List<Point2D> p3 = null;
        if (p1.getX() == p2.getX()) {
            p3 = List.of(new Point2D.Double(r.getX(), r.getY()), new Point2D.Double(p2.getX(), r.getY()),
                    new Point2D.Double(p2.getX(), r.getHeight()), new Point2D.Double(r.getX(), r.getHeight()));

        } else if (p1.getY() == p2.getY()) {
            p3 = List.of(new Point2D.Double(r.getX(), r.getY()), new Point2D.Double(r.getWidth(), r.getY()),
                    new Point2D.Double(r.getWidth(), p2.getY()), new Point2D.Double(r.getX(), p2.getY()));

        } else {
            Point2D px1[] = {new Point2D.Double(r.getX(), r.getY()), new Point2D.Double(r.getWidth(), r.getY()), p1, p2};
            Point2D px2[] = {new Point2D.Double(r.getX(), r.getHeight()), new Point2D.Double(r.getWidth(), r.getHeight()), p1, p2};
            Point2D py1[] = {new Point2D.Double(r.getX(), r.getY()), new Point2D.Double(r.getX(), r.getHeight()), p1, p2};
            Point2D py2[] = {new Point2D.Double(r.getWidth(), r.getY()), new Point2D.Double(r.getWidth(), r.getHeight()), p1, p2};

            double cx1[] = cross2(px1[0], px1[1], px1[2], px1[3]);
            double cx2[] = cross2(px2[0], px2[1], px2[3], px2[2]);
            double cy1[] = cross2(py1[0], py1[1], py1[2], py1[3]);
            double cy2[] = cross2(py2[0], py2[1], py2[3], py2[2]);

            if (cx1[0] > 0) {
                p3 = List.of(new Point2D.Double(cy1[0], cy1[1]), new Point2D.Double(cy2[0], cy2[1]), new Point2D.Double(0, cy2[1]));
            } else {
                p3 = List.of(new Point2D.Double(cx1[0], cx1[1]), new Point2D.Double(cx2[0], cx2[1]), new Point2D.Double(cx1[0], cx2[1]));
            }
        }

        clipPath.moveTo(p3.get(0).getX(), p3.get(0).getY());
        for (int i = 1; i < p3.size(); i++) {
            clipPath.lineTo(p3.get(i).getX(), p3.get(i).getY());
        }
        clipPath.closePath();

        if (side == false) {
            Area sceneArea = new Area(r);
            Area clipArea = new Area(clipPath);
            sceneArea.subtract(clipArea);
            return generalPath(sceneArea);  
        }
        return clipPath;
    }

    public static GeneralPath generalPath(Area shape) {
        final double[] dbl = new double[6];
        List<Point2D> p = new ArrayList();
        PathIterator iterator = shape.getPathIterator(null);
        while (!iterator.isDone()) {
            final int segmentType = iterator.currentSegment(dbl);
            if (segmentType == PathIterator.SEG_LINETO) {
                p.add(new Point2D.Double(dbl[0], dbl[1]));
            } else if (segmentType == PathIterator.SEG_MOVETO) {
                p.add(new Point2D.Double(dbl[0], dbl[1]));
            } else if (segmentType == PathIterator.SEG_CLOSE) {
                p.add(new Point2D.Double(dbl[0], dbl[1]));
            }
            iterator.next();
        }
        GeneralPath genPath = new GeneralPath();
        genPath.moveTo(p.get(0).getX(), p.get(0).getY());
        for (int i = 1; i < p.size(); ++i) {
            genPath.lineTo(p.get(i).getX(), p.get(i).getY());
        }
        genPath.closePath();
        return genPath;
    }

    //Точка пересечения двух векторов 
    public static double[] cross2(Point2D A, Point2D B, Point2D C, Point2D D) {

        // Line AB represented as a1x + b1y = c1
        double a1 = B.getY() - A.getY();
        double b1 = A.getX() - B.getX();
        double c1 = a1 * (A.getX()) + b1 * (A.getY());

        // Line CD represented as a2x + b2y = c2
        double a2 = D.getY() - C.getY();
        double b2 = C.getX() - D.getX();
        double c2 = a2 * (C.getX()) + b2 * (C.getY());

        double determinant = a1 * b2 - a2 * b1;

        if (determinant == 0) {
            // The lines are parallel. This is simplified
            // by returning a pair of FLT_MAX
            return new double[]{Integer.MAX_VALUE, Integer.MAX_VALUE};
        } else {
            double x = (b2 * c1 - b1 * c2) / determinant;
            double y = (a1 * c2 - a2 * c1) / determinant;
            return new double[]{x, y};
        }
    }
 
}
