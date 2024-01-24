package builder.model;

import builder.Wincalc;
import static builder.model.Com5t.aff;
import static builder.model.Com5t.gf;
import builder.script.GsonElem;
import dataset.Record;
import domain.eArtikl;
import domain.eColor;
import enums.Type;
import enums.TypeJoin;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
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
        try {
            //Создадим вершины арки
            for (ElemSimple frame : this.frames) {

                if (frame.h() != null) {
                    Record artiklRec = (this.frames.get(0).artiklRecAn == null) ? eArtikl.virtualRec() : this.frames.get(0).artiklRecAn;
                    double dh = artiklRec.getDbl(eArtikl.height);
                    LineSegment s1 = new LineSegment(frame.x1(), frame.y1(), frame.x2(), frame.y2());
                    s1.normalize();
                    double L = frame.length(), H = frame.h(), ANG = Math.toDegrees(s1.angle());
                    double R = ((ElemFrame) frame).radiusArc = (Math.pow(L / 2, 2) + Math.pow(H, 2)) / (2 * H);  //R = (L2 + H2) / 2H - радиус арки

                    //Ротация в горизонталь
                    aff.setToRotation(Math.toRadians(-ANG), s1.p0.x, s1.p0.y); //угол ротации в горизонт     
                    LineString l1 = (LineString) aff.transform(s1.toGeometry(gf)); //траесформация линии в горизонт
                    l1.normalize();
                    CoordinateSequence cs = l1.getCoordinateSequence();

                    //Внешняя арка на горизонтали
                    LineString arcA = UGeo.newLineArch(cs.getX(0), cs.getX(1), cs.getY(0), H, this.id);  //созд. арки на гортзонтали   
                    List.of(arcA.getCoordinates()).forEach(c -> c.setZ(frame.id));
                    list.addAll(List.of(arcA.getCoordinates()));

                    //Обратная ротация
                    aff.setToRotation(Math.toRadians(ANG), s1.p0.x, s1.p0.y); //угол обратной ротации  

                } else {
                    list.add(new Coordinate(frame.x1(), frame.y1(), frame.id));
                }
            }
            //Полигон векторов рамы
            list.add(list.get(0));

            Polygon geo1 = gf.createPolygon(list.toArray(new Coordinate[0]));
            Polygon geo2 = UGeo.geoPadding(geo1, this.frames, 0);
            this.area = gf.createMultiPolygon(new Polygon[] {geo1, geo2});

        } catch (Exception e) {
            System.err.println("Ошибка:AreaArch.setLocation" + toString() + e);
        }
    }

    //Соединения
    @Override
    public void joining() {
        try {
            winc.listJoin.clear();

            super.joining(); //T - соединения

            ArrayList<ElemSimple> crosList = winc.listElem.filter(Type.IMPOST, Type.SHTULP, Type.STOIKA);
            ArrayList<ElemSimple> elemList = winc.listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.IMPOST, Type.SHTULP, Type.STOIKA);

            //L - соединения
            for (int i = 0; i < this.frames.size(); i++) { //цикл по сторонам рамы
                ElemFrame nextFrame = (ElemFrame) this.frames.get((i == this.frames.size() - 1) ? 0 : i + 1);
                //TypeJoin type = (i == 0 || i == 2) ? TypeJoin.ANG2 :TypeJoin.ANG1; 
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
