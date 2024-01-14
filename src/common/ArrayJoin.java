package common;

import builder.model.ElemJoining;
import builder.model.ElemSimple;
import builder.model.UGeo;
import enums.Type;
import enums.TypeJoin;
import java.util.ArrayList;
import java.util.List;
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
               if(join.elem1.x1() == x && join.elem1.y1() == y) {
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
     * @return - класс описатель соединения
     */
    public ElemJoining get(ElemSimple el, int side) {
        try {
            for (ElemJoining join : this) {

                if (List.of(Type.IMPOST, Type.STOIKA, Type.ERKER).contains(el.type)) {
                    Point p = (side == 0) ? UGeo.newPoint(el.x1(), el.y1()) : UGeo.newPoint(el.x2(), el.y2());
                    for (ElemSimple e : el.winc.listElem) {
                        if (UGeo.newLineArch(e.x1(), e.y1(), e.x2(), e.y2()).contains(p)) {
                            return join;
                        }
                    }
                } else if (side == 0 && el.x1() == join.elem1.x2() && el.y1() == join.elem1.y2()) { //0-пред.артикул
                    return join;

                } else if (side == 1 && el.x2() == join.elem2.x1() && el.y2() == join.elem2.y1()) { //1-след.артикл
                    return join;

                } else if (side == 2 && join.type() == TypeJoin.FLAT && el == join.elem1) { //2-прилег.артикл
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
