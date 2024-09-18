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
import common.ArraySpc;
import common.UCom;
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
public class SpcTariffic extends Cal5e {

    private static boolean norm_otx = true;
    private static int precision = Math.round(new Query(eGroups.values())
            .sql(eGroups.data(), eGroups.up).get(0).getFloat(eGroups.val)); //округление длины профилей

    public SpcTariffic(Wincalc winc, boolean norm_otx) {
        super(winc);
        this.norm_otx = norm_otx;
    }

    //Тарификация конструкции
    public void calc() {
        try {
            double percentMarkup = percentMarkup(winc); //процентная надбавка на изделия сложной формы

            //Расчёт себес-сти за ед.изм. и колич. материала
            //цикл по эдементам конструкции
            for (ElemSimple elem5e : winc.listElem) {
                if (filter(elem5e)) {

                    elem5e.spcRec.costpric1 += artdetPrice(elem5e.spcRec); //себест. за ед. без отхода по табл. ARTDET с коэф. и надб.
                    elem5e.spcRec.quant1 = formatAmount(elem5e.spcRec); //количество без отхода
                    elem5e.spcRec.quant2 = elem5e.spcRec.quant1;  //базовое количество с отходом
                    if (norm_otx == true) {
                        elem5e.spcRec.quant2 = elem5e.spcRec.quant2 + (elem5e.spcRec.quant1 * elem5e.spcRec.waste / 100); //количество с отходом
                    }
                    //Вложенная спецификация
                    //цикл по детализации эдемента
                    for (SpcRecord spсRec2 : elem5e.spcRec.spcList) {
                        spсRec2.costpric1 += artdetPrice(spсRec2); //себест. за ед. без отхода
                        spсRec2.quant1 = formatAmount(spсRec2); //количество без отхода
                        spсRec2.quant2 = spсRec2.quant1; //базовое количество с отходом
                        if (norm_otx == true) {
                            spсRec2.quant2 = spсRec2.quant2 + (spсRec2.quant1 * spсRec2.waste / 100); //количество с отходом
                        }
                    }
                }
            }

            //Правила расчёта
            //цикл по эдементам конструкции
            for (ElemSimple elem5e : winc.listElem) {
                if (filter(elem5e)) {

                    Record systreeRec = eSystree.find(winc.nuni);
                    //Цикл по правилам расчёта.                 
                    for (Record rulecalcRec : eRulecalc.list()) {
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

                    elem5e.spcRec.costpric2 = elem5e.spcRec.costpric1 * elem5e.spcRec.quant2; //себест. за ед. с отходом 
                    Record artgrp1Rec = eGroups.find(elem5e.spcRec.artiklRec().getInt(eArtikl.groups1_id));
                    Record artgrp2Rec = eGroups.find(elem5e.spcRec.artiklRec().getInt(eArtikl.groups2_id));
                    double k1 = artgrp1Rec.getDbl(eGroups.val, 1);  //наценка группы мат.ценностей
                    double k2 = artgrp2Rec.getDbl(eGroups.val, 0);  //скидки группы мат.ценностей
                    double k3 = systreeRec.getDbl(eSystree.coef, 1); //коэф. рентабельности
                    elem5e.spcRec.price1 = elem5e.spcRec.costpric2 * k1 * k3;
                    elem5e.spcRec.price1 = elem5e.spcRec.price1 + (elem5e.spcRec.price1 / 100) * percentMarkup; //стоимость без скидки                     
                    elem5e.spcRec.price2 = elem5e.spcRec.price1 - (elem5e.spcRec.price1 / 100) * k2; //стоимость со скидкой 

                    //Правила расчёта вложенные
                    for (SpcRecord spc : elem5e.spcRec.spcList) {

                        //Цикл по правилам расчёта.
                        for (Record rulecalcRec : eRulecalc.list()) {
                            int form = (rulecalcRec.getInt(eRulecalc.form) == 0) ? 1 : rulecalcRec.getInt(eRulecalc.form);
                            if (form == TypeForm.P00.id) { //не проверять форму 
                                rulecalcPrise(winc, rulecalcRec, spc);
                            }
                        }
                        spc.costpric2 = spc.costpric1 * spc.quant2; //себест. за ед. с отходом  
                        Record artgrp1bRec = eGroups.find(spc.artiklRec().getInt(eArtikl.groups1_id));
                        Record artgrp2bRec = eGroups.find(spc.artiklRec().getInt(eArtikl.groups2_id));
                        double m1 = artgrp1bRec.getDbl(eGroups.val, 1);  //наценка группы мат.ценностей
                        double m2 = artgrp2bRec.getDbl(eGroups.val, 0);  //скидки группы мат.ценностей
                        double m3 = systreeRec.getDbl(eSystree.coef); //коэф. рентабельности
                        spc.price1 = spc.costpric2 * m1 * m3;
                        spc.price1 = spc.price1 + (spc.price1 / 100) * percentMarkup; //стоимость без скидки                         
                        spc.price2 = spc.price1 - (spc.price1 / 100) * m2; //стоимость со скидкой 
                    }
                }
            }

            //Расчёт веса элемента конструкции
            for (ElemSimple elem5e : winc.listElem) {
                if (filter(elem5e)) {
                    elem5e.spcRec.weight = elem5e.spcRec.quant1 * elem5e.spcRec.artiklRec().getDbl(eArtikl.density);

                    for (SpcRecord spec : elem5e.spcRec.spcList) {
                        spec.weight = spec.quant1 * spec.artiklRec().getDbl(eArtikl.density);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Tariffic.calc() " + e);
        }
    }

    //Комплекты конструкции    
    public static ArraySpc<SpcRecord> kits(Record prjprodRec, Wincalc winc, boolean norm_otx) {
        ArraySpc<SpcRecord> kitList = new ArraySpc();
        try {
            Record systreeRec = eSystree.find(winc.nuni); //для нахожд. коэф. рентабельности
            double percentMarkup = percentMarkup(winc); //процентная надбавка на изделия сложной формы
            if (prjprodRec != null) {
                List<Record> prjkitList = ePrjkit.filter3(prjprodRec.getInt(ePrjprod.id));

                //Цикл по комплектам
                for (Record prjkitRec : prjkitList) {
                    Record artiklRec = eArtikl.find(prjkitRec.getInt(ePrjkit.artikl_id), true);
                    if (artiklRec != null) {
                        SpcRecord spc = new SpcRecord("КОМП", ++winc.spcId, prjkitRec, artiklRec, null);
                        spc.width = prjkitRec.getDbl(ePrjkit.width);
                        spc.height = prjkitRec.getDbl(ePrjkit.height);
                        spc.count = prjkitRec.getDbl(ePrjkit.numb);
                        spc.colorID1 = prjkitRec.getInt(ePrjkit.color1_id);
                        spc.colorID2 = prjkitRec.getInt(ePrjkit.color2_id);
                        spc.colorID3 = prjkitRec.getInt(ePrjkit.color3_id);
                        spc.anglCut0 = prjkitRec.getDbl(ePrjkit.angl1);
                        spc.anglCut1 = prjkitRec.getDbl(ePrjkit.angl2);
                        spc.quant1 = formatAmount(spc); //количество без отхода
                        spc.quant2 = (norm_otx == true) ? spc.quant1 + (spc.quant1 * spc.waste / 100) : spc.quant1; //количество с отходом
                        spc.costpric1 += artdetPrice(spc); //себест. за ед. без отхода по табл. ARTDET с коэф. и надб.
                        spc.costpric2 = spc.costpric1 + (spc.costpric1 * (spc.quant2 - spc.quant1)); //себест. за ед. с отходом 
                        Record artgrp1Rec = eGroups.find(spc.artiklRec().getInt(eArtikl.groups1_id));
                        Record artgrp2Rec = eGroups.find(spc.artiklRec().getInt(eArtikl.groups2_id));
                        double k1 = artgrp1Rec.getDbl(eGroups.val, 1);  //наценка группы мат.ценностей
                        double k2 = artgrp2Rec.getDbl(eGroups.val, 0);  //скидки группы мат.ценностей
                        double k3 = systreeRec.getDbl(eSystree.coef, 1); //коэф. рентабельности
                        double price = spc.costpric2 * k1 * k3;
                        spc.price1 = price + price / 100 * percentMarkup; //стоимость без скидки                     
                        spc.price2 = price - price / 100 * k2; //стоимость со скидкой 
                        kitList.add(spc);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Tariffic.kits() " + e);
        }
        return kitList;
    }

    //Себес-сть за ед. изм. Рассчёт тарифа для заданного артикула заданных цветов по таблице eArtdet
    private static double artdetPrice(SpcRecord specificRec) {

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

                double k1 = artdetRec.getDbl(eArtdet.cost_c4); //тариф двухсторонней текстуры
                double k2 = color2Rec.getDbl(eColor.coef2); //ценовой коэф.внутренний текстуры
                double k3 = color3Rec.getDbl(eColor.coef3); //ценовой коэф.внешний текстуры
                double k5 = kursNoBaseRec.getDbl(eCurrenc.cross_cour); //кросс курс 
                artdetPrice += (k1 * Math.max(k2, k3) / k5);

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
                    double k1 = artdetRec.getDbl(eArtdet.cost_c1); //тариф основной текстуры
                    double k2 = color1Rec.getDbl(eColor.coef1); //ценовой коэф.основной текстуры
                    double k3 = colgrpRec.getDbl(eGroups.val); //коэф. группы текстур
                    double k5 = kursBaseRec.getDbl(eCurrenc.cross_cour); //кросс курс
                    artdetPrice += (k1 * k2 * k3) / k5;
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
                    double w1 = artdetRec.getDbl(eArtdet.cost_c3); //Тариф внешний текстуры
                    double w2 = color3Rec.getDbl(eColor.coef3); //ценовой коэф.внешний текстуры
                    double w3 = colgrpRec.getDbl(eGroups.val); //коэф. группы текстур
                    double w5 = kursNoBaseRec.getDbl(eCurrenc.cross_cour); //кросс курс
                    artdetPrice += (w1 * w2 * w3) / w5;
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
    private static void rulecalcPrise(Wincalc winc, Record rulecalcRec, SpcRecord spcRec) {

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
                                spcRec.costpric1 = spcRec.costpric1 * rulecalcRec.getDbl(eRulecalc.coeff) + rulecalcRec.getDbl(eRulecalc.incr);  //увеличение себестоимости в coegg раз и на incr величину надбавки
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
                                        for (SpcRecord specifRec2 : elem5e.spcRec.spcList) {
                                            if (specifRec2.artikl.equals(spcRec.artikl)) {
                                                quantity3 = quantity3 + specifRec2.quant1;
                                            }
                                        }
                                    }
                                }
                            } else { //по подтипу, типу
                                for (ElemSimple elem5e : elemList) { //суммирую колич. всех элементов (например штапиков)
                                    if (filter(elem5e)) {
                                        SpcRecord specifRec2 = elem5e.spcRec;
                                        if (specifRec2.artiklRec().getInt(eArtikl.level1) * 100 + specifRec2.artiklRec().getInt(eArtikl.level2) == rulecalcRec.getInt(eRulecalc.type)) {
                                            quantity3 = quantity3 + elem5e.spcRec.quant1;
                                        }
                                        for (SpcRecord specifRec3 : specifRec2.spcList) {
                                            if (specifRec3.artiklRec().getInt(eArtikl.level1) * 100 + specifRec3.artiklRec().getInt(eArtikl.level2) == rulecalcRec.getInt(eRulecalc.type)) {
                                                quantity3 = quantity3 + specifRec3.quant1;
                                            }
                                        }
                                    }
                                }
                            }
                            if (UCom.containsNumbJust(rulecalcRec.getStr(eRulecalc.quant), quantity3) == true) {
                                spcRec.costpric1 = spcRec.costpric1 * rulecalcRec.getDbl(eRulecalc.coeff) + rulecalcRec.getDbl(eRulecalc.incr); //увеличение себестоимости в coeff раз и на incr величину надбавки                      
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Tariffic.rulecalcPrise() " + e);
        }
    }

    //В зав. от единицы изм. форматируется количество
    private static double formatAmount(SpcRecord spcRec) {
        //Нужна доработка для расчёта по минимальному тарифу. См. dll VirtualPro4::CalcArtTariff

        if (UseUnit.METR.id == spcRec.artiklRec().getInt(eArtikl.unit)) { //метры
            return spcRec.count * round(spcRec.width, precision) / 1000;

        } else if (UseUnit.METR2.id == spcRec.artiklRec().getInt(eArtikl.unit)) { //кв. метры
            return spcRec.count * round(spcRec.width, precision) * round(spcRec.height, precision) / 1000000;

        } else if (UseUnit.PIE.id == spcRec.artiklRec().getInt(eArtikl.unit)) { //шт.
            return spcRec.count;

        } else if (UseUnit.KIT.id == spcRec.artiklRec().getInt(eArtikl.unit)) { //комп.
            return spcRec.count;

        } else if (UseUnit.ML.id == spcRec.artiklRec().getInt(eArtikl.unit)) { //мл
            return spcRec.count;
        }
        return 0;
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
