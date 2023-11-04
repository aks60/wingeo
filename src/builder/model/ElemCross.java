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
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

public class ElemCross extends ElemSimple {

    Polygon geoTest = null;

    public ElemCross(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
        initСonstructiv(gson.param);
        mouseEvent();
    }

    public void initСonstructiv(JsonObject param) {

        colorID1 = (isJson(param, PKjson.colorID1)) ? param.get(PKjson.colorID1).getAsInt() : winc.colorID1;
        colorID2 = (isJson(param, PKjson.colorID2)) ? param.get(PKjson.colorID2).getAsInt() : winc.colorID2;
        colorID3 = (isJson(param, PKjson.colorID3)) ? param.get(PKjson.colorID3).getAsInt() : winc.colorID3;

        double angl = UJts.anglHor(this);
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
            Geometry[] arrGeo = UJts.splitCanvas(this.x1(), this.y1(), this.x2(), this.y2(), 10000, 10000);

            //Новые координаты импоста
            Coordinate[] newImp = arrGeo[0].getCoordinates();
            this.setDimension(newImp[0].x, newImp[0].y, newImp[1].x, newImp[1].y);

            //Возвращает area слева и справа от импоста
            Polygon area1 = (Polygon) arrGeo[1];
            Polygon area2 = (Polygon) arrGeo[2];
            owner.childs().get(0).geom = area1;
            owner.childs().get(2).geom = area2;

            //Общий сегменты от совместнго между area1 и area2
            //Coordinate[] segm = owner.childs().get(0).geom.intersection(owner.childs().get(2).geom).getCoordinates();
            //Ширина импоста
            double W[] = UJts.diffOnAngl(UJts.anglHor(this), this.artiklRec.getDbl(eArtikl.height) - this.artiklRec.getDbl(eArtikl.size_centr));

            //Внутренняя ареа       
            Polygon areaPadding = UJts.areaPadding(owner.geom, winc.listElem);

            //Находим точки пересечение внутр. ареа левым и правым сегментами импоста
            Coordinate C1[] = UJts.intersectPoligon(areaPadding, this.x1() - W[0], this.y1() + W[1], this.x2() - W[0], this.y2() + W[1]);
            Coordinate C2[] = UJts.intersectPoligon(areaPadding, this.x1() + W[0], this.y1() - W[1], this.x2() + W[0], this.y2() - W[1]);

            if (C1 != null && C2 != null) {
                this.geom = UJts.newPolygon(C2[0].x, C2[0].y, C1[0].x, C1[0].y, C1[1].x, C1[1].y, C2[1].x, C2[1].y);
            } else {
                System.out.println("NULL 2 builder.model.ElemCross.setLocation()");
            }

        } catch (Exception e) {
            //this.geom = null;
            System.err.println("Ошибка:ElemCross.setLocation() " + e);
        }
    }

    public void paint() {
        try {
            if (this.geom != null) {
                Shape shape = new ShapeWriter().toShape(this.geom);
                winc.gc2d.draw(shape);
            }
            winc.gc2d.draw(new Line2D.Double(this.x1(), this.y1(), this.x2(), this.y2()));
            java.awt.Color color = winc.gc2d.getColor();

            if (this.geoTest != null) {
                winc.gc2d.setColor(new java.awt.Color(255, 000, 000));
                Shape shape = new ShapeWriter().toShape(this.geoTest);
                winc.gc2d.draw(shape);
            }
            winc.gc2d.setColor(color);

        } catch (Exception e) {
            System.err.println("Ошибка:ElemCross.paint() " + e);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    // </editor-fold>       
}
