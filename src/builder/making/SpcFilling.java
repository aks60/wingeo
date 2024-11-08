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

/**
 * ����������
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

    public void calc() {
        ArrayList<ElemSimple> elemGlassList = UCom.filter(winc.listElem, Type.GLASS);
        //���� �� ������ ����������
        for (ElemSimple elemGlass : elemGlassList) {
            calc((ElemSimple) elemGlass);
        }
    }

    public void calc(ElemSimple elemGlass) {
        try {
            Double depth = elemGlass.artiklRec.getDbl(eArtikl.depth); //������� ������           
            //List<ElemSimple> elemFrameList = new ArrayList<ElemSimple>(winc.root.frames);  //������ ��� �����������

            ArrayList<ElemSimple> listFrame = UCom.filter(winc.listElem, Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.IMPOST, Type.SHTULP, Type.STOIKA);
            Set<Double> hs = new LinkedHashSet();
            ((ElemGlass) elemGlass).areaFalz.setUserData(hs);
            List.of(((ElemGlass) elemGlass).areaFalz.getCoordinates()).forEach(p -> hs.add(p.z));

            //���� �� �������� ������������
            Double arr[] = hs.toArray(new Double[0]);
            for (int indexSegm = 0; indexSegm < arr.length; indexSegm++) {

                ElemGlass elGlass = (ElemGlass) elemGlass;
                elGlass.sideglass = indexSegm; //������ ������� ������������ 
                double ID = arr[indexSegm];
                elGlass.frameglass = listFrame.stream().filter(e -> e.id == ID).findFirst().get();

                //���� �� ������� ����������
                for (Record glasgrpRec : eGlasgrp.filter()) {
                    if (UCom.containsNumbJust(glasgrpRec.getStr(eGlasgrp.depth), depth) == true) { //��������� ������� 

                        //���� �� �������� � ������� ����������
                        List<Record> glasprofList = eGlasprof.filter2(glasgrpRec.getInt(eGlasgrp.id)); //������ �������� � ������ ����������
                        for (Record glasprofRec : glasprofList) {
                            if (elGlass.frameglass.artiklRecAn.getInt(eArtikl.id) == glasprofRec.getInt(eGlasprof.artikl_id)) { //���� �������� �������
                                if (List.of(1, 2, 3, 4).contains(glasprofRec.getInt(eGlasprof.inside))) {  //���������� ���������� ���������

                                    //������ ���������, ��������� ������������� � ������������ ��������
                                    if (fillingVar.filter(elemGlass, glasgrpRec) == true) {

                                        elGlass.gzazo = glasgrpRec.getDbl(eGlasgrp.gap); //����� ����� ������� � �������������
                                        elGlass.axisMap.put(indexSegm, glasprofRec.getDbl(eGlasprof.gsize)); //������ �� ��� �� ������������

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
            }
            //((ElemGlass) elemGlass).frameGlass = null;
            
        } catch (Exception e) {
            System.err.println("������:Filling.calc() " + e);
        } 
    }

    protected void detail(ElemSimple elemGlass, Record glasgrpRec, List<Record> glasdetList) {
        try {
            //���� �� ������ �����������
            for (Record glasdetRec : glasdetList) {

                HashMap<Integer, String> mapParam = new HashMap<Integer, String>(); //��� ������������� ��������� element � specific                        

                //������ �����������, ��������� ������������� � mapParam
                if (fillingDet.filter(mapParam, elemGlass, glasdetRec) == true) {
                    Record artiklRec = eArtikl.find(glasdetRec.getInt(eGlasdet.artikl_id), false);
                    SpcRecord spcAdd = new SpcRecord("���", glasdetRec, artiklRec, elemGlass, mapParam);
                    spcAdd.variantRec = glasgrpRec;
                    //������ ��������
                    if (UColor.colorFromElemOrSeri(spcAdd)) {
                        elemGlass.addSpecific(spcAdd);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("������:Filling.detail() " + e);
        }
    }
}
