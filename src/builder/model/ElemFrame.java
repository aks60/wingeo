package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import com.google.gson.JsonObject;
import domain.eArtikl;
import domain.eColor;
import domain.eSysprof;
import domain.eSyssize;
import enums.PKjson;
import enums.UseSide;
import java.awt.BasicStroke;
import java.awt.Shape;
import org.locationtech.jts.algorithm.Intersection;
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
            for (int i = 0; i < owner.frames.size(); i++) {
                if (owner.frames.get(i).id == this.id) {

                    int k = (i == 0) ? owner.frames.size() - 1 : i - 1;
                    int j = (i == (owner.frames.size() - 1)) ? 0 : i + 1;
                    ElemSimple elem0 = owner.frames.get(k);
                    ElemSimple elem1 = owner.frames.get(j);

                    double h0[] = UGeo.deltaOnAngl(UGeo.anglHor(this), this.artiklRec.getDbl(eArtikl.height) - this.artiklRec.getDbl(eArtikl.size_centr));
                    double h1[] = UGeo.deltaOnAngl(UGeo.anglHor(elem0), elem0.artiklRec.getDbl(eArtikl.height) - elem0.artiklRec.getDbl(eArtikl.size_centr));
                    double h2[] = UGeo.deltaOnAngl(UGeo.anglHor(elem1), elem1.artiklRec.getDbl(eArtikl.height) - elem1.artiklRec.getDbl(eArtikl.size_centr));

                    Coordinate c1 = Intersection.intersection(
                            new Coordinate(this.x1() + h0[0], this.y1() - h0[1]), new Coordinate(this.x2() + h0[0], this.y2() - h0[1]),
                            new Coordinate(elem0.x1() + h1[0], elem0.y1() - h1[1]), new Coordinate(elem0.x2() + h1[0], elem0.y2() - h1[1]));
                    Coordinate c2 = Intersection.intersection(
                            new Coordinate(this.x1() + h0[0], this.y1() - h0[1]), new Coordinate(this.x2() + h0[0], this.y2() - h0[1]),
                            new Coordinate(elem1.x1() + h2[0], elem1.y1() - h2[1]), new Coordinate(elem1.x2() + h2[0], elem1.y2() - h2[1]));

                    this.geom = UGeo.newPolygon(x1(), y1(), x2(), y2(), c2.x, c2.y, c1.x, c1.y);
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка:ElemFrame.setLocation()" + toString() + e);
        }
    }

    public void paint() {      
        if (this.geom != null) {
            java.awt.Color color = winc.gc2d.getColor();
            Shape shape = new ShapeWriter().toShape(this.geom);

            winc.gc2d.setColor(new java.awt.Color(eColor.find(this.colorID2).getInt(eColor.rgb)));
            winc.gc2d.fill(shape);            
            
            winc.gc2d.setColor(new java.awt.Color(000, 000, 000));            
            winc.gc2d.draw(shape);
            winc.gc2d.setColor(color);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    @Override
    public double x2() {
        Coordinate[] coo = this.owner.geom.getCoordinates();
        for (int i = 0; i < coo.length; i++) {
            if (coo[i].x == this.x1() && coo[i].y == this.y1()) {
                return coo[(i == coo.length - 1) ? 0 : i + 1].x;
            }
        }
        return -1;
    }

    @Override
    public double y2() {
        Coordinate[] coo = this.owner.geom.getCoordinates();
        for (int i = 0; i < coo.length; i++) {
            if (coo[i].x == this.x1() && coo[i].y == this.y1()) {
                return coo[(i == coo.length - 1) ? 0 : i + 1].y;
            }
        }
        return -1;
    }

    @Override
    public void x2(double v) {
        for (int i = 0; i < owner.frames.size(); i++) {
            if (owner.frames.get(i).x1() == this.x1() && owner.frames.get(i).y1() == this.y1()) {
                owner.frames.get((i == owner.frames.size() - 1) ? 0 : i + 1).x1(v);
            }
        }
    }

    @Override
    public void y2(double v) {
        for (int i = 0; i < owner.frames.size(); i++) {
            if (owner.frames.get(i).x1() == this.x1() && owner.frames.get(i).y1() == this.y1()) {
                owner.frames.get((i == owner.frames.size() - 1) ? 0 : i + 1).y1(v);
            }
        }
    }
    // </editor-fold>     
}
