package builder.model;

import builder.Wincalc;
import builder.making.SpcFilling;
import builder.making.SpcRecord;
import builder.script.GsonElem;
import common.ArrayCom;
import common.UCom;
import common.GeoBuffer;
import dataset.Record;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eSystree;
import enums.PKjson;
import enums.Type;
import enums.TypeArtikl;
import enums.UseUnit;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LineSegment;

public class ElemGlass extends ElemSimple {

    public double radius = 0; //радиус стекла
    public double gzazo = 0; //зазор между фальцем и стеклопакетом 
    public HashMap<Integer, Double> axisMap = new HashMap<Integer, Double>(); //размер от оси до стеклопакета
    public ElemSimple frameGlass = null;
    public int sideClass = 0;

    public Record rasclRec = eArtikl.virtualRec(); //раскладка
    public int rasclColor = -3; //цвет раскладки
    public int rasclNumber[] = {2, 2}; //количество проёмов раскладки     

    public ElemGlass(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
    }

    @Override
    public void initArtikle() {

        if (isJson(gson.param, PKjson.artglasID)) {
            artiklRec = eArtikl.find(gson.param.get(PKjson.artglasID).getAsInt(), false);
        } else {
            Record sysreeRec = eSystree.find(winc.nuni); //по умолчанию стеклопакет
            artiklRec = eArtikl.find2(sysreeRec.getStr(eSystree.glas));
        }
        artiklRecAn = artiklRec;

        //Цвет стекла
        if (isJson(gson.param, PKjson.colorGlass)) {
            colorID1 = gson.param.get(PKjson.colorGlass).getAsInt();
        } else {
            Record artdetRec = eArtdet.find(artiklRec.getInt(eArtikl.id));
            Record colorRec = eColor.find3(artdetRec.getInt(eArtdet.color_fk));
            colorID1 = colorRec.getInt(eColor.id);
            colorID2 = colorRec.getInt(eColor.id);
            colorID3 = colorRec.getInt(eColor.id);
        }

        //Раскладка
        if (isJson(gson.param, PKjson.artiklRascl)) {
            rasclRec = eArtikl.find(gson.param.get(PKjson.artiklRascl).getAsInt(), false);
            //Текстура
            if (isJson(gson.param, PKjson.colorRascl)) {
                rasclColor = eColor.get(gson.param.get(PKjson.colorRascl).getAsInt()).getInt(eColor.id);
            } else {
                rasclColor = eArtdet.find(rasclRec.getInt(eArtikl.id)).getInt(eArtdet.color_fk); //цвет по умолчанию
            }
            //Проёмы гориз.
            if (isJson(gson.param, PKjson.rasclHor)) {
                rasclNumber[0] = gson.param.get(PKjson.rasclHor).getAsInt();
            }
            //Проёмы вертик.
            if (isJson(gson.param, PKjson.rasclVert)) {
                rasclNumber[1] = gson.param.get(PKjson.rasclVert).getAsInt();
            }
        }
    }

    //Внутренний полигон створки/рамы для прорисовки
    @Override
    public void setLocation() {
        ArrayCom<ElemSimple> list = winc.listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.IMPOST);
        this.area = UGeo.bufferCross(owner.area.getGeometryN(0), list, 0); //полигон для прорисовки
    }

    //Главная спецификация    
    @Override
    public void setSpecific() {
        try {
            spcRec.place = "ЗАП";
            spcRec.setArtikl(artiklRec);
            spcRec.setColor(colorID1, colorID2, colorID3);

            //Фича определения gzazo и gaxis на раннем этапе построения. 
            new SpcFilling(winc, true).calc(this);

            ArrayCom<ElemSimple> list = winc.listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.IMPOST, Type.SHTULP, Type.STOIKA);
            Map<Double, Double> hm = new HashMap();
            for (Com5t el : list) {
                Record rec = (el.artiklRec == null) ? eArtikl.virtualRec() : el.artiklRec;
                hm.put(el.id, rec.getDbl(eArtikl.height) - rec.getDbl(eArtikl.size_centr) - rec.getDbl(eArtikl.size_falz) + gzazo);
            }
            this.area = GeoBuffer.buffer(owner.area.getGeometryN(0), hm);

            Envelope env = this.area.getGeometryN(0).getEnvelopeInternal();
            spcRec.width = env.getWidth();
            spcRec.height = env.getHeight();

        } catch (Exception e) {
            System.err.println("Ошибка:ElemGlass.setSpecific() " + e);
        }
    }

    //Вложенная спецификация
    @Override
    public void addSpecific(SpcRecord spcAdd) {
        try {
            spcAdd.count = UPar.to_11030_12060_14030_15040_25060_33030_34060_38030_39060(spcAdd); //кол. ед. с учётом парам. 
            spcAdd.count += UPar.to_14050_24050_33050_38050(spcRec, spcAdd); //кол. ед. с шагом
            spcAdd.width += UPar.to_12050_15050_34051_39020(spcAdd); //поправка мм         
            if (TypeArtikl.X502.isType(spcAdd.artiklRec)) {
                return;  //если стеклопакет сразу выход
            }

            //Погонные метры.
            if (UseUnit.METR.id == spcAdd.artiklRec.getInt(eArtikl.unit)) {

                Coordinate coo[] = this.area.getCoordinates();
                //System.out.println(this.frameGlass.id);

                if (this.area.getCoordinates().length < MAXSIDE) { //не арка
                    LineSegment s1 = UGeo.getSegment(this.area, sideClass - 1);
                    LineSegment s2 = UGeo.getSegment(this.area, sideClass);
                    spcAdd.anglHoriz = UGeo.anglHor(s2.p0.x, s2.p0.y, s2.p1.x, s2.p1.y); //угол к горизонту                    
                    spcAdd.anglCut0 = Math.toDegrees(Angle.angleBetween(coo[coo.length - 2], coo[0], coo[1]));
                    spcAdd.anglCut1 = Math.toDegrees(Angle.angleBetween(coo[coo.length - 5], coo[coo.length - 4], coo[coo.length - 3]));
                    spcAdd.width += s1.getLength() + 2 * gzazo;

                } else { //арка
                    Set hs = (Set) this.area.getUserData();
                    if (this.frameGlass.h() == null) {
                        if (hs.size() == 2) {
                            LineSegment s = new LineSegment(coo[0], coo[1]);
                            spcAdd.width += s.getLength() + 2 * gzazo;
                        } else {
                            LineSegment s = new LineSegment(coo[sideClass], coo[sideClass + 1]);
                            spcAdd.width += s.getLength() + 2 * gzazo;
                        }
                    } else {
                        spcAdd.anglCut0 = Math.toDegrees(Angle.angleBetween(coo[coo.length - 2], coo[0], coo[1]));
                        LineSegment seg = new LineSegment();
                        for (int i = 1; i < coo.length; i++) {
                            seg.setCoordinates(coo[i - 1], coo[i]);
                            if (seg.getLength() > this.frameGlass.artiklRecAn.getDbl(eArtikl.height)) {
                                if (hs.size() == 2) {
                                    spcAdd.anglCut1 = Math.toDegrees(Angle.angleBetween(coo[0], coo[1], coo[2]));
                                    //spcAdd.artiklRec.getDbl(eArtikl.height)
                                } else {
                                    spcAdd.anglCut1 = Math.toDegrees(Angle.angleBetween(coo[i - 2], coo[i - 1], coo[i]));
                                }
                            }
                            if (coo[i - 1].z == this.frameGlass.id) {
                                spcAdd.width += seg.getLength();
                            }
                        }
                    }
                }

                //spcAdd.width += segm1.getLength() + 2 * gzazo;
                spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);
                //spcAdd.anglCut0 = Math.toDegrees(Angle.angleBetween(segm1.p1, segm1.p0, segm2.p1)) / 2;
                //spcAdd.anglCut1 = Math.toDegrees(Angle.angleBetween(segm2.p0, segm2.p1, segm3.p1)) / 2;
                //spcAdd.anglHoriz = anglHor; //угол к гор. сторон стекла;

                spcRec.spcList.add(spcAdd);

                //По горизонтали
//                if ((anglHor > 315 && anglHor < 360 || anglHor >= 0 && anglHor < 45) || (anglHor > 135 && anglHor < 225)) {
//                    if (spcAdd.mapParam.get(15010) != null) {
//                        if ("Нет".equals(spcAdd.mapParam.get(15010)) == false) { //Усекать нижний штапик
//                            spcAdd.width = spcAdd.width - 2 * spcAdd.height;
//                        }
//                    }
//                    if (spcAdd.mapParam.get(15011) != null) {
//                        if ("усекать боковой".equals(spcAdd.mapParam.get(15011))) { //Расчет реза штапика
//                            spcAdd.width = spcAdd.width - 2 * spcAdd.height;
//                        }
//                    }
//
//                    //По вертикали
//                } else if ((anglHor > 225 && anglHor < 315) || (anglHor > 45 && anglHor < 135)) {
//                    if (spcAdd.mapParam.get(15010) != null) {
//                        if ("Да".equals(spcAdd.mapParam.get(15010)) == false) { //Усекать нижний штапик
//                            spcAdd.width = spcAdd.width - 2 * spcAdd.height;
//                        }
//                    }
//                    if (spcAdd.mapParam.get(15011) != null) {
//                        if ("усекать нижний".equals(spcAdd.mapParam.get(15011))) { //Расчет реза штапика
//                            spcAdd.width = spcAdd.width - 2 * spcAdd.height;
//                        }
//                    }
//                }
//                if ("по биссектрисе".equals(spcAdd.mapParam.get(15011))) { //Расчет реза штапика
//                    //
//                }
                spcAdd.width = UPar.to_12065_15045_25040_34070_39070(spcAdd); //длина мм
                spcAdd.width = spcAdd.width * UPar.to_12030_15030_25035_34030_39030(spcAdd); //"[ * коэф-т ]"
                spcAdd.width = spcAdd.width / UPar.to_12040_15031_25036_34040_39040(spcAdd); //"[ / коэф-т ]" 

                //Штуки   
            } else if (UseUnit.PIE.id == spcAdd.artiklRec.getInt(eArtikl.unit)) {

                if (spcAdd.mapParam.get(13014) != null) {
                    LineSegment s2 = UGeo.getSegment(this.area, sideClass);
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
        } catch (Exception e) {
            System.err.println("Ошибка:ElemGlass.addSpecific()  " + e);
        }
    }

    //Линии размерности
    @Override
    public void paint() {
        if (this.area != null) {
            winc.gc2d.setColor(new java.awt.Color(eColor.find(colorID1).getInt(eColor.rgb)));
            Shape shape = new ShapeWriter().toShape(this.area);
            winc.gc2d.fill(shape);
        }
    }
}
