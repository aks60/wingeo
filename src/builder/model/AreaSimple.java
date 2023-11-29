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
import java.text.Normalizer.Form;
import java.util.LinkedList;
import java.util.List;

public class AreaSimple extends Com5t {

    public LinkedCom<ElemFrame> frames = new LinkedCom(); //список рам
    public LinkedList<Point2D> listSkin = new LinkedList();
    public LinkedCom<Com5t> childs = new LinkedCom(); //дети

    public AreaSimple(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
        initСonstructiv(gson.param);
        winc.listArea.add(this);
        winc.listAll.add(this); 
    }

    /**
     * Профиль через параметр PKjson_sysprofID пример створки:sysprofID:1121,
     * typeOpen:4, sysfurnID:2916} Этого параметра нет в интерфейсе программы,
     * он сделан для тестирования с ps4. Делегируется детьми см. класс ElemFrame
     */
    public void initСonstructiv(JsonObject param) {        
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
            if (isJson(param)) {
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
            System.err.println("Ошибка:AreaSimple.initParametr() " + e);
        }
    }

    public List<Com5t> childs() {
        return childs;
    }

    public void draw() {
        try {
            winc.root.setLocation();
            winc.listArea.forEach(e -> e.setLocation());
            winc.listElem.forEach(e -> e.setLocation());

            //Прорисовка стеклопакетов
            LinkedList<ElemSimple> elemGlassList = winc.listElem.filter(Type.GLASS);
            elemGlassList.stream().forEach(el -> el.paint());

            //Прорисовка импостов
            LinkedList<ElemSimple> elemImpostList = winc.listElem.filter(Type.IMPOST);
            elemImpostList.stream().forEach(el -> el.paint());

            //Прорисовка штульпов
            LinkedList<ElemSimple> elemShtulpList = winc.listElem.filter(Type.SHTULP);
            elemShtulpList.stream().forEach(el -> el.paint());

            //Прорисовка стоек
            LinkedList<ElemSimple> elemStoikaList = winc.listElem.filter(Type.STOIKA);
            elemStoikaList.stream().forEach(el -> el.paint());

            //Прорисовка рам
            LinkedList<ElemSimple> elemFrameList = winc.listElem.filter(Type.FRAME_SIDE);
            elemFrameList.stream().forEach(el -> el.paint());

            //Прорисовка створок
            LinkedList<AreaSimple> elemStvorkaList = winc.listElem.filter(Type.STVORKA_SIDE);
            elemStvorkaList.stream().forEach(el -> el.paint());

            //Прорисовка раскладок
            //LinkedList<ElemSimple> glassList = winc.listElem.filter(Type.GLASS);
            //glassList.stream().forEach(el -> el.rascladkaPaint());
            //Прорисовка москиток
            LinkedList<ElemSimple> mosqList = winc.listElem.filter(Type.MOSKITKA);
            mosqList.stream().forEach(el -> el.paint());

            //Рисунок в память
//            if (winc.bufferImg != null) {
//                ByteArrayOutputStream byteArrOutStream = new ByteArrayOutputStream();
//                ImageIO.write(winc.bufferImg, "png", byteArrOutStream);
//                if (eProp.dev == true) {
//                    File outputfile = new File("CanvasImage.png");
//                    ImageIO.write(winc.bufferImg, "png", outputfile);
//                }
//            }
        } catch (Exception s) {
            System.err.println("Ошибка:AreaSimple.draw() " + s);
        }
    }

}
