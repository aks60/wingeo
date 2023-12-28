package builder.model;

import builder.Wincalc;
import static builder.model.Com5t.gf;
import builder.script.GsonElem;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import common.LinkedCom;
import dataset.Record;
import domain.eColor;
import static domain.eGroups.val;
import domain.eParams;
import domain.eParmap;
import domain.eSysprof;
import enums.*;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.AffineTransformation;

public class AreaSimple extends Com5t {

    public LinkedCom<ElemSimple> frames = new LinkedCom(this); //список рам
    public LinkedList<Point2D> listSkin = new LinkedList();
    public LinkedCom<Com5t> childs = new LinkedCom(this); //дети
    private List<Double> listHor = new ArrayList(), listVer = new ArrayList();

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
    }

    //Т - соединения
    public void joining() {

        //T - соединения
        LinkedList<ElemSimple> crosList = winc.listElem.filter(Type.IMPOST, Type.STOIKA);
        LinkedList<ElemSimple> elemList = winc.listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.IMPOST, Type.STOIKA);

        //Цикл по кросс элементам
        for (ElemSimple cross : crosList) {

            //Цикл по сторонам рамы и импостам
            for (ElemSimple frame : elemList) {
                if (cross.id != frame.id) {

                    LineString line = gf.createLineString(new Coordinate[]{
                        new Coordinate(frame.x1(), frame.y1()), new Coordinate(frame.x2(), frame.y2())});

                    if (line.contains(UGeo.newPoint(cross.x1(), cross.y1()))) {
                        winc.listJoin.add(new ElemJoining(this.winc, TypeJoin.TIMP, cross, frame));
                    }
                    if (line.contains(UGeo.newPoint(cross.x2(), cross.y2()))) {
                        winc.listJoin.add(new ElemJoining(this.winc, TypeJoin.TIMP, cross, frame));
                    }
                }
            }
        }
    }

    public Geometry lineTip(double x, double y, double angl, double length) {

        Geometry tip = gf.createLineString(new Coordinate[]{
            new Coordinate(x - length, y), new Coordinate(x, y),
            new Coordinate(x - 20, y - 20), new Coordinate(x, y),
            new Coordinate(x - 20, y + 20)});
        AffineTransformation aff = new AffineTransformation();
        aff.setToRotation(Math.toRadians(angl), x, y);
        return aff.transform(tip);
    }

    private int resizeFont() {
        if (winc.scale > .18) {
            return 12;
        } else if (winc.scale > .16) {
            return 11;
        } else if (winc.scale > .15) {
            return 10;
        } else {
            return 9;
        }
    }

    //Линии размерности
    public void paint() {
        
        HashSet<Double> hsHor = new HashSet(), hsVer = new HashSet();
        for (AreaSimple area5e : winc.listArea) {
            Polygon p = (area5e.type == Type.STVORKA) ? ((AreaStvorka) area5e).area2 : area5e.area;
            for (Coordinate c : List.of(p.getCoordinates())) {
                hsHor.add(c.x);
                hsVer.add(c.y);
            }
        }
        listHor.addAll(hsHor);
        listVer.addAll(hsVer);
        Collections.sort(listHor);
        Collections.sort(listVer);

        DecimalFormat df1 = new DecimalFormat("#0.#");
        double ds = winc.canvas.ds;
        double length = 200;
        java.awt.Color color = winc.gc2d.getColor();
        winc.gc2d.setColor(new java.awt.Color(0, 0, 0));
        winc.gc2d.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 80));

        Shape shape = new ShapeWriter().toShape(lineTip(winc.width() + ds, 0, -90, length));
        winc.gc2d.draw(shape);
        shape = new ShapeWriter().toShape(lineTip(winc.width() + ds, winc.height(), 90, length));;
        winc.gc2d.draw(shape);
        //winc.gc2d.rotate(Math.toRadians(-90), 11, val);
        winc.gc2d.drawString(df1.format(777), 1700, 800);
       // winc.gc2d.rotate(Math.toRadians(90), 11, val);

        winc.gc2d.setColor(color);
    }
}
