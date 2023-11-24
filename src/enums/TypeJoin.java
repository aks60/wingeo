package enums;

//То что пишет профстрой в базу данных
import java.util.Arrays;

public enum TypeJoin {

    EMPTY(00, "Тип соединеия не установлен"),
    VAR10(10, "Прилегающее"),
    VAR20(20, "Угловое на ус"),
    VAR30(30, "Угловое левое"),
    VAR31(31, "Угловое правое"),
    VAR40(40, "Т - образное (импост,ригель)"),
    VAR41(41, "Т - образное `конус` (импост)");

    public int id;
    public String name;

    TypeJoin(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static TypeJoin get(int id) {
        for (TypeJoin v : values()) {
            if (v.id == id) {
                return v;
            }
        }
        return EMPTY;
    }
}
