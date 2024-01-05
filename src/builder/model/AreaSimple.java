package builder.model;

import builder.Wincalc;
import static builder.model.Com5t.gf;
import builder.script.GsonElem;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import common.LinkedCom;
import dataset.Record;
import domain.eColor;
import domain.eParams;
import domain.eParmap;
import domain.eSysprof;
import enums.*;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.util.AffineTransformation;

public class AreaSimple extends Com5t {

    private DecimalFormat df1 = new DecimalFormat("#0.#");
    public LinkedCom<ElemSimple> frames = new LinkedCom(this); //список рам
    //public LinkedList<Point2D> listSkin = new LinkedList();
    public LinkedCom<Com5t> childs = new LinkedCom(this); //дети

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

    public Geometry lineTip(double tipX, double tipY, double angl, double length) {

        Geometry tip = gf.createLineString(new Coordinate[]{
            new Coordinate(tipX - length, tipY), new Coordinate(tipX, tipY),
            new Coordinate(tipX - 16, tipY - 16), new Coordinate(tipX, tipY),
            new Coordinate(tipX - 16, tipY + 16)});
        AffineTransformation aff = new AffineTransformation();
        aff.setToRotation(Math.toRadians(angl), tipX, tipY);
        return aff.transform(tip);
    }

    //Линии размерности
    @Override
    public void paint() {
        winc.gc2d.setColor(new java.awt.Color(0, 0, 0));
        Envelope box = winc.root.area.getEnvelopeInternal();
        HashSet<Double> hsHor = new HashSet(), hsVer = new HashSet();
        for (AreaSimple area5e : winc.listArea) {
            Geometry p = (area5e.type == Type.STVORKA) ? ((AreaStvorka) area5e).area2 : area5e.area;
            List.of(p.getCoordinates()).forEach(c -> hsHor.add(c.x));
            List.of(p.getCoordinates()).forEach(c -> hsVer.add(c.y));
        }
        List<Double> listHor = new ArrayList(hsHor);
        List<Double> listVer = new ArrayList(hsVer);
        Collections.sort(listHor);
        Collections.sort(listVer);
        Font font = winc.gc2d.getFont(); //размер шрифта (см. canvas)
        AffineTransform orig = winc.gc2d.getTransform();

        for (int i = 1; i < listVer.size(); ++i) {

            if (Math.round(listVer.get(i) - listVer.get(i - 1)) != 0) {
                Rectangle2D rec2D = font.getStringBounds(df1.format(listVer.get(i) - listVer.get(i - 1)), winc.gc2d.getFontRenderContext());
                int tip[] = {(int) Math.round(listVer.get(i - 1)), (int) Math.round(listVer.get(i))};  //y1, y2 кончик вращения вектора
                int length = (int) Math.round((listVer.get(i) - listVer.get(i - 1) - rec2D.getWidth() - 10) / 2); //длина вектора

                if (Math.round(listVer.get(i) - listVer.get(i - 1)) > 20) {
                    Geometry lineTip1 = lineTip(box.getMaxX() + rec2D.getHeight() / 2, tip[0], -90, length);
                    Shape shape = new ShapeWriter().toShape(lineTip1);
                    winc.gc2d.draw(shape);
                    Geometry lineTip2 = lineTip(box.getMaxX() + rec2D.getHeight() / 2, tip[1], 90, length);
                    shape = new ShapeWriter().toShape(lineTip2);
                    winc.gc2d.draw(shape);
                }
                int xy[] = {(int) (box.getMaxX() + rec2D.getHeight() - 10), (int) Math.round(listVer.get(i - 1) + length + rec2D.getWidth() + 5)}; //точка врашения и начала текста
                winc.gc2d.rotate(Math.toRadians(-90), xy[0], xy[1]);
                winc.gc2d.drawString(df1.format(listVer.get(i) - listVer.get(i - 1)), xy[0], xy[1]);
            }
            winc.gc2d.setTransform(orig);

        }
        for (int i = 1; i < listHor.size(); ++i) {

            if (Math.round(listHor.get(i) - listHor.get(i - 1)) != 0) {
                Rectangle2D rec2D = font.getStringBounds(df1.format(listHor.get(i) - listHor.get(i - 1)), winc.gc2d.getFontRenderContext());
                int tip[] = {(int) Math.round(listHor.get(i - 1)), (int) Math.round(listHor.get(i))}; //x1, x2 кончик вращения вектора
                int length = (int) Math.round((listHor.get(i) - listHor.get(i - 1) - rec2D.getWidth() - 10) / 2); //длина вектора

                if (Math.round(listHor.get(i) - listHor.get(i - 1)) > 20) {
                    Geometry lineTip1 = lineTip(tip[0], box.getMaxY() + rec2D.getHeight() / 2, 180, length);
                    Shape shape = new ShapeWriter().toShape(lineTip1);
                    winc.gc2d.draw(shape);
                    Geometry lineTip2 = lineTip(tip[1], box.getMaxY() + rec2D.getHeight() / 2, 0, length);
                    shape = new ShapeWriter().toShape(lineTip2);
                    winc.gc2d.draw(shape);
                }
                int xy[] = {(int) Math.round(listHor.get(i - 1) + length + 5), (int) (box.getMaxY() + rec2D.getHeight() * 0.8)}; //точка начала текста
                winc.gc2d.drawString(df1.format(listHor.get(i) - listHor.get(i - 1)), xy[0], xy[1]);
            }
            winc.gc2d.setTransform(orig);

        }
    }
}
