package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import enums.Form;
import enums.Layout;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public class AreaSimple extends Com5t {
    public Form form = null; //форма контура (параметр в развитии)
    public EnumMap<Layout, ElemSimple> frames = new EnumMap<>(Layout.class); //список рам в окне 
    public Area area2 = null;
    public LinkedList<Point2D> listSkin = new LinkedList();
    public List<Com5t> childs = new ArrayList(); //дети

    public AreaSimple(Wincalc wing, GsonElem gson, AreaSimple owner) {
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
