package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import common.UCom;
import common.listener.ListenerPaint;
import dataset.Record;
import domain.eArtikl;
import domain.eColor;
import domain.eParams;
import domain.eParmap;
import domain.eSysprof;
import enums.*;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import startup.Test;

public class AreaSimple extends Com5t {

    public ArrayList<ElemSimple> frames = new ArrayList<ElemSimple>(); //список рам
    public ListenerPaint listenerPassEdit = null; //для прорисовки точек движения сегментов
    public ArrayList<Com5t> childs = new ArrayList<Com5t>(); //дети

    public AreaSimple(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson.id, gson, owner);
        initConstructiv(winc.gson.param);
        initParametr(winc.gson.param);
        winc.listArea.add(this);
        winc.listAll.add(this);
    }

    /**
     * Профиль через параметр. PKjson_sysprofID пример створки:sysprofID:1121,
     * typeOpen:4, sysfurnID:2916} Этого параметра нет в интерфейсе программы,
     * он сделан для тестирования с ps4. Делегируется детьми см. класс ElemFrame
     */
    public void initConstructiv(JsonObject param) {
        if (isJson(param, PKjson.sysprofID)) {//профили через параметр
            sysprofRec = eSysprof.find3(param.get(PKjson.sysprofID).getAsInt());
        }
//        else if(this.owner.id == 0) {
//            sysprofRec = eSysprof.find4(this.winc.nuni, UseArtiklTo.FRAME.id, UseSideTo.ANY);
//        }
    }

    /**
     * Параметры системы(технолога) + параметры менеджера В таблице syspar1 для
     * каждой системы лежат параметры по умолчанию от технолога. К параметрам от
     * технолога замешиваем параметры от менеджера см. скрирт, например
     * {"ioknaParam": [-8252]}. При этом в winc.mapPardef будут изменения с
     * учётом менеджера.
     */
    protected void initParametr(JsonObject param) {
        try {
            if (isJson(param, null)) {
                //Добавим к параметрам системы конструкции параметры конкретной конструкции
                JsonArray ioknaParamArr = param.getAsJsonArray(PKjson.ioknaParam);
                if (ioknaParamArr != null && !ioknaParamArr.isJsonNull() && ioknaParamArr.isJsonArray()) {
                    //Цикл по пааметрам менеджера
                    ioknaParamArr.forEach(ioknaID -> {

                        //Record paramsRec, syspar1Rec;   
                        if (ioknaID.getAsInt() < 0) {
                            Record paramsRec = eParams.find(ioknaID.getAsInt()); //параметр менеджера
                            Record syspar1Rec = winc.mapPardef.get(paramsRec.getInt(eParams.groups_id));
                            if (syspar1Rec != null) { //ситуация если конструкция с nuni = -3, т.е. модели
                                syspar1Rec.setNo(eParams.text, paramsRec.getStr(eParams.text)); //накладываем параметр менеджера
                            }
                        } else {
                            Record paramsRec = eParmap.find(ioknaID.getAsInt()); //параметр менеджера
                            Record syspar1Rec = winc.mapPardef.get(paramsRec.getInt(eParmap.groups_id));
                            if (syspar1Rec != null) { //ситуация если конструкция с nuni = -3, т.е. модели
                                String text = eColor.find(paramsRec.getInt(eParmap.color_id1)).getStr(eColor.name);
                                syspar1Rec.setNo(eParams.text, text); //накладываем параметр менеджера
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:AreaSimple.parametr() " + e);
        }
    }

    public void setLocation() {
        try {
            Polygon geoShell = (Polygon) this.area.getGeometryN(0);
            Polygon geoInner = Com5t.buffer(geoShell, winc.listElem, 0, 0);
            Polygon geoFalz = Com5t.buffer(geoShell, winc.listElem, 0, 1);
            this.area = gf.createMultiPolygon(new Polygon[]{geoShell, geoInner, geoFalz});

            //splitLocation(geoShell, this.childs); //опережающее разделение импостом

//            if (geoShell.getNumPoints() > Com5t.MAXSIDE) {
//                Polygon geoInner = UGeo.bufferCross(geoShell, winc.listElem, 0, 0);
//                Polygon geoFalz = UGeo.bufferCross(geoShell, winc.listElem, 0, 1);
//                Test.init(geoShell, geoInner);
//            }
        } catch (Exception e) {
            System.err.println("Ошибка:AreaSimple.setLocation" + toString() + e);
        }
    }

    //Пилим детей импостом
    public void splitLocation(Polygon geoShell, ArrayList<Com5t> childs) {

        if (childs.size() > 2 && childs.get(1).type == Type.IMPOST) {
            ElemCross impost = (ElemCross) childs.get(1);

            //Geometry geoTEST = UGeo.split2Polygon(geoShell.copy(), impost);
            //Test.init(geoTEST);
            Geometry[] geoSplit = UGeo.splitPolygon(geoShell.copy(), impost.segment());

            childs.get(0).area = (Polygon) geoSplit[1];
            childs.get(2).area = (Polygon) geoSplit[2];
            Geometry lineImp = geoSplit[0];

            //TODO Надо перейти на градусы
            if (impost.layout() == Layout.VERT) {
                impost.setDimension(lineImp.getCoordinates()[1].x, lineImp.getCoordinates()[1].y, lineImp.getCoordinates()[0].x, lineImp.getCoordinates()[0].y);
            } else {
                impost.setDimension(lineImp.getCoordinates()[0].x, lineImp.getCoordinates()[0].y, lineImp.getCoordinates()[1].x, lineImp.getCoordinates()[1].y);
            }
        }
    }

    //Т - соединения
    public void addJoining() {
        //T - соединения
        ArrayList<ElemSimple> crosList = UCom.filter(winc.listElem, Type.IMPOST, Type.STOIKA);
        ArrayList<ElemSimple> elemList = UCom.filter(winc.listElem, Type.BOX_SIDE, Type.IMPOST);

        //Цикл по импостам
        for (ElemSimple imp : crosList) {
            LineString impost = UGeo.newLineStr(imp.x1(), imp.y1(), imp.x2(), imp.y2());
            Geometry p1 = UGeo.newPoint(imp.x1(), imp.y1()).buffer(.0001);
            Geometry p2 = UGeo.newPoint(imp.x2(), imp.y2()).buffer(.0001);

            //Цикл по импостам и рамам
            for (ElemSimple frm : elemList) {
                if (imp.id != frm.id) {
                    Geometry line = UGeo.newLineStr(frm.x1(), frm.y1(), frm.x2(), frm.y2());

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
            if (this.type != Type.STVORKA) {
                if (listenerPassEdit != null) {
                    listenerPassEdit.paint();
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

                //System.out.println("coo= " + listHor);
                Font font = new Font("Dialog", 0, UCom.scaleFont(winc.scale)); //размер шрифта (см. canvas)
                winc.gc2d.setFont(font);
                AffineTransform orig = winc.gc2d.getTransform();
                Rectangle2D txt2D = font.getStringBounds("999.99", winc.gc2d.getFontRenderContext());

                //По горизонтали
                for (int i = 1; i < listHor.size(); ++i) {
                    double dx = listHor.get(i) - listHor.get(i - 1);
                    if (Math.abs(dx) > 0.04) {

                        String txt = UCom.format(dx, -1); //текст разм.линии
                        Rectangle2D rec2D = font.getStringBounds(txt, winc.gc2d.getFontRenderContext()); //логические границы строки
                        double tail[] = {listHor.get(i - 1), listHor.get(i)}; //x1, x2 хвост вращения вектора
                        int len = (int) Math.ceil(((dx) - (rec2D.getWidth() + 10)) / 2); //длина до начала(конца) текста
                        double length = Math.round(dx); //длина вектора

                        //Размерные линии
                        Geometry lineTip1 = UGeo.lineTip((i == 1), tail[0], frameEnvelope.getMaxY() + rec2D.getHeight() / 2, 180, len);
                        Shape shape = new ShapeWriter().toShape(lineTip1);
                        winc.gc2d.draw(shape);
                        Geometry lineTip2 = UGeo.lineTip((i == (listHor.size() - 1)), tail[1], frameEnvelope.getMaxY() + rec2D.getHeight() / 2, 0, len);
                        shape = new ShapeWriter().toShape(lineTip2);
                        winc.gc2d.draw(shape);

                        //Текст на линии
                        double pxy[] = {listHor.get(i - 1) + len + 8, frameEnvelope.getMaxY() + txt2D.getHeight() * .86}; //точка начала текста
                        if (length < txt2D.getWidth()) {
                            pxy[1] = pxy[1] + txt2D.getHeight() / 2;
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
                        Rectangle2D rec2D = font.getStringBounds(txt, winc.gc2d.getFontRenderContext()); //логические границы строки
                        int tail[] = {(int) Math.ceil(listVer.get(i - 1)), (int) Math.ceil(listVer.get(i))};  //y1, y2 хвост вращения вектора
                        int len = (int) Math.round((dy - rec2D.getWidth() - 10) / 2); //длина до начала(конца) текста
                        double length = Math.round(dy); //длина вектора

                        //Размерные линии
                        Geometry lineTip1 = UGeo.lineTip((i == 1), frameEnvelope.getMaxX() + rec2D.getHeight() / 2, tail[0], -90, len);
                        Shape shape = new ShapeWriter().toShape(lineTip1);
                        winc.gc2d.draw(shape);
                        Geometry lineTip2 = UGeo.lineTip((i == (listVer.size() - 1)), frameEnvelope.getMaxX() + rec2D.getHeight() / 2, tail[1], 90, len);
                        shape = new ShapeWriter().toShape(lineTip2);
                        winc.gc2d.draw(shape);

                        //Текст на линии
                        double pxy[] = {frameEnvelope.getMaxX() + txt2D.getHeight() - 6, listVer.get(i) - len}; //точка врашения и начала текста                    
                        if (length < (txt2D.getWidth())) {
                            winc.gc2d.drawString(txt, (int) (pxy[0] + 4), (int) (pxy[1] - txt2D.getHeight() / 2));
                        } else {
                            winc.gc2d.rotate(Math.toRadians(-90), pxy[0], pxy[1]);
                            winc.gc2d.drawString(txt, (int) pxy[0], (int) pxy[1]);
                        }
                        winc.gc2d.setTransform(orig);
                    }
                }
            }
//            if (this.area != null) {
//                Shape shape1 = new ShapeWriter().toShape(this.area.getGeometryN(0));
//                Shape shape2 = new ShapeWriter().toShape(this.area.getGeometryN(1));
//                Shape shape3 = new ShapeWriter().toShape(this.area.getGeometryN(2));
//
//                winc.gc2d.setColor(new java.awt.Color(eColor.find(this.colorID2).getInt(eColor.rgb)));
//                winc.gc2d.fill(shape1);
//                winc.gc2d.fill(shape2);
//                winc.gc2d.fill(shape3);
//
//                winc.gc2d.setColor(new java.awt.Color(000, 000, 255));
//                winc.gc2d.draw(shape1);
//                winc.gc2d.draw(shape2);
//                winc.gc2d.draw(shape3);
//            }

        } catch (Exception e) {
            System.err.println("Ошибка:AreaSimple.paint() " + e);
        }
    }
}
