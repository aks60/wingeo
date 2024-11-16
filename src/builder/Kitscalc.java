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

public class Kitscalc {

    private double price1 = 0; //��������� ��� ������
    private double price2 = 0; //��������� � ��������������� �������    
    
    /**
     * ��������� �����������. ��������� ����� ���� �� ��������� � ��������
     */
    public static ArrayList<TRecord> kits(dataset.Record prjprodRec, Wincalc winc, boolean norm_otx) {
        ArrayList<TRecord> kitList = new ArrayList();
        try {
            dataset.Record systreeRec = eSystree.find(winc.nuni); //��� ������. ����. ��������������
            double percentMarkup = percentMarkup(winc); //���������� �������� �� ������� ������� �����
            if (prjprodRec != null) {
                List<dataset.Record> prjkitList = ePrjkit.filter3(prjprodRec.getInt(ePrjprod.id));

                //���� �� ����������
                for (dataset.Record prjkitRec : prjkitList) {
                    dataset.Record artiklRec = eArtikl.find(prjkitRec.getInt(ePrjkit.artikl_id), true);
                    if (artiklRec != null) {
                        TRecord spc = new TRecord("����", ++winc.spcId, prjkitRec, artiklRec, null);
                        spc.width = prjkitRec.getDbl(ePrjkit.width);
                        spc.height = prjkitRec.getDbl(ePrjkit.height);
                        spc.count = prjkitRec.getDbl(ePrjkit.numb);
                        spc.colorID1 = prjkitRec.getInt(ePrjkit.color1_id);
                        spc.colorID2 = prjkitRec.getInt(ePrjkit.color2_id);
                        spc.colorID3 = prjkitRec.getInt(ePrjkit.color3_id);
                        spc.anglCut0 = prjkitRec.getDbl(ePrjkit.angl1);
                        spc.anglCut1 = prjkitRec.getDbl(ePrjkit.angl2);
                        spc.quant1 = TTariffic.formatAmount(spc); //���������� ��� ������
                        spc.quant2 = (norm_otx == true) ? spc.quant1 + (spc.quant1 * spc.waste / 100) : spc.quant1; //���������� � �������
                        spc.sebes1 = TTariffic.artdetPrice(spc); //������. �� ��. ��� ������ �� ����. ARTDET � ����. � ����.
                        spc.sebes2 = spc.sebes1 + (spc.sebes1 * (spc.quant2 - spc.quant1)); //������. �� ��. � ������� 
                        dataset.Record artgrp1Rec = eGroups.find(spc.artiklRec().getInt(eArtikl.groups1_id));
                        dataset.Record artgrp2Rec = eGroups.find(spc.artiklRec().getInt(eArtikl.groups2_id));
                        double k1 = artgrp1Rec.getDbl(eGroups.val, 1);  //(koef)������� ������ ���.���������
                        double k2 = artgrp2Rec.getDbl(eGroups.val, 0);  //(%)������ ������ ���.���������
                        double k3 = systreeRec.getDbl(eSystree.coef, 1); //����. ��������������
                        double price = spc.count * spc.quant2 * spc.sebes2 * k1 * k3;
                        spc.price1 = price; //��������� ��� ������                     
                        spc.price2 = price - price / 100 * k2; //��������� �� ������� 
                        //winc.price1k += spc.price1;
                        //winc.price2k += spc.price2;
                        kitList.add(spc);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("������:Tariffic.kits() " + e);
        }
        return kitList;
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
    
}
