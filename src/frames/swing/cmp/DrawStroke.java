package frames.swing.cmp;

import builder.Wincalc;
import builder.Wincalc;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

public class DrawStroke {

    public static void drawLine(Wincalc winc, double x1, double y1, double x2, double y2) {
        //winc.gc2d.drawLine((int) (x1), (int) (y1), (int) (x2), (int) (y2));
        winc.gc2d.draw(new Line2D.Double(x1, y1, x2, y2));
    }

//    public static void strokePolygon(Wincalc winc, double x1, double x2, double x3, double x4,
//            double y1, double y2, double y3, double y4, int rgbFill, Color rgbStroke) {
//
//        if (rgbStroke == Color.RED) {
//            winc.gc2D.setStroke(new BasicStroke(6)); //толщина линии 
//        } else {
//            winc.gc2D.setStroke(new BasicStroke(1)); //толщина линии 
//        }
//        winc.gc2D.setColor(new java.awt.Color(rgbFill));
//        winc.gc2D.fillPolygon(new int[]{(int) x1, (int) x2, (int) x3, (int) x4},
//                new int[]{(int) y1, (int) y2, (int) y3, (int) y4}, 4);
//        winc.gc2D.setColor(rgbStroke);
//        winc.gc2D.drawPolygon(new int[]{(int) (x1), (int) (x2), (int) (x3), (int) (x4)},
//                new int[]{(int) y1, (int) y2, (int) y3, (int) y4}, 4);
//    }

    public static void strokePolygon(Wincalc winc, double x1, double x2, double x3, double x4,
            double y1, double y2, double y3, double y4, int rgbFill, Color rgbStroke) {

        if (rgbStroke == Color.RED) {
            winc.gc2d.setStroke(new BasicStroke(6)); //толщина линии 
        } else {
            winc.gc2d.setStroke(new BasicStroke(1)); //толщина линии 
        }
        winc.gc2d.setColor(new java.awt.Color(rgbFill));
        winc.gc2d.fillPolygon(new int[]{(int) x1, (int) x2, (int) x3, (int) x4},
                new int[]{(int) y1, (int) y2, (int) y3, (int) y4}, 4);
        winc.gc2d.setColor(rgbStroke);
        winc.gc2d.drawPolygon(new int[]{(int) (x1), (int) (x2), (int) (x3), (int) (x4)},
                new int[]{(int) y1, (int) y2, (int) y3, (int) y4}, 4);
    }

    public static void strokeArc(Wincalc winc, double x, double y, double w, double h,
            double startAngle, double arcExtent, int rgbStroke, double lineWidth) {
        int sw = (int) (lineWidth);
        winc.gc2d.setStroke(new BasicStroke(sw)); //толщина линии
        winc.gc2d.setColor(new java.awt.Color(rgbStroke));
        winc.gc2d.drawArc((int) (x), (int) (y), (int) (w), (int) (h), (int) startAngle, (int) arcExtent);
    }
}
