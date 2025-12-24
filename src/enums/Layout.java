package enums;

import static enums.TypeOpen1.values;
import java.util.Arrays;

//Расположение area  и profile объединены в один список Enum
public enum Layout implements Enam {
    ANY(-1, "Любая"),
    HOR(-2, "Гор."),
    VER(-3, "Верт."),
    BOT(1, "Нижн."),
    RIG(2, "Прав."),
    TOP(3, "Верх."),
    LEF(4, "Лев."),
    //TOPR(5, "Верх-Правая"),
    //TOPL(6, "Верх-Левая"),
    FULL(9, "");
    public int id;
    public String name;

    Layout(int id, String name) {
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
}
