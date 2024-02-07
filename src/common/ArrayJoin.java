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

    Coordinate co = new Coordinate();

    public ArrayJoin() {
        super();
    }

    /**
     * Получить элемент соединения по точке
     */
    public ElemJoining get(double x, double y) {
        try {
            for (ElemJoining join : this) {
                if (join.elem1.x2() == x && join.elem1.y2() == y
                        && join.elem2.x2() == x && join.elem2.y2() == y) {
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
     * @return - класс описатель соединения
     */
    public ElemJoining get(ElemSimple elem, int side) {
        try {
            for (ElemJoining join : this) {

                //T- соединение 
                if (Type.isCross(elem.type) == true) {
                    System.err.println("ОШИБКА:ArrayJoin.get()");
                    Coordinate c = (side == 0) ? new Coordinate(elem.x1(), elem.y1()) : new Coordinate(elem.x2(), elem.y2());
                    for (ElemSimple e : elem.winc.listElem) {
                        Coordinate[] arr = UGeo.arrCoord(e.x1(), e.y1(), e.x2(), e.y2());
                        if (PointLocation.isOnLine(c, arr)) {
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
     * @param el - элемент соединения,
     * @param side - сторона соединения 0-пред.артикул, 1-след.артикл,
     * 2-прилег.артикл
     * @return - элемент соединения
     */
    public ElemSimple elem(ElemSimple el, int side) {
        ElemJoining join = get(el, side);
        try {
            if (join != null) {

                if (side == 0) {
                    return (Type.isCross(el.type) == true) ? join.elem2 : join.elem1;
                } else if (side == 1) {
                    return join.elem2;
                } else if (side == 2 && join.type() == TypeJoin.FLAT) {
                    return join.elem2;
                }
            }
        } catch (Exception e) {
            System.err.println("Неудача:Соединение не найдено. " + e);
        }
        return null;
    }

}
