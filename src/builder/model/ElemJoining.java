package builder.model;

import builder.Wincalc;
import dataset.Record;
import enums.LayoutJoin;
import enums.TypeJoin;
import builder.making.Specific;
import domain.eArtikl;
import domain.eJoining;
import domain.eJoinvar;
import enums.Layout;
import enums.Type;
import java.util.List;

public class ElemJoining {

    private double id = -1; //идентификатор соединения
    private Wincalc winc;
    public Record joiningRec = eJoining.up.newRecord();
    public Record joinvarRec = eJoinvar.up.newRecord();
    public LayoutJoin layout = LayoutJoin.NONE; //расположение соединения 
    public TypeJoin type = TypeJoin.EMPTY;      //тип соединения (то что пишет )
    public int vid = 0; //вид соединения ("0-Простое L-обр", "1-Крестовое †-обр") или ("0-Простое T-обр", "1-Крестовое †-обр", "2-Сложное Y-обр)

    public ElemFrame elem1 = null;  //элемент соединения 1
    public ElemFrame elem2 = null;  //элемент соединения 2
    public double angl = 90;      //угол между профилями
    public String costs = "";     //трудозатраты, ч/ч.

    public ElemJoining(Wincalc winc, TypeJoin type, LayoutJoin layout, ElemSimple elem1, ElemSimple elem2) {
        this.id = ++winc.genId;
        this.winc = winc;
        this.type = type;
        this.layout = layout;
        elem1.anglHoriz = UGeo.anglHor(elem1);
        elem2.anglHoriz = UGeo.anglHor(elem2);
        this.elem1 = (ElemFrame) elem1;
        this.elem2 = (ElemFrame) elem2;
        //this.angl = UJts.betweenAngl(elem1, elem2);
        if (elem1.owner.type != Type.ARCH && elem2.owner.type != Type.ARCH && TypeJoin.VAR10 != type) {
            elem1.anglCut[1] = angl / 2;
            elem2.anglCut[0] = angl / 2;
        }
    }

    public void addSpecific(Specific spcAdd) { //добавление спесификаций зависимых элементов
        try {
            Specific spcRec = elem1.spcRec;

            String sideCalc = spcAdd.getParam("null", 11072, 12072);
            if (sideCalc != null && "большей".equals(sideCalc)) {
                spcAdd.width = (elem1.length() > elem2.length()) ? elem1.length() : elem2.length();
            } else if (sideCalc != null && "меньшей".equals(sideCalc)) {
                spcAdd.width = (elem1.length() > elem2.length()) ? elem2.length() : elem1.length();
            } else if (sideCalc != null && "общей".equals(sideCalc)) {
                if (elem1.layout == Layout.HORIZ || elem1.layout == Layout.BOTT || elem1.layout == Layout.TOP) {
                    spcAdd.width = (elem1.x1() > elem2.x1()) ? elem1.x1() - elem2.x2() : elem2.x1() - elem1.x2();
                } else if (elem1.layout == Layout.VERT || elem1.layout == Layout.RIGHT || elem1.layout == Layout.LEFT) {
                    spcAdd.width = (elem1.y1() > elem2.y1()) ? elem1.y1() - elem2.y2() : elem2.y1() - elem1.y2();
                }
            }
            spcAdd.count = UPar.to_11030_12060_14030_15040_25060_33030_34060_38030_39060(spcAdd); //кол. ед. с учётом парам. 
            spcAdd.count += UPar.to_11050(spcAdd, this); //кол. ед. с шагом
            spcAdd.width += UPar.to_12050_15050_34051_39020(spcAdd); //поправка мм

            if (List.of(1, 3, 5).contains(spcAdd.artiklRec.getInt(eArtikl.level1))) {
                //spcAdd.width += elem1.length();
                spcAdd.width += spcRec.width;
            }
            UPar.to_12075_34075_39075(elem1, spcAdd); //углы реза
            spcAdd.width = UPar.to_12065_15045_25040_34070_39070(spcAdd); //длина мм       
            spcAdd.width = spcAdd.width * UPar.to_12030_15030_25035_34030_39030(spcAdd);//"[ * коэф-т ]"
            spcAdd.width = spcAdd.width / UPar.to_12040_15031_25036_34040_39040(spcAdd);//"[ / коэф-т ]"
            spcAdd.count = UPar.to_11070_12070_33078_34078(spcAdd); //ставить однократно

            elem1.spcRec.spcList.add(spcAdd);

        } catch (Exception e) {
            System.err.println("Ошибка:ElemJoinning.addSpecific() " + e);
        }
    }

    public String name() {
        if (joiningRec.get(1) != null) {
            String name1 = eArtikl.query().stream().filter(rec -> rec.getInt(eArtikl.id) == elem1.artiklRecAn.getInt(eArtikl.id)).findFirst().orElse(eArtikl.up.newRecord()).getStr(eArtikl.code);
            String name2 = eArtikl.query().stream().filter(rec -> rec.getInt(eArtikl.id) == elem2.artiklRecAn.getInt(eArtikl.id)).findFirst().orElse(eArtikl.up.newRecord()).getStr(eArtikl.code);
            return name1 + " ÷ " + name2;
        }
        return "";
    }

    public boolean equals(Object obj) {
        return id == ((Com5t) obj).id;
    }

    public double id() {
        return id;
    }

    public String toString() {
        return "id=" + id + ",  layout= " + layout + ",  type=" + type + ",  elem1=" + elem1.layout + ",  elem2=" + elem2.layout + ",  " + layout.name;
    }
}
