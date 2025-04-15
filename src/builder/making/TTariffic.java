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
import builder.model.Com5t;
import static builder.model.Com5t.MAXSIDE;
import builder.model.ElemSimple;
import common.UCom;
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

    private boolean norm_otx = true;

    public TTariffic(Wincalc winc, boolean norm_otx) {
        super(winc);
        this.norm_otx = norm_otx;
    }

    //������� ����������� � ������ 
    //���� ������ � �������
    public void calculate() {
        try {
            double grpformN1 = percentMarkup(winc); //���������� �������� �� ������� ������� �����

            //������ �����-��� � �����. ���������
            //������� ��� ���������� ������ ��� ������ �������
            for (ElemSimple elem5e : winc.listElem) {
                if (filterPhantom(elem5e)) {

                    //Record artgrp1Rec = eGroups.find(elem5e.spcRec.artiklRec.getInt(eArtikl.groups1_id));
                    //double artiklS = artgrp2bRec.getDbl(eGroups.val, 0);  //������ ������ ���.���������
                    elem5e.spcRec.quant1 = formatAmount(elem5e.spcRec); //���������� ��� ������  
                    elem5e.spcRec.quant2 = (norm_otx == true) ? elem5e.spcRec.quant1 + (elem5e.spcRec.quant1 * elem5e.spcRec.waste / 100) : elem5e.spcRec.quant1; //���������� � �������
                    elem5e.spcRec.costprice = artdetCostprice(elem5e.spcRec); //������. �� ����. ARTDET � ����.����.

                    //��������� ������������
                    //���� �� ����������� ��������
                    for (TRecord spcRec : elem5e.spcRec.spcList) {
                        spcRec.quant1 = formatAmount(spcRec); //���������� ��� ������
                        spcRec.quant2 = (norm_otx == true) ? spcRec.quant1 + (spcRec.quant1 * spcRec.waste / 100) : spcRec.quant1; //���������� � �������
                        spcRec.costprice = artdetCostprice(spcRec); //������. �� ����. ARTDET � ����.����.
                    }
                }
            }

            //������ � ������ ������� � ������
            //���� �� ��������� �����������
            for (ElemSimple elem5e : winc.listElem) {
                if (filterPhantom(elem5e)) {

                    //���� �� �������� �������.                 
                    for (Record rulecalcRec : eRulecalc.filter()) {
                        rulecalcTarif(winc, rulecalcRec, elem5e.spcRec); //���������� �������������/��������� � coeff ��� � �� incr �������� �������
                    }

                    Record systreeRec = eSystree.find(winc.nuni);
                    {
                        Record artgrp1Rec = eGroups.find(elem5e.spcRec.artiklRec.getInt(eArtikl.groups1_id));
                        Record artgrp2Rec = eGroups.find(elem5e.spcRec.artiklRec.getInt(eArtikl.groups2_id));
                        double artiklK = artgrp1Rec.getDbl(eGroups.val, 1);  //������� ������ ���.���������
                        double artiklS = artgrp2Rec.getDbl(eGroups.val, 0);  //������ ������ ���.���������
                        double systreeK = systreeRec.getDbl(eSystree.coef, 1); //����. ��������������

                        elem5e.spcRec.costprice = elem5e.spcRec.costprice - elem5e.spcRec.costprice * artiklS / 100; //�������������� �� ������� 
                        elem5e.spcRec.price += elem5e.spcRec.costprice; //���� �� ����.��� 
                        double value = elem5e.spcRec.price * artiklK * systreeK;
                        elem5e.spcRec.price = value + grpformN1 * value / 100; //���� �� ����.��� 
                        elem5e.spcRec.cost1 = elem5e.spcRec.price * elem5e.spcRec.quant2; //��������� ��� ������                     
                        elem5e.spcRec.cost2 = elem5e.spcRec.cost1; //��������� �� ������� 
                        //System.out.println(elem5e.spcRec.cost1 + " -1- " + elem5e.spcRec.cost2);
                    }
                    //���� �� �����������
                    for (TRecord spcRec : elem5e.spcRec.spcList) {

                        //���� �� �������� �������.
                        for (Record rulecalcRec : eRulecalc.filter()) {
                            rulecalcTarif(winc, rulecalcRec, spcRec); //���������� �������������/��������� � coeff ��� � �� incr �������� �������
                        }

                        Record artgrp1Rec = eGroups.find(spcRec.artiklRec.getInt(eArtikl.groups1_id));
                        Record artgrp2Rec = eGroups.find(spcRec.artiklRec.getInt(eArtikl.groups2_id));
                        double artiklK = artgrp1Rec.getDbl(eGroups.val, 1);  //������� ������ ���.���������
                        double artiklS = artgrp2Rec.getDbl(eGroups.val, 0);  //������ ������ ���.���������
                        double systreeK = systreeRec.getDbl(eSystree.coef); //����. ��������������

                        spcRec.costprice = spcRec.costprice - spcRec.costprice * artiklS / 100; //�������������� �� ������� 
                        spcRec.price += spcRec.costprice; //���� �� ����.���                        
                        double value = spcRec.costprice * artiklK * systreeK;
                        spcRec.price = value + grpformN1 * value / 100; //���� �� ����.��� 
                        spcRec.cost1 = spcRec.price * spcRec.quant2; //��������� ��� ������                     
                        spcRec.cost2 = spcRec.cost1; //��������� �� �������  
                        //System.out.println(spcRec.cost1 + " -2- " + spcRec.cost2);
                    }
                }
            }

            //������ ���� �������� �����������
            for (ElemSimple elem5e : winc.listElem) {
                if (filterPhantom(elem5e)) {
                    elem5e.spcRec.weight = elem5e.spcRec.quant1 * elem5e.spcRec.artiklRec.getDbl(eArtikl.density);

                    for (TRecord spec : elem5e.spcRec.spcList) {
                        spec.weight = spec.quant1 * spec.artiklRec.getDbl(eArtikl.density);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("������:Tariffic.calculate() " + e);
        }
    }

    //�����-���/���������. ������� ������ ��� ��������� ��������, �������� ������ �� ������� eArtdet
    public static double artdetCostprice(TRecord spcRec) {

        double costpriceSum = 0; //�������������
        Record color1Rec = eColor.find(spcRec.colorID1);  //��������
        Record color2Rec = eColor.find(spcRec.colorID2);  //����������
        Record color3Rec = eColor.find(spcRec.colorID3);  //�������
        Record grpcolor1Rec = eGroups.find(color1Rec.getInt(eColor.groups_id));
        Record grpcolor2Rec = eGroups.find(color2Rec.getInt(eColor.groups_id));
        Record grpcolor3Rec = eGroups.find(color3Rec.getInt(eColor.groups_id));

        double grpcursK1 = eCurrenc.find(spcRec.artiklRec.getInt(eArtikl.currenc1_id)).getDbl(eCurrenc.cross_cour); //�����-���� ������ ��� �������� ��������
        double grpcursK2 = eCurrenc.find(spcRec.artiklRec.getInt(eArtikl.currenc2_id)).getDbl(eCurrenc.cross_cour); //�����-���� ������ ��� ���������� ������� (����������, �������, �������������)

        //���� �� ��������������� ������� ARTDET ���. ���������
        for (Record artdetRec : eArtdet.filter(spcRec.artiklRec.getInt(eArtikl.id))) {

            double costprice = 0, price = 0;
            boolean artdetUsed = false;

            //���� ����� ������������� �������� �� ����� 0, � ���� ��������2
            //����� ��������3 � �������� ����� ��������
            //
            //�������� �������� � �������������
            if (artdetRec.getDbl(eArtdet.cost_c4) != 0
                    && spcRec.colorID2 == spcRec.colorID3 && isUseTarif(artdetRec, color2Rec)) {
                {
                    spcRec.artdetRec[1] = artdetRec;
                    spcRec.artdetRec[2] = artdetRec;
                    //double mmm = spcRec.artiklRec.getInt(eArtikl.;
                    double artdet2T = artdetRec.getDbl(eArtdet.cost_c4); //����� ������������� ��������
                    double colorK2 = color2Rec.getDbl(eColor.coef2); //����.�����. �����.
                    double grpcolorK2 = grpcolor2Rec.getDbl(eGroups.val); //����. ������ �������                                       

                    costprice += (grpcolorK2 * colorK2 * artdet2T) / grpcursK2;
                }
                if (isUseTarif(artdetRec, color1Rec)) { //��������� ����� �������� ��������
                    spcRec.artdetRec[0] = artdetRec;
                    double artdetT1 = artdetRec.getDbl(eArtdet.cost_c1); //����� ������������� ��������
                    double colorK1 = color1Rec.getDbl(eColor.coef2); //����.�����. �����.
                    double grpcolorK1 = grpcolor1Rec.getDbl(eGroups.val); //����. ������ �������                                       

                    costprice += (grpcolorK1 * colorK1 * artdetT1) / grpcursK2;
                }
                artdetUsed = true;

                //�������� �Ш� �������
            } else {
                //��������� ����� �������� ��������
                if (isUseTarif(artdetRec, color1Rec)) {
                    artdetUsed = true;
                    spcRec.artdetRec[0] = artdetRec;
                    double artdetT1 = artdetRec.getDbl(eArtdet.cost_c1); //����� �������� ��������
                    double coef = grpcolor1Rec.getDbl(eGroups.val) * color1Rec.getDbl(eColor.coef1);

                    costprice += coef * artdetT1 / grpcursK1;
                }
                //��������� ����� ���������� ��������
                if (isUseTarif(artdetRec, color2Rec)) {
                    artdetUsed = true;
                    spcRec.artdetRec[1] = artdetRec;
                    double artdetT2 = artdetRec.getDbl(eArtdet.cost_c2); //����� ���������� ��������
                    double coef = grpcolor2Rec.getDbl(eGroups.val) * color2Rec.getDbl(eColor.coef2);

                    costprice += coef * artdetT2 / grpcursK2;
                }
                //��������� ����� ������� ��������
                if (isUseTarif(artdetRec, color3Rec)) {
                    artdetUsed = true;
                    spcRec.artdetRec[2] = artdetRec;
                    double artdetT3 = artdetRec.getDbl(eArtdet.cost_c3); //����� ������� ��������
                    double coef = grpcolor3Rec.getDbl(eGroups.val) * color3Rec.getDbl(eColor.coef3);

                    costprice += coef * artdetT3 / grpcursK2;
                }
            }
            //�������� ������������ ������ (���������� ��������)
            {
                //?
            }
            //������� (����. �������)
            if (artdetUsed == true) { //���� ���� ���������
                if ("���".equals(spcRec.place.substring(0, 3)) == true) {
                    if (spcRec.detailRec != null) {
                        Record elementRec = eElement.find(spcRec.detailRec.getInt(eElemdet.element_id));
                        if (elementRec.getDbl(eElement.markup) > 0) {
                            price += price * elementRec.getDbl(eElement.markup) / 100;
                        }
                    }
                }
                double coef = (Scale.nakl_cost == true) ? artdetRec.getDbl(eArtdet.coef) : 1; //k���. ����. �������� �� ����. ARTDET 
                costpriceSum += costprice * coef;
            }
        }
        return costpriceSum;
    }

    //�����-���/��������� �� �������� �������
    public static void rulecalcTarif(Wincalc winc, Record rulecalcRec, TRecord spcRec) {

        try {
            Object artiklID = rulecalcRec.get(eRulecalc.artikl_id);
            //������ ������� ������
            if (spcRec.artiklRec.get(eArtikl.id) != null
                    && (spcRec.artiklRec.get(eArtikl.id).equals(artiklID) == true || artiklID == null)) {

                //������ ��� � ������ �������
                if ((spcRec.artiklRec.getInt(eArtikl.level1) * 100 + spcRec.artiklRec.getInt(eArtikl.level2)) == rulecalcRec.getInt(eRulecalc.type)) {

                    //������ ���� ������� ������ � ��������
                    if (UCom.containsColor(rulecalcRec.getStr(eRulecalc.color1), spcRec.colorID1) == true
                            && UCom.containsColor(rulecalcRec.getStr(eRulecalc.color2), spcRec.colorID2) == true
                            && UCom.containsColor(rulecalcRec.getStr(eRulecalc.color3), spcRec.colorID3) == true) {

                        double quantity3 = spcRec.quant2; //�� ���������� � ��������

                        //����� ���������� � ������� (�� �������� ��� �������, ����)
                        if (rulecalcRec.getInt(eRulecalc.common) == 1) {
                            quantity3 = 0;
                            //������ �� ��������
                            if (rulecalcRec.get(eRulecalc.artikl_id) != null) {
                                for (ElemSimple elem5e : winc.listElem) { //�������� �����. ���� ��������� (�������� ��������)
                                    if (filterPhantom(elem5e)) {
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
                                for (ElemSimple elem5e : winc.listElem) { //�������� �����. ���� ��������� (�������� ��������)
                                    if (filterPhantom(elem5e)) {
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
                        }
                        //������ �� ���������� ��������
                        if (UCom.containsNumbJust(rulecalcRec.getStr(eRulecalc.quant), quantity3) == true) {
                            //������� �� ����� �������
                            int typeformID1 = TypeForm.typeform(spcRec.elem5e);
                            int typeformID2 = (rulecalcRec.getInt(eRulecalc.form) == 0) ? 1 : rulecalcRec.getInt(eRulecalc.form);
                            if (typeformID1 == typeformID2) {
                                //�� ������������� ��� ���������
                                if (rulecalcRec.getInt(eRulecalc.sebes) == 1) {
                                    spcRec.costprice = spcRec.costprice * rulecalcRec.getDbl(eRulecalc.coeff) + rulecalcRec.getDbl(eRulecalc.suppl);  //���������� ������������� � coeff ��� � �� incr �������� ��������
                                } else {
                                    spcRec.price = spcRec.price * rulecalcRec.getDbl(eRulecalc.coeff) + rulecalcRec.getDbl(eRulecalc.suppl);  //���������� ��������� � coeff ��� � �� incr �������� ��������                                   
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("������:Tariffic.rulecalcTarif() " + e);
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
            return spcRec.count * precision(spcRec.width) / 1000;

        } else if (UseUnit.METR2.id == spcRec.artiklRec.getInt(eArtikl.unit)) { //��. �����
            return spcRec.count * precision(spcRec.width) * precision(spcRec.height) / 1000000;

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
    public static boolean isUseTarif(Record artdetRec, Record colorRec) {

        if (artdetRec.getInt(eArtdet.color_fk) < 0) { //���� ����� ����� ��� ������ �������
            if (colorRec.getInt(eColor.groups_id) == artdetRec.getInt(eArtdet.color_fk)) {
                return true; //�������� ����������� ������
            }
        } else if (colorRec.getInt(eColor.id) == artdetRec.getInt(eArtdet.color_fk)) {
            return true; //�������� �������
        }
        return false;
    }

    private static double precision(double value) {
        if (Scale.precision == 0) {
            return value;
        }
        int places = (Scale.precision == 3) ? 1 : (Scale.precision == 2) ? 2 : 3;
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //������ �� �������, ������� ����...
    private static boolean filterPhantom(ElemSimple elem5e) {
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
