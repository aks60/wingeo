package enums;

import java.util.Arrays;

public enum LayoutJoin implements Enam {

    NONE(0, "Не определено"),
    LTOP(1, "Угловое левое верхнее", 20, 30),
    LBOT(2, "Угловое левое нижнее", 20, 30),
    RTOP(3, "Угловое правое верхнее", 20, 31),
    RBOT(4, "Угловое правое нижнее", 20, 31),
    TTOP(5, "T - соединение верхнее", 40, 41),
    TBOT(6, "T - соединение нижнее", 40, 41),
    TLEFT(7, "T - соединение левое", 40, 41),
    TRIGH(8, "T - соединение правое", 40, 41),
    CRIGH(9, "Прилегающее правое", 10),
    CLEFT(10, "Прилегающее левое", 10),
    CTOP(11, "Прилегающее верхнее", 10),
    CBOT(12, "Прилегающее нижнее", 10);

    public int id;
    public String name;
    public int[] arrType = null;
    

    LayoutJoin(int id, String name) {
        this.id = id;
        this.name = name;
    }

    LayoutJoin(int id, String name, int... arrType) {
        this.id = id;
        this.name = name;
        this.arrType = arrType;
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
    
    public boolean equalType(int _id) {
        for (int id : arrType) {
            if (id == _id) {
                return true;
            }
        }
        return false;
    }    
}
