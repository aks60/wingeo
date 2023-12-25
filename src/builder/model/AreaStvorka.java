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
import domain.eSyssize;
import enums.Layout;
import enums.LayoutHandle;
import enums.PKjson;
import enums.Type;
import enums.TypeJoin;
import enums.TypeOpen1;
import enums.TypeOpen2;
import frames.swing.DrawStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.LinkedList;
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
    private Polygon area2 = null; //полигон векторов сторон рамы

    public int handleColor = -3; //цвет ручки
    public int loopColor = -3; //цвет подвеса
    public int lockColor = -3; //цвет замка
    public int mosqColor = -3; //цвет москитки

    public double handleHeight = 60; //высота ручки
    public TypeOpen1 typeOpen = TypeOpen1.REQUEST; //направление открывания
    public LayoutHandle handleLayout = LayoutHandle.VARIAT; //положение ручки на створке      
    public boolean paramCheck[] = {true, true, true, true, true, true, true, true};
    public double offset[] = {0, 0, 0, 0};

    public AreaStvorka(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
        furniture(gson.param);
    }

    public void furniture(JsonObject param) {
        try {
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
                int index = sysfurnRec.getInt(eSysfurn.side_open);
                typeOpen = (index == TypeOpen2.REQ.id) ? typeOpen : (index == TypeOpen2.LEF.id) ? TypeOpen1.RIGH : TypeOpen1.LEFT;
            }
            //Положение ручки на створке
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
        } catch (Exception e) {
            System.err.println("Ошибка:AreaStvorka.furniture " + e);
        }
    }

    //Создание и коррекция сторон створки
    public void setLocation() {
        try {
            //Полигон векторов сторон створки
            this.area2 = (winc.listElem.filter(Type.IMPOST).isEmpty()) ? owner.area : this.area;  //случай когда створка в гл.окне 
            double delta = winc.syssizRec.getDbl(eSyssize.falz) + winc.syssizRec.getDbl(eSyssize.naxl);
            this.area = UGeo.geoPadding(this.area2, winc.listElem, -delta); //полигон векторов сторон створки

            //Координаты рам створок
            if (this.frames.size() == 0) {

                //Если стороны ств. ещё не созданы
                Coordinate[] coo = this.area.getCoordinates();
                for (int i = 0; i < coo.length - 1; i++) {
                    GsonElem gson = new GsonElem(Type.STVORKA_SIDE, coo[i].x, coo[i].y);
                    if (isJson(this.gson.param, PKjson.stvorkaSide[i])) {
                        gson.param = this.gson.param.getAsJsonObject(PKjson.stvorkaSide[i]);
                    }
                    ElemFrame sideStv = new ElemFrame(this.winc, gson.id + (.1 + Double.valueOf(i) / 10), gson, this);
                    this.frames.add(sideStv);
                }
            } else {
                //Если стороны уже созданы
                Coordinate[] coo = this.area.getCoordinates();
                for (int i = 0; i < coo.length - 1; i++) {
                    ElemSimple elem = this.frames.get(i);
                    elem.setDimension(coo[i].x, coo[i].y, coo[i + 1].x, coo[i + 1].y); //запишем координаты
                }
            }
            //Высотв ручки на створке
            ElemSimple stv = TypeOpen1.get(this, typeOpen);
            if (handleLayout == LayoutHandle.MIDL) {
                handleHeight = stv.length() / 2;
            } else {
                handleHeight = stv.length() / 2;
            }
        } catch (Exception e) {
            System.err.println("Ошибка:AreaStvorka.setLocation " + e);
        }
    }

    @Override
    public void joining() {
        LinkedList<ElemSimple> elemList = winc.listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.IMPOST, Type.STOIKA);
        try {
            //L - соединения
            for (int i = 0; i < this.frames.size(); i++) { //цикл по сторонам створки
                ElemFrame elem1 = (ElemFrame) this.frames.get(i);
                ElemFrame elem2 = (ElemFrame) this.frames.get((i == this.frames.size() - 1) ? 0 : i + 1);
                int lev1 = elem1.artiklRec.getInt(eArtikl.level1);
                int lev2 = elem2.artiklRec.getInt(eArtikl.level2);

                if ((lev1 == 1 && (lev2 == 1 || lev2 == 2)) == false) { //угловое левое/правое
                    TypeJoin type = (i == 0 || i == 2) ? TypeJoin.ANG2 : TypeJoin.ANG1;
                    winc.listJoin.add(new ElemJoining(this.winc, type, elem1, elem2));
                } else { //угловое на ус
                    winc.listJoin.add(new ElemJoining(this.winc, TypeJoin.ANGL, elem1, elem2));
                }
            }
            //Прилегающее
            LineSegment segm = new LineSegment();
            Coordinate coo1[] = this.area2.getCoordinates();  //полигон векторов сторон рамы
            Coordinate coo2[] = this.area.getCoordinates(); //полигон векторов сторон створки

            for (int j = 0; j < coo2.length - 1; j++) {
                segm.setCoordinates(coo1[j], coo1[j + 1]);
                ElemSimple elemFrm = UGeo.segMapElem(elemList, segm);
                segm.setCoordinates(coo2[j], coo2[j + 1]);
                ElemSimple elemStv = UGeo.segMapElem(this.frames, segm);
                winc.listJoin.add(new ElemJoining(this.winc, TypeJoin.FLAT, elemStv, elemFrm));
            }
        } catch (Exception e) {
            System.err.println("AreaStvorka.joining() " + e);
        }
    }

    public void paint() {
        if (typeOpen != TypeOpen1.EMPTY) {
            java.awt.Color color = winc.gc2d.getColor();
            winc.gc2d.setColor(new java.awt.Color(255, 000, 000));
            double DX = 20, DY = 60, X1, Y1;

            ElemSimple stv = TypeOpen1.get(this, typeOpen);
            int ind = UGeo.getIndex(this.area, stv);
            Coordinate p = UGeo.getSegment(area, ind, 0).midPoint();
            LineSegment s1 = UGeo.getSegment(area, ind, -1);
            LineSegment s2 = UGeo.getSegment(area, ind, +1);
            winc.gc2d.draw(new Line2D.Double(s1.p0.x, s1.p0.y, p.x, p.y));
            winc.gc2d.draw(new Line2D.Double(s2.p1.x, s2.p1.y, p.x, p.y)); 
            
            X1 = stv.x1() - stv.artiklRec.getDbl(eArtikl.height) / 2;
            Y1 = stv.y1() + (stv.y2() - stv.y1()) / 2;
//            if (root.type == Type.DOOR) {
//                DY = 20;
//                winc.gc2d.rotate(Math.toRadians(-90), X1 - DX, Y1 - DY);
//                DrawStroke.strokePolygon(winc, X1 - DX, X1 + DX, X1 + DX, X1 - DX, Y1 - DY, Y1 - DY, Y1 + DY, Y1 + DY, 0xFFFFFFFF, Color.BLACK);
//                DX = DX - 12;
//                Y1 = Y1 + 20;
//                DY = 60;
//                DrawStroke.strokePolygon(winc, X1 - DX, X1 + DX, X1 + DX, X1 - DX, Y1 - DY, Y1 - DY, Y1 + DY, Y1 + DY, 0xFFFFFFFF, Color.BLACK);
//
//            } else {
//                int handlRGB = eColor.find(this.handleColor).getInt(eColor.rgb);
//                DrawStroke.strokePolygon(winc, X1 - DX, X1 + DX, X1 + DX, X1 - DX, Y1 - DY, Y1 - DY, Y1 + DY, Y1 + DY, handlRGB, Color.BLACK);
//                DX = DX - 12;
//                Y1 = Y1 + 20;
//            }
            winc.gc2d.setColor(color);
        }
    }
}
