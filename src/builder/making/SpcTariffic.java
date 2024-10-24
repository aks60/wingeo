package builder.making;

import dataset.Record;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eCurrenc;
import domain.eGroups;
import domain.eRulecalc;
import domain.eSystree;
import enums.Layout;
import enums.TypeForm;
import enums.UseUnit;
import builder.Wincalc;
import builder.model.ElemSimple;
import common.UCom;
import dataset.Query;
import domain.eElemdet;
import domain.eElement;
import domain.ePrjkit;
import domain.ePrjprod;
import enums.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * ������ ��������� ��������� ���� �������� ��. � UML
 */
public class SpcTariffic extends Cal5e {

    private static boolean norm_otx = true;
    private static int precision = Math.round(new Query(eGroups.values())
            .sql(eGroups.data(), eGroups.up).get(0).getFloat(eGroups.val)); //���������� ����� ��������

    public SpcTariffic(Wincalc winc, boolean norm_otx) {
        super(winc);
        this.norm_otx = norm_otx;
    }

    //������� ����������� � ������ 
    //���� ������ � �������
    public void calc() {
        try {
            double percentMarkup = percentMarkup(winc); //���������� �������� �� ������� ������� �����

            //������ �����-��� �� ��.���. � �����. ���������
            //���� �� ��������� �����������
            for (ElemSimple elem5e : winc.listElem) {
                if (filter(elem5e)) {

                    elem5e.spcRec.sebes1 += artdetPrice(elem5e.spcRec); //������. �� ��. ��� ������ �� ����. ARTDET � ����. � ����.
                    elem5e.spcRec.quant1 = formatAmount(elem5e.spcRec); //���������� ��� ������
                    elem5e.spcRec.quant2 = elem5e.spcRec.quant1;  //������� ���������� � �������
                    if (norm_otx == true) {
                        elem5e.spcRec.quant2 = elem5e.spcRec.quant2 + (elem5e.spcRec.quant1 * elem5e.spcRec.waste / 100); //���������� � �������
                    }
                    //��������� ������������
                    //���� �� ����������� ��������
                    for (SpcRecord sp�Rec2 : elem5e.spcRec.spcList) {
                        sp�Rec2.sebes1 += artdetPrice(sp�Rec2); //������. �� ��. ��� ������
                        sp�Rec2.quant1 = formatAmount(sp�Rec2); //���������� ��� ������
                        sp�Rec2.quant2 = sp�Rec2.quant1; //������� ���������� � �������
                        if (norm_otx == true) {
                            sp�Rec2.quant2 = sp�Rec2.quant2 + (sp�Rec2.quant1 * sp�Rec2.waste / 100); //���������� � �������
                        }
                    }
                }
            }

            //������� � ������ ������� � ������
            //���� �� ��������� �����������
            for (ElemSimple elem5e : winc.listElem) {
                if (filter(elem5e)) {

                    // <editor-fold defaultstate="collapsed" desc="������� �����.">                     
                    //���� �� �������� �������.                 
                    for (Record rulecalcRec : eRulecalc.list()) {
                        //�� ���������� � �������������� �� ������� ������ �������
                        //���������� ������������� � coeff ��� � �� incr �������� �������.

                        //������ �� ���� '����� �������', � �����������. � ������ ������������ ������ 1, 4, 10, 12 ���������
                        int form = (rulecalcRec.getInt(eRulecalc.form) == 0) ? 1 : rulecalcRec.getInt(eRulecalc.form);
                        if (Type.GLASS == elem5e.type) {//������ ��� ������������

                            if (form == TypeForm.P00.id) {//�� ��������� �����
                                rulecalcPrise(winc, rulecalcRec, elem5e.spcRec);

                            } else if (form == TypeForm.P10.id && Type.TRAPEZE == elem5e.owner.type) { //�� �������������, �� ������� ����������
                                rulecalcPrise(winc, rulecalcRec, elem5e.spcRec);

                            } else if (form == TypeForm.P12.id && Type.ARCH == elem5e.owner.type) {//�� ������������� ���������� � ������
                                rulecalcPrise(winc, rulecalcRec, elem5e.spcRec);
                            }
                        } else if (form == TypeForm.P04.id && elem5e.type == Type.FRAME_SIDE
                                && elem5e.owner.type == Type.ARCH && elem5e.layout() == Layout.TOP) {  //������� � ��������  (������ ��� ���� ������� AYPC.W62.0101)
                            rulecalcPrise(winc, rulecalcRec, elem5e.spcRec); //������� � ��������

                        } else {
                            if (form == TypeForm.P00.id) {  //�� ��������� �����
                                rulecalcPrise(winc, rulecalcRec, elem5e.spcRec); //�� ��������� �� ��������� �����
                            }
                        }
                    }
                    // </editor-fold>                     
                    Record systreeRec = eSystree.find(winc.nuni);
                    elem5e.spcRec.sebes2 = elem5e.spcRec.sebes1 * elem5e.spcRec.quant2; //������. �� ��. � ������� 
                    Record artgrp1Rec = eGroups.find(elem5e.spcRec.artiklRec().getInt(eArtikl.groups1_id));
                    Record artgrp2Rec = eGroups.find(elem5e.spcRec.artiklRec().getInt(eArtikl.groups2_id));
                    double k1 = artgrp1Rec.getDbl(eGroups.val, 1);  //������� ������ ���.���������
                    double k2 = artgrp2Rec.getDbl(eGroups.val, 0);  //������ ������ ���.���������
                    double k3 = systreeRec.getDbl(eSystree.coef, 1); //����. ��������������
                    elem5e.spcRec.price1 = elem5e.spcRec.sebes2 * k1 * k3;
                    elem5e.spcRec.price1 = elem5e.spcRec.price1 + (elem5e.spcRec.price1 / 100) * percentMarkup; //��������� ��� ������                     
                    elem5e.spcRec.price2 = elem5e.spcRec.price1 - (elem5e.spcRec.price1 / 100) * k2; //��������� �� ������� 

                    //���� �� �����������
                    for (SpcRecord spc : elem5e.spcRec.spcList) {

                        // <editor-fold defaultstate="collapsed" desc="������� �����. ���������">  
                        //���� �� �������� �������.
                        for (Record rulecalcRec : eRulecalc.list()) {
                            int form = (rulecalcRec.getInt(eRulecalc.form) == 0) ? 1 : rulecalcRec.getInt(eRulecalc.form);
                            if (form == TypeForm.P00.id) { //�� ��������� ����� 
                                rulecalcPrise(winc, rulecalcRec, spc);
                            }
                        }
                        // </editor-fold> 
                        spc.sebes2 = spc.sebes1 * spc.quant2; //������. �� ��. � �������  
                        Record artgrp1bRec = eGroups.find(spc.artiklRec().getInt(eArtikl.groups1_id));
                        Record artgrp2bRec = eGroups.find(spc.artiklRec().getInt(eArtikl.groups2_id));
                        double m1 = artgrp1bRec.getDbl(eGroups.val, 1);  //������� ������ ���.���������
                        double m2 = artgrp2bRec.getDbl(eGroups.val, 0);  //������ ������ ���.���������
                        double m3 = systreeRec.getDbl(eSystree.coef); //����. ��������������
                        spc.price1 = spc.sebes2 * m1 * m3;
                        spc.price1 = spc.price1 + (spc.price1 / 100) * percentMarkup; //��������� ��� ������                         
                        spc.price2 = spc.price1 - (spc.price1 / 100) * m2; //��������� �� ������� 
                    }
                }
            }

            //������ ���� �������� �����������
            for (ElemSimple elem5e : winc.listElem) {
                if (filter(elem5e)) {
                    elem5e.spcRec.weight = elem5e.spcRec.quant1 * elem5e.spcRec.artiklRec().getDbl(eArtikl.density);

                    for (SpcRecord spec : elem5e.spcRec.spcList) {
                        spec.weight = spec.quant1 * spec.artiklRec().getDbl(eArtikl.density);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("������:Tariffic.calc() " + e);
        }
    }

    //��������� �����������    
    public static ArrayList<SpcRecord> kits(Record prjprodRec, Wincalc winc, boolean norm_otx) {
        ArrayList<SpcRecord> kitList = new ArrayList();
        try {
            Record systreeRec = eSystree.find(winc.nuni); //��� ������. ����. ��������������
            double percentMarkup = percentMarkup(winc); //���������� �������� �� ������� ������� �����
            if (prjprodRec != null) {
                List<Record> prjkitList = ePrjkit.filter3(prjprodRec.getInt(ePrjprod.id));

                //���� �� ����������
                for (Record prjkitRec : prjkitList) {
                    Record artiklRec = eArtikl.find(prjkitRec.getInt(ePrjkit.artikl_id), true);
                    if (artiklRec != null) {
                        SpcRecord spc = new SpcRecord("����", ++winc.spcId, prjkitRec, artiklRec, null);
                        spc.width = prjkitRec.getDbl(ePrjkit.width);
                        spc.height = prjkitRec.getDbl(ePrjkit.height);
                        spc.count = prjkitRec.getDbl(ePrjkit.numb);
                        spc.colorID1 = prjkitRec.getInt(ePrjkit.color1_id);
                        spc.colorID2 = prjkitRec.getInt(ePrjkit.color2_id);
                        spc.colorID3 = prjkitRec.getInt(ePrjkit.color3_id);
                        spc.anglCut0 = prjkitRec.getDbl(ePrjkit.angl1);
                        spc.anglCut1 = prjkitRec.getDbl(ePrjkit.angl2);
                        spc.quant1 = formatAmount(spc); //���������� ��� ������
                        spc.quant2 = (norm_otx == true) ? spc.quant1 + (spc.quant1 * spc.waste / 100) : spc.quant1; //���������� � �������
                        spc.sebes1 = artdetPrice(spc); //������. �� ��. ��� ������ �� ����. ARTDET � ����. � ����.
                        spc.sebes2 = spc.sebes1 + (spc.sebes1 * (spc.quant2 - spc.quant1)); //������. �� ��. � ������� 
                        Record artgrp1Rec = eGroups.find(spc.artiklRec().getInt(eArtikl.groups1_id));
                        Record artgrp2Rec = eGroups.find(spc.artiklRec().getInt(eArtikl.groups2_id));
                        double k1 = artgrp1Rec.getDbl(eGroups.val, 1);  //(koef)������� ������ ���.���������
                        double k2 = artgrp2Rec.getDbl(eGroups.val, 0);  //(%)������ ������ ���.���������
                        double k3 = systreeRec.getDbl(eSystree.coef, 1); //����. ��������������
                        double price = spc.count * spc.quant2 * spc.sebes2 * k1 * k3;
                        spc.price1 = price; //��������� ��� ������                     
                        spc.price2 = price - price / 100 * k2; //��������� �� ������� 
                        kitList.add(spc);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("������:Tariffic.kits() " + e);
        }
        return kitList;
    }

    //�����-��� �� ��. ���. ������� ������ ��� ��������� �������� �������� ������ �� ������� eArtdet
    private static double artdetPrice(SpcRecord specificRec) {

        double inPrice = 0;
        Record color1Rec = eColor.find(specificRec.colorID1);  //��������
        Record color2Rec = eColor.find(specificRec.colorID2);  //����������
        Record color3Rec = eColor.find(specificRec.colorID3);  //�������

        Record kursBaseRec = eCurrenc.find(specificRec.artiklRec().getInt(eArtikl.currenc1_id));    // �����-���� ������ ��� �������� ��������
        Record kursNoBaseRec = eCurrenc.find(specificRec.artiklRec().getInt(eArtikl.currenc2_id));  // �����-���� ������ ��� ���������� ������� (����������, �������, �������������)

        //���� �� ��������������� ������� ARTDET ���. ���������
        for (Record artdetRec : eArtdet.filter(specificRec.artiklRec().getInt(eArtikl.id))) {

            double artdetPrice = 0;
            boolean artdetUsed = false;

            //�������� �������� � �������������
            //���� ����� ������������� �������� �� ����� 0, � ����
            //��������1 ����� ��������2 � �������� ����� ��������
            if (artdetRec.getDbl(eArtdet.cost_c4) != 0
                    && color2Rec.getInt(eColor.id) == color3Rec.getInt(eColor.id)
                    && isTariff(artdetRec, color2Rec)) {

                double k1 = artdetRec.getDbl(eArtdet.cost_c4); //����� ������������� ��������
                double k2 = color2Rec.getDbl(eColor.coef2); //������� ����.���������� ��������
                double k3 = color3Rec.getDbl(eColor.coef3); //������� ����.������� ��������
                double k5 = kursNoBaseRec.getDbl(eCurrenc.cross_cour); //����� ���� 
                artdetPrice += (k1 * Math.max(k2, k3) / k5);

                if (isTariff(artdetRec, color1Rec)) { //��������� ����� �������� ��������
                    double m1 = artdetRec.getDbl(eArtdet.cost_unit); //����� ������� ���������
                    double m2 = (specificRec.elem5e == null) ? 0 : specificRec.elem5e.artiklRec.getDbl(eArtikl.density); //�������� ���                    
                    if (m1 > 0 && m2 > 0) {
                        artdetPrice += m1 * m2;
                    } else {
                        Record colgrpRec = eGroups.find(color1Rec.getInt(eColor.groups_id));
                        double z1 = artdetRec.getDbl(eArtdet.cost_c1); //����� �������� ��������"
                        double z2 = color1Rec.getDbl(eColor.coef1); //������� ����.�������� �����.
                        double z3 = colgrpRec.getDbl(eGroups.val); //����. ������ �������
                        double z5 = kursBaseRec.getDbl(eCurrenc.cross_cour); //����� ����
                        artdetPrice += (z1 * z2 * z3 * z5) / z5;
                    }
                }
                artdetUsed = true;

                //�������� �Ш� �������
            } else {
                //��������� ����� �������� ��������
                if (isTariff(artdetRec, color1Rec)) {
                    Record colgrpRec = eGroups.find(color1Rec.getInt(eColor.groups_id));
                    double k1 = artdetRec.getDbl(eArtdet.cost_c1); //����� �������� ��������
                    double k2 = color1Rec.getDbl(eColor.coef1); //������� ����.�������� ��������
                    double k3 = colgrpRec.getDbl(eGroups.val); //����. ������ �������
                    double k5 = kursBaseRec.getDbl(eCurrenc.cross_cour); //����� ����
                    artdetPrice += (k1 * k2 * k3) / k5;
                    artdetUsed = true;
                }
                //��������� ����� ���������� ��������
                if (isTariff(artdetRec, color2Rec)) {
                    Record colgrpRec = eGroups.find(color2Rec.getInt(eColor.groups_id));
                    double d1 = artdetRec.getDbl(eArtdet.cost_c2); //����� ���������� ��������
                    double d2 = color2Rec.getDbl(eColor.coef2); //������� ����.���������� ��������
                    double d3 = colgrpRec.getDbl(eGroups.val); //����. ������ �������
                    double d5 = kursNoBaseRec.getDbl(eCurrenc.cross_cour); //����� ����
                    artdetPrice += (d1 * d2 * d3) / d5;
                    artdetUsed = true;
                }
                //��������� ����� ������� ��������
                if (isTariff(artdetRec, color3Rec)) {
                    Record colgrpRec = eGroups.find(color3Rec.getInt(eColor.groups_id));
                    double w1 = artdetRec.getDbl(eArtdet.cost_c3); //����� ������� ��������
                    double w2 = color3Rec.getDbl(eColor.coef3); //������� ����.������� ��������
                    double w3 = colgrpRec.getDbl(eGroups.val); //����. ������ �������
                    double w5 = kursNoBaseRec.getDbl(eCurrenc.cross_cour); //����� ����
                    artdetPrice += (w1 * w2 * w3) / w5;
                    artdetUsed = true;
                }
            }
            //�������� ������������ ������ (���������� ��������)
            /*if (artdetUsed && artdetRec.getDbl(eArtdet.cost_min) != 0
                    && specificRec.quant1 != 0 && artdetPrice
                    * specificRec.quant1 < artdetRec.getDbl(eArtdet.cost_min)) {

                artdetPrice = artdetRec.getDbl(eArtdet.cost_min) / specificRec.quant1;
            }*/
            if (artdetUsed == true) { //���� ���� ���������
                if ("���".equals(specificRec.place.substring(0, 3)) == true) {
                    if (specificRec.detailRec != null) {
                        Record elementRec = eElement.find(specificRec.detailRec.getInt(eElemdet.element_id));
                        if (elementRec.getDbl(eElement.markup) > 0) {
                            artdetPrice = artdetPrice + (artdetPrice * (elementRec.getDbl(eElement.markup))) / 100;
                        }
                    }
                }
                inPrice = inPrice + (artdetPrice
                        * artdetRec.getDbl(eArtdet.coef)); //k���. �������� �� ����. ARTDET
            }
        }
        return inPrice;
    }

    //������� �������. ������ �� ���� form, color(1,2,3) ������� RULECALC
    private static void rulecalcPrise(Wincalc winc, Record rulecalcRec, SpcRecord spcRec) {

        try {
            //���� ������� ��� ��� ��� ������ �������
            if (spcRec.artiklRec().get(eArtikl.id) != null
                    && (spcRec.artiklRec().get(eArtikl.id).equals(rulecalcRec.get(eRulecalc.artikl_id)) == true || rulecalcRec.get(eRulecalc.artikl_id) == null)) {

                if ((spcRec.artiklRec().getInt(eArtikl.level1) * 100 + spcRec.artiklRec().getInt(eArtikl.level2)) == rulecalcRec.getInt(eRulecalc.type)) {
                    if (UCom.containsColor(rulecalcRec.getStr(eRulecalc.color1), spcRec.colorID1) == true
                            && UCom.containsColor(rulecalcRec.getStr(eRulecalc.color2), spcRec.colorID2) == true
                            && UCom.containsColor(rulecalcRec.getStr(eRulecalc.color3), spcRec.colorID3) == true) {

                        if (rulecalcRec.getInt(eRulecalc.common) == 0) {
                            if (UCom.containsNumbJust(rulecalcRec.getStr(eRulecalc.quant), spcRec.quant2) == true) {
                                spcRec.sebes1 = spcRec.sebes1 * rulecalcRec.getDbl(eRulecalc.coeff) + rulecalcRec.getDbl(eRulecalc.incr);  //���������� ������������� � coegg ��� � �� incr �������� ��������
                            }

                        } else if (rulecalcRec.getInt(eRulecalc.common) == 1) { //�� ������������� c �������� ������ ���������� �� ��������, �������, ����
                            ArrayList<ElemSimple> elemList = winc.listElem;
                            double quantity3 = 0;
                            if (rulecalcRec.get(eRulecalc.artikl_id) != null) { //�� ��������
                                for (ElemSimple elem5e : elemList) { //�������� �����. ���� ��������� (�������� ��������)
                                    if (filter(elem5e)) {
                                        if (elem5e.spcRec.artikl.equals(spcRec.artikl)) {
                                            quantity3 = quantity3 + elem5e.spcRec.quant1;
                                        }
                                        for (SpcRecord specifRec2 : elem5e.spcRec.spcList) {
                                            if (specifRec2.artikl.equals(spcRec.artikl)) {
                                                quantity3 = quantity3 + specifRec2.quant1;
                                            }
                                        }
                                    }
                                }
                            } else { //�� �������, ����
                                for (ElemSimple elem5e : elemList) { //�������� �����. ���� ��������� (�������� ��������)
                                    if (filter(elem5e)) {
                                        SpcRecord specifRec2 = elem5e.spcRec;
                                        if (specifRec2.artiklRec().getInt(eArtikl.level1) * 100 + specifRec2.artiklRec().getInt(eArtikl.level2) == rulecalcRec.getInt(eRulecalc.type)) {
                                            quantity3 = quantity3 + elem5e.spcRec.quant1;
                                        }
                                        for (SpcRecord specifRec3 : specifRec2.spcList) {
                                            if (specifRec3.artiklRec().getInt(eArtikl.level1) * 100 + specifRec3.artiklRec().getInt(eArtikl.level2) == rulecalcRec.getInt(eRulecalc.type)) {
                                                quantity3 = quantity3 + specifRec3.quant1;
                                            }
                                        }
                                    }
                                }
                            }
                            if (UCom.containsNumbJust(rulecalcRec.getStr(eRulecalc.quant), quantity3) == true) {
                                spcRec.sebes1 = spcRec.sebes1 * rulecalcRec.getDbl(eRulecalc.coeff) + rulecalcRec.getDbl(eRulecalc.incr); //���������� ������������� � coeff ��� � �� incr �������� ��������                      
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("������:Tariffic.rulecalcPrise() " + e);
        }
    }

    //� ���. �� ������� ���. ������������� ����������
    private static double formatAmount(SpcRecord spcRec) {
        //����� ��������� ��� ������� �� ������������ ������. ��. dll VirtualPro4::CalcArtTariff

        if (UseUnit.METR.id == spcRec.artiklRec().getInt(eArtikl.unit)) { //�����
            return spcRec.count * round(spcRec.width, precision) / 1000;

        } else if (UseUnit.METR2.id == spcRec.artiklRec().getInt(eArtikl.unit)) { //��. �����
            return spcRec.count * round(spcRec.width, precision) * round(spcRec.height, precision) / 1000000;

        } else if (UseUnit.PIE.id == spcRec.artiklRec().getInt(eArtikl.unit)) { //��.
            return spcRec.count;

        } else if (UseUnit.KIT.id == spcRec.artiklRec().getInt(eArtikl.unit)) { //����.
            return spcRec.count;

        } else if (UseUnit.ML.id == spcRec.artiklRec().getInt(eArtikl.unit)) { //��
            return spcRec.count;
        }
        return 0;
    }

    //���������� �������� �� ������� ������� �����
    private static double percentMarkup(Wincalc winc) {
        if (Type.ARCH == winc.root.type) {
            return eGroups.find(2101).getDbl(eGroups.val);

        } else if (Type.RECTANGL != winc.root.type) {
            return eGroups.find(2104).getDbl(eGroups.val);
        }
        return 0;
    }

    //���������, ������ �� ����������� �������� ����� ���-�������� ��� �������� ��������
    public static boolean isTariff(Record artdetRec, Record colorRec) {

        if (artdetRec.getInt(eArtdet.color_fk) < 0) { //���� ����� ����� ��� ������ �������
            if (colorRec.getInt(eColor.groups_id) == artdetRec.getInt(eArtdet.color_fk)) {
                return true; //�������� ����������� ������
            }
        } else if (colorRec.getInt(eColor.id) == artdetRec.getInt(eArtdet.color_fk)) {
            return true; //�������� �������
        }
        return false;
    }

    private static double round(double value, int places) {
        if (places == 0) {
            return value;
        }
        places = (places == 3) ? 1 : (places == 2) ? 2 : 3;
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //������ �� �������, ������� ����...
    private static boolean filter(ElemSimple elem5e) {
        if (elem5e.artiklRec == null) {
            return false;
        } else if (elem5e.artiklRec.getInt(eArtikl.id) == -3) {
            return false;
        } else if (elem5e.spcRec.artiklRec() == null) {
            return false;
        } else if (elem5e.spcRec.artiklRec().getInt(eArtikl.id) == -3) {
            return false;
        }
        return true;
    }
}
