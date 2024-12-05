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
import static common.UCom.round;
import dataset.Query;
import domain.eElemdet;
import domain.eElement;
import domain.ePrjkit;
import domain.ePrjprod;
import enums.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Расчёт стоимости элементов окна алгоритм см. в UML
 */
public class TTariffic extends Cal5e {

    private static boolean norm_otx = true;
    public static int precision = Math.round(new Query(eGroups.values())
            .sql(eGroups.data(), eGroups.up).get(0).getFloat(eGroups.val)); //округление длины профилей

    public TTariffic(Wincalc winc, boolean norm_otx) {
        super(winc);
        this.norm_otx = norm_otx;
    }

    //Рассчёт конструкции с учётом 
    //всех скидок и наценок
    public void calculate() {
        try {
            double k9 = percentMarkup(winc); //процентная надбавка на изделия сложной формы

            //Расчёт себес-сти за ед.изм. и колич. материала
            //цикл по эдементам конструкции
            for (ElemSimple elem5e : winc.listElem) {
                if (filter(elem5e)) {

                    elem5e.spcRec.sebes1 += artdetPrice(elem5e.spcRec); //себест. по табл. ARTDET и прав.расч.
                    elem5e.spcRec.quant1 = formatAmount(elem5e.spcRec); //количество без отхода  

                    if (norm_otx == false) {
                        elem5e.spcRec.quant2 = elem5e.spcRec.quant1;  //количество без отхода
                    } else {
                        elem5e.spcRec.quant2 = elem5e.spcRec.quant1 + (elem5e.spcRec.quant1 * elem5e.spcRec.waste / 100); //количество с отходом                       
                    }

                    //Вложенная спецификация
                    //цикл по детализации эдемента
                    for (TRecord spсRec2 : elem5e.spcRec.spcList) {
                        spсRec2.sebes1 += artdetPrice(spсRec2); //себест. за ед. без отхода
                        spсRec2.quant1 = formatAmount(spсRec2); //количество без отхода

                        if (norm_otx == false) {
                            spсRec2.quant2 = spсRec2.quant1; //количество без отхода  
                        } else {
                            spсRec2.quant2 = spсRec2.quant1 + (spсRec2.quant1 * spсRec2.waste / 100); //количество с отходом
                        }
                    }
                }
            }

            //Рассчёт с учётом наценок и скидок
            //цикл по эдементам конструкции
            for (ElemSimple elem5e : winc.listElem) {
                if (filter(elem5e)) {

                    // <editor-fold defaultstate="collapsed" desc="Правила рассч.">                     
                    //Цикл по правилам расчёта.                 
                    for (Record rulecalcRec : eRulecalc.filter()) {
                        //Всё обнуляется и рассчитывается по таблице правил расчёта
                        //Увеличение себестоимости в coeff раз и на incr величину наценки.

                        //Фильтр по полю 'форма профиля', в заполнениях. В БиМакс используюеся только 1, 4, 10, 12 параметры
                        int form = (rulecalcRec.getInt(eRulecalc.form) == 0) ? 1 : rulecalcRec.getInt(eRulecalc.form);
                        if (Type.GLASS == elem5e.type) {//фильтр для стеклопакета

                            if (form == TypeForm.P00.id) {//не проверять форму
                                rulecalcPrise(winc, rulecalcRec, elem5e.spcRec);

                            } else if (form == TypeForm.P10.id && Type.TRAPEZE == elem5e.owner.type) { //не прямоугольное, не арочное заполнение
                                rulecalcPrise(winc, rulecalcRec, elem5e.spcRec);

                            } else if (form == TypeForm.P12.id && Type.ARCH == elem5e.owner.type) {//не прямоугольное заполнение с арками
                                rulecalcPrise(winc, rulecalcRec, elem5e.spcRec);
                            }
                        } else if (form == TypeForm.P04.id && elem5e.type == Type.FRAME_SIDE
                                && elem5e.owner.type == Type.ARCH && elem5e.layout() == Layout.TOP) {  //профиль с радиусом  (фильтр для арки профиля AYPC.W62.0101)
                            rulecalcPrise(winc, rulecalcRec, elem5e.spcRec); //профиль с радиусом

                        } else {
                            if (form == TypeForm.P00.id) {  //не проверять форму
                                rulecalcPrise(winc, rulecalcRec, elem5e.spcRec); //всё остальное не проверять форму
                            }
                        }
                    }
                    // </editor-fold> 

                    Record systreeRec = eSystree.find(winc.nuni);
                    Record artgrp1Rec = eGroups.find(elem5e.spcRec.artiklRec().getInt(eArtikl.groups1_id));
                    Record artgrp2Rec = eGroups.find(elem5e.spcRec.artiklRec().getInt(eArtikl.groups2_id));
                    double artgrpK1 = artgrp1Rec.getDbl(eGroups.val, 1);  //наценка группы мат.ценностей
                    double artgrpK2 = artgrp2Rec.getDbl(eGroups.val, 0);  //скидки группы мат.ценностей
                    double rentabK = systreeRec.getDbl(eSystree.coef, 1); //коэф. рентабельности

                    double sbc1 = elem5e.spcRec.sebes1 * artgrpK1 * rentabK;
                    elem5e.spcRec.sebes2 = sbc1 + k9 * sbc1 / 100; //стоимость за един.изм 
                    elem5e.spcRec.price1 = elem5e.spcRec.sebes2 * elem5e.spcRec.quant2; //стоимость без скидки                     
                    elem5e.spcRec.price2 = elem5e.spcRec.price1 - artgrpK2 * elem5e.spcRec.price1 / 100; //стоимость со скидкой 

                    //Цикл по детализации
                    for (TRecord spcRec : elem5e.spcRec.spcList) {

                        // <editor-fold defaultstate="collapsed" desc="Правила рассч. вложенные">  
                        //Цикл по правилам расчёта.
                        for (Record rulecalcRec : eRulecalc.filter()) {
                            int form = (rulecalcRec.getInt(eRulecalc.form) == 0) ? 1 : rulecalcRec.getInt(eRulecalc.form);
                            if (form == TypeForm.P00.id) { //не проверять форму 
                                rulecalcPrise(winc, rulecalcRec, spcRec);
                            }
                        }
                        // </editor-fold> 

                        Record artgrp1bRec = eGroups.find(spcRec.artiklRec().getInt(eArtikl.groups1_id));
                        Record artgrp2bRec = eGroups.find(spcRec.artiklRec().getInt(eArtikl.groups2_id));
                        double m1 = artgrp1bRec.getDbl(eGroups.val, 1);  //наценка группы мат.ценностей
                        double m2 = artgrp2bRec.getDbl(eGroups.val, 0);  //скидки группы мат.ценностей
                        double m3 = systreeRec.getDbl(eSystree.coef); //коэф. рентабельности

                        double sbs2 = spcRec.sebes1 * m1 * m3;
                        spcRec.sebes2 = sbs2 + k9 * sbs2 / 100; //стоимость за един.изм 
                        spcRec.price1 = spcRec.sebes2 * spcRec.quant2; //стоимость без скидки                     
                        spcRec.price2 = spcRec.price1 - m2 * spcRec.price1 / 100; //стоимость со скидкой 
                    }
                }
            }

            //Расчёт веса элемента конструкции
            for (ElemSimple elem5e : winc.listElem) {
                if (filter(elem5e)) {
                    elem5e.spcRec.weight = elem5e.spcRec.quant1 * elem5e.spcRec.artiklRec().getDbl(eArtikl.density);

                    for (TRecord spec : elem5e.spcRec.spcList) {
                        spec.weight = spec.quant1 * spec.artiklRec().getDbl(eArtikl.density);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Tariffic.calc() " + e);
        }
    }

    //Себес-сть за ед. изм. Рассчёт тарифа для заданного артикула заданных цветов по таблице eArtdet
    public static double artdetPrice(TRecord specificRec) {

        double inPrice = 0;
        Record color1Rec = eColor.find(specificRec.colorID1);  //основная
        Record color2Rec = eColor.find(specificRec.colorID2);  //внутренняя
        Record color3Rec = eColor.find(specificRec.colorID3);  //внешняя

        Record kursBaseRec = eCurrenc.find(specificRec.artiklRec().getInt(eArtikl.currenc1_id));    // кросс-курс валюты для основной текстуры
        Record kursNoBaseRec = eCurrenc.find(specificRec.artiklRec().getInt(eArtikl.currenc2_id));  // кросс-курс валюты для неосновных текстур (внутренняя, внешняя, двухсторонняя)

        //Цикл по тарификационной таблице ARTDET мат. ценностей
        for (Record artdetRec : eArtdet.filter(specificRec.artiklRec().getInt(eArtikl.id))) {

            double artdetPrice = 0;
            boolean artdetUsed = false;

            //СЛОЖЕНИЕ ОСНОВНОЙ И ДВУХСТОРОННЕЙ
            //если тариф двухсторонней текстуры не равен 0, и если
            //текстура1 равна текстура2 и заданный тариф применим
            if (artdetRec.getDbl(eArtdet.cost_c4) != 0
                    && color2Rec.getInt(eColor.id) == color3Rec.getInt(eColor.id)
                    && isTariff(artdetRec, color2Rec)) {

                double twocolorK = artdetRec.getDbl(eArtdet.cost_c4); //тариф двухсторонней текстуры
                double incolorK = color2Rec.getDbl(eColor.coef2); //ценовой коэф.внутренний текстуры
                double outcolorK = color3Rec.getDbl(eColor.coef3); //ценовой коэф.внешний текстуры
                double croscursK = kursNoBaseRec.getDbl(eCurrenc.cross_cour); //кросс курс 
                artdetPrice += (twocolorK * Math.max(incolorK, outcolorK) / croscursK);

                if (isTariff(artdetRec, color1Rec)) { //подбираем тариф основной текстуры
                    double m1 = artdetRec.getDbl(eArtdet.cost_unit); //тариф единица измерения
                    double m2 = (specificRec.elem5e == null) ? 0 : specificRec.elem5e.artiklRec.getDbl(eArtikl.density); //удельный вес                    
                    if (m1 > 0 && m2 > 0) {
                        artdetPrice += m1 * m2;
                    } else {
                        Record colgrpRec = eGroups.find(color1Rec.getInt(eColor.groups_id));
                        double z1 = artdetRec.getDbl(eArtdet.cost_c1); //тариф основной текстуры"
                        double z2 = color1Rec.getDbl(eColor.coef1); //ценовой коэф.основной текст.
                        double z3 = colgrpRec.getDbl(eGroups.val); //коэф. группы текстур
                        double z5 = kursBaseRec.getDbl(eCurrenc.cross_cour); //кросс курс
                        artdetPrice += (z1 * z2 * z3 * z5) / z5;
                    }
                }
                artdetUsed = true;

                //СЛОЖЕНИЕ ТРЁХ ТЕКСТУР
            } else {
                //Подбираем тариф основной текстуры
                if (isTariff(artdetRec, color1Rec)) {
                    Record colgrpRec = eGroups.find(color1Rec.getInt(eColor.groups_id));
                    double c1 = artdetRec.getDbl(eArtdet.cost_c1); //тариф основной текстуры
                    double c2 = color1Rec.getDbl(eColor.coef1); //ценовой коэф.основной текстуры
                    double c3 = colgrpRec.getDbl(eGroups.val); //коэф. группы текстур
                    double c5 = kursBaseRec.getDbl(eCurrenc.cross_cour); //кросс курс
                    artdetPrice += (c1 * c2 * c3) / c5;
                    artdetUsed = true;
                }
                //Подбираем тариф внутренней текстуры
                if (isTariff(artdetRec, color2Rec)) {
                    Record colgrpRec = eGroups.find(color2Rec.getInt(eColor.groups_id));
                    double d1 = artdetRec.getDbl(eArtdet.cost_c2); //тариф внутренний текстуры
                    double d2 = color2Rec.getDbl(eColor.coef2); //ценовой коэф.внутренний текстуры
                    double d3 = colgrpRec.getDbl(eGroups.val); //коэф. группы текстур
                    double d5 = kursNoBaseRec.getDbl(eCurrenc.cross_cour); //кросс курс
                    artdetPrice += (d1 * d2 * d3) / d5;
                    artdetUsed = true;
                }
                //Подбираем тариф внешней текстуры
                if (isTariff(artdetRec, color3Rec)) {
                    Record colgrpRec = eGroups.find(color3Rec.getInt(eColor.groups_id));
                    double e1 = artdetRec.getDbl(eArtdet.cost_c3); //Тариф внешний текстуры
                    double e2 = color3Rec.getDbl(eColor.coef3); //ценовой коэф.внешний текстуры
                    double e3 = colgrpRec.getDbl(eGroups.val); //коэф. группы текстур
                    double e5 = kursNoBaseRec.getDbl(eCurrenc.cross_cour); //кросс курс
                    artdetPrice += (e1 * e2 * e3) / e5;
                    artdetUsed = true;
                }
            }
            //Проверка минимального тарифа (Непонятная проверка)
            /*if (artdetUsed && artdetRec.getDbl(eArtdet.cost_min) != 0
                    && specificRec.quant1 != 0 && artdetPrice
                    * specificRec.quant1 < artdetRec.getDbl(eArtdet.cost_min)) {

                artdetPrice = artdetRec.getDbl(eArtdet.cost_min) / specificRec.quant1;
            }*/
            if (artdetUsed == true) { //если было попадание
                if ("ВСТ".equals(specificRec.place.substring(0, 3)) == true) {
                    if (specificRec.detailRec != null) {
                        Record elementRec = eElement.find(specificRec.detailRec.getInt(eElemdet.element_id));
                        if (elementRec.getDbl(eElement.markup) > 0) {
                            artdetPrice = artdetPrice + (artdetPrice * (elementRec.getDbl(eElement.markup))) / 100;
                        }
                    }
                }
                inPrice = inPrice + (artdetPrice
                        * artdetRec.getDbl(eArtdet.coef)); //kоэф. текстуры по табл. ARTDET
            }
        }
        return inPrice;
    }

    //Правила расчёта. Фильтр по полю form, color(1,2,3) таблицы RULECALC
    private static void rulecalcPrise(Wincalc winc, Record rulecalcRec, TRecord spcRec) {

        try {
            //Если артикул ИЛИ тип ИЛИ подтип совпали
            if (spcRec.artiklRec().get(eArtikl.id) != null
                    && (spcRec.artiklRec().get(eArtikl.id).equals(rulecalcRec.get(eRulecalc.artikl_id)) == true || rulecalcRec.get(eRulecalc.artikl_id) == null)) {

                if ((spcRec.artiklRec().getInt(eArtikl.level1) * 100 + spcRec.artiklRec().getInt(eArtikl.level2)) == rulecalcRec.getInt(eRulecalc.type)) {
                    if (UCom.containsColor(rulecalcRec.getStr(eRulecalc.color1), spcRec.colorID1) == true
                            && UCom.containsColor(rulecalcRec.getStr(eRulecalc.color2), spcRec.colorID2) == true
                            && UCom.containsColor(rulecalcRec.getStr(eRulecalc.color3), spcRec.colorID3) == true) {

                        if (rulecalcRec.getInt(eRulecalc.common) == 0) {
                            if (UCom.containsNumbJust(rulecalcRec.getStr(eRulecalc.quant), spcRec.quant2) == true) {
                                spcRec.sebes1 = spcRec.sebes1 * rulecalcRec.getDbl(eRulecalc.coeff) + rulecalcRec.getDbl(eRulecalc.incr);  //увеличение себестоимости в coegg раз и на incr величину надбавки
                            }

                        } else if (rulecalcRec.getInt(eRulecalc.common) == 1) { //по использованию c расчётом общего количества по артикулу, подтипу, типу
                            ArrayList<ElemSimple> elemList = winc.listElem;
                            double quantity3 = 0;
                            if (rulecalcRec.get(eRulecalc.artikl_id) != null) { //по артикулу
                                for (ElemSimple elem5e : elemList) { //суммирую колич. всех элементов (например штапиков)
                                    if (filter(elem5e)) {
                                        if (elem5e.spcRec.artikl.equals(spcRec.artikl)) {
                                            quantity3 = quantity3 + elem5e.spcRec.quant1;
                                        }
                                        for (TRecord specifRec2 : elem5e.spcRec.spcList) {
                                            if (specifRec2.artikl.equals(spcRec.artikl)) {
                                                quantity3 = quantity3 + specifRec2.quant1;
                                            }
                                        }
                                    }
                                }
                            } else { //по подтипу, типу
                                for (ElemSimple elem5e : elemList) { //суммирую колич. всех элементов (например штапиков)
                                    if (filter(elem5e)) {
                                        TRecord specifRec2 = elem5e.spcRec;
                                        if (specifRec2.artiklRec().getInt(eArtikl.level1) * 100 + specifRec2.artiklRec().getInt(eArtikl.level2) == rulecalcRec.getInt(eRulecalc.type)) {
                                            quantity3 = quantity3 + elem5e.spcRec.quant1;
                                        }
                                        for (TRecord specifRec3 : specifRec2.spcList) {
                                            if (specifRec3.artiklRec().getInt(eArtikl.level1) * 100 + specifRec3.artiklRec().getInt(eArtikl.level2) == rulecalcRec.getInt(eRulecalc.type)) {
                                                quantity3 = quantity3 + specifRec3.quant1;
                                            }
                                        }
                                    }
                                }
                            }
                            if (UCom.containsNumbJust(rulecalcRec.getStr(eRulecalc.quant), quantity3) == true) {
                                spcRec.sebes1 = spcRec.sebes1 * rulecalcRec.getDbl(eRulecalc.coeff) + rulecalcRec.getDbl(eRulecalc.incr); //увеличение себестоимости в coeff раз и на incr величину надбавки                      
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Tariffic.rulecalcPrise() " + e);
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

        if (UseUnit.METR.id == spcRec.artiklRec().getInt(eArtikl.unit)) { //метры
            return spcRec.count * round(spcRec.width, TTariffic.precision) / 1000;

        } else if (UseUnit.METR2.id == spcRec.artiklRec().getInt(eArtikl.unit)) { //кв. метры
            return spcRec.count * round(spcRec.width, TTariffic.precision) * round(spcRec.height, TTariffic.precision) / 1000000;

        } else if (UseUnit.PIE.id == spcRec.artiklRec().getInt(eArtikl.unit)) { //шт.
            return spcRec.count;

        } else if (UseUnit.KIT.id == spcRec.artiklRec().getInt(eArtikl.unit)) { //комп.
            return spcRec.count;

        } else if (UseUnit.ML.id == spcRec.artiklRec().getInt(eArtikl.unit)) { //мл
            return spcRec.count;
        }
        return 0;
    }

    //Проверяет, должен ли применяться заданный тариф мат-ценности для заданной текстуры
    public static boolean isTariff(Record artdetRec, Record colorRec) {

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
        } else if (elem5e.spcRec.artiklRec() == null) {
            return false;
        } else if (elem5e.spcRec.artiklRec().getInt(eArtikl.id) == -3) {
            return false;
        }
        return true;
    }
}
