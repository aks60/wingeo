package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import dataset.Record;
import domain.eArtikl;
import domain.eColor;
import enums.Type;
import enums.TypeJoin;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.LinkedList;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

public class AreaRectangl extends AreaSimple {

    public AreaRectangl(Wincalc winc, GsonElem gson) {
        super(winc, gson, null);
        this.owner = this;
    }

    //Полигон рамы. Функ. выпоняется после создания рам конструкции
    @Override
    public void setLocation() {
        try {
            ArrayList<Coordinate> coo = new ArrayList<Coordinate>();
            Record artiklRec = (this.frames.get(0).artiklRecAn == null) ? eArtikl.virtualRec() : this.frames.get(0).artiklRecAn;
           // double dh = artiklRec.getDbl(eArtikl.height);

            //Вершины рамы
            this.frames.forEach(frame -> coo.add(new Coordinate(frame.x1(), frame.y1(), frame.id)));
            coo.add(new Coordinate(this.frames.get(0).x1(), this.frames.get(0).y1(), this.frames.get(0).id));

            //Аrea рамы
            Polygon geo1 = gf.createPolygon(coo.toArray(new Coordinate[0]));
            //Polygon geo2 = (Polygon) UGeo.geoBuffer(geo1, this.frames);
            Polygon geo2 = UGeo.geoPadding(geo1, this.frames, 0);
            this.area = gf.createMultiPolygon(new Polygon[]{geo1, geo2});

        } catch (Exception e) {
            System.err.println("Ошибка:AreaRectangl.setLocation" + toString() + e);
        }
    }

    //L - соединения
    @Override
    public void joining() {
        try {
            winc.listJoin.clear();

            super.joining(); //T - соединения

            //L - соединения
            for (int i = 0; i < this.frames.size(); i++) { //цикл по сторонам рамы
                ElemFrame nextFrame = (ElemFrame) this.frames.get((i == this.frames.size() - 1) ? 0 : i + 1);
                winc.listJoin.add(new ElemJoining(this.winc, TypeJoin.ANGL, this.frames.get(i), nextFrame));
            }
        } catch (Exception e) {
            System.err.println("AreaRectangl.joining() " + e);
        }
    }

    @Override
    public void paint() {
        super.paint();

//        if (this.area != null) {
//            Shape shape = new ShapeWriter().toShape(this.area);
//
//            winc.gc2d.setColor(new java.awt.Color(eColor.find(this.colorID2).getInt(eColor.rgb)));
//            winc.gc2d.fill(shape);
//
//            winc.gc2d.setColor(new java.awt.Color(000, 000, 000));
//            winc.gc2d.draw(shape);
//        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    // </editor-fold>     
}
