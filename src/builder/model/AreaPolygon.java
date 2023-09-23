package builder.model;

import builder.Wincalc;
import builder.script.GeoElem;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;

public class AreaPolygon extends AreaSimple {

    public AreaPolygon(Wincalc wing, GeoElem gson) {
        super(wing, gson, null);
    }

    public void setLocation() {
        try {
            GeneralPath p = new GeneralPath();
            p.reset();
            p.moveTo((float) wing.listFrame.get(0).x1(), (float) wing.listFrame.get(0).y1());
            wing.listFrame.get(0).enext = wing.listFrame.get(1);
            for (int i = 1; i < wing.listFrame.size(); ++i) {
                p.lineTo((float) wing.listFrame.get(i).x1(), (float) wing.listFrame.get(i).y1());
                wing.listFrame.get(i).enext = (i + 1 == wing.listFrame.size()) ? wing.listFrame.get(0) : wing.listFrame.get(i + 1);
            }
            p.closePath();
            area = new Area(p);

        } catch (Exception e) {
            System.err.println("Ошибка:Area2Polygon.setLocation()" + toString() + e);
        }
    }

    public void setLocation2() {

        List<Coordinate> listCoord = new ArrayList();
        for (ElemFrame frame : wing.listFrame) {
            listCoord.add(new Coordinate(frame.x1(), frame.y1()));
        }
        listCoord.add(new Coordinate(wing.listFrame.get(0).x1(), wing.listFrame.get(0).y1()));
        
        Coordinate[] coordinates = new Coordinate[listCoord.size()];
        listCoord.toArray(coordinates);
        AREA = wing.geomFact.createPolygon(coordinates);
        ShapeWriter sw = new ShapeWriter();
        Shape polyShape = sw.toShape(AREA);
        area = new Area(polyShape);
        
    }

    public void paint() {
     //   wing.gc2D.draw(area);
//        Area area1 = UGeo.area(0, 0, 0, 900, 600, 800, 0, 0);
//        UGeo.PRINT("", area1);
//
//        Area area2 = new Area(new Rectangle(0, 0, 200, 900));
//        area1.intersect(area2);
//        UGeo.PRINT("", area1);
//        
//        wing.gc2D.draw(area1);
//        wing.gc2D.draw(area2);

//        try {
//            wing.gc2D.draw(area);
//            
//            if (wing.listCross.isEmpty() == false) {
//                Elem2Cross cros = wing.listCross.get(0);
//
//                Area area2[] = UGeo.split(area, cros);
//                double line[] = UGeo.cross(area2);
//                if (line != null) {
//                    cros.setLocation(line[0], line[1], line[2], line[3]);
//                }
//                wing.gc2D.draw(new Line2D.Double(cros.x1(), cros.y1(), cros.x2(), cros.y2()));
//            }
//        } catch (Exception e) {
//            System.err.println("Ошибка:Area2Simple.paint()" + toString() + e);
//        }
    }
}
