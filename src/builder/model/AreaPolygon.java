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
            for (int i = 0; i < winc.listFrame.size(); i++) {
                if (i + 1 < winc.listFrame.size()) {
                    winc.listFrame.get(i).enext = winc.listFrame.get(i + 1);
                } else {
                    winc.listFrame.get(i).enext = winc.listFrame.get(0);
                }
            }
            ArrayList<Coordinate> listCoord = new ArrayList<Coordinate>();
            winc.listFrame.forEach(line -> listCoord.add(new Coordinate(line.x1(), line.y1())));
            listCoord.add(new Coordinate(winc.listFrame.get(0).x1(), winc.listFrame.get(0).y1()));            
            Coordinate[] arrCoord = listCoord.toArray(new Coordinate[0]);

            this.geom = gf.createPolygon(arrCoord);                        

        } catch (Exception e) {
            System.err.println("Ошибка:Area2Polygon.setLocation()" + toString() + e);
        }
    }

    public void paint() {
    }
}
