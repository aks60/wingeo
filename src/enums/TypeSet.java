package enums;

import static enums.TypeUse.values;

public enum TypeSet implements Enam {
    P1(1, "����������"),
    P2(2, "�����������"),
    P3(3, "�������������"),
    P4(4, "��������"),
    P5(5, "������ �/�"),
    P6(6, "��������� ������"),
    P7(7, "�������������"),
    P8(8, "�������");

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
