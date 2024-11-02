package enums;

import static enums.TypeOpen1.values;

public enum UseUnit implements Enam {
    METR(1, "���.�."),
    METR2(2, "��.�."),
    PIE(3, "��."),
    ML(4, "��."),
    KIT(5, "����."),
    GRAM(6, "�����."),
    KG(7, "��."),
    LITER(8, "����"),
    DOSE(10, "����"),
    PAIR(11, "����"),
    MONTH(12, "�����");
    

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
    
//    public static UseUnit getUnit(int index) {
//        for (UseUnit unit: values()) {
//            if (unit.id == index) {
//                return unit.name;
//            }
//        }
//        return "";
//    }
}
