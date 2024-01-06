package builder.making;

import dataset.Record;
import domain.eArtikl;
import domain.eGlasdet;
import domain.eGlasgrp;
import domain.eGlasprof;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import builder.Wincalc;
import builder.model.ElemGlass;
import builder.param.ElementDet;
import builder.param.FillingDet;
import builder.param.FillingVar;
import builder.model.ElemSimple;
import builder.model.UGeo;
import common.LinkedCom;
import common.UCom;
import dataset.Query;
import enums.Type;
import java.util.ArrayList;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

/**
 * Заполнения
 */
public class Filling extends Cal5e {

    private FillingVar fillingVar = null;
    private FillingDet fillingDet = null;
    private ElementDet elementDet = null;

    public Filling(Wincalc winc) {
        super(winc);
        fillingVar = new FillingVar(winc);
        fillingDet = new FillingDet(winc);
        elementDet = new ElementDet(winc);
    }

    public Filling(Wincalc winc, boolean shortPass) {
        super(winc);
        fillingVar = new FillingVar(winc);
        fillingDet = new FillingDet(winc);
        elementDet = new ElementDet(winc);
        this.shortPass = shortPass;
    }

    @Override
    public void calc() {
        LinkedList<ElemSimple> elemGlassList = winc.listElem.filter(Type.GLASS);
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
            
            LinkedCom<ElemSimple> listFrame = winc.listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.IMPOST, Type.SHTULP, Type.STOIKA);
            Coordinate[] coo = elemGlass.owner.area.getCoordinates();
            
            //Цикл по сторонам стеклопакета
            for (int indexSegm = 0; indexSegm < coo.length - 1; indexSegm++) {              
                ElemSimple elemFrame = listFrame.find2(coo[indexSegm].z); // UGeo.segMapElem(listFrame, segm);

                //Цикл по группам заполнений
                for (Record glasgrpRec : eGlasgrp.findAll()) {
                    if (UCom.containsNumbJust(glasgrpRec.getStr(eGlasgrp.depth), depth) == true) { //доступные толщины 
                        List<Record> glasprofList = eGlasprof.find(glasgrpRec.getInt(eGlasgrp.id)); //список профилей в группе заполнений
                        
                        ((ElemGlass) elemGlass).gzazo = glasgrpRec.getDbl(eGlasgrp.gap); //зазор между фальцем и стеклопакетом

                        //Цикл по профилям в группах заполнений
                        for (Record glasprofRec : glasprofList) {
                            if (elemFrame.artiklRecAn.getInt(eArtikl.id) == glasprofRec.getInt(eGlasprof.artikl_id)) { //если артикулы совпали
                                if (List.of(1, 2, 3, 4).contains(glasprofRec.getInt(eGlasprof.inside))) {  //внутреннее заполнение допустимо

                                    //ФИЛЬТР вариантов, параметры накапливаются в спецификации элемента
                                    if (fillingVar.filter(elemGlass, glasgrpRec) == true) {

                                        ((ElemGlass) elemGlass).indexSegm = indexSegm;                                                                                
                                        ((ElemGlass) elemGlass).gaxisMap.put(indexSegm, glasprofRec.getDbl(eGlasprof.gsize)); //размер от оси до стеклопакета

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

                HashMap<Integer, String> mapParam = new HashMap(); //тут накапливаются параметры element и specific                        

                //ФИЛЬТР детализации, параметры накапливаются в mapParam
                if (fillingDet.filter(mapParam, elemGlass, glasdetRec) == true) {
                    Record artiklRec = eArtikl.find(glasdetRec.getInt(eGlasdet.artikl_id), false);
                    Specific spcAdd = new Specific("ЗАП", glasdetRec, artiklRec, elemGlass, mapParam);

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
