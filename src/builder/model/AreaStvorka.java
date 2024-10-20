package builder.model;

import builder.Wincalc;
import builder.making.SpcRecord;
import builder.script.GsonElem;
import com.google.gson.JsonObject;
import common.UCom;
import dataset.Query;
import dataset.Record;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eElement;
import domain.eSysfurn;
import domain.eSysprof;
import domain.eSyssize;
import enums.Layout;
import enums.LayoutKnob;
import enums.PKjson;
import enums.Type;
import enums.TypeJoin;
import enums.TypeOpen1;
import enums.TypeOpen2;
import enums.UseSideTo;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.AffineTransformation;

public class AreaStvorka extends AreaSimple {

    public SpcRecord spcRec = null; //спецификация москитки
    public Record sysfurnRec = eSysfurn.up.newRecord(Query.SEL); //фурнитура
    public Record knobRec = eArtikl.virtualRec(); //ручка
    public Record loopRec = eArtikl.virtualRec(); //подвес(петли)
    public Record lockRec = eArtikl.up.newRecord(Query.SEL); //замок
    public Record mosqRec = eArtikl.virtualRec(); //москитка
    public Record elementRec = eElement.up.newRecord(Query.SEL); //состав москидки 

    public Geometry frameBox = null; //полигон векторов сторон рамы
    public LineString lineOpenHor = null; //линии горизонт. открывания
    public LineString lineOpenVer = null; //линии вертик. открывания
    public Polygon knobOpen = null; //ручка открывания    
    public int knobColor = -3; //цвет ручки вирт...
    public int loopColor = -3; //цвет подвеса вирт...
    public int lockColor = -3; //цвет замка вирт...
    public int mosqColor = -3; //цвет москитки вирт...

    public double knobHeight = 0; //высота ручки
    public TypeOpen1 typeOpen = TypeOpen1.EMPTY; //направление открывания
    public LayoutKnob knobLayout = LayoutKnob.MIDL; //положение ручки на створке      
    public double offset[] = {0, 0, 0, 0};

    public AreaStvorka(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
        initArtikle(gson.param);
    }

    /**
     * Фурнитура выбирается вручную из списка системы либо первая в списке
     * системы.
     *
     * Ручка по умолчанию из сист. фурнитуры либо если есть подбирается из детализации
     * выбр. фурн. либо выбирается вручную из ручек фыбранной фурнитуры. Цвет
     * первая запись из текстуры артикулов или подбор из текстур или вручную.
     *
     */
    public void initArtikle(JsonObject param) {
        try {
            //Поиск по параметру или первая запись из списка...
            //Фурнитура
            if (isJson(param, PKjson.sysfurnID)) {
                sysfurnRec = eSysfurn.find2(param.get(PKjson.sysfurnID).getAsInt());
            } else { //по умолчанию
                sysfurnRec = eSysfurn.find3(winc.nuni); //ищем первую в системе
            }
            //Ручка
            if (isJson(param, PKjson.artiklKnob)) {
                knobRec = eArtikl.find(param.get(PKjson.artiklKnob).getAsInt(), false);
            } else { //по умолчанию
                knobRec = eArtikl.find(sysfurnRec.getInt(eSysfurn.artikl_id1), false);
            }
            //Текстура ручки
            if (isJson(param, PKjson.colorKnob)) {
                knobColor = param.get(PKjson.colorKnob).getAsInt();
            } else if (knobColor == -3) { //по умолчанию (первая в списке)
                knobColor = eArtdet.find(knobRec.getInt(eArtikl.id)).getInt(eArtdet.color_fk);
                if (knobColor < 0) { //если все текстуры группы
                    List<Record> recordList = eColor.filter(knobColor);
                    if (recordList.isEmpty() == false) {
                        knobColor = eColor.filter(knobColor).get(0).getInt(eColor.id);
                    }
                }
            }
            //Подвес (петли)
            if (isJson(param, PKjson.artiklLoop)) {
                loopRec = eArtikl.find(param.get(PKjson.artiklLoop).getAsInt(), false);
            } 
            //Текстура подвеса
            if (isJson(param, PKjson.colorLoop)) {
                loopColor = param.get(PKjson.colorLoop).getAsInt();
            }
            //Замок
            if (isJson(param, PKjson.artiklLock)) {
                lockRec = eArtikl.find(param.get(PKjson.artiklLock).getAsInt(), false);
            }
            //Текстура замка
            if (isJson(param, PKjson.colorLock)) {
                lockColor = param.get(PKjson.colorLock).getAsInt();
            }
            //Сторона открывания
            if (isJson(param, PKjson.typeOpen)) {
                typeOpen = TypeOpen1.get(param.get(PKjson.typeOpen).getAsInt());
            } else {
                int index = sysfurnRec.getInt(eSysfurn.side_open);
                typeOpen = (index == TypeOpen2.REQ.id) ? typeOpen : (index == TypeOpen2.LEF.id) ? TypeOpen1.RIGH : TypeOpen1.LEFT;
            }
            //Положение ручки на створке, ручка задана параметром
            if (isJson(param, PKjson.positionKnob)) {
                int position = param.get(PKjson.positionKnob).getAsInt();
                if (position == LayoutKnob.VAR.id) { //вариационная
                    knobLayout = LayoutKnob.VAR;
                    if (isJson(param, PKjson.heightKnob)) {
                        knobHeight = param.get(PKjson.heightKnob).getAsInt();
                        if (isJson(param, PKjson.heightKnob)) {
                            knobHeight = param.get(PKjson.heightKnob).getAsInt();
                        }
                    }
                } else { //по середине или константная
                    knobLayout = (position == LayoutKnob.MIDL.id) ? LayoutKnob.MIDL : LayoutKnob.CONST;
                    //knobHeight = owner.area.getEnvelopeInternal().getHeight() / 2;
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:AreaStvorka.initArtikle() " + e);
        }
    }

    //Создание и коррекция сторон створки
    public void setLocation() {
        try {
            //owner.area - если нет полигона створки в гл.окне 
            //this.area  - получатется при распиле owner.area импостом
            this.frameBox = (UCom.filter(winc.listElem, Type.IMPOST).isEmpty())
                    || (root.type == Type.DOOR) ? owner.area.getGeometryN(0) : this.area.getGeometryN(0);

            //Полигон створки с учётом нахлёста 
            double dh = winc.syssizRec.getDbl(eSyssize.falz) + winc.syssizRec.getDbl(eSyssize.naxl);
            Polygon stvOuter = buffer(this.frameBox, winc.listElem, -dh); //полигон векторов сторон створки с учётом нахл. 

            //Если стороны ств. ещё не созданы 
            if (this.frames.isEmpty()) {
                Coordinate[] coo = stvOuter.getGeometryN(0).getCoordinates();
                for (int i = 0; i < coo.length - 1; i++) {

                    //Координаты рам створок
                    GsonElem gson = new GsonElem(Type.STVORKA_SIDE, coo[i].x, coo[i].y);
                    //Впихнул параметры в gson
                    if (isJson(this.gson.param, PKjson.stvorkaSide[i])) {
                        gson.param = this.gson.param.getAsJsonObject(PKjson.stvorkaSide[i]);
                    }
                    ElemFrame sideStv = new ElemFrame(this.winc, this.id + (.1 + Double.valueOf(i) / 10), gson, this);
                    this.frames.add(sideStv);
                    coo[i].z = sideStv.id;
                }
                coo[coo.length - 1].z = coo[0].z;  //т.к в цикле нет последней точки

            } else { //Если стороны уже созданы
                Coordinate[] coo = stvOuter.getGeometryN(0).getCoordinates();
                for (int i = 0; i < coo.length - 1; i++) {
                    ElemSimple elem = this.frames.get(i);
                    coo[i].z = elem.id;
                    elem.setDimension(coo[i].x, coo[i].y, coo[i + 1].x, coo[i + 1].y); //запишем координаты
                }
                coo[coo.length - 1].z = coo[0].z;  //т.к в цикле нет последней точки
            }

            Polygon stvInner = buffer(stvOuter, this.frames, 0);
            this.area = gf.createMultiPolygon(new Polygon[]{stvOuter, stvInner});

            //Высота ручки, линии открывания
            if (this.typeOpen != TypeOpen1.EMPTY) {

                if (isJson(gson.param, PKjson.positionKnob) == false) {
                    if (sysfurnRec.getInt(eSysfurn.hand_pos) == LayoutKnob.MIDL.id) { //по середине
                        knobLayout = LayoutKnob.MIDL;
                        knobHeight = this.area.getEnvelopeInternal().getHeight() / 2;
                    } else if (sysfurnRec.getInt(eSysfurn.hand_pos) == LayoutKnob.CONST.id) { //константная
                        knobLayout = LayoutKnob.CONST;
                        knobHeight = this.area.getEnvelopeInternal().getHeight() / 2;
                    }
                }

                //Линии гориз. открывания
                ElemSimple stvside = TypeOpen1.getKnob(this, this.typeOpen);
                int ind = UGeo.getIndex(this.area, stvside.id);
                Coordinate h = UGeo.getSegment(area, ind).midPoint(); //высота ручки по умолчанию
                LineSegment s1 = UGeo.getSegment(area, ind - 1);
                LineSegment s2 = UGeo.getSegment(area, ind + 1);
                lineOpenHor = gf.createLineString(UGeo.arrCoord(s1.p0.x, s1.p0.y, h.x, h.y, s2.p1.x, s2.p1.y, h.x, h.y));

                //Линии вертик. открывания
                if (typeOpen == TypeOpen1.LEFTUP || typeOpen == TypeOpen1.RIGHUP) {
                    ElemSimple stv2 = UCom.layout(this.frames, Layout.TOP);
                    ind = UGeo.getIndex(this.area, stv2.id);
                    Coordinate p2 = UGeo.getSegment(area, ind).midPoint();
                    s1 = UGeo.getSegment(area, ind - 1);
                    s2 = UGeo.getSegment(area, ind + 1);
                    lineOpenVer = gf.createLineString(UGeo.arrCoord(p2.x, p2.y, s1.p0.x, s1.p0.y, p2.x, p2.y, s2.p1.x, s2.p1.y));
                }
                //Полигон ручки
                double DX = 10, DY = 60;
                if (knobLayout == LayoutKnob.VAR && this.knobHeight != 0) {
                    LineSegment lineSegm = UGeo.getSegment(area, ind);
//                    h = lineSegm.pointAlong(1 - (this.knobHeight / lineSegm.getLength())); //высота ручки на створке
                    h = lineSegm.pointAlong((this.knobHeight / lineSegm.getLength())); //высота ручки на створке
                }
                Record sysprofRec = eSysprof.find5(winc.nuni, stvside.type.id2, UseSideTo.ANY, UseSideTo.ANY); //ТАК ДЕЛАТЬ НЕЛЬЗЯ...
                Record artiklRec = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), false); //артикул
                double dx = artiklRec.getDbl(eArtikl.height) / 2;
                if (typeOpen == TypeOpen1.UPPER) {
                    h.y = (typeOpen == TypeOpen1.LEFT || typeOpen == TypeOpen1.LEFTUP) ? h.y - 2 * dx : h.y + 2 * dx;
                } else {
                    h.x = (typeOpen == TypeOpen1.LEFT || typeOpen == TypeOpen1.LEFTUP) ? h.x - dx : h.x + dx;
                }
                if (root.type == Type.DOOR) {
                    this.knobOpen = gf.createPolygon(UGeo.arrCoord(h.x - DX, h.y - DY, h.x + DX, h.y - DY, h.x + DX, h.y + DY, h.x - DX, h.y + DY));
                } else {
                    this.knobOpen = gf.createPolygon(UGeo.arrCoord(h.x - DX, h.y - DY, h.x + DX, h.y - DY, h.x + DX, h.y + DY, h.x - DX, h.y + DY));
                }
                //Направление открывания
                if (typeOpen != TypeOpen1.UPPER) {
                    double anglHoriz = UGeo.anglHor(stvside.x1(), stvside.y1(), stvside.x2(), stvside.y2());
                    if (!(anglHoriz == 90 || anglHoriz == 270)) {
                        AffineTransformation aff = new AffineTransformation();
                        aff.setToRotation(Math.toRadians(anglHoriz), this.knobOpen.getCentroid().getX(), this.knobOpen.getCentroid().getY());
                        this.knobOpen = (Polygon) aff.transform(this.knobOpen);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:AreaStvorka.setLocation " + e);
        }
    }

    //L - соединения, прил.соед.
    @Override
    public void addJoining() {
        ArrayList<ElemSimple> elemList = UCom.filter(winc.listElem, Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.IMPOST, Type.STOIKA, Type.SHTULP);
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
            Coordinate coo1[] = this.area.getGeometryN(0).getCoordinates(); //полигон векторов сторон створки
            Coordinate coo2[] = this.frameBox.getGeometryN(0).getCoordinates(); //полигон векторов сторон рамы

            for (int j = 0; j < coo1.length - 1; j++) {
               final double id1 = coo1[j].z;
                ElemSimple elemStv = elemList.stream().filter(e -> e.id == id1).findFirst().get();
               final double id2 = coo2[j].z;
                ElemSimple elemFrm = elemList.stream().filter(e -> e.id == id2).findFirst().get();
                if (elemStv != null && elemFrm != null) {
                    winc.listJoin.add(new ElemJoining(this.winc, TypeJoin.FLAT, elemStv, elemFrm));
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:AreaStvorka.joining() " + e);
        }
    }

    public void paint() {
        if (this.knobOpen != null) {
            winc.gc2d.setColor(new java.awt.Color(0, 0, 0));

            if (this.lineOpenHor != null) { //линии горизонт. открывания
                Shape shape = new ShapeWriter().toShape(this.lineOpenHor);
                winc.gc2d.draw(shape);
            }
            if (this.lineOpenVer != null) { //линии вертик. открывания
                Shape shape = new ShapeWriter().toShape(this.lineOpenVer);
                winc.gc2d.draw(shape);
            }
            Shape shape = new ShapeWriter().toShape(this.knobOpen);
            Record colorRec = eColor.find(knobColor);
            int rgb = colorRec.getInt(eColor.rgb);
            winc.gc2d.setColor(new java.awt.Color(rgb));
            winc.gc2d.fill(shape);

            if (timer.isRunning() == true) {
                this.frames.stream().filter(e -> e.type == Type.STVORKA_SIDE).forEach(e -> ((Com5t) e).timer.start());
            }
            winc.gc2d.setColor(new java.awt.Color(0, 0, 0));
            winc.gc2d.draw(shape);
        }
    }

}
