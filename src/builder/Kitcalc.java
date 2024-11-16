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

    private static double price1 = 0; //стоимость без скидки
    private static double price2 = 0; //стоимость с технологической скидкой    

    //Комплекты конструкции
    public static ArrayList<TRecord> specificProd(Record prjprodRec, Wincalc winc, boolean norm_otx) {
        try {
            if (prjprodRec != null) {
                List<dataset.Record> prjkitList = ePrjkit.filter3(prjprodRec.getInt(ePrjprod.id));
                return kits(prjkitList, winc, norm_otx);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Kitscalc.specificProd() " + e);
        }
        return null;
    }

    //Комплекты заказа
    public static ArrayList<TRecord> specificProj(Record projectdRec, Wincalc winc, boolean norm_otx) {
        try {
            if (projectdRec != null) {
                List<dataset.Record> prjkitList = ePrjkit.filter4(projectdRec.getInt(eProject.id));
                return kits(prjkitList, winc, norm_otx);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Kitscalc.specificProj() " + e);
        }
        return null;
    }

    //Список комплектов (коэф.рентабельности не учитывается)
    public static ArrayList<TRecord> kits(List<dataset.Record> prjkitList, Wincalc winc, boolean norm_otx) {
        price1 = 0; //стоимость без скидки
        price2 = 0; //стоимость с технологической скидкой         
        ArrayList<TRecord> kitList = new ArrayList();

        //Цикл по комплектам
        for (dataset.Record prjkitRec : prjkitList) {
            dataset.Record artiklRec = eArtikl.find(prjkitRec.getInt(ePrjkit.artikl_id), true);
            if (artiklRec != null) {
                TRecord rec = new TRecord("КОМП", ++winc.nppID, prjkitRec, artiklRec, null);
                rec.width = prjkitRec.getDbl(ePrjkit.width);
                rec.height = prjkitRec.getDbl(ePrjkit.height);
                rec.count = prjkitRec.getDbl(ePrjkit.numb);
                rec.colorID1 = prjkitRec.getInt(ePrjkit.color1_id);
                rec.colorID2 = prjkitRec.getInt(ePrjkit.color2_id);
                rec.colorID3 = prjkitRec.getInt(ePrjkit.color3_id);
                rec.anglCut0 = prjkitRec.getDbl(ePrjkit.angl1);
                rec.anglCut1 = prjkitRec.getDbl(ePrjkit.angl2);
                rec.quant1 = TTariffic.formatAmount(rec); //количество без отхода
                rec.quant2 = (norm_otx == true) ? rec.quant1 + (rec.quant1 * rec.waste / 100) : rec.quant1; //количество с отходом
                rec.sebes1 = TTariffic.artdetPrice(rec); //себест. за ед. без отхода по табл. ARTDET с коэф. и надб.
                rec.sebes2 = rec.sebes1 + (rec.sebes1 * (rec.quant2 - rec.quant1)); //себест. за ед. с отходом 
                dataset.Record artgrp1Rec = eGroups.find(rec.artiklRec().getInt(eArtikl.groups1_id));
                dataset.Record artgrp2Rec = eGroups.find(rec.artiklRec().getInt(eArtikl.groups2_id));
                double k1 = artgrp1Rec.getDbl(eGroups.val, 1);  //(koef)наценка группы мат.ценностей
                double k2 = artgrp2Rec.getDbl(eGroups.val, 0);  //(%)скидки группы мат.ценностей
                double price = rec.count * rec.quant2 * rec.sebes2 * k1;
                rec.price1 = price; //стоимость без скидки                     
                rec.price2 = price - price / 100 * k2; //стоимость со скидкой 
                price1 += rec.price1;
                price2 += rec.price2;
                kitList.add(rec);
            }
        }
        return kitList;
    }

    public static double price(int index) {
        return (index == 1) ? price1 : price2;
    }

}
