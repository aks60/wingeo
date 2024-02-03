package builder.model;

import builder.Wincalc;
import builder.making.Specific;
import static builder.model.Com5t.gf;
import builder.script.GsonElem;
import common.UCom;
import common.listener.ListenerKey;
import common.listener.ListenerMouse;
import enums.Layout;
import enums.Type;
import frames.swing.draw.Canvas;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

public abstract class ElemSimple extends Com5t {

    public double anglCut[] = {45, 45}; //угол реза
    public double[] anglFlat = {0, 0, 0, 0}; //мин/мах внутренний и мин/мах внешний угол к плоскости   
    public double[] betweenHoriz = {0, 0}; //угол между векторами   
    private java.awt.Point pointPress = null;
    public boolean passEdit[] = {false, false, false, false, false}; //симафор редакт.конструкции
    public final double delta = 3;
    public final double SIZE = 20;
    //public Polygon pmove = null;

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
                if (passEdit[0] == true || passEdit[1] == true) {
                    if (evt.getKeyCode() == KeyEvent.VK_UP) {
                        dY = -.1;
                    } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
                        dY = .1;
                    } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
                        dX = -.1;
                    } else if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
                        dX = .1;
                    }
                    if (passEdit[0] == true) {
                        double X1 = dX / winc.scale + x1();
                        double Y1 = dY / winc.scale + y1();
                        if (X1 >= 0 && X1 <= W / winc.scale && Y1 >= 0 && Y1 <= H / winc.scale) { //контроль выхода за канву
                            x1(X1);
                            y1(Y1);
                        }
                    } else if (passEdit[1] == true) {
                        double X2 = dX / winc.scale + x2();
                        double Y2 = dY / winc.scale + y2();
                        if (X2 >= 0 && X2 <= W / winc.scale && Y2 >= 0 && Y2 <= H / winc.scale) { //контроль выхода за канву
                            x2(X2);
                            y2(Y2);
                        }
                    }
                }
            }
        };
        ListenerMouse mousePressed = (evt) -> {

            if (this.area != null) {
                pointPress = evt.getPoint();
                Coordinate wincPress = new Coordinate((evt.getX() - Canvas.translate[0]) / winc.scale, (evt.getY() - Canvas.translate[1]) / winc.scale);
                boolean b = this.area.contains(gf.createPoint(wincPress));

                //Если клик внутри контура
                if (b == true) {

                    if (passEdit[3] == true) {
                        passEdit[4] = true;
                    } else {
                        passEdit = UCom.getArr(false, false, false, false, false);
                    }
                    LineSegment segm = new LineSegment(this.x1(), this.y1(), this.x2(), this.y2());
                    double coef = segm.segmentFraction(wincPress);
                    if (coef < .33) {
                        passEdit[0] = true; //кликнул ближе к началу вектора

                    } else if (coef > .67) {
                        passEdit[1] = true; //кликнул ближе к концу вектора

                    } else {
                        passEdit[2] = true; //кликнул по середине вектора
                    }
                    winc.canvas.repaint();
                } else {
                    passEdit = UCom.getArr(false, false, false, false, false);
                }
            }
        };
        ListenerMouse mouseReleased = (evt) -> {
            passEdit[3] = !passEdit[3];
        };
        ListenerMouse mouseDragge = (evt) -> {
            if (this.area != null) {
                double W = winc.canvas.getWidth();
                double H = winc.canvas.getHeight();
                double dX = evt.getX() - pointPress.getX(); //прирощение по горизонтали
                double dY = evt.getY() - pointPress.getY(); //прирощение по вертикали 
                if (passEdit[3] == true) {

                    if (passEdit[0] == true) {
                        double X1 = dX / winc.scale + x1();
                        double Y1 = dY / winc.scale + y1();
                        if (X1 > 0) {
                            this.x1(X1);
                            pointPress = evt.getPoint();
                        }
                        if (Y1 > 0) {
                            this.y1(Y1);
                            pointPress = evt.getPoint();
                        }

                    } else if (passEdit[1] == true) {
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
            }
        };

        this.winc.keyboardPressed.add(keyPressed);

        this.winc.mousePressed.add(mousePressed);

        this.winc.mouseReleased.add(mouseReleased);

        this.winc.mouseDragged.add(mouseDragge);
    }

    //Пометка точек редактирования конструкции
    @Override
    public void paint() {
        if (this.area != null) {
            if (this.passEdit[0] == true || this.passEdit[1] == true || this.passEdit[2] == true) {
                this.root.listenerPassEdit = () -> {

                    winc.gc2d.setColor(new java.awt.Color(255, 000, 000));
                    if (this.passEdit[0] == true) {
                        Arc2D arc = new Arc2D.Double(this.x1() - SIZE / 2, this.y1() - SIZE / 2, SIZE, SIZE, 0, 360, Arc2D.OPEN);
                        winc.gc2d.draw(arc);

                    } else if (this.passEdit[1] == true) {
                        Arc2D arc = new Arc2D.Double(this.x2() - SIZE / 2, this.y2() - SIZE / 2, SIZE, SIZE, 0, 360, Arc2D.OPEN);
                        winc.gc2d.draw(arc);

                    } else {
                        if (this.h() != null) {
                            List<Coordinate> list = Arrays.asList(owner.area.getGeometryN(0).getCoordinates()).stream().filter(c -> c.z == this.id).collect(toList());
                            int i = list.size() / 2;
                            Coordinate c1 = list.get(i), c2 = list.get(i + 1);
                            Coordinate smid = new LineSegment(c1.x, c1.y, c2.x, c2.y).midPoint();
                            Rectangle2D rec = new Rectangle2D.Double(smid.x - SIZE / 2, smid.y - SIZE / 2, SIZE, SIZE);
                            winc.gc2d.draw(rec);

                        } else {
                            Coordinate smid = new LineSegment(this.x1(), this.y1(), this.x2(), this.y2()).midPoint();
                            Rectangle2D rec = new Rectangle2D.Double(smid.x - SIZE / 2, smid.y - SIZE / 2, SIZE, SIZE);
                            winc.gc2d.draw(rec);
                        }
                    }
                };
            }
        }
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
        if (passEdit[0] == false && passEdit[1] == false) {
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
