package enums;

//�������� �������� ��������
import java.util.List;

public enum UseColor implements Enam {

    MANUAL(0, "��������� �������"),
    PROF(11, "�������"),
    GLAS(15, "����������"),
    COL1(1, "��������"),
    COL2(2, "����������"),
    COL3(3, "�������"),
    C1SER(6, "�������� � �����"),
    C2SER(7, "���������� � �����"),
    C3SER(8, "������� � �����"),
    PARAM(9, "��������"),
    PARSER(10, "�������� � �����"),
    //
    P04(4, "�������� �04"),
    P05(5, "�������� �05"),
    P12(12, "�������� �12"),
    P13(13, "�������� �13"),
    P14(14, "�������� �14");

    public int id;
    public String name;

    private UseColor(int id, String name) {
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

    public static boolean isSeries(int typesUS) {
        
        if (List.of(C1SER.id, C2SER.id, C3SER.id, PARSER.id).contains(typesUS & 0x0000000f)
                || List.of(C1SER.id, C2SER.id, C3SER.id, PARSER.id).contains((typesUS & 0x000000f0) >> 4)
                || List.of(C1SER.id, C2SER.id, C3SER.id, PARSER.id).contains((typesUS & 0x00000f00) >> 8)) {
            return true;
        }
        return false;
    }

    public static Object[] precision = {100000, "����.������"};
    public static Object[] automatic = {0, "����������"};
}
