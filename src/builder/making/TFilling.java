package builder.making;

import dataset.Record;
import domain.eArtikl;
import domain.eGlasdet;
import domain.eGlasgrp;
import domain.eGlasprof;
import java.util.HashMap;
import java.util.List;
import builder.Wincalc;
import builder.model.Com5t;
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

    public void fill(ElemSimple elemGlass) {
        try {
            Double depth = elemGlass.artiklRec.getDbl(eArtikl.depth); //толщина стекда           
            ArrayList<ElemSimple> listFrame = UCom.filter(winc.listElem, Type.BOX_SIDE, Type.STV_SIDE, Type.IMPOST, Type.SHTULP, Type.STOIKA);
            Set<Double> hs = new LinkedHashSet();
            Geometry areaFalz = elemGlass.owner.area.getGeometryN(2);
            List.of(areaFalz.getCoordinates()).forEach(p -> hs.add(p.z));

            //Цикл по сторонам стеклопакета
            Double arr[] = hs.toArray(new Double[0]);
            for (int indexSegm = 0; indexSegm < arr.length; indexSegm++) {
                try {
                    ElemGlass elGlass = (ElemGlass) elemGlass;
                    elGlass.sideglass = indexSegm; //индекс стороны стеклопакета 
                    double ID = arr[indexSegm];
                    elGlass.frameglass = listFrame.stream().filter(e -> e.id == ID).findFirst().get();

                    //Цикл по группам заполнений
                    for (Record glasgrpRec : eGlasgrp.filter()) {
                        if (UCom.containsNumbJust(glasgrpRec.getStr(eGlasgrp.depth), depth) == true) { //доступные толщины 

                            //Цикл по профилям в группах заполнений
                            List<Record> glasprofList = eGlasprof.filter2(glasgrpRec.getInt(eGlasgrp.id)); //список профилей в группе заполнений
                            for (Record glasprofRec : glasprofList) {
                                if (elGlass.frameglass.artiklRecAn.getInt(eArtikl.id) == glasprofRec.getInt(eGlasprof.artikl_id)) { //если артикулы совпали
                                    if (List.of(1, 2, 3, 4).contains(glasprofRec.getInt(eGlasprof.inside))) {  //внутреннее заполнение допустимо

                                        //ФИЛЬТР вариантов, параметры накапливаются в спецификации элемента
                                        if (fillingVar.filter(elemGlass, glasgrpRec) == true) {

                                            elGlass.gzazo = glasgrpRec.getDbl(eGlasgrp.gap); //зазор между фальцем и стеклопакетом
                                            elGlass.axisMap.put(indexSegm, glasprofRec.getDbl(eGlasprof.gsize)); //размер от оси до стеклопакета

                                            if (shortPass == false) {
                                                List<Record> glasdetList = eGlasdet.filter(glasgrpRec.getInt(eGlasgrp.id), elemGlass.artiklRec.getDbl(eArtikl.depth));
                                                detail(elemGlass, glasgrpRec, glasdetList);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка:Filling.fill(111) " + e);
                }
            }
            //((ElemGlass) elemGlass).frameGlass = null;

        } catch (Exception e) {
            System.err.println("Ошибка:Filling.fill() " + e);
        }
    }

    protected void detail(ElemSimple elemGlass, Record glasgrpRec, List<Record> glasdetList) {
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
                        elemGlass.addSpecific(spcAdd);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Filling.detail() " + e);
        }
    }
}
