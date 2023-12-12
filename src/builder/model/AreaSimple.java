package builder.model;

import builder.Wincalc;
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
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

public class AreaSimple extends Com5t {

    public LinkedCom<ElemSimple> frames = new LinkedCom(); //список рам
    public LinkedList<Point2D> listSkin = new LinkedList();
    public LinkedCom<Com5t> childs = new LinkedCom(); //дети

    public AreaSimple(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson.id, gson, owner);
        initConstructiv(gson.param);
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
    protected void parametr(JsonObject param) {
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

    //Соединения
    public void joining() {

        //T - соединения
        LinkedList<ElemSimple> crosList = winc.listElem.filter(Type.IMPOST, Type.SHTULP, Type.STOIKA);
        LinkedList<ElemSimple> elemList = winc.listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.IMPOST, Type.SHTULP, Type.STOIKA);

        //Цикл по кросс элементам
        for (ElemSimple crosE : crosList) {
            //Цикл по сторонам рамы и импостам
            for (ElemSimple elemE : elemList) {
                LineString line = gf.createLineString(new Coordinate[]{
                    new Coordinate(elemE.x1(), elemE.y1()), new Coordinate(elemE.x2(), elemE.y2())});

                Point point = gf.createPoint(new Coordinate(crosE.x1(), crosE.y1()));
                if (line.contains(point)) {
                    winc.listJoin.add(new ElemJoining(this.winc, TypeJoin.TIMG1, elemE, crosE));
                }
                point = gf.createPoint(new Coordinate(crosE.x2(), crosE.y2()));
                if (line.contains(point)) {
                    winc.listJoin.add(new ElemJoining(this.winc, TypeJoin.TIMG1, elemE, crosE));
                }
            }
        }
    }

    /**
     * Определяет ближайшего соседа в указанном направлении
     *
     * @param side - сторона направления
     */
    //@Override
    public ElemSimple joinSide(Layout side) {
        try {
            if (this.equals(winc.root) || this.type == Type.STVORKA) {
                return frames.get(side);
            }
            LinkedCom<ElemSimple> mapJoin = winc.root.frames;
            Com5t ret = null;
            List<Com5t> listCom = owner.childs;
            for (int index = 0; index < listCom.size(); ++index) {
                if (listCom.get(index).id == id) {

                    if (owner.equals(winc.root) && owner.layout() == Layout.VERT) {
                        if (side == Layout.TOP) {
                            ret = (index == 0) ? mapJoin.get(side) : listCom.get(index - 1);
                        } else if (side == Layout.BOTT) {
                            ret = (index == listCom.size() - 1) ? mapJoin.get(side) : listCom.get(index + 1);
                        } else {
                            ret = winc.root.frames.get(side);
                        }

                    } else if (owner.equals(winc.root) && owner.layout() == Layout.HORIZ) {
                        if (side == Layout.LEFT) {
                            ret = (index == 0) ? mapJoin.get(side) : listCom.get(index - 1);
                        } else if (side == Layout.RIGHT) {
                            ret = (index == listCom.size() - 1) ? mapJoin.get(side) : listCom.get(index + 1);
                        } else {
                            return winc.root.frames.get(side);
                        }

                    } else {
                        if (owner.layout() == Layout.VERT) {
                            if (side == Layout.TOP) {
                                ret = (index == 0) ? owner.joinSide(side) : listCom.get(index - 1);
                            } else if (side == Layout.BOTT) {
                                ret = (index == listCom.size() - 1) ? owner.joinSide(side) : listCom.get(index + 1);
                            } else {
                                ret = owner.joinSide(side);
                            }
                        } else {
                            if (side == Layout.LEFT) {
                                ret = (index == 0) ? owner.joinSide(side) : listCom.get(index - 1);
                            } else if (side == Layout.RIGHT) {
                                ret = (index == listCom.size() - 1) ? owner.joinSide(side) : listCom.get(index + 1);
                            } else {
                                ret = owner.joinSide(side);
                            }
                        }
                    }
                }
            }
            return (ElemSimple) ret;
        } catch (Exception e) {
            System.err.println("Ошибка:AreaSimple.adjoinedElem() " + e);
            return null;
        }
    }
}
