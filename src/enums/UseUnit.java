package enums;

import static enums.TypeOpen1.values;

public enum UseUnit implements Enam {
    METR(1, "пог.м."),
    METR2(2, "кв.м."),
    PIE(3, "шт."),
    ML(4, "мл."),
    KIT(5, "комп."),
    GRAM(6, "грамм."),
    KG(7, "кг."),
    LITER(8, "литр"),
    DOSE(10, "доза"),
    PAIR(11, "пара"),
    MONTH(12, "месяц");
    

    public int id = 0;
    public String name = "";

    UseUnit(int value, String name) {
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
    
    public static String getName(int index) {
        for (UseUnit unit: values()) {
            if (unit.id == index) {
                return unit.name;
            }
        }
        return "";
    }
}
