package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import com.google.gson.JsonObject;
import domain.eArtikl;
import domain.eSysprof;
import domain.eSyssize;
import enums.PKjson;
import enums.UseSide;
import java.awt.Shape;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;

public class ElemFrame extends ElemSimple {

    public ElemFrame(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
        initСonstructiv(gson.param);
        mouseEvent();
    }

    /**
     * Профиль через параметр или первая запись в системе см. табл. sysprof Цвет
     * если нет параметра то берём winc.color.
     */
    public void initСonstructiv(JsonObject param) {

        colorID1 = (isJson(param, PKjson.colorID1)) ? param.get(PKjson.colorID1).getAsInt() : winc.colorID1;
        colorID2 = (isJson(param, PKjson.colorID2)) ? param.get(PKjson.colorID2).getAsInt() : winc.colorID2;
        colorID3 = (isJson(param, PKjson.colorID3)) ? param.get(PKjson.colorID3).getAsInt() : winc.colorID3;

        if (isJson(param, PKjson.sysprofID)) { //профили через параметр
            sysprofRec = eSysprof.find3(param.get(PKjson.sysprofID).getAsInt());

        } else if (owner.sysprofRec != null) { //профили через параметр рамы, створки
            sysprofRec = owner.sysprofRec;
        } else {
            sysprofRec = eSysprof.find4(winc.nuni, type.id2, UseSide.ANY);
        }
        artiklRec = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), false);
        artiklRecAn = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), true);

        //Системные константы как правило на всю систему профилей
        if (winc.syssizeRec == null) {
            winc.syssizeRec = eSyssize.find(artiklRec);
        }
    }

    public void setLocation() {
        try {
            anglHoriz = UGeo.horizontAngl(this);
            for (int i = 0; i < winc.listFrame.size(); i++) {
                if (winc.listFrame.get(i).id == this.id) {

                    int k = (i == 0) ? winc.listFrame.size() - 1 : i - 1;
                    int j = (i == (winc.listFrame.size() - 1)) ? 0 : i + 1;
                    ElemSimple e0 = winc.listFrame.get(k);
                    ElemSimple e1 = winc.listFrame.get(j);

                    double h0[] = UGeo.diffOnAngl(UGeo.horizontAngl(this), this.artiklRec.getDbl(eArtikl.height) - this.artiklRec.getDbl(eArtikl.size_centr));
                    double h1[] = UGeo.diffOnAngl(UGeo.horizontAngl(e0), e0.artiklRec.getDbl(eArtikl.height) - e0.artiklRec.getDbl(eArtikl.size_centr));
                    double h2[] = UGeo.diffOnAngl(UGeo.horizontAngl(e1), e1.artiklRec.getDbl(eArtikl.height) - e1.artiklRec.getDbl(eArtikl.size_centr));
                    double p1[] = UGeo.crossOnLine(
                            x1() + h0[0], y1() + h0[1], x2() + h0[0], y2() + h0[1],
                            e0.x1() + h1[0], e0.y1() + h1[1], e0.x2() + h1[0], e0.y2() + h1[1]);
                    double p2[] = UGeo.crossOnLine(
                            x1() + h0[0], y1() + h0[1], x2() + h0[0], y2() + h0[1],
                            e1.x1() + h2[0], e1.y1() + h2[1], e1.x2() + h2[0], e1.y2() + h2[1]);

                    this.geom = UJts.createPolygon(x1(), y1(), x2(), y2(), p2[0], p2[1], p1[0], p1[1]);

                    Coordinate[] shell = new Coordinate[]{
                        new Coordinate(x1(), y1()), new Coordinate(x2(), y2()),
                        new Coordinate(p2[0], p2[1]), new Coordinate(p1[0], p1[1]), new Coordinate(x1(), y1())
                    };
                    this.geom = gf.createPolygon(shell);
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка:ElemFrame.setLocation()" + toString() + e);
        }
    }

    public void paint() {
        if (this.geom != null) {
            Shape shape = new ShapeWriter().toShape(this.geom);
            winc.gc2d.draw(shape);
        }
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
