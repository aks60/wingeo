package builder.making;

import dataset.Record;
import domain.eArtikl;
import domain.eColor;
import enums.UseUnit;
import enums.TypeArtikl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import builder.model.ElemSimple;
import common.ArraySpc;
import java.util.List;

/**
 * Спецификация элемента окна
 */
public class Specific {

    public ArraySpc<Specific> spcList = new ArraySpc();  //список составов, фурнитур и т.д.
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
    public double width = 0;  //Длина
    public double height = 0;  //Ширина
    public double weight = 0;  //Масса
    public double anglCut0 = 0;  //Угол1
    public double anglCut1 = 0;  //Угол2
    public double anglHoriz = 0; // Угол к горизонту    
    public double count = 1;  //Кол. единиц

    public int unit = 0;  //Ед.изм   
    public double wastePrc = 0;  //Процент отхода см. eArtikl.otx_norm    
    public double quant1 = 0;  //Количество без отхода
    public double quant2 = 0;  //Количество с отходом
    public double costpric1 = 0;  //Себест. за ед. без отхода     
    public double costpric2 = 0;  //Себест. за ед. с отходом
    public double price = 0;  //Стоимость без скидки
    public double cost2 = 0;  //Стоимость с технологической скидкой

    public Specific() {        
    }
    
    public Specific(double id, ElemSimple elem5e) {
        ++elem5e.winc.spcID;
        this.id = id;
        this.elem5e = elem5e;
        this.mapParam = new HashMap();
    }

    public Specific(String place, Record detailRec, Record artiklRec, ElemSimple elem5e, HashMap<Integer, String> mapParam) {
        this.id = ++elem5e.winc.spcID;
        this.elem5e = elem5e;
        this.mapParam = mapParam;
        this.detailRec = detailRec;
        this.place = place;
        setArtikl(artiklRec);
    }

    public Specific(String place, double id, Record detailRec, Record artiklRec, HashMap<Integer, String> mapParam) {
        this.id = id;
        this.mapParam = mapParam;
        this.detailRec = detailRec;
        this.place = place;
        setArtikl(artiklRec);
    }
    
    public void setArtikl(Record artiklRec) {
        this.artikl = artiklRec.getStr(eArtikl.code);
        this.name = artiklRec.getStr(eArtikl.name);
        this.wastePrc = artiklRec.getDbl(eArtikl.otx_norm);
        this.unit = artiklRec.getInt(eArtikl.unit); //atypi;
        this.artiklRec = artiklRec;
        setAnglCut();
    }
    
    public Specific clon(Specific spec) {
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
        this.wastePrc = spec.wastePrc;
        this.quant2 = spec.quant2;
        this.costpric1 = spec.costpric1;
        this.costpric2 = spec.costpric2;
        this.price = spec.price;
        this.cost2 = spec.cost2;
        this.anglHoriz = spec.anglHoriz;
        this.mapParam = spec.mapParam;
        this.elem5e = spec.elem5e;
        return this;
    }

    public Vector getVector(int npp) {
        return new Vector(List.of(npp, id, elem5e.id, place, artikl, name, eColor.find(colorID1).getStr(eColor.name), eColor.find(colorID2).getStr(eColor.name),
                eColor.find(colorID3).getStr(eColor.name), width, height, weight, anglCut0, anglCut1, anglHoriz,
                count, UseUnit.getName(unit), wastePrc, quant1, quant2, costpric1, costpric2, price, cost2));
    }

    public void setColor(int side, int colorID) {
        if (side == 1) {
            colorID1 = colorID;
        } else if (side == 2) {
            colorID2 = colorID;
        } else if (side == 3) {
            colorID3 = colorID;
        }
    }

    protected void setAnglCut() {
        if (TypeArtikl.X109.isType(artiklRec)
                || TypeArtikl.X135.isType(artiklRec)
                || TypeArtikl.X117.isType(artiklRec)
                || TypeArtikl.X136.isType(artiklRec)) {
            anglCut1 = 90;
            anglCut0 = 90;

        } else if (TypeArtikl.X109.isType(artiklRec)) {
            anglCut1 = 0;
            anglCut0 = 0;
        }
    }

    public String getParam(Object def, int... p) {

        if (mapParam != null) {
            for (int index = 0; index < p.length; ++index) {
                int key = p[index];
                String str = mapParam.get(Integer.valueOf(key));
                if (str != null) {
                    return str;
                }
            }
        }
        return String.valueOf(def);
    }

    public static void write_csv(ArrayList<Specific> spcList) {
        //См. историю
    }

    public static void write_txt(ArrayList<Specific> specList) {
        //См. историю
    }

    public String toString() {
        return artikl + " - " + name;
    }
}
