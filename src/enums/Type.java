package enums;

//���� ����������� � ���������
import builder.model.Com5t;
import java.util.List;

public enum Type implements Enam {

    NONE(0, 0, "�� ����������"),
    //== TypeElem ==
    FRAME_SIDE(1, 1, "������� �������"),
    STVORKA_SIDE(2, 2, "������� �������"),
    IMPOST(3, 3, "������"),
    RIGEL_IMP(4, 777, "������/������"),
    STOIKA(5, 5, "������"),
    STOIKA_FRAME(6, 777, "������/�������"),
    ERKER(7, 7, "�����"),
    EDGE(8, 8, "�����"),
    SHTULP(9, 9, "������"),
    GLASS(10, 777, "���������� (�����������, ������)"),
    MOSQUIT(13, 777, "��������"),
    RASKL(14, 7, "���������"),
    SAND(15, 777, "�������"),
    BLINDS(15, 777, "������"),
    //SUPPORT(XX, "���������"),
    JOINING(98, 777, "����������"),
    PARAM(99, 777, "��������� �����������"),
    //== TypeArea ==
    AREA(2000, 777, "���������"),
    RECTANGL(2001, 1, "���� ��������������"),
    TRAPEZE(2002, 1, "���� ��������"),
    TRIANGL(2003, 1, "����������� ����"),
    ARCH(2004, 1, "������� ����"),
    STVORKA(2005, 2, "�������"),
    FRAME(2006, 3, "�������"),
    DOOR(2007, 3, "�����");

    public int id;
    public int id2 = 0; //��� UseArtiklTo
    public String name;

    Type(int id, String name) {
        this.id = id;
        this.name = name;
    }

    Type(int id, int id2, String name) {
        this.id = id;
        this.id2 = id2;
        this.name = name;
    }

    public int numb() {
        return id;
    }

    public static boolean isCross(Type type) {
        if (List.of(Type.IMPOST, Type.STOIKA, Type.ERKER, Type.SHTULP).contains(type)) {
            return true;
        }
        return false;
    }

    public static boolean contains(Com5t com5t, Type... types) {
        for (Type type : types) {
            if (com5t.type == type) {
                return true;
            }
        }
        return false;
    }
}
