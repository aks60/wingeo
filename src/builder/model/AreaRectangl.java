package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import enums.TypeJoin;
import java.util.ArrayList;
import org.locationtech.jts.geom.Coordinate;
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
            //Вершины рамы
            ArrayList<Coordinate> cooBox = new ArrayList<Coordinate>();
            this.frames.forEach(frame -> cooBox.add(new Coordinate(frame.x1(), frame.y1(), frame.id)));
            cooBox.add(new Coordinate(this.frames.get(0).x1(), this.frames.get(0).y1(), this.frames.get(0).id));

            //Аrea рамы            
            Polygon geoShell = gf.createPolygon(cooBox.toArray(new Coordinate[0]));             
            this.area = UGeo.multiPolygon(geoShell, winc.listElem);
            
            splitLocation((Polygon) this.area.getGeometryN(0), this.childs); //опережающее разделение импостом
            
            //new Test().mpol(this.area);
        } catch (Exception e) {
            System.err.println("Ошибка:AreaRectangl.setLocation" + toString() + e);
        }
    }

    //L - соединения
    @Override
    public void addJoining() {
        try {
            winc.listJoin.clear();

            super.addJoining(); //T - соединения

            //L - соединения
            for (int i = 0; i < this.frames.size(); i++) { //цикл по сторонам рамы
                ElemFrame nextFrame = (ElemFrame) this.frames.get((i == this.frames.size() - 1) ? 0 : i + 1);
                winc.listJoin.add(new ElemJoining(this.winc, TypeJoin.ANGL, this.frames.get(i), nextFrame));
            }
        } catch (Exception e) {
            System.err.println("AreaRectangl.addJoining() " + e);
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
