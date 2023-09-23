package common;

import builder.IElem5e;
import builder.model1.ElemJoining;
import enums.LayoutJoin;
import enums.Type;
import enums.TypeJoin;
import java.util.ArrayList;
import java.util.List;

public class ArrayJoin extends ArrayList<ElemJoining> {

    public ArrayJoin() {
        super();
    }

    /**
     * Получить элемент соединения профилей.
     *
     * @param el - элемент соединения,
     * @param side - сторона соединения 0-пред.артикул, 1-след.артикл,
     * 2-прилег.артикл
     * @return - класс описатель соединения
     */
    public ElemJoining get(IElem5e el, int side) {
        try {
            for (ElemJoining join : this) {
                if (List.of(Type.IMPOST, Type.STOIKA, Type.ERKER).contains(el.type())) {
                    if (side == 0 && join.elem1.id() == el.id() && (join.layout == LayoutJoin.TEE || join.layout == LayoutJoin.TEE)) {
                        return join;
                    } else if (side == 1 && join.elem1.id() == el.id() && (join.layout == LayoutJoin.TEE || join.layout == LayoutJoin.TEE)) {
                        return join;
                    }
                } else {
                    if (side == 0 && join.elem2.id() == el.id() && join.type != TypeJoin.VAR10) {  //Угловое левое
                        if (List.of(Type.IMPOST, Type.STOIKA, Type.ERKER).contains(join.elem1.type()) == false) {
                            return join;
                        }
                    } else if (side == 1 && join.elem1.id() == el.id() && join.type != TypeJoin.VAR10) { //Угловое правое
                        if (List.of(Type.IMPOST, Type.STOIKA, Type.ERKER).contains(join.elem2.type()) == false) {
                            return join;
                        }
                    } else if (side == 2 && join.elem1.id() == el.id() && join.type == TypeJoin.VAR10) { //Прилегающее
                        //if (List.of(Type.IMPOST, Type.STOIKA, Type.ERKER).contains(join.elem1.type()) == false) {
                            return join;
                        //}
                    }
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
    public IElem5e elem(IElem5e el, int side) {
        ElemJoining join = get(el, side);
        try {
            if (join != null) {

                if (side == 0) {
                    return (el.type() == Type.IMPOST || el.type() == Type.SHTULP || el.type() == Type.STOIKA) ? join.elem2 : join.elem1;
                } else if (side == 1) {
                    return join.elem2;
                } else if (side == 2 && join.type == TypeJoin.VAR10) {
                    return join.elem2;
                }
            }
        } catch (Exception e) {
            System.err.println("Неудача:Соединение не найдено. " + e);
        }
        return null;
    }

}
