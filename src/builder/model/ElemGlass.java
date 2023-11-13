package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import com.google.gson.JsonObject;
import dataset.Record;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eSystree;
import enums.PKjson;

public class ElemGlass extends ElemSimple {

    public double radiusGlass = 0; //радиус стекла
    public double gzazo = 0; //зазор между фальцем и стеклопакетом 
    public double gsize[] = {0, 0, 0, 0}; //размер от оси до стеклопакета

    public Record rasclRec = eArtikl.virtualRec(); //раскладка
    public int rasclColor = -3; //цвет раскладки
    public int rasclNumber[] = {2, 2}; //количество проёмов раскладки     

    public ElemGlass(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
    }

    public void initСonstructiv(JsonObject param) {

        if (isJson(param, PKjson.artglasID)) {
            artiklRec = eArtikl.find(param.get(PKjson.artglasID).getAsInt(), false);
        } else {
            Record sysreeRec = eSystree.find(winc.nuni); //по умолчанию стеклопакет
            artiklRec = eArtikl.find2(sysreeRec.getStr(eSystree.glas));
        }
        artiklRecAn = artiklRec;

        //Цвет стекла
        if (isJson(param, PKjson.colorGlass)) {
            colorID1 = param.get(PKjson.colorGlass).getAsInt();
        } else {
            Record artdetRec = eArtdet.find(artiklRec.getInt(eArtikl.id));
            Record colorRec = eColor.find3(artdetRec.getInt(eArtdet.color_fk));
            colorID1 = colorRec.getInt(eColor.id);
            colorID2 = colorRec.getInt(eColor.id);
            colorID3 = colorRec.getInt(eColor.id);
        }

        //Раскладка
        if (isJson(param, PKjson.artiklRascl)) {
            rasclRec = eArtikl.find(param.get(PKjson.artiklRascl).getAsInt(), false);
            //Текстура
            if (isJson(param, PKjson.colorRascl)) {
                rasclColor = eColor.get(param.get(PKjson.colorRascl).getAsInt()).getInt(eColor.id);
            } else {
                rasclColor = eArtdet.find(rasclRec.getInt(eArtikl.id)).getInt(eArtdet.color_fk); //цвет по умолчанию
            }
            //Проёмы гориз.
            if (isJson(param, PKjson.rasclHor)) {
                rasclNumber[0] = param.get(PKjson.rasclHor).getAsInt();
            }
            //Проёмы вертик.
            if (isJson(param, PKjson.rasclVert)) {
                rasclNumber[1] = param.get(PKjson.rasclVert).getAsInt();
            }
        }
    }

    public void setLocation() {
        setDimension(owner.x1(), owner.y1(), owner.x2(), owner.y2());
    }

    public void paint() {
        winc.gc2d.fillPolygon(new int[]{(int) this.x1(), (int) this.x2(), (int) this.x2(), (int) this.x1()},
                new int[]{(int) this.y1(), (int) this.y1(), (int) this.y2(), (int) this.y2()}, 4);
    }
}
