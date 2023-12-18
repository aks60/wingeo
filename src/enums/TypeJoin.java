package enums;

//То что пишет профстрой в базу данных
import java.util.Arrays;

public enum TypeJoin {

    NONE(00, "Не определен"),
    FLAT(10, "Прилегающее"),
    ANGL(20, "Угловое на ус"),
    ANG1(30, "Угловое левое"),
    ANG2(31, "Угловое правое"),
    TIMP(40, "Т - образное импост"),
    TCON(41, "Т - образное `конус`");

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
        return NONE;
    }
}
