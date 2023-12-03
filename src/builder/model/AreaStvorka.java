package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import com.google.gson.JsonObject;
import dataset.Record;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eElement;
import domain.eSysfurn;
import enums.Layout;
import enums.LayoutHandle;
import enums.PKjson;
import enums.Type;
import enums.TypeOpen1;
import enums.TypeOpen2;
import java.awt.Shape;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

public class AreaStvorka extends AreaSimple {

    public Record sysfurnRec = eSysfurn.up.newRecord(); //фурнитура
    public Record handleRec = eArtikl.virtualRec(); //ручка
    public Record loopRec = eArtikl.virtualRec(); //подвес(петли)
    public Record lockRec = eArtikl.virtualRec(); //замок
    public Record mosqRec = eArtikl.virtualRec(); //москитка
    public Record elementRec = eElement.up.newRecord(); //состав москидки  

    public int handleColor = -3; //цвет ручки
    public int loopColor = -3; //цвет подвеса
    public int lockColor = -3; //цвет замка
    public int mosqColor = -3; //цвет москитки

    public double handleHeight = 0; //высота ручки
    public TypeOpen1 typeOpen = TypeOpen1.INVALID; //направление открывания
    public LayoutHandle handleLayout = LayoutHandle.VARIAT; //положение ручки на створке      
    public boolean paramCheck[] = {true, true, true, true, true, true, true, true};
    public double offset[] = {0, 0, 0, 0};

    public AreaStvorka(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
        furniture(gson.param);
    }

    public void furniture(JsonObject param) {

        //ElemSimple stvLeft = frames.get(Layout.LEFT);
        //Фурнитура створки, ручка, подвес
        if (isJson(param, PKjson.sysfurnID)) {
            sysfurnRec = eSysfurn.find2(param.get(PKjson.sysfurnID).getAsInt());
            paramCheck[0] = false;
        } else {
            sysfurnRec = eSysfurn.find3(winc.nuni); //ищем первую в системе
        }
        //Ручка
        if (isJson(param, PKjson.artiklHandl)) {
            handleRec = eArtikl.find(param.get(PKjson.artiklHandl).getAsInt(), false);
            paramCheck[1] = false;
        } else {
            handleRec = eArtikl.find(sysfurnRec.getInt(eSysfurn.artikl_id1), false);
            paramCheck[1] = true;
        }
        //Текстура ручки
        if (isJson(param, PKjson.colorHandl)) {
            handleColor = param.get(PKjson.colorHandl).getAsInt();
            paramCheck[2] = false;
        } else {
            int colorFK = eArtdet.find(handleRec.getInt(eArtikl.id)).getInt(eArtdet.color_fk);
            handleColor = eColor.find3(colorFK).getInt(eColor.id);
            paramCheck[2] = true;
        }
        //Подвес (петли)
        if (isJson(param, PKjson.artiklLoop)) {
            loopRec = eArtikl.find(param.get(PKjson.artiklLoop).getAsInt(), false);
            paramCheck[3] = false;
        }
        //Текстура подвеса
        if (isJson(param, PKjson.colorLoop)) {
            loopColor = param.get(PKjson.colorLoop).getAsInt();
            paramCheck[4] = false;
        }
        //Замок
        if (isJson(param, PKjson.artiklLock)) {
            lockRec = eArtikl.find(param.get(PKjson.artiklLock).getAsInt(), false);
            paramCheck[5] = false;
        }
        //Текстура замка
        if (isJson(param, PKjson.colorLock)) {
            lockColor = param.get(PKjson.colorLock).getAsInt();
            paramCheck[6] = false;
        }
        //Сторона открывания
        if (isJson(param, PKjson.typeOpen)) {
            typeOpen = TypeOpen1.get(param.get(PKjson.typeOpen).getAsInt());
            paramCheck[7] = false;
        } else {
            typeOpen = (sysfurnRec.getInt(eSysfurn.side_open) == TypeOpen2.LEF.id) ? TypeOpen1.LEFT : TypeOpen1.RIGHT;
        }
        //Положение или высота ручки на створке
        if (isJson(param, PKjson.positionHandl)) {
            int position = param.get(PKjson.positionHandl).getAsInt();
            if (position == LayoutHandle.VARIAT.id) {
                handleLayout = LayoutHandle.VARIAT;
                handleHeight = param.get(PKjson.heightHandl).getAsInt();
            } else {
                handleLayout = (position == LayoutHandle.MIDL.id) ? LayoutHandle.MIDL : LayoutHandle.CONST;
                //handleHeight = stvLeft.height() / 2;
            }
        } else if (sysfurnRec.getInt(eSysfurn.hand_pos) == LayoutHandle.MIDL.id) {
            handleLayout = LayoutHandle.MIDL;
            //handleHeight = stvLeft.height() / 2;
        } else if (sysfurnRec.getInt(eSysfurn.hand_pos) == LayoutHandle.CONST.id) {
            handleLayout = LayoutHandle.CONST;
            //handleHeight = stvLeft.height() / 2;
        } else if (sysfurnRec.getInt(eSysfurn.hand_pos) == LayoutHandle.VARIAT.id) {
            handleLayout = LayoutHandle.VARIAT;
            //handleHeight = stvLeft.height() / 2;
        } else {
            handleLayout = LayoutHandle.MIDL; //по умолчанию
            //handleHeight = stvLeft.height() / 2;
        }
    }

    //Создание и коррекция сторон створки
    public void setLocation() {
        double naxl = -8;
        try {
            //Ареа створки
            Polygon geo = (owner == root) ? owner.geom : this.geom; //случай когда створка в гл.окне или внутр...
            this.geom = UGeo.geoPadding(geo, winc.listElem, naxl);
            Coordinate[] coo = this.geom.getCoordinates();

            //Координаты рам створок
            if (this.frames.size() == 0) {
                //Если стороны ств. ещё не созданы
                for (int i = 0; i < coo.length - 1; i++) {
                    GsonElem gselem = new GsonElem(Type.STVORKA_SIDE, coo[i].x, coo[i].y);
                    ElemFrame stvside = new ElemFrame(this.winc, gson.id + (.01 + Double.valueOf(i) / 100), gselem, this);
                    this.frames.add(stvside);
                }
            } else {
                //Если стороны уже созданы
                for (int i = 0; i < coo.length - 1; i++) {
                    ElemSimple elem = this.frames.get(i);
                    elem.setDimension(coo[i].x, coo[i].y, coo[i + 1].x, coo[i + 1].y); //запишем координаты
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:AreaStvorka.setLocation " + e);
        }
    }

    public void paint() {
        if (this.geom != null) {

            java.awt.Color color = winc.gc2d.getColor();
            Shape shape = new ShapeWriter().toShape(this.geom);

            winc.gc2d.setColor(new java.awt.Color(eColor.find(this.colorID2).getInt(eColor.rgb)));
            winc.gc2d.fill(shape);

            winc.gc2d.setColor(new java.awt.Color(255, 000, 000));
            winc.gc2d.draw(shape);
            winc.gc2d.setColor(color);
        }
    }
}
