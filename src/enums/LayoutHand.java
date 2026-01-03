package enums;

import static enums.TypeOpen2.values;

public enum LayoutHand implements Enam {

    MIDL(1, "По середине"),
    CONST(2, "Константная"),
    VAR(3, "Установлена");
    

    public int id = 0;
    public String name = "";

    LayoutHand(int value, String name) {
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
