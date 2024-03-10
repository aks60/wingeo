package builder.model;

import builder.Wincalc;
import static builder.model.Com5t.aff;
import static builder.model.Com5t.gf;
import builder.script.GsonElem;
import dataset.Record;
import domain.eArtikl;
import enums.TypeJoin;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

public class AreaArch extends AreaSimple {

    public AreaArch(Wincalc winc, GsonElem gson) {
        super(winc, gson, null);
        this.owner = this;
    }

    //Полигон рамы. Функ. выпоняется после создания рам конструкции
    @Override
    public void setLocation() {
        ArrayList<Coordinate> list = new ArrayList<Coordinate>();
        Geometry arcA = null, arcB = null;
        try {
            //Создадим вершины арки
            for (ElemSimple frame : this.frames) {
                if (frame.h() != null) {

                    Record artiklRec = (this.frames.get(0).artiklRecAn == null) ? eArtikl.virtualRec() : this.frames.get(0).artiklRecAn;
                    double dh = artiklRec.getDbl(eArtikl.height);
                    LineSegment segm = UGeo.normalize(new LineSegment(frame.x1(), frame.y1(), frame.x2(), frame.y2()));
                    double ANG = Math.toDegrees(segm.angle());

                    if (ANG == 0) {
                        arcB = UGeo.newLineArch(segm.p0.x, segm.p1.x, segm.p0.y, frame.h(), frame.id);  //созд. арки на горизонтали 
                    } else {
                        //Поворот на горизонталь
                        aff.setToRotation(Math.toRadians(-ANG), segm.p0.x, segm.p0.y);
                        segm = UGeo.getSegment((LineString) aff.transform(segm.toGeometry(gf)));//трансформация линии в горизонт
                        arcA = UGeo.newLineArch(segm.p0.x, segm.p1.x, segm.p0.y, frame.h(), frame.id);  //созд. арки на гортзонтали   

                        //Обратный поворот
                        aff.setToRotation(Math.toRadians(ANG), segm.p0.x, segm.p0.y);
                        arcB = aff.transform(arcA);
                    }
                    List.of(arcB.getCoordinates()).forEach(c -> c.setZ(frame.id));
                    list.addAll(List.of(arcB.getCoordinates()));

                } else {
                    list.add(new Coordinate(frame.x1(), frame.y1(), frame.id));
                }
            }
            //list.add(list.get(0));
            Polygon geo1 = UGeo.newPolygon(list);
            Polygon geo2 = UGeo.geoPadding(geo1, this.frames, 0);
            this.area = gf.createMultiPolygon(new Polygon[]{geo1, geo2});

        } catch (Exception e) {
            System.err.println("Ошибка:AreaArch.setLocation" + toString() + e);
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
