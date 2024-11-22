package builder.making;

import dataset.Record;
import domain.eArtikl;
import domain.eColor;
import enums.UseUnit;
import enums.TypeArt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import builder.model.ElemSimple;
import builder.model.UPar;
import java.util.List;

/**
 * Спецификация элемента окна
 */
public class TRecord {

    public ArrayList<TRecord> spcList = new ArrayList<TRecord>(); //список составов, фурнитур и т. д.  
    public HashMap<Integer, String> mapParam = null;  //параметры спецификации
    public ElemSimple elem5e = null;  //элемент пораждающий спецификацию (контейнер)
    public Record variantRec = null;  //вариант в конструктиве
    public Record detailRec = null;  //детализация в конструктиве
    public Record artiklRec = null;  //артикул в детализации конструктива

    public double id = -1; //ID
    public String place = "---";  //Место размешения
    public String name = "-";  //Наименование
    public String artikl = "-";  //Артикул
    public int colorID1 = -3;  //Осн.текстура
    public int colorID2 = -3;  //Внутр.текстура
    public int colorID3 = -3;  //Внешн.текстура
    public double width = -1;  //Длина
    public double height = -1;  //Ширина
    public double weight = -1;  //Масса
    public double anglCut0 = -1;  //Угол1
    public double anglCut1 = -1;  //Угол2
    public double anglHoriz = 0; // Угол к горизонту    
    public double count = 1;  //Кол. единиц
    public int unit = 0;  //Ед.изм   
    public double waste = -1;  //Процент отхода см. eArtikl.otx_norm     
    public double quant1 = 0;  //Количество без отхода
    public double quant2 = 0;  //Количество с отходом
    public double sebes1 = 0;  //Себест. за ед. без отхода     
    public double sebes2 = 0;  //Себест. за ед. с отходом
    public double price1 = 0;  //Стоимость без скидки
    public double price2 = 0;  //Стоимость с технологической скидкой

    public TRecord() {
    }

    public TRecord(TRecord spec) {
        this.id = spec.id;
        this.place = spec.place;
        this.artikl = spec.artikl;
        this.artiklRec = spec.artiklRec;
        this.detailRec = spec.detailRec;
        this.name = spec.name;
        this.colorID1 = spec.colorID1;
        this.colorID2 = spec.colorID2;
        this.colorID3 = spec.colorID3;
        this.width = spec.width;
        this.height = spec.height;
        this.weight = spec.weight;
        this.anglCut0 = spec.anglCut0;
        this.anglCut1 = spec.anglCut1;
        this.count = spec.count;
        this.unit = spec.unit;
        this.quant1 = spec.quant1;
        this.waste = spec.waste;
        this.quant2 = spec.quant2;
        this.sebes1 = spec.sebes1;
        this.sebes2 = spec.sebes2;
        this.price1 = spec.price1;
        this.price2 = spec.price2;
        this.anglHoriz = spec.anglHoriz;
        this.mapParam = spec.mapParam;
        this.elem5e = spec.elem5e;
    }

    public TRecord(double id, ElemSimple elem5e) {
        ++elem5e.winc.nppID;
        this.id = id;
        this.elem5e = elem5e;
        this.mapParam = new HashMap<Integer, String>();
    }

    public TRecord(String place, Record detailRec, Record artiklRec, ElemSimple elem5e, HashMap<Integer, String> mapParam) {
        this.id = ++elem5e.winc.nppID;
        this.elem5e = elem5e;
        this.mapParam = mapParam;
        this.detailRec = detailRec;
        this.place = place;
        artiklRec(artiklRec);
    }

    public TRecord(String place, double id, Record detailRec, Record artiklRec, HashMap<Integer, String> mapParam) {
        this.id = id;
        this.mapParam = mapParam;
        this.detailRec = detailRec;
        this.place = place;
        artiklRec(artiklRec);
    }

    public void artiklRec(Record artiklRec) {
        this.artikl = artiklRec.getStr(eArtikl.code);
        this.name = artiklRec.getStr(eArtikl.name);
        this.waste = artiklRec.getDbl(eArtikl.otx_norm);
        this.unit = artiklRec.getInt(eArtikl.unit);
        this.artiklRec = artiklRec;
    }

    public Record artiklRec() {
        return artiklRec;
    }

    public void color(int colorID1, int colorID2, int colorID3) {
        this.colorID1 = colorID1;
        this.colorID2 = colorID2;
        this.colorID3 = colorID3;
    }

    public void copy(TRecord spec) {
        this.id = spec.id;
        this.place = spec.place;
        this.artikl = spec.artikl;
        this.artiklRec = spec.artiklRec;
        this.detailRec = spec.detailRec;
        this.name = spec.name;
        this.colorID1 = spec.colorID1;
        this.colorID2 = spec.colorID2;
        this.colorID3 = spec.colorID3;
        this.width = spec.width;
        this.height = spec.height;
        this.weight = spec.weight;
        this.anglCut0 = spec.anglCut0;
        this.anglCut1 = spec.anglCut1;
        this.count = spec.count;
        this.unit = spec.unit;
        this.quant1 = spec.quant1;
        this.waste = spec.waste;
        this.quant2 = spec.quant2;
        this.sebes1 = spec.sebes1;
        this.sebes2 = spec.sebes2;
        this.price1 = spec.price1;
        this.price2 = spec.price2;
        this.anglHoriz = spec.anglHoriz;
        this.mapParam = spec.mapParam;
        this.elem5e = spec.elem5e;
    }

    public Vector getVector(int npp) {
        double elemID = (elem5e == null) ? 0 : elem5e.id;
        double height2 = (elem5e != null && elem5e.h() != null && artiklRec != null && artiklRec.getInt(eArtikl.level1) == 1 && artiklRec.getInt(eArtikl.level2) == 1)
                ? (Math.pow(elem5e.winc.width() / 2, 2) + Math.pow(elem5e.h(), 2)) / (2 * elem5e.h()) : height;

        //System.out.println(height2);
        return new Vector(List.of(npp, id, elemID, place, artikl, name, eColor.find(colorID1).getStr(eColor.name), eColor.find(colorID2).getStr(eColor.name),
                eColor.find(colorID3).getStr(eColor.name), width, height2, weight, anglCut0, anglCut1, anglHoriz,
                count, UseUnit.getName(unit), waste, quant1, quant2, sebes1, sebes2, price1, price2));
    }

    public void setAnglCut(double angl0, double angl1) {
        this.anglCut0 = angl0;
        this.anglCut1 = angl1;
    }

    public String getParam(Object def, int... p) {
        return UPar.getParam(def, mapParam, p);
    }

    public static void write_csv(ArrayList<TRecord> spcList) {
        //См. историю
    }

    public static void write_txt(ArrayList<TRecord> specList) {
        //См. историю
    }

    public boolean equals(Object obj) {
        if (obj instanceof TRecord) {
            return this.id == ((TRecord) obj).id;
        } else {
            return super.equals(obj);
        }
    }

    public String toString() {
        return artikl + " - " + name;
    }
}
