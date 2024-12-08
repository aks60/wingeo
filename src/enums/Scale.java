package enums;

import dataset.Record;
import builder.Wincalc;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eCurrenc;
import domain.eGroups;
import domain.eRulecalc;

//Шкала ценообразования
public enum Scale {

    systreeK(1, " - коэффициент рентабельности"), //SYSTREE     
    grpcursK1(1, " - коэф. курса валют основной текстуры"), //GROUPS 
    grpcursK2(1, " - коэф. курса валют неосновных текстур"), //GROUPS  

    artiklK(1, " - kоэф. наценки группы мат.ценностей"), //ARTIKL
    artiklS(0, " - проц. скидки группы мат.ценностей"), //ARTIKL
    artiklW(0, " - проц. отхода мат.ценностей"), //ARTIKL
    
    separator1(-777, "<html><font size='3' color='blue'> ТАРИФЫ: <font size='2' color='black'> (см. Модели->Артикулы)"),
    artdetT1(0, " - тариф основн. текстуры"), //ARTDET
    artdetT2(0, " - тариф внутр. текстуры"),  //ARTDET 
    artdetT3(0, " - тариф внешн. текстуры"),  //ARTDET 
    artdet2T(0, " - тариф двухст. текстуры"), //ARTDET 	
    separator2(-777, "<html><font size='3' color='blue'> ЦЕНОВЫЕ КОЭФ. ТЕКСТУР:"),
    separator3(-888, "<html><font size='2' color='black'> см. Модели->Артикулы"),
    artdetK1(1, " - kоэф. основн. текстуры"), //ARTDET 
    artdetK2(1, " - kоэф. внутр. текстуры"), //ARTDET 
    artdetK3(1, " - kоэф. внешн. текстуры"), //ARTDET 
    separator4(-888, "<html><font size='2' color='black'> <font size='2' color='black'> см. Справочники->Текстуры"),
    colorK1(1, " - коэф. основной текстуры"), //COLOR 
    colorK2(1, " - коэф. внутренний текстуры"), //COLOR 
    colorK3(1, " - коэф. внешний текстуры"), //COLOR 
    grpcolorK1(1, " - kоэф. группы основн. текстуры"), //GROUPS
    grpcolorK2(1, " - kоэф. группы внутр. текстуры"), //GROUPS
    grpcolorK3(1, " - kоэф. группы внешн. текстуры"), //GROUPS
    
    separator8(-777, "<html><font size='3' color='blue'> ИЗДЕЛИЯ СЛОЖНОЙ ФОРМЫ: <font size='2' color='black'> (см. Настр.->Цен.коэф.)"),
    grpformN1(0, " - проц. наценка на изд. сложной формы"), //GROUPS 
    grpformN2(0, " - проц. наценка на изд. непрямоуг-ми коробками"), //GROUPS 

    separator9(-777, "  ПРАВИЛА РАСЧЁТА: <font size='2' color='black'> (см. Настр.->Цен.коэф.)"),
    rulecalcK(1, " - коэф. правил расчёта"), //RULECALC 
    rulecalcN(0, " - руб. надб. правил расчёта");  //RULECALC 

    public double v;
    public String s;

    Scale(double v, String s) {

        this.v = v;
        this.s = s;
    }

    public static void init(Wincalc winc, Record artiklRec, Record artdetRec[], Record groupsRec[], Record colorRec[], Record rulecalcRec) {

        Record artgrp1Rec = eGroups.find(artiklRec.getInt(eArtikl.groups1_id));
        Record artgrp2Rec = eGroups.find(artiklRec.getInt(eArtikl.groups2_id));
        Record croscurs1Rec = eCurrenc.find(artiklRec.getInt(eArtikl.currenc1_id));
        Record croscurs2Rec = eCurrenc.find(artiklRec.getInt(eArtikl.currenc2_id));

        artiklK.v = artgrp1Rec.getDbl(eGroups.val, 1); //koef. наценка группы мат.ценностей
        artiklS.v = artgrp2Rec.getDbl(eGroups.val, 0); //проц. скидки группы мат.ценностей
        artiklW.v = artiklRec.getDbl(eArtikl.otx_norm, 0); //проц. скидки группы мат.ценностей
        grpcursK1.v = croscurs1Rec.getDbl(eCurrenc.cross_cour); //курс валюты для основной текстуры        
        grpcursK2.v = croscurs2Rec.getDbl(eCurrenc.cross_cour); //курс валюты для неосн. текстур (внутр., внешняя, двухстор.)            

        artdetT1.v = artdetRec[0].getDbl(eArtdet.cost_c1); //тариф основн текстуры
        artdetT2.v = artdetRec[1].getDbl(eArtdet.cost_c2); //тариф внутр текстуры
        artdetT3.v = artdetRec[2].getDbl(eArtdet.cost_c3); //тариф внешн текстуры
        
        artdetK1.v = artdetRec[0].getDbl(eArtdet.coef); //ценовой коэф. основн. текстуры
        artdetK2.v = artdetRec[1].getDbl(eArtdet.coef); //ценовой коэф. внутр. текстуры
        artdetK3.v = artdetRec[2].getDbl(eArtdet.coef); //ценовой коэф. внешн. текстуры 

        if (Type.ARCH == winc.root.type) { //надбавка на изд. сложной формы
            grpformN1.v = eGroups.find(2101).getDbl(eGroups.val);

        } else if (Type.RECTANGL != winc.root.type) {
            grpformN1.v = eGroups.find(2104).getDbl(eGroups.val);
        } else {
            grpformN1.v = 0;
        }
        grpcolorK1.v = groupsRec[0].getDbl(eGroups.val); //коэф. группы текстур         
        grpcolorK2.v = groupsRec[1].getDbl(eGroups.val); //коэф. группы текстур         
        grpcolorK3.v = groupsRec[2].getDbl(eGroups.val); //коэф. группы текстур         

        colorK1.v = colorRec[0].getDbl(eColor.coef1); //ценовой коэф. основн. текстуры
        colorK2.v = colorRec[1].getDbl(eColor.coef2); //ценовой коэф. внутр. текстуры
        colorK3.v = colorRec[2].getDbl(eColor.coef3); //ценовой коэф. внешн. текстуры   

        if (rulecalcRec != null) {
            Scale.rulecalcK.v = rulecalcRec.getDbl(eRulecalc.coeff);
            Scale.rulecalcN.v = rulecalcRec.getDbl(eRulecalc.incr);
        }
    }
}
