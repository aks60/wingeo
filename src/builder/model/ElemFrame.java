package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import com.google.gson.JsonObject;
import domain.eArtikl;
import domain.eColor;
import domain.eSysprof;
import domain.eSyssize;
import enums.Layout;
import enums.PKjson;
import enums.Type;
import enums.UseSide;
import java.awt.Shape;
import org.locationtech.jts.algorithm.Intersection;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

public class ElemFrame extends ElemSimple {

    public ElemFrame(Wincalc winc, GsonElem gson, AreaSimple owner) {
        this(winc, gson.id, gson, owner);
    }

    public ElemFrame(Wincalc winc, double id, GsonElem gson, AreaSimple owner) {
        super(winc, id, gson, owner);
        initСonstructiv(gson.param);
        if (gson.type == Type.FRAME_SIDE) {
            systemEvent();
        }
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
            if (Layout.BOTT.equals(layout())) {
                sysprofRec = eSysprof.find5(winc.nuni, type.id2, UseSide.BOT, UseSide.HORIZ);
            } else if (Layout.RIGHT.equals(layout())) {
                sysprofRec = eSysprof.find5(winc.nuni, type.id2, UseSide.RIGHT, UseSide.VERT);
            } else if (Layout.TOP.equals(layout())) {
                sysprofRec = eSysprof.find5(winc.nuni, type.id2, UseSide.TOP, UseSide.HORIZ);
            } else if (Layout.LEFT.equals(layout())) {
                sysprofRec = eSysprof.find5(winc.nuni, type.id2, UseSide.LEFT, UseSide.VERT);
            } else {
                sysprofRec = eSysprof.find4(winc.nuni, type.id2, UseSide.ANY);
            }
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
                    ElemSimple e1 = owner.frames.get(k);
                    ElemSimple e2 = owner.frames.get(j);

                    double w0 = this.artiklRec.getDbl(eArtikl.height) - this.artiklRec.getDbl(eArtikl.size_centr);
                    double w1 = e1.artiklRec.getDbl(eArtikl.height) - e1.artiklRec.getDbl(eArtikl.size_centr);
                    double w2 = e2.artiklRec.getDbl(eArtikl.height) - e2.artiklRec.getDbl(eArtikl.size_centr);

                    LineSegment segm0 = new LineSegment(this.x1(), this.y1(), this.x2(), this.y2());
                    LineSegment segm1 = new LineSegment(e1.x1(), e1.y1(), e1.x2(), e1.y2());
                    LineSegment segm2 = new LineSegment(e2.x1(), e2.y1(), e2.x2(), e2.y2());

                    LineSegment segm3 = segm0.offset(-w0);
                    LineSegment segm4 = segm1.offset(-w1);
                    LineSegment segm5 = segm2.offset(-w2);

                    Coordinate c1 = Intersection.intersection(
                            new Coordinate(segm3.p0.x, segm3.p0.y), new Coordinate(segm3.p1.x, segm3.p1.y),
                            new Coordinate(segm4.p0.x, segm4.p0.y), new Coordinate(segm4.p1.x, segm4.p1.y));
                    Coordinate c2 = Intersection.intersection(
                            new Coordinate(segm3.p0.x, segm3.p0.y), new Coordinate(segm3.p1.x, segm3.p1.y),
                            new Coordinate(segm5.p0.x, segm5.p0.y), new Coordinate(segm5.p1.x, segm5.p1.y));

                    this.geom = UGeo.newPolygon(x1(), y1(), x2(), y2(), c2.x, c2.y, c1.x, c1.y);
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка:ElemFrame.setLocation()" + toString() + e);
        }
    }

    public Layout layout() {
        double angl = this.anglHoriz();
        if (angl == 90) {
            return Layout.LEFT;
        } else if (angl == 0) {
            return Layout.BOTT;
        } else if (angl == -90) {
            return Layout.RIGHT;
        } else if (angl == 180) {
            return Layout.TOP;
        }
        return Layout.ANY;
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
        for (int i = 0; i < owner.frames.size(); i++) {
            if (owner.frames.get(i).x1() == this.x1() && owner.frames.get(i).y1() == this.y1()) {
                return owner.frames.get((i == owner.frames.size() - 1) ? 0 : i + 1).x1();
            }
        }
        return -1;
    }

    @Override
    public double y2() {
        for (int i = 0; i < owner.frames.size(); i++) {
            if (owner.frames.get(i).x1() == this.x1() && owner.frames.get(i).y1() == this.y1()) {
                return owner.frames.get((i == owner.frames.size() - 1) ? 0 : i + 1).y1();
            }
        }
        return -1;
    }

    @Override
    public void x2(double v) {
        for (int i = 0; i < owner.frames.size(); i++) {
            if (owner.frames.get(i).x1() == this.x1() && owner.frames.get(i).y1() == this.y1()) {
                owner.frames.get((i == owner.frames.size() - 1) ? 0 : i + 1).x1(v);
                return;
            }
        }
    }

    @Override
    public void y2(double v) {
        for (int i = 0; i < owner.frames.size(); i++) {
            if (owner.frames.get(i).x1() == this.x1() && owner.frames.get(i).y1() == this.y1()) {
                owner.frames.get((i == owner.frames.size() - 1) ? 0 : i + 1).y1(v);
                return;
            }
        }
    }
    // </editor-fold>     
}
