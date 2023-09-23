package builder.model2.xlam;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ShapeSplit
{
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                createAndShowGUI();
            }
        });
    }    

    private static void createAndShowGUI()
    {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new ShapeSplitPanel());
        f.setSize(1100,600);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}

class ShapeSplitPanel extends JPanel implements MouseMotionListener
{
    private Shape inputShape = new Ellipse2D.Double(300,200,200,300);
    private Point2D point0 = new Point2D.Double(200,300);
    private Point2D point1 = new Point2D.Double(600,400);

    ShapeSplitPanel()
    {
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics gr)
    {
        super.paintComponent(gr);
        Graphics2D g = (Graphics2D)gr;
        g.setColor(Color.BLUE);
        g.fill(inputShape);

        g.setColor(Color.BLACK);
        g.draw(new Line2D.Double(point0, point1));
        g.fill(new Ellipse2D.Double(
            point0.getX() - 3, point0.getY()-3, 6, 6));
        g.fill(new Ellipse2D.Double(
            point1.getX() - 3, point1.getY()-3, 6, 6));

        split(new Area(inputShape), point0, point1, g);

    }


    private static Area[] split(Area a, Point2D p0, Point2D p1, Graphics2D g)
    {
        // Compute the direction of the line (L)
        // and its perpendicular (P)
        double dx = p1.getX() - p0.getX();
        double dy = p1.getY() - p0.getY();
        double length = Math.hypot(dx, dy);
        double dirLx = dx / length;
        double dirLy = dy / length;
        double dirPx = -dirLy;
        double dirPy = dirLx;

        // Compute the minimum and maximum of all dot 
        // products that describe the distance of the
        // projection of the corner points of the 
        // bounding box on on the line (L) and its
        // perpendicular (P). These are upper limits
        // for the extents of the object along these
        // directions 
        double minDotL = Double.MAX_VALUE;
        double maxDotL = -Double.MAX_VALUE;
        double minDotP = Double.MAX_VALUE;
        double maxDotP = -Double.MAX_VALUE;
        Rectangle2D bounds = a.getBounds2D();
        for (int i=0; i<4; i++)
        {
            Point2D corner = getCorner(bounds, i);
            double pdx = corner.getX() - p0.getX();
            double pdy = corner.getY() - p0.getY();

            double dotL = dirLx * pdx + dirLy * pdy;
            minDotL = Math.min(minDotL, dotL);
            maxDotL = Math.max(maxDotL, dotL);

            double dotP = dirPx * pdx + dirPy * pdy;
            minDotP = Math.min(minDotP, dotP);
            maxDotP = Math.max(maxDotP, dotP);
        }

        // Compute the start- and end points of 
        // the line segments describing the 
        // extent of the bounds along the line
        // and the perpendicular
        Point2D extentLmin = new Point2D.Double(
            p0.getX() + minDotL * dirLx, 
            p0.getY() + minDotL * dirLy); 

        Point2D extentLmax = new Point2D.Double(
            p0.getX() + maxDotL * dirLx, 
            p0.getY() + maxDotL * dirLy); 

        Point2D extentPmin = new Point2D.Double(
            p0.getX() + minDotP * dirPx, 
            p0.getY() + minDotP * dirPy); 

        Point2D extentPmax = new Point2D.Double(
            p0.getX() + maxDotP * dirPx, 
            p0.getY() + maxDotP * dirPy);

        // Compute the two rectangles that cover
        // each half of the object based on 
        // the given line
        Path2D half0 = new Path2D.Double();
        half0.moveTo(extentLmin.getX(), extentLmin.getY());
        half0.lineTo(
            extentLmin.getX() + minDotP * dirPx, 
            extentLmin.getY() + minDotP * dirPy);
        half0.lineTo(
            extentLmax.getX() + minDotP * dirPx, 
            extentLmax.getY() + minDotP * dirPy);
        half0.lineTo(extentLmax.getX(), extentLmax.getY());
        half0.closePath();

        Path2D half1 = new Path2D.Double();
        half1.moveTo(extentLmin.getX(), extentLmin.getY());
        half1.lineTo(
            extentLmin.getX() + maxDotP * dirPx, 
            extentLmin.getY() + maxDotP * dirPy);
        half1.lineTo(
            extentLmax.getX() + maxDotP * dirPx, 
            extentLmax.getY() + maxDotP * dirPy);
        half1.lineTo(extentLmax.getX(), extentLmax.getY());
        half1.closePath();

        // Compute the resulting areas by intersecting
        // the original area with both halves
        Area a0 = new Area(a);
        a0.intersect(new Area(half0));

        Area a1 = new Area(a);
        a1.intersect(new Area(half1));

        // Debugging output
        if (g != null)
        {
            g.setColor(Color.GRAY);
            g.draw(bounds);

            g.setColor(Color.RED);
            g.draw(new Line2D.Double(extentLmin, extentLmax));

            g.setColor(Color.GREEN);
            g.draw(new Line2D.Double(extentPmin, extentPmax));

            g.setColor(Color.YELLOW.darker());
            g.draw(half0);

            g.setColor(Color.MAGENTA);
            g.draw(half1);

            g.setColor(Color.BLUE);
            g.fill(AffineTransform.getTranslateInstance(400, -20).
                createTransformedShape(a0));

            g.setColor(Color.BLUE);
            g.fill(AffineTransform.getTranslateInstance(400, +20).
                createTransformedShape(a1));
        }
        return new Area[] { a0, a1 };
    }

    private static Point2D getCorner(Rectangle2D r, int corner)
    {
        switch (corner)
        {
            case 0: return new Point2D.Double(r.getMinX(), r.getMinY());
            case 1: return new Point2D.Double(r.getMinX(), r.getMaxY());
            case 2: return new Point2D.Double(r.getMaxX(), r.getMaxY());
            case 3: return new Point2D.Double(r.getMaxX(), r.getMinY());
        }
        return null;
    }



    @Override
    public void mouseDragged(MouseEvent e)
    {
        point1.setLocation(e.getPoint());
        repaint();
    }


    @Override
    public void mouseMoved(MouseEvent e)
    {
    }
}