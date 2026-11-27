package builder.model;

import builder.Wincalc;
import builder.making.TFilling;
import builder.making.TRecord;
import builder.script.GsonElem;
import common.UCom;
import dataset.Query;
import dataset.Record;
import metadb.domain.eArtdet;
import metadb.domain.eArtikl;
import metadb.domain.eColor;
import metadb.domain.eJoining;
import metadb.domain.eJoinpar1;
import metadb.domain.eJoinvar;
import metadb.domain.eSystree;
import metadb.enums.PKjson;
import metadb.enums.TypeArt;
import metadb.enums.UseUnit;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.polygonize.Polygonizer;

public class ElemGlass extends ElemSimple {

    public double radius = 0; //радиус стекла
    public double gzazo = 0; //зазор между фальцем и стеклопакетом 
    public HashMap<Integer, Double> axisMap = new HashMap<Integer, Double>(); //размер от оси до стеклопакета
    public ElemSimple side_frame = null;
    public int side_index = 0;

    public Record rascRec = eArtikl.virtualRec(); //раскладка
    public Integer rascColorID = -3; //цвет раскладки
    public int[] rascNumber = {2, 2}; //количество проёмов раскладки 
    public List<Polygon> listRasc = new ArrayList();

    public ElemGlass(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
    }

    @Override
    public void initArtikle() {

        if (UPar.isFinite(gson.param, PKjson.artiklID)) {
            artiklRec = eArtikl.find(gson.param.get(PKjson.artiklID).getAsInt(), false);
        } else {
            Record sysreeRec = eSystree.find(winc.nuni); //по умолчанию стеклопакет
            artiklRec = eArtikl.find2(sysreeRec.getStr(eSystree.glas));
        }
        artiklRecAn = artiklRec;

        //Цвет стекла
        if (UPar.isFinite(gson.param, PKjson.colorID1)) {
            colorID1 = gson.param.get(PKjson.colorID1).getAsInt();
            colorID2 = colorID1;
            colorID3 = colorID1;
        } else {
            Record artdetRec = eArtdet.find(artiklRec.getInt(eArtikl.id));
            Record colorRec = eColor.find3(artdetRec.getInt(eArtdet.color_fk));
            colorID1 = colorRec.getInt(eColor.id);
            colorID2 = colorID1;
            colorID3 = colorID1;
        }

        //Раскладка
        if (UPar.isFinite(gson.param, PKjson.artiklRasc)) {
            rascRec = eArtikl.find(gson.param.get(PKjson.artiklRasc).getAsInt(), false);
            //Текстура
            if (UPar.isFinite(gson.param, PKjson.colorRasc)) {
                rascColorID = eColor.find(gson.param.get(PKjson.colorRasc).getAsInt()).getInt(eColor.id);
            } else {
                rascColorID = eArtdet.find(rascRec.getInt(eArtikl.id)).getInt(eArtdet.color_fk); //цвет по умолчанию
            }
            if (UPar.isFinite(gson.param, PKjson.horRasc)) {
                rascNumber[0] = gson.param.get(PKjson.horRasc).getAsInt(); //проёмы гориз.
            }
            if (UPar.isFinite(gson.param, PKjson.verRasc)) {
                rascNumber[1] = gson.param.get(PKjson.verRasc).getAsInt(); //проёмы вертик.
            }
        }
    }

    //Внутренний полигон створки/рамы для прорисовки
    @Override
    public void setLocation() {
        try {
            //Полигон по фальцу для прорисовки и рассчёта штапик...
            Geometry geoFalz = owner.area.getGeometryN(2);

            Coordinate[] coo = geoFalz.getCoordinates();
            if (geoFalz.getEnvelopeInternal().getMaxY() <= coo[0].y) {
                coo[0].z = coo[1].z;
                coo[1].z = coo[coo.length - 2].z;
                coo[2].z = coo[coo.length - 2].z;
                coo[coo.length - 1].z = coo[1].z;
            }

            //Раскладка
            if (this.rascRec.isVirtual() == false) {
                this.listRasc.clear();
                Polygon areaCanvas = UGeo.newPolygon(0, 0, 0, 10000, 10000, 10000, 10000, 0);
                Polygon areaInner = (Polygon) owner.area.getGeometryN(1);
                Envelope envRasc = areaInner.getEnvelopeInternal();
                double deltaH = Math.round(this.rascRec.getDbl(eArtikl.height) - this.rascRec.getDbl(eArtikl.size_centr)); //ширина
                final int numH = (gson.param.get(PKjson.horRasc) == null) ? 2 : gson.param.get(PKjson.horRasc).getAsInt();
                final int numV = (gson.param.get(PKjson.verRasc) == null) ? 2 : gson.param.get(PKjson.verRasc).getAsInt();
                final double lengthY = (envRasc.getMaxY() - envRasc.getMinY()) / numV, lengthX = (envRasc.getMaxX() - envRasc.getMinX()) / numH;
                double h = 0, w = 0;

                //Полигон раскладки
                if (owner.area.getNumPoints() < Com5t.MAXSIDE) {

                    for (int i = 1; i < numV; i++) {
                        h = h + lengthY;
                        LineSegment segmRasc = new LineSegment(new Coordinate(envRasc.getMinX(), envRasc.getMinY() + h), new Coordinate(envRasc.getMaxX(), envRasc.getMinY() + h));
                        LineSegment offsetSegment[] = {segmRasc.offset(+deltaH), segmRasc.offset(-deltaH)}; //левый и правый сегмент вдоль раскладки

                        //Точки пересечения канвы сегментами раскладки                       
                        Coordinate C1[] = UGeo.crossGeoOfLine(areaCanvas, offsetSegment[0]);
                        Coordinate C2[] = UGeo.crossGeoOfLine(areaCanvas, offsetSegment[1]);

                        //Ареа  раскладки
                        Polygon areaEnvelope = UGeo.newPolygon(C2[0].x, C2[0].y, C1[0].x, C1[0].y, C1[1].x, C1[1].y, C2[1].x, C2[1].y);
                        Polygon areaRasc = (Polygon) areaEnvelope.intersection(areaInner); //полигон элемента конструкции 
                        this.listRasc.add(areaRasc);
                    }
                    for (int i = 1; i < numH; i++) {
                        w = w + lengthX;
                        LineSegment segmRasc = new LineSegment(new Coordinate(envRasc.getMinX() + w, envRasc.getMinY()), new Coordinate(envRasc.getMinX() + w, envRasc.getMaxX()));
                        LineSegment offsetSegment[] = {segmRasc.offset(+deltaH), segmRasc.offset(-deltaH)};  //левый и правый сегмент вдоль раскладки

                        //Точки пересечения канвы сегментами раскладки
                        Coordinate C1[] = UGeo.crossGeoOfLine(areaCanvas, offsetSegment[0]);
                        Coordinate C2[] = UGeo.crossGeoOfLine(areaCanvas, offsetSegment[1]);

                        //Ареа  раскладки
                        Polygon areaEnvelope = UGeo.newPolygon(C2[0].x, C2[0].y, C1[0].x, C1[0].y, C1[1].x, C1[1].y, C2[1].x, C2[1].y);
                        Polygon areaRasc = (Polygon) areaEnvelope.intersection(areaInner); //полигон элемента конструкции
                        this.listRasc.add(areaRasc);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error: ElemGlass.setLocation. " + e);
        }
    }

    //Главная спецификация    
    @Override
    public void setSpecific() {
        try {
            spcRec.place = "ЗАП";
            spcRec.artiklRec(artiklRec);
            spcRec.color(colorID1, colorID2, colorID3);

            //Фича определения gzazo и gaxis на раннем этапе построения. 
            new TFilling(winc, true).fill(this);

            //Полигон стеклопакета
            this.area = UGeo.bufferGeometry(owner.area.getGeometryN(0), winc.listElem, gzazo, 1);

            Envelope envGlass = this.area.getEnvelopeInternal();
            spcRec.width = envGlass.getWidth();
            spcRec.height = envGlass.getHeight();

            //Раскладка
            if (this.rascRec.isVirtual() == false) {
                this.listRasc.clear();
                double paramDw = 0;
                int artikl_id1 = rascRec.getInt(eArtikl.id);
                int artikl_id2 = owner.artiklRec.getInt(eArtikl.id);
                Query qJoining = new Query(eJoining.values()).sql(eJoining.data(), eJoining.artikl_id1, artikl_id1, eJoining.artikl_id2, artikl_id2, "and");
                if (!qJoining.isEmpty()) {
                    Query qJoinvar = new Query(eJoinvar.values()).sql(eJoinvar.data(), eJoinvar.joining_id, qJoining.get(0).getInt(eJoining.id), eJoinvar.types, 40, "and");
                    if (!qJoinvar.isEmpty()) {
                        Query qJoinpar1 = new Query(eJoinpar1.values()).sql(eJoinpar1.data(), eJoinpar1.joinvar_id, qJoinvar.get(0).getStr(eJoinvar.id), eJoinpar1.groups_id, "4044", "and");
                        if (!qJoinpar1.isEmpty()) {
                            paramDw = (qJoinpar1.get(0).get(eJoinpar1.text) instanceof Double) ? qJoinpar1.get(0).getDbl(eJoinpar1.text) : 0;
                        }
                    }
                }
                final double deltaBox = (paramDw == 0) ? -5 : paramDw;  //поправка по умолчанию 5мм с каждой стороны
                final double height_rasc = Math.round(this.rascRec.getDbl(eArtikl.height)); //ширина
                final int numH = (gson.param.get(PKjson.horRasc) == null) ? 2 : gson.param.get(PKjson.horRasc).getAsInt();
                final int numV = (gson.param.get(PKjson.verRasc) == null) ? 2 : gson.param.get(PKjson.verRasc).getAsInt();

                Polygon areaInner = (Polygon) owner.area.getGeometryN(1);
                Envelope envInner = areaInner.getEnvelopeInternal();

                //Полигоны раскладки
                if (owner.area.getNumPoints() < Com5t.MAXSIDE) {

                    double w = 0;
                    final double lengthX = (envInner.getMaxX() - envInner.getMinX()) / numH;
                    Polygon areaCanvas = UGeo.newPolygon(0, 0, 0, 10000, 10000, 10000, 10000, 0);
                    for (int i = 1; i < numH; i++) {
                        w = w + lengthX;
                        LineSegment segmVRasc = new LineSegment(new Coordinate(envInner.getMinX() + w, envInner.getMinY()), new Coordinate(envInner.getMinX() + w, envInner.getMaxY()));
                        LineSegment offsetVSegment[] = {segmVRasc.offset(+height_rasc / 2), segmVRasc.offset(-height_rasc / 2)}; //левый и правый сегмент вдоль раскладки

                        //Точки пересечения канвы сегментами раскладки
                        Coordinate C1[] = UGeo.crossGeoOfLine(areaCanvas, offsetVSegment[0]);
                        Coordinate C2[] = UGeo.crossGeoOfLine(areaCanvas, offsetVSegment[1]);

                        //Ареа  раскладки
                        Polygon areaVEnvelope = UGeo.newPolygon(C2[0].x, C2[0].y, C1[0].x, C1[0].y, C1[1].x, C1[1].y, C2[1].x, C2[1].y);
                        Polygon areaVRasc = (Polygon) areaVEnvelope.intersection(areaInner); //полигон элемента конструкции 
                        Envelope envVRasc = areaVRasc.getEnvelopeInternal();

                        TRecord spcAdd = new TRecord("ВСТ", null, rascRec, this, null);
                        spcAdd.width = envVRasc.getMaxY() - envVRasc.getMinY() - deltaBox * 2;
                        spcAdd.height = rascRec.getDbl(eArtikl.height);
                        spcAdd.color(this.rascColorID, this.rascColorID, this.rascColorID);
                        spcAdd.setAnglCut(90, 90);
                        this.spcRec.spcList.add(spcAdd); //в спецификацию
                    }

                    double h = 0;
                    final double lengthY = (envInner.getMaxY() - envInner.getMinY()) / numV;
                    for (int k = 1; k < numV; k++) {
                        h = h + lengthY;
                        LineSegment segmHRasc = new LineSegment(new Coordinate(envInner.getMinX(), envInner.getMinY() + h), new Coordinate(envInner.getMaxX(), envInner.getMinY() + h));
                        LineSegment offsetHSegment[] = {segmHRasc.offset(+height_rasc / 2), segmHRasc.offset(-height_rasc / 2)};  //левый и правый сегмент вдоль раскладки

                        //Точки пересечения канвы сегментами раскладки
                        Coordinate C3[] = UGeo.crossGeoOfLine(areaCanvas, offsetHSegment[0]);
                        Coordinate C4[] = UGeo.crossGeoOfLine(areaCanvas, offsetHSegment[1]);

                        //Ареа  раскладки
                        Polygon areaHEnvelope = UGeo.newPolygon(C4[0].x, C4[0].y, C3[0].x, C3[0].y, C3[1].x, C3[1].y, C4[1].x, C4[1].y);
                        Polygon areaHRasc = (Polygon) areaHEnvelope.intersection(areaInner); //полигон элемента конструкции
                        Envelope envHRasc = areaHRasc.getEnvelopeInternal();

                        TRecord spcAdd = new TRecord("ВСТ", null, rascRec, this, null);
                        spcAdd.width = (envHRasc.getMaxX() - envHRasc.getMinX() - deltaBox * 2 - height_rasc * (numH - 1)) / numH;
                        spcAdd.height = rascRec.getDbl(eArtikl.height);
                        spcAdd.color(this.rascColorID, this.rascColorID, this.rascColorID);
                        spcAdd.setAnglCut(90, 90);
                        this.spcRec.spcList.add(spcAdd); //в спецификацию
                    }
                }
            }
            //Test.init() 
        } catch (Exception e) {
            System.err.println("Error: ElemGlass.setSpecific() " + e);
        }
    }

    //Вложенная спецификация
    @Override
    public void addSpecific(TRecord spcAdd) {
        try {
            if (spcAdd.artiklRec.getStr(eArtikl.code).substring(0, 1).equals("@")) {
                return;
            }
            spcAdd.count = UPar.to_11030_12060_14030_15040_25060_33030_34060_38030_39060(spcAdd); //кол. ед. с учётом парам. 
            spcAdd.count += UPar.to_14050_24050_33050_38050(spcRec, spcAdd); //кол. ед. с шагом
            spcAdd.width += UPar.to_12050_15050_34051_39020(spcAdd); //поправка мм         
            if (TypeArt.X502.isType(spcAdd.artiklRec)) {
                return;  //если стеклопакет сразу выход
            }
            //Ареа фальца
            Geometry geoFalz = owner.area.getGeometryN(2);

            //Погонные метры.
            if (UseUnit.METR.id == spcAdd.artiklRec.getInt(eArtikl.unit)) {

                Coordinate coo[] = geoFalz.getCoordinates();
                spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);
                spcAdd.anglHoriz = UGeo.anglHor(side_frame.x1(), side_frame.y1(), side_frame.x2(), side_frame.y2()); //угол к горизонту 

                //Арка
                if (side_frame.h() != null) {
                    int index = IntStream.range(1, coo.length).filter(k -> coo[k].z == side_frame.id).findFirst().getAsInt();
                    spcAdd.anglCut0 = UGeo.anglCut(spcAdd, geoFalz, coo.length - 2, 0, '-');
                    spcAdd.anglCut1 = UGeo.anglCut(spcAdd, geoFalz, index - 1, index, '+');
                    spcAdd.width += UGeo.lengthCurve(geoFalz, side_frame.id);

                    //Остальное
                } else {
                    Coordinate[] c1 = {coo[UGeo.getIndex(coo.length, side_index - 1)], coo[side_index], coo[UGeo.getIndex(coo.length, side_index + 1)]};
                    Coordinate[] c2 = {coo[side_index], coo[UGeo.getIndex(coo.length, side_index + 1)], coo[UGeo.getIndex(coo.length, side_index + 2)]};
                    double angBetween0 = Math.toDegrees(Angle.angleBetween(c1[0], c1[1], c1[2]));
                    double angBetween1 = Math.toDegrees(Angle.angleBetween(c2[0], c2[1], c2[2]));
                    spcAdd.anglCut0 = Math.abs(angBetween0 - UGeo.anglCut(spcAdd, geoFalz, UGeo.getIndex(coo.length, side_index - 1), side_index, '-'));
                    spcAdd.anglCut1 = Math.abs(angBetween1 - UGeo.anglCut(spcAdd, geoFalz, UGeo.getIndex(coo.length, side_index), UGeo.getIndex(coo.length, side_index + 1), '+'));

                    spcAdd.width += coo[side_index].distance(coo[side_index + 1]);

                }

                spcRec.spcList.add(spcAdd);

                //Параметры по горизонтали
                double angHor = spcAdd.anglHoriz;
                if ((angHor > 315 && angHor < 360 || angHor >= 0 && angHor < 45) || (angHor > 135 && angHor < 225)) {
                    if (spcAdd.mapParam.get(15010) != null) {
                        if ("Нет".equals(spcAdd.mapParam.get(15010)) == false) { //Усекать нижний штапик
                            spcAdd.width = spcAdd.width - 2 * spcAdd.height;
                        }
                    }
                    if (spcAdd.mapParam.get(15011) != null) {
                        if ("усекать боковой".equals(spcAdd.mapParam.get(15011))) { //Расчет реза штапика
                            spcAdd.width = spcAdd.width - 2 * spcAdd.height;
                        }
                    }

                    //Параметры по вертикали
                } else if ((angHor > 225 && angHor < 315) || (angHor > 45 && angHor < 135)) {
                    if (spcAdd.mapParam.get(15010) != null) {
                        if ("Да".equals(spcAdd.mapParam.get(15010)) == false) { //Усекать нижний штапик
                            spcAdd.width = spcAdd.width - 2 * spcAdd.height;
                        }
                    }
                    if (spcAdd.mapParam.get(15011) != null) {
                        if ("усекать нижний".equals(spcAdd.mapParam.get(15011))) { //Расчет реза штапика
                            spcAdd.width = spcAdd.width - 2 * spcAdd.height;
                        }
                    }
                }
                if ("по биссектрисе".equals(spcAdd.mapParam.get(15011))) { //Расчет реза штапика
                    //
                }
                spcAdd.width = UPar.to_12065_15045_25040_34070_39070(spcAdd); //длина мм
                spcAdd.width = spcAdd.width * UPar.to_12030_15030_25035_34030_39030(spcAdd); //"[ * коэф-т ]"
                spcAdd.width = spcAdd.width / UPar.to_12040_15031_25036_34040_39040(spcAdd); //"[ / коэф-т ]" 

                //Штуки   
            } else if (UseUnit.PIE.id == spcAdd.artiklRec.getInt(eArtikl.unit)) {

                if (spcAdd.mapParam.get(13014) != null) {
                    LineSegment s2 = UGeo.getSegment(geoFalz, side_index);
                    spcAdd.anglHoriz = UGeo.anglHor(s2.p0.x, s2.p0.y, s2.p1.x, s2.p1.y); //угол к горизонту                     
                    if (UCom.containsNumbJust(spcAdd.mapParam.get(13014), spcAdd.anglHoriz) == true) { //Углы ориентации стороны
                        spcRec.spcList.add(spcAdd);
                    }
                } else {
                    spcRec.spcList.add(spcAdd);
                }
            } else {
                System.err.println("Элемент не обработан");
            }

            //Test.init(owner.area.getGeometryN(0), owner.area.getGeometryN(1), owner.area.getGeometryN(2));
        } catch (Exception e) {
            System.err.println("Error: ElemGlass.addSpecific()  " + e);
        }
    }

    //Линии размерности
    @Override
    public void paint() {
        try {
            Geometry geoFalz = owner.area.getGeometryN(2);
            if (geoFalz != null && winc.sceleton == false) {

                winc.gc2d.setColor(this.color());
                Shape shape = new ShapeWriter().toShape(geoFalz);
                winc.gc2d.fill(shape);

                if (this.rascRec.isVirtual() == false) {
                    winc.gc2d.setStroke(new BasicStroke(1));
                    if (owner.area.getNumPoints() < Com5t.MAXSIDE) {
                        Record colorRasc = eColor.find(this.rascColorID);
                        winc.gc2d.setColor(new Color(colorRasc.getInt(eColor.rgb)));
                        for (Polygon p : this.listRasc) {
                            shape = new ShapeWriter().toShape(p);
                            winc.gc2d.setColor(new Color(colorRasc.getInt(eColor.rgb)));
                            winc.gc2d.fill(shape);
                            winc.gc2d.setColor(new java.awt.Color(000, 000, 000));
                            winc.gc2d.draw(shape);
                        }
                    }
                }

            } else if (geoFalz != null) {
                //
            }
        } catch (Exception e) {
            System.err.println("Error: ElemClass.paint() " + e);
        }
    }

    public Double width() {
        return owner.area.getGeometryN(2).getEnvelopeInternal().getWidth();
    }

    public Double height() {
        return owner.area.getGeometryN(2).getEnvelopeInternal().getHeight();
    }
}
