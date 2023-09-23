package builder.model;

import builder.Wincalc;
import builder.making.Specific;
import builder.script.GeoElem;
import domain.eArtikl;
import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class ElemSimple extends Com5t {

    public double anglCut[] = {45, 45}; //угол реза
    public double[] anglFlat = {0, 0, 0, 0}; //мин/мах внутренний и мин/мах внешний угол к плоскости
    public double anglHoriz = 0; //угол к горизонту    
    public double[] betweenHoriz = {0, 0}; //угол между векторами    

    public Specific spcRec = null; //спецификация элемента
    public Color borderColor = Color.BLACK;

    public ElemSimple(Wincalc wing, GeoElem gson, Com5t owner) {
        super(wing, gson, owner);
        //spcRec = new Specific(id, this);
    }

    public void setLocation() {
//        try {
//            GeneralPath p = new GeneralPath();
//            p.reset();
//            p.moveTo(wing.listFrame.get(0).x1(), wing.listFrame.get(0).y1());
//            for (int i = 1; i < wing.listFrame.size(); ++i) {
//                p.lineTo(wing.listFrame.get(i).x1(), wing.listFrame.get(i).y1());
//            }
//            p.closePath();
//            area = new Area(p);
//
//        } catch (Exception e) {
//            System.err.println("Ошибка:Elem2Simple.build()" + toString() + e);
//        }        
    }

    public void addSpecific(Specific spcAdd) {
        
    }
    
    @Override
    public String toString() {
        return super.toString() + ", anglHoriz=" + anglHoriz;
    }
}
