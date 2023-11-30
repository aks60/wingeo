package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import com.google.gson.JsonObject;
import domain.eArtikl;
import domain.eColor;
import domain.eSysprof;
import enums.Layout;
import enums.PKjson;
import enums.UseSide;
import java.awt.Shape;
import java.awt.geom.Line2D;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.GeometryFixer;

public class ElemCross extends ElemSimple {

    public ElemCross(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
        initСonstructiv(gson.param);
        systemEvent();
    }

    public void initСonstructiv(JsonObject param) {

        colorID1 = (isJson(param, PKjson.colorID1)) ? param.get(PKjson.colorID1).getAsInt() : winc.colorID1;
        colorID2 = (isJson(param, PKjson.colorID2)) ? param.get(PKjson.colorID2).getAsInt() : winc.colorID2;
        colorID3 = (isJson(param, PKjson.colorID3)) ? param.get(PKjson.colorID3).getAsInt() : winc.colorID3;

        double angl = UGeo.anglHor(this);
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
            //Пилим полигон импостом
            Geometry[] geoSplit = UGeo.geoSplit(owner.geom, this.x1(), this.y1(), this.x2(), this.y2());
            Polygon geo1 = (Polygon) geoSplit[1];
            Polygon geo2 = (Polygon) geoSplit[2];
            owner.childs.get(0).geom = geo1;
            owner.childs.get(2).geom = geo2;

            //Новые координаты импоста
            Geometry lineImp = owner.geom.intersection(geoSplit[0]);
            if (lineImp.getGeometryType().equals("MultiLineString")) { //исправление коллизий
                int index = (lineImp.getGeometryN(0).getLength() > lineImp.getGeometryN(1).getLength()) ? 0 : 1;
                lineImp = lineImp.getGeometryN(index);
            }
            //Присваиваю нов. коорд.
            this.setDimension(lineImp.getCoordinates()[0].x, lineImp.getCoordinates()[0].y, lineImp.getCoordinates()[1].x, lineImp.getCoordinates()[1].y);

            //Внутренняя ареа       
            Polygon geoPadding = UGeo.geoPadding(owner.geom, winc.listElem);
            if (geoPadding.isValid() == false) { //исправление коллизий
                GeometryFixer fix = new GeometryFixer(geoPadding);
                geoPadding = (Polygon) fix.getResult().getGeometryN(0);
            }

            //Левый и правый сегмент вдоль импоста
            double delta = this.artiklRec.getDbl(eArtikl.height) - this.artiklRec.getDbl(eArtikl.size_centr); //ширина
            LineSegment baseSegm = new LineSegment(new Coordinate(this.x1(), this.y1()), new Coordinate(this.x2(), this.y2()));
            LineSegment moveBaseSegment[] = {baseSegm.offset(+delta), baseSegm.offset(-delta)};

            //Точки пересечения канвы сегментами импоста
            Polygon areaCanvas = UGeo.newPolygon(0, 0, 0, 10000, 10000, 10000, 10000, 0);
            Coordinate C1[] = UGeo.geoIntersect(areaCanvas, moveBaseSegment[0]);
            Coordinate C2[] = UGeo.geoIntersect(areaCanvas, moveBaseSegment[1]);

            //Ареа импоста обрезаем areaPadding 
            Polygon areaExp = UGeo.newPolygon(C2[0].x, C2[0].y, C1[0].x, C1[0].y, C1[1].x, C1[1].y, C2[1].x, C2[1].y);
            this.geom = (Polygon) areaExp.intersection(geoPadding);

        } catch (Exception e) {
            System.err.println("Ошибка:ElemCross.setLocation() " + e);
        }
    }

    public Layout layout() {
        double angl = this.anglHoriz();
        if (angl == 90) {
            return Layout.VERT;
        } else if (angl == 0) {
            return Layout.HORIZ;
        } else if (angl == -90) {
            return Layout.VERT;
        } else if (angl == 180) {
            return Layout.HORIZ;
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
            winc.gc2d.draw(new Line2D.Double(this.x1(), this.y1(), this.x2(), this.y2()));
            winc.gc2d.draw(shape);
            winc.gc2d.setColor(color);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    // </editor-fold>       
}
