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

    private double price1 = 0; //стоимость без скидки
    private double price2 = 0; //стоимость с технологической скидкой    
    
    /**
     * Комплекты конструкции. Комплекты могут быть не привязаны к изделиям
     */
    public static ArrayList<TRecord> kits(dataset.Record prjprodRec, Wincalc winc, boolean norm_otx) {
        ArrayList<TRecord> kitList = new ArrayList();
        try {
            dataset.Record systreeRec = eSystree.find(winc.nuni); //для нахожд. коэф. рентабельности
            double percentMarkup = percentMarkup(winc); //процентная надбавка на изделия сложной формы
            if (prjprodRec != null) {
                List<dataset.Record> prjkitList = ePrjkit.filter3(prjprodRec.getInt(ePrjprod.id));

                //Цикл по комплектам
                for (dataset.Record prjkitRec : prjkitList) {
                    dataset.Record artiklRec = eArtikl.find(prjkitRec.getInt(ePrjkit.artikl_id), true);
                    if (artiklRec != null) {
                        TRecord spc = new TRecord("КОМП", ++winc.spcId, prjkitRec, artiklRec, null);
                        spc.width = prjkitRec.getDbl(ePrjkit.width);
                        spc.height = prjkitRec.getDbl(ePrjkit.height);
                        spc.count = prjkitRec.getDbl(ePrjkit.numb);
                        spc.colorID1 = prjkitRec.getInt(ePrjkit.color1_id);
                        spc.colorID2 = prjkitRec.getInt(ePrjkit.color2_id);
                        spc.colorID3 = prjkitRec.getInt(ePrjkit.color3_id);
                        spc.anglCut0 = prjkitRec.getDbl(ePrjkit.angl1);
                        spc.anglCut1 = prjkitRec.getDbl(ePrjkit.angl2);
                        spc.quant1 = TTariffic.formatAmount(spc); //количество без отхода
                        spc.quant2 = (norm_otx == true) ? spc.quant1 + (spc.quant1 * spc.waste / 100) : spc.quant1; //количество с отходом
                        spc.sebes1 = TTariffic.artdetPrice(spc); //себест. за ед. без отхода по табл. ARTDET с коэф. и надб.
                        spc.sebes2 = spc.sebes1 + (spc.sebes1 * (spc.quant2 - spc.quant1)); //себест. за ед. с отходом 
                        dataset.Record artgrp1Rec = eGroups.find(spc.artiklRec().getInt(eArtikl.groups1_id));
                        dataset.Record artgrp2Rec = eGroups.find(spc.artiklRec().getInt(eArtikl.groups2_id));
                        double k1 = artgrp1Rec.getDbl(eGroups.val, 1);  //(koef)наценка группы мат.ценностей
                        double k2 = artgrp2Rec.getDbl(eGroups.val, 0);  //(%)скидки группы мат.ценностей
                        double k3 = systreeRec.getDbl(eSystree.coef, 1); //коэф. рентабельности
                        double price = spc.count * spc.quant2 * spc.sebes2 * k1 * k3;
                        spc.price1 = price; //стоимость без скидки                     
                        spc.price2 = price - price / 100 * k2; //стоимость со скидкой 
                        //winc.price1k += spc.price1;
                        //winc.price2k += spc.price2;
                        kitList.add(spc);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Tariffic.kits() " + e);
        }
        return kitList;
    }  

    //Процентная надбавка на изделия сложной формы
    private static double percentMarkup(Wincalc winc) {
        if (Type.ARCH == winc.root.type) {
            return eGroups.find(2101).getDbl(eGroups.val);

        } else if (Type.RECTANGL != winc.root.type) {
            return eGroups.find(2104).getDbl(eGroups.val);
        }
        return 0;
    }
    
}
