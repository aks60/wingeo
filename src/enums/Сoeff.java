package enums;

import dataset.Record;
import builder.Wincalc;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eCurrenc;
import domain.eGroups;

public enum Сoeff {
    rentabK, //SYSTREE коэффициент рентабельности

    grpcolK, //GROUPS kоэф. группы текстуры
    croscursK, //GROUPS кросс курс 
    compformN, //GROUPS надбавка на изделия сложной формы

    indetT, //ARTDET тариф внутр.. текстуры
    twodetT, //ARTDET тариф двухст. текстуры
    artdetK, //ARTDET kоэф. текстуры

    osncolK, //COLOR коэф. основной текстуры
    incolK, //COLOR коэф. внутренний текстуры
    outcolK, //COLOR коэф. внешний текстуры

    rulecalcK, //RULECALC коэф. правил расчёта
    rulecalcN; //RULECALC надб. правил расчёта

    private double v = 0;

    Сoeff() {

        this.v = 0;
    }

    public double v() {
        return this.v;
    }

    public static void init(Wincalc winc, Record color2Rec, Record artiklRec, Record artdetRec, Record colgrpRec) {
        if (Type.ARCH == winc.root.type) {
            compformN.v = eGroups.find(2101).getDbl(eGroups.val);

        } else if (Type.RECTANGL != winc.root.type) {
            compformN.v = eGroups.find(2104).getDbl(eGroups.val);
        } else {
            compformN.v = 0;
        }

        indetT.v = artdetRec.getDbl(eArtdet.cost_c2); //тариф внутренний текстуры
        outcolK.v = color2Rec.getDbl(eColor.coef2); //ценовой коэф.внутренний текстуры
        grpcolK.v = colgrpRec.getDbl(eGroups.val); //коэф. группы текстур

        dataset.Record kursBaseRec = eCurrenc.find(artiklRec.getInt(eArtikl.currenc1_id));    // кросс-курс валюты для основной текстуры
        dataset.Record kursNoBaseRec = eCurrenc.find(artiklRec.getInt(eArtikl.currenc2_id));  // кросс-курс валюты для неосновных текстур (внутренняя, внешняя, двухсторонняя)

        croscursK.v = kursNoBaseRec.getDbl(eCurrenc.cross_cour); //кросс курс        
    }
}
