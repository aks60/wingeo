package builder.model;

import builder.Wincalc;
import static builder.model.Com5t.gf;
import builder.script.GsonElem;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import common.ArrayCom;
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
import org.locationtech.jts.geom.util.AffineTransformation;

public class AreaSimple extends Com5t {

    private DecimalFormat df1 = new DecimalFormat("#0.#");
    public ArrayCom<ElemSimple> frames = new ArrayCom(this); //список рам
    public ListenerPaint listenerPassEdit = null;
    public ArrayCom<Com5t> childs = new ArrayCom(this); //дети

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
        ArrayList<ElemSimple> crosList = winc.listElem.filter(Type.IMPOST, Type.STOIKA);
        ArrayList<ElemSimple> elemList = winc.listElem.filterNo(Type.GLASS);

        //Цикл по кросс элементам
        for (ElemSimple cross : crosList) {

            //Цикл по сторонам рамы и импостам
            for (ElemSimple frame : elemList) {
                if (cross.id != frame.id) {
                    LineString line = UGeo.newLineStr(frame.x1(), frame.y1(), frame.x2(), frame.y2());

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

    //Линии размерности
    @Override
    public void paint() {
        try {
            if (listenerPassEdit != null) {
                listenerPassEdit.paint();
            }
            winc.gc2d.setColor(new java.awt.Color(0, 0, 0));
            Envelope boxRama = winc.root.area.getGeometryN(0).getEnvelopeInternal();
            HashSet<Double> hsHor = new HashSet(), hsVer = new HashSet();

            for (AreaSimple area5e : winc.listArea) {
                Geometry poly = (area5e.type == Type.STVORKA) ? ((AreaStvorka) area5e).areaBox.getGeometryN(0) : area5e.area.getGeometryN(0);
                Coordinate coo[] = poly.getCoordinates();
                hsHor.add(coo[0].x);
                hsVer.add(coo[0].y);
                if (this instanceof AreaArch) {
                    Geometry geo1 = this.area.getGeometryN(0);
                    Envelope env = geo1.getEnvelopeInternal();
                    hsVer.add(env.getMinY());
                }
                for (int i = 1; i < coo.length; i++) {
                    Coordinate c1 = coo[i - 1], c2 = coo[i];

                    if (c2.z != c1.z) {
                        hsHor.add(c2.x);
                    }
                    if (c2.z != c1.z) {
                        hsVer.add(c2.y);
                    }
                }
            }
            List<Double> listHor = new ArrayList(hsHor);
            List<Double> listVer = new ArrayList(hsVer);
            Collections.sort(listHor);
            Collections.sort(listVer);
            int coeff = 6;
            Font font = winc.gc2d.getFont(); //размер шрифта (см. canvas)
            AffineTransform orig = winc.gc2d.getTransform();
            Rectangle2D txt2D = font.getStringBounds("999.99", winc.gc2d.getFontRenderContext());

            //По горизонтали
            for (int i = 1; i < listHor.size(); ++i) {
                double dx = listHor.get(i) - listHor.get(i - 1);

                String txt = UCom.format(dx, -1); //текст разм.линии
                Rectangle2D rec2D = font.getStringBounds(txt, winc.gc2d.getFontRenderContext()); //логические границы строки
                double tail[] = {listHor.get(i - 1), listHor.get(i)}; //x1, x2 хвост вращения вектора
                int len = (int) Math.ceil(((dx) - (rec2D.getWidth() + 10)) / 2); //длина до начала(конца) текста
                double length = Math.round(dx); //длина вектора

                //Размерные линии
                Geometry lineTip1 = UGeo.lineTip((i == 1), tail[0], boxRama.getMaxY() + rec2D.getHeight() / 2, 180, len);
                Shape shape = new ShapeWriter().toShape(lineTip1);
                winc.gc2d.draw(shape);
                Geometry lineTip2 = UGeo.lineTip((i == (listHor.size() - 1)), tail[1], boxRama.getMaxY() + rec2D.getHeight() / 2, 0, len);
                shape = new ShapeWriter().toShape(lineTip2);
                winc.gc2d.draw(shape);

                //Текст на линии
                double pxy[] = {listHor.get(i - 1) + len + 8, boxRama.getMaxY() + txt2D.getHeight() * .86}; //точка начала текста
                if (length < txt2D.getWidth()) {

                    winc.gc2d.setFont(new Font(font.getName(), font.getStyle(), font.getSize() - font.getSize() / coeff));
                    pxy[1] = pxy[1] + txt2D.getHeight() / 2;
                    winc.gc2d.drawString(txt, (int) pxy[0], (int) (pxy[1]));
                } else {
                    winc.gc2d.drawString(txt, (int) pxy[0], (int) pxy[1]);
                }
                winc.gc2d.setFont(font);
                winc.gc2d.setTransform(orig);

            }

            //По вертикали
            for (int i = 1; i < listVer.size(); ++i) {
                double dy = listVer.get(i) - listVer.get(i - 1);

                //System.out.println("dy = " + dy);
                //System.out.println("listVer = " + listVer);
                String txt = UCom.format(dy, -1); //текст разм.линии
                Rectangle2D rec2D = font.getStringBounds(txt, winc.gc2d.getFontRenderContext()); //логические границы строки
                int tail[] = {(int) Math.ceil(listVer.get(i - 1)), (int) Math.ceil(listVer.get(i))};  //y1, y2 хвост вращения вектора
                int len = (int) Math.round((dy - rec2D.getWidth() - 10) / 2); //длина до начала(конца) текста
                double length = Math.round(dy); //длина вектора

                //Размерные линии
                Geometry lineTip1 = UGeo.lineTip((i == 1), boxRama.getMaxX() + rec2D.getHeight() / 2, tail[0], -90, len);
                Shape shape = new ShapeWriter().toShape(lineTip1);
                winc.gc2d.draw(shape);
                Geometry lineTip2 = UGeo.lineTip((i == (listVer.size() - 1)), boxRama.getMaxX() + rec2D.getHeight() / 2, tail[1], 90, len);
                shape = new ShapeWriter().toShape(lineTip2);
                winc.gc2d.draw(shape);

                //Текст на линии
                double pxy[] = {boxRama.getMaxX() + txt2D.getHeight() - 6, listVer.get(i) - len}; //точка врашения и начала текста                    
                if (length < (txt2D.getWidth())) {

                    txt = UCom.format(dy, -2); //текст разм.линии
                    winc.gc2d.setFont(new Font(font.getName(), font.getStyle(), font.getSize() - font.getSize() / coeff));
                    winc.gc2d.drawString(txt, (int) (pxy[0] + txt2D.getHeight() / 2), (int) (pxy[1] - txt2D.getHeight() / 2));
                } else {
                    txt = UCom.format(dy, -1); //текст разм.линии
                    winc.gc2d.rotate(Math.toRadians(-90), pxy[0], pxy[1]);
                    winc.gc2d.drawString(txt, (int) pxy[0], (int) pxy[1]);
                }
                winc.gc2d.setFont(font);
                winc.gc2d.setTransform(orig);
            }

        } catch (Exception e) {
            System.err.println("Ошибка:AreaSimple.paint()");
        }
    }
}
