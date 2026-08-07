package enums;

//Варианты рассчёта текстуры
import java.util.List;

public enum UseColor implements Enam {

    MANUAL(0, "Указанная вручную"),
    PROF(11, "Профиль"),
    GLAS(15, "Заполнение"),
    COL1(1, "Основная"),
    COL2(2, "Внутренняя"),
    COL3(3, "Внешняя"),
    C1SER(6, "Основная и серия"),
    C2SER(7, "Внутренняя и серия"),
    C3SER(8, "Внешняя и серия"),
    PARAM(9, "Параметр"),
    PARSER(10, "Параметр и серия"),
    //
    P04(4, "Параметр №04"),
    P05(5, "Параметр №05"),
    P12(12, "Параметр №12"),
    P13(13, "Параметр №13"),
    P14(14, "Параметр №14");

    public int id;
    public String name;

    private UseColor(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int numb() {
        return id;
    }

    public String text() {
        return name;
    }

    public Enam[] fields() {
        return values();
    }

    public static boolean isSeries(int typesUS) {
        
        if (List.of(C1SER.id, C2SER.id, C3SER.id, PARSER.id).contains(typesUS & 0x0000000f)
                || List.of(C1SER.id, C2SER.id, C3SER.id, PARSER.id).contains((typesUS & 0x000000f0) >> 4)
                || List.of(C1SER.id, C2SER.id, C3SER.id, PARSER.id).contains((typesUS & 0x00000f00) >> 8)) {
            return true;
        }
        return false;
    }

    public static Object[] precision = {100000, "Точн.подбор"};
    public static Object[] automatic = {0, "Автоподбор"};
}
