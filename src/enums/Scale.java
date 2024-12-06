package enums;

import dataset.Record;
import builder.Wincalc;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eCurrenc;
import domain.eGroups;
import domain.eRulecalc;

//Ўкала ценообразовани€
public enum Scale {
    systreeK(1, "коэффициент рентабельности"), //SYSTREE 

    artiklK(1, "kоэф. наценки группы мат.ценностей"), //ARTIKL
    artiklS(0, "проц. скидки группы мат.ценностей"), //ARTIKL
    artiklW(0, "проц. отхода мат.ценностей"), //ARTIKL

    artdetT1(0, "тариф основн. текстуры"), //ARTDET
    artdetT2(0, "тариф внутр. текстуры"), //ARTDET 
    artdetT3(0, "тариф внешн. текстуры"), //ARTDET 
    artdet2T(0, "тариф двухст. текстуры"), //ARTDET 
    artdetK(1, "kоэф. ценовой текстуры"), //ARTDET 

    grpcursK1(1, "коэф. курс валют основной текстуры"), //GROUPS 
    grpcursK2(1, "коэф. курс валют неосновных текстур"), //GROUPS     
    grpformN1(0, "проц. наценка на изд. сложной формы"), //GROUPS 
    grpformN2(0, "проц. наценка на изд. непр€моуг-ми коробками"), //GROUPS 
    grpcolorK(1, "kоэф. группы текстуры"), //GROUPS

    colorK1(1, "коэф. основной текстуры"), //COLOR 
    colorK2(1, "коэф. внутренний текстуры"), //COLOR 
    colorK3(1, "коэф. внешний текстуры"), //COLOR 

    rulecalcK(1, "коэф. правил расчЄта"), //RULECALC 
    rulecalcN(0, "руб. надб. правил расчЄта");  //RULECALC 

    public double v;
    public String s;

    Scale(double v, String s) {

        this.v = v;
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
                grpformN1.v = eGroups.find(2101).getDbl(eGroups.val);

            } else if (Type.RECTANGL != winc.root.type) {
                grpformN1.v = eGroups.find(2104).getDbl(eGroups.val);
            } else {
                grpformN1.v = 0;
            }
            grpcolorK.v = groupsRec.getDbl(eGroups.val); //коэф. группы текстур         
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
