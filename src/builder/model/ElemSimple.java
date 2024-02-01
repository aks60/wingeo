package builder.model;

import builder.Wincalc;
import builder.making.Specific;
import static builder.model.Com5t.gf;
import builder.script.GsonElem;
import common.listener.ListenerKey;
import common.listener.ListenerMouse;
import enums.Layout;
import enums.Type;
import java.awt.Color;
import java.awt.event.KeyEvent;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Point;

public abstract class ElemSimple extends Com5t {

    public double anglCut[] = {45, 45}; //угол реза
    public double[] anglFlat = {0, 0, 0, 0}; //мин/мах внутренний и мин/мах внешний угол к плоскости   
    public double[] betweenHoriz = {0, 0}; //угол между векторами   
    private java.awt.Point pointPress = null;
    private boolean pass[] = {false, false};
    private final double delta = 3;

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

    //Test
    public ElemSimple(double id, GsonElem gson) {
        super(id, gson);
    }

    public abstract void initArtikle();

    public abstract void setLocation();

    public abstract void setSpecific();

    public abstract void addSpecific(Specific spcAdd);

    public void addEvents() {

        ListenerKey keyPressed = (evt) -> {
            if (this.area != null) {
                double W = winc.canvas.getWidth();
                double H = winc.canvas.getHeight();
                double dX = 0;
                double dY = 0;
                if (pass[0] == true || pass[1] == true) {
                    if (evt.getKeyCode() == KeyEvent.VK_UP) {
                        dY = -.1;
                    } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
                        dY = .1;
                    } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
                        dX = -.1;
                    } else if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
                        dX = .1;
                    }
                    if (pass[0] == true) {
                        double X1 = dX / winc.scale + x1();
                        double Y1 = dY / winc.scale + y1();
                        if (X1 >= 0 && X1 <= W / winc.scale && Y1 >= 0 && Y1 <= H / winc.scale) { //контроль выхода за канву
                            x1(X1);
                            y1(Y1);
                            //pointPress.setLocation(x1(), y1());
                        }
                    } else if (pass[1] == true) {
                        double X2 = dX / winc.scale + x2();
                        double Y2 = dY / winc.scale + y2();
                        if (X2 >= 0 && X2 <= W / winc.scale && Y2 >= 0 && Y2 <= H / winc.scale) { //контроль выхода за канву
                            x2(X2);
                            y2(Y2);
                            //pointPress.setLocation(x2(), y2());
                        }
                    }
                }
            }
        };
        ListenerMouse mousePressed = (evt) -> {

            if (this.area != null) {
                pointPress = evt.getPoint();
                Coordinate wincPress = new Coordinate(evt.getX() / winc.scale, evt.getY() / winc.scale);
                boolean b = this.area.contains(gf.createPoint(wincPress));

                //Если клик внутри контура
                if (b == true) {
                    LineSegment segm = new LineSegment(this.x1(), this.y1(), this.x2(), this.y2());
                    double coef = segm.segmentFraction(wincPress);
                    if (coef < .33) {
                        pass[0] = true; //кликнул ближе к началу вектора
                    } else if (coef > .67) {
                        pass[1] = true; //кликнул ближе к концу вектора
                    }
                }
            }
        };
        ListenerMouse mouseReleased = (evt) -> {

            pass[0] = false;
            pass[1] = false;
        };
        ListenerMouse mouseDragge = (evt) -> {
            if (this.area != null) {
                double W = winc.canvas.getWidth();
                double H = winc.canvas.getHeight();
                double dX = evt.getX() - pointPress.getX(); //прирощение по горизонтали
                double dY = evt.getY() - pointPress.getY(); //прирощение по вертикали 

                if (pass[0] == true) {
                    double X1 = dX / winc.scale + x1();
                    double Y1 = dY / winc.scale  + y1();
                    if (X1 > 0) {
                        this.x1(X1);
                        pointPress = evt.getPoint();
                    }
                    if (Y1 > 0) {
                        this.y1(Y1);
                        pointPress = evt.getPoint();
                    }

                } else if (pass[1] == true) {
                    double X2 = dX / winc.scale + x2();
                    double Y2 = dY / winc.scale + y2();
                    if (X2 > 0) {
                        this.x2(X2);
                        pointPress = evt.getPoint();
                    }
                    if (Y2 > 0) {
                        this.y2(Y2);
                        pointPress = evt.getPoint();
                    }
                }
            }
        };

        this.winc.keyboardPressed.add(keyPressed);

        this.winc.mousePressed.add(mousePressed);

        this.winc.mouseReleased.add(mouseReleased);

        this.winc.mouseDragged.add(mouseDragge);
    }

    /**
     * Определяет прилегающий элемент по точке принадлежащей вектору. Прил.
     * соед. используется для определения координат примыкаемого элемента. (см.
     * ElemXxx.setSpecific())
     *
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

    public void setDimension(double x1, double y1, double x2, double y2) {
        if (pass[0] == false && pass[1] == false) {
            gson.x1 = x1;
            gson.y1 = y1;
            gson.x2 = x2;
            gson.y2 = y2;
        }
    }

    @Override
    public String toString() {
        return super.toString() + ", anglHoriz=" + anglHoriz();
    }
}
