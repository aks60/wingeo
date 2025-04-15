package builder.making;

import dataset.Record;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eCurrenc;
import domain.eGroups;
import domain.eRulecalc;
import domain.eSystree;
import enums.Layout;
import enums.TypeForm;
import enums.UseUnit;
import builder.Wincalc;
import builder.model.Com5t;
import static builder.model.Com5t.MAXSIDE;
import builder.model.ElemSimple;
import common.UCom;
import domain.eElemdet;
import domain.eElement;
import enums.Scale;
import enums.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Расчёт стоимости элементов окна алгоритм см. в UML
 */
public class TTariffic extends Cal5e {

    private boolean norm_otx = true;

    public TTariffic(Wincalc winc, boolean norm_otx) {
        super(winc);
        this.norm_otx = norm_otx;
    }

    //Рассчёт конструкции с учётом 
    //всех скидок и наценок
    public void calculate() {
        try {
            double grpformN1 = percentMarkup(winc); //процентная надбавка на изделия сложной формы

            //Расчёт себес-сти и колич. материала
            //сделано для подготовки данных для правил расчёта
            for (ElemSimple elem5e : winc.listElem) {
                if (filterPhantom(elem5e)) {

                    //Record artgrp1Rec = eGroups.find(elem5e.spcRec.artiklRec.getInt(eArtikl.groups1_id));
                    //double artiklS = artgrp2bRec.getDbl(eGroups.val, 0);  //скидки группы мат.ценностей
                    elem5e.spcRec.quant1 = formatAmount(elem5e.spcRec); //количество без отхода  
                    elem5e.spcRec.quant2 = (norm_otx == true) ? elem5e.spcRec.quant1 + (elem5e.spcRec.quant1 * elem5e.spcRec.waste / 100) : elem5e.spcRec.quant1; //количество с отходом
                    elem5e.spcRec.costprice = artdetCostprice(elem5e.spcRec); //себест. по табл. ARTDET и прав.расч.

                    //Вложенная спецификация
                    //цикл по детализации эдемента
                    for (TRecord spcRec : elem5e.spcRec.spcList) {
                        spcRec.quant1 = formatAmount(spcRec); //количество без отхода
                        spcRec.quant2 = (norm_otx == true) ? spcRec.quant1 + (spcRec.quant1 * spcRec.waste / 100) : spcRec.quant1; //количество с отходом
                        spcRec.costprice = artdetCostprice(spcRec); //себест. по табл. ARTDET и прав.расч.
                    }
                }
            }

            //Расчёт с учётом наценок и скидок
            //цикл по эдементам конструкции
            for (ElemSimple elem5e : winc.listElem) {
                if (filterPhantom(elem5e)) {

                    //Цикл по правилам расчёта.                 
                    for (Record rulecalcRec : eRulecalc.filter()) {
                        rulecalcTarif(winc, rulecalcRec, elem5e.spcRec); //увеличение себестоимости/стоимости в coeff раз и на incr величину наценки
                    }

                    Record systreeRec = eSystree.find(winc.nuni);
                    {
                        Record artgrp1Rec = eGroups.find(elem5e.spcRec.artiklRec.getInt(eArtikl.groups1_id));
                        Record artgrp2Rec = eGroups.find(elem5e.spcRec.artiklRec.getInt(eArtikl.groups2_id));
                        double artiklK = artgrp1Rec.getDbl(eGroups.val, 1);  //наценка группы мат.ценностей
                        double artiklS = artgrp2Rec.getDbl(eGroups.val, 0);  //скидки группы мат.ценностей
                        double systreeK = systreeRec.getDbl(eSystree.coef, 1); //коэф. рентабельности

                        elem5e.spcRec.costprice = elem5e.spcRec.costprice - elem5e.spcRec.costprice * artiklS / 100; //себесстоимость со скидкой 
                        elem5e.spcRec.price += elem5e.spcRec.costprice; //цена за един.изм 
                        double value = elem5e.spcRec.price * artiklK * systreeK;
                        elem5e.spcRec.price = value + grpformN1 * value / 100; //цена за един.изм 
                        elem5e.spcRec.cost1 = elem5e.spcRec.price * elem5e.spcRec.quant2; //стоимость без скидки                     
                        elem5e.spcRec.cost2 = elem5e.spcRec.cost1; //стоимость со скидкой 
                        //System.out.println(elem5e.spcRec.cost1 + " -1- " + elem5e.spcRec.cost2);
                    }
                    //Цикл по детализации
                    for (TRecord spcRec : elem5e.spcRec.spcList) {

                        //Цикл по правилам расчёта.
                        for (Record rulecalcRec : eRulecalc.filter()) {
                            rulecalcTarif(winc, rulecalcRec, spcRec); //увеличение себестоимости/стоимости в coeff раз и на incr величину наценки
                        }

                        Record artgrp1Rec = eGroups.find(spcRec.artiklRec.getInt(eArtikl.groups1_id));
                        Record artgrp2Rec = eGroups.find(spcRec.artiklRec.getInt(eArtikl.groups2_id));
                        double artiklK = artgrp1Rec.getDbl(eGroups.val, 1);  //наценка группы мат.ценностей
                        double artiklS = artgrp2Rec.getDbl(eGroups.val, 0);  //скидки группы мат.ценностей
                        double systreeK = systreeRec.getDbl(eSystree.coef); //коэф. рентабельности

                        spcRec.costprice = spcRec.costprice - spcRec.costprice * artiklS / 100; //себесстоимость со скидкой 
                        spcRec.price += spcRec.costprice; //цена за един.изм                        
                        double value = spcRec.costprice * artiklK * systreeK;
                        spcRec.price = value + grpformN1 * value / 100; //цена за един.изм 
                        spcRec.cost1 = spcRec.price * spcRec.quant2; //стоимость без скидки                     
                        spcRec.cost2 = spcRec.cost1; //стоимость со скидкой  
                        //System.out.println(spcRec.cost1 + " -2- " + spcRec.cost2);
                    }
                }
            }

            //Расчёт веса элемента конструкции
            for (ElemSimple elem5e : winc.listElem) {
                if (filterPhantom(elem5e)) {
                    elem5e.spcRec.weight = elem5e.spcRec.quant1 * elem5e.spcRec.artiklRec.getDbl(eArtikl.density);

                    for (TRecord spec : elem5e.spcRec.spcList) {
                        spec.weight = spec.quant1 * spec.artiklRec.getDbl(eArtikl.density);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Tariffic.calculate() " + e);
        }
    }

    //Себес-сть/стоимость. Рассчёт тарифа для заданного артикула, заданных цветов по таблице eArtdet
    public static double artdetCostprice(TRecord spcRec) {

        double costpriceSum = 0; //себестоимость
        Record color1Rec = eColor.find(spcRec.colorID1);  //основная
        Record color2Rec = eColor.find(spcRec.colorID2);  //внутренняя
        Record color3Rec = eColor.find(spcRec.colorID3);  //внешняя
        Record grpcolor1Rec = eGroups.find(color1Rec.getInt(eColor.groups_id));
        Record grpcolor2Rec = eGroups.find(color2Rec.getInt(eColor.groups_id));
        Record grpcolor3Rec = eGroups.find(color3Rec.getInt(eColor.groups_id));

        double grpcursK1 = eCurrenc.find(spcRec.artiklRec.getInt(eArtikl.currenc1_id)).getDbl(eCurrenc.cross_cour); //кросс-курс валюты для основной текстуры
        double grpcursK2 = eCurrenc.find(spcRec.artiklRec.getInt(eArtikl.currenc2_id)).getDbl(eCurrenc.cross_cour); //кросс-курс валюты для неосновных текстур (внутренняя, внешняя, двухсторонняя)

        //Цикл по тарификационной таблице ARTDET мат. ценностей
        for (Record artdetRec : eArtdet.filter(spcRec.artiklRec.getInt(eArtikl.id))) {

            double costprice = 0, price = 0;
            boolean artdetUsed = false;

            //Если тариф двухсторонней текстуры не равен 0, и если текстура2
            //равна текстура3 и заданный тариф применим
            //
            //СЛОЖЕНИЕ ОСНОВНОЙ И ДВУХСТОРОННЕЙ
            if (artdetRec.getDbl(eArtdet.cost_c4) != 0
                    && spcRec.colorID2 == spcRec.colorID3 && isUseTarif(artdetRec, color2Rec)) {
                {
                    spcRec.artdetRec[1] = artdetRec;
                    spcRec.artdetRec[2] = artdetRec;
                    //double mmm = spcRec.artiklRec.getInt(eArtikl.;
                    double artdet2T = artdetRec.getDbl(eArtdet.cost_c4); //тариф двухсторонней текстуры
                    double colorK2 = color2Rec.getDbl(eColor.coef2); //коэф.внутр. текст.
                    double grpcolorK2 = grpcolor2Rec.getDbl(eGroups.val); //коэф. группы текстур                                       

                    costprice += (grpcolorK2 * colorK2 * artdet2T) / grpcursK2;
                }
                if (isUseTarif(artdetRec, color1Rec)) { //подбираем тариф основной текстуры
                    spcRec.artdetRec[0] = artdetRec;
                    double artdetT1 = artdetRec.getDbl(eArtdet.cost_c1); //тариф двухсторонней текстуры
                    double colorK1 = color1Rec.getDbl(eColor.coef2); //коэф.внутр. текст.
                    double grpcolorK1 = grpcolor1Rec.getDbl(eGroups.val); //коэф. группы текстур                                       

                    costprice += (grpcolorK1 * colorK1 * artdetT1) / grpcursK2;
                }
                artdetUsed = true;

                //СЛОЖЕНИЕ ТРЁХ ТЕКСТУР
            } else {
                //Подбираем тариф основной текстуры
                if (isUseTarif(artdetRec, color1Rec)) {
                    artdetUsed = true;
                    spcRec.artdetRec[0] = artdetRec;
                    double artdetT1 = artdetRec.getDbl(eArtdet.cost_c1); //тариф основной текстуры
                    double coef = grpcolor1Rec.getDbl(eGroups.val) * color1Rec.getDbl(eColor.coef1);

                    costprice += coef * artdetT1 / grpcursK1;
                }
                //Подбираем тариф внутренней текстуры
                if (isUseTarif(artdetRec, color2Rec)) {
                    artdetUsed = true;
                    spcRec.artdetRec[1] = artdetRec;
                    double artdetT2 = artdetRec.getDbl(eArtdet.cost_c2); //тариф внутренний текстуры
                    double coef = grpcolor2Rec.getDbl(eGroups.val) * color2Rec.getDbl(eColor.coef2);

                    costprice += coef * artdetT2 / grpcursK2;
                }
                //Подбираем тариф внешней текстуры
                if (isUseTarif(artdetRec, color3Rec)) {
                    artdetUsed = true;
                    spcRec.artdetRec[2] = artdetRec;
                    double artdetT3 = artdetRec.getDbl(eArtdet.cost_c3); //тариф внешний текстуры
                    double coef = grpcolor3Rec.getDbl(eGroups.val) * color3Rec.getDbl(eColor.coef3);

                    costprice += coef * artdetT3 / grpcursK2;
                }
            }
            //Проверка минимального тарифа (Непонятная проверка)
            {
                //?
            }
            //Вставки (проц. наценка)
            if (artdetUsed == true) { //если было попадание
                if ("ВСТ".equals(spcRec.place.substring(0, 3)) == true) {
                    if (spcRec.detailRec != null) {
                        Record elementRec = eElement.find(spcRec.detailRec.getInt(eElemdet.element_id));
                        if (elementRec.getDbl(eElement.markup) > 0) {
                            price += price * elementRec.getDbl(eElement.markup) / 100;
                        }
                    }
                }
                double coef = (Scale.nakl_cost == true) ? artdetRec.getDbl(eArtdet.coef) : 1; //kоэф. накл. расходов по табл. ARTDET 
                costpriceSum += costprice * coef;
            }
        }
        return costpriceSum;
    }

    //Себес-сть/стоимость по правилам расчёта
    public static void rulecalcTarif(Wincalc winc, Record rulecalcRec, TRecord spcRec) {

        try {
            Object artiklID = rulecalcRec.get(eRulecalc.artikl_id);
            //Фильтр артикул совпал
            if (spcRec.artiklRec.get(eArtikl.id) != null
                    && (spcRec.artiklRec.get(eArtikl.id).equals(artiklID) == true || artiklID == null)) {

                //Фильтр тип и подтип совпали
                if ((spcRec.artiklRec.getInt(eArtikl.level1) * 100 + spcRec.artiklRec.getInt(eArtikl.level2)) == rulecalcRec.getInt(eRulecalc.type)) {

                    //Фильтр коды текстур попали в диапазон
                    if (UCom.containsColor(rulecalcRec.getStr(eRulecalc.color1), spcRec.colorID1) == true
                            && UCom.containsColor(rulecalcRec.getStr(eRulecalc.color2), spcRec.colorID2) == true
                            && UCom.containsColor(rulecalcRec.getStr(eRulecalc.color3), spcRec.colorID3) == true) {

                        double quantity3 = spcRec.quant2; //по количеству в элементе

                        //Общее количество в проекте (по артикулу или подтипу, типу)
                        if (rulecalcRec.getInt(eRulecalc.common) == 1) {
                            quantity3 = 0;
                            //Фильтр по артикулу
                            if (rulecalcRec.get(eRulecalc.artikl_id) != null) {
                                for (ElemSimple elem5e : winc.listElem) { //суммирую колич. всех элементов (например штапиков)
                                    if (filterPhantom(elem5e)) {
                                        if (elem5e.spcRec.artikl.equals(spcRec.artikl)) { //фильтр по артикулу
                                            quantity3 += elem5e.spcRec.quant1;
                                        }
                                        for (TRecord specifRec2 : elem5e.spcRec.spcList) {
                                            if (specifRec2.artikl.equals(spcRec.artikl)) { //фильтр по артикулу
                                                quantity3 += specifRec2.quant1;
                                            }
                                        }
                                    }
                                }
                                //Фильтр по подтипу, типу
                            } else {
                                for (ElemSimple elem5e : winc.listElem) { //суммирую колич. всех элементов (например штапиков)
                                    if (filterPhantom(elem5e)) {
                                        TRecord specifRec2 = elem5e.spcRec;
                                        if (specifRec2.artiklRec.getInt(eArtikl.level1) * 100 + specifRec2.artiklRec.getInt(eArtikl.level2) == rulecalcRec.getInt(eRulecalc.type)) {
                                            quantity3 += elem5e.spcRec.quant1;
                                        }
                                        for (TRecord specifRec3 : specifRec2.spcList) {
                                            if (specifRec3.artiklRec.getInt(eArtikl.level1) * 100 + specifRec3.artiklRec.getInt(eArtikl.level2) == rulecalcRec.getInt(eRulecalc.type)) {
                                                quantity3 += specifRec3.quant1;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //Фильтр по количеству элемента
                        if (UCom.containsNumbJust(rulecalcRec.getStr(eRulecalc.quant), quantity3) == true) {
                            //Правило по форме позиции
                            int typeformID1 = TypeForm.typeform(spcRec.elem5e);
                            int typeformID2 = (rulecalcRec.getInt(eRulecalc.form) == 0) ? 1 : rulecalcRec.getInt(eRulecalc.form);
                            if (typeformID1 == typeformID2) {
                                //По себестоимости или стоимости
                                if (rulecalcRec.getInt(eRulecalc.sebes) == 1) {
                                    spcRec.costprice = spcRec.costprice * rulecalcRec.getDbl(eRulecalc.coeff) + rulecalcRec.getDbl(eRulecalc.suppl);  //увеличение себестоимости в coeff раз и на incr величину надбавки
                                } else {
                                    spcRec.price = spcRec.price * rulecalcRec.getDbl(eRulecalc.coeff) + rulecalcRec.getDbl(eRulecalc.suppl);  //увеличение стоимости в coeff раз и на incr величину надбавки                                   
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Tariffic.rulecalcTarif() " + e);
        }
    }

    //Процентная надбавка на изделия сложной формы
    public static double percentMarkup(Wincalc winc) {
        if (Type.ARCH == winc.root.type) {
            return eGroups.find(2101).getDbl(eGroups.val);

        } else if (Type.RECTANGL != winc.root.type) {
            return eGroups.find(2104).getDbl(eGroups.val);
        }
        return 0;
    }

    //В зав. от единицы изм. форматируется количество
    public static double formatAmount(TRecord spcRec) {
        //Нужна доработка для расчёта по минимальному тарифу. См. dll VirtualPro4::CalcArtTariff

        if (UseUnit.METR.id == spcRec.artiklRec.getInt(eArtikl.unit)) { //метры
            return spcRec.count * precision(spcRec.width) / 1000;

        } else if (UseUnit.METR2.id == spcRec.artiklRec.getInt(eArtikl.unit)) { //кв. метры
            return spcRec.count * precision(spcRec.width) * precision(spcRec.height) / 1000000;

        } else if (UseUnit.PIE.id == spcRec.artiklRec.getInt(eArtikl.unit)) { //шт.
            return spcRec.count;

        } else if (UseUnit.KIT.id == spcRec.artiklRec.getInt(eArtikl.unit)) { //комп.
            return spcRec.count;

        } else if (UseUnit.ML.id == spcRec.artiklRec.getInt(eArtikl.unit)) { //мл
            return spcRec.count;
        }
        return 0;
    }

    //Проверяет, должен ли применяться заданный тариф мат-ценности для заданной текстуры
    public static boolean isUseTarif(Record artdetRec, Record colorRec) {

        if (artdetRec.getInt(eArtdet.color_fk) < 0) { //этот тариф задан для группы текстур
            if (colorRec.getInt(eColor.groups_id) == artdetRec.getInt(eArtdet.color_fk)) {
                return true; //текстура принадлежит группе
            }
        } else if (colorRec.getInt(eColor.id) == artdetRec.getInt(eArtdet.color_fk)) {
            return true; //текстуры совпали
        }
        return false;
    }

    private static double precision(double value) {
        if (Scale.precision == 0) {
            return value;
        }
        int places = (Scale.precision == 3) ? 1 : (Scale.precision == 2) ? 2 : 3;
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //Фильтр на фантомы, грязные фичи...
    private static boolean filterPhantom(ElemSimple elem5e) {
        if (elem5e.artiklRec == null) {
            return false;
        } else if (elem5e.artiklRec.getInt(eArtikl.id) == -3) {
            return false;
        } else if (elem5e.spcRec.artiklRec == null) {
            return false;
        } else if (elem5e.spcRec.artiklRec.getInt(eArtikl.id) == -3) {
            return false;
        }
        return true;
    }
}
