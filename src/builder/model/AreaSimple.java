package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import common.UCom;
import common.listener.ListenerPaint;
import dataset.Record;
import domain.eColor;
import domain.eParams;
import domain.eParmap;
import domain.eSysprof;
import enums.*;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import startup.Test;

public class AreaSimple extends Com5t {

    public ArrayList<ElemSimple> frames = new ArrayList<ElemSimple>(); //список рам
    public ArrayList<Com5t> childs = new ArrayList<Com5t>(); //дети

    public AreaSimple(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson.id, gson, owner);
        initConstructiv(winc.gson.param);
        winc.listArea.add(this);
        winc.listAll.add(this);
    }

    /**
     * Профиль через параметр. PKjson_sysprofID пример створки:sysprofID:1121,
     * typeOpen:4, sysfurnID:2916} Этого параметра нет в интерфейсе программы,
     * он сделан для тестирования с ps4. Делегируется детьми см. класс ElemFrame
     */
    public void initConstructiv(JsonObject param) {
        if (isFinite(param, PKjson.sysprofID)) {//профили через параметр
            sysprofRec = eSysprof.find3(param.get(PKjson.sysprofID).getAsInt());
        }
//        else if(this.owner.id == 0) {
//            sysprofRec = eSysprof.find4(this.winc.nuni, UseArtiklTo.FRAME.id, UseSideTo.ANY);
//        }
    }

    public void setLocation() {
        try {
            Polygon geoShell = (Polygon) this.area.getGeometryN(0);
            Polygon geoInner = UGeo.bufferGeometry(geoShell, winc.listElem, 0, 0);
            Polygon geoFalz = UGeo.bufferGeometry(geoShell, winc.listElem, 0, 1);
            this.area = gf.createMultiPolygon(new Polygon[]{geoShell, geoInner, geoFalz});

            //Test.init(this.area.getGeometryN(0), this.area.getGeometryN(1), this.area.getGeometryN(2));
        } catch (Exception e) {
            System.err.println("Ошибка:AreaSimple.setLocation" + toString() + e);
        }
    }

    //Т - соединения
    public void addJoining() {
        //T - соединения
        ArrayList<ElemSimple> crosList = UCom.filter(winc.listElem, Type.IMPOST, Type.STOIKA);
        ArrayList<ElemSimple> elemList = UCom.filter(winc.listElem, Type.BOX_SIDE, Type.IMPOST);

        //Цикл по импостам
        for (ElemSimple imp : crosList) {
            LineString impost = UGeo.newLineString(imp.x1(), imp.y1(), imp.x2(), imp.y2());
            Geometry p1 = UGeo.newPoint(imp.x1(), imp.y1()).buffer(.0001);
            Geometry p2 = UGeo.newPoint(imp.x2(), imp.y2()).buffer(.0001);

            //Цикл по импостам и рамам
            for (ElemSimple frm : elemList) {
                if (imp.id != frm.id) {
                    Geometry line = UGeo.newLineString(frm.x1(), frm.y1(), frm.x2(), frm.y2());

                    if (frm.type == enums.Type.BOX_SIDE) {
                        if (line.intersects(p1)) { //левая сторона
                            winc.listJoin.add(new ElemJoining(this.winc, TypeJoin.TIMP, frm, imp));
                        }
                        if (line.intersects(p2)) { //правая сторона
                            winc.listJoin.add(new ElemJoining(this.winc, TypeJoin.TIMP, imp, frm));;
                        }

                    } else if (frm.type == enums.Type.IMPOST && imp.owner.area.buffer(.0001).contains(line)) {
                        if (line.intersects(p1)) { //левая сторона
                            winc.listJoin.add(new ElemJoining(this.winc, TypeJoin.TIMP, frm, imp));
                        }
                        if (line.intersects(p2)) { //правая сторона
                            winc.listJoin.add(new ElemJoining(this.winc, TypeJoin.TIMP, imp, frm));;
                        }
                    }
                }
            }
        }
    }

    //Линии и координаты размерности
    @Override
    public void paint() {
        try {
            if (winc.sceleton == false) {
                if (this.type != Type.STVORKA) {
                    for (ElemSimple el : this.frames) {
                        if (el.passMask[1] > 0) {

                            double SIZE = 20;
                            winc.gc2d.setColor(new java.awt.Color(255, 000, 000));

                            //Хвост вектора, точка круг
                            if (el.passMask[0] == 0) {
                                Arc2D arc = new Arc2D.Double(el.x1() - SIZE / 2, el.y1() - SIZE / 2, SIZE, SIZE, 0, 360, Arc2D.OPEN);
                                winc.gc2d.draw(arc);

                                //Начало вектора. точка круг
                            } else if (el.passMask[0] == 1) {
                                Arc2D arc = new Arc2D.Double(el.x2() - SIZE / 2, el.y2() - SIZE / 2, SIZE, SIZE, 0, 360, Arc2D.OPEN);
                                winc.gc2d.draw(arc);

                                //Середина вектора. точка квадрат
                            } else if (el.passMask[0] == 2) {
                                if (el.h() != null) { //арка
                                    List<Coordinate> list = Arrays.asList(owner.area.getGeometryN(0).getCoordinates())
                                            .stream().filter(c -> c.z == el.id).collect(toList());
                                    int i = list.size() / 2; //index середины дуги
                                    Coordinate c1 = list.get(i), c2 = list.get(i + 1);
                                    Coordinate smid = new LineSegment(c1.x, c1.y, c2.x, c2.y).midPoint();
                                    Rectangle2D rec = new Rectangle2D.Double(smid.x - SIZE / 2, smid.y - SIZE / 2, SIZE, SIZE);
                                    winc.gc2d.draw(rec);

                                } else {
                                    Coordinate smid = new LineSegment(el.x1(), el.y1(), el.x2(), el.y2()).midPoint();
                                    Rectangle2D rec = new Rectangle2D.Double(smid.x - SIZE / 2, smid.y - SIZE / 2, SIZE, SIZE);
                                    winc.gc2d.draw(rec);
                                }
                            }
                        }
                    }
                    winc.gc2d.setColor(new java.awt.Color(0, 0, 0));
                    Envelope frameEnvelope = winc.root.area.getGeometryN(0).getEnvelopeInternal();
                    HashSet<Double> hsHor = new HashSet<Double>(), hsVer = new HashSet<Double>();
                    if (this.type != Type.DOOR) {

                        for (AreaSimple area5e : winc.listArea) {
                            Geometry frameBox = (area5e.type == Type.STVORKA) ? area5e.area.getGeometryN(3) : area5e.area.getGeometryN(0);
                            Coordinate coo[] = frameBox.getCoordinates();

                            if (this instanceof AreaArch) {
                                Geometry geo1 = this.area.getGeometryN(0);
                                Envelope env = geo1.getEnvelopeInternal();
                                hsVer.add(env.getMinY());
                            }
                            for (int i = 1; i < coo.length; i++) {
                                Coordinate c1 = coo[i - 1], c2 = coo[i];

                                if (c2.z != c1.z && Math.abs(c2.x - c1.x) > 0.09) {
                                    hsHor.add(c2.x);
                                }
                                if (c2.z != c1.z && Math.abs(c2.y - c1.y) > 0.09) {
                                    hsVer.add(c2.y);
                                }
                            }

                        }
                    } else {
                        Geometry geoShell = this.area.getGeometryN(0);
                        Coordinate cooShell[] = geoShell.getCoordinates();
                        for (int i = 1; i < cooShell.length; i++) {
                            Coordinate c1 = cooShell[i - 1], c2 = cooShell[i];

                            if (c2.z != c1.z && Math.abs(c2.x - c1.x) > 0.09) {
                                hsHor.add(c2.x);
                            }
                            if (c2.z != c1.z && Math.abs(c2.y - c1.y) > 0.09) {
                                hsVer.add(c2.y);
                            }
                        }
                        for (ElemSimple elem5e : UCom.filter(winc.listElem, Type.IMPOST)) {
                            hsVer.add(elem5e.y1());
                        }
                    }
                    List<Double> listHor = new ArrayList<Double>(hsHor);
                    List<Double> listVer = new ArrayList<Double>(hsVer);
                    Collections.sort(listHor);
                    Collections.sort(listVer);

                    Font font = new Font("Dialog", 0, UCom.scaleFont(winc.scale)); //размер шрифта (см. canvas)
                    winc.gc2d.setFont(font);
                    AffineTransform orig = winc.gc2d.getTransform();
                    Rectangle2D metricTxt = font.getStringBounds("999.99", winc.gc2d.getFontRenderContext());

                    //По горизонтали
                    for (int i = 1; i < listHor.size(); ++i) {
                        double dx = listHor.get(i) - listHor.get(i - 1);
                        if (Math.abs(dx) > 0.04) {

                            String txt = UCom.format(dx, -1); //текст разм.линии
                            Rectangle2D metricNumb = font.getStringBounds(txt, winc.gc2d.getFontRenderContext()); //логические границы строки
                            double tail[] = {listHor.get(i - 1), listHor.get(i)}; //x1, x2 хвост вращения вектора
                            int len = (int) Math.ceil(((dx) - (metricNumb.getWidth() + 10)) / 2); //длина до начала(конца) текста
                            double length = Math.round(dx); //длина вектора

                            //Размерные линии
                            Geometry lineTip1 = UGeo.lineTip((i == 1), tail[0], frameEnvelope.getMaxY() + metricNumb.getHeight() / 2, 180, len);
                            Shape shape = new ShapeWriter().toShape(lineTip1);
                            winc.gc2d.draw(shape);
                            Geometry lineTip2 = UGeo.lineTip((i == (listHor.size() - 1)), tail[1], frameEnvelope.getMaxY() + metricNumb.getHeight() / 2, 0, len);
                            shape = new ShapeWriter().toShape(lineTip2);
                            winc.gc2d.draw(shape);

                            //Текст на линии
                            double pxy[] = {listHor.get(i - 1) + len + 8, frameEnvelope.getMaxY() + metricTxt.getHeight() * .86}; //точка начала текста
                            if (length < metricTxt.getWidth()) {
                                pxy[1] = pxy[1] + metricTxt.getHeight() / 2;
                                winc.gc2d.drawString(txt, (int) pxy[0], (int) (pxy[1]));
                            } else {
                                winc.gc2d.drawString(txt, (int) pxy[0], (int) pxy[1]);
                            }
                            winc.gc2d.setTransform(orig);
                        }
                    }

                    //По вертикали
                    for (int i = 1; i < listVer.size(); ++i) {
                        double dy = listVer.get(i) - listVer.get(i - 1);
                        if (Math.abs(dy) > 0.04) {

                            String txt = UCom.format(dy, -1); //текст разм.линии
                            Rectangle2D metricNumb = font.getStringBounds(txt, winc.gc2d.getFontRenderContext()); //логические границы строки
                            int tail[] = {(int) Math.ceil(listVer.get(i - 1)), (int) Math.ceil(listVer.get(i))};  //y1, y2 хвост вращения вектора
                            int len = (int) Math.round((dy - metricNumb.getWidth() - 10) / 2); //длина до начала(конца) текста
                            double length = Math.round(dy); //длина вектора

                            //Размерные линии
                            Geometry lineTip1 = UGeo.lineTip((i == 1), frameEnvelope.getMaxX() + metricNumb.getHeight() / 2, tail[0], -90, len);
                            Shape shape = new ShapeWriter().toShape(lineTip1);
                            winc.gc2d.draw(shape);
                            Geometry lineTip2 = UGeo.lineTip((i == (listVer.size() - 1)), frameEnvelope.getMaxX() + metricNumb.getHeight() / 2, tail[1], 90, len);
                            shape = new ShapeWriter().toShape(lineTip2);
                            winc.gc2d.draw(shape);

                            //Текст на линии
                            double pxy[] = {frameEnvelope.getMaxX() + metricTxt.getHeight() - 6, listVer.get(i) - len}; //точка врашения и начала текста                    
                            if (length < (metricTxt.getWidth())) {
                                winc.gc2d.drawString(txt, (int) (pxy[0] + 4), (int) (pxy[1] - metricTxt.getHeight() / 2));
                            } else {
                                winc.gc2d.rotate(Math.toRadians(-90), pxy[0], pxy[1]);
                                winc.gc2d.drawString(txt, (int) pxy[0], (int) pxy[1]);
                            }
                            winc.gc2d.setTransform(orig);
                        }
                    }
                }
            } else if (this.area != null) {
                winc.gc2d.setColor(new java.awt.Color(000, 000, 255));
                for (int i = 0; i < 3; ++i) {
                    Shape shape = new ShapeWriter().toShape(this.area.getGeometryN(i));
                    winc.gc2d.draw(shape);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:AreaSimple.paint() " + e);
        }
    }
}
