
package enums;

import static enums.UseUnit.values;

public enum TypeOpen2 implements Enam {
    QUE(1, "Запрос"),
    LEF(2, "Левое"),
    RIG(3, "Правое");

    public int id = 0;
    public String name = "";

    TypeOpen2(int value, String name) {
        this.id = value;
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