package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import enums.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import org.locationtech.jts.geom.Coordinate;

public class AreaRectangl extends AreaSimple {

    public AreaRectangl(Wincalc winc, GsonElem gson) {
        super(winc, gson, null);
    }

    //Полигон рамы. Функ. выпоняется после создания рам конструкции
    @Override
    public void setLocation() {
        try {
            ArrayList<Coordinate> coo = new ArrayList<Coordinate>();

            //Создадим вершины рамы
            this.frames.forEach(line -> coo.add(new Coordinate(line.x1(), line.y1())));
            coo.add(new Coordinate(this.frames.get(0).x1(), this.frames.get(0).y1()));

            //Создадим area рамы
            Coordinate[] arr = coo.toArray(new Coordinate[0]);
            
            //Полигон векторов рамы
            this.area = gf.createPolygon(arr);

        } catch (Exception e) {
            System.err.println("Ошибка:AreaRectangl.setLocation" + toString() + e);
        }
    }

    //Соединения
    @Override
    public void joining() {
        try {
            winc.listJoin.clear();
            LinkedList<ElemSimple> crosList = winc.listElem.filter(Type.IMPOST, Type.SHTULP, Type.STOIKA);
            LinkedList<ElemSimple> elemList = winc.listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.IMPOST, Type.SHTULP, Type.STOIKA);

            //L - соединения
            for (int i = 0; i < this.frames.size(); i++) { //цикл по сторонам рамы
                ElemFrame nextFrame = (ElemFrame) this.frames.get((i == this.frames.size() - 1) ? 0 : i + 1);
                winc.listJoin.add(new ElemJoining(this.winc, this.frames.get(i), nextFrame));
            }
            //T - соединения
            for (ElemSimple e1 : crosList) { //цикл по кросс элементам
                for (ElemSimple e2 : elemList) { //цикл по сторонам рамы и импостам (т.к. в створке Т-обр. соединений нет)
                    if (e2.inside(e1.x1(), e1.y1()) == true && e2 != e1) {
                        winc.listJoin.add(new ElemJoining(winc, e1, e2));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("AreaRectangl.joining() " + e);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    // </editor-fold>     
}
