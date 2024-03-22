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
import common.ArrayCom;
import common.UCom;
import dataset.Query;
import enums.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.locationtech.jts.geom.Coordinate;
import startup.Test;

/**
 * Заполнения
 */
public class SpcFilling extends Cal5e {

    private FillingVar fillingVar = null;
    private FillingDet fillingDet = null;
    private ElementDet elementDet = null;

    public SpcFilling(Wincalc winc) {
        super(winc);
        fillingVar = new FillingVar(winc);
        fillingDet = new FillingDet(winc);
        elementDet = new ElementDet(winc);
    }

    public SpcFilling(Wincalc winc, boolean shortPass) {
        super(winc);
        fillingVar = new FillingVar(winc);
        fillingDet = new FillingDet(winc);
        elementDet = new ElementDet(winc);
        this.shortPass = shortPass;
    }

    @Override
    public void calc() {
        ArrayList<ElemSimple> elemGlassList = winc.listElem.filter(Type.GLASS);
        //Цикл по списку заполнений
        for (ElemSimple elemGlass : elemGlassList) {
            calc(elemGlass);
        }
    }

    public void calc(ElemSimple elemGlass) {
        super.calc();
        try {
            Double depth = elemGlass.artiklRec.getDbl(eArtikl.depth); //толщина стекда           
            List<ElemSimple> elemFrameList = new ArrayList<ElemSimple>(winc.root.frames);  //список рам конструкции

            ArrayCom<ElemSimple> listFrame = winc.listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.IMPOST, Type.SHTULP, Type.STOIKA);
            Coordinate[] coo = elemGlass.area.getGeometryN(0).getCoordinates();
            if (elemGlass.area.getGeometryN(0).getEnvelopeInternal().getMaxY() <= coo[0].y) {
                coo[0].z = coo[1].z;
                coo[2].z = coo[1].z;
                coo[coo.length - 1].z = coo[1].z;               
            }
            Set hs = new HashSet();
            List.of(elemGlass.area.getCoordinates()).forEach(p -> hs.add(p.z));
            //if (elemGlass.id == 6) {
                //new Test().mpol = elemGlass.area.getGeometryN(0);
            //}
            //Цикл по сторонам стеклопакета
            for (int indexSegm = 0; indexSegm < hs.size(); indexSegm++) {

                ElemGlass elGlass = (ElemGlass) elemGlass;
                elGlass.sideClass = indexSegm; //индекс стороны стеклопакета 
                elGlass.frameGlass = listFrame.get(coo[indexSegm].z);

                //Цикл по группам заполнений
                for (Record glasgrpRec : eGlasgrp.findAll()) {
                    if (UCom.containsNumbJust(glasgrpRec.getStr(eGlasgrp.depth), depth) == true) { //доступные толщины 

                        //Цикл по профилям в группах заполнений
                        List<Record> glasprofList = eGlasprof.find(glasgrpRec.getInt(eGlasgrp.id)); //список профилей в группе заполнений
                        for (Record glasprofRec : glasprofList) {
                            if (elGlass.frameGlass.artiklRecAn.getInt(eArtikl.id) == glasprofRec.getInt(eGlasprof.artikl_id)) { //если артикулы совпали
                                if (List.of(1, 2, 3, 4).contains(glasprofRec.getInt(eGlasprof.inside))) {  //внутреннее заполнение допустимо

                                    //ФИЛЬТР вариантов, параметры накапливаются в спецификации элемента
                                    if (fillingVar.filter(elemGlass, glasgrpRec) == true) {

                                        elGlass.gzazo = glasgrpRec.getDbl(eGlasgrp.gap); //зазор между фальцем и стеклопакетом
                                        elGlass.axisMap.put(indexSegm, glasprofRec.getDbl(eGlasprof.gsize)); //размер от оси до стеклопакета

                                        if (shortPass == false) {
                                            List<Record> glasdetList = eGlasdet.find(glasgrpRec.getInt(eGlasgrp.id), elemGlass.artiklRec.getDbl(eArtikl.depth));
                                            detail(elemGlass, glasgrpRec, glasdetList);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Filling.calc() " + e);
        } finally {
            Query.conf = conf;
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
                    SpcRecord spcAdd = new SpcRecord("ЗАП", glasdetRec, artiklRec, elemGlass, mapParam);

                    //Подбор текстуры
                    if (UColor.colorFromProduct(spcAdd)) {
                        elemGlass.addSpecific(spcAdd);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Filling.detail() " + e);
        }
    }
}
