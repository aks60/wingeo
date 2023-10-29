package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import com.google.gson.JsonObject;
import domain.eArtikl;
import domain.eSysprof;
import enums.PKjson;
import enums.UseSide;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

public class ElemCross extends ElemSimple {

    public Area areaTest1 = null;
    public Area areaTest2 = null;
    public Line2D.Double lineTest1 = null;
    public Line2D.Double lineTest2 = null;
    public Line2D.Double lineTest3 = null;

    public ElemCross(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
        initСonstructiv(gson.param);
        mouseEvent();
    }

    public void initСonstructiv(JsonObject param) {

        colorID1 = (isJson(param, PKjson.colorID1)) ? param.get(PKjson.colorID1).getAsInt() : winc.colorID1;
        colorID2 = (isJson(param, PKjson.colorID2)) ? param.get(PKjson.colorID2).getAsInt() : winc.colorID2;
        colorID3 = (isJson(param, PKjson.colorID3)) ? param.get(PKjson.colorID3).getAsInt() : winc.colorID3;

        double angl = UGeo.horizontAngl(this);
        if (isJson(param, PKjson.sysprofID)) { //профили через параметр
            sysprofRec = eSysprof.find3(param.get(PKjson.sysprofID).getAsInt());
        } else {
            sysprofRec = eSysprof.find4(winc.nuni, type.id2, UseSide.ANY);
        }
        artiklRec = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), false);
        artiklRecAn = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), true);
    }

    public void setLocation() {
        try {
            anglHoriz = Angle.angle(new Coordinate(this.x1(), this.y1()), new Coordinate(this.x2(), this.y2()));
            double w = owner.geom.getEnvelopeInternal().getWidth();
            double h = owner.geom.getEnvelopeInternal().getHeight();
            
            //Пересечение канвы вектором импоста. 
            //Возвращает area слева и справа от импоста
            Geometry dblPoly = UJts.splitPolygon(UJts.newPolygon(0, 0, w, 0, w, h, 0, h), this.x1(), this.y1(), this.x2(), this.y2());

            Geometry P1 = dblPoly.getGeometryN(0);
            Geometry P2 = dblPoly.getGeometryN(1);
            
            Polygon area1 = (Polygon) owner.geom.copy();
            Polygon area2 = (Polygon) owner.geom.copy();
            area1.intersection(P1);
            area2.intersection(P2);

            owner.childs().get(0).geom = area1;
            owner.childs().get(2).geom = area2;

            //Предыдущий и последующий сегмент от совместнго между area1 и area2
            LineSegment lineQue[] = UJts.prevAndNextSegment(area1, area2);

            if (lineQue != null) {
                this.setDimension(lineQue[2].p0.x, lineQue[2].p0.y, lineQue[2].p1.x, lineQue[2].p0.y);
                
                //Ширина импоста
                double W[] = UGeo.diffOnAngl(UGeo.horizontAngl(this), 
                        this.artiklRec.getDbl(eArtikl.height) - this.artiklRec.getDbl(eArtikl.size_centr));

                //Находим пересечение канвы сегментами импоста
                double L1[] = UJts.crossCanvas(this.x1() + W[0], this.y1() + W[1], this.x2() + W[0], this.y2() + W[1], w, h);
                double L2[] = UJts.crossCanvas(this.x1() - W[0], this.y1() - W[1], this.x2() - W[0], this.y2() - W[1], w, h);
                
                //Расширенная area импоста между канвой
                Polygon areaClip = UJts.newPolygon(L1[0], L1[1], L1[2], L1[3], L2[2], L2[3], L2[0], L2[1]);

                //Area импоста внутренняя       
                Polygon areaPadding = UJts.areaPadding(owner.geom, winc.listElem);
                

                if (areaClip != null) {
                    areaPadding.intersection(areaClip);
                    this.geom = areaPadding;
                }
            }
        } catch (Exception e) {
            this.geom = null;
            System.err.println("Ошибка:ElemCross.setLocation() " + e);
        }
    }

    public void paint() {
        try {
            java.awt.Color color = winc.gc2d.getColor();

            if (this.areaTest1 != null) {
                winc.gc2d.setColor(new java.awt.Color(255, 000, 000));
                winc.gc2d.draw(this.areaTest1);
            }
            if (this.areaTest2 != null) {
                winc.gc2d.setColor(new java.awt.Color(000, 255, 000));
                winc.gc2d.draw(this.areaTest2);
            }
            if (this.lineTest1 != null) {
                winc.gc2d.setColor(new java.awt.Color(00, 000, 255));
                winc.gc2d.draw(this.lineTest1);
            }
            if (this.lineTest2 != null) {
                winc.gc2d.setColor(new java.awt.Color(00, 000, 255));
                winc.gc2d.draw(this.lineTest2);
            }
            if (this.lineTest3 != null) {
                winc.gc2d.setColor(new java.awt.Color(00, 000, 255));
                winc.gc2d.draw(this.lineTest3);
            }
            winc.gc2d.setColor(color);
            if (this.geom != null) {
                Shape shape = new ShapeWriter().toShape(this.geom);
                winc.gc2d.draw(shape);
            }

        } catch (Exception e) {
            System.err.println("Ошибка:ElemCross.paint() " + e);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    // </editor-fold>       
}
