package enums;

//Типы конструкций и элементов
import builder.model.Com5t;
import java.util.List;

public enum Type implements Enam {

    NONE(0, 0, "Не определено"),
    //== TypeElem ==
    FRAME_SIDE(1, 1, "Сторона коробки"),
    STVORKA_SIDE(2, 2, "Сторона створки"),
    IMPOST(3, 3, "Импост"),
    RIGEL_IMP(4, 777, "Ригель/импост"),
    STOIKA(5, 5, "Стойка"),
    STOIKA_FRAME(6, 777, "Стойка/коробка"),
    ERKER(7, 7, "Эркер"),
    EDGE(8, 8, "Грань"),
    SHTULP(9, 9, "Штульп"),
    GLASS(10, 777, "Заполнение (Стеклопакет, стекло)"),
    MOSQUIT(13, 777, "Москитка"),
    RASKL(14, 7, "Раскладка"),
    SAND(15, 777, "Сэндвич"),
    BLINDS(15, 777, "Жалюзи"),
    //SUPPORT(XX, "Подкладка"),
    JOINING(98, 777, "Соединения"),
    PARAM(99, 777, "Параметры конструкции"),
    //== TypeArea ==
    AREA(2000, 777, "Контейнер"),
    RECTANGL(2001, 1, "Окно четырёхугольное"),
    TRAPEZE(2002, 1, "Окно трапеция"),
    TRIANGL(2003, 1, "Треугольное окно"),
    ARCH(2004, 1, "Арочное окно"),
    STVORKA(2005, 2, "Створка"),
    FRAME(2006, 3, "Коробка"),
    DOOR(2007, 3, "Дверь");

    public int id;
    public int id2 = 0; //это UseArtiklTo
    public String name;

    Type(int id, String name) {
        this.id = id;
        this.name = name;
    }

    Type(int id, int id2, String name) {
        this.id = id;
        this.id2 = id2;
        this.name = name;
    }

    public int numb() {
        return id;
    }

    public static boolean isCross(Type type) {
        if (List.of(Type.IMPOST, Type.STOIKA, Type.ERKER, Type.SHTULP).contains(type)) {
            return true;
        }
        return false;
    }

    public static boolean contains(Com5t com5t, Type... types) {
        for (Type type : types) {
            if (com5t.type == type) {
                return true;
            }
        }
        return false;
    }
}
