package builder.model2;

import builder.Wincalc;
import builder.script.GeoElem;
import com.google.gson.JsonObject;
import domain.eArtikl;
import domain.eColor;
import static domain.eColor.rgb;
import domain.eSysprof;
import domain.eSyssize;
import enums.PKjson;
import enums.UseSide;
import frames.swing.DrawStroke;
import java.awt.Polygon;
import java.awt.geom.PathIterator;
import java.util.List;

public class ElemFrame extends ElemSimple {

    public ElemFrame(Wincalc wing, GeoElem gson, Com5t owner) {
        super(wing, gson, owner);
        initСonstructiv(gson.param);
        mouseEvent();
    }

    /**
     * Профиль через параметр или первая запись в системе см. табл. sysprof Цвет
     * если нет параметра то берём wing.color.
     */
    public void initСonstructiv(JsonObject param) {

        colorID1 = (isJson(param, PKjson.colorID1)) ? param.get(PKjson.colorID1).getAsInt() : wing.colorID1;
        colorID2 = (isJson(param, PKjson.colorID2)) ? param.get(PKjson.colorID2).getAsInt() : wing.colorID2;
        colorID3 = (isJson(param, PKjson.colorID3)) ? param.get(PKjson.colorID3).getAsInt() : wing.colorID3;

        if (isJson(param, PKjson.sysprofID)) { //профили через параметр
            sysprofRec = eSysprof.find3(param.get(PKjson.sysprofID).getAsInt());

        } else if (owner.sysprofRec != null) { //профили через параметр рамы, створки
            sysprofRec = owner.sysprofRec;
        } else {
            sysprofRec = eSysprof.find4(wing.nuni, type.id2, UseSide.ANY);
        }
        artiklRec = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), false);
        artiklRecAn = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), true);

        //Системные константы как правило на всю систему профилей
        if (wing.syssizeRec == null) {
            wing.syssizeRec = eSyssize.find(artiklRec);
        }
    }

    public void setLocation() {
        try {
            anglHoriz = UGeo.horizontAngl(this);
            for (int i = 0; i < wing.listFrame.size(); i++) {
                if (wing.listFrame.get(i).id == this.id) {
                    
                    int k = (i == 0) ? wing.listFrame.size() - 1 : i - 1;
                    int j = (i == (wing.listFrame.size() - 1)) ? 0 : i + 1;
                    ElemSimple e0 = wing.listFrame.get(k);
                    ElemSimple e1 = wing.listFrame.get(j);

                    double h0[] = UGeo.diffOnAngl(UGeo.horizontAngl(this), this.artiklRec.getDbl(eArtikl.height) - this.artiklRec.getDbl(eArtikl.size_centr));
                    double h1[] = UGeo.diffOnAngl(UGeo.horizontAngl(e0), e0.artiklRec.getDbl(eArtikl.height) - e0.artiklRec.getDbl(eArtikl.size_centr));
                    double h2[] = UGeo.diffOnAngl(UGeo.horizontAngl(e1), e1.artiklRec.getDbl(eArtikl.height) - e1.artiklRec.getDbl(eArtikl.size_centr));
                    double p1[] = UGeo.crossOnLine(
                            x1() + h0[0], y1() + h0[1], x2() + h0[0], y2() + h0[1], 
                            e0.x1() + h1[0], e0.y1() + h1[1], e0.x2() + h1[0], e0.y2() + h1[1]);
                    double p2[] = UGeo.crossOnLine(
                            x1() + h0[0], y1() + h0[1], x2() + h0[0], y2() + h0[1], 
                            e1.x1() + h2[0], e1.y1() + h2[1], e1.x2() + h2[0], e1.y2() + h2[1]);
                    
                    this.area = UGeo.rectangl(x1(), y1(), x2(), y2(), p2[0], p2[1], p1[0], p1[1]);
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка:Elem2Frame.setLocation()" + toString() + e);
        }
    }

    public void paint() {
        wing.gc2D.draw(area);
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    @Override
    public double x2() {
        return (enext.gson.x1 != null) ? enext.gson.x1 : -1;
    }

    @Override
    public double y2() {
        return (enext.gson.y1 != null) ? enext.gson.y1 : -1;
    }

    @Override
    public void x2(double v) {
        enext.gson.x1 = v;
    }

    @Override
    public void y2(double v) {
        enext.gson.y1 = v;
    }
    // </editor-fold>     
}
