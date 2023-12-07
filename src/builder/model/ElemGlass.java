package builder.model;

import builder.Wincalc;
import builder.making.Filling;
import builder.making.Specific;
import builder.script.GsonElem;
import common.UCom;
import dataset.Record;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eSyssize;
import domain.eSystree;
import enums.PKjson;
import enums.Type;
import enums.TypeArtikl;
import enums.UseUnit;
import java.awt.Shape;
import java.util.List;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LineSegment;

public class ElemGlass extends ElemSimple {

    public double radiusGlass = 0; //радиус стекла
    public double gzazo = 0; //зазор между фальцем и стеклопакетом 
    public double gsize[] = {0, 0, 0, 0}; //размер от оси до стеклопакета

    public Record rasclRec = eArtikl.virtualRec(); //раскладка
    public int rasclColor = -3; //цвет раскладки
    public int rasclNumber[] = {2, 2}; //количество проёмов раскладки     

    public ElemGlass(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
    }

    @Override
    public void initConstructiv() {

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
    public void setLocation() {
        this.geom = UGeo.geoPadding(owner.geom, winc.listElem, -20);
    }

    //Главная спецификация    
    @Override
    public void setSpecific() {
        try {
            spcRec.place = "ЗАП";
            spcRec.setArtikl(artiklRec);
            spcRec.colorID1 = colorID1;
            spcRec.colorID2 = colorID2;
            spcRec.colorID3 = colorID3;

            //Фича определения gzazo и gsize на раннем этапе построения. 
            Filling filling = new Filling(winc, true);
            filling.calc(this);

            //Внешний полигон створки/рамы для прорисовки 
            Coordinate[] coo = owner.geom.getCoordinates();
            
            Coordinate[] out = new Coordinate[coo.length];
            List<ElemSimple> listFrame = winc.listElem.filter(Type.FRAME_SIDE, Type.IMPOST, Type.SHTULP, Type.STOIKA);
            for (int i = 0; i < coo.length; i++) {

                //Сегменты полигона
                int j = (i == coo.length - 1) ? 1 : i + 1;
                int k = (i == 0 || i == coo.length - 1) ? coo.length - 2 : i - 1;
                LineSegment segm1 = new LineSegment(coo[k], coo[i]);
                LineSegment segm2 = new LineSegment(coo[i], coo[j]);

                //Ширина сегментов
                ElemSimple e1 = UGeo.segMapElem(listFrame, segm1);
                ElemSimple e2 = UGeo.segMapElem(listFrame, segm2);
                Record syssizeRec1 = eSyssize.get(e1.artiklRec);
                Record syssizeRec2 = eSyssize.get(e2.artiklRec);
                double w1 = (syssizeRec1 == null) ? e1.artiklRec.getDbl(eArtikl.size_centr) + gsize[0]
                        : (e1.artiklRec.getDbl(eArtikl.height) - e1.artiklRec.getDbl(eArtikl.size_centr)) - syssizeRec1.getDbl(eSyssize.falz) + gzazo;
                double w2 = (syssizeRec2 == null) ? e2.artiklRec.getDbl(eArtikl.size_centr) + gsize[0]
                        : (e2.artiklRec.getDbl(eArtikl.height) - e2.artiklRec.getDbl(eArtikl.size_centr)) - syssizeRec2.getDbl(eSyssize.falz) + gzazo;

                //Смещение сегментов
                LineSegment segm3 = segm1.offset(-w1);
                LineSegment segm4 = segm2.offset(-w2);  
                
                //Точка пересечения внутренних сегментов
                out[i] = segm4.lineIntersection(segm3);                
            }
            this.geom = Com5t.gf.createPolygon(out);
            Envelope env = this.geom.getEnvelopeInternal();
            spcRec.width = env.getWidth();
            spcRec.height = env.getHeight();

        } catch (Exception e) {
            System.err.println("Ошибка:ElemGlass.setSpecific() " + e);
        }
    }

    //Вложенная спецификация
    @Override
    public void addSpecific(Specific spcAdd) {
        try {
            if (Type.ARCH == owner.type && (anglHoriz == 90 || anglHoriz == 270)) {
                return;  //нет таких сторон у арки
            }
            spcAdd.count = UPar.to_11030_12060_14030_15040_25060_33030_34060_38030_39060(spcAdd); //кол. ед. с учётом парам. 
            spcAdd.count += UPar.to_14050_24050_33050_38050(spcRec, spcAdd); //кол. ед. с шагом
            spcAdd.width += UPar.to_12050_15050_34051_39020(spcAdd); //поправка мм         
            if (TypeArtikl.X502.isType(spcAdd.artiklRec)) {
                return;  //если стеклопакет сразу выход
            }

            if (UseUnit.METR.id == spcAdd.artiklRec.getInt(eArtikl.unit)) {

                if (Type.ARCH == owner.type) {
                    // <editor-fold defaultstate="collapsed" desc="ARCH"> 
//                    Double dw = spcAdd.width;
//                    ElemSimple imp = owner.joinSide(Layout.BOTT);
//                    ElemSimple arch = root.frames.get(Layout.TOP);
//                    double radiusArch = ((AreaArch) winc.rootArea).radiusArch;
//
//                    if (anglHoriz() == 0) { //по основанию арки
//                        double r1 = radiusArch - arch.artiklRec.getDbl(eArtikl.height) + arch.artiklRec.getDbl(eArtikl.size_falz); //внешний радиус штапика
//                        double h1 = imp.y1() - imp.artiklRec.getDbl(eArtikl.height) + imp.artiklRec.getDbl(eArtikl.size_centr)
//                                + imp.artiklRec.getDbl(eArtikl.size_falz) - arch.artiklRec.getDbl(eArtikl.height) + arch.artiklRec.getDbl(eArtikl.size_falz);
//                        double w1 = Math.sqrt((2 * r1 * h1) - (h1 * h1)); //длина нижней стороны штапика
//                        double r2 = r1 - spcAdd.artiklRec.getDbl(eArtikl.height); //внутренний радиус
//                        double h2 = h1 - 2 * spcAdd.artiklRec.getDbl(eArtikl.height);
//                        double w2 = Math.sqrt((2 * r2 * h2) - (h2 * h2)); //длина верхней стороны штапика
//                        double ang1 = Math.toDegrees(Math.atan(spcAdd.artiklRec.getDbl(eArtikl.height) / (w1 - w2))); //угол реза
//                        spcAdd.width = (2 * w1 + dw);
//                        spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);
//                        spcAdd.anglCut0 = ang1;
//                        spcAdd.anglCut1 = ang1;
//                        spcRec.spcList.add(spcAdd); //добавим спецификацию
//
//                    } else if (anglHoriz() == 180) { //по дуге арки   
//                        if (spcAdd.artiklRec.getInt(eArtikl.level1) == 1 && spcAdd.artiklRec.getInt(eArtikl.level2) == 8) { //Штапик
//
//                            double r1 = radiusArch - arch.artiklRec.getDbl(eArtikl.height) + arch.artiklRec.getDbl(eArtikl.size_falz); //внешний радиус штапика
//                            double h1 = imp.y1() - imp.artiklRec.getDbl(eArtikl.height) + imp.artiklRec.getDbl(eArtikl.size_centr)
//                                    + imp.artiklRec.getDbl(eArtikl.size_falz) - arch.artiklRec.getDbl(eArtikl.height);
//                            double w1 = Math.sqrt((2 * r1 * h1) - (h1 * h1)); //длина нижней стороны основания арки штапика
//                            double r2 = r1 - spcAdd.artiklRec.getDbl(eArtikl.height); //внутренний радиус
//                            double h2 = h1 - 2 * spcAdd.artiklRec.getDbl(eArtikl.height);
//                            double w2 = Math.sqrt((2 * r2 * h2) - (h2 * h2)); //длина верхней стороны основания арки штапика   
//                            double ang1 = Math.toDegrees(Math.atan(spcAdd.artiklRec.getDbl(eArtikl.height) / (w1 - w2))); //угол реза
//                            double ang2 = Math.toDegrees(Math.asin(w1 / r1)); //угол дуги арки
//                            //double ang2 = Math.toDegrees(Math.asin((owner.width() / 2) / radiusArch)); //угол дуги арки
//                            double w4 = ((2 * Math.PI * r1) / 360) * ang2 * 2; //длина верхней стороны арки штапика
//                            double ang3 = 90 - (90 - ang2 + ang1);
//                            spcAdd.width = (dw + w4);
//                            spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);
//                            spcAdd.anglCut0 = ang3;
//                            spcAdd.anglCut1 = ang3;
//                            spcRec.spcList.add(spcAdd); //добавим спецификацию
//
//                        } else {
//                            //В PS4 длина уплотнения равна длине штапика, в SA нет.
//                            double r1 = radiusArch - arch.artiklRec.getDbl(eArtikl.height) + spcAdd.artiklRec.getDbl(eArtikl.height); //внешний радиус уплотнения
//                            double h1 = imp.y1() - imp.artiklRec.getDbl(eArtikl.size_centr) - arch.artiklRec.getDbl(eArtikl.height);
//                            double w1 = Math.sqrt((2 * r1 * h1) - (h1 * h1)); //длина нижней стороны основания арки уплотнения
//                            double r2 = r1 - spcAdd.artiklRec.getDbl(eArtikl.height); //внутренний радиус
//                            double h2 = h1 - 2 * spcAdd.artiklRec.getDbl(eArtikl.height);
//                            double w2 = Math.sqrt((2 * r2 * h2) - (h2 * h2)); //длина верхней стороны основания арки уплотнения   
//                            double ang1 = Math.toDegrees(Math.atan(spcAdd.artiklRec.getDbl(eArtikl.height) / (w1 - w2))); //угол реза
//                            double ang2 = Math.toDegrees(Math.asin(w1 / r1)); //угол дуги арки
//                            double w4 = ((2 * Math.PI * r1) / 360) * ang2 * 2; //длина верхней стороны арки уплотнения
//                            double ang3 = 90 - (90 - ang2 + ang1);
//                            spcAdd.width = (dw + w4);
//                            spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);
//                            spcAdd.anglCut0 = ang3;
//                            spcAdd.anglCut1 = ang3;
//                            spcRec.spcList.add(spcAdd); //добавим спецификацию
//                        }
//                    }
                    // </editor-fold> 
                } else if (Type.TRAPEZE == owner.type) {
                    // <editor-fold defaultstate="collapsed" desc="TRAPEZE"> 
//                    ElemSimple inTop = owner.joinSide(Layout.TOP), inBott = owner.joinSide(Layout.BOTT), inRigh = owner.joinSide(Layout.RIGHT), inLeft = owner.joinSide(Layout.LEFT);
//                    if (winc.form == Form.RIGHT) {
//
//                        if (anglHoriz() == 0) {
//                            spcAdd.width += width() + 2 * gzazo;
//                            spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);
//                            spcAdd.anglCut1 = 45;
//                            spcAdd.anglCut2 = 45;
//                            spcAdd.anglHoriz = inBott.anglHoriz();
//
//                        } else if (anglHoriz() == 90) {
//                            ElemSimple el = winc.listJoin.elem(inRigh, 1);
//                            double dy1 = inBott.y2() - inRigh.y2() - (inBott.artiklRec.getDbl(eArtikl.height)
//                                    - inBott.artiklRec.getDbl(eArtikl.size_centr) - inBott.artiklRec.getDbl(eArtikl.size_falz));
//                            double dy2 = (inTop.artiklRec.getDbl(eArtikl.height) - inTop.artiklRec.getDbl(eArtikl.size_falz)) / UCom.sin(inTop.anglHoriz() - 90);
//                            double dy3 = (inRigh.artiklRec.getDbl(eArtikl.height) - inRigh.artiklRec.getDbl(eArtikl.size_falz)) / UCom.tan(inTop.anglHoriz() - 90);
//                            spcAdd.width += dy1 - dy2 + dy3;
//                            spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);
//                            spcAdd.anglCut1 = 45;
//                            spcAdd.anglCut1 = inRigh.anglCut[1];
//                            spcAdd.anglHoriz = inRigh.anglHoriz();
//
//                        } else if (anglHoriz() == 180) {
//                            ElemJoining ej = winc.listJoin.get(inTop, 1);
//                            double dx1 = inLeft.x2() + inLeft.artiklRec.getDbl(eArtikl.height) - inLeft.artiklRec.getDbl(eArtikl.size_falz);
//                            double dx2 = inRigh.x2() - inRigh.artiklRec.getDbl(eArtikl.height) + inRigh.artiklRec.getDbl(eArtikl.size_falz);
//                            spcAdd.width += (dx2 - dx1) / UCom.sin(ej.angl);
//                            spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);
//                            spcAdd.anglCut1 = root.frames.get(Layout.TOP).anglCut[0];
//                            spcAdd.anglCut2 = root.frames.get(Layout.TOP).anglCut[1];
//                            spcAdd.anglHoriz = inTop.anglHoriz();
//
//                        } else if (anglHoriz() == 270) {
//                            ElemJoining ej = winc.listJoin.get(inTop, 1);
//                            double dy1 = (inTop.artiklRec.getDbl(eArtikl.height) - inTop.artiklRec.getDbl(eArtikl.size_falz)) / UCom.cos(90 - ej.angl);
//                            double dy2 = (inLeft.artiklRec.getDbl(eArtikl.height) - inLeft.artiklRec.getDbl(eArtikl.size_falz)) * UCom.tan(90 - ej.angl);
//                            double Y1 = inLeft.y1() + dy1 + dy2;
//                            double Y2 = inBott.y1() - inBott.artiklRec.getDbl(eArtikl.height) + inBott.artiklRec.getDbl(eArtikl.size_centr) + inBott.artiklRec.getDbl(eArtikl.size_falz);
//                            spcAdd.width += Y2 - Y1;
//                            spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);
//                            spcAdd.anglCut0 = inLeft.anglCut[0];
//                            spcAdd.anglCut1 = 45;
//                            spcAdd.anglHoriz = inLeft.anglHoriz();
//                        }
//                    } else if (winc.form == Form.LEFT) {
//                        if (anglHoriz() == 0) {
//                            spcAdd.width += width() + 2 * gzazo;
//                            spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);
//                            spcAdd.anglCut0 = 45;
//                            spcAdd.anglCut0 = 45;
//                            spcAdd.anglHoriz = inBott.anglHoriz();
//
//                        } else if (anglHoriz() == 90) {
//                            ElemJoining ej = winc.listJoin.get(inTop, 0);
//                            double dy1 = (inTop.artiklRec.getDbl(eArtikl.height) - inTop.artiklRec.getDbl(eArtikl.size_falz)) / UCom.cos(90 - ej.angl);
//                            double dy2 = (inRigh.artiklRec.getDbl(eArtikl.height) - inRigh.artiklRec.getDbl(eArtikl.size_falz)) * UCom.tan(90 - ej.angl);
//                            double Y1 = inRigh.y2() + dy1 + dy2;
//                            double Y2 = inBott.y2() - inBott.artiklRec.getDbl(eArtikl.height) + inBott.artiklRec.getDbl(eArtikl.size_centr) + inBott.artiklRec.getDbl(eArtikl.size_falz);
//                            spcAdd.width += Y2 - Y1;
//                            spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);
//                            spcAdd.anglCut0 = inRigh.anglCut[1];
//                            spcAdd.anglCut1 = 45;
//                            spcAdd.anglHoriz = inRigh.anglHoriz();                            
//
//                        } else if (anglHoriz() == 180) {           
//                            ElemJoining ej = winc.listJoin.get(inTop, 1);
//                            double dx1 = inLeft.x2() + inLeft.artiklRec.getDbl(eArtikl.height) - inLeft.artiklRec.getDbl(eArtikl.size_falz);
//                            double dx2 = inRigh.x2() - inRigh.artiklRec.getDbl(eArtikl.height) + inRigh.artiklRec.getDbl(eArtikl.size_falz);
//                            spcAdd.width += (dx2 - dx1) / UCom.sin(ej.angl);
//                            spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);
//                            spcAdd.anglCut0 = root.frames.get(Layout.TOP).anglCut[0];
//                            spcAdd.anglCut1 = root.frames.get(Layout.TOP).anglCut[1];
//                            spcAdd.anglHoriz = inTop.anglHoriz();                            
//
//                        } else if (anglHoriz() == 270) {
//                            ElemSimple el = winc.listJoin.elem(inLeft, 0);
//                            double dy1 = inBott.y1() - inLeft.y1() - (inBott.artiklRec.getDbl(eArtikl.height)
//                                    - inBott.artiklRec.getDbl(eArtikl.size_centr) - inBott.artiklRec.getDbl(eArtikl.size_falz));
//                            double dy2 = (inTop.artiklRec.getDbl(eArtikl.height) - inTop.artiklRec.getDbl(eArtikl.size_falz)) / UCom.sin(270 - inTop.anglHoriz());
//                            double dy3 = (inLeft.artiklRec.getDbl(eArtikl.height) - inLeft.artiklRec.getDbl(eArtikl.size_falz)) / UCom.tan(270 - inTop.anglHoriz());
//                            spcAdd.width += dy1 - dy2 + dy3;
//                            spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);
//                            spcAdd.anglCut0 = 45;
//                            spcAdd.anglCut1 = inLeft.anglCut[1];
//                            spcAdd.anglHoriz = inLeft.anglHoriz();                            
//                        }
//                    }
//                    spcRec.spcList.add(spcAdd); //добавим спецификацию
                    // </editor-fold>
                } else {
                    // <editor-fold defaultstate="collapsed" desc="AREA and STVORKA"> 
                    if (anglHoriz == 0 || anglHoriz == 180) { //по горизонтали
                        spcAdd.width += width() + 2 * gzazo;
                        spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);

                    } else if (anglHoriz == 90 || anglHoriz == 270) { //по вертикали
                        spcAdd.width += height() + 2 * gzazo;
                        spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);

                    } else {
                        System.out.println("Промах:builder.model.IArea5e.addFilling()");
                    }
                    spcAdd.anglCut0 = 45;
                    spcAdd.anglCut1 = 45;
                    spcAdd.anglHoriz = anglHoriz;
                    spcRec.spcList.add(spcAdd);
                    // </editor-fold>
                }

                if (anglHoriz == 0 || anglHoriz == 180) { //по горизонтали
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

                } else if (anglHoriz == 90 || anglHoriz == 270) { //по вертикали
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

            } else if (UseUnit.PIE.id == spcAdd.artiklRec.getInt(eArtikl.unit)) {

                if (spcAdd.mapParam.get(13014) != null) {
                    if (UCom.containsNumbJust(spcAdd.mapParam.get(13014), anglHoriz) == true) { //Углы ориентации стороны
                        spcRec.spcList.add(spcAdd);
                    }
                } else {
                    spcRec.spcList.add(spcAdd);
                }
            } else {
                System.out.println("Элемент не обработан");
            }
        } catch (Exception e) {
            System.err.println("Ошибка:ElemGlass.addSpecific()  " + e);
        }
    }

    public void paint() {
        if (owner.geom != null) {
            java.awt.Color color = winc.gc2d.getColor();
            winc.gc2d.setColor(new java.awt.Color(eColor.find(colorID2).getInt(eColor.rgb)));
            Shape shape = new ShapeWriter().toShape(this.geom);
            winc.gc2d.fill(shape);
            winc.gc2d.setColor(color);
        }
    }
}
