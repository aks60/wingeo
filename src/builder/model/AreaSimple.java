package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import common.LinkedCom;
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
    public LinkedCom<Com5t> childs = new LinkedCom(); //дети

    public AreaSimple(Wincalc winc) {
        super(winc, winc.gson, null);
//        this.layout = winc.gson.layout;
//        this.colorID1 = winc.gson.color1;
//        this.colorID2 = winc.gson.color2;
//        this.colorID3 = winc.gson.color3;
//
//        initСonstructiv(winc.gson.param());
//        setLocation(winc.gson.width(), winc.gson.height());
//        initParametr(winc.gson.param());
    }

    public AreaSimple(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
    }

    /**
     * Изменение размеров конструкции по оси X
     *
     * @param v - новый размер
     */
    public void resizeX(double v) {
//        GsonRoot rootGson = winc.rootGson;
//        try {
//            if (id() == 0) {
//                double k = v / gson.width(); //коэффициент
//                if (k != 1) {
//                    if (UCom.getDbl(rootGson.width2(), 0.0) > UCom.getDbl(rootGson.width1(), 0.0)) {
//                        rootGson.width2(v);
//                        if (rootGson.width1() != null) {
//                            rootGson.width1(k * rootGson.width1());
//                        }
//                    }
//                    if (UCom.getDbl(rootGson.width1(), 0.0) > UCom.getDbl(rootGson.width2(), 0.0)) {
//                        rootGson.width1(v);
//                        if (rootGson.width2() != null) {
//                            rootGson.width2(k * rootGson.width2());
//                        }
//                    }
//                    for (IArea5e e : winc.listArea) { //перебор всех гориз. area
//                        if (e.layout() == Layout.HORIZ) {
//                            for (ICom5t e2 : e.childs()) { //изменение детей по ширине
//                                if (e2 instanceof IArea5e) {
//                                    e2.gson().length(k * e2.gson().length());
//                                }
//                            }
//                        }
//                    }
//                }
//            } else {
//                double k = v / lengthX(); //коэффициент 
//                if (k != 1) {
//                    gson.length(v);
//                    if (type() == Type.ARCH) {
//                        rootGson.width1(rootGson.width() - v);
//                    } else if (type() == Type.TRAPEZE && form == Form.RIGHT) {
//                        rootGson.width1(rootGson.width() - v);
//                    } else if (type() == Type.TRAPEZE && form == Form.LEFT) {
//                        rootGson.width2(rootGson.width() - v);
//                    }
//
//                    for (ICom5t e : childs) { //изменение детей по высоте
//                        if (e.owner().layout() == Layout.HORIZ && (e.type() == Type.AREA || e.type() == Type.STVORKA)) {
//                            ((IArea5e) e).resizeX(k * e.lengthX()); //рекурсия изменения детей
//
//                        } else {
//                            if (e instanceof IArea5e) {
//                                for (ICom5t e2 : ((IArea5e) e).childs()) {
//                                    if (e2.owner().layout() == Layout.HORIZ && (e2.type() == Type.AREA || e2.type() == Type.STVORKA)) {
//                                        ((IArea5e) e2).resizeX(k * e2.lengthX()); //рекурсия изменения детей
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Ошибка:AreaSimple.resizeX()");
//        }
    }

    /**
     * Изменение размеров конструкции по оси Y
     *
     * @param v - новый размер
     */
    public void resizeY(double v) {
//        GsonRoot rootGson = winc.rootGson;
//        try {
//            if (id() == 0) {
//                double k = v / gson.height(); //коэффициент
//                if (k != 1) {
//                    if (UCom.getDbl(rootGson.height1(), 0.0) > UCom.getDbl(rootGson.height2(), 0.0)) {
//                        rootGson.height1(v);
//                        if (rootGson.height2() != null) {
//                            rootGson.height2(k * rootGson.height2());
//                        }
//                    }
//                    if (UCom.getDbl(rootGson.height2(), 0.0) > UCom.getDbl(rootGson.height1(), 0.0)) {
//                        rootGson.height2(v);
//                        if (rootGson.height1() != null) {
//                            rootGson.height1(k * rootGson.height1());
//                        }
//                    }
//                    for (IArea5e e : winc.listArea) { //перебор всех вертик. area
//                        if (e.layout() == Layout.VERT) {
//                            for (ICom5t e2 : e.childs()) { //изменение детей по высоте
//                                if (e2 instanceof IArea5e) {
//                                    e2.gson().length(k * e2.gson().length());
//                                }
//                            }
//                        }
//                    }
//                }
//            } else {
//                double k = v / lengthY(); //коэффициент 
//                if (k != 1) {
//                    gson.length(v);
//                    if (type() == Type.ARCH) {
//                        rootGson.height2(rootGson.height() - v);
//                    } else if (type() == Type.TRAPEZE && form == Form.RIGHT) {
//                        rootGson.height2(rootGson.height() - v);
//                    } else if (type() == Type.TRAPEZE && form == Form.LEFT) {
//                        rootGson.height1(rootGson.height() - v);
//                    }
//
//                    for (ICom5t e : childs) { //изменение детей по высоте
//                        if (e.owner().layout() == Layout.VERT && (e.type() == Type.AREA || e.type() == Type.STVORKA)) {
//                            ((IArea5e) e).resizeY(k * e.lengthY()); //рекурсия изменения детей
//
//                        } else {
//                            if (e instanceof IArea5e) {
//                                for (ICom5t e2 : ((IArea5e) e).childs()) {
//                                    if (e2.owner().layout() == Layout.VERT && (e2.type() == Type.AREA || e2.type() == Type.STVORKA)) {
//                                        ((IArea5e) e2).resizeY(k * e2.lengthY()); //рекурсия изменения детей
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Ошибка:AreaSimple.resizeY()");
//        }
    }

    @Override
    public List<Com5t> childs() {
        return childs;
    }

    public void paint() {
//        try {
//            if (area != null) {
//                winc.gc2d.draw(area);
//            }
//        } catch (Exception e) {
//            System.err.println("Ошибка:AreaSimple.paint()" + toString() + e);
//        }
    }
}
