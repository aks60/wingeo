package enums;

import builder.model.AreaStvorka;
import builder.model.ElemSimple;
import common.UCom;
import java.util.List;

/**
 * ���� ���������� �������
 */
public enum TypeOpen1 implements Enam {

    EMPTY(0, "empty", "������ ������� (�� �����������)"),
    LEFT(1, "�����", "����� ���������� (����������� ������-������, ����� ������)"),
    RIGH(2, "������", "������ ���������� (����������� �����-�������, ����� �����)"),
    LEFTUP(3, "�����", "����� ���������-��������"),
    RIGHUP(4, "������", "������ ���������-��������"),
    UPPER(5, "��������", "�������� (����������� ������)"),
    LEFMOV(11, "�����", "���������� ����� (����������� ������-������, ������� ������"),
    RIGMOV(12, "������", "���������� ������ (����������� �����-�������, ������� �����"),
    REQUEST(16, "������", "�� ����������");

    public int id;
    public String name;
    public String name2;

    TypeOpen1(int id, String name, String name2) {
        this.id = id;
        this.name = name;
        this.name2 = name2;
    }

    public int numb() {
        return id;
    }

    public String text() {
        return name2;
    }

    public Enam[] fields() {
        return values();
    }

    public static TypeOpen1 get(int id) {
        for (TypeOpen1 typeOpen : values()) {
            if (typeOpen.id == id) {
                return typeOpen;
            }
        }
        return null;
    }

    public Layout axisStv() {
        if (this == LEFT || this == LEFTUP || this == LEFMOV) {
            return Layout.LEFT;
        } else if (this == RIGH || this == RIGHUP || this == RIGMOV) {
            return Layout.RIGHT;
        } else if (this == UPPER) {
            return Layout.BOTT;
        } else {
            return Layout.LEFT;  //�� ���������
        }
    }

    public static ElemSimple getKnob(AreaStvorka areaStv, TypeOpen1 typeOpen) {
        if (List.of(LEFT, LEFTUP, LEFMOV).contains(typeOpen)) {
            return UCom.layout(areaStv.frames, Layout.RIGHT);
        } else if (List.of(RIGH, RIGHUP, RIGMOV).contains(typeOpen)) {
            return UCom.layout(areaStv.frames, Layout.LEFT);
        } else if (UPPER == typeOpen) {
            return UCom.layout(areaStv.frames, Layout.TOP);
        } else {
            return UCom.layout(areaStv.frames, Layout.LEFT);
        }
    }
}
