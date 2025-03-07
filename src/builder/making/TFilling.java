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
 * ����������
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
        //���� �� ������ ����������
        for (ElemSimple elemGlass : elemGlassList) {
            fill((ElemSimple) elemGlass);
        }
    }

    public void fill(ElemSimple elem5e) {
        try {
            ElemGlass elemGlass = (ElemGlass) elem5e;
            Double depth = elemGlass.artiklRec.getDbl(eArtikl.depth); //������� ������           
            ArrayList<ElemSimple> listFrame = UCom.filter(winc.listElem, Type.BOX_SIDE, Type.STV_SIDE, Type.IMPOST, Type.SHTULP, Type.STOIKA);
            Set<Double> hs = new LinkedHashSet();
            Geometry geoFalz = elemGlass.owner.area.getGeometryN(2);
            List.of(geoFalz.getCoordinates()).forEach(p -> hs.add(p.z));

            //���� �� �������� ������������
            Double arr[] = hs.toArray(new Double[0]);
            for (int indexSegm = 0; indexSegm < arr.length; indexSegm++) {

                elemGlass.side_index = indexSegm; //������ ������� ������������                
                double ID = arr[indexSegm];
                elemGlass.side_frame = listFrame.stream().filter(e -> e.id == ID).findFirst().orElse(null);

                //���� �� ������� ����������
                for (Record glasgrpRec : eGlasgrp.filter()) {
                    if (UCom.containsNumbJust(glasgrpRec.getStr(eGlasgrp.depth), depth) == true) { //��������� ������� 

                        //���� �� �������� � ������� ����������
                        List<Record> glasprofList = eGlasprof.filter2(glasgrpRec.getInt(eGlasgrp.id)); //������ �������� � ������ ����������
                        for (Record glasprofRec : glasprofList) {

                            if (elemGlass.side_frame.artiklRecAn.getInt(eArtikl.id) == glasprofRec.getInt(eGlasprof.artikl_id)) { //���� �������� �������
                                if (List.of(1, 2, 3, 4).contains(glasprofRec.getInt(eGlasprof.inside))) {  //���������� ���������� ���������

                                    //������ ���������, ��������� ������������� � ������������ ��������
                                    if (fillingVar.filter(elemGlass, glasgrpRec) == true) {

                                        elemGlass.gzazo = glasgrpRec.getDbl(eGlasgrp.gap); //����� ����� ������� � �������������
                                        elemGlass.axisMap.put(indexSegm, glasprofRec.getDbl(eGlasprof.gsize)); //������ �� ��� �� ������������

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
        } catch (Exception e) {
            System.err.println("������:Filling.fill() " + e);
        }
    }

    protected void detail(ElemGlass elemGlass, Record glasgrpRec, List<Record> glasdetList) {
        try {
            //���� �� ������ �����������
            for (Record glasdetRec : glasdetList) {

                HashMap<Integer, String> mapParam = new HashMap<Integer, String>(); //��� ������������� ��������� element � specific                        

                //������ �����������, ��������� ������������� � mapParam
                if (fillingDet.filter(mapParam, elemGlass, glasdetRec) == true) {
                    Record artiklRec = eArtikl.find(glasdetRec.getInt(eGlasdet.artikl_id), false);
                    TRecord spcAdd = new TRecord("���", glasdetRec, artiklRec, elemGlass, mapParam);
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
