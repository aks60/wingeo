package builder.model;

import builder.Wincalc;
import builder.making.TRecord;
import static builder.model.Com5t.gf;
import builder.script.GsonElem;
import common.UCom;
import enums.Layout;
import enums.Type;
import frames.swing.comp.Canvas;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.swing.Timer;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

public abstract class ElemSimple extends Com5t {

    //public double[] anglFlat = {0, 0, 0, 0}; //мин/мах внутренний и мин/мах внешний угол к плоскости   
    public double[] betweenHoriz = {0, 0}; //угол между векторами   
    private java.awt.Point pointPress = null;
    public int passMask[] = {0, 0}; //маска [0]=0 -начало, [0]=1 -конец, [0]=2 -середина, [1]>0 -прорисовка кружка и разр. редакт. x,y
    public final double delta = 3;
    private Timer timer = new Timer(160, null);
    public TRecord spcRec = null; //спецификация элемента

    public ElemSimple(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson.id, gson, owner);
        this.spcRec = new TRecord(id, this);
        winc.listElem.add(this);
        winc.listAll.add(this);
    }

    public ElemSimple(Wincalc winc, double id, GsonElem gson, AreaSimple owner) {
        super(winc, id, gson, owner);
        this.spcRec = new TRecord(id, this);
        winc.listElem.add(this);
        winc.listAll.add(this);
    }

    //Для TEST
    public ElemSimple(double id, GsonElem gson) {
        super(id, gson);
    }

    public abstract void initArtikle();

    public abstract void setLocation();

    public abstract void setSpecific();

    public abstract void addSpecific(TRecord spcAdd);

    public void addListenerEvents() {
        timer.setRepeats(false);

        this.winc.keyboardPressed.add((evt) -> {

            if (this.area != null && passMask[1] > 0) {
                LineSegment segm = new LineSegment(this.x1(), this.y1(), this.x2(), this.y2());
                int key = evt.getKeyCode();
                //double dxy = (timer.isRunning() == true) ? 0.14 + winc.scale : 0.1 * winc.scale;
                double dxy = (timer.isRunning() == true) ? 0.04 : 0.1 * winc.scale;
                double X = 0, Y = 0, dX = 0, dY = 0;

                if (key == KeyEvent.VK_UP) {
                    dY = -dxy;
                } else if (key == KeyEvent.VK_DOWN) {
                    dY = dxy;
                } else if (key == KeyEvent.VK_LEFT) {
                    dX = -dxy;
                } else if (key == KeyEvent.VK_RIGHT) {
                    dX = dxy;
                }
                //Кликнул начало вектора
                if (passMask[0] == 0) {
                    X = dX / winc.scale + this.x1();
                    Y = dY / winc.scale + this.y1();
                    UGeo.movePoint(this, X, Y);

                    //Кликнул конец вектора
                } else if (passMask[0] == 1) {
                    X = dX / winc.scale + this.x2();
                    Y = dY / winc.scale + this.y2();
                    UGeo.movePoint(this, X, Y);

                    //Кликнул по середине вектора 
                } else if (passMask[0] == 2) {

                    if (this.h() != null) {
                        this.h(this.h() - dY / winc.scale);
                    } else {
                        X = dX / winc.scale + this.x2();
                        Y = dY / winc.scale + this.y2();

                        if (Y > 0 && List.of(Layout.BOT, Layout.TOP, Layout.HOR).contains(this.layout())) {
                            this.y1(Y);
                            this.y2(Y);
                        }
                        if (X > 0 && List.of(Layout.LEF, Layout.RIG, Layout.VER).contains(this.layout())) {
                            this.x1(X);
                            this.x2(X);
                        }
                    }
                }
                if (X < 0 || Y < 0) {
                    UGeo.moveGson(winc.gson, Math.abs(dX), Math.abs(dY), winc.scale);
                }
            }
            timer.stop();
            timer.start();
        });
        this.winc.mousePressed.add((evt) -> {
            if (this.area != null) {
                pointPress = evt.getPoint();
                Coordinate wincPress = new Coordinate((evt.getX() - Canvas.translateXY[0])
                        / winc.scale, (evt.getY() - Canvas.translateXY[1]) / winc.scale);
                boolean inside = this.area.contains(gf.createPoint(wincPress));

                //Если клик внутри контура
                if (inside == true) {
                    ++passMask[1];
                    LineSegment segm = new LineSegment(this.x1(), this.y1(), this.x2(), this.y2());
                    double coeff = segm.segmentFraction(wincPress); //доля расстояния вдоль этого отрезка.

                    if (coeff < .33) { //кликнул начало вектора
                        passMask[1] = (passMask[0] != 0) ? 1 : passMask[1];
                        passMask[0] = 0;

                    } else if (coeff > .67) {//кликнул конец вектора
                        passMask[1] = (passMask[0] != 1) ? 1 : passMask[1];
                        passMask[0] = 1;

                    } else {//кликнул по середине вектора                 
                        passMask[1] = (passMask[0] != 2) ? 1 : passMask[1];
                        passMask[0] = 2;
                    }
                } else { //Промах, всё обнуляю
                    passMask = UCom.getArr(0, 0);
                }
                winc.canvas.requestFocusInWindow();
                winc.canvas.repaint();
            }
        });
        this.winc.mouseDragged.add((evt) -> {
            //Фильтр движухи откл. когда passMask[1] > 1 
            if (passMask[1] > 1 && this.area != null) {

                double X = 0, Y = 0;
                double dX = evt.getX() - pointPress.getX(); //прирощение по горизонтали
                double dY = evt.getY() - pointPress.getY(); //прирощение по вертикали 
                pointPress = evt.getPoint(); //новое положение клика точки

                if (passMask[0] == 0) { //начало вектора
                    X = dX / winc.scale + x1();
                    Y = dY / winc.scale + y1();
                    UGeo.movePoint(this, X, Y);

                } else if (passMask[0] == 1) { //конец вектора
                    X = dX / winc.scale + x2();
                    Y = dY / winc.scale + y2();
                    UGeo.movePoint(this, X, Y);

                } else if (passMask[0] == 2) { //середина вектора
                    X = dX / winc.scale + x2();
                    Y = dY / winc.scale + y2();
                    if (Y > 0 && List.of(Layout.BOT, Layout.TOP, Layout.HOR).contains(layout())) {
                        if (this.h() != null) {
                            this.h(this.h() - dY / winc.scale);
                        } else {
                            this.y1(Y);
                            this.y2(Y);
                        }
                    }
                    if (X > 0 && List.of(Layout.LEF, Layout.RIG, Layout.VER).contains(layout())) {
                        if (this.h() != null) {
                            this.h(this.h() - dX / winc.scale);
                        } else {
                            this.x1(X);
                            this.x2(X);
                        }
                    }
                }
                if (X < 0 || Y < 0) {
                    UGeo.moveGson(winc.gson, Math.abs(dX), Math.abs(dY), winc.scale);
                }
            }
        });
    }

    @Override
    public Layout layout() {
        try {
            double anglHor = UGeo.anglHor(x1(), y1(), x2(), y2());

            if (anglHor > 315 && anglHor <= 360 || anglHor >= 0 && anglHor < 45) {
                return (this.type == Type.IMPOST || this.type == Type.SHTULP) ? Layout.HOR : Layout.BOT;

            } else if (anglHor >= 45 && anglHor < 135) {
                return Layout.RIG;

            } else if (anglHor >= 135 && anglHor < 225) {
                return Layout.TOP;

            } else if (anglHor >= 225 && anglHor <= 315) {
                return (this.type == Type.IMPOST || this.type == Type.SHTULP) ? Layout.VER : Layout.LEF;
            }
        } catch (Exception e) {
            System.err.println("Ошибка:ElemSimple.layout() " + e);
        }
        return Layout.ANY;
    }

    @Override
    public void paint() {
    }

    @Override
    public String toString() {
        return super.toString() + ", anglHoriz=" + UGeo.anglHor(x1(), y1(), x2(), y2());
    }
}
