package builder.model;

import builder.Wincalc;
import builder.making.Specific;
import builder.script.GsonElem;
import java.awt.Color;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.geom.Coordinate;

public class ElemSimple extends Com5t {

    public double anglCut[] = {45, 45}; //угол реза
    public double[] anglFlat = {0, 0, 0, 0}; //мин/мах внутренний и мин/мах внешний угол к плоскости   
    public double[] betweenHoriz = {0, 0}; //угол между векторами    

    public Specific spcRec = null; //спецификация элемента
    public Color borderColor = Color.BLACK;

    public ElemSimple(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
        winc.listElem.add(this);
        winc.listAll.add(this);        
        spcRec = new Specific(id, this);
    }

    public void setLocation() {
//        try {
//            GeneralPath p = new GeneralPath();
//            p.reset();
//            p.moveTo(winc.listFrame.get(0).x1(), winc.listFrame.get(0).y1());
//            for (int i = 1; i < winc.listFrame.size(); ++i) {
//                p.lineTo(winc.listFrame.get(i).x1(), winc.listFrame.get(i).y1());
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
    
    //Угол к горизонту 
    public double anglHoriz() {
        return Angle.toDegrees(Angle.angle(new Coordinate(this.x1(), this.y1()), new Coordinate(this.x2(), this.y2())));
    }
    
    @Override
    public String toString() {
        return super.toString() + ", anglHoriz=" + anglHoriz();
    }
}
