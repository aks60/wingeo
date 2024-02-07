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
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.swing.Timer;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

public abstract class ElemSimple extends Com5t {

    public double anglCut[] = {45, 45}; //угол реза
    public double[] anglFlat = {0, 0, 0, 0}; //мин/мах внутренний и мин/мах внешний угол к плоскости   
    public double[] betweenHoriz = {0, 0}; //угол между векторами   
    private java.awt.Point pointPress = null;
    public int passMask[] = {0, 0}; //маска редактир.конструкции
    public final double delta = 3;
    public final double SIZE = 20;
    private Timer timer = new Timer(160, (evt) -> {
    });

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
        timer.setRepeats(false);

        ListenerKey keyPressed = (evt) -> {
            if (this.area != null && passMask[1] > 0) {
                LineSegment segm = new LineSegment(this.x1(), this.y1(), this.x2(), this.y2());
                int key = evt.getKeyCode();
                double dxy = (timer.isRunning() == true) ? 0.14 + winc.scale : 0.1 * winc.scale;
                double dX = 0, dY = 0;

                if (key == KeyEvent.VK_UP) {
                    dY = -dxy;
                } else if (key == KeyEvent.VK_DOWN) {
                    dY = dxy;
                } else if (key == KeyEvent.VK_LEFT) {
                    dX = -dxy;
                } else if (key == KeyEvent.VK_RIGHT) {
                    dX = dxy;
                }
                if (passMask[0] == 0) {
                    double X1 = dX / winc.scale + this.x1();
                    double Y1 = dY / winc.scale + this.y1();
                    if (X1 >= 0 && X1 <= winc.canvas.getWidth() / winc.scale && Y1 >= 0
                            && Y1 <= winc.canvas.getHeight() / winc.scale) { //контроль выхода за канву
                        this.x1(X1);
                        this.y1(Y1);
                    }
                } else if (passMask[0] == 1) {
                    double X2 = dX / winc.scale + x2();
                    double Y2 = dY / winc.scale + y2();
                    if (X2 >= 0 && X2 <= winc.canvas.getWidth() / winc.scale && Y2 >= 0
                            && Y2 <= winc.canvas.getHeight() / winc.scale) { //контроль выхода за канву
                        this.x2(X2);
                        this.y2(Y2);
                    }
                } else if (passMask[0] == 2) {
                    double X = dX / winc.scale + x2();
                    double Y = dY / winc.scale + y2();
                    if (dY != 0) {
                        this.y1(Y);
                        this.y2(Y);
                    } else if (dX != 0) {
                        this.x1(X);
                        this.x2(X);
                    }
                }
            }
            timer.stop();
            timer.start();
        };
        ListenerKey keyReleased = (evt) -> {
        };
        ListenerMouse mousePressed = (evt) -> {

            if (this.area != null) {
                pointPress = evt.getPoint();
                Coordinate wincPress = new Coordinate((evt.getX() - Canvas.translate[0]) / winc.scale, (evt.getY() - Canvas.translate[1]) / winc.scale);
                boolean b = this.area.contains(gf.createPoint(wincPress));

                //Если клик внутри контура
                if (b == true) {

                    if (passMask[1] == 1) {
                        passMask[1] = 2;
                    } else {
                        passMask = UCom.getArr(0, 1);
                    }
                    LineSegment segm = new LineSegment(this.x1(), this.y1(), this.x2(), this.y2());
                    double coef = segm.segmentFraction(wincPress);
                    if (coef < .33) { //кликнул начало вектора
                        passMask[1] = (passMask[0] != 0) ? 1 : passMask[1];
                        passMask[0] = 0;

                    } else if (coef > .67) {//кликнул конец вектора
                        passMask[1] = (passMask[0] != 1) ? 1 : passMask[1];
                        passMask[0] = 1;

                    } else {//кликнул середина вектора 
                        if ((this.anglHoriz() > -45 && this.anglHoriz() < 45) //Bot
                                || (this.anglHoriz() > -135 && this.anglHoriz() < -45)) { //Right
                            passMask[1] = (passMask[0] != 2) ? 1 : passMask[1];
                            passMask[0] = 2;
                        } else {
                            passMask = UCom.getArr(0, 0);
                        }
                    }
                } else {
                    passMask = UCom.getArr(0, 0);
                    root.listenerPassEdit = null;
                }
                winc.canvas.repaint();
            }
        };
        ListenerMouse mouseReleased = (evt) -> {
        };
        ListenerMouse mouseDragge = (evt) -> {
            if (this.area != null) {
                double W = winc.canvas.getWidth();
                double H = winc.canvas.getHeight();
                double dX = evt.getX() - pointPress.getX(); //прирощение по горизонтали
                double dY = evt.getY() - pointPress.getY(); //прирощение по вертикали 
                if (passMask[1] > 1) {

                    if (passMask[0] == 0) { //начало вектора
                        double X1 = dX / winc.scale + x1();
                        double Y1 = dY / winc.scale + y1();
                        pointPress = evt.getPoint();
                        if (X1 > 0) {
                            this.x1(X1);
                        }
                        if (Y1 > 0) {
                            this.y1(Y1);
                        }

                    } else if (passMask[0] == 1) { //конец вектора
                        double X2 = dX / winc.scale + x2();
                        double Y2 = dY / winc.scale + y2();
                        pointPress = evt.getPoint();
                        if (X2 > 0) {
                            this.x2(X2);
                        }
                        if (Y2 > 0) {
                            this.y2(Y2);
                        }

                    } else if (passMask[0] == 2) { //конец вектора
                        double X = dX / winc.scale + x2();
                        double Y = dY / winc.scale + y2();
                        pointPress = evt.getPoint();
                        if (this.anglHoriz() > -45 && this.anglHoriz() < 45) { //Bot
                            this.y1(Y);
                            this.y2(Y);
                        }
                        if (this.anglHoriz() > -135 && this.anglHoriz() < -45) { //Right
                            this.x1(X);
                            this.x2(X);
                        }
                        if (X < 0 || Y < 0) {
                            winc.gson.translate(winc.gson, Math.abs(dX), Math.abs(dY), winc.scale);
                        }
                    }
                }
            }
        };

        this.winc.keyboardPressed.add(keyPressed);
        this.winc.keyboardReleased.add(keyReleased);
        this.winc.mousePressed.add(mousePressed);
        this.winc.mouseReleased.add(mouseReleased);
        this.winc.mouseDragged.add(mouseDragge);
    }

    //Пометка точек редактирования конструкции
    @Override
    public void paint() {
        if (this.area != null) {
            if (this.passMask[1] > 0) {

                this.root.listenerPassEdit = () -> {

                    winc.gc2d.setColor(new java.awt.Color(255, 000, 000));
                    if (this.passMask[0] == 0) { //хвост вектора
                        Arc2D arc = new Arc2D.Double(this.x1() - SIZE / 2, this.y1() - SIZE / 2, SIZE, SIZE, 0, 360, Arc2D.OPEN);
                        winc.gc2d.draw(arc);

                    } else if (this.passMask[0] == 1) { //начало вектора
                        Arc2D arc = new Arc2D.Double(this.x2() - SIZE / 2, this.y2() - SIZE / 2, SIZE, SIZE, 0, 360, Arc2D.OPEN);
                        winc.gc2d.draw(arc);

                    } else if (this.passMask[0] == 2) { //середина вектора
                        if (this.h() != null) {
                            List<Coordinate> list = Arrays.asList(owner.area.getGeometryN(0).getCoordinates())
                                    .stream().filter(c -> c.z == this.id).collect(toList());
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
        //if (passMask[1] > 1) {
        gson.x1 = x1;
        gson.y1 = y1;
        gson.x2 = x2;
        gson.y2 = y2;
        // }
    }

    @Override
    public String toString() {
        return super.toString() + ", anglHoriz=" + anglHoriz();
    }
}
