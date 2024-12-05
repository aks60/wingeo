package enums;

import dataset.Record;
import builder.Wincalc;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eCurrenc;
import domain.eGroups;

public enum �oeff {
    rentabK, //SYSTREE ����������� ��������������

    grpcolK, //GROUPS k���. ������ ��������
    croscursK, //GROUPS ����� ���� 
    compformN, //GROUPS �������� �� ������� ������� �����

    indetT, //ARTDET ����� �����.. ��������
    twodetT, //ARTDET ����� ������. ��������
    artdetK, //ARTDET k���. ��������

    osncolK, //COLOR ����. �������� ��������
    incolK, //COLOR ����. ���������� ��������
    outcolK, //COLOR ����. ������� ��������

    rulecalcK, //RULECALC ����. ������ �������
    rulecalcN; //RULECALC ����. ������ �������

    private double v = 0;

    �oeff() {

        this.v = 0;
    }

    public double v() {
        return this.v;
    }

    public static void init(Wincalc winc, Record color2Rec, Record artiklRec, Record artdetRec, Record colgrpRec) {
        if (Type.ARCH == winc.root.type) {
            compformN.v = eGroups.find(2101).getDbl(eGroups.val);

        } else if (Type.RECTANGL != winc.root.type) {
            compformN.v = eGroups.find(2104).getDbl(eGroups.val);
        } else {
            compformN.v = 0;
        }

        indetT.v = artdetRec.getDbl(eArtdet.cost_c2); //����� ���������� ��������
        outcolK.v = color2Rec.getDbl(eColor.coef2); //������� ����.���������� ��������
        grpcolK.v = colgrpRec.getDbl(eGroups.val); //����. ������ �������

        dataset.Record kursBaseRec = eCurrenc.find(artiklRec.getInt(eArtikl.currenc1_id));    // �����-���� ������ ��� �������� ��������
        dataset.Record kursNoBaseRec = eCurrenc.find(artiklRec.getInt(eArtikl.currenc2_id));  // �����-���� ������ ��� ���������� ������� (����������, �������, �������������)

        croscursK.v = kursNoBaseRec.getDbl(eCurrenc.cross_cour); //����� ����        
    }
}
