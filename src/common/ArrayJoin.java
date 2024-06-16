package common;

import builder.model.ElemJoining;
import builder.model.ElemSimple;
import builder.model.UGeo;
import dataset.Record;
import domain.eArtikl;
import enums.Type;
import enums.TypeJoin;
import java.util.ArrayList;
import org.locationtech.jts.algorithm.PointLocation;
import org.locationtech.jts.geom.Coordinate;

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
        boolean imp = Type.isCross(elem.type);
        try {
            for (ElemJoining join : this) {
                //Угл.соединение
                if (imp == false && (side == 0 || side == 1)) {
                    if (side == 0 && elem.x1() == join.elem1.x2() && elem.y1() == join.elem1.y2()) { //0-пред.артикул
                        return join;

                    } else if (side == 1 && elem.x2() == join.elem2.x1() && elem.y2() == join.elem2.y1()) { //1-след.артикл
                        return join;
                    }
                    //T- соединение левое
                } else if (imp == true && side == 0 && elem.id == join.elem2.id) {
                    Coordinate[] line = UGeo.arrCoord(join.elem1.x1(), join.elem1.y1(), join.elem1.x2(), join.elem1.y2());
                    Coordinate point = new Coordinate(elem.x1(), elem.y1());
                    if (PointLocation.isOnLine(point, line)) {
                        return join;
                    }
                    //T- соединение правое
                } else if (imp == true && side == 1 && elem.id == join.elem1.id) {
                    Coordinate[] line = UGeo.arrCoord(join.elem2.x1(), join.elem2.y1(), join.elem2.x2(), join.elem2.y2());
                    Coordinate point = new Coordinate(elem.x2(), elem.y2());
                    if (PointLocation.isOnLine(point, line)) {
                        return join;
                    }
                    //Прил.соединение
                } else if (side == 2 && join.type() == TypeJoin.FLAT) {
                    if (elem.type == Type.STVORKA_SIDE && elem.equals(join.elem1)) {
                        return join;
                    } else if (elem.equals(join.elem2)) {
                        return join;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:ArrayJoin.join() " + message(elem, side) + " " + e);
        }
        if (side != 2) {
            System.out.println("Неудача:ArrayJoin.join() " + message(elem, side));
        }
        return null;
    }

    /**
     * Получить элемент соединения профилей.
     *
     * @param elem - элемент соединения,
     * @param side - сторона соединения 0-пред.артикул, 
     * 1-след.артикл, 2-прилег.артикл
     * @return - элемент соединения
     */
    public ElemSimple elem(ElemSimple elem, int side) {
        boolean imp = Type.isCross(elem.type);
        try {
            for (ElemJoining join : this) {
                //Угл.соединение
                if (imp == false && (side == 0 || side == 1)) {
                    if (side == 0 && elem.x1() == join.elem1.x2() && elem.y1() == join.elem1.y2()) { //0-пред.артикул
                        return join.elem1;

                    } else if (side == 1 && elem.x2() == join.elem2.x1() && elem.y2() == join.elem2.y1()) { //1-след.артикл
                        return join.elem2;
                    }
                    //T- соединение левое
                } else if (imp == true && side == 0 && elem.id == join.elem2.id) {
                    Coordinate[] line = UGeo.arrCoord(join.elem1.x1(), join.elem1.y1(), join.elem1.x2(), join.elem1.y2());
                    Coordinate point = new Coordinate(elem.x1(), elem.y1());
                    if (PointLocation.isOnLine(point, line)) {
                        return join.elem1;
                    }
                    //T- соединение правое
                } else if (imp == true && side == 1 && elem.id == join.elem1.id) {
                    Coordinate[] line = UGeo.arrCoord(join.elem2.x1(), join.elem2.y1(), join.elem2.x2(), join.elem2.y2());
                    Coordinate point = new Coordinate(elem.x2(), elem.y2());
                    if (PointLocation.isOnLine(point, line)) {
                        return join.elem2;
                    }
                    //Прил.соединение
                } else if (side == 2 && join.type() == TypeJoin.FLAT) {
                    if (elem.type == Type.STVORKA_SIDE && elem.equals(join.elem1)) {
                        return join.elem2;
                    } else if (elem.equals(join.elem2)) {
                        return join.elem1;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:ArrayJoin.elem() " + message(elem, side) + " " + e);
        }
        if (side != 2) {
            System.out.println("Неудача:ArrayJoin.elem() " + message(elem, side));
        }
        return null;
    }    

    private static String message(ElemSimple elem, int side) {
        return "Соединение не найдено для elem.id=" + elem.id + ", side=" + side;
    }
}
