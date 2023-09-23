package enums;

import java.util.Arrays;

public enum LayoutJoin implements Enam {

    NONE(0, "Не определено"),
    ANGL(1, "Угловое", 20, 31),
    TEE(5, "T - соединение", 40, 41),
    FLAT(9, "Прилегающее", 10);

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
