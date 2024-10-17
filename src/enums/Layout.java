package enums;

import static enums.TypeOpen1.values;
import java.util.Arrays;

//������������ area  � profile ���������� � ���� ������ Enum
public enum Layout implements Enam {
    ANY(-1, "�����"),
    HORIZ(-2, "���."),
    VERT(-3, "����."),
    BOTT(1, "����."),
    RIGHT(2, "����."),
    TOP(3, "����."),
    LEFT(4, "���."),
    //TOPR(5, "����-������"),
    //TOPL(6, "����-�����"),
    FULL(9, "");
    public int id;
    public String name;

    Layout(int id, String name) {
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
