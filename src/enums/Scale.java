package enums;

import dataset.Record;
import builder.Wincalc;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eCurrenc;
import domain.eGroups;
import domain.eRulecalc;

public enum Scale {
    systreeK("коэффициент рентабельности"), //SYSTREE 

    artiklK("koef. наценка группы мат.ценностей"), //ARTIKL
    artiklS("проц. скидки группы мат.ценностей"), //ARTIKL

    artdetT1("тариф основн. текстуры"), //ARTDET
    artdetT2("тариф внутр. текстуры"), //ARTDET 
    artdetT3("тариф внешн. текстуры"), //ARTDET 
    artdet2T("тариф двухст. текстуры"), //ARTDET 
    artdetK("kоэф. ценовой текстуры"), //ARTDET 

    grpcursK1("кросс-курс основной текстуры"), //GROUPS 
    grpcursK2("кросс-курс неосновных текстур"), //GROUPS 
    grpcolK("kоэф. группы текстуры"), //GROUPS
    grpformN("надбавка на изд. сложной формы"), //GROUPS 

    colorK1("коэф. основной текстуры"), //COLOR 
    colorK2("коэф. внутренний текстуры"), //COLOR 
    colorK3("коэф. внешний текстуры"), //COLOR 

    rulecalcK("коэф. правил расчЄта"), //RULECALC 
    rulecalcN("надб. правил расчЄта");  //RULECALC 

    public double v;
    public String s;

    Scale(String s) {

        this.v = 0;
        this.s = s;
    }

    public static void init(Wincalc winc, Record artiklRec, Record artdetRec, Record groupsRec, Record colorRec, Record rulecalcRec) {

        if (artiklRec != null) {
            Record artgrp1Rec = eGroups.find(artiklRec.getInt(eArtikl.groups1_id));
            Record artgrp2Rec = eGroups.find(artiklRec.getInt(eArtikl.groups2_id));
            artiklK.v = artgrp1Rec.getDbl(eGroups.val, 1); //koef. наценка группы мат.ценностей
            artiklS.v = artgrp2Rec.getDbl(eGroups.val, 0); //проц. скидки группы мат.ценностей
            Record croscurs1Rec = eCurrenc.find(artiklRec.getInt(eArtikl.currenc1_id));
            Record croscurs2Rec = eCurrenc.find(artiklRec.getInt(eArtikl.currenc2_id));
            grpcursK1.v = croscurs1Rec.getDbl(eCurrenc.cross_cour); //кросс-курс валюты дл€ основной текстуры        
            grpcursK2.v = croscurs2Rec.getDbl(eCurrenc.cross_cour); //кросс-курс валюты дл€ неосновных текстур (внутренн€€, внешн€€, двухсторонн€€)            
        }

        if (artdetRec != null) {
            artdetT1.v = artdetRec.getDbl(eArtdet.cost_c1); //тариф основн текстуры
            artdetT2.v = artdetRec.getDbl(eArtdet.cost_c2); //тариф внутр текстуры
            artdetT3.v = artdetRec.getDbl(eArtdet.cost_c3); //тариф внешн текстуры
        }

        if (groupsRec != null) {
            if (Type.ARCH == winc.root.type) { //надбавка на изд. сложной формы
                grpformN.v = eGroups.find(2101).getDbl(eGroups.val);

            } else if (Type.RECTANGL != winc.root.type) {
                grpformN.v = eGroups.find(2104).getDbl(eGroups.val);
            } else {
                grpformN.v = 0;
            }
            grpcolK.v = groupsRec.getDbl(eGroups.val); //коэф. группы текстур         
        }

        if (colorRec != null) {
            colorK1.v = colorRec.getDbl(eColor.coef1); //ценовой коэф. основн. текстуры
            colorK2.v = colorRec.getDbl(eColor.coef2); //ценовой коэф. внутр. текстуры
            colorK3.v = colorRec.getDbl(eColor.coef3); //ценовой коэф. внешн. текстуры   
        }

        if (rulecalcRec != null) {
            Scale.rulecalcK.v = rulecalcRec.getDbl(eRulecalc.coeff);
            Scale.rulecalcN.v = rulecalcRec.getDbl(eRulecalc.incr);
        }
    }
}
