package builder.model;

import builder.Wincalc;
import builder.making.SpcFilling;
import builder.making.SpcRecord;
import builder.script.GsonElem;
import common.ArrayCom;
import common.UCom;
import dataset.Record;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eGlasgrp;
import domain.eSystree;
import enums.PKjson;
import enums.Type;
import enums.TypeArtikl;
import enums.UseUnit;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;
import startup.Test;

public class ElemGlass extends ElemSimple {

    public Geometry areaFalz = null;
    public Geometry areaSht = null;
    public double radius = 0; //радиус стекла
    public double gzazo = 0; //зазор между фальцем и стеклопакетом 
    public HashMap<Integer, Double> axisMap = new HashMap<Integer, Double>(); //размер от оси до стеклопакета
    public ElemSimple elemglass = null;
    public int sideglass = 0;

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
        Map<Double, Double> hm = new HashMap();
        for (Com5t el : list) {
            Record rec = (el.artiklRec == null) ? eArtikl.virtualRec() : el.artiklRec;
            hm.put(el.id, (rec.getDbl(eArtikl.height) - rec.getDbl(eArtikl.size_centr)) - 25.5);//rec.getDbl(eArtikl.size_falz));
        }
        this.areaFalz = UGeo.bufferUnion(owner.area.getGeometryN(0), list, hm);  //полигон по фальцу для прорисовки и рассчёта штапик...
        this.areaSht = this.areaFalz; 
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
            this.area = UGeo.bufferUnion(owner.area.getGeometryN(0), list, hm); //полигон стеклопакета

            Envelope env = this.area.getEnvelopeInternal();
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
                
//                if (spcAdd.artiklRec.getInt(eArtikl.level2) == 8) {
//                    double heightSht = spcAdd.artiklRec.getDbl(eArtikl.height);
//                    ArrayCom<ElemSimple> list = winc.listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.IMPOST);
//                    Map<Double, Double> hm = new HashMap();
//                    for (Com5t el : list) {
//                        Record rec = (el.artiklRec == null) ? eArtikl.virtualRec() : el.artiklRec;
//                        hm.put(el.id, (rec.getDbl(eArtikl.height) - rec.getDbl(eArtikl.size_centr)) - heightSht);
//                    }
//                    this.areaSht = UGeo.bufferUnion(owner.area.getGeometryN(0), list, hm);  //полигон для рассчёта штапик...    
//                }
                Coordinate coo[] = this.areaSht.getCoordinates();
                spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);
                spcAdd.anglHoriz = UGeo.anglHor(elemglass); //угол к горизонту 

                //Арка
                if (elemglass.h() != null) {
                    int index2 = IntStream.range(0, coo.length).filter(j -> coo[j].z == elemglass.id).findFirst().getAsInt();
                    spcAdd.anglCut0 = UGeo.anglCut(spcAdd, this.areaSht, coo.length - 2, 0, '-');
                    spcAdd.anglCut1 = UGeo.anglCut(spcAdd, this.areaSht, index2 - 2, index2 - 1, '+');
                    for (int j = 1; j < coo.length; j++) {
                        if (coo[j - 1].z == elemglass.id) {
                            spcAdd.width += coo[j - 1].distance(coo[j]);
                        }
                    }
                    System.out.println(coo[0].distance(coo[1]));
                    //new Test().mpol = this.areaSht;

                    //Остальное
                } else {
                    Coordinate[] c1 = {coo[UGeo.getIndex(coo, sideglass - 1)], coo[sideglass], coo[UGeo.getIndex(coo, sideglass + 1)]};
                    Coordinate[] c2 = {coo[sideglass], coo[UGeo.getIndex(coo, sideglass + 1)], coo[UGeo.getIndex(coo, sideglass + 2)]};
                    double angBetween0 = Math.toDegrees(Angle.angleBetween(c1[0], c1[1], c1[2]));
                    double angBetween1 = Math.toDegrees(Angle.angleBetween(c2[0], c2[1], c2[2]));
                    spcAdd.anglCut0 = angBetween0 - UGeo.anglCut(spcAdd, this.areaSht, UGeo.getIndex(coo, sideglass - 1), sideglass, '-');
                    spcAdd.anglCut1 = angBetween1 - UGeo.anglCut(spcAdd, this.areaSht, UGeo.getIndex(coo, sideglass), UGeo.getIndex(coo, sideglass + 1), '+');
                    spcAdd.width += coo[sideglass].distance(coo[sideglass + 1]) + 2 * gzazo; //Тут надо учитывать наклон штапика
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
                    LineSegment s2 = UGeo.getSegment(this.areaSht, sideglass);
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

    public void addSpecific2(SpcRecord spcAdd) {
        try {
            spcAdd.count = UPar.to_11030_12060_14030_15040_25060_33030_34060_38030_39060(spcAdd); //кол. ед. с учётом парам. 
            spcAdd.count += UPar.to_14050_24050_33050_38050(spcRec, spcAdd); //кол. ед. с шагом
            spcAdd.width += UPar.to_12050_15050_34051_39020(spcAdd); //поправка мм         
            if (TypeArtikl.X502.isType(spcAdd.artiklRec)) {
                return;  //если стеклопакет сразу выход
            }

            //Погонные метры.
            if (UseUnit.METR.id == spcAdd.artiklRec.getInt(eArtikl.unit)) {

                //Создание геометрии штапиков
                if (this.area.getNumGeometries() == 1) {
                    ArrayCom<ElemSimple> list = winc.listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.IMPOST);
                    double gap = spcAdd.variantRec.getDbl(eGlasgrp.gap);
                    Polygon geo = UGeo.bufferUnion(owner.area.getGeometryN(0), list, gap); //полигон для прорисовки
                    this.area = gf.createMultiPolygon(new Polygon[]{(Polygon) this.area, geo});
                    //new Test().mpol = this.area;
                }

                //double arcID = winc.listElem.stream().filter(e -> e.h() != null).findFirst().get().id;
                Coordinate coo[] = owner.area.getGeometryN(0).getCoordinates();
                Coordinate co2[] = owner.area.getGeometryN(1).getCoordinates();
                double angHor1 = UGeo.anglHor(winc.listElem.get(coo[sideglass].z));

                Set hs = (Set) this.area.getUserData();
                spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);

                //Арка
                if (this.area.getCoordinates().length > MAXSIDE) {
                    double arcID = winc.listElem.stream().filter(e -> e.h() != null).findFirst().get().id;
                    double angBetween0 = 0;//Math.toDegrees(Angle.angleBetween(coo[coo.length - 2], coo[index1], coo[1]));
                    double angBetween1 = 0;//Math.toDegrees(Angle.angleBetween(coo[index2 - 2], coo[index2 - 1], coo[index2]));

                    //Основание арки
                    if (this.elemglass.h() == null) {
//                        LineSegment s1 = new LineSegment(coo[coo.length - 2], coo[0]);
//                        LineSegment s2 = new LineSegment(coo[0], coo[1]);
//                        LineSegment s3 = new LineSegment(coo[1], coo[2]);
//                        Coordinate c1 = UGeo.offset(s1, s2, spcAdd.height);
//                        Coordinate c2 = UGeo.offset(s2, s3, spcAdd.height);
//                        double length1 = new LineSegment(s2.p0, c1).getLength();
//                        double length2 = new LineSegment(s2.p1, c2).getLength();
////                        spcAdd.anglCut0 = angBetween0 - Math.toDegrees(Math.asin(spcAdd.height / length1));
////                        spcAdd.anglCut1 = angBetween1 - Math.toDegrees(Math.asin(spcAdd.height / length2));
//                        LineSegment s = new LineSegment(coo[0], coo[1]);
//                        spcAdd.width += s.getLength() + 2 * gzazo;
//
                        //Дуга арки
                    } else {
                        spcAdd.anglCut0 = angBetween0;
                        spcAdd.anglCut1 = angBetween1;
                        LineSegment s = new LineSegment();
                        for (int i = 1; i < coo.length; i++) {
                            if (coo[i - 1].z == this.elemglass.id) {
                                s.setCoordinates(coo[i - 1], coo[i]);
                                spcAdd.width += s.getLength();
                            }
                        }
                    }

                    //Остальное
                } else {
                    LineSegment s1 = UGeo.getSegment(this.area, sideglass - 1);
                    LineSegment s2 = UGeo.getSegment(this.area, sideglass);
                    LineSegment s3 = UGeo.getSegment(this.area, sideglass + 1);
                    double angBetween0 = Math.toDegrees(Angle.angleBetween(s1.p0, s1.p1, s2.p0));
                    double angBetween1 = Math.toDegrees(Angle.angleBetween(s2.p0, s2.p1, s3.p1));
                    spcAdd.anglCut0 = angBetween0 / 2;
                    spcAdd.anglCut1 = angBetween1 / 2;
                    spcAdd.anglHoriz = UGeo.anglHor(s2.p0.x, s2.p0.y, s2.p1.x, s2.p1.y); //угол к горизонту 
                    spcAdd.width += s1.getLength() + 2 * gzazo;
                }

                spcRec.spcList.add(spcAdd);

                //По горизонтали
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

                    //По вертикали
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
                    LineSegment s2 = UGeo.getSegment(this.area, sideglass);
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

    public void addSpecific3(SpcRecord spcAdd) {
        try {
            spcAdd.count = UPar.to_11030_12060_14030_15040_25060_33030_34060_38030_39060(spcAdd); //кол. ед. с учётом парам. 
            spcAdd.count += UPar.to_14050_24050_33050_38050(spcRec, spcAdd); //кол. ед. с шагом
            spcAdd.width += UPar.to_12050_15050_34051_39020(spcAdd); //поправка мм         
            if (TypeArtikl.X502.isType(spcAdd.artiklRec)) {
                return;  //если стеклопакет сразу выход
            }

            //Погонные метры.
            if (UseUnit.METR.id == spcAdd.artiklRec.getInt(eArtikl.unit)) {

                Geometry areaHt = this.area.getGeometryN(1);
                Coordinate coo[] = areaHt.getCoordinates();
                spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);
                spcAdd.anglHoriz = UGeo.anglHor(elemglass); //угол к горизонту 

                //Арка
                if (elemglass.h() != null) {
                    LineSegment s1a = UGeo.getSegment(areaHt, coo.length - 2);
                    LineSegment s2a = UGeo.getSegment(areaHt, 0);
                    LineSegment s1b = s1a.offset(-spcAdd.height);
                    LineSegment s2b = s2a.offset(-spcAdd.height);
                    double anglBetw = Math.toDegrees(Angle.angleBetween(s1a.p0, s1a.p1, s2a.p1));
                    Coordinate cross = s1b.lineIntersection(s2b);
                    double hpt = s2a.p0.distance(cross);
                    spcAdd.anglCut0 = anglBetw - Math.toDegrees(Math.asin(spcAdd.height / hpt));

                    for (int i = coo.length - 2; i > 0; i--) {
                        if (coo[i].z != elemglass.id) {
                            s1a = UGeo.getSegment(areaHt, i - 1);
                            s2a = UGeo.getSegment(areaHt, i);
                            s1b = s1a.offset(-spcAdd.height);
                            s2b = s2a.offset(-spcAdd.height);
                            anglBetw = Math.toDegrees(Angle.angleBetween(s1a.p0, s1a.p1, s2a.p1));
                            cross = s1b.lineIntersection(s2b);
                            hpt = s2a.p0.distance(cross);
                            spcAdd.anglCut1 = anglBetw - Math.toDegrees(Math.asin(spcAdd.height / hpt));
                            break;
                        }
                    }
                    for (int i = 1; i < coo.length; i++) {
                        if (coo[i - 1].z == elemglass.id) {
                            spcAdd.width += coo[i - 1].distance(coo[i]);
                        }
                    }

                    //Остальное
                } else {
                    int index1 = (sideglass == 0) ? coo.length - 2 : sideglass - 1;
                    int index3 = (sideglass == coo.length - 1) ? 0 : sideglass + 1;

                    LineSegment s1a = UGeo.getSegment(areaHt, index1);
                    LineSegment s2a = UGeo.getSegment(areaHt, sideglass);
                    LineSegment s3a = UGeo.getSegment(areaHt, index3);

                    LineSegment s1b = s1a.offset(-spcAdd.height);
                    LineSegment s2b = s2a.offset(-spcAdd.height);
                    LineSegment s3b = s3a.offset(-spcAdd.height);

                    Coordinate crosL = s1b.lineIntersection(s2b);
                    double hpt1 = s2a.p0.distance(crosL);
                    spcAdd.anglCut0 = Math.toDegrees(Math.asin(spcAdd.height / hpt1));

                    Coordinate crosR = s2b.lineIntersection(s3b);
                    double hpt2 = s2a.p1.distance(crosR);
                    spcAdd.anglCut1 = Math.toDegrees(Math.asin(spcAdd.height / hpt2));

                    spcAdd.width += s2a.getLength() + 2 * gzazo;
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
                    LineSegment s2 = UGeo.getSegment(this.area, sideglass);
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
        if (areaFalz != null) {
            winc.gc2d.setColor(new java.awt.Color(eColor.find(colorID1).getInt(eColor.rgb)));
            Shape shape = new ShapeWriter().toShape(areaFalz);
            winc.gc2d.fill(shape);
        }
    }
}
