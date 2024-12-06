package enums;

import dataset.Record;
import builder.Wincalc;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eCurrenc;
import domain.eGroups;
import domain.eRulecalc;

public enum Scale {
    systreeK("����������� ��������������"), //SYSTREE 

    artiklK("koef. ������� ������ ���.���������"), //ARTIKL
    artiklS("����. ������ ������ ���.���������"), //ARTIKL

    artdetT1("����� ������. ��������"), //ARTDET
    artdetT2("����� �����. ��������"), //ARTDET 
    artdetT3("����� �����. ��������"), //ARTDET 
    artdet2T("����� ������. ��������"), //ARTDET 
    artdetK("k���. ������� ��������"), //ARTDET 

    grpcursK1("�����-���� �������� ��������"), //GROUPS 
    grpcursK2("�����-���� ���������� �������"), //GROUPS 
    grpcolK("k���. ������ ��������"), //GROUPS
    grpformN("�������� �� ���. ������� �����"), //GROUPS 

    colorK1("����. �������� ��������"), //COLOR 
    colorK2("����. ���������� ��������"), //COLOR 
    colorK3("����. ������� ��������"), //COLOR 

    rulecalcK("����. ������ �������"), //RULECALC 
    rulecalcN("����. ������ �������");  //RULECALC 

    public double v;
    public String s;

    Scale(String s) {

        this.v = 0;
        this.s = s;
    }

    public static void init(Wincalc winc, Record artiklRec, Record artdetRec, Record groupsRec, Record colorRec, Record rulecalcRec) {

        if (artiklRec != null) {
            Record artgrp1Rec = eGroups.find(artiklRec.getInt(eArtikl.groups1_id));
            Record artgrp2Rec = eGroups.find(artiklRec.getInt(eArtikl.groups2_id));
            artiklK.v = artgrp1Rec.getDbl(eGroups.val, 1); //koef. ������� ������ ���.���������
            artiklS.v = artgrp2Rec.getDbl(eGroups.val, 0); //����. ������ ������ ���.���������
            Record croscurs1Rec = eCurrenc.find(artiklRec.getInt(eArtikl.currenc1_id));
            Record croscurs2Rec = eCurrenc.find(artiklRec.getInt(eArtikl.currenc2_id));
            grpcursK1.v = croscurs1Rec.getDbl(eCurrenc.cross_cour); //�����-���� ������ ��� �������� ��������        
            grpcursK2.v = croscurs2Rec.getDbl(eCurrenc.cross_cour); //�����-���� ������ ��� ���������� ������� (����������, �������, �������������)            
        }

        if (artdetRec != null) {
            artdetT1.v = artdetRec.getDbl(eArtdet.cost_c1); //����� ������ ��������
            artdetT2.v = artdetRec.getDbl(eArtdet.cost_c2); //����� ����� ��������
            artdetT3.v = artdetRec.getDbl(eArtdet.cost_c3); //����� ����� ��������
        }

        if (groupsRec != null) {
            if (Type.ARCH == winc.root.type) { //�������� �� ���. ������� �����
                grpformN.v = eGroups.find(2101).getDbl(eGroups.val);

            } else if (Type.RECTANGL != winc.root.type) {
                grpformN.v = eGroups.find(2104).getDbl(eGroups.val);
            } else {
                grpformN.v = 0;
            }
            grpcolK.v = groupsRec.getDbl(eGroups.val); //����. ������ �������         
        }

        if (colorRec != null) {
            colorK1.v = colorRec.getDbl(eColor.coef1); //������� ����. ������. ��������
            colorK2.v = colorRec.getDbl(eColor.coef2); //������� ����. �����. ��������
            colorK3.v = colorRec.getDbl(eColor.coef3); //������� ����. �����. ��������   
        }

        if (rulecalcRec != null) {
            Scale.rulecalcK.v = rulecalcRec.getDbl(eRulecalc.coeff);
            Scale.rulecalcN.v = rulecalcRec.getDbl(eRulecalc.incr);
        }
    }
}
