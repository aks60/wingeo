package enums;

//То что пишет профстрой в базу данных
import java.util.Arrays;

public enum TypeJoin {

    NONE(00, "Не определен"),
    FLAT(10, "Прилегающее"),
    ANGL(20, "Угловое на ус"),
    ANG1(30, "Угловое левое"),
    ANG2(31, "Угловое правое"),
    TIMG1(40, "Т - образное (импост,ригель)"),
    TIMG2(41, "Т - образное `конус` (импост)");

    public int id;
    public String name;

    TypeJoin(int id, String name) {
        this.id = id;
        this.name = name;
    }
/*
    NONE(00, 0, "Не определен"),
    FLAT(10, 10, "Прилегающее"),
    ANGL(20, 20, "Угловое на ус"),
    ANG1(30, 20, "Угловое левое"),
    ANG2(31, 20, "Угловое правое"),
    TIMG1(40, 40, "Т - образное (импост,ригель)"),
    TIMG2(41, 40, "Т - образное `конус` (импост)");

    public int id;
    public int id2;
    public String name;

    TypeJoin(int id, int id2, String name) {
        this.id = id;
        this.id2 = id2;
        this.name = name;
    }    
    */
    public static TypeJoin get(int id) {
        for (TypeJoin v : values()) {
            if (v.id == id) {
                return v;
            }
        }
        return NONE;
    }
}
