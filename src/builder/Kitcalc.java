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
import domain.eSystree;
import enums.Scale;

public class Kitcalc {

    private static ArrayList<TRecord> kitList = new ArrayList<TRecord>(); //������ ���������
    private static double price1 = 0; //��������� ��� ������
    private static double price2 = 0; //��������� � ��������������� �������    

    public static void init() {
        kitList.clear();
        price1 = 0;
        price2 = 0;
    }

    //��������� �����������
    public static ArrayList<TRecord> tarifficProd(Record prjprodRec, Wincalc win, boolean norm_otx) {
        try {
            if (prjprodRec != null) {
                List<dataset.Record> kitList = ePrjkit.filter3(prjprodRec.getInt(ePrjprod.id));
                return calculate(kitList, win, norm_otx);
            }
        } catch (Exception e) {
            System.err.println("������:Kitscalc.specificProd() " + e);
        }
        return null;
    }

    //��������� ������
    public static ArrayList<TRecord> tarifficProj(Record projectdRec, Wincalc win, boolean norm_otx) {
        try {
            if (projectdRec != null) {
                List<Record> kitList = ePrjkit.filter4(projectdRec.getInt(eProject.id));
                return calculate(kitList, win, norm_otx);
            }
        } catch (Exception e) {
            System.err.println("������:Kitscalc.specificProj() " + e);
        }
        return null;
    }

    //������ ���������� (����.�������������� �� �����������)
    private static ArrayList<TRecord> calculate(List<Record> kit_list, Wincalc win, boolean norm_otx) {
        init();
        //���� �� ����������
        for (Record prjkitRec : kit_list) {
            Record artiklRec = eArtikl.find(prjkitRec.getInt(ePrjkit.artikl_id), true);
            if (artiklRec != null) {

                //������������
                TRecord rec = new TRecord("����", ++win.nppID, prjkitRec, artiklRec, null);
                rec.width = prjkitRec.getDbl(ePrjkit.width);
                rec.height = prjkitRec.getDbl(ePrjkit.height);
                rec.count = prjkitRec.getDbl(ePrjkit.numb);
                rec.colorID1 = prjkitRec.getInt(ePrjkit.color1_id);
                rec.colorID2 = prjkitRec.getInt(ePrjkit.color2_id);
                rec.colorID3 = prjkitRec.getInt(ePrjkit.color3_id);
                rec.anglCut0 = prjkitRec.getDbl(ePrjkit.angl1);
                rec.anglCut1 = prjkitRec.getDbl(ePrjkit.angl2);

                //�����������    
                if (win.listElem.isEmpty() == false) {
                    Record systreeRec = eSystree.find(win.nuni);
                    Scale.systreeK.v = systreeRec.getDbl(eSystree.coef); //����. ��������������
                    Scale.grpformN1.v = TTariffic.percentMarkup(win); //���������� �������� �� ������� ������� �����
                }

                Record artgrp1bRec = eGroups.find(rec.artiklRec().getInt(eArtikl.groups1_id));
                Record artgrp2bRec = eGroups.find(rec.artiklRec().getInt(eArtikl.groups2_id));
                Scale.artiklK.v = artgrp1bRec.getDbl(eGroups.val, 1);  //������� ������ ���.���������
                Scale.artiklS.v = artgrp2bRec.getDbl(eGroups.val, 0);  //������ ������ ���.���������               

                rec.quant1 = TTariffic.formatAmount(rec); //���������� ��� ������  
                rec.quant2 = (norm_otx == true) ? rec.quant1 + (rec.quant1 * rec.waste / 100) : rec.quant1; //���������� � �������
                rec.sebes1 += TTariffic.artdetPrice(rec); //������. �� ����. ARTDET � ����.����.

                double sbs = rec.sebes1 * Scale.artiklK.v * Scale.systreeK.v;
                rec.sebes2 = sbs + Scale.grpformN1.v * sbs / 100; //��������� �� ����.��� 
                rec.price1 = rec.sebes2 * rec.quant2; //��������� ��� ������                     
                rec.price2 = rec.price1 - Scale.artiklS.v * rec.price1 / 100; //��������� �� ������� 
                kitList.add(rec);
            }
        }
        return kitList;
    }

    private static ArrayList<TRecord> calculate2(List<Record> kit_list, Wincalc npp, boolean norm_otx) {
        init();
        //���� �� ����������
        for (Record prjkitRec : kit_list) {
            Record artiklRec = eArtikl.find(prjkitRec.getInt(ePrjkit.artikl_id), true);
            if (artiklRec != null) {

                //������������
                TRecord rec = new TRecord("����", ++npp.nppID, prjkitRec, artiklRec, null);
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
                rec.sebes2 = rec.sebes1 + (rec.waste * rec.sebes1 / 100); //������. �� ��. � ������� 
                Record artgrp1Rec = eGroups.find(rec.artiklRec().getInt(eArtikl.groups1_id));
                Record artgrp2Rec = eGroups.find(rec.artiklRec().getInt(eArtikl.groups2_id));
                Scale.artiklK.v = artgrp1Rec.getDbl(eGroups.val, 1);  //koef. ������� ������ ���.���������
                Scale.artiklS.v = artgrp2Rec.getDbl(eGroups.val, 0);  //����. ������ ������ ���.���������
                rec.price1 = rec.quant2 * rec.sebes2 * Scale.artiklK.v; //��������� ��� ������                     
                rec.price2 = rec.price1 - Scale.artiklS.v * rec.price1 / 100; //��������� �� ������� 
                price1 += rec.price1;
                price2 += rec.price2;
                kitList.add(rec);
            }
        }
        return kitList;
    }

    public static ArrayList<TRecord> kits() {
        return kitList;
    }

    public static double price(int index) {
        return (index == 1) ? price1 : price2;
    }

}
