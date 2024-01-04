package builder.model;

import builder.Wincalc;
import static builder.model.Com5t.gf;
import builder.script.GsonElem;
import domain.eArtikl;
import enums.Type;
import enums.TypeJoin;
import java.util.ArrayList;
import java.util.LinkedList;
import org.locationtech.jts.geom.Coordinate;

public class AreaArch extends AreaSimple {

    public double radiusArch = 0; //радиус арки

    public AreaArch(Wincalc winc, GsonElem gson) {
        super(winc, gson, null);
        this.owner = this;
    }

    //Полигон рамы. Функ. выпоняется после создания рам конструкции
    @Override
    public void setLocation() {
        try {
            ArrayList<Coordinate> coo = new ArrayList<Coordinate>();

            //Создадим вершины арки
            for (int i = 0; i < this.frames.size(); i++) {
                ElemFrame frame = (ElemFrame) this.frames.get(i);
                if (frame.gson.h != null) {;
                
                   radiusArch = (Math.pow(root.width() / 2, 2) + Math.pow(this.h(), 2)) / (2 * this.h());  //R = (L2 + H2) / 2H - радиус арки  
                   coo.add(new Coordinate(frame.x1(), frame.y1()));
                } else {
                    coo.add(new Coordinate(frame.x1(), frame.y1()));
                }
            }
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

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    // </editor-fold>      
}
