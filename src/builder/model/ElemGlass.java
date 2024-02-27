package builder.model;

import builder.Wincalc;
import builder.making.FillingSpc;
import builder.making.SpcRecord;
import builder.script.GsonElem;
import common.ArrayCom;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;

public class ElemGlass extends ElemSimple {

    public double radiusGlass = 0; //радиус стекла
    public double anglGHoriz = 0; //угол к горизонту
    public double gzazo = 0; //зазор между фальцем и стеклопакетом 
    //public double gaxis = 0; //размер от оси до стеклопакета
    public HashMap<Integer, Double> gaxisMap = new HashMap<Integer, Double>(); //размер от оси до стеклопакета
    public int indexSegm = 0;

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
        Geometry geo = UGeo.geoPadding(owner.area.getGeometryN(0), winc.listElem, 0);
        Envelope env = geo.getEnvelopeInternal();
        setDimension(env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY());
        this.area = geo;
    }

    //Главная спецификация    
    @Override
    public void setSpecific() {
        try {
            spcRec.place = "ЗАП";
            spcRec.setArtikl(artiklRec);
            spcRec.setColor(colorID1, colorID2, colorID3);

            //Фича определения gzazo и gaxis на раннем этапе построения. 
            new FillingSpc(winc, true).calc(this);

            //Внешний полигон створки/рамы для прорисовки 
            Coordinate[] coo = owner.area.getGeometryN(0).getCoordinates();

            Coordinate[] out = new Coordinate[coo.length];
            ArrayCom<ElemSimple> listFrame = winc.listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.IMPOST, Type.SHTULP, Type.STOIKA);
            for (int i = 0; i < coo.length; i++) {

                //Сегменты полигона
                int j = (i == coo.length - 1) ? 1 : i + 1;
                int k = (i == 0 || i == coo.length - 1) ? coo.length - 2 : i - 1;
                LineSegment segm1 = new LineSegment(coo[k], coo[i]);
                LineSegment segm2 = new LineSegment(coo[i], coo[j]);

                //Ширина сегментов
                ElemSimple e1 = listFrame.get(coo[k].z);
                ElemSimple e2 = listFrame.get(coo[i].z);
                double falz = winc.syssizRec.getDbl(eSyssize.falz);
                double w1 = (winc.syssizRec == null) ? e1.artiklRec.getDbl(eArtikl.size_centr) + gaxisMap.get(k)
                        : (e1.artiklRec.getDbl(eArtikl.height) - e1.artiklRec.getDbl(eArtikl.size_centr)) - falz + gzazo;
                double w2 = (winc.syssizRec == null) ? e2.artiklRec.getDbl(eArtikl.size_centr) + gaxisMap.get(i)
                        : (e2.artiklRec.getDbl(eArtikl.height) - e2.artiklRec.getDbl(eArtikl.size_centr)) - falz + gzazo;

                //Смещение сегментов
                LineSegment segm3 = segm1.offset(-w1);
                LineSegment segm4 = segm2.offset(-w2);

                //Точка пересечения внутренних сегментов
                out[i] = segm4.lineIntersection(segm3);
                out[i].z = e2.id;
            }
            //Полигон элемента конструкции
            this.area = Com5t.gf.createPolygon(out);

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

                LineSegment segm1 = UGeo.getSegment(this.area, indexSegm - 1);
                LineSegment segm2 = UGeo.getSegment(this.area, indexSegm + 1);
                LineSegment segm3 = UGeo.getSegment(this.area, indexSegm + 2);

                spcAdd.width += segm1.getLength() + 2 * gzazo;
                spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);
                spcAdd.anglCut0 = Math.toDegrees(Angle.angleBetween(segm2.p1, segm2.p0, segm1.p0)) / 2;
                spcAdd.anglCut1 = Math.toDegrees(Angle.angleBetween(segm2.p0, segm2.p1, segm3.p1)) / 2;
                spcAdd.anglHoriz = spcAdd.elem5e.anglHoriz(); //угол к гор. сторон стекла;

                spcRec.spcList.add(spcAdd);

                if (anglGHoriz == 0 || anglGHoriz == 180) { //по горизонтали
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

                } else if (anglGHoriz == 90 || anglGHoriz == 270) { //по вертикали
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
                    if (UCom.containsNumbJust(spcAdd.mapParam.get(13014), anglGHoriz) == true) { //Углы ориентации стороны
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
