package builder.making;

import dataset.Record;
import domain.eArtikl;
import domain.eGlasdet;
import domain.eGlasgrp;
import domain.eGlasprof;
import java.util.HashMap;
import java.util.List;
import builder.Wincalc;
import builder.model.ElemGlass;
import builder.param.ElementDet;
import builder.param.FillingDet;
import builder.param.FillingVar;
import builder.model.ElemSimple;
import common.UCom;
import enums.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

/**
 * Заполнения
 */
public class TFilling extends Cal5e {

    private FillingVar fillingVar = null;
    private FillingDet fillingDet = null;
    private ElementDet elementDet = null;

    public TFilling(Wincalc winc) {
        super(winc);
        fillingVar = new FillingVar(winc);
        fillingDet = new FillingDet(winc);
        elementDet = new ElementDet(winc);
    }

    public TFilling(Wincalc winc, boolean shortPass) {
        super(winc);
        fillingVar = new FillingVar(winc);
        fillingDet = new FillingDet(winc);
        elementDet = new ElementDet(winc);
        this.shortPass = shortPass;
    }

    public void fill() {
        ArrayList<ElemSimple> elemGlassList = UCom.filter(winc.listElem, Type.GLASS);
        //Цикл по списку заполнений
        for (ElemSimple elemGlass : elemGlassList) {
            fill((ElemSimple) elemGlass);
        }
    }

    public void fill(ElemSimple elem5e) {
        try {
            ElemGlass elemGlass = (ElemGlass) elem5e;
            Double depth = elemGlass.artiklRec.getDbl(eArtikl.depth); //толщина стекда           
            ArrayList<ElemSimple> listFrame = UCom.filter(winc.listElem, Type.BOX_SIDE, Type.STV_SIDE, Type.IMPOST, Type.SHTULP, Type.STOIKA);
            Set<Double> hs = new LinkedHashSet();
            Geometry geoFalz = elemGlass.owner.area.getGeometryN(2);
            Coordinate cooFalz[] = geoFalz.getCoordinates();
            List.of(cooFalz).forEach(p -> hs.add(p.z));

            //Цикл по сторонам стеклопакета
            Double arr[] = hs.toArray(new Double[0]);

            if (elemGlass.id == 6.0) {
                System.out.println("");
            }
            for (int indexSegm = 0; indexSegm < arr.length; indexSegm++) {

                elemGlass.side_index = indexSegm; //индекс стороны стеклопакета                
                double ID = arr[indexSegm];
                elemGlass.side_frame = listFrame.stream().filter(e -> e.id == ID).findFirst().orElse(null);

                //Цикл по группам заполнений
                for (Record glasgrpRec : eGlasgrp.filter()) {
                    if (UCom.containsNumbJust(glasgrpRec.getStr(eGlasgrp.depth), depth) == true) { //доступные толщины 

                        //Цикл по профилям в группах заполнений
                        List<Record> glasprofList = eGlasprof.filter2(glasgrpRec.getInt(eGlasgrp.id)); //список профилей в группе заполнений
                        for (Record glasprofRec : glasprofList) {

                            if (elemGlass.side_frame.artiklRecAn.getInt(eArtikl.id) == glasprofRec.getInt(eGlasprof.artikl_id)) { //если артикулы совпали
                                if (List.of(1, 2, 3, 4).contains(glasprofRec.getInt(eGlasprof.inside))) {  //внутреннее заполнение допустимо
//                                            if (elemGlass.id == 6.0 && glasgrpRec.getInt(1) == 46) {
//                                                boolean bbb = elem5e.owner.area.getGeometryN(0).isRectangle();
//                                                int index = elemGlass.side_index;
//                                            }
                                    //ФИЛЬТР вариантов, параметры накапливаются в спецификации элемента
                                    if (fillingVar.filter(elemGlass, glasgrpRec) == true) {

                                        elemGlass.gzazo = glasgrpRec.getDbl(eGlasgrp.gap); //зазор между фальцем и стеклопакетом
                                        elemGlass.axisMap.put(indexSegm, glasprofRec.getDbl(eGlasprof.gsize)); //размер от оси до стеклопакета

                                        if (shortPass == false) {
                                            List<Record> glasdetList = eGlasdet.filter(glasgrpRec.getInt(eGlasgrp.id), elemGlass.artiklRec.getDbl(eArtikl.depth));
                                            if (elemGlass.id == 6.0 && glasgrpRec.getInt(1) == 48) {
                                                int index = elemGlass.side_index;
                                            }
                                            detail(elemGlass, glasgrpRec, glasdetList);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //((ElemGlass) elemGlass).frameGlass = null;

        } catch (Exception e) {
            System.err.println("Ошибка:Filling.fill() " + e);
        }
    }

    protected void detail(ElemGlass elemGlass, Record glasgrpRec, List<Record> glasdetList) {
        try {
            //Цикл по списку детализации
            for (Record glasdetRec : glasdetList) {

                HashMap<Integer, String> mapParam = new HashMap<Integer, String>(); //тут накапливаются параметры element и specific                        

                //ФИЛЬТР детализации, параметры накапливаются в mapParam
                if (fillingDet.filter(mapParam, elemGlass, glasdetRec) == true) {
                    Record artiklRec = eArtikl.find(glasdetRec.getInt(eGlasdet.artikl_id), false);
                    TRecord spcAdd = new TRecord("ЗАП", glasdetRec, artiklRec, elemGlass, mapParam);
                    spcAdd.variantRec = glasgrpRec;
                    //Подбор текстуры
                    if (UColor.colorFromElemOrSeri(spcAdd)) {

                        if (elemGlass.id == 6.0 && "420610".equals(spcAdd.artikl)) { // && glasgrpRec.getInt(1) == 48) {
                            int id = glasgrpRec.getInt(1);
                            int index = elemGlass.side_index;
                        }
                        
                        elemGlass.addSpecific(spcAdd);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Filling.detail() " + e);
        }
    }
}
