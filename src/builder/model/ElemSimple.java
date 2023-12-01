package builder.model;

import builder.Wincalc;
import builder.making.Specific;
import builder.script.GsonElem;
import enums.Layout;
import java.awt.Color;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.geom.Coordinate;

public class ElemSimple extends Com5t {

    public double anglCut[] = {45, 45}; //угол реза
    public double[] anglFlat = {0, 0, 0, 0}; //мин/мах внутренний и мин/мах внешний угол к плоскости   
    public double[] betweenHoriz = {0, 0}; //угол между векторами 
    public double anglHoriz = 0; //угол к горизонту 

    public Specific spcRec = null; //спецификация элемента
    public Color borderColor = Color.BLACK;

    public ElemSimple(Wincalc winc, GsonElem gson, AreaSimple owner) {
        this(winc, gson.id, gson, owner);
    }

    public ElemSimple(Wincalc winc, double id, GsonElem gson, AreaSimple owner) {
        super(winc, id, gson, owner);
        spcRec = new Specific(id, this);
        winc.listElem.add(this);
        winc.listAll.add(this);
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
