package builder.model;

import builder.Wincalc;
import builder.making.Specific;
import builder.script.GsonElem;
import enums.Layout;
import enums.Type;
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
    
    /**
     * Определяет прилегающий элемент по точке принадлежащей вектору. Прил. соед. используется для
     * определения координат примыкаемого элемента. (см. ElemXxx.setSpecific())
     * @param side - сторона прилегания
     * @return - элемент прилегания
     */
    //@Override
    public ElemSimple joinFlat(Layout side) {
        boolean begin = false;
        try {
            //Цикл по элементам кострукции
            for (int index = winc.listElem.size() - 1; index >= 0; --index) {
                ElemSimple el = (ElemSimple) winc.listElem.get(index);

                if (begin == true && el.type != Type.GLASS) {
                    //Проверка начинает выполняться после появления в обратном цикле самого элемента(this) 
                    if (Layout.BOTT == side && el.layout() != Layout.VERT) {
                        double Y2 = (y2() > y1()) ? y2() : y1();
                        if (el.inside(x1() + (x2() - x1()) / 2, Y2) == true) {
                            return (ElemSimple) el;
                        }
                    } else if (Layout.LEFT == side && el.layout() != Layout.HORIZ) {
                        if (el.inside(x1(), y1() + (y2() - y1()) / 2) == true) {
                            return (ElemSimple) el;
                        }
                    } else if (Layout.TOP == side && el.layout() != Layout.VERT) {
                        double Y1 = (y2() > y1()) ? y1() : y2();
                        if (el.inside(x1() + (x2() - x1()) / 2, Y1) == true && (el.owner.type == Type.ARCH && el.layout() == Layout.TOP) == false) {
                            return (ElemSimple) el;
                        }
                    } else if (Layout.RIGHT == side && el.layout() != Layout.HORIZ) {
                        if (el.inside(x2(), y1() + (y2() - y1()) / 2)) {
                            return (ElemSimple) el;
                        }
                    }
                }
                if (this == el) {
                    begin = true;
                }
            }
            System.err.println("Неудача:ElemSimple.joinFlat() id=" + this.id + ", " + side + " соединение не найдено");
            return null;

        } catch (Exception e) {
            System.err.println("Ошибка:IElem5e.joinFlat() " + e);
            return null;
        }
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
