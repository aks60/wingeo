package builder;

import builder.making.TRecord;
import builder.making.TTariffic;
import domain.eArtikl;
import domain.eGroups;
import domain.ePrjkit;
import domain.ePrjprod;
import java.util.ArrayList;
import java.util.List;
import dataset.Record;
import domain.eProject;

public class Kitcalc {

    private static double price1 = 0; //��������� ��� ������
    private static double price2 = 0; //��������� � ��������������� �������    

    public static void init() {
        price1 = 0;
        price2 = 0;         
    }

    //��������� �����������
    public static ArrayList<TRecord> tarifficProd(Record prjprodRec, Wincalc winc, boolean norm_otx) {
        try {
            if (prjprodRec != null) {
                List<dataset.Record> kitList = ePrjkit.filter3(prjprodRec.getInt(ePrjprod.id));
                return calc(kitList, winc, norm_otx);
            }
        } catch (Exception e) {
            System.err.println("������:Kitscalc.specificProd() " + e);
        }
        return null;
    }

    //��������� ������
    public static ArrayList<TRecord> tarifficProj(Record projectdRec, Wincalc winc, boolean norm_otx) {
        try {            
            if (projectdRec != null) {
                List<Record> kitList = ePrjkit.filter4(projectdRec.getInt(eProject.id));
                return calc(kitList, winc, norm_otx);
            }
        } catch (Exception e) {
            System.err.println("������:Kitscalc.specificProj() " + e);
        }
        return null;
    }

    //������ ���������� (����.�������������� �� �����������)
    private static ArrayList<TRecord> calc(List<Record> kitList, Wincalc winc, boolean norm_otx) {
        ArrayList<TRecord> outList = new ArrayList();
        init();
        //���� �� ����������
        for (Record prjkitRec : kitList) {
            Record artiklRec = eArtikl.find(prjkitRec.getInt(ePrjkit.artikl_id), true);
            if (artiklRec != null) {
                
                //������������
                TRecord rec = new TRecord("����", ++winc.nppID, prjkitRec, artiklRec, null);
                rec.width = prjkitRec.getDbl(ePrjkit.width);
                rec.height = prjkitRec.getDbl(ePrjkit.height);
                rec.count = prjkitRec.getDbl(ePrjkit.numb);
                rec.colorID1 = prjkitRec.getInt(ePrjkit.color1_id);
                rec.colorID2 = prjkitRec.getInt(ePrjkit.color2_id);
                rec.colorID3 = prjkitRec.getInt(ePrjkit.color3_id);
                rec.anglCut0 = prjkitRec.getDbl(ePrjkit.angl1);
                rec.anglCut1 = prjkitRec.getDbl(ePrjkit.angl2);
                rec.quant1 = TTariffic.formatAmount(rec); //���������� ��� ������
                
                //�����������
                rec.quant2 = (norm_otx == true) ? rec.quant1 + (rec.quant1 * rec.waste / 100) : rec.quant1; //���������� � �������
                rec.sebes1 = TTariffic.artdetPrice(rec); //������. �� ��. ��� ������ �� ����. ARTDET � ����. � ����.
                rec.sebes2 = rec.sebes1 + (rec.sebes1 * (rec.quant2 - rec.quant1)); //������. �� ��. � ������� 
                Record artgrp1Rec = eGroups.find(rec.artiklRec().getInt(eArtikl.groups1_id));
                Record artgrp2Rec = eGroups.find(rec.artiklRec().getInt(eArtikl.groups2_id));
                double k1 = artgrp1Rec.getDbl(eGroups.val, 1);  //(koef)������� ������ ���.���������
                double k2 = artgrp2Rec.getDbl(eGroups.val, 0);  //(%)������ ������ ���.���������
                double price = rec.quant2 * rec.sebes2 * k1;
                rec.price1 = price; //��������� ��� ������                     
                rec.price2 = price - price / 100 * k2; //��������� �� ������� 
                price1 += rec.price1;
                price2 += rec.price2;
                outList.add(rec);
            }
        }
        return outList;
    }

    public static double price(int index) {
        return (index == 1) ? price1 : price2;
    }

}
