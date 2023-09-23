package enums;

import static enums.LayoutFurn1.values;

public enum UseFurn2 implements Enam {
    P1(-1, "Ведомая"),
    P2(0, "Обычная"),
    P3(1, "Ведущая"),
    P4(2, "Адаптер");

    public int id;
    public String name;

    UseFurn2(int id, String name) {
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
