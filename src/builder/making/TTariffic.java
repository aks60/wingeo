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
import builder.model.ElemSimple;
import common.UCom;
import dataset.Query;
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

    private boolean norm_cost = true;

    public TTariffic(Wincalc winc, boolean norm_cost) {
        super(winc);
        this.norm_cost = norm_cost;
    }

    //Рассчёт конструкции с учётом 
    //всех скидок и наценок
    public void calculate() {
        try {
            Scale.grpformN1.v = percentMarkup(winc); //процентная надбавка на изделия сложной формы

            //Расчёт себес-сти и колич. материала
            //сделано для подготовки данных для правил расчёта
            for (ElemSimple elem5e : winc.listElem) {
                if (filter(elem5e)) {

                    elem5e.spcRec.quant1 = formatAmount(elem5e.spcRec); //количество без отхода  
                    elem5e.spcRec.quant2 = (norm_cost == true) ? elem5e.spcRec.quant1 + (elem5e.spcRec.quant1 * elem5e.spcRec.waste / 100) : elem5e.spcRec.quant1; //количество с отходом
                    elem5e.spcRec.costprice += artdetCostprice(elem5e.spcRec); //себест. по табл. ARTDET и прав.расч.
                    elem5e.spcRec.price = elem5e.spcRec.costprice; //цена

                    //Вложенная спецификация
                    //цикл по детализации эдемента
                    for (TRecord spcRec : elem5e.spcRec.spcList) {
                        spcRec.quant1 = formatAmount(spcRec); //количество без отхода
                        spcRec.quant2 = (norm_cost == true) ? spcRec.quant1 + (spcRec.quant1 * spcRec.waste / 100) : spcRec.quant1; //количество с отходом
                        spcRec.costprice += artdetCostprice(spcRec); //себест. по табл. ARTDET и прав.расч.
                        spcRec.price = spcRec.costprice; //цена
                    }
                }
            }

            //Расчёт с учётом наценок и скидок
            //цикл по эдементам конструкции
            for (ElemSimple elem5e : winc.listElem) {
                if (filter(elem5e)) {

                    //<editor-fold defaultstate="collapsed" desc="ПРАВИЛА РАСЧЁТА">                     
                    //Цикл по правилам расчёта.                 
                    for (Record rulecalcRec : eRulecalc.filter()) {
                        //Всё обнуляется и рассчитывается по таблице правил расчёта
                        //Увеличение себестоимости/стоимости в coeff раз и на incr величину наценки.

                        //Фильтр по полю 'форма профиля', в заполнениях. В БиМакс используюеся только 1, 4, 10, 12 параметры
                        int form = (rulecalcRec.getInt(eRulecalc.form) == 0) ? 1 : rulecalcRec.getInt(eRulecalc.form);
                        if (Type.GLASS == elem5e.type) {//фильтр для стеклопакета

                            if (form == TypeForm.P00.id) {//не проверять форму
                                rulecalcTarif(winc, rulecalcRec, elem5e.spcRec);

                            } else if (form == TypeForm.P10.id && Type.TRAPEZE == elem5e.owner.type) { //не прямоугольное, не арочное заполнение
                                rulecalcTarif(winc, rulecalcRec, elem5e.spcRec);

                            } else if (form == TypeForm.P12.id && Type.ARCH == elem5e.owner.type) {//не прямоугольное заполнение с арками
                                rulecalcTarif(winc, rulecalcRec, elem5e.spcRec);
                            }
                        } else if (form == TypeForm.P04.id && elem5e.type == Type.FRAME_SIDE
                                && elem5e.owner.type == Type.ARCH && elem5e.layout() == Layout.TOP) {  //профиль с радиусом  (фильтр для арки профиля AYPC.W62.0101)
                            rulecalcTarif(winc, rulecalcRec, elem5e.spcRec); //профиль с радиусом

                        } else {
                            if (form == TypeForm.P00.id) {  //не проверять форму
                                rulecalcTarif(winc, rulecalcRec, elem5e.spcRec); //всё остальное не проверять форму
                            }
                        }
                    }
                    // </editor-fold> 

                    Record systreeRec = eSystree.find(winc.nuni);
                    Record artgrp1Rec = eGroups.find(elem5e.spcRec.artiklRec.getInt(eArtikl.groups1_id));
                    Record artgrp2Rec = eGroups.find(elem5e.spcRec.artiklRec.getInt(eArtikl.groups2_id));
                    Scale.artiklK.v = artgrp1Rec.getDbl(eGroups.val, 1);  //наценка группы мат.ценностей
                    Scale.artiklS.v = artgrp2Rec.getDbl(eGroups.val, 0);  //скидки группы мат.ценностей
                    Scale.systreeK.v = systreeRec.getDbl(eSystree.coef, 1); //коэф. рентабельности

                    double sbs1 = elem5e.spcRec.costprice * Scale.artiklK.v * Scale.systreeK.v;
                    elem5e.spcRec.price = sbs1 + Scale.grpformN1.v * sbs1 / 100; //цена за един.изм 
                    elem5e.spcRec.cost1 = elem5e.spcRec.price * elem5e.spcRec.quant2; //стоимость без скидки                     
                    elem5e.spcRec.cost2 = elem5e.spcRec.cost1 - Scale.artiklS.v * elem5e.spcRec.cost1 / 100; //стоимость со скидкой 

                    //Цикл по детализации
                    for (TRecord spcRec : elem5e.spcRec.spcList) {

                        // <editor-fold defaultstate="collapsed" desc="ПРАВИЛА РАСЧЁТА">  
                        //Цикл по правилам расчёта.
                        for (Record rulecalcRec : eRulecalc.filter()) {
                            int form = (rulecalcRec.getInt(eRulecalc.form) == 0) ? 1 : rulecalcRec.getInt(eRulecalc.form);
                            if (form == TypeForm.P00.id) { //не проверять форму 
                                rulecalcTarif(winc, rulecalcRec, spcRec);
                            }
                        }
                        // </editor-fold> 

                        Record artgrp1bRec = eGroups.find(spcRec.artiklRec.getInt(eArtikl.groups1_id));
                        Record artgrp2bRec = eGroups.find(spcRec.artiklRec.getInt(eArtikl.groups2_id));
                        Scale.artiklK.v = artgrp1bRec.getDbl(eGroups.val, 1);  //наценка группы мат.ценностей
                        Scale.artiklS.v = artgrp2bRec.getDbl(eGroups.val, 0);  //скидки группы мат.ценностей
                        Scale.systreeK.v = systreeRec.getDbl(eSystree.coef); //коэф. рентабельности

                        double sbs2 = spcRec.costprice * Scale.artiklK.v * Scale.systreeK.v;
                        spcRec.price = sbs2 + Scale.grpformN1.v * sbs2 / 100; //цена за един.изм 
                        spcRec.cost1 = spcRec.price * spcRec.quant2; //стоимость без скидки                     
                        spcRec.cost2 = spcRec.cost1 - Scale.artiklS.v * spcRec.cost1 / 100; //стоимость со скидкой                                   
                    }
                }
            }

            //Расчёт веса элемента конструкции
            for (ElemSimple elem5e : winc.listElem) {
                if (filter(elem5e)) {
                    elem5e.spcRec.weight = elem5e.spcRec.quant1 * elem5e.spcRec.artiklRec.getDbl(eArtikl.density);

                    for (TRecord spec : elem5e.spcRec.spcList) {
                        spec.weight = spec.quant1 * spec.artiklRec.getDbl(eArtikl.density);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Tariffic.calc() " + e);
        }
    }

    //Себес-сть. Рассчёт тарифа для заданного артикула, заданных цветов по таблице eArtdet
    public static double artdetCostprice(TRecord specificRec) {

        double costpriceSum = 0; //себестоимость
        Record color1Rec = eColor.find(specificRec.colorID1);  //основная
        Record color2Rec = eColor.find(specificRec.colorID2);  //внутренняя
        Record color3Rec = eColor.find(specificRec.colorID3);  //внешняя

        Scale.grpcursK1.v = eCurrenc.find(specificRec.artiklRec.getInt(eArtikl.currenc1_id)).getDbl(eCurrenc.cross_cour); //кросс-курс валюты для основной текстуры
        Scale.grpcursK2.v = eCurrenc.find(specificRec.artiklRec.getInt(eArtikl.currenc2_id)).getDbl(eCurrenc.cross_cour); //кросс-курс валюты для неосновных текстур (внутренняя, внешняя, двухсторонняя)

        //Цикл по тарификационной таблице ARTDET мат. ценностей
        for (Record artdetRec : eArtdet.filter(specificRec.artiklRec.getInt(eArtikl.id))) {

            double costprice = 0;
            boolean artdetUsed = false;

            //СЛОЖЕНИЕ ОСНОВНОЙ И ДВУХСТОРОННЕЙ
            //если тариф двухсторонней текстуры не равен 0, и если
            //текстура1 равна текстура2 и заданный тариф применим
            if (artdetRec.getDbl(eArtdet.cost_c4) != 0
                    && specificRec.colorID2 == specificRec.colorID3 && isUseTarif(artdetRec, color2Rec)) {

                specificRec.artdetRec[1] = artdetRec;
                specificRec.artdetRec[2] = artdetRec;
                Scale.artdet2T.v = artdetRec.getDbl(eArtdet.cost_c4); //тариф двухсторонней текстуры

                costprice += Scale.artdet2T.v / Scale.grpcursK2.v;

                if (isUseTarif(artdetRec, color1Rec)) { //подбираем тариф основной текстуры
                    specificRec.artdetRec[0] = artdetRec;
                    double m1 = artdetRec.getDbl(eArtdet.cost_unit); //тариф единица измерения
                    double m2 = (specificRec.elem5e == null) ? 0 : specificRec.elem5e.artiklRec.getDbl(eArtikl.density); //удельный вес                    
                    if (m1 > 0 && m2 > 0) {
                        costprice += m1 * m2;
                    } else {
                        Record colgrpRec = eGroups.find(color1Rec.getInt(eColor.groups_id));
                        Scale.artdetT1.v = artdetRec.getDbl(eArtdet.cost_c1); //тариф основной текстуры"

                        costprice += Scale.artdetT1.v / Scale.grpcursK1.v;
                    }
                }
                artdetUsed = true;

                //СЛОЖЕНИЕ ТРЁХ ТЕКСТУР
            } else {
                //Подбираем тариф основной текстуры
                if (isUseTarif(artdetRec, color1Rec)) {
                    specificRec.artdetRec[0] = artdetRec;
                    Scale.artdetT1.v = artdetRec.getDbl(eArtdet.cost_c1); //тариф основной текстуры

                    costprice += Scale.artdetT1.v / Scale.grpcursK1.v;
                    artdetUsed = true;
                }
                //Подбираем тариф внутренней текстуры
                if (isUseTarif(artdetRec, color2Rec)) {
                    specificRec.artdetRec[1] = artdetRec;
                    Scale.artdetT2.v = artdetRec.getDbl(eArtdet.cost_c2); //тариф внутренний текстуры

                    costprice += Scale.artdetT2.v / Scale.grpcursK2.v;
                    artdetUsed = true;
                }
                //Подбираем тариф внешней текстуры
                if (isUseTarif(artdetRec, color3Rec)) {
                    specificRec.artdetRec[2] = artdetRec;
                    Scale.artdetT3.v = artdetRec.getDbl(eArtdet.cost_c3); //тариф внешний текстуры

                    costprice += Scale.artdetT3.v / Scale.grpcursK2.v;
                    artdetUsed = true;
                }
            }
            //Проверка минимального тарифа (Непонятная проверка)
            {
                //?
            }
            //Вставки, проц. наценка
            if (artdetUsed == true) { //если было попадание
                if ("ВСТ".equals(specificRec.place.substring(0, 3)) == true) {
                    if (specificRec.detailRec != null) {
                        Record elementRec = eElement.find(specificRec.detailRec.getInt(eElemdet.element_id));
                        if (elementRec.getDbl(eElement.markup) > 0) {
                            costprice = costprice + (costprice * (elementRec.getDbl(eElement.markup))) / 100;
                        }
                    }
                }
                double coef = (Scale.nakl_cost == true) ? artdetRec.getDbl(eArtdet.coef) : 1; //kоэф. накл. расходов по табл. ARTDET 
                costpriceSum = costpriceSum + costprice * coef;
            }
        }
        return costpriceSum;
    }

    //Себес-сть по правилам расчёта
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

                            //Правило по количеству элемента
                            if (rulecalcRec.getInt(eRulecalc.common) == 0) {
                                //Фильтр по количеству отдельного элемента
                                if (UCom.containsNumbJust(rulecalcRec.getStr(eRulecalc.quant), spcRec.quant2) == true) {
                                    //По себестоимости или стоимости
                                    if (rulecalcRec.getInt(eRulecalc.sebes) == 1) {
                                        spcRec.costprice = spcRec.costprice * rulecalcRec.getDbl(eRulecalc.coeff) + rulecalcRec.getDbl(eRulecalc.suppl);  //увеличение себестоимости в coeff раз и на incr величину надбавки
                                    } else {
                                        spcRec.price = spcRec.price * rulecalcRec.getDbl(eRulecalc.coeff) + rulecalcRec.getDbl(eRulecalc.suppl);  //увеличение стоимости в coeff раз и на incr величину надбавки                                   
                                    }
                                }

                                //Правило по сумме количеств всего проекта
                            } else if (rulecalcRec.getInt(eRulecalc.common) == 1) { //по использованию c расчётом общего количества по артикулу, подтипу, типу
                                ArrayList<ElemSimple> elemList = winc.listElem;
                                double quantity3 = 0;

                                //Фильтр по артикулу
                                if (rulecalcRec.get(eRulecalc.artikl_id) != null) {
                                    for (ElemSimple elem5e : elemList) { //суммирую колич. всех элементов (например штапиков)
                                        if (filter(elem5e)) {
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
                                    for (ElemSimple elem5e : elemList) { //суммирую колич. всех элементов (например штапиков)
                                        if (filter(elem5e)) {
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
                                //Фильтр по количеству всего проекта
                                if (UCom.containsNumbJust(rulecalcRec.getStr(eRulecalc.quant), quantity3) == true) {
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
            System.err.println("Ошибка:Tariffic.rulecalcPrise() " + e);
        }
    }

    //Цена за ед. и стоимость
    public static void artiklPriceAndCost(TRecord specificRec) {

        Record color1Rec = eColor.find(specificRec.colorID1);  //основная
        Record color2Rec = eColor.find(specificRec.colorID2);  //внутренняя
        Record color3Rec = eColor.find(specificRec.colorID3);  //внешняя
        
        Record artdetRec1 = specificRec.artdetRec[0];
        Record artdetRec2 = specificRec.artdetRec[1];
        Record artdetRec3 = specificRec.artdetRec[2];
        
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
            return spcRec.count * round(spcRec.width, Scale.precision) / 1000;

        } else if (UseUnit.METR2.id == spcRec.artiklRec.getInt(eArtikl.unit)) { //кв. метры
            return spcRec.count * round(spcRec.width, Scale.precision) * round(spcRec.height, Scale.precision) / 1000000;

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

    private static double round(double value, int places) {
        if (places == 0) {
            return value;
        }
        places = (places == 3) ? 1 : (places == 2) ? 2 : 3;
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //Фильтр на фантомы, грязные фичи...
    private static boolean filter(ElemSimple elem5e) {
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
