package builder;

import builder.making.TRecord;
import builder.making.TTariffic;
import domain.eArtikl;
import domain.eGroups;
import domain.ePrjkit;
import domain.ePrjprod;
import domain.eSystree;
import enums.Type;
import java.util.ArrayList;
import java.util.List;

public class Kitcalc {

    private static double price1 = 0; //��������� ��� ������
    private static double price2 = 0; //��������� � ��������������� �������    
    
    /**
     * ��������� �����������. ��������� ����� ���� �� ��������� � ��������
     */
    public static ArrayList<TRecord> specific(dataset.Record prjprodRec, Wincalc winc, boolean norm_otx) {
        ArrayList<TRecord> kitList = new ArrayList();
        try {
            dataset.Record systreeRec = eSystree.find(winc.nuni); //��� ������. ����. ��������������
            double percentMarkup = TTariffic.percentMarkup(winc); //���������� �������� �� ������� ������� �����
            if (prjprodRec != null) {
                List<dataset.Record> prjkitList = ePrjkit.filter3(prjprodRec.getInt(ePrjprod.id));

                //���� �� ����������
                for (dataset.Record prjkitRec : prjkitList) {
                    dataset.Record artiklRec = eArtikl.find(prjkitRec.getInt(ePrjkit.artikl_id), true);
                    if (artiklRec != null) {
                        TRecord rec = new TRecord("����", ++winc.spcId, prjkitRec, artiklRec, null);
                        rec.width = prjkitRec.getDbl(ePrjkit.width);
                        rec.height = prjkitRec.getDbl(ePrjkit.height);
                        rec.count = prjkitRec.getDbl(ePrjkit.numb);
                        rec.colorID1 = prjkitRec.getInt(ePrjkit.color1_id);
                        rec.colorID2 = prjkitRec.getInt(ePrjkit.color2_id);
                        rec.colorID3 = prjkitRec.getInt(ePrjkit.color3_id);
                        rec.anglCut0 = prjkitRec.getDbl(ePrjkit.angl1);
                        rec.anglCut1 = prjkitRec.getDbl(ePrjkit.angl2);
                        rec.quant1 = TTariffic.formatAmount(rec); //���������� ��� ������
                        rec.quant2 = (norm_otx == true) ? rec.quant1 + (rec.quant1 * rec.waste / 100) : rec.quant1; //���������� � �������
                        rec.sebes1 = TTariffic.artdetPrice(rec); //������. �� ��. ��� ������ �� ����. ARTDET � ����. � ����.
                        rec.sebes2 = rec.sebes1 + (rec.sebes1 * (rec.quant2 - rec.quant1)); //������. �� ��. � ������� 
                        dataset.Record artgrp1Rec = eGroups.find(rec.artiklRec().getInt(eArtikl.groups1_id));
                        dataset.Record artgrp2Rec = eGroups.find(rec.artiklRec().getInt(eArtikl.groups2_id));
                        double k1 = artgrp1Rec.getDbl(eGroups.val, 1);  //(koef)������� ������ ���.���������
                        double k2 = artgrp2Rec.getDbl(eGroups.val, 0);  //(%)������ ������ ���.���������
                        double k3 = systreeRec.getDbl(eSystree.coef, 1); //����. ��������������
                        double price = rec.count * rec.quant2 * rec.sebes2 * k1 * k3;
                        rec.price1 = price; //��������� ��� ������                     
                        rec.price2 = price - price / 100 * k2; //��������� �� ������� 
                        price1 += rec.price1;
                        price2 += rec.price2;
                        kitList.add(rec);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("������:Kitscalc.specific() " + e);
        }
        return kitList;
    }     
}
