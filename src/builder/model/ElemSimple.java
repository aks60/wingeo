package builder.model;

import builder.Wincalc;
import builder.making.SpcRecord;
import static builder.model.Com5t.gf;
import builder.script.GsonElem;
import common.UCom;
import common.listener.ListenerKey;
import common.listener.ListenerMouse;
import enums.Layout;
import enums.Type;
import frames.swing.Canvas;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.swing.Timer;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LineSegment;

public abstract class ElemSimple extends Com5t {

    //public double[] anglFlat = {0, 0, 0, 0}; //мин/мах внутренний и мин/мах внешний угол к плоскости   
    public double[] betweenHoriz = {0, 0}; //угол между векторами   
    private java.awt.Point pointPress = null;
    public int passMask[] = {0, 0}; //маска редактир. [0]=0 -начало, [0]=1 -конец, 
    //[0]=2 -середина вектора, [1] > 0 -вешаем обр. прорисовки кружка и разр. редактиров. x,y
    public final double delta = 3;
    public final double SIZE = 20;
    private Timer timer = new Timer(160, null);
    public SpcRecord spcRec = null; //спецификация элемента
    public Color borderColor = Color.BLACK;

    public ElemSimple(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson.id, gson, owner);
        spcRec = new SpcRecord(id, this);
        winc.listElem.add(this);
        winc.listAll.add(this);
    }

    public ElemSimple(Wincalc winc, double id, GsonElem gson, AreaSimple owner) {
        super(winc, id, gson, owner);
        spcRec = new SpcRecord(id, this);
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

    public abstract void addSpecific(SpcRecord spcAdd);

    public void addEvents() {
        timer.setRepeats(false);

        ListenerKey keyPressed = (evt) -> {

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
                    moveXY(X, Y);

                    //Кликнул конец вектора
                } else if (passMask[0] == 1) {
                    X = dX / winc.scale + this.x2();
                    Y = dY / winc.scale + this.y2();
                    moveXY(X, Y);

                    //Кликнул по середине вектора 
                } else if (passMask[0] == 2) {

                    if (this.h() != null) {
                        this.h(this.h() + dY / winc.scale);
                        this.y1(this.h() + dY / winc.scale);
                        this.y2(this.h() + dY / winc.scale);

                    } else {
                        X = dX / winc.scale + this.x2();
                        Y = dY / winc.scale + this.y2();

                        if (Y > 0 && List.of(Layout.BOTT, Layout.TOP, Layout.HORIZ).contains(this.layout())) {
                            this.y1(Y);
                            this.y2(Y);
                        }
                        if (X > 0 && List.of(Layout.LEFT, Layout.RIGHT, Layout.VERT).contains(this.layout())) {
                            this.x1(X);
                            this.x2(X);
                        }
                    }
                }
                if (X < 0 || Y < 0) {
                    winc.gson.translate(winc.gson, Math.abs(dX), Math.abs(dY), winc.scale);
                }
            }
            timer.stop();
            timer.start();
        };
        ListenerMouse mousePressed = (evt) -> {
            if (this.area != null) {
                pointPress = evt.getPoint();
                Coordinate wincPress = new Coordinate((evt.getX() - Canvas.translate[0]) / winc.scale, (evt.getY() - Canvas.translate[1]) / winc.scale);
                boolean b = this.area.contains(gf.createPoint(wincPress));

                //Если клик внутри контура
                if (b == true) {
                    ++passMask[1];
                    LineSegment segm = new LineSegment(this.x1(), this.y1(), this.x2(), this.y2());
                    double coef = segm.segmentFraction(wincPress); //доля расстояния (в [0,0, 1,0] ) вдоль этого отрезка.

                    if (coef < .33) { //кликнул начало вектора
                        passMask[1] = (passMask[0] != 0) ? 1 : passMask[1];
                        passMask[0] = 0;

                    } else if (coef > .67) {//кликнул конец вектора
                        passMask[1] = (passMask[0] != 1) ? 1 : passMask[1];
                        passMask[0] = 1;

                    } else {//кликнул по середине вектора                 
                        passMask[1] = (passMask[0] != 2) ? 1 : passMask[1];
                        passMask[0] = 2;
                    }
                } else { //Промах, всё обнуляю
                    passMask = UCom.getArr(0, 0);
                    root.listenerPassEdit = null;
                }
                winc.canvas.requestFocusInWindow();
                winc.canvas.repaint();
            }
        };
        ListenerMouse mouseDragge = (evt) -> {
            if (this.area != null) {
                double X = 0, Y = 0;
                double W = winc.canvas.getWidth(), H = winc.canvas.getHeight();
                double dX = evt.getX() - pointPress.getX(); //прирощение по горизонтали
                double dY = evt.getY() - pointPress.getY(); //прирощение по вертикали 

                //Фильтр движухи вкл-ся когда passMask[1] > 1 !!! 
                if (passMask[1] > 1) {
                    pointPress = evt.getPoint();

                    if (passMask[0] == 0) { //начало вектора
                        X = dX / winc.scale + x1();
                        Y = dY / winc.scale + y1();
                        moveXY(X, Y);

                    } else if (passMask[0] == 1) { //конец вектора
                        X = dX / winc.scale + x2();
                        Y = dY / winc.scale + y2();
                        moveXY(X, Y);

                    } else if (passMask[0] == 2) { //середина вектора
                        X = dX / winc.scale + x2();
                        Y = dY / winc.scale + y2();
                        if (Y > 0 && List.of(Layout.BOTT, Layout.TOP, Layout.HORIZ).contains(layout())) {
                            this.y1(Y);
                            this.y2(Y);
                        }
                        if (X > 0 && List.of(Layout.LEFT, Layout.RIGHT, Layout.VERT).contains(layout())) {
                            this.x1(X);
                            this.x2(X);
                        }
                    }
                    if (X < 0 || Y < 0) {
                        winc.gson.translate(winc.gson, Math.abs(dX), Math.abs(dY), winc.scale);
                    }
                }
            }
        };

        this.winc.keyboardPressed.add(keyPressed);
        this.winc.mousePressed.add(mousePressed);
        this.winc.mouseDragged.add(mouseDragge);
    }

    private void moveXY(double x, double y) {
        if (x > 0 && y > 0) {
            if (List.of(Layout.BOTT, Layout.HORIZ).contains(layout())) {
                if (passMask[0] == 0) {
                    this.y1(y);
                } else if (passMask[0] == 1) {
                    this.y2(y);
                }
            } else if (List.of(Layout.RIGHT).contains(layout())) {
                if (passMask[0] == 0) {
                    this.x1(x);
                } else if (passMask[0] == 1) {
                    this.x2(x);
                }
            } else if (List.of(Layout.TOP).contains(layout())) {
                if (passMask[0] == 0) {
                    this.y1(y);
                } else if (passMask[0] == 1) {
                    this.y2(y);
                }
            } else if (List.of(Layout.LEFT, Layout.VERT).contains(layout())) {
                if (passMask[0] == 0) {
                    this.x1(x);
                } else if (passMask[0] == 1) {
                    this.x2(x);
                }
            }
        }
    }

    @Override
    public Layout layout() {
        try {
            double anglHor = UGeo.anglHor(x1(), y1(), x2(), y2());

            if (anglHor > 315 && anglHor <= 360 || anglHor >= 0 && anglHor < 45) {
                return (this.type == Type.IMPOST || this.type == Type.SHTULP) ? Layout.HORIZ : Layout.BOTT;

            } else if (anglHor >= 45 && anglHor < 135) {
                return Layout.RIGHT;

            } else if (anglHor >= 135 && anglHor < 225) {
                return Layout.TOP;

            } else if (anglHor >= 225 && anglHor <= 315) {
                return (this.type == Type.IMPOST || this.type == Type.SHTULP) ? Layout.VERT : Layout.LEFT;
            }
        } catch (Exception e) {
            System.err.println("Ошибка:ElemSimple.layout() " + e);
        }
        return Layout.ANY;
    }

    //Точка редактирования конструкции
    @Override
    public void paint() {
        if (this.area != null) {
            if (this.passMask[1] > 0) {

                this.root.listenerPassEdit = () -> {  //вешаем глобальный обработчик!
                    winc.gc2d.setColor(new java.awt.Color(255, 000, 000));

                    //Хвост вектора, точка круг
                    if (this.passMask[0] == 0) {
                        Arc2D arc = new Arc2D.Double(this.x1() - SIZE / 2, this.y1() - SIZE / 2, SIZE, SIZE, 0, 360, Arc2D.OPEN);
                        winc.gc2d.draw(arc);

                        //Начало вектора. точка круг
                    } else if (this.passMask[0] == 1) {
                        Arc2D arc = new Arc2D.Double(this.x2() - SIZE / 2, this.y2() - SIZE / 2, SIZE, SIZE, 0, 360, Arc2D.OPEN);
                        winc.gc2d.draw(arc);

                        //Середина вектора. точка квадрат
                    } else if (this.passMask[0] == 2) {
                        if (this.h() != null) { //арка
                            List<Coordinate> list = Arrays.asList(owner.area.getGeometryN(0).getCoordinates())
                                    .stream().filter(c -> c.z == this.id).collect(toList());
                            int i = list.size() / 2; //index середины дуги
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
    public ElemSimple getElemjoin(Layout side) {
        boolean begin = false;
        try {
            //Цикл по элементам конструкции
            for (int index = winc.listElem.size() - 1; index >= 0; --index) {
                ElemSimple el = (ElemSimple) winc.listElem.get(index);

                if (begin == true && el.type != Type.GLASS) {
                    //Проверка начинает выполняться после появления в обратном цикле самого элемента(this) 
                    if (Layout.BOTT == side && el.layout() != Layout.VERT) {
                        double Y2 = (y2() > y1()) ? y2() : y1();
                        if (UGeo.isInRing(x1() + (x2() - x1()) / 2, Y2, el.area) == true) {
                            return (ElemSimple) el;
                        }
                    } else if (Layout.LEFT == side && el.layout() != Layout.HORIZ) {
                        if (UGeo.isInRing(x1(), y1() + (y2() - y1()) / 2, el.area) == true) {
                            return (ElemSimple) el;
                        }
                    } else if (Layout.TOP == side && el.layout() != Layout.VERT) {
                        double Y1 = (y2() > y1()) ? y1() : y2();
                        if (UGeo.isInRing(x1() + (x2() - x1()) / 2, Y1, el.area) == true && (el.owner.type == Type.ARCH && el.layout() == Layout.TOP) == false) {
                            return (ElemSimple) el;
                        }
                    } else if (Layout.RIGHT == side && el.layout() != Layout.HORIZ) {
                        if (UGeo.isInRing(x2(), y1() + (y2() - y1()) / 2, el.area)) {
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
        return super.toString() + ", anglHoriz=" + UGeo.anglHor(x1(), y1(), x2(), y2());
    }
}
