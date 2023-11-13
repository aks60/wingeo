package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import enums.Layout;
import enums.LayoutJoin;
import enums.TypeJoin;
import java.util.ArrayList;
import org.locationtech.jts.geom.Coordinate;

public class AreaRectangl extends AreaSimple {

    public AreaRectangl(Wincalc winc, GsonElem gson) {
        super(winc, gson, null);
    }

    public AreaRectangl(Wincalc winc) {
        super(winc);
        setDimension(0, 0, winc.gson.width(), winc.gson.height());
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

    //Угловые соединения
    @Override
    public void joining() {

        super.joining(); //T - соединения

        ElemSimple elemBott = frames.get(Layout.BOTT), elemRight = frames.get(Layout.RIGHT),
                elemTop = frames.get(Layout.TOP), elemLeft = frames.get(Layout.LEFT);

        //Угловое соединение правое нижнее
        winc.listJoin.add(new ElemJoining(winc, TypeJoin.VAR20, LayoutJoin.ANGL, elemBott, elemRight));
        //Угловое соединение правое верхнее
        winc.listJoin.add(new ElemJoining(winc, TypeJoin.VAR20, LayoutJoin.ANGL, elemRight, elemTop));
        //Угловое соединение левое верхнее    
        winc.listJoin.add(new ElemJoining(winc, TypeJoin.VAR20, LayoutJoin.ANGL, elemTop, elemLeft));
        //Угловое соединение левое нижнее
        winc.listJoin.add(new ElemJoining(winc, TypeJoin.VAR20, LayoutJoin.ANGL, elemLeft, elemBott));
    }
    
    public void paint() {
    }
    
    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    // </editor-fold>     
}
