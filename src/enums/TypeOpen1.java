package enums;

import static enums.TypeUse.values;

/**
 * Типы открывания створок
 */
public enum TypeOpen1 implements Enam {

   
    FIXED(0, "empty", "Глухая створка (не открывается)"),
    LEFT(1, "Левое", "Левая поворотная (открывается справа-налево, ручка справа)"),
    RIGH(2, "Правое", "Правая поворотная (открывается слева-направо, ручка слева)"),
    LEFTUP(3, "Левое", "Левая поворотно-откидная"),
    RIGHUP(4, "Правое", "Правая поворотно-откидная"),
    UPPER(5, "Откидная", "Откидная (открывается сверху)"),
    LEFMOV(11, "Левое", "Раздвижная влево (открывается справа-налево, защелка справа"),
    RIGHMOV(12, "Правое", "Раздвижная вправо (открывается слева-направо, защелка слева"),
    REQUEST(16, "Запрос", "Не определено");

    public int id;
    public String name;
    public String name2;

    TypeOpen1(int id, String name, String name2) {
        this.id = id;
        this.name = name;
        this.name2 = name2;
    }

    public int numb() {
        return id;
    }

    public String text() {
        return name2;
    }

    public Enam[] fields() {
        return values();
    }

    public static TypeOpen1 get(int id) {
        for (TypeOpen1 typeOpen : values()) {
            if (typeOpen.id == id) {
                return typeOpen;
            }
        }
        return null;
    }

    public Layout axisStv() {
        if (this == LEFT || this == LEFTUP || this == LEFMOV) {
            return Layout.LEFT;
        } else if (this == RIGH || this == RIGHUP || this == RIGHMOV) {
            return Layout.RIGHT;
        } else if (this == UPPER) {
            return Layout.BOTT;
        } else {
            return Layout.LEFT;  //по умолчанию
        }
    }
}
