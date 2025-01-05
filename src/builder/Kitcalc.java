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

    public static ArrayList<TRecord> kitList = new ArrayList<TRecord>(); //список комплекта
    public static double cost1 = 0; //стоимость без скидки
    public static double cost2 = 0; //стоимость с технологической и скидкой менеджера  

    public static void init() {
        kitList.clear();
        cost1 = 0;
        cost2 = 0;
    }

    //Комплекты конструкции
    public static ArrayList<TRecord> tarifficFree(Wincalc win, Record projectdRec, double discKit, boolean normOtx, boolean numProd) {
        try {
            if (projectdRec != null) {
                List<Record> prjkitList = ePrjkit.filter5(projectdRec.getInt(eProject.id));
                return calculate(win, prjkitList, discKit, normOtx, numProd);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Kitscalc.tarifficFree() " + e);
        }
        return null;
    }
    
    //Комплекты конструкции
    public static ArrayList<TRecord> tarifficProd(Wincalc win, Record prjprodRec, double discKit, boolean normOtx, boolean numProd) {
        try {
            if (prjprodRec != null) {
                List<dataset.Record> kitList = ePrjkit.filter3(prjprodRec.getInt(ePrjprod.id));
                return calculate(win, kitList, discKit, normOtx, numProd);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Kitscalc.specificProd() " + e);
        }
        return null;
    }

    //Комплекты заказа (комплекты в изделиях и комп. в проекте)
    public static ArrayList<TRecord> tarifficProj(Wincalc win, Record projectdRec, double discKit, boolean normOtx, boolean numProd) {
        try {
            if (projectdRec != null) {
                List<Record> prjkitList = ePrjkit.filter4(projectdRec.getInt(eProject.id));
                return calculate(win, prjkitList, discKit, normOtx, numProd);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Kitscalc.specificProj() " + e);
        }
        return null;
    }

    //Список комплектов (коэф.рентабельности и сложн.форм не учитываются)
    private static ArrayList<TRecord> calculate(Wincalc winc, List<Record> listKit, double discKit, boolean normOtx, boolean isNumProd) {
        init();
        //Цикл по комплектам
        for (Record prjkitRec : listKit) {
            int numProd = 1;
            if (isNumProd == true) {
                Record prjprodRec = ePrjprod.find(prjkitRec.getInt(ePrjkit.prjprod_id));
                numProd = (prjprodRec != null) ? prjprodRec.getInt(ePrjprod.num) : 1;
            }
            Record artiklRec = eArtikl.find(prjkitRec.getInt(ePrjkit.artikl_id), true);

            if (artiklRec != null) {

                //СПЕЦИФИКАЦИЯ
                TRecord spcRec = new TRecord("КОМП", ++winc.nppID, prjkitRec, artiklRec, null);
                spcRec.width = prjkitRec.getDbl(ePrjkit.width);
                spcRec.height = prjkitRec.getDbl(ePrjkit.height);
                spcRec.count = prjkitRec.getDbl(ePrjkit.numb) * numProd;
                spcRec.colorID1 = prjkitRec.getInt(ePrjkit.color1_id);
                spcRec.colorID2 = prjkitRec.getInt(ePrjkit.color2_id);
                spcRec.colorID3 = prjkitRec.getInt(ePrjkit.color3_id);
                spcRec.anglCut0 = prjkitRec.getDbl(ePrjkit.angl1);
                spcRec.anglCut1 = prjkitRec.getDbl(ePrjkit.angl2);

                //ТАРИФИКАЦИЯ 
                Record artgrp1bRec = eGroups.find(spcRec.artiklRec.getInt(eArtikl.groups1_id));
                Record artgrp2bRec = eGroups.find(spcRec.artiklRec.getInt(eArtikl.groups2_id));
                double artiklK = artgrp1bRec.getDbl(eGroups.val, 1);  //наценка группы мат.ценностей
                double artiklS = artgrp2bRec.getDbl(eGroups.val, 0);  //скидки группы мат.ценностей               
                spcRec.quant1 = TTariffic.formatAmount(spcRec); //количество без отхода  
                spcRec.quant2 = (normOtx == true) ? spcRec.quant1 + (spcRec.quant1 * spcRec.waste / 100) : spcRec.quant1; //количество с отходом
                spcRec.costprice = TTariffic.artdetCostprice(spcRec); //себест. по табл. ARTDET и прав.расч.
                spcRec.costprice = spcRec.costprice - spcRec.costprice * artiklS / 100; //себесстоимость со скидкой 
                spcRec.price = spcRec.costprice;
                spcRec.price = spcRec.price * artiklK; //цена за един.изм 
                spcRec.cost1 = spcRec.price * spcRec.quant2; //стоимость без скидки                     
                spcRec.cost2 = spcRec.cost1 - discKit * spcRec.cost1 / 100; //стоимость со скид.менеджера
                cost1 += spcRec.cost1;
                cost2 += spcRec.cost2;
                kitList.add(spcRec);
            }
        }
        return kitList;
    }
}
