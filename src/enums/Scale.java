package enums;

import dataset.Record;
import builder.Wincalc;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eCurrenc;
import domain.eGroups;
import domain.eRulecalc;

//����� ���������������
public enum Scale {

    systreeK(1, " - ����������� ��������������"), //SYSTREE     
    grpcursK1(1, " - ����. ���� ����� �������� ��������"), //GROUPS 
    grpcursK2(1, " - ����. ���� ����� ���������� �������"), //GROUPS  

    artiklK(1, " - k���. ������� ������ ���.���������"), //ARTIKL
    artiklS(0, " - ����. ������ ������ ���.���������"), //ARTIKL
    artiklW(0, " - ����. ������ ���.���������"), //ARTIKL
    
    separator1(-777, "-------------------������---------------------------------"),
    artdetT1(0, " - ����� ������. ��������"), //ARTDET
    artdetT2(0, " - ����� �����. ��������"), //ARTDET 
    artdetT3(0, " - ����� �����. ��������"), //ARTDET 
    artdet2T(0, " - ����� ������. ��������"), //ARTDET 
    
    separator2(-777, "--------------������� ����. �������-----------------------"),
    colorK1(1, " - ����. �������� ��������"), //COLOR 
    colorK2(1, " - ����. ���������� ��������"), //COLOR 
    colorK3(1, " - ����. ������� ��������"), //COLOR 
    grpcolorK1(1, " - k���. ������ ������. ��������"), //GROUPS
    grpcolorK2(1, " - k���. ������ �����. ��������"), //GROUPS
    grpcolorK3(1, " - k���. ������ �����. ��������"), //GROUPS
    artdetK1(1, " - k���. ������� ������. ��������"), //ARTDET 
    artdetK2(1, " - k���. ������� �����. ��������"), //ARTDET 
    artdetK3(1, " - k���. ������� �����. ��������"), //ARTDET 
    
    separator3(-777, "--------------������� ������� �����-----------------------"),
    grpformN1(0, " - ����. ������� �� ���. ������� �����"), //GROUPS 
    grpformN2(0, " - ����. ������� �� ���. ���������-�� ���������"), //GROUPS 

    separator9(-777, "-----------------������� ���ר��--------------------------"),
    rulecalcK(1, " - ����. ������ �������"), //RULECALC 
    rulecalcN(0, " - ���. ����. ������ �������");  //RULECALC 

    public double v;
    public String s;

    Scale(double v, String s) {

        this.v = v;
        this.s = s;
    }

    public static void init(Wincalc winc, Record artiklRec, Record artdetRec[], Record groupsRec[], Record colorRec[], Record rulecalcRec) {

        Record artgrp1Rec = eGroups.find(artiklRec.getInt(eArtikl.groups1_id));
        Record artgrp2Rec = eGroups.find(artiklRec.getInt(eArtikl.groups2_id));
        Record croscurs1Rec = eCurrenc.find(artiklRec.getInt(eArtikl.currenc1_id));
        Record croscurs2Rec = eCurrenc.find(artiklRec.getInt(eArtikl.currenc2_id));

        artiklK.v = artgrp1Rec.getDbl(eGroups.val, 1); //koef. ������� ������ ���.���������
        artiklS.v = artgrp2Rec.getDbl(eGroups.val, 0); //����. ������ ������ ���.���������
        artiklW.v = artiklRec.getDbl(eArtikl.otx_norm, 0); //����. ������ ������ ���.���������
        grpcursK1.v = croscurs1Rec.getDbl(eCurrenc.cross_cour); //���� ������ ��� �������� ��������        
        grpcursK2.v = croscurs2Rec.getDbl(eCurrenc.cross_cour); //���� ������ ��� �����. ������� (�����., �������, ��������.)            

        artdetT1.v = artdetRec[0].getDbl(eArtdet.cost_c1); //����� ������ ��������
        artdetT2.v = artdetRec[1].getDbl(eArtdet.cost_c2); //����� ����� ��������
        artdetT3.v = artdetRec[2].getDbl(eArtdet.cost_c3); //����� ����� ��������

        if (Type.ARCH == winc.root.type) { //�������� �� ���. ������� �����
            grpformN1.v = eGroups.find(2101).getDbl(eGroups.val);

        } else if (Type.RECTANGL != winc.root.type) {
            grpformN1.v = eGroups.find(2104).getDbl(eGroups.val);
        } else {
            grpformN1.v = 0;
        }
        grpcolorK1.v = groupsRec[0].getDbl(eGroups.val); //����. ������ �������         
        grpcolorK1.v = groupsRec[1].getDbl(eGroups.val); //����. ������ �������         
        grpcolorK1.v = groupsRec[2].getDbl(eGroups.val); //����. ������ �������         

        colorK1.v = colorRec[0].getDbl(eColor.coef1); //������� ����. ������. ��������
        colorK2.v = colorRec[1].getDbl(eColor.coef2); //������� ����. �����. ��������
        colorK3.v = colorRec[2].getDbl(eColor.coef3); //������� ����. �����. ��������   

        if (rulecalcRec != null) {
            Scale.rulecalcK.v = rulecalcRec.getDbl(eRulecalc.coeff);
            Scale.rulecalcN.v = rulecalcRec.getDbl(eRulecalc.incr);
        }
    }
}
