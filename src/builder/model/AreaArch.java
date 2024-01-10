package builder.model;

import builder.Wincalc;
import static builder.model.Com5t.gf;
import builder.script.GsonElem;
import domain.eColor;
import enums.Type;
import enums.TypeJoin;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.util.GeometricShapeFactory;

public class AreaArch extends AreaSimple {

    private Geometry arc = null;

    public AreaArch(Wincalc winc, GsonElem gson) {
        super(winc, gson, null);
        this.owner = this;
    }

    //Полигон рамы. Функ. выпоняется после создания рам конструкции
    @Override
    public void setLocation() {
        ArrayList<Coordinate> coo = new ArrayList<Coordinate>();
        try {
            //Создадим вершины арки
            for (int i = 0; i < this.frames.size(); i++) {
                ElemFrame frame = (ElemFrame) this.frames.get(i);

                if (frame.h() != null) {
                    GeometricShapeFactory gsf = new GeometricShapeFactory();
                    double L = frame.length(), H = frame.h();
                    double R = frame.radiusArc = (Math.pow(L / 2, 2) + Math.pow(H, 2)) / (2 * H);  //R = (L2 + H2) / 2H - радиус арки

                    double ang1 = Math.PI / 2 - Math.asin(L / (R * 2));
                    //gsf.setNumPoints(1000);
                    gsf.setSize(2 * R);
                    gsf.setBase(new Coordinate(L / 2 - R, 0));
                    LineString arch = gsf.createArc(Math.PI + ang1, Math.PI - 2 * ang1).reverse();
                    List.of(arch.getCoordinates()).forEach(c -> c.setZ(frame.id));
                    coo.addAll(List.of(arch.getCoordinates()));

                } else {
                    coo.add(new Coordinate(frame.x1(), frame.y1(), frame.id));
                }
            }
            //Полигон векторов рамы
            //coo.add(new Coordinate(this.frames.get(0).x1(), this.frames.get(0).y1(), this.frames.get(0).id));
            this.area = gf.createPolygon(coo.toArray(new Coordinate[0]));
            
            //Geometry arc = UGeo.geoPadding(this.area, this.frames, -163);
            //int m = 0;

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

            LinkedList<ElemSimple> crosList = winc.listElem.filter(Type.IMPOST, Type.SHTULP, Type.STOIKA);
            LinkedList<ElemSimple> elemList = winc.listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.IMPOST, Type.SHTULP, Type.STOIKA);

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

    //Линии размерности
    @Override
    public void paint() {
        super.paint();
        if (this.area != null) { //TEST
            winc.gc2d.setColor(new java.awt.Color(255, 000, 000));
            Shape shape = new ShapeWriter().toShape(this.area);
            winc.gc2d.draw(shape);
//            if (arc != null) {
//                shape = new ShapeWriter().toShape(this.arc);
//                winc.gc2d.draw(shape);
//            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    // </editor-fold>      
}
