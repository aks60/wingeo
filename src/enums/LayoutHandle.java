package enums;

import static enums.TypeOpen2.values;

public enum LayoutHandle implements Enam {

    MIDL(1, "По середине"),
    CONST(2, "Константная"),
    VARIAT(3, "Установлена");
    

    public int id = 0;
    public String name = "";

    LayoutHandle(int value, String name) {
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
