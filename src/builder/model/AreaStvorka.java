package builder.model;

import builder.Wincalc;
import builder.making.TRecord;
import static builder.model.Com5t.gf;
import builder.script.GsonElem;
import common.UCom;
import dataset.Query;
import dataset.Record;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eElement;
import domain.eSysfurn;
import domain.eSyssize;
import enums.Layout;
import enums.LayoutHand;
import enums.PKjson;
import enums.Type;
import enums.TypeJoin;
import enums.TypeOpen1;
import enums.TypeOpen2;
import frames.swing.comp.Canvas;
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

    public TRecord spcRec = null; //спецификация москитки
    public Record sysfurnRec = eSysfurn.up.newRecord(Query.SEL); //фурнитура 
    public Record handRec[] = {eArtikl.virtualRec(), eArtikl.virtualRec()}; //ручка 0-вручную 1-авторасчёт
    public Record loopRec[] = {eArtikl.virtualRec(), eArtikl.virtualRec()}; //подвес(петли) 0-вручную 1-авторасчёт
    public Record lockRec[] = {eArtikl.virtualRec(), eArtikl.virtualRec()}; //замок 0-вручную 1-авторасчёт
    public Record mosqRec = eArtikl.virtualRec(); //москитка
    public Record elementRec = eElement.up.newRecord(Query.SEL); //состав москидки 

    public LineString lineOpenHor = null; //линии горизонт. открывания
    public LineString lineOpenVer = null; //линии вертик. открывания
    public Geometry imageHand = gf.createMultiPolygon(new Polygon[]{
        UGeo.newPolygon(0, 0, 0, 40, 10, 40, 10, 10, 30, 10, 30, 40, 40, 40, 40, 0),
        UGeo.newPolygon(10, 10, 10, 120, 30, 120, 30, 10)}); //ручка шаблон 
    public Geometry areaHand = null; //ручка открывания 
    public LineSegment segmentHand = null;
    public int handColor[] = {-3, -3}; //цвет ручки 0-вручную 1-авторасчёт
    public int loopColor[] = {-3, -3}; //цвет подвеса 0-вручную 1-авторасчёт
    public int lockColor[] = {-3, -3}; //цвет замка 0-вручную 1-авторасчёт
    public int mosqColor = -3; //цвет москитки вирт.

    public double handHeight = 0; //высота ручки
    public TypeOpen1 typeOpen = TypeOpen1.EMPTY; //направление открывания
    public LayoutHand handLayout = LayoutHand.MIDL; //положение ручки на створке      
    public double offset[] = {0, 0, 0, 0};

    public AreaStvorka(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
    }

    public void initStvorka() {
        try {
            //Если нет полигона створки в гл.окне то 'owner.area', иначе 'this.area', получается при распиле owner.area импостом
            Geometry frameBox = (UCom.filter(winc.listElem, Type.IMPOST).isEmpty()) || (root.type == Type.DOOR) ? owner.area.getGeometryN(0) : this.area.getGeometryN(0);

            //Полигон створки с учётом нахлёста 
            double dh = winc.syssizRec.getDbl(eSyssize.falz) + winc.syssizRec.getDbl(eSyssize.naxl);
            Polygon stvShell = UGeo.bufferGeometry(frameBox, winc.listElem, -dh, 0); //полигон векторов сторон створки с учётом нахл. 
            Coordinate[] coo = stvShell.getGeometryN(0).getCoordinates();
            for (int i = 0; i < coo.length - 1; i++) {

                double ID = this.id + (.1 + Double.valueOf(i) / 10);
                ElemSimple sideStv = this.frames.stream().filter(rec -> rec.id == ID).findFirst().orElse(null);

                if (sideStv != null) {
                    if (UPar.isFinite(this.gson.param, PKjson.stvorkaSide[i])) {
                        sideStv.gson.param = this.gson.param.getAsJsonObject(PKjson.stvorkaSide[i]); //обновил параметры в gson
                    }
                    sideStv.x1(coo[i].x);
                    sideStv.y1(coo[i].y);
                    coo[i].z = sideStv.id;
                } else {
                    GsonElem gson = new GsonElem(Type.STV_SIDE, coo[i].x, coo[i].y, "{}");
                    if (UPar.isFinite(this.gson.param, PKjson.stvorkaSide[i])) {
                        gson.param = this.gson.param.getAsJsonObject(PKjson.stvorkaSide[i]); //впихнул параметры в gson
                    }
                    ElemFrame newStv = new ElemFrame(this.winc, ID, gson, this);
                    this.frames.add(newStv);
                    coo[i].z = newStv.id;
                }
            }
            coo[coo.length - 1].z = coo[0].z;  //т.к в цикле нет последней точки  

        } catch (Exception e) {
            System.err.println("Ошибка:AreaStvorka.initStvorka() " + e);
        }
    }

    /**
     * Фурнитура выбирается вручную из списка системы либо первая в списке
     * системы.
     *
     * Ручка по умолчанию из сист. фурнитуры либо если есть подбирается из
     * детализации выбр. фурн. либо выбирается вручную из ручек фыбранной
     * фурнитуры. Цвет первая запись из текстуры артикулов или подбор из текстур
     * или вручную.
     *
     */
    public void initArtikle() {
        try {
            handRec[1] = eArtikl.virtualRec();
            loopRec[1] = eArtikl.virtualRec();
            lockRec[1] = eArtikl.virtualRec();
            typeOpen = TypeOpen1.EMPTY;
            lineOpenHor = null;
            lineOpenVer = null;
            handColor[0] = -3;
            loopColor[0] = -3;
            lockColor[0] = -3;
            handColor[1] = -3;
            loopColor[1] = -3;
            lockColor[1] = -3;

            super.initArtikle();

            //Поиск по параметру или первая запись из списка...
            //Фурнитура
            if (UPar.isFinite(gson.param, PKjson.sysfurnID)) {
                sysfurnRec = eSysfurn.find2(gson.param.get(PKjson.sysfurnID).getAsInt());
            } else { //по умолчанию
                sysfurnRec = eSysfurn.find3(winc.nuni); //ищем первую в системе
            }
            //Ручка
            if (UPar.isFinite(gson.param, PKjson.artiklHand)) {
                handRec[0] = eArtikl.find(gson.param.get(PKjson.artiklHand).getAsInt(), false);
            } else { //по умолчанию
                handRec[0] = eArtikl.find(sysfurnRec.getInt(eSysfurn.artikl_id1), false);
            }
            //Текстура ручки
            if (UPar.isFinite(gson.param, PKjson.colorHand)) {
                handColor[0] = gson.param.get(PKjson.colorHand).getAsInt();
            } else if (handColor[0] == -3) { //по умолчанию (первая в списке)
                handColor[0] = eArtdet.find(handRec[0].getInt(eArtikl.id)).getInt(eArtdet.color_fk);
                if (handColor[0] != -3 && handColor[0] < 0) { //если все текстуры группы
                    List<Record> recordList = eColor.filter(handColor[0]);
                    handColor[0] = recordList.get(0).getInt(eColor.id); //первая в списке группы
                }
            }
            //Подвес (петли)
            if (UPar.isFinite(gson.param, PKjson.artiklLoop)) {
                loopRec[0] = eArtikl.find(gson.param.get(PKjson.artiklLoop).getAsInt(), false);
            }
            //Текстура подвеса
            if (UPar.isFinite(this.gson.param, PKjson.colorLoop)) {
                loopColor[0] = gson.param.get(PKjson.colorLoop).getAsInt();
            }
            //Замок
            if (UPar.isFinite(this.gson.param, PKjson.artiklLock)) {
                lockRec[0] = eArtikl.find(this.gson.param.get(PKjson.artiklLock).getAsInt(), false);
            }
            //Текстура замка
            if (UPar.isFinite(gson.param, PKjson.colorLock)) {
                lockColor[0] = gson.param.get(PKjson.colorLock).getAsInt();
            }
            //Сторона открывания
            if (UPar.isFinite(gson.param, PKjson.typeOpen)) {
                typeOpen = TypeOpen1.get(gson.param.get(PKjson.typeOpen).getAsInt());
            } else {
                int index = sysfurnRec.getInt(eSysfurn.side_open);
                typeOpen = (index == TypeOpen2.REQ.id) ? typeOpen : (index == TypeOpen2.LEF.id) ? TypeOpen1.LEFT : TypeOpen1.RIGH;
            }
        } catch (Exception e) {
            System.err.println("Ошибка:AreaStvorka.initArtikle() " + e);
        }
    }

    //Создание и коррекция сторон створки
    public void setLocation() {
        Geometry frameBox = (UCom.filter(winc.listElem, Type.IMPOST).isEmpty())
                || (root.type == Type.DOOR) ? owner.area.getGeometryN(0) : this.area.getGeometryN(0);
        try {
            //Полигон створки с учётом нахлёста 
            double dh = winc.syssizRec.getDbl(eSyssize.falz) + winc.syssizRec.getDbl(eSyssize.naxl);
            Polygon stvShell = UGeo.bufferGeometry(frameBox, winc.listElem, -dh, 0); //полигон векторов сторон створки с учётом нахл.
            Coordinate[] coo = stvShell.getGeometryN(0).getCoordinates();
            for (int i = 0; i < coo.length - 1; i++) {
                //Запишем координаты
                ElemSimple elem = this.frames.get(i);
                coo[i].z = elem.id;
                elem.gson.x1 = coo[i].x;
                elem.gson.y1 = coo[i].y;
                elem.gson.x2 = coo[i + 1].x;
                elem.gson.y2 = coo[i + 1].y;
            }
            coo[coo.length - 1].z = coo[0].z;  //т.к в цикле нет последней точки

            Polygon stvInner = UGeo.bufferGeometry(stvShell, this.frames, 0, 0);
            Polygon stvFalz = UGeo.bufferGeometry(stvShell, this.frames, 0, 1);
            this.area = gf.createMultiPolygon(new Polygon[]{stvShell, stvInner, stvFalz, (Polygon) frameBox});

            //Высота ручки, линии открывания
            if (this.typeOpen != TypeOpen1.EMPTY) {

                ElemSimple stvside = TypeOpen1.getHand(this, this.typeOpen);
                int indexSideOpen = UGeo.getIndex(this.area, stvside.id);
                segmentHand = UGeo.getSegment(area, indexSideOpen).offset(-1 * this.artiklRec.getDbl(eArtikl.height) / 2 + 10); //линия сегмента ручки

                //Ручка задана параметром
                handHeight = segmentHand.getLength() / 2;
                if (UPar.isFinite(gson.param, PKjson.positionHand)) {
                    int position = gson.param.get(PKjson.positionHand).getAsInt();
                    if (position == LayoutHand.VAR.id) { //установлена на высоте (вариационная)
                        handLayout = LayoutHand.VAR;
                        if (UPar.isFinite(gson.param, PKjson.heightHand)) {
                            handHeight = gson.param.get(PKjson.heightHand).getAsInt();
                        }
                    } else { //по середине или константная (конст.-настраивается в коструктиве)
                        handLayout = (position == LayoutHand.MIDL.id) ? LayoutHand.MIDL : LayoutHand.CONST;
                    }
                }

                //Полигон ручки
                Coordinate cooHand = segmentHand.pointAlong(1 - (this.handHeight + 20) / segmentHand.getLength()); //положение ручки на створке
                AffineTransformation aff = new AffineTransformation().translationInstance(cooHand.x - 10, cooHand.y);
                Geometry imageHand = aff.transform(this.imageHand);
                aff.setToRotation(segmentHand.angle() - Math.PI / 2, cooHand.x - 10, cooHand.y);
                this.areaHand  = aff.transform(imageHand);

                //Линии гориз. открывания                                   
                Coordinate h = UGeo.getSegment(area, indexSideOpen).midPoint();
                LineSegment s1 = UGeo.getSegment(area, indexSideOpen - 1);
                LineSegment s2 = UGeo.getSegment(area, indexSideOpen + 1);
                lineOpenHor = gf.createLineString(UGeo.arrCoord(s1.p0.x, s1.p0.y, h.x, h.y, s2.p1.x, s2.p1.y, h.x, h.y));

                //Линии вертик. открывания
                if (typeOpen == TypeOpen1.LEFTUP || typeOpen == TypeOpen1.RIGHUP) {
                    ElemSimple stv2 = UCom.layout(this.frames, Layout.TOP);
                    indexSideOpen = UGeo.getIndex(this.area, stv2.id);
                    Coordinate p2 = UGeo.getSegment(area, indexSideOpen).midPoint();
                    s1 = UGeo.getSegment(area, indexSideOpen - 1);
                    s2 = UGeo.getSegment(area, indexSideOpen + 1);
                    lineOpenVer = gf.createLineString(UGeo.arrCoord(p2.x, p2.y, s1.p0.x, s1.p0.y, p2.x, p2.y, s2.p1.x, s2.p1.y));
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:AreaStvorka.setLocation " + e);
        }
    }

    //L - соединения, прил.соед.
    @Override
    public void addJoining() {
        ArrayList<ElemSimple> elemList = UCom.filter(winc.listElem, Type.BOX_SIDE, Type.STV_SIDE, Type.IMPOST, Type.STOIKA, Type.SHTULP);
        //Geometry frameBox = (UCom.filter(winc.listElem, Type.IMPOST).isEmpty()) || (root.type == Type.DOOR) ? owner.area.getGeometryN(0) : this.area.getGeometryN(0);
        Geometry frameBox = this.area.getGeometryN(3);
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
            Coordinate coo2[] = frameBox.getGeometryN(0).getCoordinates(); //полигон векторов сторон рамы

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
        if (winc.sceleton == false) {
            if (this.imageHand != null && winc.sceleton == false) {
                winc.gc2d.setColor(new java.awt.Color(0, 0, 0));

                if (this.lineOpenHor != null) { //линии горизонт. открывания
                    Shape shape = new ShapeWriter().toShape(this.lineOpenHor);
                    winc.gc2d.draw(shape);
                }
                if (this.lineOpenVer != null) { //линии вертик. открывания
                    Shape shape = new ShapeWriter().toShape(this.lineOpenVer);
                    winc.gc2d.draw(shape);
                }
                //Для картинок в строках winc.canvas равен null
                if (winc.canvas != null) {
                    Shape shape = new ShapeWriter().toShape(areaHand);
                    int handColor2 = (handColor[1] == -3) ? handColor[0] : handColor[1];
                    Record colorRec = eColor.find(handColor2);
                    int rgb = colorRec.getInt(eColor.rgb);
                    winc.gc2d.setColor(new java.awt.Color(rgb));
                    winc.gc2d.fill(shape);
                    winc.gc2d.setColor(new java.awt.Color(000, 000, 000));
                    winc.gc2d.draw(shape);
                }
                if (timer.isRunning() == true) {
                    this.frames.stream().filter(e -> e.type == Type.STV_SIDE).forEach(e -> ((Com5t) e).timer.start());
                }
                //winc.gc2d.setColor(new java.awt.Color(0, 0, 0));
                //winc.gc2d.draw(shape);
            }
        } else {
            winc.gc2d.setColor(new java.awt.Color(000, 000, 255));
            for (int i = 0; i < 3; ++i) {
                Shape shape = new ShapeWriter().toShape(this.area.getGeometryN(i));
                winc.gc2d.draw(shape);
            }
        }
    }

}
