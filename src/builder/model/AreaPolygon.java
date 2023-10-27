package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import java.util.ArrayList;
import org.locationtech.jts.geom.Coordinate;

public class AreaPolygon extends AreaSimple {

    public AreaPolygon(Wincalc winc, GsonElem gson) {
        super(winc, gson, null);
    }
    
    public void setLocation() {
        try {            
            ArrayList<Coordinate> listCoord = new ArrayList();
            listCoord.add(new Coordinate(winc.listFrame.get(0).x1(), winc.listFrame.get(0).y1()));
            winc.listFrame.forEach(line -> listCoord.add(new Coordinate(line.x2(), line.y2())));
            Coordinate[] arrCoord = listCoord.toArray(new Coordinate[0]);
            
            this.geom = gf.createPolygon(arrCoord);

            UGeo.PRINT(this.geom.getCoordinates());
                    
        } catch (Exception e) {
            System.err.println("Ошибка:Area2Polygon.setLocation()" + toString() + e);
        }
    }
    
    public void paint() {
    }
}
