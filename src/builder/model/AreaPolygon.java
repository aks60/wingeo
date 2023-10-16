package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;

public class AreaPolygon extends AreaSimple {

    public AreaPolygon(Wincalc winc, GsonElem gson) {
        super(winc, gson, null);
    }

    public void setLocation() {
        try {
            GeneralPath p = new GeneralPath();
            p.moveTo((float) winc.listFrame.get(0).x1(), (float) winc.listFrame.get(0).y1());
            winc.listFrame.get(0).enext = winc.listFrame.get(1);
            for (int i = 1; i < winc.listFrame.size(); ++i) {
                p.lineTo((float) winc.listFrame.get(i).x1(), (float) winc.listFrame.get(i).y1());
                winc.listFrame.get(i).enext = (i + 1 == winc.listFrame.size()) ? winc.listFrame.get(0) : winc.listFrame.get(i + 1);
            }
            p.closePath();
            area = new Area(p);

        } catch (Exception e) {
            System.err.println("Ошибка:Area2Polygon.setLocation()" + toString() + e);
        }
    }
    
    public void paint() {
     //   winc.gc2D.draw(area);
//        Area area1 = UGeo.area(0, 0, 0, 900, 600, 800, 0, 0);
//        UGeo.PRINT("", area1);
//
//        Area area2 = new Area(new Rectangle(0, 0, 200, 900));
//        area1.intersect(area2);
//        UGeo.PRINT("", area1);
//        
//        winc.gc2D.draw(area1);
//        winc.gc2D.draw(area2);

//        try {
//            winc.gc2D.draw(area);
//            
//            if (winc.listCross.isEmpty() == false) {
//                Elem2Cross cros = winc.listCross.get(0);
//
//                Area area2[] = UGeo.split(area, cros);
//                double line[] = UGeo.cross(area2);
//                if (line != null) {
//                    cros.setLocation(line[0], line[1], line[2], line[3]);
//                }
//                winc.gc2D.draw(new Line2D.Double(cros.x1(), cros.y1(), cros.x2(), cros.y2()));
//            }
//        } catch (Exception e) {
//            System.err.println("Ошибка:Area2Simple.paint()" + toString() + e);
//        }
    }
}
