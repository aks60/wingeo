package builder.model2;

import builder.Wincalc;
import builder.script.GeoElem;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AreaSimple extends Com5t {

    public Area area2 = null;
    public LinkedList<Point2D> listSkin = new LinkedList();
    public List<Com5t> childs = new ArrayList(); //дети

    public AreaSimple(Wincalc wing, GeoElem gson, Com5t owner) {
        super(wing, gson, owner);
    }

    @Override
    public List<Com5t> childs() {
        return childs;
    }

//    public void paint() {
//        try {
//            if (area != null) {
//                wing.gc2D.draw(area);
//            }
//        } catch (Exception e) {
//            System.err.println("Ошибка:Area2Simple.paint()" + toString() + e);
//        }
//    }

//    public void mouseEvent() {
//
//    }
}
