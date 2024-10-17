package enums;

import static enums.TypeOpen2.values;

public enum LayoutKnob implements Enam {

    MIDL(1, "�� ��������"),
    CONST(2, "�����������"),
    VAR(3, "�����������");
    

    public int id = 0;
    public String name = "";

    LayoutKnob(int value, String name) {
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
