package enums;

import static enums.TypeUse.values;

public enum TypeSet implements Enam {
    P1(1, "Внутренний"),
    P2(2, "Армирование"),
    P3(3, "Ламинирование"),
    P4(4, "Покраска"),
    P5(5, "Состав с/п"),
    P6(6, "Кронштейн стойки"),
    P7(7, "Дополнительно");

    public int id;
    public String name;

    TypeSet(int id, String name) {
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
