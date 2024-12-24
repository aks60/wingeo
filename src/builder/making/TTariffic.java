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
import enums.Scale;
import enums.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * ������ ��������� ��������� ���� �������� ��. � UML
 */
public class TTariffic extends Cal5e {

    private static boolean norm_otx = true;
    public static int precision = Math.round(new Query(eGroups.values())
            .sql(eGroups.data(), eGroups.up).get(0).getFloat(eGroups.val)); //���������� ����� ��������

    public TTariffic(Wincalc winc, boolean norm_otx) {
        super(winc);
        this.norm_otx = norm_otx;
    }

    //������� ����������� � ������ 
    //���� ������ � �������
    public void calculate() {
        try {
            Scale.grpformN1.v = percentMarkup(winc); //���������� �������� �� ������� ������� �����

            //������ �����-��� � �����. ���������
            //������� ��� ���������� ������ ��� ������ �������
            for (ElemSimple elem5e : winc.listElem) {
                if (filter(elem5e)) {

                    elem5e.spcRec.quant1 = formatAmount(elem5e.spcRec); //���������� ��� ������  
                    elem5e.spcRec.quant = (norm_otx == true) ? elem5e.spcRec.quant1 + (elem5e.spcRec.quant1 * elem5e.spcRec.waste / 100) : elem5e.spcRec.quant1; //���������� � �������
                    elem5e.spcRec.sebes += artdetPrice(elem5e.spcRec); //������. �� ����. ARTDET � ����.����.

                    //��������� ������������
                    //���� �� ����������� ��������
                    for (TRecord spcRec : elem5e.spcRec.spcList) {
                        spcRec.quant1 = formatAmount(spcRec); //���������� ��� ������
                        spcRec.quant = (norm_otx == true) ? spcRec.quant1 + (spcRec.quant1 * spcRec.waste / 100) : spcRec.quant1; //���������� � �������
                        spcRec.sebes += artdetPrice(spcRec); //������. �� ��. ��� ������
                    }
                }
            }

            //������ � ������ ������� � ������
            //���� �� ��������� �����������
            for (ElemSimple elem5e : winc.listElem) {
                if (filter(elem5e)) {

                    //<editor-fold defaultstate="collapsed" desc="������� �����.">                     
                    //���� �� �������� �������.                 
                    for (Record rulecalcRec : eRulecalc.filter()) {
                        //�� ���������� � �������������� �� ������� ������ �������
                        //���������� �������������/��������� � coeff ��� � �� incr �������� �������.

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
                    Record artgrp1Rec = eGroups.find(elem5e.spcRec.artiklRec.getInt(eArtikl.groups1_id));
                    Record artgrp2Rec = eGroups.find(elem5e.spcRec.artiklRec.getInt(eArtikl.groups2_id));
                    Scale.artiklK.v = artgrp1Rec.getDbl(eGroups.val, 1);  //������� ������ ���.���������
                    Scale.artiklS.v = artgrp2Rec.getDbl(eGroups.val, 0);  //������ ������ ���.���������
                    Scale.systreeK.v = systreeRec.getDbl(eSystree.coef, 1); //����. ��������������

                    double sbs1 = elem5e.spcRec.sebes * Scale.artiklK.v * Scale.systreeK.v;
                    elem5e.spcRec.price1 = sbs1 + Scale.grpformN1.v * sbs1 / 100; //��������� �� ����.��� 
                    elem5e.spcRec.price2 = elem5e.spcRec.price1 * elem5e.spcRec.quant; //��������� ��� ������                     
                    elem5e.spcRec.price3 = elem5e.spcRec.price2 - Scale.artiklS.v * elem5e.spcRec.price2 / 100; //��������� �� ������� 

                    //���� �� �����������
                    for (TRecord spcRec : elem5e.spcRec.spcList) {

                        // <editor-fold defaultstate="collapsed" desc="������� �����. ���������">  
                        //���� �� �������� �������.
                        for (Record rulecalcRec : eRulecalc.filter()) {
                            int form = (rulecalcRec.getInt(eRulecalc.form) == 0) ? 1 : rulecalcRec.getInt(eRulecalc.form);
                            if (form == TypeForm.P00.id) { //�� ��������� ����� 
                                rulecalcPrise(winc, rulecalcRec, spcRec);
                            }
                        }
                        // </editor-fold> 

                        Record artgrp1bRec = eGroups.find(spcRec.artiklRec.getInt(eArtikl.groups1_id));
                        Record artgrp2bRec = eGroups.find(spcRec.artiklRec.getInt(eArtikl.groups2_id));
                        Scale.artiklK.v = artgrp1bRec.getDbl(eGroups.val, 1);  //������� ������ ���.���������
                        Scale.artiklS.v = artgrp2bRec.getDbl(eGroups.val, 0);  //������ ������ ���.���������
                        Scale.systreeK.v = systreeRec.getDbl(eSystree.coef); //����. ��������������

                        double sbs2 = spcRec.sebes * Scale.artiklK.v * Scale.systreeK.v;
                        spcRec.price1 = sbs2 + Scale.grpformN1.v * sbs2 / 100; //��������� �� ����.��� 
                        spcRec.price2 = spcRec.price1 * spcRec.quant; //��������� ��� ������                     
                        spcRec.price3 = spcRec.price2 - Scale.artiklS.v * spcRec.price2 / 100; //��������� �� �������                                   
                    }
                }
            }

            //������ ���� �������� �����������
            for (ElemSimple elem5e : winc.listElem) {
                if (filter(elem5e)) {
                    elem5e.spcRec.weight = elem5e.spcRec.quant1 * elem5e.spcRec.artiklRec.getDbl(eArtikl.density);

                    for (TRecord spec : elem5e.spcRec.spcList) {
                        spec.weight = spec.quant1 * spec.artiklRec.getDbl(eArtikl.density);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("������:Tariffic.calc() " + e);
        }
    }

    //�����-��� �� ��. ���. ������� ������ ��� ��������� �������� �������� ������ �� ������� eArtdet
    public static double artdetPrice(TRecord specificRec) {

        double inPrice = 0;
        Record color1Rec = eColor.find(specificRec.colorID1);  //��������
        Record color2Rec = eColor.find(specificRec.colorID2);  //����������
        Record color3Rec = eColor.find(specificRec.colorID3);  //�������

        Record cursYesBaseRec = eCurrenc.find(specificRec.artiklRec.getInt(eArtikl.currenc1_id));    // �����-���� ������ ��� �������� ��������
        Record cursNoBaseRec = eCurrenc.find(specificRec.artiklRec.getInt(eArtikl.currenc2_id));  // �����-���� ������ ��� ���������� ������� (����������, �������, �������������)

        //���� �� ��������������� ������� ARTDET ���. ���������
        for (Record artdetRec : eArtdet.filter(specificRec.artiklRec.getInt(eArtikl.id))) {

            double artdetPrice = 0;
            boolean artdetUsed = false;

            //�������� �������� � �������������
            //���� ����� ������������� �������� �� ����� 0, � ����
            //��������1 ����� ��������2 � �������� ����� ��������
            if (artdetRec.getDbl(eArtdet.cost_c4) != 0
                    && color2Rec.getInt(eColor.id) == color3Rec.getInt(eColor.id)
                    && isTariff(artdetRec, color2Rec)) {

                Scale.artdet2T.v = artdetRec.getDbl(eArtdet.cost_c4); //����� ������������� ��������
                Scale.colorK2.v = color2Rec.getDbl(eColor.coef2); //������� ����.���������� ��������
                Scale.colorK3.v = color3Rec.getDbl(eColor.coef3); //������� ����.������� ��������
                Scale.grpcursK2.v = cursNoBaseRec.getDbl(eCurrenc.cross_cour); //����� ���� �� ���. �����. 
                artdetPrice += (Scale.artdet2T.v * Math.max(Scale.colorK2.v, Scale.colorK3.v) / Scale.grpcursK2.v);

                if (isTariff(artdetRec, color1Rec)) { //��������� ����� �������� ��������
                    specificRec.artdetRec[0] = artdetRec;
                    specificRec.artdetRec[1] = artdetRec;
                    specificRec.artdetRec[2] = artdetRec;
                    double m1 = artdetRec.getDbl(eArtdet.cost_unit); //����� ������� ���������
                    double m2 = (specificRec.elem5e == null) ? 0 : specificRec.elem5e.artiklRec.getDbl(eArtikl.density); //�������� ���                    
                    if (m1 > 0 && m2 > 0) {
                        artdetPrice += m1 * m2;
                    } else {
                        Record colgrpRec = eGroups.find(color1Rec.getInt(eColor.groups_id));
                        Scale.artdetT1.v = artdetRec.getDbl(eArtdet.cost_c1); //����� �������� ��������"
                        Scale.colorK1.v = color1Rec.getDbl(eColor.coef1); //������� ����.�������� �����.
                        Scale.grpcolorK1.v = colgrpRec.getDbl(eGroups.val); //����. ������ �������
                        Scale.grpcursK1.v = cursYesBaseRec.getDbl(eCurrenc.cross_cour); //����� ����

                        artdetPrice += (Scale.artdetT1.v * Scale.colorK1.v * Scale.grpcolorK1.v * Scale.grpcursK1.v) / Scale.grpcursK1.v;
                    }
                }
                artdetUsed = true;

                //�������� �Ш� �������
            } else {
                //��������� ����� �������� ��������
                if (isTariff(artdetRec, color1Rec)) {
                    specificRec.artdetRec[0] = artdetRec;
                    Record colgrpRec = eGroups.find(color1Rec.getInt(eColor.groups_id));
                    Scale.artdetT1.v = artdetRec.getDbl(eArtdet.cost_c1); //����� �������� ��������
                    Scale.colorK1.v = color1Rec.getDbl(eColor.coef1); //������� ����. �������� ��������
                    Scale.grpcolorK1.v = colgrpRec.getDbl(eGroups.val); //����. ������ �������
                    Scale.grpcursK1.v = cursYesBaseRec.getDbl(eCurrenc.cross_cour); //����� ����

                    artdetPrice += (Scale.artdetT1.v * Scale.colorK1.v * Scale.grpcolorK1.v) / Scale.grpcursK1.v;
                    artdetUsed = true;
                }
                //��������� ����� ���������� ��������
                if (isTariff(artdetRec, color2Rec)) {
                    specificRec.artdetRec[1] = artdetRec;
                    Record colgrpRec = eGroups.find(color2Rec.getInt(eColor.groups_id));
                    Scale.artdetT2.v = artdetRec.getDbl(eArtdet.cost_c2); //����� ���������� ��������
                    Scale.colorK2.v = color2Rec.getDbl(eColor.coef2); //������� ����. ���������� ��������
                    Scale.grpcolorK1.v = colgrpRec.getDbl(eGroups.val); //����. ������ �������
                    Scale.grpcursK2.v = cursNoBaseRec.getDbl(eCurrenc.cross_cour); //����� ����

                    artdetPrice += (Scale.artdetT2.v * Scale.colorK2.v * Scale.grpcolorK1.v) / Scale.grpcursK2.v;
                    artdetUsed = true;
                }
                //��������� ����� ������� ��������
                if (isTariff(artdetRec, color3Rec)) {
                    specificRec.artdetRec[2] = artdetRec;
                    Record colgrpRec = eGroups.find(color3Rec.getInt(eColor.groups_id));
                    Scale.artdetT3.v = artdetRec.getDbl(eArtdet.cost_c3); //����� ������� ��������
                    Scale.colorK3.v = color3Rec.getDbl(eColor.coef3); //������� ����.������� ��������
                    Scale.grpcolorK3.v = colgrpRec.getDbl(eGroups.val); //����. ������ �������
                    Scale.grpcursK2.v = cursNoBaseRec.getDbl(eCurrenc.cross_cour); //����� ����

                    artdetPrice += (Scale.artdetT3.v * Scale.colorK3.v * Scale.grpcolorK3.v) / Scale.grpcursK2.v;
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

    //������� !!! ������� �������. ������ �� ����� ����������� 
    public static void rulecalcFilter(Wincalc winc, ElemSimple elem5e) {

        for (Record rulecalcRec : eRulecalc.filter()) {
            //�� ���������� � �������������� �� ������� ������ �������
            //���������� �������������/��������� � coeff ��� � �� incr �������� �������.

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
    }

    //������� �������. ������ ���������
    public static void rulecalcPrise(Wincalc winc, Record rulecalcRec, TRecord spcRec) {

        try {
            //������ ������� ������
            if (spcRec.artiklRec.get(eArtikl.id) != null
                    && (spcRec.artiklRec.get(eArtikl.id).equals(rulecalcRec.get(eRulecalc.artikl_id)) == true
                    || rulecalcRec.get(eRulecalc.artikl_id) == null)) {

                //������ ��� � ������ �������
                if ((spcRec.artiklRec.getInt(eArtikl.level1) * 100 + spcRec.artiklRec.getInt(eArtikl.level2)) == rulecalcRec.getInt(eRulecalc.type)) {

                    //Object o1 = (spcRec.artiklRec.getInt(eArtikl.level1) * 100 + spcRec.artiklRec.getInt(eArtikl.level2));
                    //Object o2 = rulecalcRec.getInt(eRulecalc.type);
                    //������ ���� ������� ������ � ��������
                    if (UCom.containsColor(rulecalcRec.getStr(eRulecalc.color1), spcRec.colorID1) == true
                            && UCom.containsColor(rulecalcRec.getStr(eRulecalc.color2), spcRec.colorID2) == true
                            && UCom.containsColor(rulecalcRec.getStr(eRulecalc.color3), spcRec.colorID3) == true) {

                        //������� �� ���������� ��������
                        if (rulecalcRec.getInt(eRulecalc.common) == 0) {
                            //������ �� ���������� ���������� ��������
                            if (UCom.containsNumbJust(rulecalcRec.getStr(eRulecalc.quant), spcRec.quant) == true) {
                                //�� ������������� ��� ���������
                                if (rulecalcRec.getInt(eRulecalc.sebes) == 1) {
                                    spcRec.sebes = spcRec.sebes * rulecalcRec.getDbl(eRulecalc.coeff) + rulecalcRec.getDbl(eRulecalc.suppl);  //���������� ������������� � coeff ��� � �� incr �������� ��������
                                } else {
                                    spcRec.price2 = spcRec.price2 * rulecalcRec.getDbl(eRulecalc.coeff) + rulecalcRec.getDbl(eRulecalc.suppl);  //���������� ��������� � coeff ��� � �� incr �������� ��������                                   
                                }
                            }

                            //������� �� ����� ��������� ����� �������
                        } else if (rulecalcRec.getInt(eRulecalc.common) == 1) { //�� ������������� c �������� ������ ���������� �� ��������, �������, ����
                            ArrayList<ElemSimple> elemList = winc.listElem;
                            double quantity3 = 0;

                            //������ �� ��������
                            if (rulecalcRec.get(eRulecalc.artikl_id) != null) {
                                for (ElemSimple elem5e : elemList) { //�������� �����. ���� ��������� (�������� ��������)
                                    if (filter(elem5e)) {
                                        if (elem5e.spcRec.artikl.equals(spcRec.artikl)) { //������ �� ��������
                                            quantity3 += elem5e.spcRec.quant1;
                                        }
                                        for (TRecord specifRec2 : elem5e.spcRec.spcList) {
                                            if (specifRec2.artikl.equals(spcRec.artikl)) { //������ �� ��������
                                                quantity3 += specifRec2.quant1;
                                            }
                                        }
                                    }
                                }
                                //������ �� �������, ����
                            } else {
                                for (ElemSimple elem5e : elemList) { //�������� �����. ���� ��������� (�������� ��������)
                                    if (filter(elem5e)) {
                                        TRecord specifRec2 = elem5e.spcRec;
                                        if (specifRec2.artiklRec.getInt(eArtikl.level1) * 100 + specifRec2.artiklRec.getInt(eArtikl.level2) == rulecalcRec.getInt(eRulecalc.type)) {
                                            quantity3 += elem5e.spcRec.quant1;
                                        }
                                        for (TRecord specifRec3 : specifRec2.spcList) {
                                            if (specifRec3.artiklRec.getInt(eArtikl.level1) * 100 + specifRec3.artiklRec.getInt(eArtikl.level2) == rulecalcRec.getInt(eRulecalc.type)) {
                                                quantity3 += specifRec3.quant1;
                                            }
                                        }
                                    }
                                }
                            }
                            //������ �� ���������� ����� �������
                            if (UCom.containsNumbJust(rulecalcRec.getStr(eRulecalc.quant), quantity3) == true) {
                                //�� ������������� ��� ���������
                                if (rulecalcRec.getInt(eRulecalc.sebes) == 1) {
                                    spcRec.sebes = spcRec.sebes * rulecalcRec.getDbl(eRulecalc.coeff) + rulecalcRec.getDbl(eRulecalc.suppl);  //���������� ������������� � coeff ��� � �� incr �������� ��������
                                } else {
                                    spcRec.price2 = spcRec.price2 * rulecalcRec.getDbl(eRulecalc.coeff) + rulecalcRec.getDbl(eRulecalc.suppl);  //���������� ��������� � coeff ��� � �� incr �������� ��������                                   
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("������:Tariffic.rulecalcPrise() " + e);
        }
    }

    //���������� �������� �� ������� ������� �����
    public static double percentMarkup(Wincalc winc) {
        if (Type.ARCH == winc.root.type) {
            return eGroups.find(2101).getDbl(eGroups.val);

        } else if (Type.RECTANGL != winc.root.type) {
            return eGroups.find(2104).getDbl(eGroups.val);
        }
        return 0;
    }

    //� ���. �� ������� ���. ������������� ����������
    public static double formatAmount(TRecord spcRec) {
        //����� ��������� ��� ������� �� ������������ ������. ��. dll VirtualPro4::CalcArtTariff

        if (UseUnit.METR.id == spcRec.artiklRec.getInt(eArtikl.unit)) { //�����
            return spcRec.count * round(spcRec.width, TTariffic.precision) / 1000;

        } else if (UseUnit.METR2.id == spcRec.artiklRec.getInt(eArtikl.unit)) { //��. �����
            return spcRec.count * round(spcRec.width, TTariffic.precision) * round(spcRec.height, TTariffic.precision) / 1000000;

        } else if (UseUnit.PIE.id == spcRec.artiklRec.getInt(eArtikl.unit)) { //��.
            return spcRec.count;

        } else if (UseUnit.KIT.id == spcRec.artiklRec.getInt(eArtikl.unit)) { //����.
            return spcRec.count;

        } else if (UseUnit.ML.id == spcRec.artiklRec.getInt(eArtikl.unit)) { //��
            return spcRec.count;
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
        } else if (elem5e.spcRec.artiklRec == null) {
            return false;
        } else if (elem5e.spcRec.artiklRec.getInt(eArtikl.id) == -3) {
            return false;
        }
        return true;
    }
}
