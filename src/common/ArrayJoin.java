package common;

import builder.model.ElemJoining;
import builder.model.ElemSimple;
import builder.model.UGeo;
import enums.Type;
import enums.TypeJoin;
import static java.lang.String.join;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.algorithm.PointLocation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

public class ArrayJoin extends ArrayList<ElemJoining> {

    public ArrayJoin() {
        super();
    }
    
    /**
     * Получить элемент соединения профилей.
     *
     * @param elem - элемент соединения,
     * @param side - сторона соединения 0-пред.артикул, 1-след.артикл,
     * 2-прилег.артикл
     * @return - класс описатель соединения
     */
    public ElemJoining join(ElemSimple elem, int side) {
        try {
            for (ElemJoining join : this) {

                //T- соединение 
                if ((side == 0 || side == 1) && Type.isCross(elem.type) == true) {
                    Coordinate point = (side == 0) ? new Coordinate(elem.x1(), elem.y1()) : new Coordinate(elem.x2(), elem.y2());
                    if (elem.id == join.elem1.id) {
                        Coordinate[] line = UGeo.arrCoord(join.elem2.x1(), join.elem2.y1(), join.elem2.x2(), join.elem2.y2());
                        if (PointLocation.isOnLine(point, line)) {
                            return join;
                        }
                    }
                } else if (side == 0 && elem.x1() == join.elem1.x2() && elem.y1() == join.elem1.y2()) { //0-пред.артикул
                    return join;

                } else if (side == 1 && elem.x2() == join.elem2.x1() && elem.y2() == join.elem2.y1()) { //1-след.артикл
                    return join;

                } else if (side == 2 && join.type() == TypeJoin.FLAT && elem.equals(join.elem1)) { //2-прилег.артикл
                    return join;
                }
            }
        } catch (Exception e) {
            System.err.println("Неудача:Соединение не найдено. " + e);
        }

        return null;
    }

    /**
     * Получить элемент соединения профилей.
     *
     * @param elem - элемент соединения,
     * @param side - сторона соединения 0-пред.артикул, 1-след.артикл,
     * 2-прилег.артикл
     * @return - элемент соединения
     */
    public ElemSimple elem(ElemSimple elem, int side) {
        try {
            for (ElemJoining join : this) {

                //T- соединение 
                if ((side == 0 || side == 1) && Type.isCross(elem.type) == true) {
                    Coordinate point = (side == 0) ? new Coordinate(elem.x1(), elem.y1()) : new Coordinate(elem.x2(), elem.y2());
                    if (elem.id == join.elem1.id) {
                        Coordinate[] line = UGeo.arrCoord(join.elem2.x1(), join.elem2.y1(), join.elem2.x2(), join.elem2.y2());
                        if (PointLocation.isOnLine(point, line)) {
                            return join.elem2;
                        }
                    }
                } else if (side == 0 && elem.x1() == join.elem1.x2() && elem.y1() == join.elem1.y2()) { //0-пред.артикул
                    return join.elem1;

                } else if (side == 1 && elem.x2() == join.elem2.x1() && elem.y2() == join.elem2.y1()) { //1-след.артикл
                    return join.elem2;

                } else if (side == 2 && join.type() == TypeJoin.FLAT) {
                    if(elem.type == Type.STVORKA_SIDE && elem.equals(join.elem1)) {
                        return join.elem2;
                    } else if(elem.equals(join.elem2)) {
                        return join.elem1;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Неудача:Соединение не найдено. " + e);
        }
        return null;
    }
}
