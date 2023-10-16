package frames.swing.draw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Drawtest {

    private JFrame frame;
    private Polygon poly;

    public Drawtest() {

        initComponents();

    }

    private void initComponents() {

        frame = new JFrame();
        frame.setResizable(false);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //int xPoly[] = {150, 250, 325, 375, 450, 275, 100};
        //int yPoly[] = {150, 100, 125, 225, 250, 375, 300};
        //poly = new Polygon(xPoly, yPoly, xPoly.length);
        
        Area a = new Area(new Polygon(new int[]{0, 100, 200}, new int[]{200, 0, 200}, 3));
        Area b = new Area(new Polygon(new int[]{0, 200, 400}, new int[]{0, 200, 0}, 3));
        //b.subtract(a);
        b.intersect(a);
        List<Area> ar = getAreas(b);

        JPanel p = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g.setColor(Color.BLUE);
                //g2.draw(ar.get(0));
                //g2.draw(ar.get(1));
                //g2.draw(a);
                g2.draw(b);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 600);
            }
        };
        frame.add(p);
        frame.pack();
        frame.setVisible(true);

    }

    public static List<Area> getAreas(Area ar) {
        PathIterator iter = ar.getPathIterator(null);
        List<Area> areas = new ArrayList<Area>();
        Path2D.Float poly = new Path2D.Float();
        Point2D.Float start = null;

        while (!iter.isDone()) {
            float point[] = new float[2]; //x,y
            int type = iter.currentSegment(point);

            if (type == PathIterator.SEG_MOVETO) {
                poly.moveTo(point[0], point[1]);

            } else if (type == PathIterator.SEG_CLOSE) {
                areas.add(new Area(poly));
                poly.reset();

            } else {
                poly.lineTo(point[0], point[1]);
            }
            iter.next();
        }
        return areas;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Drawtest();
            }
        });
    }
}
